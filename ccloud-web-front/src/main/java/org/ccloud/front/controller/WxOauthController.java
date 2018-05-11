package org.ccloud.front.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SellerQuery;
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
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

@RouterMapping(url = "/wxoauth")
public class WxOauthController extends BaseFrontController {

	@ActionCache
	@Clear(SessionInterceptor.class)
	@Before(WechatUserInterceptor.class)
	public void index() {
		
		String openId = null;
		String accessToken = null;
		String gotoUrl = getPara("goto", Consts.INDEX_URL);
		Object userJsonObj = getSession().getAttribute(Consts.SESSION_WECHAT_USER);
		
		if (userJsonObj == null) {
			renderText("您未关注公众号，请关注公众号‘慧经销’!");
			return ;
		}
		
		String wxUserJsonData = userJsonObj.toString();
		if (StrKit.notBlank(wxUserJsonData)) {
			
			JSONObject userJson = JSON.parseObject(wxUserJsonData);
			openId = userJson.getString("openid");
			accessToken = userJson.getString("access_token");
			
			if (StrKit.notBlank(openId, accessToken)) {
				CookieUtils.put(this, Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken, 7200);
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
					
					List<Department> tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
					if (tmpList.size() > 0) {
						Department dept = tmpList.get(0);
						String dealerDataArea = DepartmentQuery.me().getDealerDataArea(tmpList);
						setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);
						setSellerSession(dept.getStr("seller_id"), dept.getStr("seller_name"), dept.getStr("seller_code"));
					}
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
					List<Department> deptList = Lists.newArrayList();
					for (User user : userList) {
						deptList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
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
					setSessionAttr("sellerList", sellerList);
					String cookieUserId = getCookie(Consts.COOKIE_SELECTED_USER_ID);
					String cookieSellerId = getCookie(Consts.COOKIE_SELECTED_SELLER_ID);
					
					if (StrKit.notBlank(cookieSellerId, cookieUserId)) {
						
						// 用户模拟登录
						User curUser = UserQuery.me().findById(cookieUserId);
						if (curUser == null) {
							gotoUrl = "/user/choice";
							forwardAction(gotoUrl);
							return ;
						}
						
						init(curUser.getUsername(), curUser.getPassword(), true);
						// 获取用户所处的经销商账套
						List<Department> tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(curUser.getDepartmentId());
						String dealerDataArea = DepartmentQuery.me().getDealerDataArea(tmpList);
						setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);
						// 缓存用户的账套信息
						Seller seller = SellerQuery.me().findById(cookieSellerId);
						setSellerSession(seller.getId(), seller.getSellerName(), seller.getSellerCode());
						
						forwardAction(Consts.INDEX_URL);
						return ;
					}
					
					gotoUrl = "/user/choice";
				}
			}
		}
		
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
					String dataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, dataArea + "%");
				} else {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, user.getDataArea() + "%");
				}
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
	
	private void setSellerSession(String sellerId, String sellerName, String sellerCode) {
		setSessionAttr(Consts.SESSION_SELLER_ID, sellerId);
		setSessionAttr(Consts.SESSION_SELLER_NAME, sellerName);
		setSessionAttr(Consts.SESSION_SELLER_CODE, sellerCode);
	}
	
}
