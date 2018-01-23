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
package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.addon.HookInvoker;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.model.Dict;
import org.ccloud.model.User;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.ui.freemarker.tag.IndexPageTag;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
		String para = getPara();
		
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (user != null) {
			Dict order = DictQuery.me().findByKey("message_type", "order");
			Page<Record> orderPage = MessageQuery.me().paginateObj(getPageNumber(), Integer.MAX_VALUE, sellerId, order.getValue(), null, user.getId(), null);
			setAttr("orderPage", orderPage);
			
			Dict customer = DictQuery.me().findByKey("message_type", "customer");
			Page<Record> customerPage = MessageQuery.me().paginateObj(getPageNumber(), Integer.MAX_VALUE, sellerId, customer.getValue(), null, user.getId(), null);
			setAttr("customerPage", customerPage);
			
			Dict customerVisit = DictQuery.me().findByKey("message_type", "customer_visit");
			Page<Record> customerVisitPage = MessageQuery.me().paginateObj(getPageNumber(), Integer.MAX_VALUE, sellerId, customerVisit.getValue(), null, user.getId(), null);
			setAttr("customerVisitPage", customerVisitPage);
			
			setAttr("orderTotal", SalesOrderQuery.me().getToDo(user.getUsername()).size());
			setAttr("customerVisitTotal",CustomerVisitQuery.me().getToDo(user.getUsername()).size());
			setAttr("customerTotal",SellerCustomerQuery.me().getToDo(user.getUsername()).size());
			setAttr("activityApplyTotal", ActivityApplyQuery.me().getToDo(user.getUsername()).size());

			List<User> userList = UserQuery.me().findByMobile(user.getMobile());
			setSessionAttr("sellerListSize", userList.size());
			
			if (StringUtils.isBlank(para)) {
				setAttr(IndexPageTag.TAG_NAME, new IndexPageTag(getRequest(), null, 1, null));
				render("index.html");
				return;
			}
		}

		
		if (StrKit.isBlank(para)) {
			render("index.html");
			return ;
		}
		
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
