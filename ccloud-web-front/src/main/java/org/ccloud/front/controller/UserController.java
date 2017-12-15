package org.ccloud.front.controller;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.interceptor.UserInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Product;
import org.ccloud.model.Seller;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.SmsCodeQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = Consts.ROUTER_USER)
public class UserController extends BaseFrontController {

	public void index() {
		String action = getPara();
		if (StringUtils.isBlank(action)) {
			renderError(404);
		}

		keepPara();

		BigInteger userId = StringUtils.toBigInteger(action, null);
		if (userId != null) {
			
			
		} else {
			if ("detail".equalsIgnoreCase(action)) {
				renderError(404);
			}
			
			render(String.format("user_%s.html", action));
		}
	}
	
	@Clear(UserInterceptor.class)
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登录的url
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("user_login.html");
			return;
		}
		
		long errorTimes = CookieUtils.getLong(this, "_login_errors", 0);
		
//		if (errorTimes >= 3) {
//			if (!validateCaptcha("_login_captcha")) { // 验证码没验证成功！
//				if (isAjaxRequest()) {
//					renderAjaxResultForError("没有该用户");
//				} else {
//					redirect(Consts.ROUTER_USER_LOGIN);
//				}
//				return;
//			}
//		}
		
		List<User> userList = UserQuery.me().findByMobile(username);
		if (null == userList || userList.size() == 0) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
			return;
		}
		

		List<Map<String, String>> sellerList = Lists.newArrayList();
		for (User temp : userList) {
			List<Department> tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(temp.getDepartmentId());
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
			renderError(404);
			return ;
		} else if (sellerList.size() > 1) {
			setAttr("mobile", username);
			setAttr("sellerList", sellerList);
			setSessionAttr("sellerList", sellerList);
			forwardAction("/user/choice");
			return ;
		}
		
		User user = userList.get(0);
		
		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			//CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			
			if (!user.isAdministrator()) {
				Map<String, String> map = sellerList.get(0);
				setSessionAttr(Consts.SESSION_SELLER_ID, map.get("seller_id"));
				setSessionAttr(Consts.SESSION_SELLER_NAME, map.get("seller_name"));
				setSessionAttr(Consts.SESSION_SELLER_CODE, map.get("seller_code"));
			}
			// 获取用户权限
			init(user.getUsername(), user.getPassword(), true);
			
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
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
		}
	}
	
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
	
	public void change() {
		
		String mobile = getPara("mobile");
		String openid = getPara("openid");
		String sellerId = getPara("sellerId");
		User curUser = null;
		
		List<User> userList = UserQuery.me().findByWechatOpenid(openid);
		if (userList == null || userList.size() == 0)
			userList = UserQuery.me().findByMobile(mobile);
		
		for (User user : userList) {
			if (curUser != null)
				break;
			
			List<Department> deptList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
			for (Department dept : deptList) {
				if (StrKit.equals(sellerId, dept.getStr("seller_id"))) {
					curUser = user;
					setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
					setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
					setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
					break;
				}
			}
		}
		
		if (curUser == null) {
			renderError(404);
			return ;
		}
		
		init(curUser.getUsername(), curUser.getPassword(), true);
		
		redirect("/"); 
	}
	
	public void checkMobile() {
		
		String mobile = getPara("mobile");
		List<User> list = UserQuery.me().findByMobile(mobile);
		if (list != null && list.size() > 0)
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
					
					// 获取用户权限
					init(user.getUsername(), user.getPassword(), true);

					List<Department> tmpList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
					if (tmpList.size() > 0) {
						Department dept = tmpList.get(0);
						setSessionAttr(Consts.SESSION_SELLER_ID, dept.get("seller_id"));
						setSessionAttr(Consts.SESSION_SELLER_NAME, dept.get("seller_name"));
						setSessionAttr(Consts.SESSION_SELLER_CODE, dept.get("seller_code"));
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
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
}
