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

import org.ccloud.model.Group;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GroupQuery extends JBaseQuery { 

	protected static final Group DAO = new Group();
	private static final GroupQuery QUERY = new GroupQuery();

	public static GroupQuery me() {
		return QUERY;
	}

	public Group findById(final String id) {
		return DAO.findById(id);
	}

	public Page<Group> paginate(int pageNumber, int pageSize,String keyword, String dataArea, String orderby) {
		String select = "select g.*,d.dept_name ";
		StringBuilder fromBuilder = new StringBuilder("from `group` g INNER JOIN `department` d ON d.id = g.dept_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "g.group_name", keyword, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "g.data_area", dataArea, params, needWhere);

		fromBuilder.append("order by " + orderby);

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

    public List<Group> findAll() {
        return DAO.doFind();
    }

	public boolean delete(String id) {
		return DAO.deleteById(id);
	}

	public List<Group> findByDept(String dataArea, String userId) {
		StringBuilder sqlBuilder = new StringBuilder("select g.*, t1.user_id ");

		sqlBuilder.append("from `group` g ");
		sqlBuilder.append("LEFT JOIN (SELECT u.group_id,u.user_id FROM user_group_rel u where u.user_id = ?) t1 on t1.group_id = g.id ");
		final List<Object> params = new LinkedList<Object>();
		params.add(userId);
		sqlBuilder.append(" where g.data_area like '"+dataArea+"' ");
		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		}
		return DAO.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Group> _findByDeptId(String deptId) {
		String sql = "SELECT * from `group` where dept_id = '"+deptId+"' and SUBSTRING(group_code,5 )>SUBSTRING('role01',5) and SUBSTRING(group_code,5)<SUBSTRING('role21',5)";
		return DAO.find(sql);
	}
	
	public List<Record> findByUserCheck(String id, String dataArea) {
		StringBuilder stringBuilder = new StringBuilder("SELECT DISTINCT g.group_name,g.id,r.id as userRelId FROM `group` g ");
		stringBuilder.append("LEFT JOIN (SELECT * FROM `user_group_rel` r  where user_id=?) r ");
		stringBuilder.append("ON LOCATE(g.id,r.group_id) > 0 ");
		stringBuilder.append("WHERE g.id <> '0' ");
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(id);
		stringBuilder.append("and g.data_area like '"+dataArea+"' ");
		return Db.find(stringBuilder.toString(), params.toArray());
	}
	
	public Group findDeptIdAndDataAreaAndGroupCode(String deptId,String dataArea,String groupCode){
		String sql = "select * from `group` where dept_id = ? and data_area = ? and SUBSTRING(group_code,5 ) = ?";
		return DAO.findFirst(sql, deptId,dataArea,groupCode);
	}
	
	public List<Group> findByDeptId(String id) {
		return DAO.doFind("dept_id = ?", id);
	}
	
	public Group findDataAreaAndGroupName(String dataArea,String groupName){
		String sql = "select * from `group` where data_area = ? and group_name = ?";
		return DAO.findFirst(sql, dataArea,groupName);
	}
}
