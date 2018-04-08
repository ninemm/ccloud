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
import org.ccloud.wwechat.WorkWechatApiConfigInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.qyweixin.sdk.api.ApiResult;
import com.jfinal.qyweixin.sdk.api.ConUserApi;
import com.jfinal.qyweixin.sdk.api.OAuthApi;

@RouterMapping(url = "/woauth")
public class WWxOauthController extends BaseFrontController {

	@ActionCache
	@Clear(SessionInterceptor.class)
	@Before(WorkWechatApiConfigInterceptor.class)
	public void index() {
		String gotoUrl = getPara("goto", Consts.INDEX_URL);
		String wechatUserJson = getSessionAttr(Consts.SESSION_WECHAT_USER);
		String code = getPara("code");
		
		if (StrKit.isBlank(wechatUserJson)) {
			ApiResult userResult = OAuthApi.getUserInfoByCode(code);
			if (userResult != null) {
				setSessionAttr(Consts.SESSION_WECHAT_USER, userResult.getJson());
				wechatUserJson = userResult.getJson();
			}
		}
		
		JSONObject userJson = JSON.parseObject(wechatUserJson);
		String wechatUserId = userJson.getString("UserId");
		if (StrKit.isBlank(wechatUserId)) {
			renderError(500);
			return ;
		} else {
			CookieUtils.put(this, Consts.SESSION_WECHAT_OPEN_ID, wechatUserId);
			setSessionAttr(Consts.SESSION_WECHAT_OPEN_ID, wechatUserId);
		}
		
		List<User> userList = UserQuery.me().findByWechatUserId(wechatUserId);
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
				ApiResult wxUserResult = ConUserApi.getUser(wechatUserId);
				if (wxUserResult.isSucceed()) {
					user.setAvatar(wxUserResult.getStr("avatar"));
					user.setNickname(wxUserResult.getStr("name"));
					user.setWechatUserId(wechatUserId);
					
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
				
				setAttr("wechatUserId", wechatUserId);
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
