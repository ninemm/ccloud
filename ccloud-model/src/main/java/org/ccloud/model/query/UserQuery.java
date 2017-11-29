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

import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.User;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class UserQuery extends JBaseQuery { 

	protected static final User DAO = new User();
	private static final UserQuery QUERY = new UserQuery();

	public static UserQuery me() {
		return QUERY;
	}

	public User findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<User> paginate(int pageNumber, int pageSize, String keyword, String dataArea, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder(" from user u ");

		LinkedList<Object> params = new LinkedList<Object>();
		fromBuilder.append("WHERE LOCATE('" + dataArea + "' ,u.data_area) = 1 ");
		appendIfNotEmptyWithLike(fromBuilder, "username", keyword, params, false);
		
		fromBuilder.append("order by " + orderby);
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (ids[i].equals("0")) {
					continue;
				}
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public User findUserByUsername(final String username) {
		return DAO.getCache(username, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("username = ? AND status = 1", username);
			}
		});
	}

	public List<User> findByDeptId(String deptId) {
		return DAO.doFind("department_id = ?", deptId);
	}

	public List<User> findByGroupId(String id) {
		return DAO.doFind("group_id = ?", id);
	}

	public List<User> findByRoleId(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select * ");
		sqlBuilder.append("from `user` u ");
		sqlBuilder.append("where u.group_id in ");
		sqlBuilder.append("(SELECT gr.group_id FROM group_role_rel gr where gr.role_id= ?)");
		return DAO.find(sqlBuilder.toString(), id);
	}

	public List<User> findByStation(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select * ");

		sqlBuilder.append("from `user` u ");
		final List<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(sqlBuilder, "u.station_id", id, params, true);
		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		}
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<User> findByDeptDataArea(String dataArea) {
		StringBuilder sqlBuilder = new StringBuilder("select * ");

		sqlBuilder.append("from `user` u ");
		final List<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(sqlBuilder, "u.data_area", dataArea, params, true);
		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		}
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<User> findByRoleIds(String[] ids) {
		StringBuilder sqlBuilder = new StringBuilder("select * ");
		LinkedList<Object> params = new LinkedList<Object>();
		sqlBuilder.append("from `user` u ");
		sqlBuilder.append("where u.group_id in ");
		sqlBuilder.append("(SELECT gr.group_id FROM group_role_rel gr ");
		if (ids.length > 0) {
			sqlBuilder.append("where gr.role_id in (?");
			params.add(ids[0]);
			for (int i = 1; i < ids.length; i++) {
				sqlBuilder.append(",?");
				params.add(ids[i]);
			}

			sqlBuilder.append(") ");
		}
		sqlBuilder.append(") ");
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<User> findByGroupIds(String[] ids) {
		StringBuilder sqlBuilder = new StringBuilder("select * ");
		LinkedList<Object> params = new LinkedList<Object>();
		sqlBuilder.append("from `user` u ");
		if (ids.length > 0) {
			sqlBuilder.append("where u.group_id in (?");
			params.add(ids[0]);
			for (int i = 1; i < ids.length; i++) {
				sqlBuilder.append(",?");
				params.add(ids[i]);
			}

			sqlBuilder.append(") ");
		}
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<User> findByRole(String id) {
		StringBuilder sql = new StringBuilder("group_id in ");
		sql.append("(SELECT gr.group_id FROM group_role_rel gr ");
		sql.append("LEFT JOIN `group` g ON gr.group_id = g.id WHERE gr.role_id=?)");
		return DAO.doFind(sql.toString(), id);
	}
	
	public List<User> findUserList(String userId) {
		StringBuilder sql = new StringBuilder(" department_id=(select department_id FROM `user` WHERE id =?)");
		return DAO.doFind(sql.toString(), userId);
	}


	public List<User> findUserList(String userId) {
		StringBuilder sql = new StringBuilder(" department_id=(select department_id FROM `user` WHERE id =?)");
		return DAO.doFind(sql.toString(), userId);
	}

}
