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

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.ccloud.model.SalesRefundInstock;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesRefundInstockQuery extends JBaseQuery {

	protected static final SalesRefundInstock DAO = new SalesRefundInstock();
	private static final SalesRefundInstockQuery QUERY = new SalesRefundInstockQuery();

	public static SalesRefundInstockQuery me() {
		return QUERY;
	}

	public SalesRefundInstock findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Record findMoreById(final String id) {
		StringBuilder fromBuilder = new StringBuilder(
				" select o.*,c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, u.realname, u.mobile ");
		fromBuilder.append(" from `cc_sales_refund_instock` o ");
		fromBuilder.append(" left join cc_customer c on o.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join user u on o.biz_user_id = u.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate) {
		String select = "select r.*, c.customer_name ";
		StringBuilder fromBuilder = new StringBuilder(" from `cc_sales_refund_instock` r");
		fromBuilder.append(" join cc_customer c on r.customer_id = c.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "r.refund_sn", keyword, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and r.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and r.create_date <= ?");
			params.add(endDate);
		}

		fromBuilder.append(" order by r.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public boolean insert(Map<String, String[]> paraMap, String instockId, String instockSn, String sellerId,
			String userId, Date date, Object deptId, Object dataArea) {
		DAO.set("id", instockId);
		DAO.set("instock_sn", instockSn);
		DAO.set("warehouse_id", StringUtils.getArrayFirst(paraMap.get("warehouseId")));

		DAO.set("seller_id", sellerId);
		DAO.set("customer_id", StringUtils.getArrayFirst(paraMap.get("customerId")));
		DAO.set("customer_type_id", StringUtils.getArrayFirst(paraMap.get("customerType")));

		DAO.set("biz_user_id", StringUtils.getArrayFirst(paraMap.get("biz_user_id")));
		DAO.set("input_user_id", userId);
		DAO.set("status", 0);// 待退货

		DAO.set("total_reject_amount", StringUtils.getArrayFirst(paraMap.get("total")));

		DAO.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		DAO.set("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
		DAO.set("create_date", date);
		DAO.set("dept_id", deptId);
		DAO.set("data_area", dataArea);
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
