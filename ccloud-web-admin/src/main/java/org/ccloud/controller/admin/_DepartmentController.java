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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Department;
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.StationQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
//import org.ccloud.wwechat.WorkWechatContactApiConfigInterceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.qyweixin.sdk.api.ApiResult;
import com.jfinal.qyweixin.sdk.api.ConDepartmentApi;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/department", viewPath = "/WEB-INF/admin/department")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/department","/admin/all"},logical=Logical.OR)
public class _DepartmentController extends JBaseCRUDController<Department> {
	
	@Override
	public void index() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		List<Map<String, Object>> list = DepartmentQuery.me().findDeptListAsTree(1, dataArea, isSuperAdmin);
		setAttr("treeData", JSON.toJSON(list));
		render("index.html");
	}
	
	public void list() {

		String keyword = getPara("k", "");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		String parentId = getPara("parentId", "0");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Department> page = DepartmentQuery.me().paginate(getPageNumber(), getPageSize(), parentId, keyword, dataArea, null);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@Override
	@RequiresPermissions(value={"/admin/department/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			List<User> orderReviewers = UserQuery.me().findOrderReviewerByDeptId(id);
			String orderReviewUserName = "";
			for (User u : orderReviewers) {
				if (StrKit.notBlank(orderReviewUserName)) {
					orderReviewUserName = orderReviewUserName + ",";
				}

				orderReviewUserName += u.getStr("realname");
			}

			List<User> managers = UserQuery.me().findManagerByDeptId(id);
			String managerUserName = "";
			for (User u : managers) {
				if (StrKit.notBlank(managerUserName)) {
					managerUserName = managerUserName + ",";
				}

				managerUserName += u.getStr("realname");
			}
			setAttr("dept", DepartmentQuery.me().findById(id));
			setAttr("order_reviewer_id_name", orderReviewUserName);
			setAttr("principal_user_id_name", managerUserName);
		}

	}

	@Override
	public void save() {

		final Department dept = getModel(Department.class);
		if (!StringUtils.isNotBlank(dept.getId())) {
			String parentDataArea = getPara("parentDataArea");
			String dataArea = null;
			List<Department> list = DepartmentQuery.me().findByParentId(dept.getParentId());
			if (list.size() > 0) {
				dataArea = "00" + String.valueOf(new BigDecimal(list.get(0).getDataArea()).add(new BigDecimal(1)));
			} else {
				dataArea = parentDataArea + "001";
			}
			dept.setDataArea(dataArea);// 生成数据域	
		}
		if (StringUtils.isBlank(dept.getId())) {
			dept.setIsParent(0);
		}

		if (dept.saveOrUpdate()) {
			renderAjaxResultForSuccess("ok");
			DepartmentQuery.me().updateParents(dept);
		} else {
			renderAjaxResultForError("false");
		}
	}

	@Override
	@RequiresPermissions(value={"/admin/department/edit","/admin/all"},logical=Logical.OR)
	@Before(Tx.class)
	public void delete() {
		String id = getPara("id");
		final Department r = DepartmentQuery.me().findById(id);
		List<User> list = UserQuery.me().findByDeptDataArea(r.getDataArea() + "%");
		if (list.size() > 0) {
			renderAjaxResultForError("已有用户处于部门下或删除失败");
			return;
		}
		if (r != null) {
			List<String> ids = new ArrayList<>();
			this.deleteChild(r, ids);
			int count = DepartmentQuery.me().batchDelete(ids);
			if (count > 0) {
				renderAjaxResultForSuccess("删除成功");
			} else {
				renderAjaxResultForError("删除失败");
			}
		}
		DepartmentQuery.me().updateParents(r);
	}
	
	private void deleteChild(Department department, List<String> ids) {
		ids.add(department.getId());
		if (department.getIsParent() > 0) {
			List<Department> list = DepartmentQuery.me().findByParentId(department.getId());
			for (Department child : list) {
				this.deleteChild(child, ids);
			}
		}
	}	

	public void department_tree() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		List<Map<String, Object>> list = DepartmentQuery.me().findDeptListAsTree(1, dataArea, isSuperAdmin);
		setAttr("treeData", JSON.toJSON(list));
	}

	public void user_tree() {

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Map<String, Object>> treeData = DepartmentQuery.me().findDeptListAsTree(dataArea, true);
		setAttr("treeData", JSON.toJSON(treeData));

	}
	
	public void getAboutInfo() {
		String id = getPara("id");
		List<Map<String, Object>> stationList = StationQuery.me().findUserListByStation(id);//岗位树
		List<Map<String, Object>> roleList = DepartmentQuery.me().findUserListByRole(id);//角色树
		List<Map<String, Object>> wareHouseList = DepartmentQuery.me().findWareHouse(id);//仓库树
		List<Map<String, Object>> userList = DepartmentQuery.me().findUserTree(id);//仓库树
		List<Map<String, Object>> sellerList = DepartmentQuery.me().findSeller(id);//账套树
		List<Map<String, Object>> groupList = DepartmentQuery.me().findGroup(id);//分组树
		List<Map<String, Object>> customTypeList = DepartmentQuery.me().findCustomType(id);//客户类型树
		Map<String,Object> map = new HashMap<>();
		map.put("stationList", stationList);
		map.put("roleList", roleList);
		map.put("wareHouseList", wareHouseList);
		map.put("userList", userList);
		map.put("sellerList", sellerList);
		map.put("groupList", groupList);
		map.put("customTypeList", customTypeList);		
		renderJson(map);
	}
	
	public void getCustomer() {
		String id = getPara("id");
		List<Map<String, Object>> customerList = DepartmentQuery.me().findCustomer(id);//客户树
		renderJson(customerList);	
	}
	
	public void organizationSyn() {
		List<Record>list = DepartmentQuery.me().findSellerName();
		setAttr("list", list);
		render("department_sync.html");
	}
		
	public void findByDataArea() {
		String dataArea = getPara("dataArea");
		List<Record>list=new ArrayList<Record>();
		if (!StrKit.notBlank(dataArea)) {
			renderJson(list);	
			return;
		}
		list=DepartmentQuery.me().findByDataAreaSeller(dataArea);
		renderJson(list);	
	}
	
	//@Before(WorkWechatContactApiConfigInterceptor.class)
	public void synchronous() {
		ApiResult department = ConDepartmentApi.getDepartment("0");
		String sellerIds = getPara("sellerIds");
		String[] sellerId = sellerIds.split(",");
		for (String seller_id : sellerId) {
			//获取层级
			String deptLevel = DepartmentQuery.me().findDeptLevelBysellerId(seller_id);
			Seller seller = SellerQuery.me().findById(seller_id);
			int pid;
			if (deptLevel.equals("1")) {
				pid=Integer.parseInt(OptionQuery.me().findValue("qywechat_default_deptid"));
			}else {
				//查找一级经销商的企业微信id
				String parentid = DepartmentQuery.me().findParentidBysellerId(seller_id);
				if (StringUtils.isNotBlank(parentid)) {
					pid=Integer.parseInt(parentid);
				}else {
					renderAjaxResultForError("父部门没有同步");
					return;
				}
			}
			String json="{\"name\": \""+seller.getSellerName()+"\",\"parentid\": "+pid+"}";
			ApiResult createDepartment = ConDepartmentApi.createDepartment(json);
			if (createDepartment.getInt("errcode")!=0) {
				renderAjaxResultForError("同步失败");
				return;
			}
			seller.setJpwxOpenId(createDepartment.getInt("id").toString());
			seller.update();
		}
		renderAjaxResultForSuccess("同步成功");
	}
	
}
