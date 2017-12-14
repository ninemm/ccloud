package org.ccloud.front.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.wechat.WechatUserInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
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
			
			List<User> userList = UserQuery.me().findByWechatOpenid(openId);
			if (userList == null || userList.size() == 0) {
				gotoUrl = "/user/bind";
			} else {
				int count = userList.size();
				if (count == 1) {
					User user = userList.get(0);
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
						setSessionAttr(Consts.SESSION_LOGINED_USER, user);
					} else {
						LogKit.warn("user info get failure");
					}
				} else {
					List<Map<String, String>> sellerList = Lists.newArrayList();
					for (User user : userList) {
						List<Department> deptList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
						if (deptList.size() > 0) {
							Department dept = deptList.get(0);
							Map<String, String> seller = Maps.newHashMap();
							seller.put("seller_id", dept.getStr("seller_id"));
							seller.put("seller_name", dept.getStr("seller_name"));
							seller.put("seller_code", dept.getStr("seller_code"));
							sellerList.add(seller);
						}
					}
					setAttr("openid", openId);
					setAttr("sellerList", sellerList);
					gotoUrl = "/user/choice";
				}
			}
		}
		
		//redirect(gotoUrl);
		forwardAction(gotoUrl);
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
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
	
}
