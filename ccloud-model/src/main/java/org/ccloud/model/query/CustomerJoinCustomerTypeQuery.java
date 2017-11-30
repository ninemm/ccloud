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

import org.ccloud.model.CustomerJoinCustomerType;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerJoinCustomerTypeQuery extends JBaseQuery {

	protected static final CustomerJoinCustomerType DAO = new CustomerJoinCustomerType();
	private static final CustomerJoinCustomerTypeQuery QUERY = new CustomerJoinCustomerTypeQuery();

	public static CustomerJoinCustomerTypeQuery me() {
		return QUERY;
	}

	public List<Integer> findCustomerTypeListByCustomerId(String customerId, String dataArea) {
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		StringBuilder sqlBuilder = new StringBuilder("select cj.customer_type_id ");
		sqlBuilder.append(" from `cc_customer_join_customer_type` cj ");
		sqlBuilder.append(" join `cc_customer_type` c on cj.customer_type_id = c.id ");
		needWhere = appendIfNotEmpty(sqlBuilder, "cj.customer_id", customerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.data_area", dataArea, params, needWhere);

		return Db.query(sqlBuilder.toString(), params.toArray());
	}

	public CustomerJoinCustomerType findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<CustomerJoinCustomerType> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_customer_join_customer_type` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int deleteByCustomerId(String customerId) {
		return DAO.doDelete("customer_id = ?", customerId);
	}

	public boolean insert(String customerId, String customerType) {
		DAO.set("customer_id", customerId);
		DAO.set("customer_type_id", customerType);
		return DAO.save();
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
	
}
