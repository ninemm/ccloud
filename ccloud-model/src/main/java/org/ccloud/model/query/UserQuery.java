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

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import org.ccloud.Consts;
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

	public Page<User> paginate(int pageNumber, int pageSize, String keyword, String dataArea, String orderby, String userId) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder(" from user u ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "username", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "data_area", dataArea, params, needWhere);
		if (userId != null) {
			fromBuilder.append("AND u.id != ? ");
			params.add(userId);
		}
		
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
	
	public List<User> findByWechatOpenid(final String openid) {
		return DAO.doFind("wechat_open_id = ? AND status = 1", openid);
//		return DAO.doFindByCache(User.CACHE_NAME, openid, "wechat_open_id = ? AND status = 1", openid);
	}
	
	public List<User> findByMobile(final String mobile) {
		return DAO.doFindByCache(User.CACHE_NAME, mobile, "mobile = ? AND status = 1", mobile);
	}
	
	public List<User> findByMobileOrWechatOpenid(String value) {
		return DAO.doFindByCache(User.CACHE_NAME, value, "(mobile = ? OR wechat_open_id = ?) AND status = 1", value, value);
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
		StringBuilder sql = new StringBuilder("SELECT u.* FROM user_group_rel a ");
		sql.append("LEFT JOIN `user` u ON a.user_id = u.id ");
		sql.append("where a.group_id in (SELECT b.group_id from group_role_rel b where b.role_id = ?) ");
		return DAO.find(sql.toString(), id);
	}
	
	public List<User> findUserList(String userId) {
		StringBuilder sql = new StringBuilder(" department_id=(select department_id FROM `user` WHERE id =?)");
		return DAO.doFind(sql.toString(), userId);
	}

	public List<Record> findNextLevelsUserList(String dataArea) {

		StringBuilder sql = new StringBuilder("SELECT id, realname ");
		sql.append("FROM user ");
		final List<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(sql, "data_area", dataArea, params, true);

		return Db.find(sql.toString(), params.toArray() );
	}
	
	public User findManagerByDeptId(String deptId) {
		String sql = "select u.* from user u left join department d on u.id = d.principal_user_id where d.id = ?";
		return DAO.findFirst(sql, deptId);
	}	
	
	public List<Record> findByUserCheck(String id, String dataArea) {
		StringBuilder stringBuilder = new StringBuilder("SELECT u.id,u.realname,a.id as check_status FROM user u LEFT JOIN ");
		stringBuilder.append("(SELECT * FROM user_group_rel gr WHERE gr.group_id = ?) a ");
		stringBuilder.append("ON u.id = a.user_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(id);
		appendIfNotEmptyWithLike(stringBuilder, "u.data_area", dataArea, params, false);
		return Db.find(stringBuilder.toString(), params.toArray());
	}
	
	public List<Record> findBydeptAndGroup(String dataArea, String id) {
		String data = dataArea + "%";
 		StringBuilder fromBuilder = new StringBuilder("select * ");
		fromBuilder.append("from user u ");
		fromBuilder.append("left join (SELECT gr.user_id FROM user_group_rel gr WHERE gr.group_id = ?) b ");
		fromBuilder.append("on u.id = b.user_id ");
		fromBuilder.append("where u.data_area like ?");
		List<Record> list = Db.find(fromBuilder.toString(), id, data);
		return list;
	}
	
	public List<User> findIdAndNameByDataArea(String dataArea) {
		StringBuilder sqlBuilder = new StringBuilder("select u.id,u.realname as text ");

		sqlBuilder.append("from `user` u ");
		final List<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(sqlBuilder, "u.data_area", dataArea, params, true);
		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		}
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}	
	
	public List<Record> getCustomerInfoByUserId(String userId, String dataArea){
		String data = dataArea + "%";
		StringBuilder fromBuilder = new StringBuilder("SELECT c.id, c.customer_code AS code, c.customer_name AS name, c.contact, c.mobile ");
		fromBuilder.append("FROM `cc_user_join_customer` AS uc LEFT JOIN `cc_customer` AS c ON uc.seller_customer_id = c.id ");
		fromBuilder.append("WHERE c.id != '' AND c.is_enabled = 1 AND uc.user_id = ? AND uc.data_area like ?");
		
		List<Record> list = Db.find(fromBuilder.toString(), userId, data);
		return list;
	}
	
	public List<String> findUserIdsByDeptDataArea(String dataArea) {

		StringBuilder fromBuilder = new StringBuilder(" SELECT u.id ");
		fromBuilder.append(" FROM department d ");
		fromBuilder.append(" JOIN `user` u ON d.id = u.department_id ");
		fromBuilder.append(" WHERE d.data_area = ? ");

		return Db.query(fromBuilder.toString(), dataArea);
	}

	//查询该人下的所有业务员
	public List<User> findByDataAreaSalesman(String dataArea) {
		StringBuilder fromBuilder = new StringBuilder("SELECT u.id,u.realname ");
		fromBuilder.append(" FROM `user` u  ");
		fromBuilder.append(" LEFT JOIN user_group_rel ugr ON ugr.user_id=u.id ");
		fromBuilder.append(" LEFT JOIN `group` g ON ugr.group_id=g.id ");
		fromBuilder.append(" LEFT JOIN group_role_rel grr ON grr.group_id=g.id ");
		fromBuilder.append(" LEFT JOIN role r ON r.id=grr.role_id ");
		fromBuilder.append(" WHERE u.data_area LIKE '"+dataArea+"'  and r.role_code='"+Consts.ROLE_CODE_010+"'");
		return DAO.find(fromBuilder.toString());
	}

	public Page<User> paginateUser(int pageNumber, int pageSize, String keyword, String dataArea, String orderby,
			String userId) {
		String select = "select u.id,u.username,u.realname,u.nickname,u.mobile,u.department_name,u.station_name,u.status,group_concat(g.group_name) group_name";
		StringBuilder fromBuilder = new StringBuilder(" from user u ");
		fromBuilder.append(" LEFT JOIN user_group_rel ugr ON ugr.user_id=u.id ");
		fromBuilder.append(" LEFT JOIN `group` g ON g.id=ugr.group_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "u.username", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "u.data_area", dataArea, params, needWhere);
		if (userId != null) {
			fromBuilder.append("AND u.id != ? ");
			params.add(userId);
		}
		
		fromBuilder.append("GROUP BY u.id order by " + orderby);
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
}
