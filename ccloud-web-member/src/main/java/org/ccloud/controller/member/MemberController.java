/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.controller.member;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.QrcodeApi;
import com.jfinal.weixin.sdk.api.UserApi;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.interceptor.UserInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.Member;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

@RouterMapping(url = "/menber/menber")
public class MemberController extends BaseFrontController {

//	public void index() {
//
//
//
//		render("product.html");
//	}

	public void auth() {




	}

	public void index() {
		String action = getPara();
		if (StringUtils.isBlank(action)) {
			renderError(404);
		}

		keepPara();

		BigInteger userId = StringUtils.toBigInteger(action, null);
		if (userId == null) {
			if ("detail".equalsIgnoreCase(action)) {
				renderError(404);
			} else if ("choice".equalsIgnoreCase(action)) {
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
				if (user != null) {
					setAttr("mobile", user.getMobile());
					setAttr("openid", user.getWechatOpenId());
				}
			}

			render(String.format("user_%s.html", action));
		}
	}

	@Clear({UserInterceptor.class,SessionInterceptor.class})
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登录的url
	public void login() {
//		String username = getPara("username");
//		String password = getPara("password");
//
//		if (username == null || password == null) {
//			render("user_login.html");
//			return;
//		}
//
//		List<User> userList = UserQuery.me().findByMobile(username);
//		if (null == userList || userList.size() == 0) {
//			if (isAjaxRequest()) {
//				renderAjaxResultForError("没有该用户");
//			} else {
//				setAttr("errorMsg", "没有该用户");
//				render("user_login.html");
//			}
//			return;
//		}
//
//
//
//		User user = userList.get(0);
//
//		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
//			MessageKit.sendMessage(Actions.USER_LOGINED, user);
//			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
//
//			if (!user.isAdministrator()) {
//				Department dept = tmpList.get(0);
//				if (dept == null) {
//					renderError(404);
//					return ;
//				}
//				String dealerDataArea = DepartmentQuery.me().getDealerDataArea(tmpList);
//				setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);
//				setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
//				setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
//				setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
////				setSessionAttr("cont", dept.get("seller_code"));
//			}
//
//			if (this.isAjaxRequest()) {
//				renderAjaxResultForSuccess("登录成功");
//			} else {
//				String gotoUrl = getPara("goto");
//				if (StringUtils.isNotEmpty(gotoUrl)) {
//					gotoUrl = StringUtils.urlDecode(gotoUrl);
//					gotoUrl = StringUtils.urlRedirect(gotoUrl);
//					redirect(gotoUrl);
//				} else {
//					redirect(Consts.ROUTER_USER_CENTER);
//				}
//			}
//		} else {
//			if (isAjaxRequest()) {
//				renderAjaxResultForError("密码错误");
//			} else {
//				setAttr("errorMsg", "密码错误");
//				render("user_login.html");
//			}
//		}
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

		render("user_bind.html");
	}

	//检测手机号
	@Clear(SessionInterceptor.class)
	public void checkMobile() {

		String mobile = getPara("mobile");
		List<User> list = UserQuery.me().findByMobile(mobile);
		if (list != null && list.size() > 0)
			renderAjaxResultForSuccess();
		else
			renderAjaxResultForError("手机号不存在");
	}

	@Clear(SessionInterceptor.class)
	public void update() {

		final String mobile = getPara("mobile");
		final String code = getPara("code");
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

					List<User> userList = UserQuery.me().findByMobile(mobile);
					if (userList == null || userList.size() == 0) {
						ret.set("message", "手机号不存在，请联系管理员");
						return false;
					}

					User user = userList.get(0);
					user.setAvatar(wxUserResult.getStr("headimgurl"));
					user.setNickname(wxUserResult.getStr("nickname"));
					user.setWechatOpenId(openId);
					if (!user.saveOrUpdate()) {
						ret.set("message", "手机号绑定失败，请联系管理员");
						return false;
					}

				}
				return true;
			}
		});

		if (updated) {
			renderAjaxResultForSuccess("绑定手机号成功");
			return ;
		}
		renderAjaxResultForError(ret.getStr("message"));
	}


	//设置页面
	public void config() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);
		setAttr("member", member);
		render("user_config.html");
	}


}
