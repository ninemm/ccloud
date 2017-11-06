/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.PriceSystem;
import org.ccloud.model.User;
import org.ccloud.model.query.PriceSystemQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/priceSystem", viewPath = "/WEB-INF/admin/price_system")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value = { "/admin/priceSystem", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
public class _PriceSystemController extends JBaseCRUDController<PriceSystem> {

	@Override
	public void index() {
		render("index.html");
	}

	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		User user = getSessionAttr("user");

		Page<Record> page = PriceSystemQuery.me().paginate(getPageNumber(), getPageSize(), keyword,
				user.getDepartmentId(), DataAreaUtil.getUserDeptDataArea(user.getDataArea()),
				SecurityUtils.getSubject().isPermitted("/admin/all"));

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());

		renderJson(map);

	}

	@Override
	public void save() {

		PriceSystem priceSystem = getModel(PriceSystem.class);
		User user = getSessionAttr("user");

		if (SecurityUtils.getSubject().isPermitted("/admin/all")) {
			priceSystem.set("dept_id", getPara("parent_id"));
			priceSystem.set("data_area", getPara("data_area"));
		} else {
			priceSystem.set("dept_id", user.getDepartmentId());
			priceSystem.set("data_area", DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
		}

		priceSystem.saveOrUpdate();

		renderAjaxResultForSuccess();

	}
}
