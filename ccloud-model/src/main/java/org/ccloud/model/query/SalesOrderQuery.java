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

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SalesOrderDetail;
import org.ccloud.utils.DateUtils;
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
		StringBuilder fromBuilder = new StringBuilder(" select o.*,c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as typeCode, u.realname, u.mobile ");
		fromBuilder.append(" from `cc_sales_order` o ");
		fromBuilder.append(" left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append(" left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join user u on o.biz_user_id = u.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate,
			String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_order` o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.order_sn", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);

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

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String status,
			String customerTypeId, String startDate, String endDate, String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name, ct.name as customerTypeName ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_order` o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append("left join cc_customer_type ct on o.customer_type_id = ct.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.order_sn", keyword, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.customer_type_id", customerTypeId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);

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

		fromBuilder.append(" order by o.create_date desc ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public List<Record> findProductListBySeller(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sg.id, sg.product_id, sg.custom_name, sg.store_count, sg.price, sg.warehouse_id, t1.valueName, p.big_unit, p.small_unit, p.convert_relate ");
		fromBuilder.append("FROM cc_seller_product sg ");
		fromBuilder.append("LEFT JOIN cc_product p ON sg.product_id = p.id ");
		fromBuilder.append(
				"LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append(
				"RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = sg.product_id ");
		fromBuilder.append("WHERE sg.is_enable = 1 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sg.seller_id", sellerId, params, false);

		fromBuilder.append(" ORDER BY sg.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public List<Record> findCustomerListByUser(String userId) {
		StringBuilder fromBuilder = new StringBuilder(
				" select cs.id, cc.customer_name, cc.contact, cc.mobile, cc.prov_name, cc.city_name, cc.country_name, cc.address ");
		fromBuilder.append(" from `cc_seller_customer` cs ");
		fromBuilder.append(" LEFT JOIN cc_customer cc ON cs.customer_id = cc.id ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON cs.id = ujc.seller_customer_id ");
		// fromBuilder.append(" WHERE cs.customer_kind = '1' ");
		fromBuilder.append(" WHERE cs.is_enabled = 1 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "ujc.user_id", userId, params, false);

		fromBuilder.append(" order by cc.create_date ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public List<Record> findCustomerTypeListByCustomerId(String customerId, String dataArea) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select c.id, c.name, c.code, cp.factor ");
		sqlBuilder.append(" from `cc_customer_join_customer_type` cj ");
		sqlBuilder.append(" left join `cc_customer_type` c on cj.customer_type_id = c.id ");
		sqlBuilder.append(" left join `cc_price_system` cp on cp.id = c.price_system_id ");
		sqlBuilder.append(" where c.is_show = 1 ");
		appendIfNotEmpty(sqlBuilder, "cj.seller_customer_id", customerId, params, false);
		appendIfNotEmpty(sqlBuilder, "c.data_area", dataArea, params, false);

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public boolean insert(Map<String, String[]> paraMap, String orderId, String orderSn, String sellerId, String userId,
			Date date, String deptId, String dataArea) {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setId(orderId);
		salesOrder.setOrderSn(orderSn);
		salesOrder.setSellerId(sellerId);
		salesOrder.setBizUserId(userId);
		salesOrder.setCustomerId(StringUtils.getArrayFirst(paraMap.get("customerId")));
		salesOrder.setCustomerTypeId(StringUtils.getArrayFirst(paraMap.get("customerType")));
		salesOrder.setContact(StringUtils.getArrayFirst(paraMap.get("contact")));
		salesOrder.setMobile(StringUtils.getArrayFirst(paraMap.get("mobile")));
		salesOrder.setAddress(StringUtils.getArrayFirst(paraMap.get("address")));
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);// 待审核
		String total = StringUtils.getArrayFirst(paraMap.get("total"));
		String type = StringUtils.getArrayFirst(paraMap.get("receiveType"));
		salesOrder.setTotalAmount(new BigDecimal(total));
		salesOrder.setReceiveType(StringUtils.isNumeric(type) ? Integer.parseInt(type) : 0);
		salesOrder.setDeliveryAddress(StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
		Date deliveryDate = DateUtils.strToDate(StringUtils.getArrayFirst(paraMap.get("deliveryDate")),
				DateUtils.DEFAULT_NORMAL_FORMATTER);
		salesOrder.setDeliveryDate(deliveryDate);
		salesOrder.setRemark(StringUtils.getArrayFirst(paraMap.get("remark")));
		salesOrder.setCreateDate(date);
		salesOrder.setDeptId(deptId);
		salesOrder.setDataArea(dataArea);
		return salesOrder.save();
	}

	public boolean insertForApp(Map<String, String[]> paraMap, String orderId, String orderSn, String sellerId,
			String userId, Date date, String deptId, String dataArea) {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setId(orderId);
		salesOrder.setOrderSn(orderSn);
		salesOrder.setSellerId(sellerId);
		salesOrder.setBizUserId(userId);
		salesOrder.setCustomerId(StringUtils.getArrayFirst(paraMap.get("customerId")));
		salesOrder.setCustomerTypeId(StringUtils.getArrayFirst(paraMap.get("customerType")));
		salesOrder.setContact(StringUtils.getArrayFirst(paraMap.get("contact")));
		salesOrder.setMobile(StringUtils.getArrayFirst(paraMap.get("mobile")));
		salesOrder.setAddress(StringUtils.getArrayFirst(paraMap.get("address")));
		salesOrder.setStatus(0);// 待审核
		String total = StringUtils.getArrayFirst(paraMap.get("total"));
		String type = StringUtils.getArrayFirst(paraMap.get("receiveType"));
		salesOrder.setTotalAmount(new BigDecimal(total));
		salesOrder.setReceiveType(StringUtils.isNumeric(type) ? Integer.parseInt(type) : 0);
		salesOrder.setDeliveryAddress(StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
		Date deliveryDate = DateUtils.strToDate(StringUtils.getArrayFirst(paraMap.get("deliveryDate")),
				DateUtils.DEFAULT_NORMAL_FORMATTER);
		salesOrder.setDeliveryDate(deliveryDate);
		salesOrder.setRemark(StringUtils.getArrayFirst(paraMap.get("remark")));
		salesOrder.setCreateDate(date);
		salesOrder.setDeptId(deptId);
		salesOrder.setDataArea(dataArea);
		return salesOrder.save();
	}
	
	public int updateConfirm(String orderId, int status, String userId, Date date) {

		return Db.update("update cc_sales_order set status = ?, confirm_user_id = ?, confirm_date = ? where id = ?",
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
		String sql = "SELECT id FROM cc_sales_order WHERE order_sn='" + order_sn + "'";
		return Db.queryStr(sql);
	}

	public String getNewSn(String sellerId) {
		String sql = "SELECT s.order_sn FROM cc_sales_order s WHERE date(s.create_date) = curdate() AND s.seller_id = ? ORDER BY s.create_date desc";
		SalesOrder sales = DAO.findFirst(sql, sellerId);
		String SN = "";
		if (sales == null || StringUtils.isBlank(sales.getOrderSn())) {
			SN = Consts.SALES_ORDER_SN;
		} else {
			String endSN = StringUtils.substringSN(Consts.SALES_ORDER_SN, sales.getOrderSn());
			SN = new BigDecimal(endSN).add(new BigDecimal(1)).toString();
		}
		return SN;
	}

	public boolean checkStatus(String outStockId, Date date) {
		SalesOrder salesOrder = this.findByOutStockId(outStockId);
		List<SalesOrderDetail> list = SalesOrderDetailQuery.me().findBySalesOrderId(salesOrder.getId());
		boolean status = true;
		for (SalesOrderDetail salesOrderDetail : list) {
			if (salesOrderDetail.getLeftCount() > 0) {
				status = false;
				break;
			}
		}

		if (status) {
			salesOrder.setStatus(Consts.SALES_ORDER_STATUS_ALL_OUT);
		} else {
			salesOrder.setStatus(Consts.SALES_ORDER_STATUS_PART_OUT);
		}
		salesOrder.setModifyDate(date);

		if (!salesOrder.update()) {
			return false;
		}
		return true;
	}

	private SalesOrder findByOutStockId(String outStockId) {
		String sql = "SELECT cs.* FROM cc_sales_order cs LEFT JOIN cc_sales_order_join_outstock cj ON cs.id = cj.order_id where cj.outstock_id=? ";
		return DAO.findFirst(sql, outStockId);
	}

	public Page<Record> findBySellerCustomerId(int pageNumber, int pageSize, String customerId, String dataArea) {
		boolean needwhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		String select = "SELECT o.order_sn, o.total_count, o.create_date, o.total_amount, o.realname, o.`status`,o.data_area,o.receive_type ";

		StringBuilder sql = new StringBuilder(
				"FROM (SELECT cso.order_sn, cso.total_count, cso.create_date, cso.total_amount, u.realname, cso.`status`,cso.data_area,cso.receive_type ");
		sql.append("FROM cc_sales_order cso LEFT JOIN cc_sales_order_detail csod ON cso.id = csod.order_id ");
		sql.append("LEFT JOIN `user` u ON u.id = cso.biz_user_id ");
		sql.append("LEFT JOIN cc_seller_customer csc ON csc.id = cso.customer_id ");

		needwhere = appendIfNotEmpty(sql, "csc.customer_id", customerId, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "cso.data_area", dataArea, params, needwhere);

		sql.append("GROUP BY cso.id ");
		sql.append("ORDER BY cso.`status`, cso.create_date DESC) AS o");
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());
	}

	public boolean insertOrderByComposition(Map<String, String[]> paraMap, String orderId, String orderSn,
			String sellerId, String userId, Date date, String deptId, String dataArea) {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setId(orderId);
		salesOrder.setOrderSn(orderSn);
		salesOrder.setSellerId(sellerId);		
		salesOrder.setBizUserId(userId);
		salesOrder.setCustomerId(StringUtils.getArrayFirst(paraMap.get("customerId")));
		salesOrder.setCustomerTypeId(StringUtils.getArrayFirst(paraMap.get("customerType")));
		salesOrder.setContact(StringUtils.getArrayFirst(paraMap.get("contact")));
		salesOrder.setMobile(StringUtils.getArrayFirst(paraMap.get("mobile")));
		salesOrder.setAddress(StringUtils.getArrayFirst(paraMap.get("address")));
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);// 待审核
		String total = StringUtils.getArrayFirst(paraMap.get("total"));
		String type = StringUtils.getArrayFirst(paraMap.get("receiveType"));
		salesOrder.setTotalAmount(new BigDecimal(total));
		salesOrder.setReceiveType(StringUtils.isNumeric(type)? Integer.parseInt(type) : 0);
		salesOrder.setDeliveryAddress(StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
		Date deliveryDate = DateUtils.strToDate(StringUtils.getArrayFirst(paraMap.get("deliveryDate")), DateUtils.DEFAULT_NORMAL_FORMATTER);
		salesOrder.setDeliveryDate(deliveryDate);
		salesOrder.setRemark(StringUtils.getArrayFirst(paraMap.get("remark")));
		salesOrder.setCreateDate(date);
		salesOrder.setDeptId(deptId);
		salesOrder.setDataArea(dataArea);
		return salesOrder.save();
	}

}
