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
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.Operation;
import org.ccloud.model.User;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class OperationQuery extends JBaseQuery { 

	protected static final Operation DAO = new Operation();
	private static final OperationQuery QUERY = new OperationQuery();

	public static OperationQuery me() {
		return QUERY;
	}

	public Operation findById(final String id) {
//		return DAO.getCache(id, new IDataLoader() {
//			@Override
//			public Object load() {
				return DAO.findById(id);
//			}
//		});
	}

	public Page<Operation> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `operation` o ");

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere=appendIfNotEmptyWithLike(fromBuilder, "o.operation_name", keyword, params, needWhere);
		
		fromBuilder.append("order by " + orderby);

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Operation> queryModuleAndOperation(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select m.id, m.module_name, s.name as sys_name, me.module_name as parent_name, GROUP_CONCAT(o.id) as operation_code, GROUP_CONCAT(o.operation_name) as operation_name, null as operationList ";

		StringBuilder fromBuilder = new StringBuilder("from `module` m ");
		fromBuilder.append("join `systems` s on s.id = m.system_id ");
		fromBuilder.append("join `module` me on me.id = m.parent_id ");
		fromBuilder.append("left join `operation` o on o.module_id = m.id ");
		fromBuilder.append("WHERE operation_code is not null ");

		boolean needWhere = false;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "m.module_name", keyword, params, needWhere);

		fromBuilder.append("GROUP BY m.id");

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		
		fromBuilder.append(" order by ");
		
		if (StrKit.isBlank(orderBy)) {
			fromBuilder.append("o.order_list asc ");
			return ;
		}
		
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];
		
		fromBuilder.append("o.order_list ");
		
		if (orderbyInfo.length == 1) {
			fromBuilder.append("desc");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}
	}

	public List<String> getPermissionsByRole(List<String> roles) {
		List<String> permission = new ArrayList<>();
		String[] roleIds = roles.toArray(new String[roles.size()]); 
		List<String> list = RoleOperationRelQuery.me().findUrlByRoleId(roleIds);
		permission.addAll(list);
		return permission;
	}

	public List<String> getPermissionsByStation(String stationId) {
		List<String> permission = new ArrayList<>();
		if (stationId != null) {
			String[] stationIds = stationId.split(",");
			List<String> list = StationOperationRelQuery.me().findUrlByStationId(stationIds);
			permission.addAll(list);
		}
		return permission;
	}
	
	public String findIdByUrl(String url) {
		return Db.queryStr("select id from operation where url = ?", url);
	}

	public List<Operation> findByModule(String id) {
		return DAO.doFind("module_id = ?", id);
	}

	public List<Record> queryStationOperation(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select m.id, m.module_name, s.name as sys_name, ");
		sqlBuilder.append("me.module_name as parent_name, GROUP_CONCAT(o.id) as operation_code, GROUP_CONCAT(o.operation_name) ");
		sqlBuilder.append("as operation_name,GROUP_CONCAT(IFNULL(b.station_id,'0')) as station_id ");
		sqlBuilder.append("from `module` m join `systems` s on s.id = m.system_id join `module` me on me.id = m.parent_id ");
		sqlBuilder.append("left join `operation` o on o.module_id = m.id ");
		sqlBuilder.append("left join (SELECT sr.station_id,sr.operation_id FROM station_operation_rel sr WHERE sr.station_id = ?) ");
		sqlBuilder.append("b ON b.operation_id = o.id ");
		sqlBuilder.append("WHERE operation_code is not null GROUP BY m.id ORDER BY sys_name, parent_name,module_name");
		return Db.find(sqlBuilder.toString(), id);
	}
	
	public List<Record> queryRoleOperation(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select m.id, m.module_name, s.name as sys_name, ");
		sqlBuilder.append("me.module_name as parent_name, GROUP_CONCAT(o.id) as operation_code, GROUP_CONCAT(o.operation_name) ");
		sqlBuilder.append("as operation_name,GROUP_CONCAT(IFNULL(b.role_id,'0')) as station_id ");
		sqlBuilder.append("from `module` m join `systems` s on s.id = m.system_id join `module` me on me.id = m.parent_id ");
		sqlBuilder.append("left join `operation` o on o.module_id = m.id ");
		sqlBuilder.append("left join (SELECT sr.role_id,sr.operation_id FROM role_operation_rel sr WHERE sr.role_id = ?) ");
		sqlBuilder.append("b ON b.operation_id = o.id ");
		sqlBuilder.append("WHERE operation_code is not null GROUP BY m.id ORDER BY sys_name, parent_name,module_name");
		return Db.find(sqlBuilder.toString(), id);
	}

	public List<Record> queryMenuOperation(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select m.id, m.module_name, s.name as sys_name, ");
		sqlBuilder.append("me.module_name as parent_name, GROUP_CONCAT(o.id) as operation_code, GROUP_CONCAT(o.operation_name) ");
		sqlBuilder.append("as operation_name,GROUP_CONCAT(IFNULL(b.id,'0')) as station_id ");
		sqlBuilder.append("from `module` m join `systems` s on s.id = m.system_id join `module` me on me.id = m.parent_id ");
		sqlBuilder.append("left join `operation` o on o.module_id = m.id ");
		sqlBuilder.append("left join (SELECT m1.id,m1.operator_id FROM menu m1 WHERE m1.id = ?) ");
		sqlBuilder.append("b ON b.operator_id = o.id ");
		sqlBuilder.append("WHERE operation_code is not null GROUP BY m.id ORDER BY sys_name, parent_name,module_name");
		return Db.find(sqlBuilder.toString(), id);
	}

	public List<String> getPermissionsByUser(final User user) {
		return DAO.getFromListCache(user.getId(), new IDataLoader() {
			@Override
			public Object load() {
				List<String> list = new ArrayList<>();
				LinkedList<Object> params = new LinkedList<Object>();
				StringBuilder fromBuilder = new StringBuilder("select o.url from `role_operation_rel` r left join `operation` o ");
				fromBuilder.append("on r.operation_id = o.id where r.role_id in ");
				fromBuilder.append("(SELECT gr.role_id FROM group_role_rel gr where gr.group_id = ?) ");
				fromBuilder.append("UNION ALL ");
				fromBuilder.append("select o.url from `station_operation_rel` r left join `operation` o ");
				fromBuilder.append("on r.operation_id = o.id ");
				fromBuilder.append("WHERE LOCATE((SELECT u.station_id FROM `user` u where u.id = ?),r.station_id) > 0");
				params.add(user.getGroupId());
				params.add(user.getId());
				List<Record> records = Db.find(fromBuilder.toString(), params.toArray());
				for (Record record : records) {
					list.add(record.getStr("url"));
				}
				return list;				
			}
		});			
	}	

}
