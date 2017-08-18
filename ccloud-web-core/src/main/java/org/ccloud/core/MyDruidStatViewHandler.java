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
package org.ccloud.core;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccloud.Consts;
import org.ccloud.model.User;
import org.ccloud.model.query.UserQuery;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidStatViewHandler;

public class MyDruidStatViewHandler extends DruidStatViewHandler {
	
	static String visitPath = "/admin/druid";

	public MyDruidStatViewHandler() {
		super(visitPath);
	}

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if (target.startsWith(visitPath) && CCloud.isInstalled() && CCloud.isLoaded()) {

			String encrypt_key = PropKit.get("encrypt_key");
			String cookieInfo = getCookie(request, Consts.COOKIE_LOGINED_USER);

			String userId = CookieUtils.getFromCookieInfo(encrypt_key, cookieInfo);
			if (StringUtils.isNotBlank(userId)) {
				User user = UserQuery.me().findById(userId);
				if (user != null && user.isAdministrator()) {
					super.handle(target, request, response, isHandled);
					return;
				}
			}
		}

		next.handle(target, request, response, isHandled);
	}

	private String getCookie(HttpServletRequest request, String name) {
		Cookie cookie = getCookieObject(request, name);
		return cookie != null ? cookie.getValue() : null;
	}

	private Cookie getCookieObject(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(name))
					return cookie;
		return null;
	}

}
