package org.ccloud.front.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.interceptor.UserInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.qyweixin.sdk.api.ApiResult;
import com.jfinal.qyweixin.sdk.api.ConUserApi;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = Consts.ROUTER_USER)
public class UserController extends BaseFrontController {
	
	@Clear({SessionInterceptor.class})
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
			/*User user1 = getSessionAttr(Consts.SESSION_LOGINED_USER);
			if(user1!=null){
				List<User> userList = UserQuery.me().findByMobile(user1.getMobile());
				setSessionAttr("sellerListSize", userList.size());
			}*/
			render(String.format("user_%s.html", action));
		}
	}
	
	@Clear({UserInterceptor.class,SessionInterceptor.class})
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登录的url
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("user_login.html");
			return;
		}
		
		List<User> userList = UserQuery.me().findByMobile(username);
		if (null == userList || userList.size() == 0) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("user_login.html");
			}
			return;
		}

		List<Map<String, String>> sellerList = Lists.newArrayList();
		List<Department> tmpList = Lists.newArrayList();
		for (User temp : userList) {
			tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(temp.getDepartmentId());
			if (tmpList.size() > 0) {
				Department dept = tmpList.get(0);
				Map<String, String> seller = Maps.newHashMap();
				seller.put("seller_id", dept.getStr("seller_id"));
				seller.put("seller_name", dept.getStr("seller_name"));
				seller.put("seller_code", dept.getStr("seller_code"));
				sellerList.add(seller);
			}
		}
		
		if (sellerList.size() == 0) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有分配销售商");
			} else {
				renderError(404);
			}
			return ;
		} else if (sellerList.size() > 1) {
			setAttr("mobile", username);
			setSessionAttr("sellerList", sellerList);
			forwardAction("/user/choice");
			return ;
		}
		
		User user = userList.get(0);
		
		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			
			if (!user.isAdministrator()) {
				Department dept = tmpList.get(0);
				if (dept == null) {
					renderError(404);
					return ;
				}
				String dealerDataArea = DepartmentQuery.me().getDealerDataArea(tmpList);
				setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);
				setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
				setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
				setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
//				setSessionAttr("cont", dept.get("seller_code"));
			}
			// 获取用户权限
			initUserRole(user.getUsername(), user.getPassword(), true);
			
			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登录成功");
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				setAttr("errorMsg", "密码错误");
				render("user_login.html");
			}
		}
	}
	
	public void center() {
		keepPara();
		String action = getPara(0, "index");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
//		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
//		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";

//		Long totalOrderCount = SalesOrderQuery.me().findTotalOrdersCountByDataArea(dataArea);
//		Long totalCustomerCount = SellerCustomerQuery.me().findTotalCountByDataArea(dataArea);
//		setAttr("totalOrderCount", totalOrderCount.intValue());
//		setAttr("totalCustomerCount", totalCustomerCount.intValue());
		setAttr("orderTotal", SalesOrderQuery.me().findToDoOrderReviewCount(user.getUsername()));
		setAttr("customerVisitTotal", CustomerVisitQuery.me().findToDoCustomerVisitReviewCount(user.getUsername()));
		setAttr("customerTotal", SellerCustomerQuery.me().findToDoCustomerReviewCount(user.getUsername()));
		setAttr("activityApplyTotal", ActivityApplyQuery.me().findToDoActivityReviewCount(user.getUsername()));
		
		render(String.format("user_center_%s.html", action));
	}
	
	//绑定用户信息
	@Clear({SessionInterceptor.class})
	public void bind() {
		
		String wechatUserId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
		ApiResult wxUserResult = ConUserApi.getUser(wechatUserId);
		if (wxUserResult != null) {
			setAttr("avatar", wxUserResult.getStr("avatar"));
			setAttr("nickname", wxUserResult.getStr("name"));
		}
		
		render("user_bind.html");
	}
	
	//选择账套
	@Clear(SessionInterceptor.class)
	public void change() {
		
		User curUser = initSellerAccount();
		
		if (curUser == null) {
			if (isAjaxRequest())
				renderAjaxResultForError("切换账号失败");
			else
				renderError(404);
			return ;
		}
		setSessionAttr(Consts.SESSION_LOGINED_USER, curUser);
		initUserRole(curUser.getUsername(), curUser.getPassword(), true);
		
		if (isAjaxRequest()) {
			renderAjaxResultForSuccess("切换账号成功");
			return ;
		}
		redirect(Consts.INDEX_URL);
	}
	
	private User initSellerAccount() {
		User curUser = null;
		String mobile = getPara("mobile");
		String openid = getPara("openid");
		String wechatUserId = getPara("wechatUserId");
		String sellerId = getPara("sellerId");
		
		User tmpUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (tmpUser != null) {
			openid = getPara("openid", tmpUser.getWechatOpenId());
			mobile = getPara("mobile", tmpUser.getMobile());
			wechatUserId = getPara("wechatUserId", tmpUser.getWechatUseriId());
		}
		
		List<User> userList = Lists.newArrayList();
		if (StrKit.notBlank(openid))
			userList = UserQuery.me().findByWechatOpenid(openid);
		else if (StrKit.notBlank(mobile))
			userList = UserQuery.me().findByMobile(mobile);
		else if (StrKit.notBlank(wechatUserId))
			userList = UserQuery.me().findByWechatUserId(wechatUserId);
		
		for (User user : userList) {
			if (curUser != null)
				break;
			
			List<Department> deptList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
			for (Department dept : deptList) {
				if (StrKit.equals(sellerId, dept.getStr("seller_id"))) {
					curUser = user;
					String dealerDataArea = DepartmentQuery.me().getDealerDataArea(deptList);
					setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);					
					setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
					setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
					setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
					
					setCookie(Consts.COOKIE_SELECTED_SELLER_ID, sellerId, 60 * 60 * 24 * 7);
					setCookie(Consts.COOKIE_SELECTED_USER_ID, curUser.getId(), 60 * 60 * 24 * 7);
					break;
				}
			}
		}
		
		return curUser;
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
				
				String wechatUserId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
				ApiResult wxUserResult = ConUserApi.getUser(wechatUserId);
				if (wxUserResult != null) {
					
					List<User> userList = UserQuery.me().findByMobile(mobile);
					if (userList == null || userList.size() == 0) {
						ret.set("message", "手机号不存在，请联系管理员");
						return false;
					}
					
					User user = userList.get(0);
					user.setAvatar(wxUserResult.getStr("avatar"));
					try {
						if (StrKit.notBlank(wxUserResult.getStr("nickname"))) {
							String nickname = URLEncoder.encode(wxUserResult.getStr("nickname"), "utf-8");
							user.setNickname(nickname);
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//						user.setNickname(wxUserResult.getStr("nickname"));
					user.setWechatUseriId(wechatUserId);
					if (!user.saveOrUpdate()) {
						ret.set("message", "手机号绑定失败，请联系管理员");
						return false;
					}
					
					// 获取用户权限
					initUserRole(user.getUsername(), user.getPassword(), true);

					List<Department> tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
					if (tmpList.size() > 0) {
						Department dept = tmpList.get(0);
						setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
						setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
						setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
						String dealerDataArea = DepartmentQuery.me().getDealerDataArea(tmpList);
						setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, dealerDataArea);						
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

	private void initUserRole(String username, String password, Boolean rememberMe) {
		
		Subject subject = SecurityUtils.getSubject();
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(username, password, rememberMe, "", "");
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			if (user != null) {
				// 数据查看时的数据域
				if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA,
							DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea()) + "%");
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
	
	//设置页面
	public void config() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		User nowUser = UserQuery.me().findById(user.getId());
		setAttr("user", nowUser);
		render("user_config.html");
	}
	
	//业务员负责区域
	public void alterUser() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		User nowUser = UserQuery.me().findById(user.getId());
		String areaName = getPara("areaName");
		String[] split = areaName.split(",");
		nowUser.setProvince(split[0]);
		nowUser.setCity(split[1]);
		nowUser.setRegion(split[2]);
		nowUser.update();
		render("user_config.html");
	}
	
	
	@Clear({SessionInterceptor.class})
	public void choice() {
		render("user_choice.html");
	}
	
	@Clear({SessionInterceptor.class})
	public void timeout() {
		render("timeout.html");
		return;
	}
	
}
