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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Department;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/department", viewPath = "/WEB-INF/admin/department")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _DepartmentController extends JBaseCRUDController<Department> {

	public void list() {

		String keyword = getPara("k", "");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		String parentId = getPara("parentId", "0");

		Page<Department> page = DepartmentQuery.me().paginate(getPageNumber(), getPageSize(), parentId, keyword, null);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@Override
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			setAttr("dept", DepartmentQuery.me().findById(id));
			List<User> list = UserQuery.me().findByDeptId(id);
			setAttr("list", list);
		}

	}

	@Override
	public void save() {

		final Department dept = getModel(Department.class);
		Department parent = DepartmentQuery.me().findById(dept.getParentId());
		String dataArea = DataAreaUtil.dataAreaSetByDept(dept.getDeptLevel(), parent.getDataArea());
		dept.setDataArea(dataArea);// 生成数据域
		dept.setIsParent(0);

		if (dept.saveOrUpdate()) {
			renderAjaxResultForSuccess("ok");
			DepartmentQuery.me().updateParents(dept);
		} else {
			renderAjaxResultForError("false");
		}
	}

	@Override
	public void delete() {
		String id = getPara("id");
		final Department r = DepartmentQuery.me().findById(id);
		if (r != null) {
			List<String> ids = new ArrayList<>();
			ids.add(id);
			if (r.getIsParent() > 0) {
				List<Department> deptList = DepartmentQuery.me().findByParentId(id);
				for (Department department : deptList) {
					ids.add(department.getId());
				}
			}
			int count = DepartmentQuery.me().batchDelete(ids);
			if (count > 0) {
				renderAjaxResultForSuccess("删除成功");
			} else {
				renderAjaxResultForError("删除失败");
			}
		}
		DepartmentQuery.me().updateParents(r);
	}

	public void department_tree() {
		List<Map<String, Object>> list = DepartmentQuery.me().findDeptListAsTree(1);
		setAttr("treeData", JSON.toJSON(list));
	}

}
