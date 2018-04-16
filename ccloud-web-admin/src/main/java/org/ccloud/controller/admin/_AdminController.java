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
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;
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
import org.ccloud.model.query.BiManagerQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;

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
		
		render("index.html");
	}
	@Clear(AdminInterceptor.class)
	public void login() {
		
		String usernameORmobile = getPara("usernameORmobile");
		String password = getPara("password");
		String rememberMeStr = getPara("remember_me");
		boolean rememberMe = false;
		if (rememberMeStr != null && rememberMeStr.equals("onb")) {
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

			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);

			List<Record> sellerByUser = BiManagerQuery.me().findSellerByUser(user.getId());
			String sellerArray[] = new String[sellerByUser.size()];
			String sellerNameArray[] = new String[sellerByUser.size()];
			for (int i = 0; i < sellerByUser.size(); i++) {
				sellerArray[i] = sellerByUser.get(i).getStr("dealer_data_area");
				sellerNameArray[i] = sellerByUser.get(i).getStr("seller_name");
			}

			List<Record> brandByUser = BiManagerQuery.me().findBrandByUser(user.getId());
			String brandArray[] = new String[brandByUser.size()];
			for (int i = 0; i < brandByUser.size(); i++) {
				brandArray[i] = brandByUser.get(i).getStr("brand_id");
			}

			List<Record> productByUser = BiManagerQuery.me().findProductByUser(user.getId());
			String productArray[] = new String[productByUser.size()];
			for (int i = 0; i < productByUser.size(); i++) {
				productArray[i] = productByUser.get(i).getStr("product_id");
			}

			setSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY, sellerArray);
			setSessionAttr(Consts.SESSION_SELLER_NAME, sellerNameArray);
			setSessionAttr(Consts.SESSION_BRAND_ID_ARRAY, brandArray);
			setSessionAttr(Consts.SESSION_PRODUCT_ID_ARRAY, productArray);

			renderJson(true);
			//redirect("/admin/index");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//renderAjaxResultForError("用户名或密码错误");
			renderJson(false);
			return;
		}
	}


	@Before(UCodeInterceptor.class)
	public void logout() {
		removeSessionAttr(Consts.SESSION_LOGINED_USER);

		CookieUtils.remove(this, Consts.COOKIE_LOGINED_USER);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		redirect("/admin");
	}

	public void checkRole() {
		render("404.html");
	}

}
