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
import java.util.List;
import java.util.Map;

import org.ccloud.model.SalesOrder;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesOrderQuery extends JBaseQuery {

	protected static final SalesOrder DAO = new SalesOrder();
	private static final SalesOrderQuery QUERY = new SalesOrderQuery();

	public static SalesOrderQuery me() {
		return QUERY;
	}

	public SalesOrder findById(final String id) {

		return DAO.findById(id);

	}

	public Record findMoreById(final String id) {
		StringBuilder fromBuilder = new StringBuilder(" select o.*,c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, u.realname, u.mobile ");
		fromBuilder.append(" from `cc_sales_order` o ");
		fromBuilder.append(" left join cc_customer c on o.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join user u on o.biz_user_id = u.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate) {
		String select = "select o.*, c.customer_name ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_order` o ");
		fromBuilder.append(" join cc_customer c on o.customer_id = c.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.order_sn", keyword, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}

		fromBuilder.append(" order by o.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public List<Record> findProductListBySeller(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sg.id, sg.product_id, sg.custom_name, sg.store_count, sg.price, sg.warehouse_id, GROUP_CONCAT(gsv.name) AS spe_name, p.big_unit, p.small_unit, p.convert_relate ");
		fromBuilder.append(" FROM cc_seller_product sg ");
		fromBuilder.append(" LEFT JOIN cc_product p ON sg.product_id = p.id ");
		fromBuilder
				.append(" LEFT JOIN cc_product_goods_specification_value pgsv ON sg.product_id = pgsv.product_set_id ");
		fromBuilder.append(
				" LEFT JOIN cc_goods_specification_value gsv ON pgsv.goods_specification_value_set_id = gsv.goods_specification_id ");
		fromBuilder.append(" WHERE sg.is_enable = 1 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sg.seller_id", sellerId, params, false);

		fromBuilder.append(" GROUP BY sg.product_id ");
		fromBuilder.append(" ORDER BY sg.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public List<Record> findCustomerListByUser(String userId) {
		StringBuilder fromBuilder = new StringBuilder(
				" select c.id, c.customer_name, c.contact, c.mobile, c.prov_name, c.city_name, c.country_name, c.address ");
		fromBuilder.append(" from `cc_customer` c ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c.id = ujc.customer_id ");
		fromBuilder.append(" WHERE c.customer_kind = '1' ");
		fromBuilder.append(" AND c.is_enabled = 1 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "ujc.user_id", userId, params, false);

		fromBuilder.append(" order by c.create_date ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public List<Record> findCustomerTypeListByCustomerId(String customerId, String dataArea) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select c.id, c.name, c.code ");
		sqlBuilder.append(" from `cc_customer_join_customer_type` cj ");
		sqlBuilder.append(" join `cc_customer_type` c on cj.customer_type_id = c.id ");
		sqlBuilder.append(" where c.is_show = 1 ");
		appendIfNotEmpty(sqlBuilder, "cj.customer_id", customerId, params, false);
		appendIfNotEmpty(sqlBuilder, "c.data_area", dataArea, params, false);

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public boolean insert(Map<String, String[]> paraMap, String orderId, String orderSn, String sellerId, String userId,
			Date date, String deptId, String dataArea) {
		DAO.set("id", orderId);
		DAO.set("order_sn", orderSn);
		DAO.set("seller_id", sellerId);
		DAO.set("biz_user_id", userId);
		DAO.set("customer_id", StringUtils.getArrayFirst(paraMap.get("customerId")));
		DAO.set("customer_type_id", StringUtils.getArrayFirst(paraMap.get("customerType")));
		DAO.set("contact", StringUtils.getArrayFirst(paraMap.get("contact")));
		DAO.set("mobile", StringUtils.getArrayFirst(paraMap.get("mobile")));
		DAO.set("address", StringUtils.getArrayFirst(paraMap.get("address")));
		DAO.set("status", 0);// 待审核
		DAO.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		DAO.set("receive_type", StringUtils.getArrayFirst(paraMap.get("receiveType")));
		DAO.set("delivery_address", StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
		DAO.set("delivery_date", StringUtils.getArrayFirst(paraMap.get("deliveryDate")));
		DAO.set("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
		DAO.set("create_date", date);
		DAO.set("dept_id", deptId);
		DAO.set("data_area", dataArea);
		return DAO.save();
	}

	public int updateConfirm(String orderId, int status, String userId, Date date) {

		return Db.update(
				"update cc_sales_order set status = ?, confirm_user_id = ?, confirm_date = ? where id = ?",
				status, userId, date, orderId);

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

	public static String getBillIdBySn(String order_sn) {
		String sql = "SELECT id FROM cc_sales_order WHERE order_sn='"+order_sn+"'";
		return Db.queryStr(sql);
	}

}
