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

import org.ccloud.model.Seller;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerQuery extends JBaseQuery { 

	protected static final Seller DAO = new Seller();
	private static final SellerQuery QUERY = new SellerQuery();

	public static SellerQuery me() {
		return QUERY;
	}

	public Seller findById(final String id) {
		
		StringBuilder sqlBuilder = new StringBuilder("select cc.*, d.dept_name as parent_name ");
		sqlBuilder.append("from `cc_seller` cc ");
		sqlBuilder.append("join `department` d on d.id = cc.dept_id ");
		sqlBuilder.append("where cc.id = ?");
		return DAO.findFirst(sqlBuilder.toString(), id);
		//return DAO.findById(id);
	}
	
	public List<Seller> findByDeptId(String id){
		String sql="SELECT cs.* from cc_seller cs LEFT JOIN  user u on u.department_id=cs.dept_id where u.id= ?";
		return DAO.find(sql,id);
	}

	public int deleteByCcSellerId(String sellerId) {
		return DAO.doDelete("id = ?", sellerId);
	}
	
	public Page<Seller> paginate(int pageNumber, int pageSize,String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller` ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "seller_name", keyword, params, true);
		
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

	public List<Seller> querySellIdByDept(String departmentId) {
		return DAO.doFind("dept_id = ? and is_enabled = 1", departmentId);
	}
	
	public List<Seller> findSellerListByUser(String userId) {
		String sql = "select cs.* from cc_seller cs LEFT JOIN `user` u on u.department_id = cs.dept_id "
				+ "WHERE u.id =? and cs.seller_type=1";

		return DAO.find(sql, userId);
	}
	
}
