/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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
package org.ccloud.interceptor;

import com.jfinal.kit.StrKit;
import org.ccloud.menu.MenuManager;
import org.ccloud.model.User;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;

public class AdminInterceptor implements Interceptor {
	
	@Override
	public void intercept(Invocation inv) {

		Controller controller = inv.getController();
		
		String target = controller.getRequest().getRequestURI();
		String cpath = controller.getRequest().getContextPath();

		if (!target.startsWith(cpath + "/admin")) {
			inv.invoke();
			return;
		}

		controller.setAttr("c", controller.getPara("c"));// action
		controller.setAttr("p", controller.getPara("p"));// page
		controller.setAttr("m", controller.getPara("m"));// module
		controller.setAttr("t", controller.getPara("t"));// taxonomy
		controller.setAttr("s", controller.getPara("s"));//	status
		controller.setAttr("k", controller.getPara("k"));// keyword
		controller.setAttr("page", controller.getPara("page"));

		User user = InterUtils.tryToGetUser(inv);
		
		if (user != null) {
//			controller.setAttr("_menu_html", MenuManager.me().generateHtml());
			String htmlBuilder = CacheKit.get(MenuManager.CACHE_NAME, user.getId());
			if (StrKit.notBlank(htmlBuilder)) {
				controller.setAttr("_menu_html", htmlBuilder);
			} else {
				MenuManager.me().refresh();
				controller.setAttr("_menu_html", MenuManager.me().generateHtmlByUser(user));
			}
			inv.invoke();
			return;
		}

		controller.redirect("/admin/login");
	}
	

}
