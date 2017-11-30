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

import java.util.List;

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
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;

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
//		setAttr("toDoCustomerList", CustomerQuery.me().getToDo(user.getUsername()));

		render("index.html");
	}

	@Clear(AdminInterceptor.class)
	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		String rememberMeStr = getPara("remember_me");
		boolean rememberMe = false;
		if (rememberMeStr != null && rememberMeStr.equals("on")) {
			rememberMe = true;
		}

		if (!StringUtils.areNotEmpty(username, password)) {
			render("login.html");
			return;
		}

		Subject subject = SecurityUtils.getSubject();
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(username, password, rememberMe, "", "");
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			if (user != null) {
				List<Seller> sellerList = SellerQuery.me().querySellIdByDept(user.getDepartmentId());

				// 数据查看时的数据域
				if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA,
							DataAreaUtil.getUserDeptDataArea(user.getDataArea()) + "%");
				} else {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, user.getDataArea());
				}
				setSessionAttr("sellerList", sellerList);
				if (sellerList.size() > 0) {
					setSessionAttr("sellerId", sellerList.get(0).getId());
					setSessionAttr("sellerCode", sellerList.get(0).getSellerCode());
					setSessionAttr("sellerName", sellerList.get(0).getSellerName());
				}
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
			renderAjaxResultForSuccess("登录成功");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			renderAjaxResultForError("用户名或密码错误");
		}
	}

	@Before(UCodeInterceptor.class)
	public void logout() {
		removeSessionAttr(Consts.SESSION_LOGINED_USER);
		removeSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		removeSessionAttr("sellerId");
		removeSessionAttr("sellerCode");
		removeSessionAttr("sellerName");
		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		redirect("/admin");
	}

	public void checkRole() {
		render("404.html");
	}

}
