/**
 * Copyright (c) 2015-2016, Eric Huang (ninemm@qq.com).
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
package org.ccloud.front.controller;

import org.ccloud.core.JBaseController;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;

import com.jfinal.aop.Clear;
import com.jfinal.plugin.ehcache.CacheKit;

@Clear
@RouterNotAllowConvert
@RouterMapping(url = "/counter")
public class VisitorCounter extends JBaseController {

	private static final String CACHE_NAME = "visitor_counter";
	private static final String CID = "cid:";
	private static final String RID = "rid:";
	private String type;

	public void index() {
		String type = getPara("type");
		String id = getPara("id");
		if (id == null) {
			renderJavascript("");
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id, type));
		visitorCount = visitorCount == null ? 0 : visitorCount;
		CacheKit.put(CACHE_NAME, buildKey(id, type), visitorCount + 1);
		renderJavascript("");
	}

	public void show() {
		String id = getPara("id");
		if (id == null) {
			renderNull();
			return;
		}

		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id, type));
		visitorCount = visitorCount == null ? 0 : visitorCount;
		renderText(visitorCount + "");
	}

	public static long getVisitorCount(String id, String type) {
		Long visitorCount = CacheKit.get(CACHE_NAME, buildKey(id, type));
		return visitorCount == null ? 0 : visitorCount;
	}

	public static void clearVisitorCount(String id, String type) {
		CacheKit.remove(CACHE_NAME, buildKey(id, type));
	}

	private static String buildKey(String id, String type) {
		
		if ("route".equals(type))
			return RID + type + ":" +  id;
		else
			return CID + type + ":" +  id;
	}

}
