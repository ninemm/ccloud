/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.controller.admin;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.AdminInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Department;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserJoinCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;

import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/admin", viewPath = "/WEB-INF/admin")
@RouterNotAllowConvert
public class _AdminController extends JBaseController {

	@Before(ActionCacheClearInterceptor.class)
	public void index() {

		/*
		 * List<TplModule> moduleList = TemplateManager.me().currentTemplateModules();
		 * setAttr("modules", moduleList);
		 * 
		 * if (moduleList != null && moduleList.size() > 0) { String moduels[] = new
		 * String[moduleList.size()]; for (int i = 0; i < moduleList.size(); i++) {
		 * moduels[i] = moduleList.get(i).getName(); }
		 * 
		 * List<Content> contents = ContentQuery.me().findListInNormal(1, 20, null,
		 * null, null, null, moduels, null, null, null, null, null, null, null, null);
		 * setAttr("contents", contents); }
		 * 
		 * Page<Comment> commentPage =
		 * CommentQuery.me().paginateWithContentNotInDelete(1, 10, null, null, null,
		 * null); if (commentPage != null) { setAttr("comments", commentPage.getList());
		 * }
		 */

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (user == null) {
			redirect("/admin/login");
			return;
		}
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		DecimalFormat df = new DecimalFormat("###,###,###,##0.00");
		Record count = new Record();
		if(StrKit.notBlank(selDataArea)) {
			count = SalesOrderQuery.me().queryCountToDayOrders(user.getId(), selDataArea);
		}
		Map<String, List<Record>> salesManAmount = Maps.newHashMap();
		salesManAmount.put("sales_day", SalesOrderQuery.me().querysalesManAmountBy(selDataArea,"day","desc"));
		salesManAmount.put("sales_month", SalesOrderQuery.me().querysalesManAmountBy(selDataArea,"month","desc"));
		
		Map<String, List<Record>> goodsSales = Maps.newHashMap();
		goodsSales.put("goodsSalesToDay", SalesOrderQuery.me().queryGoodsSales(selDataArea, true,"desc"));
		goodsSales.put("goodsSalesAll", SalesOrderQuery.me().queryGoodsSales(selDataArea, false,"desc"));
		
		Map<String, List<Record>> directBusinessAmount = Maps.newHashMap();
		directBusinessAmount.put("directs_day", SalesOrderQuery.me().querySellerSales(selDataArea, "day","desc"));
		directBusinessAmount.put("directs_month", SalesOrderQuery.me().querySellerSales(selDataArea, "month","desc"));
		
		Map<String, List<Record>> amountCollect = Maps.newHashMap();
		amountCollect.put("amount_weeks", SalesOrderQuery.me().queryAmountBy(selDataArea, "weeks"));
		amountCollect.put("amount_months", SalesOrderQuery.me().queryAmountBy(selDataArea, "months"));
		amountCollect.put("amount_quarter", SalesOrderQuery.me().queryAmountBy(selDataArea, "quarter"));
		
		setAttr("toDoCustomerList", SellerCustomerQuery.me().getToDo(user.getUsername()));
		setAttr("toDoOrdersList", SalesOrderQuery.me().getToDo(user.getUsername()));
		setAttr("count_order", StrKit.notBlank(selDataArea)?count.get("count_order"):0);
		setAttr("sum_amount", StrKit.notBlank(selDataArea)?count.get("sum_amount")!=null?df.format(count.get("sum_amount")):"0.00":"0.00");
		setAttr("toDoCustomerVisitList", CustomerVisitQuery.me().getToDo(user.getUsername()));
		
		setAttr("customerCount",StrKit.notBlank(selDataArea)?UserJoinCustomerQuery.me().customerCount(selDataArea,false):0);
		setAttr("newCustomerCount",StrKit.notBlank(selDataArea)?UserJoinCustomerQuery.me().customerCount(selDataArea,true):0);
		setAttr("salesManAmount",JSON.toJSONString(salesManAmount));
		setAttr("goodsSales",JSON.toJSONString(goodsSales));
		setAttr("directAmount",JSON.toJSONString(directBusinessAmount));
		setAttr("amountCollect",JSON.toJSONString(amountCollect));
		
		setSessionAttr("sellerCustomerList", SellerCustomerQuery.me().getToDo(user.getUsername()).size());
		setSessionAttr("orderList", SalesOrderQuery.me().getToDo(user.getUsername()).size());
		setSessionAttr("sellerCustomerVisitList", CustomerVisitQuery.me().getToDo(user.getUsername()).size());
		setSessionAttr("allList", SalesOrderQuery.me().getToDo(user.getUsername()).size()+CustomerVisitQuery.me().getToDo(user.getUsername()).size()+SellerCustomerQuery.me().getToDo(user.getUsername()).size());
		
		setAttr("identity",SecurityUtils.getSubject().isPermitted("/admin/manager"));
		
		String changePassword="false";
		String mobile = user.getMobile();
		//将手机号作为键和值存到cookie 第一次登录没有修改密码 弹框提示
		String change=CookieUtils.get(this, user.getMobile());
		//先判断手机号在cookie里面有没有 再判断登录用户是不是使用的初始密码
		if (!mobile.equals(change)&&user.getPassword().equals(EncryptUtils.encryptPassword(Consts.USER_DEFAULT_PASSWORD, user.getSalt()))) {
			//没有修改密码 且第一次登录
			CookieUtils.put(this, mobile,mobile);
			changePassword="true";
		}
		setAttr("changePassword",changePassword);

		render("index.html");
	}
	
	@Clear(AdminInterceptor.class)
	public void login() {
		
		String usernameORmobile = getPara("usernameORmobile");
		String password = getPara("password");
		String rememberMeStr = getPara("remember_me");
		boolean rememberMe = false;
		if (rememberMeStr != null && rememberMeStr.equals("on")) {
			rememberMe = true;
		}

		if (!StringUtils.areNotEmpty(usernameORmobile, password)) {
			render("login.html");
			return;
		}
		
		User _user;
		try {
			_user = UserQuery.me().findUserByMobile(usernameORmobile);
			if (null==_user) {
				_user = UserQuery.me().findUserByUsername(usernameORmobile);
			}
			password = EncryptUtils.encryptPassword(password, _user.getSalt());
		} catch (Exception e1) {
			
			e1.printStackTrace();
			renderJson(false);
			return;
		}
		Subject subject = SecurityUtils.getSubject();
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(_user.getUsername(), password, rememberMe, "", "");
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			if (user != null) {
				// 数据查看时的数据域
				if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
					String dataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea()) + "%";
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, dataArea);
				} else {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, user.getDataArea());
				}
				String mobile = user.getMobile();
				List<User> userList = UserQuery.me().findByMobile(mobile);
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
				
				if (sellerList.size() == 0 && !user.isAdministrator()) {
					renderError(404);
					return ;
				} else if (sellerList.size() > 1) {
					setAttr("mobile", mobile);
					setAttr("sellerList", sellerList);
					setSessionAttr("sellerList", sellerList);
					forwardAction("/admin/choice");
					Map<String, Object> map = new HashMap<>();
					map.put("mobile", mobile);
					map.put("sellerList", sellerList);
					map.put("size", sellerList.size());
					renderJson(map);
					return ;
				}
				
				if (!user.isAdministrator() && tmpList != null) {
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
					setSessionAttr(Consts.SESSION_SELLER_HAS_STORE, dept.get("has_store"));
				} else {
					setSessionAttr(Consts.SESSION_DEALER_DATA_AREA, DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea()) + "%");
				}
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
//			String change=CookieUtils.get(this, mobile);
//			if (!mobile.equals(change)) {
//				if (password.equals(EncryptUtils.encryptPassword("123456", _user.getSalt()))) {
//					CookieUtils.put(this, mobile,mobile);
//					renderAjaxResultForSuccess("change");
//					return;
//				}
//			}
			renderJson(true);
			//redirect("/admin/index");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//renderAjaxResultForError("用户名或密码错误");
			renderJson(false);
			return;
		}
	}
	
	@Clear(AdminInterceptor.class)
	public void choice() { 
		List<Map<String, String>> sellerList = getSessionAttr("sellerList");
		String moblie = getPara("mobile");
		setAttr("sellerList", sellerList);
		setAttr("mobile",moblie);
		//keepPara();
		render("choice.html");
		
	}

	@Before(UCodeInterceptor.class)
	public void logout() {
		removeSessionAttr(Consts.SESSION_LOGINED_USER);
		removeSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		removeSessionAttr(Consts.SESSION_SELLER_ID);
		removeSessionAttr(Consts.SESSION_SELLER_CODE);
		removeSessionAttr(Consts.SESSION_SELLER_NAME);
		removeSessionAttr("sellerList");
		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		redirect("/admin");
	}

	public void checkRole() {
		render("404.html");
	}
	
	@Clear(AdminInterceptor.class)
	public void change() {
		
		String mobile = getPara("mobile");
		String sellerId = getPara("sellerId");
		User curUser = null;
		
		List<User> userList = UserQuery.me().findByMobile(mobile);
		
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
					setSessionAttr(Consts.SESSION_SELLER_HAS_STORE, dept.get("has_store"));
					break;
				}
			}
		}
		
		if (curUser == null) {
			renderError(404);
			return ;
		}
		
		init(curUser.getUsername(), curUser.getPassword(), true);
		
		redirect("/admin/index");
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
							DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea()) + "%");
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
