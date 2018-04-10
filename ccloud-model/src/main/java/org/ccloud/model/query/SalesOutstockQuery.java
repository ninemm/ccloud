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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.Customer;
import org.ccloud.model.Product;
import org.ccloud.model.SalesOutstock;
import org.ccloud.model.SellerProduct;
import org.ccloud.utils.DateUtils;
import org.ccloud.model.vo.carSalesPrintNeedInfo;
import org.ccloud.model.vo.printAllNeedInfo;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.PathKit;
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
		StringBuilder fromBuilder = new StringBuilder(" select o.*, sa.biz_user_id as order_user, sa.create_date as order_date, sa.activity_apply_id, cs.customer_kind, c.id as customerId, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as customerTypeCode, u.realname, u.mobile ");
		fromBuilder.append(" ,w.code as warehouseCode, cp.factor, sa.proc_inst_id, sa.id as order_id ");
		fromBuilder.append(" from `cc_sales_outstock` o ");
		fromBuilder.append(" left join cc_sales_order_join_outstock co ON co.outstock_id = o.id ");
		fromBuilder.append(" left join cc_sales_order sa on sa.id = co.order_id ");
		fromBuilder.append(" left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append(" left join cc_customer c on cs.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join cc_price_system cp on cp.id = ct.price_system_id ");
		fromBuilder.append(" left join user u on sa.biz_user_id = u.id ");
		fromBuilder.append(" left join cc_warehouse w on o.warehouse_id = w.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Record findMoreBySn(final String sn) {
		StringBuilder fromBuilder = new StringBuilder("select o.*, sa.biz_user_id as order_user , cs.customer_kind, c.id as customerId, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as customerTypeCode, u.realname, u.mobile ");
		fromBuilder.append(" ,w.code as warehouseCode, cp.factor ");
		fromBuilder.append(" from `cc_sales_outstock` o ");
		fromBuilder.append(" left join cc_sales_order_join_outstock co ON co.outstock_id = o.id ");
		fromBuilder.append(" left join cc_sales_order sa on sa.id = co.order_id ");
		fromBuilder.append(" left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append(" left join cc_customer c on cs.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join cc_price_system cp on cp.id = ct.price_system_id ");
		fromBuilder.append(" left join user u on sa.biz_user_id = u.id ");
		fromBuilder.append(" left join cc_warehouse w on o.warehouse_id = w.id ");
		fromBuilder.append(" where o.outstock_sn = ? ");

		return Db.findFirst(fromBuilder.toString(), sn);
	}

	public boolean pass(final String orderId, final String userId, final String sellerId, final String sellerCode) {
		boolean isSave = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				Record order = SalesOrderQuery.me().findMoreById(orderId);
				List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
				// //应收账款
				// createReceivables(order);

				Date date = new Date();

				String outstockId = "";
				String warehouseId = "";
				String outstockSn = "";
				BigDecimal outTotalAmount = new BigDecimal(0);
				int i = 1;
				boolean composite = false;
				for (Record orderDetail : orderDetailList) {
					if (!warehouseId.equals(orderDetail.getStr("warehouse_id"))) {
						if (StringUtils.isNotBlank(outstockId)) {
							SalesOutstockQuery.me().updateTotalAmount(outstockId, outTotalAmount.toString());
							outTotalAmount = new BigDecimal(0);
						}
						if (orderDetail.getInt("is_composite") == 1) {
							composite = true;
						}
						outstockId = StrKit.getRandomUUID();
						warehouseId = orderDetail.getStr("warehouse_id");
						String OrderSO = SalesOutstockQuery.me().getNewSn(sellerId);
						// 销售出库单：SS + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 0001(流水号)
						outstockSn = "SS" + sellerCode + order.getStr("typeCode") 
								+ DateUtils.format("yyMMdd", date) + OrderSO;
						SalesOutstockQuery.me().insert(outstockId, outstockSn, warehouseId, sellerId, order, date);
						SalesOrderJoinOutstockQuery.me().insert(orderId, outstockId);
					}

					outTotalAmount = outTotalAmount
							.add(SalesOutstockDetailQuery.me().insert(outstockId, orderDetail, date, order));
					if (i == orderDetailList.size()) {
						if (composite) {
							outTotalAmount = order.getBigDecimal("total_amount");
						}
						SalesOutstockQuery.me().updateTotalAmount(outstockId, outTotalAmount.toString());
					}
					i++;
				}
				
				//生成出库单时候生成二维码
				if (Consts.QRDEALERCODE.contains(sellerCode)) {
					generateQrcode(orderId,order,orderDetailList, sellerCode);
				}
				SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_AUDIT_STATUS_PASS, userId, date);// 已审核通过

				return true;
			}
		});

		return isSave;
	}

	public int updateTotalAmount(String outstockId, String outTotalAmount) {
		StringBuilder fromBuilder = new StringBuilder(
				"update cc_sales_outstock cc set cc.total_amount = ? where cc.id = ? ");
		return Db.update(fromBuilder.toString(), outTotalAmount, outstockId);
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
		outstock.setReceiveType(order.getInt("receive_type"));
		outstock.setDeliveryAddress(order.getStr("delivery_address"));
		outstock.setDeliveryDate(order.getDate("delivery_date"));
		outstock.setRemark(order.getStr("remark"));
		outstock.setCreateDate(date);
		outstock.setDeptId(order.getStr("dept_id"));
		outstock.setDataArea(order.getStr("data_area"));

		return outstock.save();
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String sellerId, String keyword, String startDate, 
			String endDate, String printStatus, String stockOutStatus, String status, String dataArea,String order,String sort,String salesmanId, String carWarehouseId) {
		String select = "select o.*,  c.prov_name,c.city_name,c.country_name,c.address, c.customer_name,u.realname,ct.name as customerName,t0.id as orderId,t0.order_sn,t0.create_date as orderDate ,t0.realname as bizName ";
		if (StrKit.notBlank(status)) {
			select = select + ",t2.refundCount, t2.outCount ";
		}
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append("left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append("left join cc_customer c on c.id = cs.customer_id ");
		fromBuilder.append("left join user u on u.id = o.biz_user_id ");
		fromBuilder.append("left join cc_customer_type ct on ct.id = o.customer_type_id ");
		fromBuilder.append("left join cc_sales_order_join_outstock sojo on sojo.outstock_id = o.id ");
		fromBuilder.append("LEFT JOIN (SELECT cso.id ,cso.order_sn,cso.create_date,cso.`status`,u.realname,cso.biz_user_id from cc_sales_order cso LEFT JOIN `user` u on u.id = cso.biz_user_id ) t0 on t0.id = sojo.order_id ");
		
		if (StrKit.notBlank(status)) {
			fromBuilder.append("left join (SELECT cc.id, cc.outstock_id, IFNULL(SUM(cc.product_count),0) as outCount, IFNULL(SUM(t1.count), 0) AS refundCount ");
			fromBuilder.append("FROM cc_sales_outstock_detail cc LEFT JOIN (SELECT SUM(cr.reject_product_count) AS count, cr.outstock_detail_id FROM cc_sales_refund_instock_detail cr ");
			fromBuilder.append("LEFT JOIN cc_sales_refund_instock ci ON ci.id = cr.refund_instock_id where ci.`status` not in (?,?) ");
			fromBuilder.append("GROUP BY cr.outstock_detail_id ) t1 ON cc.id = t1.outstock_detail_id GROUP BY cc.outstock_id ) t2 on t2.outstock_id = o.id ");
		}

		LinkedList<Object> params = new LinkedList<Object>();
		if (StrKit.notBlank(status)) {
			params.add(Consts.SALES_REFUND_INSTOCK_CANCEL);
			params.add(Consts.SALES_REFUND_INSTOCK_REFUSE);
		}
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.seller_id", sellerId, params, needWhere);
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(carWarehouseId)) {
			fromBuilder.append(" and o.warehouse_id= ?");
			params.add(carWarehouseId);
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
		if (StrKit.notBlank(salesmanId)) {
			fromBuilder.append(" and t0.biz_user_id = ?");
			params.add(salesmanId);
		}
		if (StrKit.notBlank(carWarehouseId)) {
			fromBuilder.append(" and o.warehouse_id= ?");
			params.add(carWarehouseId);
		}
		if (StrKit.notBlank(status)) {
			fromBuilder.append(" and o.status != ? and outCount > refundCount");
			params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		}		

		fromBuilder.append(" and t0.status != "+Consts.SALES_ORDER_STATUS_CANCEL+" and ( o.outstock_sn like '%"+keyword+"%' or c.customer_name like '%"+keyword+"%' ) ");

		if (sort == "" || null == sort) {
			fromBuilder.append("order by " + "o.create_date desc");
		} else {
			fromBuilder.append("order by " + sort + " " + order);
		}

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
			String outstockSn = sales.getOutstockSn();
			String endSN = StringUtils.substringSN(Consts.SALES_OUT_STOCK_SN,outstockSn );
			SN = StringUtils.addIntStrAndFillZeros(endSN, 1, 4);
//			SN = new BigDecimal(endSN).add(new BigDecimal(1)).toString();
		}
		return SN;
	}

	public boolean updateStatus(String id, String userId, int salesOutStockStatusOut, Date date, String total) {
		StringBuilder fromBuilder = new StringBuilder(
				"update cc_sales_outstock cc set cc.biz_user_id=? , cc.biz_date=? , cc.status = ? , cc.modify_date = ? ");
		if (total != null) {
			fromBuilder.append(", cc.total_amount = ? ");
		}
		fromBuilder.append("where cc.id = ?");
		int i = 0;
		if (total != null) {
			i = Db.update(fromBuilder.toString(), userId, date, salesOutStockStatusOut, date, total, id);
		} else {
			i = Db.update(fromBuilder.toString(), userId, date, salesOutStockStatusOut, date, id);
		}
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String status,
			String customerTypeId, String startDate, String endDate, String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name, ct.name as customerTypeName, t2.refundCount, t2.outCount ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append("left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append("left join (SELECT cc.id, cc.outstock_id, IFNULL(SUM(cc.product_count),0) as outCount, IFNULL(SUM(t1.count), 0) AS refundCount ");
		fromBuilder.append("FROM cc_sales_outstock_detail cc LEFT JOIN (SELECT SUM(cr.reject_product_count) AS count, cr.outstock_detail_id FROM cc_sales_refund_instock_detail cr ");
		fromBuilder.append("LEFT JOIN cc_sales_refund_instock ci ON ci.id = cr.refund_instock_id where ci.`status` not in (?,?) ");
		fromBuilder.append("GROUP BY cr.outstock_detail_id ) t1 ON cc.id = t1.outstock_detail_id GROUP BY cc.outstock_id ) t2 on t2.outstock_id = o.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(Consts.SALES_REFUND_INSTOCK_CANCEL);
		params.add(Consts.SALES_REFUND_INSTOCK_REFUSE);
		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "o.status", status, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "ct.name", customerTypeId, params, needWhere);
		// needWhere = appendIfNotEmpty(fromBuilder, "o.customer_type_id",
		// customerTypeId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where refundCount < outCount AND o.status != ? ");
			params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		} else {
			fromBuilder.append(" AND refundCount < outCount AND o.status != ? ");
			params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		}

		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(
					" and (o.outstock_sn like '%" + keyword + "%' or c.customer_name like '%" + keyword + "%')");
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

	// 应收账款
	public Page<Record> paginateForReceivables(int pageNumber, int pageSize, String keyword, String userId,
			String customerTypeId, String startDate, String endDate, String isDone, String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name, ct.name as customerTypeName,o.total_amount - COALESCE(t.actAmount,0) AS balanceAmount ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append(" LEFT JOIN cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append(" LEFT JOIN cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order_join_outstock cso ON o.id = cso.outstock_id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order cs ON cso.order_id = cs.id ");
		fromBuilder.append(" LEFT JOIN (select r.ref_sn, SUM(r.act_amount) AS actAmount from cc_receiving r group by r.ref_sn)t ON t.ref_sn=o.outstock_sn ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "o.customer_type_id", customerTypeId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cs.biz_user_id", userId, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where o.status != ? ");
			params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		} else {
			fromBuilder.append(" AND o.status != ? ");
			params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		}

		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(
					" and (o.outstock_sn like '%" + keyword + "%' or c.customer_name like '%" + keyword + "%')");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}

		if ("1".equals(isDone)) {
			fromBuilder.append(" and o.total_amount = COALESCE(t.actAmount, 0)  ");
		} else if ("2".equals(isDone)) {
			fromBuilder.append(" and o.total_amount > COALESCE(t.actAmount, 0) ");
		}

		fromBuilder.append(" order by  o.create_date desc ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public printAllNeedInfo findStockOutForPrint(final String id) {
		StringBuilder fromBuilder = new StringBuilder("select so.total_count,o.outstock_sn,o.receive_type,o.remark as stockOutRemark,o.delivery_address,o.total_amount, cs.customer_kind, cs.id as customerId, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, ct.code as customerTypeCode, u.realname, u.mobile, ");
		fromBuilder.append(" sn.phone sellerPhone,w.code as warehouseCode, cp.factor,w.`name` as warehouseName,w.phone as warehousePhone,so.create_date as placeOrderTime,so.remark,sn.seller_name,so.total_amount,so.id as orderId,so.biz_user_id, so.activity_apply_id,so.order_qrcode_url, o.id as salesOutStockId,sn.id as sellerId,pt.context as printFootContext ");
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
		Record record = Db.findFirst(fromBuilder.toString(), id);	
			printAllNeedInfo.setOutstockSn(record.getStr("outstock_sn"));
			printAllNeedInfo.setDeliveryAddress(record.getStr("delivery_address"));
			printAllNeedInfo.setCustomerName(record.getStr("customer_name"));
			printAllNeedInfo.setCustomerContacts(record.getStr("ccontact"));
			printAllNeedInfo.setCustomerPhone(record.getStr("cmobile"));
			printAllNeedInfo.setCustomerKind(record.getStr("customer_kind"));
			printAllNeedInfo.setPlaceOrderMan(record.getStr("realname"));
			printAllNeedInfo.setPlaceOrderPhone(record.getStr("mobile"));
			printAllNeedInfo.setSellerPhone(record.getStr("sellerPhone"));
			printAllNeedInfo.setWarehouseName(record.getStr("warehouseName"));
			printAllNeedInfo.setWarehousePhone(record.getStr("warehousePhone"));
			printAllNeedInfo.setTotalCount(record.getBigDecimal("total_count"));
			printAllNeedInfo.setSalesAmount(record.getBigDecimal("total_amount"));
			printAllNeedInfo.setSellerName(record.getStr("seller_name"));
			printAllNeedInfo.setRemark(record.getStr("remark"));
			printAllNeedInfo.setPlaceOrderTime(record.getDate("placeOrderTime"));
			printAllNeedInfo.setOrderId(record.getStr("orderId"));
			printAllNeedInfo.setBizUserId(record.getStr("biz_user_id"));
			printAllNeedInfo.setActivityApplyId(record.getStr("activity_apply_id"));
			printAllNeedInfo.setCustomerId(record.getStr("customerId"));
			printAllNeedInfo.setStockOutRemark(record.getStr("stockOutRemark"));
			printAllNeedInfo.setReceiveType(record.getInt("receive_type"));
			printAllNeedInfo.setSalesOutStockId(record.getStr("salesOutStockId"));
			printAllNeedInfo.setPrintFootContext(record.getStr("printFootContext"));
			printAllNeedInfo.setOrderQrcodeUrl(record.getStr("order_qrcode_url"));
			return printAllNeedInfo;
	}

	public void updatePrintStatus(String id) {
		String sql = "update cc_sales_outstock cc set is_print = 1,print_count=print_count+ " + 1 + " where cc.id = '"
				+ id + "'";
		Db.update(sql);
	}

	public boolean updateStockOutStatus(String id, String userId, Date stockOutDate, int salesOutStockStatusOut,
			Date modafyDate, String remark) {
		String sql = "update cc_sales_outstock cc set cc.biz_user_id=? , cc.biz_date=? , cc.status = ? , cc.modify_date = ?,cc.remark = ? where cc.id = ?";
		int i = Db.update(sql, userId, stockOutDate, salesOutStockStatusOut, modafyDate, remark, id);
		if (i > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<carSalesPrintNeedInfo> getCarSalesPrintInfo(String wareHouseId, String beginDate, String endDate) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" SELECT so1.convert_relate, so1.custom_name, w.`name` as wareHouseName, SUM(so1.bigCount)as bigCount, so1.smallCount, so1.is_gift, ");
		stringBuilder.append(" SUM(so1.product_amount)as product_amount, so1.sell_product_id, so1.bar_code, w.phone as wareHousePhone,so1.big_unit,so1.small_unit  FROM cc_sales_outstock c LEFT JOIN cc_warehouse w  ");
		stringBuilder.append(" on w.id = c.warehouse_id JOIN(SELECT co.sell_product_id, co.outstock_id, co.is_gift, p.convert_relate,  ");
		stringBuilder.append(" cs.custom_name,p.big_unit,p.small_unit, floor(co.product_count/p.convert_relate) as bigCount, MOD(co.product_count,p.convert_relate) as ");
		stringBuilder.append(" smallCount, cs.bar_code, case when co.is_gift = 0  THEN product_amount ELSE 0 END as product_amount FROM ");
		stringBuilder.append(" cc_sales_outstock_detail co LEFT JOIN cc_seller_product cs on cs.id = co.sell_product_id LEFT JOIN cc_product p ");
		stringBuilder.append(" on p.id = cs.product_id ) so1 on so1.outstock_id = c.id WHERE c.warehouse_id =? AND c.biz_date >= ? AND c.biz_date <=? and c.`status` ='1000' GROUP BY so1.sell_product_id,so1.is_gift");
		List<Record> records = Db.find(stringBuilder.toString(), wareHouseId, beginDate, endDate);
		List<carSalesPrintNeedInfo> carSalesPrintNeedInfos = new ArrayList<>();
		for (Record record : records) {
			carSalesPrintNeedInfo carSalesPrintNeedInfo = new carSalesPrintNeedInfo();
			carSalesPrintNeedInfo.setWareHouseName(record.getStr("wareHouseName"));
			carSalesPrintNeedInfo.setBarCode(record.getStr("bar_code"));
			carSalesPrintNeedInfo.setProductName(record.getStr("custom_name"));
			carSalesPrintNeedInfo.setBigCount(record.getInt("bigCount"));
			carSalesPrintNeedInfo.setIsGift(record.getInt("is_gift"));
			carSalesPrintNeedInfo.setSmallCount(record.getInt("smallCount"));
			carSalesPrintNeedInfo.setWareHousePhone(record.getStr("wareHousePhone"));
			carSalesPrintNeedInfo.setBigUnit(record.getStr("big_unit"));
			carSalesPrintNeedInfo.setSmallUnit(record.getStr("small_unit"));
			carSalesPrintNeedInfo.setProductAmout(record.getBigDecimal("product_amount"));
			carSalesPrintNeedInfos.add(carSalesPrintNeedInfo);
		}
		return carSalesPrintNeedInfos;
	}

	public List<Record> findReceivablesUserList(String sellerId, String startDate, String endDate, Integer status) {
		StringBuilder fromBuilder = new StringBuilder("SELECT u.realname, u.id FROM cc_sales_outstock o ");
		fromBuilder.append("LEFT JOIN cc_sales_order_join_outstock cj on cj.outstock_id = o.id ");
		fromBuilder.append("LEFT JOIN cc_sales_order cs on cs.id = cj.order_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = cs.biz_user_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);

		if (needWhere) {
			if (status != null) {
				fromBuilder.append(" where o.status = ? ");
				params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
			} else {
				fromBuilder.append(" where o.status != ? ");
				params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
			}
		} else {
			if (status != null) {
				fromBuilder.append("  AND o.status = ? ");
				params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
			} else {
				fromBuilder.append("  AND o.status != ? ");
				params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
			}
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}
		fromBuilder.append("GROUP BY u.id ");

		if (params.isEmpty())
			return Db.find(fromBuilder.toString());

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public List<Record> getUserPrintInfo(String[] stockOutId, String userId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" SELECT SUM(sod.tax_amount) AS taxAmount,SUM(sod.bigCount) as bigCount, SUM(sod.smallCount) as smallCount, sod.bar_code, SUM(sod.product_amount) as productAmout, sod.is_gift, u.realname, sod.custom_name,u.mobile ");
		stringBuilder.append(" FROM cc_sales_outstock c JOIN (SELECT floor(d.product_count / p.convert_relate ) AS bigCount, MOD (d.product_count, p.convert_relate ) ");
		stringBuilder.append(" AS smallCount, d.product_amount, sp.bar_code, d.outstock_id, d.is_gift, sp.custom_name,(sp.tax_price*d.product_count/ p.convert_relate) AS tax_amount ");
		stringBuilder.append(" FROM cc_sales_outstock_detail d INNER JOIN cc_seller_product sp ON d.sell_product_id = sp.id INNER ");
		stringBuilder.append(" JOIN cc_product p ON p.id = sp.product_id ) sod ON sod.outstock_id = c.id INNER JOIN cc_sales_order_join_outstock ");
		stringBuilder.append(" cj on cj.outstock_id = c.id INNER JOIN cc_sales_order s on s.id = cj.order_id INNER JOIN `user` u on u.id = s.biz_user_id ");
		stringBuilder.append(" where 1 = 1 ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(stringBuilder, "c.id", stockOutId, params, false);

		if (StrKit.notBlank(userId)) {
			stringBuilder.append(" and s.biz_user_id= ? GROUP BY sod.custom_name,sod.is_gift ");
			params.add(userId);
		}

		return Db.find(stringBuilder.toString(), params.toArray());

	}

	public SalesOutstock findOrderId(String orderId) {
		String sql = "select co.* from cc_sales_outstock co LEFT JOIN cc_sales_order_join_outstock coo on coo.outstock_id = co.id where coo.order_id = ?";
		return DAO.findFirst(sql, orderId);
	}

	public List<Record> findUserList(String sellerId, String startDate, String endDate) {
		StringBuilder fromBuilder = new StringBuilder("SELECT u.realname, u.id FROM cc_sales_outstock o ");
		fromBuilder.append("LEFT JOIN cc_sales_order_join_outstock cj on cj.outstock_id = o.id ");
		fromBuilder.append("LEFT JOIN cc_sales_order cs on cs.id = cj.order_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = cs.biz_user_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}
		fromBuilder.append("GROUP BY u.id ");

		if (params.isEmpty())
			return Db.find(fromBuilder.toString());

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	protected boolean generateQrcode(final String orderId,final Record order,final List<Record> orderDetailList, final String sellerCode) {
        boolean isSave =  Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				StringBuilder stringBuilder = new StringBuilder();
				String QRcontent="";
				String orderSn = order.getStr("order_sn");
				String orcodeFileName = orderSn + ".png";
				String childFileName = DateUtils.dateString();
				PathKit.getWebRootPath();
				String imagePath = PathKit.getWebRootPath() + "/";
				String newStr = imagePath + Consts.ORDER_QRCODE_PATH + childFileName ;

				String orcodeImgUrl = Consts.ORDER_QRCODE_PATH + childFileName +"/" +  orcodeFileName;
				Customer customer = CustomerQuery.me().findSellerCustomerId(order.getStr("customer_id"));
				stringBuilder.append(order.getStr("customer_id")).append("||" + orderSn).append("||" + customer.getCustomerName() + "||");					

				
               for (Record orderDetail : orderDetailList) {
               	SellerProduct sellerProduct = SellerProductQuery.me().findById(orderDetail.getStr("sell_product_id"));
               	Product product = ProductQuery.me().findById(sellerProduct.getProductId());
               	BigDecimal[] bigCount = orderDetail.getBigDecimal("product_amount").divideAndRemainder(orderDetail.getBigDecimal("product_price"));
       	     	stringBuilder.append("(" +product.getProductSn() + "," + bigCount[0] +")" + "|");
				}
               QRcontent = stringBuilder.toString().substring(0, stringBuilder.length() -1);
				Date date = new Date();
					org.ccloud.utils.QRCodeUtils.genQRCode(QRcontent, newStr, orcodeFileName);
	           		int i = SalesOrderQuery.me().updateQrcodeImgUrl(orcodeImgUrl, orderId, date);
	           		if (i < 0) {
						return false;
					}
				return true;
			}
		});
		return isSave;  
		
	}

	public Page<Record> _paginateForApp(int pageNumber, int pageSize, String keyword, String status,
	                                    String customerTypeId, String startDate, String endDate, String sellerId, String dataArea,String bizUserId) {
		String select = "select o.*, c.customer_name, c.contact as ccontact, c.mobile as cmobile, ct.name as customerTypeName, so.id as order_id, so.proc_inst_id";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock` o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append("left join cc_customer_type ct on o.customer_type_id = ct.id ");

		fromBuilder.append("left join cc_sales_order_join_outstock sojo on o.id = sojo.outstock_id ");
		fromBuilder.append("left join cc_sales_order so on sojo.order_id = so.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmpty(fromBuilder, "o.status", status, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "ct.name", customerTypeId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "so.biz_user_id", bizUserId, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1 ");
		}

		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(
					" and (o.outstock_sn like '%" + keyword + "%' or c.customer_name like '%" + keyword + "%')");
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
	
}
