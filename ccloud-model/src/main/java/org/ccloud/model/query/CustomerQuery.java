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

import org.ccloud.model.Customer;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerQuery extends JBaseQuery {

	protected static final Customer DAO = new Customer();
	private static final CustomerQuery QUERY = new CustomerQuery();

	public static CustomerQuery me() {
		return QUERY;
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword) {

		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_customer` c ");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needwhere = true;
		needwhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", keyword, params, needwhere);

		fromBuilder.append(" order by c.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> paginateByCorp(int pageNumber, int pageSize, String keyword, String corpSellerId) {

		String select = "select c.* ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_customer` c ");
		fromBuilder.append("LEFT JOIN `cc_customer_join_corp` ccjc ON c.id = ccjc.customer_id ");

		LinkedList<Object> params = new LinkedList<Object>();

		boolean needwhere = true;
		needwhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", keyword, params, needwhere);
		needwhere = appendIfNotEmpty(fromBuilder, "ccjc.seller_id", corpSellerId, params, needwhere);

		fromBuilder.append(" order by c.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public boolean enable(String id, int isEnabled) {
		Customer ccCustomer = DAO.findById(id);
		ccCustomer.set("is_enabled", isEnabled);

		return ccCustomer.saveOrUpdate();
	}

	public Customer findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public List<Record> findByCustomerName(String dataArea,String deptId,String customerName) {
		StringBuilder sqlBuilder = new StringBuilder("select cc_c.*,cc_s.nickname nickName,cc_s.customer_kind kind,cc_s.sub_type subType,cc_s.id sellercId,GROUP_CONCAT(u.id) principalIds,GROUP_CONCAT(u.realname) principal ");
		sqlBuilder.append("from cc_customer cc_c inner join cc_seller_customer cc_s on cc_c.id = cc_s.customer_id ");
		sqlBuilder.append("left join cc_user_join_customer cju on cju.seller_customer_id = cc_s.id left join `user` u on cju.user_id = u.id ");
		sqlBuilder.append("where cc_s.dept_id ='"+deptId+"' ");
		sqlBuilder.append("and cc_s.data_area like '"+dataArea+"%' ");
		sqlBuilder.append("and cc_c.customer_name like '%"+customerName+"%' ");
		sqlBuilder.append("GROUP BY cc_s.id");
		List<Record> list = Db.find(sqlBuilder.toString());
		return list;
	}


	public Customer findByCustomerNameAndMobile(String customerName, String mobile) {
		return DAO.doFindFirst("customer_name = ? and mobile = ?", customerName, mobile);
	}

	@Deprecated
	public Customer findByCustomerMobile(String mobile) {
		return DAO.doFindFirst("mobile = ?", mobile);
	}

	@Deprecated
	public Integer findByNameAndMobile(String name, String mobile) {
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		StringBuilder sqlBuilder = new StringBuilder(" select count(1) ");
		sqlBuilder.append(" from `cc_customer` c ");
		needWhere = appendIfNotEmpty(sqlBuilder, "c.customer_name", name, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.mobile", mobile, params, needWhere);

		return Db.queryInt(sqlBuilder.toString(), params.toArray());
	}

	public Integer findByMobile(String mobile) {
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		StringBuilder sqlBuilder = new StringBuilder(" select count(1) ");
		sqlBuilder.append(" from `cc_customer` c ");
		needWhere = appendIfNotEmpty(sqlBuilder, "c.mobile", mobile, params, needWhere);

		return Db.queryInt(sqlBuilder.toString(), params.toArray());
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

	public List<Customer> findByUserId(String id) {
/*原		StringBuilder sql = new StringBuilder("SELECT cc.* FROM cc_customer cc ");
		sql.append("RIGHT JOIN cc_user_join_customer a ON cc.id = a.customer_id ");
		sql.append("WHERE a.user_id= ? ");*/
		StringBuilder sql = new StringBuilder("select cc.* from cc_customer cc ");
		sql.append("right join cc_seller_customer csc on csc.customer_id = cc.id ");
		sql.append("inner join cc_user_join_customer a on csc.id = a.seller_customer_id ");
		sql.append("where user_id = ? ");
		return DAO.find(sql.toString(), id);
	}

	public List<Customer> getToDo(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT c.*, a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime");
		sb.append(" FROM cc_customer c");
		sb.append(" JOIN act_ru_task a on c.proc_inst_id = a.PROC_INST_ID_");
		sb.append(" JOIN act_ru_identitylink u on c.proc_inst_id = u.PROC_INST_ID_");
		sb.append(" where FIND_IN_SET(?, u.USER_ID_) ");
		return DAO.find(sb.toString(), username);
	}

	public List<Record> getCustomerIdName() {
		StringBuilder fromBuilder = new StringBuilder("SELECT id,customer_name AS text FROM cc_customer");
		List<Record> list = Db.find(fromBuilder.toString());
		return list;
	}
	
	public Customer findSellerCustomerId(String sellerCustomerId){
		String sql = "select c.* from cc_customer c LEFT JOIN cc_seller_customer s on s.customer_id = c.id where s.id = ?";
		return DAO.findFirst(sql, sellerCustomerId);
	}
}
