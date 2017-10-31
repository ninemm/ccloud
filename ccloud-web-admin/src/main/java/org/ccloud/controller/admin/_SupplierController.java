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

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Supplier;
import org.ccloud.model.query.SupplierQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/supplier", viewPath = "/WEB-INF/admin/supplier")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"supplier:view","admin:all"},logical=Logical.OR)
public class _SupplierController extends JBaseCRUDController<Supplier> {

	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		Page<Supplier> page = SupplierQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "create_date");
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	@Override
	@RequiresPermissions(value={"supplier:edit","admin:all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Supplier supplier = SupplierQuery.me().findById(id);
			setAttr("supplier", supplier);
		}
	}
	
	@Override
	@RequiresPermissions(value={"supplier:edit","admin:all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		final Supplier r = SupplierQuery.me().findById(id);
		if (r != null) {
			if (r.delete()) {
				renderAjaxResultForSuccess("删除成功");
				return;
			}
		}
		renderAjaxResultForError("删除失败");
	}

	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"supplier:edit","admin:all"},logical=Logical.OR)
	public void batchDelete() {

		String[] ids = getParaValues("dataItem");
		int count = SupplierQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}

	}

	@RequiresPermissions(value={"supplier:edit","admin:all"},logical=Logical.OR)
	public void enable() {
		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");
		Supplier supplier = SupplierQuery.me().findById(id);
		supplier.setIsEnable(isEnabled);
		if (supplier.saveOrUpdate()) {
			renderAjaxResultForSuccess("更新成功");
		} else {
			renderAjaxResultForError("更新失败");
		}

	}

}
