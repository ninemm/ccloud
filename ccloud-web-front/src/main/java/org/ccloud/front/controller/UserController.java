package org.ccloud.front.controller;

import java.sql.SQLException;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.SmsCodeQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = Consts.ROUTER_USER)
public class UserController extends BaseFrontController{

	public void index() {
		render("user.html");
	}
	
	public void login() {}
	
	public void center() {
		keepPara();
		String action = getPara(0, "index");
		render(String.format("user_center_%s.html", action));
	}
	
	public void bind() {
		
		String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
		ApiResult wxUserResult = UserApi.getUserInfo(openId);
		if (wxUserResult != null) {
			setAttr("avatar", wxUserResult.getStr("headimgurl"));
			setAttr("nickname", wxUserResult.getStr("nickname"));
		}
		
		render("user_bind.html");
	}
	
	public void checkMobile() {
		
		String mobile = getPara("mobile");
		User user = UserQuery.me().findByMobile(mobile);
		if (user != null)
			renderAjaxResultForSuccess();
		else
			renderAjaxResultForError("手机号不存在");
	}
	
	
	public void update() {
		
		final String mobile = getPara("mobile");
		final String code = getPara("code");
		final Ret ret = Ret.create();
		
		boolean updated = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				
				boolean isSend = false;
				// 验证短信验证码是否正确
				SmsCode smsCode = SmsCodeQuery.me().findByMobileAndCode(mobile, code);
				if (smsCode != null) {
					smsCode.setStatus(1);
					if (!smsCode.update()) {
						return false;
					}
					isSend = true;
				} else {
					return false;
				}
				
				if (isSend) {
					String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
					
					ApiResult wxUserResult = UserApi.getUserInfo(openId);
					if (wxUserResult != null) {
						User user = UserQuery.me().findByMobile(mobile);
						
						if (user == null) {
							ret.set("message", "手机号不存在，请联系管理员");
							return false;
						}
						
						user.setAvatar(wxUserResult.getStr("headimgurl"));
						user.setNickname(wxUserResult.getStr("nickname"));
						user.setWechatOpenId(openId);
						if (!user.saveOrUpdate()) {
							ret.set("message", "手机号绑定失败，请联系管理员");
							return false;
						}
						
						// 获取用户权限
						init(user.getUsername(), user.getPassword(), true);
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

	private void init(String username, String password, Boolean rememberMe) {
		
		Subject subject = SecurityUtils.getSubject();
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(username, password, rememberMe, "", "");
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			if (user != null) {
				// 数据查看时的数据域
				if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA,
							DataAreaUtil.getUserDeptDataArea(user.getDataArea()) + "%");
				} else {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, user.getDataArea());
				}

				// sellerId
				if (!subject.isPermitted("/admin/all")) {
					List<Record> sellerList = SellerQuery.me().querySellerIdByDept(user.getDepartmentId());

					if(sellerList.size() == 0) {
						sellerList = SellerQuery.me().queryParentSellerIdByDept(user.getDepartmentId());

						while(StrKit.isBlank(sellerList.get(0).getStr("sellerId"))) {
							sellerList = SellerQuery.me().queryParentSellerIdByDept(sellerList.get(0).getStr("parent_id"));
						}
					}

					setSessionAttr("sellerList", sellerList);
					setSessionAttr("sellerId", sellerList.get(0).get("sellerId"));
					setSessionAttr("sellerCode", sellerList.get(0).get("sellerCode"));
					setSessionAttr("sellerName", sellerList.get(0).get("sellerName"));
				}
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
}
