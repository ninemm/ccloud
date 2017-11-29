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
package org.ccloud.model.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Department;
import org.ccloud.model.Group;
import org.ccloud.model.ModelSorter;
import org.ccloud.model.Role;
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;

import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class DepartmentQuery extends JBaseQuery { 

	protected static final Department DAO = new Department();
	private static final DepartmentQuery QUERY = new DepartmentQuery();

	public static DepartmentQuery me() {
		return QUERY;
	}

	public Department findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				
				StringBuilder sqlBuilder = new StringBuilder("select d.*,a.dept_name as parent_name,b.realname as user_realname ");
				sqlBuilder.append("from `department` d  ");
				sqlBuilder.append("left join (select id,dept_name from department) a on d.parent_id = a.id ");
				sqlBuilder.append("left join `user` b on b.id = d.principal_user_id ");
				sqlBuilder.append("where d.id = ?");
				return DAO.findFirst(sqlBuilder.toString(), id);
			}
		});
	}
	
	public List<Department> findDeptList(String dataArea, String orderby) {
		final StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM department d ");
		sqlBuilder.append("where d.id <> '0' ");
		
		final List<Object> params = new LinkedList<Object>();
		sqlBuilder.append("AND LOCATE('" + dataArea + "' , d.data_area) = 1 ");
		buildOrderBy(orderby, sqlBuilder);
		String key = buildKey(dataArea, null, null, null, orderby);
		
		List<Department> data = DAO.getFromListCache(key, new IDataLoader() {
			@Override
			public Object load() {
				if (params.isEmpty()) {
					return DAO.find(sqlBuilder.toString());
				}
				return DAO.find(sqlBuilder.toString(), params.toArray());
			}
		});
		
		if (data == null)
			return null;
		return new ArrayList<Department>(data);
	}
	
	private String buildKey(String module, Object... params) {
		StringBuffer keyBuffer = new StringBuffer(module == null ? "" : "module:" + module);
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				keyBuffer.append("-p").append(i).append(":").append(params[i]);
			}
		}
		return keyBuffer.toString().replace(" ", "");
	}	

	public Page<Department> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select d.*,a.dept_name as parent_name,b.realname as user_realname ";
		StringBuilder fromBuilder = new StringBuilder("from `department` d ");
		fromBuilder.append("left join (select id,dept_name from department) a on d.parent_id = a.id ");
		fromBuilder.append("left join `user` b on b.id = d.principal_user_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "dept_name", keyword, params, true);
		
		fromBuilder.append("order by " + orderby);

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<Department> paginate(int pageNumber, int pageSize, String parentId, String keyword, String dataArea, String orderby) {
		
		String select = "select d.*,a.dept_name as parent_name,b.realname as user_realname ";
		
		StringBuilder fromBuilder = new StringBuilder("from `department` d ");
		fromBuilder.append("left join (select id,dept_name from department) a on d.parent_id = a.id ");
		fromBuilder.append("left join `user` b on b.id = d.principal_user_id ");

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(fromBuilder, "d.parent_id", parentId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "d.dept_name", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "d.data_area", dataArea, params, needWhere);
		
		buildOrderBy(orderby, fromBuilder);

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}
	
	public int batchDelete(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.size(); i++) {
				if (DAO.deleteById(ids.get(i))) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}
	
	public List<Map<String, Object>> findDeptListAsTree(String dataArea, boolean hasUser) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		List<Department> list = findDeptList(dataArea, "order_list asc");
		List<Map<String, Object>> deptTreeList = new ArrayList<Map<String, Object>>();
		ModelSorter.tree(list);
		
		map.put("text", "总部");// 父子表第一级名称,以后可以存储在字典表或字典类
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuild(list, hasUser));
		deptTreeList.add(map);
		
		return deptTreeList;
	}
	
	public List<Map<String, Object>> findDeptListAsTree(int i, String dataArea) {
		List<Department> list = findDeptList(dataArea, "order_list asc");
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		ModelSorter.tree(list);
		Map<String, Object> map = new HashMap<>();
		map.put("text", "总部");// 父子表第一级名称,以后可以存储在字典表或字典类
		ArrayList<String> newArrayList = Lists.newArrayList();
		if (dataArea.equals(Consts.DEPT_HQ_DATAAREA)) {
			newArrayList.add(Consts.DEPT_HQ_ID);
			newArrayList.add(Consts.DEPT_HQ_DATAAREA);
		}
		map.put("tags", newArrayList);
		map.put("nodes", doBuild(list)); 
		resTreeList.add(map);
		return resTreeList;
	}
	
	private List<Map<String, Object>> doBuild(List<Department> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for(Department dept : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", dept.getDeptName());
			ArrayList<String> newArrayList = Lists.newArrayList();
			newArrayList.add(dept.getId());
			newArrayList.add(dept.getDataArea());
			map.put("tags", newArrayList);
				resTreeList.add(map);
			
			if(dept.getChildList() != null && dept.getChildList().size() > 0) {
				map.put("nodes", doBuild(dept.getChildList()));
			}
		}
		return resTreeList;
	}
	
	private List<Map<String, Object>> doBuild(List<Department> list, boolean addUserFlg) {
		List<Map<String, Object>> resTreeList = new ArrayList<Map<String, Object>>();
		for (Department dept : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", dept.getDeptName());
			map.put("tags", Lists.newArrayList(dept.getId(), dept.getDataArea()));

			List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();

			if (dept.getChildList() != null && dept.getChildList().size() > 0) {
				childList = doBuild(dept.getChildList(), addUserFlg);
			}

			if (addUserFlg) {
				childList = addUser(dept.getId(), childList);
			}

			map.put("nodes", childList);

			resTreeList.add(map);
		}
		return resTreeList;
	}

	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		
		fromBuilder.append(" order by ");
		
		if (StrKit.isBlank(orderBy)) {
			fromBuilder.append("d.order_list,d.dept_level asc ");
			return ;
		}
		
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];
		
		fromBuilder.append("d.order_list,d.dept_level ");
		
		if (orderbyInfo.length == 1) {
			fromBuilder.append("desc");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}
	}

	public Integer childNumById(String parentId) {
		Integer num = DAO.doFindCount("parent_id = ?", parentId).intValue();
		return num;
		
	}

	public List<Department> findByParentId(String id) {
		return DAO.doFind("parent_id = ? ORDER BY data_area desc", id);
	}

	public void updateParents(Department dept) {
		if (dept != null && dept.getParentId() != "0") {
			Department parentDept = DepartmentQuery.me().findById(dept.getParentId());
			Integer childNum = DepartmentQuery.me().childNumById(dept.getParentId());
			if (childNum > 0) {
				if (parentDept.getIsParent() == 0) {
					parentDept.setIsParent(1);
					parentDept.update();
				}
			} else {
				if (parentDept.getIsParent() > 0) {
					parentDept.setIsParent(0);
					parentDept.update();
				}
			}
		}
	}
	
	
	public Department findBySellerId(String sellerId){
		String sql = "select d.* from department d left join cc_seller cs on cs.dept_id = d.id where cs.id =?";
		return DAO.findFirst(sql, sellerId);
	}

	public Department findByUserId(String userId){
		String sql = "select d.* from department d LEFT JOIN user u on u.department_id=d.id where u.id=?";
		return DAO.findFirst(sql, userId);
	}
	
	public List<Map<String, Object>> findUserListByRole(String id) {
		List<Role> list = RoleQuery.me().findByDeptId(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有角色";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildByUser(list));
		resTreeList.add(map);
		return resTreeList;
	}

	private List<Map<String, Object>> doBuildByUser(List<Role> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (Role role : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", role.getRoleName());
			map.put("tags", Lists.newArrayList(role.getId()));

			List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
			
			childList = addUser(role.getId(), childList);
			map.put("nodes", childList);

			resTreeList.add(map);
			
		}
		return resTreeList;
	}

	private List<Map<String, Object>> addUser(String id, List<Map<String, Object>> childList) {
		List<User> list = UserQuery.me().findByRole(id);
		for (User user : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", user.getRealname());
			map.put("tags", Lists.newArrayList(user.getId(), "user"));
			childList.add(map);

		}
		return childList;
	}

	public List<Map<String, Object>> findWareHouse(String id) {
		List<Warehouse> list = WarehouseQuery.me().findWareHouseByDept(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有仓库";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildByWareHouse(list));
		resTreeList.add(map);
		return resTreeList;		
	}

	private List<Map<String, Object>> doBuildByWareHouse(List<Warehouse> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (Warehouse ware : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", ware.getName());
			map.put("tags", Lists.newArrayList(ware.getId()));
			resTreeList.add(map);
		}
		return resTreeList;
	}

	public List<Map<String, Object>> findSeller(String id) {
		List<Seller> list = SellerQuery.me().querySellIdByDept(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有账套";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildBySeller(list));
		resTreeList.add(map);
		return resTreeList;	
	}

	private List<Map<String, Object>> doBuildBySeller(List<Seller> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (Seller seller : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", seller.getSellerName());
			map.put("tags", Lists.newArrayList(seller.getId()));
			resTreeList.add(map);
		}
		return resTreeList;
	}

	public List<Map<String, Object>> findGroup(String id) {
		List<Group> list = GroupQuery.me().findByDeptId(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有分组";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildGroup(list));
		resTreeList.add(map);
		return resTreeList;	
	}

	private List<Map<String, Object>> doBuildGroup(List<Group> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (Group group : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", group.getGroupName());
			map.put("tags", Lists.newArrayList(group.getId()));
			resTreeList.add(map);
		}
		return resTreeList;
	}

	public List<Map<String, Object>> findCustomType(String id) {
		List<CustomerType> list = CustomerTypeQuery.me().findByDept(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有客户类型";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildCustom(list));
		resTreeList.add(map);
		return resTreeList;	
	}

	private List<Map<String, Object>> doBuildCustom(List<CustomerType> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (CustomerType type : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", type.getName());
			map.put("tags", Lists.newArrayList(type.getId()));
			resTreeList.add(map);
		}
		return resTreeList;
	}

	public List<Map<String, Object>> findCustomer(String id) {
		List<Customer> list = CustomerQuery.me().findByUserId(id);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		String title = "所有客户";
		if (list.size() == 0) {
			title = "暂无数据";
		}		
		map.put("text", title);
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuildCustomer(list));
		resTreeList.add(map);
		return resTreeList;	
	}

	private List<Map<String, Object>> doBuildCustomer(List<Customer> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for (Customer customer : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", customer.getCustomerName());
			map.put("tags", Lists.newArrayList(customer.getId()));
			resTreeList.add(map);
		}
		return resTreeList;
	}
	
}
