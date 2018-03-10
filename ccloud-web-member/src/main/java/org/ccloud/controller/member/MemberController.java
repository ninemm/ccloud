/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.controller.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.interceptor.UserInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.wechat.WechatUserInterceptor;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@RouterMapping(url = "/member/member")
public class MemberController extends BaseFrontController {

	@Before(WechatUserInterceptor.class)
	public void auth() {

		String openId = null;
		String accessToken = null;
		String gotoUrl = getPara("goto", "/member/product/index");
		String wechatUserJson = getSessionAttr(Consts.SESSION_WECHAT_USER);

		if (StrKit.notBlank(wechatUserJson)) {
			JSONObject userJson = JSON.parseObject(wechatUserJson);
			accessToken = userJson.getString("access_token");
			openId = userJson.getString("openid");

			if (StrKit.notBlank(openId, accessToken)) {
				CookieUtils.put(this, Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken);
				CookieUtils.put(this, Consts.SESSION_WECHAT_OPEN_ID, openId);
				setSessionAttr(Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken);
				setSessionAttr(Consts.SESSION_WECHAT_OPEN_ID, openId);
			} else {
				renderError(500);
				return;
			}

			Member member = MemberQuery.me().findByWechatOpenid(openId);
			if (member == null) {
				gotoUrl = "/member/member/bind";
			} else {

				// 更新用户的信息
				ApiResult wxUserResult = UserApi.getUserInfo(openId);
				if (wxUserResult.isSucceed()) {
					member.setAvatar(wxUserResult.getStr("headimgurl"));
					member.setNickname(wxUserResult.getStr("nickname"));
					member.setWechatOpenId(openId);

					if (!member.saveOrUpdate()) {
						renderError(500);
						return;
					}
					setSessionAttr(Consts.SESSION_LOGINED_MEMBER, member);
				} else {
					LogKit.warn("member info get failure");
				}
			}

			forwardAction(gotoUrl);
		}
	}

	//检测手机号
	@Clear(SessionInterceptor.class)
	public void checkMobile() {

		String mobile = getPara("mobile");
		String sales_id = getPara("sales_id");
		List<Record> list = MemberQuery.me().checkCustomerExist(mobile, sales_id);
		if (list != null && list.size() > 0)
			renderAjaxResultForSuccess();
		else
			renderAjaxResultForError("手机号不存在,请业务员确认");
	}

	@Clear(SessionInterceptor.class)
	public void update() {

		final String mobile = getPara("mobile");
		final String code = getPara("code");
		final String sales_id = getPara("sales_id");
		final Ret ret = Ret.create();

		boolean updated = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 验证短信验证码是否正确
				SmsCode smsCode = SmsCodeQuery.me().findByMobileAndCode(mobile, code);
				if (smsCode == null)
					return false;

				smsCode.setStatus(1);
				if (!smsCode.update())
					return false;

				String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
				ApiResult wxUserResult = UserApi.getUserInfo(openId);
				if (wxUserResult != null) {

					List<Record> list = MemberQuery.me().checkCustomerExist(mobile, sales_id);
					if (list == null || list.size() == 0) {
						ret.set("message", "您还不是该业务员客户,请业务员确认");
						return false;
					}

					Member member = MemberQuery.me().findByWechatOpenid(openId);
					Date createDate = new Date();
					String memberId = "";
					if (member == null) {
						Record record = list.get(0);
						memberId = StrKit.getRandomUUID();
						member = new Member();
						member.setId(memberId);
						member.setCustomerId(record.getStr("id"));
						member.setUsername(record.getStr("mobile"));
						member.setRealname(record.getStr("contact"));
						member.setNickname(wxUserResult.getStr("nickname"));
						member.setMobile(record.getStr("mobile"));
						member.setSalt(EncryptUtils.salt());
						member.setPassword(EncryptUtils.encryptPassword("123456", member.getSalt()));
						member.setAvatar(wxUserResult.getStr("headimgurl"));
						member.setProvName(record.getStr("prov_name"));
						member.setCityName(record.getStr("city_name"));
						member.setCountryName(record.getStr("country_name"));
						member.setAddress(record.getStr("address"));
						member.setWechatOpenId(openId);
						member.setStatus(1);
						member.setCreateDate(createDate);
						if (!member.save()) {
							ret.set("message", "手机号绑定失败，请联系管理员");
							return false;
						}
					} else {
						member.setNickname(wxUserResult.getStr("nickname"));
						member.setAvatar(wxUserResult.getStr("headimgurl"));
						member.setWechatOpenId(openId);
						member.update();
						memberId = member.getId();
					}

					User sales = UserQuery.me().findById(sales_id);
					List<Department> deptList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(sales.getDepartmentId());
					String dealerDataArea = DepartmentQuery.me().getDealerDataArea(deptList);
					Seller seller = SellerQuery.me()._findByDataArea(dealerDataArea);
					MemberJoinSeller memberJoinSeller = new MemberJoinSeller();
					memberJoinSeller.setMemberId(memberId);
					memberJoinSeller.setSellerId(seller.getId());
					memberJoinSeller.setUserId(sales_id);
					memberJoinSeller.setStatus(1);
					memberJoinSeller.setCreateDate(createDate);
					if (!memberJoinSeller.save()) {
						ret.set("message", "手机号绑定失败，请联系管理员");
						return false;
					}
					setSessionAttr(Consts.SESSION_LOGINED_MEMBER, member);
				}
				return true;
			}
		});

		if (updated) {
			renderAjaxResultForSuccess("绑定手机号成功");
			return;
		}
		renderAjaxResultForError(ret.getStr("message"));
	}

	//绑定用户信息
	@Clear({SessionInterceptor.class})
	public void bind() {

		String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
		ApiResult wxUserResult = UserApi.getUserInfo(openId);
		if (wxUserResult != null) {
			setAttr("avatar", wxUserResult.getStr("headimgurl"));
			setAttr("nickname", wxUserResult.getStr("nickname"));
		}
		setAttr("scene_str", getPara("scene_str"));
		render("member_bind.html");
	}

//		public void index() {
//			String action = getPara();
//			if (StringUtils.isBlank(action)) {
//				renderError(404);
//			}
//
//			keepPara();
//
//			BigInteger userId = StringUtils.toBigInteger(action, null);
//			if (userId == null) {
//				if ("detail".equalsIgnoreCase(action)) {
//					renderError(404);
//				} else if ("choice".equalsIgnoreCase(action)) {
//					User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
//					if (user != null) {
//						setAttr("mobile", user.getMobile());
//						setAttr("openid", user.getWechatOpenId());
//					}
//				}
//
//				render(String.format("user_%s.html", action));
//			}
//		}

	@Clear({UserInterceptor.class, SessionInterceptor.class})
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("member_login.html");
			return;
		}

		List<Member> memberList = MemberQuery.me().findByMobile(username);
		if (null == memberList || memberList.size() == 0) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("member_login.html");
			}
			return;
		}


		Member member = memberList.get(0);

		if (EncryptUtils.verlifyUser(member.getPassword(), member.getSalt(), password)) {
//			MessageKit.sendMessage(Actions.USER_LOGINED, member);
			setSessionAttr(Consts.SESSION_LOGINED_MEMBER, member);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_MEMBER, member.getId());

			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登录成功");
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect("/member/product/index");
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				setAttr("errorMsg", "密码错误");
				render("member_login.html");
			}
		}
	}


//

//

//
//
//		//设置页面
//		public void config () {
//			Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);
//			setAttr("member", member);
//			render("user_config.html");
//		}


}

