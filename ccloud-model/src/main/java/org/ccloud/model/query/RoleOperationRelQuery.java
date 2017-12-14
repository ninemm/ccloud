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

import org.ccloud.model.RoleOperationRel;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class RoleOperationRelQuery extends JBaseQuery { 

	protected static final RoleOperationRel DAO = new RoleOperationRel();
	private static final RoleOperationRelQuery QUERY = new RoleOperationRelQuery();

	public static RoleOperationRelQuery me() {
		return QUERY;
	}

	public RoleOperationRel findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<RoleOperationRel> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `role_operation_rel` ");

		LinkedList<Object> params = new LinkedList<Object>();

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

	public int deleteByOperationId(String operationId) {
		return DAO.doDelete(" operation_id = ?", operationId);
	}

	public List<String> findUrlByRoleId(final String[] string) {
//		return DAO.getFromListCache(string, new IDataLoader() {
//			@Override
//			public Object load() {
				List<String> list = new ArrayList<>();
				LinkedList<Object> params = new LinkedList<Object>();
				StringBuilder fromBuilder = new StringBuilder("select o.url from ");
				fromBuilder.append("`role_operation_rel` r ");
				fromBuilder.append("left join `operation` o ");
				fromBuilder.append("on r.operation_id = o.id ");
				if (string.length > 0) {
					fromBuilder.append("where r.role_id in (?");
					params.add(string[0]);
					for (int i = 1; i < string.length; i++) {
						fromBuilder.append(",?");
						params.add(string[i]);
					}

					fromBuilder.append(") ");
				}				
				List<Record> records = Db.find(fromBuilder.toString(), params.toArray());
				for (Record record : records) {
					list.add(record.getStr("url"));
				}
				return list;
//			}
//		});		
	}

	public int delete(String roleId, String operationId) {
		StringBuilder sql = new StringBuilder(" role_id = ? and operation_id = ? ");
		return DAO.doDelete(sql.toString(), roleId, operationId);
	}
	
	public List<RoleOperationRel> findByRoleId(String roleId){
		return DAO.doFind(" role_id = ?", roleId);
	}

}
