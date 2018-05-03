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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Department;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinWarehouse;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserJoinWarehouseQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/warehouse", viewPath = "/WEB-INF/admin/warehouse")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _WarehouseController extends JBaseCRUDController<Warehouse> { 

	public void list() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		Page<Warehouse> page=new  Page<Warehouse>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String user_id = user.getId();
		//判断登录的人是不是经销商管理员
		if (isSuperAdmin) {
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			page = WarehouseQuery.me().paginateDataArea(getPageNumber(), getPageSize(), keyword, "c.create_date", dataArea,user_id);
		}else {
			page = WarehouseQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "c.create_date", user_id);
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//删除仓库  删除UserJoinWarehouse
	/*public void delete() {
		Db.tx(new IAtom() {
		    @Override
		    public boolean run() throws SQLException {
		    		String warehouse_id = getPara("id");
				int deleteWarehouse = WarehouseQuery.me().deleteWarehouseId(warehouse_id);
				if (!(deleteWarehouse>0)) {
		    			renderAjaxResultForError("删除失败!");
		    			return false;
				}
		    		int deleteUserJoinWarehouse = UserJoinWarehouseQuery.me().deleteWarehouseId(warehouse_id);
		    		if (!(deleteUserJoinWarehouse>0)) {
		    			renderAjaxResultForError("删除失败!");
		    			return false;
				}
		    		renderAjaxResultForSuccess("删除成功");
		        return true;                	
		    }
		});
	}*/
	
	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		String[] ids = getParaValues("dataItem");
		int count = WarehouseQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
	}
	
	public void enable() {
		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");
		Warehouse warehouse = WarehouseQuery.me().findById(id);
		warehouse.setIsEnabled(isEnabled);
		if (warehouse.saveOrUpdate()) {
			renderAjaxResultForSuccess("更新成功");
		} else {
			renderAjaxResultForError("更新失败");
		}

	}

	//保存仓库
	@Override
	public void save() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Warehouse warehouse = getModel(Warehouse.class); 
		String seller_id=getSessionAttr("sellerId").toString();
		//判断仓库是否有默认仓库
		if (warehouse.getIsDefault()==1) {
			List<Warehouse> list = WarehouseQuery.me().findIsDefault(user.getId(),seller_id);
			if (list.size()!=0&&!list.get(0).getId().equals(warehouse.getId())) {
				renderAjaxResultForError("请求错误,已有默认仓库");
				return;
			}
		}
		warehouse.setDeptId(user.getDepartmentId());
		warehouse.setDataArea(user.getDataArea());
		warehouse.setSellerId(seller_id);
		if (StringUtils.isBlank(warehouse.getId())) {
			warehouse.setId(StrKit.getRandomUUID());
			warehouse.setCreateDate(new Date());
			warehouse.save();
			UserJoinWarehouse userJoinWarehouse=new UserJoinWarehouse();
			userJoinWarehouse.setWarehouseId(warehouse.getId());
			userJoinWarehouse.setUserId(user.getId());
			userJoinWarehouse.save();
			renderAjaxResultForSuccess("保存成功！");
		}else {
			warehouse.saveOrUpdate();
			renderAjaxResultForSuccess("修改成功！");
		}
	}
	
	public void user_tree() {
		String warehouse_id = getPara(0);
		List<UserJoinWarehouse> listUserJoinWarehouse = UserJoinWarehouseQuery.me().findByWarehouseId(warehouse_id);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Map<String, Object>> treeData = DepartmentQuery.me().findDeptListAsTree(dataArea, true,listUserJoinWarehouse);
		setAttr("treeData", JSON.toJSON(treeData));
		setAttr("id", warehouse_id);
	}
	
	
	//仓库授权
	public void adduserJoinWarehouse() {
		Db.tx(new IAtom() {
		    @Override
		    public boolean run() throws SQLException {
				String warehouse_id=getPara("id");
				String userIds = getPara("userIds");
				int i=UserJoinWarehouseQuery.me().deleteWarehouseId(warehouse_id);
				if (i==0) {
					renderAjaxResultForError("删除失败");
					return false;
				}
				UserJoinWarehouse userJoinWarehouse=new UserJoinWarehouse();
				userJoinWarehouse.setWarehouseId(warehouse_id);
				String[] userIdArray = userIds.split(",");
				for (String userId : userIdArray) {
					userJoinWarehouse.setUserId(userId);
					boolean save = userJoinWarehouse.save();
					if (!save) {
						renderAjaxResultForError("授权失败");
						return false;  
					}
				}
				renderAjaxResultForSuccess("授权成功！");
				return true;                	
		    }
		});
	}
	
	public void getSeller() {
		 String toWarehouseId = getPara("toWarehouseId");
		 List<Record> findBySeller = WarehouseQuery.me().findBySeller(toWarehouseId);
		 List<Map<String, Object>> list = new ArrayList<>();
		 for (Record record : findBySeller) {
			 if (record.getStr("department_id")!=null) {
				List<Department> Department = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(record.getStr("department_id"));
				Department dept = Department.get(0);
				Map<String, Object> map = new HashMap<>();
				boolean a=true;
				for (Map<String, Object> map2 : list) {
					if (map2.get("sellerId").equals(dept.getStr("seller_id"))) {
						a=false;
						break;
					}
				}
				if (a) {
					map.put("seller_name", dept.getStr("seller_name"));
					map.put("sellerId", dept.getStr("seller_id"));
					list.add(map);
				}
			 }
		}
		 renderJson(list);
	}
}