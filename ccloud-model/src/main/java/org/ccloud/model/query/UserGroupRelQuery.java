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

import org.ccloud.model.UserGroupRel;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class UserGroupRelQuery extends JBaseQuery { 

	protected static final UserGroupRel DAO = new UserGroupRel();
	private static final UserGroupRelQuery QUERY = new UserGroupRelQuery();

	public static UserGroupRelQuery me() {
		return QUERY;
	}

	public UserGroupRel findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<UserGroupRel> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `user_group_rel` ");

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
	
	public int deleteByGroupId(String groupId) {

        return DAO.doDelete(" group_id = ?", groupId);
    }

	public List<UserGroupRel> findByUserId(String id) {
		return DAO.doFind("user_id = ?", id);
	}
	
	
	public List<Record> findByUserIdAndGroupId(String id,String groupId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("select id from user_group_rel where user_id = ? and group_id =?");
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(id);
		params.add(groupId);
		return Db.find(stringBuilder.toString(), params.toArray());

	}
	
	public List<String> findUserIdsByGroup(String groupCode, String userId) {

		StringBuilder fromBuilder = new StringBuilder(" SELECT ugr.user_id ");
		fromBuilder.append(" FROM `user` u ");
		fromBuilder.append(" JOIN `user_group_rel` ugr ON u.id = ugr.user_id ");
		fromBuilder.append(" JOIN `group` g ON ugr.group_id = g.id ");
		fromBuilder.append(" AND LOCATE( ? , g.group_code) > 0 ");
		fromBuilder.append(" WHERE u.id = ? ");

		return Db.query(fromBuilder.toString(), groupCode, userId);
	}
	
	public List<String> findUserNamesByRoleCode(String groupCode, String roleCode, String userIds) {

		StringBuilder fromBuilder = new StringBuilder(" SELECT u.username ");
		fromBuilder.append(" FROM `user` u ");
		fromBuilder.append(" JOIN `user_group_rel` ugr ON u.id = ugr.user_id ");
		fromBuilder.append(" JOIN `group` g ON ugr.group_id = g.id ");
		fromBuilder.append(" AND LOCATE( ? , g.group_code) > 0 ");
		fromBuilder.append(" JOIN `group_role_rel` grr ON g.id = grr.group_id ");
		fromBuilder.append(" JOIN role r ON grr.role_id = r.id ");
		fromBuilder.append(" WHERE r.role_code = ? ");
		fromBuilder.append(" AND FIND_IN_SET(u.id, ?) ");

		return Db.query(fromBuilder.toString(), groupCode, roleCode, userIds);
	}
	
}
