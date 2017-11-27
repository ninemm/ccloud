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
import java.util.Map;

import org.ccloud.model.Customer;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerQuery extends JBaseQuery {

	protected static final Customer DAO = new Customer();
	private static final CustomerQuery QUERY = new CustomerQuery();

	public static CustomerQuery me() {
		return QUERY;
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, Map<String, String[]> paraMap,
			String deptId, String dataArea) {

		String select = "select c.id, c.customer_code, c.customer_name, c.contact, c.mobile, c.customer_kind, c.is_enabled, c.is_archive, "
				+ " c.prov_name, c.city_name, c.country_name, c.prov_code, c.city_code, c.country_code, c.address, t1.customerTypeNames, t2.realnames";
		StringBuilder fromBuilder = new StringBuilder("from `cc_customer` c ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		fromBuilder.append(" LEFT JOIN ( SELECT c1.id, GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		fromBuilder.append(
				" FROM cc_customer c1 LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.customer_id ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		needWhere = appendIfNotEmpty(fromBuilder, "cjct.customer_type_id",
				StringUtils.getArrayFirst(paraMap.get("customerTypeId")), params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "ct.dept_id", deptId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "ct.data_area", dataArea, params, needWhere);
		fromBuilder.append(" GROUP BY c1.id ");
		fromBuilder.append(" ) t1 ON c.id = t1.id");

		needWhere = true;
		if (StrKit.isBlank(deptId)) {
			fromBuilder.append(" LEFT JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		} else {
			fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		}
		fromBuilder.append(" FROM cc_customer c2 JOIN cc_user_join_customer ujc ON c2.id = ujc.customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");
		needWhere = appendIfNotEmpty(fromBuilder, "ujc.dept_id", deptId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, needWhere);
		fromBuilder.append(" GROUP BY c2.id ");
		fromBuilder.append(" ) t2 ON c.id = t2.id ");

		needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", keyword, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.customer_kind",
				StringUtils.getArrayFirst(paraMap.get("customerKind")), params, needWhere);

		needWhere = appendIfNotEmpty(fromBuilder, "c.is_enabled", StringUtils.getArrayFirst(paraMap.get("isEnabled")),
				params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.is_archive", StringUtils.getArrayFirst(paraMap.get("isArchive")),
				params, needWhere);

		fromBuilder.append(" GROUP BY c.id ");
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
		return DAO.findById(id);
	}

	public Integer findByNameAndMobile(String name, String mobile) {
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		StringBuilder sqlBuilder = new StringBuilder(" select count(1) ");
		sqlBuilder.append(" from `cc_customer` c ");
		needWhere = appendIfNotEmpty(sqlBuilder, "c.customer_name", name, params, needWhere);
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
		StringBuilder sql = new StringBuilder("SELECT cc.* FROM cc_customer cc ");
		sql.append("RIGHT JOIN cc_user_join_customer a ON cc.id = a.customer_id ");
		sql.append("WHERE a.user_id= ? ");		
		return DAO.find(sql.toString(), id);
	}

}
