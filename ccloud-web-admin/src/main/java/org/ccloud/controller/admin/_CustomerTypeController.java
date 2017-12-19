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

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.PriceSystemQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.model.ActReProcdef;
import org.ccloud.workflow.query.ActReProcdefQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customerType", viewPath = "/WEB-INF/admin/customer_type")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerTypeController extends JBaseCRUDController<CustomerType> {

	@Override
	@RequiresPermissions(value = { "/admin/customerType", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void index() {
		render("index.html");
	}

	@RequiresPermissions(value = { "/admin/customerType", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String show = getPara("show");

		Page<Record> page = null;
		if (SecurityUtils.getSubject().isPermitted("/admin/all")) {
			page = CustomerTypeQuery.me().paginate(getPageNumber(), getPageSize(), keyword, show,  null);
		} else {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			page = CustomerTypeQuery.me().paginate(getPageNumber(), getPageSize(), keyword, show,
					DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@RequiresPermissions(value = { "/admin/customerType/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void enable() {

		String id = getPara("id");
		int show = getParaToInt("show");

		if (CustomerTypeQuery.me().enable(id, show)) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	@Override
	@RequiresPermissions(value = { "/admin/customerType/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void edit() {
		String id = getPara("id");

		boolean notBlank = StrKit.notBlank(id);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		if (notBlank && isSuperAdmin) {// 超级管理员修改
			Record customerType = CustomerTypeQuery.me().findMoreById(id);
			List<Record> priceSystemList = PriceSystemQuery.me().findPriceSystemByDeptId(customerType.getStr("dept_id"),
					customerType.getStr("data_area"));
			setAttr("customerType", customerType);
			setAttr("priceSystemList", priceSystemList);

		} else if (notBlank && !isSuperAdmin) {// 经销商管理员修改
			setAttr("customerType", CustomerTypeQuery.me().findById(id));
			setAttr("priceSystemList", PriceSystemQuery.me().findPriceSystemByDeptId(user.getDepartmentId(),
					DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea())));

		} else if (!notBlank && !isSuperAdmin) {// 经销商管理员新增
			setAttr("priceSystemList", PriceSystemQuery.me().findPriceSystemByDeptId(user.getDepartmentId(),
					DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea())));
		}
		
		List<ActReProcdef> procDefList = ActReProcdefQuery.me().findListInNormal();
		setAttr("procDefList", procDefList);

		render("edit.html");
	}

	@Override
	@RequiresPermissions(value = { "/admin/customerType/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void save() {

		CustomerType customerType = getModel(CustomerType.class);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		if (SecurityUtils.getSubject().isPermitted("/admin/all")) {
			customerType.set("dept_id", getPara("parent_id"));
			customerType.set("data_area", getPara("data_area"));
		} else {
			customerType.set("dept_id", user.getDepartmentId());
			customerType.set("data_area", DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea()));
		}

		customerType.saveOrUpdate();

		renderAjaxResultForSuccess();

	}

	@RequiresPermissions(value = { "/admin/customerType/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void findPriceSystemByDeptId() {
		List<Record> priceSystemList = PriceSystemQuery.me().findPriceSystemByDeptId(getPara("parent_id"),
				getPara("data_area"));

		renderJson(priceSystemList);

	}
}
