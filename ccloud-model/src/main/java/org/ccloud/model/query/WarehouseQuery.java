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

import org.ccloud.model.Warehouse;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class WarehouseQuery extends JBaseQuery { 

	protected static final Warehouse DAO = new Warehouse();
	private static final WarehouseQuery QUERY = new WarehouseQuery();

	public static WarehouseQuery me() {
		return QUERY;
	}

	public Warehouse findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	public Page<Warehouse> paginate(int pageNumber, int pageSize,String keyword, String orderby, String user_id) {
		String select = "select c.id,c.name,c.code,c.contact,c.phone,c.is_inited,c.is_enabled,c.is_default,d.dept_name as deptName";
		StringBuilder fromBuilder = new StringBuilder("from `cc_warehouse` c, cc_user_join_warehouse uw,department d where ");
        fromBuilder.append("c.dept_id = d.id and c.id =uw.warehouse_id and uw.user_id='"+user_id+"'");
		LinkedList<Object> params = new LinkedList<Object>();

		appendIfNotEmptyWithLike(fromBuilder, "c.name", keyword, params, true);
		
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

	public List<Warehouse> findEnable() {
		return DAO.doFind("is_enable = 1");
	}
	
	public List<Warehouse> findAll() {
		return DAO.doFind();
	}

	public List<Warehouse> findWareHouseByDept(String id) {
		return DAO.doFind("dept_id = ?", id);
	}
	
	public List<Warehouse> findByUserId(String userId){
		String sql = "select cw.* from cc_warehouse cw LEFT JOIN user u on u.department_id=cw.dept_id where u.id=?";
		return DAO.find(sql, userId);
	}

	public int deleteWarehouseId(String warehouse_id) {
		return DAO.doDelete("id=?",warehouse_id);
	}

	public List<Warehouse> findWarehouseByUserId(String userId) {
		String sql = "select w.* from  cc_warehouse w,cc_user_join_warehouse uw where w.id =uw.warehouse_id and uw.user_id=? and w.is_enabled=1";
		return DAO.find(sql, userId);
	}

	public List<Warehouse> findIsDefault(String id) {
		String sql = "select * from  cc_warehouse w , cc_user_join_warehouse uw where uw.warehouse_id=w.id and w.is_default=1 and uw.user_id=?";
		return DAO.find(sql,id);
	}
	
	public Warehouse findOneByUserId(String userId){
		String sql = "SELECT cw.* from cc_warehouse cw LEFT JOIN `user` u on u.department_id = cw.dept_id where cw.is_default=1 and u.id=? ";
		return DAO.findFirst(sql, userId);
	}
 }
