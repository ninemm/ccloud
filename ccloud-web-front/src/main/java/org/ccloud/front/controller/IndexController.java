package org.ccloud.front.controller;
/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@126.com).
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

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.addon.HookInvoker;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.StringUtils;

import com.jfinal.render.Render;

@RouterMapping(url = "/")
public class IndexController extends BaseFrontController {

	@ActionCache
	public void index() {
		try {
			Render render = onRenderBefore();
			if (render != null) {
				render(render);
			} else {
				doRender();
			}
		} finally {
			onRenderAfter();
		}
	}

	private void doRender() {
		setGlobleAttrs();
		//String para = getPara();
		render("index.html");

	}

	private void setGlobleAttrs() {
		String title = OptionQuery.me().findValue("seo_index_title");
		String keywords = OptionQuery.me().findValue("seo_index_keywords");
		String description = OptionQuery.me().findValue("seo_index_description");

		if (StringUtils.isNotBlank(title)) {
			setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, title);
		}

		if (StringUtils.isNotBlank(keywords)) {
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, keywords);
		}

		if (StringUtils.isNotBlank(description)) {
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, description);
		}
	}

	private Render onRenderBefore() {
		return HookInvoker.indexRenderBefore(this);
	}

	private void onRenderAfter() {
		HookInvoker.indexRenderAfter(this);
	}

}
