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
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.ccloud.Consts;
import org.ccloud.model.Receivables;
import org.ccloud.model.SalesOutstock;
import org.ccloud.utils.DateUtils;
import org.ccloud.model.vo.printAllNeedInfo;
import org.ccloud.utils.StringUtils;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesOutstockQuery extends JBaseQuery {

	protected static final SalesOutstock DAO = new SalesOutstock();
	private static final SalesOutstockQuery QUERY = new SalesOutstockQuery();

	public static SalesOutstockQuery me() {
		return QUERY;
	}

	public SalesOutstock findById(final String id) {
		return DAO.findById(id);
	}

	public Record findMoreById(final String id) {
		StringBuilder fromBuilder = new StringBuilder(
				" select o.*, cs.customer_kind, c.id as customerId, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as customerTypeCode, u.realname, u.mobile ");
		fromBuilder.append(" ,w.code as warehouseCode, cp.factor ");
		fromBuilder.append(" from `cc_sales_outstock` o ");
		fromBuilder.append(" left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append(" left join cc_customer c on cs.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join cc_price_system cp on cp.id = ct.price_system_id ");
		fromBuilder.append(" left join user u on o.biz_user_id = u.id ");
		fromBuilder.append(" left join cc_warehouse w on o.warehouse_id = w.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}
	
	public boolean pass(final String orderId, final String userId, final String sellerId, final String sellerCode) {
		boolean isSave = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				Record order = SalesOrderQuery.me().findMoreById(orderId);
				List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
				createReceivables(order);

				Date date = new Date();

				String outstockId = "";
				String warehouseId = "";
				String outstockSn = "";
				for (Record orderDetail : orderDetailList) {
					if (!warehouseId.equals(orderDetail.getStr("warehouse_id"))) {

						outstockId = StrKit.getRandomUUID();
						warehouseId = orderDetail.getStr("warehouse_id");
						String OrderSO = SalesOutstockQuery.me().getNewSn(sellerId);
						// 销售出库单：SS + 100000(机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
						outstockSn = "SS" + sellerCode + order.getStr("typeCode") + orderDetail.getStr("warehouseCode")
								+ DateUtils.format("yyMMdd", date) + OrderSO;

						SalesOutstockQuery.me().insert(outstockId, outstockSn, warehouseId, sellerId, order, date);
						SalesOrderJoinOutstockQuery.me().insert(orderId, outstockId);
					}

					SalesOutstockDetailQuery.me().insert(outstockId, orderDetail, date, order);
				}

				SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_AUDIT_STATUS_PASS, userId, date);// 已审核通过

				return true;
			}
		});

		return isSave;
	}
	
	private void createReceivables(Record order) {
		String customeId = order.getStr("customer_id");
		Receivables receivables = ReceivablesQuery.me().findByCustomerId(customeId);
		if (receivables == null) {
			receivables = new Receivables();
			receivables.setObjectId(order.getStr("customer_id"));
			receivables.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
			receivables.setReceiveAmount(order.getBigDecimal("total_amount"));
			receivables.setActAmount(new BigDecimal(0));
			receivables.setBalanceAmount(order.getBigDecimal("total_amount"));
			receivables.setDeptId(order.getStr("dept_id"));
			receivables.setDataArea(order.getStr("data_area"));
			receivables.setCreateDate(new Date());
		} else {
			receivables.setReceiveAmount(receivables.getReceiveAmount().add(order.getBigDecimal("total_amount")));
			receivables.setBalanceAmount(receivables.getBalanceAmount().add(order.getBigDecimal("total_amount")));
		}
		receivables.saveOrUpdate();
	}

	public boolean insert(String outstockId, String outstockSn, String warehouseId, String sellerId, Record order,
			Date date) {
		SalesOutstock outstock = new SalesOutstock();
		outstock.setId(outstockId);
		outstock.setOutstockSn(outstockSn);
		outstock.setWarehouseId(warehouseId);
		outstock.setSellerId(sellerId);
		outstock.setCustomerId(order.getStr("customer_id"));
		outstock.setCustomerTypeId(order.getStr("customer_type_id"));
		outstock.setContact(order.getStr("contact"));
		outstock.setMobile(order.getStr("mobile"));
		outstock.setAddress(order.getStr("address"));
		outstock.setBizUserId(order.getStr("biz_user_id"));
		outstock.setTotalAmount(order.getBigDecimal("total_amount"));
		outstock.setReceiveType(order.getInt("receive_type"));
		outstock.setDeliveryAddress(order.getStr("delivery_address"));
		outstock.setDeliveryDate(order.getDate("delivery_date"));
		outstock.setRemark(order.getStr("remark"));
		outstock.setCreateDate(date);
		outstock.setDeptId(order.getStr("dept_id"));
		outstock.setDataArea(order.getStr("data_area"));
		
		return outstock.save();
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate,String printStatus, String stockOutStatus,String custoemName,String dataArea) {
		String select = "select o.*, c.customer_name ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append("left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append("left join cc_customer c on c.id = cs.customer_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.outstock_sn", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", custoemName, params, needWhere);
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
		
		if (StrKit.notBlank(printStatus)) {
			fromBuilder.append(" and o.is_print = ?");
			params.add(printStatus);
		}
		
		
		if (StrKit.notBlank(stockOutStatus)) {
			fromBuilder.append(" and o.status = ?");
			params.add(stockOutStatus);
		}

		fromBuilder.append(" order by o.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
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

	public String getNewSn(String sellerId) {
		String sql = "SELECT s.outstock_sn FROM cc_sales_outstock s WHERE date(s.create_date) = curdate() AND s.seller_id = ? ORDER BY s.create_date desc";
		SalesOutstock sales = DAO.findFirst(sql, sellerId);
		String SN = "";
		if (sales == null || StringUtils.isBlank(sales.getOutstockSn())) {
			SN = Consts.SALES_OUT_STOCK_SN;
		} else {
			String endSN = StringUtils.substringSN(Consts.SALES_OUT_STOCK_SN, sales.getOutstockSn());
			SN = new BigDecimal(endSN).add(new BigDecimal(1)).toString();
		}
		return SN;
	}

	public boolean updateStatus(String id, int salesOutStockStatusOut, Date date) {
		String sql = "update cc_sales_outstock cc set cc.status = ? AND cc.modify_date = ? where cc.id = ?";
		int i = Db.update(sql, salesOutStockStatusOut, date, id);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String status,
			String customerTypeId, String startDate, String endDate, String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name, ct.name as customerTypeName ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append("left join cc_customer_type ct on o.customer_type_id = ct.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.outstock_sn", keyword, params, needWhere);
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

	
	public printAllNeedInfo findStockOutForPrint(final String id) {
		StringBuilder fromBuilder = new StringBuilder("select o.outstock_sn,o.delivery_address,o.total_amount, cs.customer_kind, c.id as customerId, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as customerTypeCode, u.realname, u.mobile, ");
		fromBuilder.append(" w.code as warehouseCode, cp.factor,w.`name` as warehouseName,w.phone as warehousePhone,o.create_date as placeOrderTime,o.remark,sn.seller_name,so.total_amount,so.id as orderId,so.biz_user_id,o.id as salesOutStockId,sn.id as sellerId,pt.context as printFootContext ");
		fromBuilder.append(" from `cc_sales_outstock` o ");
		fromBuilder.append(" left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sj on sj.outstock_id = o.id ");
		fromBuilder.append(" left join cc_customer c on cs.customer_id = c.id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order so on so.id = sj.order_id ");
		fromBuilder.append(" LEFT JOIN cc_seller sn on sn.id = so.seller_id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join cc_price_system cp on cp.id = ct.price_system_id ");
		fromBuilder.append(" left join user u on so.biz_user_id = u.id ");
		fromBuilder.append(" left join cc_warehouse w on o.warehouse_id = w.id ");
		fromBuilder.append(" LEFT JOIN cc_seller_join_template cjt on cjt.seller_id = sn.id ");
		fromBuilder.append(" LEFT JOIN cc_print_template pt on pt.id = cjt.print_template_id ");
		fromBuilder.append(" where o.id = ? ");
		printAllNeedInfo printAllNeedInfo = new printAllNeedInfo();
		 List<Record> records = Db.find(fromBuilder.toString(), id);
		 for (Record record : records) {
			printAllNeedInfo.setOutstockSn(record.getStr("outstock_sn"));
			printAllNeedInfo.setDeliveryAddress(record.getStr("delivery_address"));
			printAllNeedInfo.setCustomerName(record.getStr("customer_name"));
			printAllNeedInfo.setCustomerContacts(record.getStr("ccontact"));
			printAllNeedInfo.setCustomerPhone(record.getStr("cmobile"));
			printAllNeedInfo.setPlaceOrderMan(record.getStr("realname"));
			printAllNeedInfo.setPlaceOrderPhone(record.getStr("mobile"));
			printAllNeedInfo.setWarehouseName(record.getStr("warehouseName"));
			printAllNeedInfo.setWarehousePhone(record.getStr("warehousePhone"));
			printAllNeedInfo.setSalesAmount(record.getBigDecimal("total_amount"));
			printAllNeedInfo.setSellerName(record.getStr("seller_name"));
			printAllNeedInfo.setRemark(record.getStr("remark"));
			printAllNeedInfo.setPlaceOrderTime(record.getDate("placeOrderTime"));
			printAllNeedInfo.setOrderId(record.getStr("orderId"));
			printAllNeedInfo.setBizUserId(record.getStr("biz_user_id"));
			printAllNeedInfo.setSalesOutStockId(record.getStr("salesOutStockId"));
			printAllNeedInfo.setPrintFootContext(record.getStr("printFootContext"));
		}
		    return printAllNeedInfo;
	}
	
	public void updatePrintStatus(String id) {
		String sql = "update cc_sales_outstock cc set is_print = 1,print_count=print_count+ "+ 1 +" where cc.id = '"+id+"'";
		Db.update(sql);
	}

}
