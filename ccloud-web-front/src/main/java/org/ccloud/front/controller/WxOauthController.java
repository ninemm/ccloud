package org.ccloud.front.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.User;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.wechat.WechatUserInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

@RouterMapping(url = "/wxoauth")
public class WxOauthController extends BaseFrontController {

	@ActionCache
	@Before(WechatUserInterceptor.class)
	public void index() {
		
		String openId = null;
		String accessToken = null;
		String gotoUrl = getPara("goto", "/");
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
				return ;
			}
			
			User user = UserQuery.me().findByWechatOpenid(openId);
			if (user == null) {
				gotoUrl = "/user/bind";
			} else {
				
				// 获取用户的相应权限放入缓存
				init(user.getUsername(), user.getPassword(), true);
				
				// 更新用户的信息
				ApiResult wxUserResult = UserApi.getUserInfo(openId);
				if (wxUserResult.isSucceed()) {
					user.setAvatar(wxUserResult.getStr("headimgurl"));
					user.setNickname(wxUserResult.getStr("nickname"));
					user.setWechatOpenId(openId);
					
					if (!user.saveOrUpdate()) {
						renderError(500);
						return ;
					}
				} else {
					LogKit.warn("user info get failure");
				}
					
			}
		}
		
		redirect(gotoUrl);
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
