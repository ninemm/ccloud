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

import org.ccloud.model.UserJoinCustomer;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class UserJoinCustomerQuery extends JBaseQuery {

	protected static final UserJoinCustomer DAO = new UserJoinCustomer();
	private static final UserJoinCustomerQuery QUERY = new UserJoinCustomerQuery();

	public static UserJoinCustomerQuery me() {
		return QUERY;
	}

	public List<Record> findUserListBySellerCustomerId(String sellerCustomerId, String dataArea) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder(" select c.user_id, i.realname ");
		sqlBuilder.append(" from `cc_user_join_customer` c ");
		sqlBuilder.append(" join `user` i on c.user_id = i.id ");
		sqlBuilder.append(" where c.seller_customer_id = ? ");
		params.add(sellerCustomerId);

		appendIfNotEmptyWithLike(sqlBuilder, "c.data_area", dataArea, params, false);

		return Db.find(sqlBuilder.toString(), params.toArray());
	}
	
	public List<Record> findCustomerTypeBySellerCustomerId(String sellerCustomerId, String dataArea){
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("select cct.id,cct.`code`,cct.`name` ");
		sqlBuilder.append("from cc_customer_join_customer_type cc inner join cc_customer_type cct on cc.customer_type_id = cct.id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder,"cc.seller_customer_id",sellerCustomerId,params,needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cct.data_area", dataArea, params, needWhere);
		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public UserJoinCustomer findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<UserJoinCustomer> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_user_join_customer` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int deleteBySelerCustomerId(String sellerCustomerId) {
		return DAO.doDelete("seller_customer_id = ?", sellerCustomerId);
	}
	
	public int deleteBySelerCustomerIdAndUserId(String sellerCustomerId, String userId) {
		return DAO.doDelete("seller_customer_id = ? AND user_id = ?", sellerCustomerId,userId);
	}

	public boolean insert(String customerId, String userId, String deptId, String dataArea) {
		UserJoinCustomer userJoinCustomer = new UserJoinCustomer();
		userJoinCustomer.set("seller_customer_id", customerId);
		userJoinCustomer.set("user_id", userId);
		userJoinCustomer.set("dept_id", deptId);
		userJoinCustomer.set("data_area", dataArea);

		return userJoinCustomer.save();
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
	
	public long customerCount(String dataArea,boolean newCustomer) {
//		StringBuilder fromBuilder = new StringBuilder("select count(seller_customer_id) from cc_user_join_customer ");
//		fromBuilder.append("where data_area like '"+dataArea+"'");
		StringBuilder fromBuilder = new StringBuilder("SELECT COUNT(*) ");
		fromBuilder.append(" FROM ( SELECT count(*) FROM cc_seller_customer csc INNER JOIN cc_user_join_customer cjc ON csc.id = cjc.seller_customer_id ");
		fromBuilder.append(" where cjc.data_area like '"+dataArea+"' ");
		if(newCustomer) {
			fromBuilder.append(" and DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(csc.create_date)");
		}
		fromBuilder.append(" GROUP BY csc.customer_id ) t1");
		return Db.queryLong(fromBuilder.toString());
	}
}
