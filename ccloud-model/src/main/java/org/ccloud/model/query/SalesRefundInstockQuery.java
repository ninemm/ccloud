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
import org.ccloud.model.Inventory;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.SalesRefundInstockDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

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
		return DAO.findById(id);
	}

	public Record findMoreById(final String id) {
		StringBuilder fromBuilder = new StringBuilder(
				" select o.*,c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName, u.realname, u.mobile, cp.factor ");
		fromBuilder.append(" from `cc_sales_refund_instock` o ");
		fromBuilder.append(" left join cc_seller_customer cs on o.customer_id = cs.id ");
		fromBuilder.append(" left join cc_customer c on cs.customer_id = c.id ");
		fromBuilder.append(" left join cc_customer_type ct on o.customer_type_id = ct.id ");
		fromBuilder.append(" left join cc_price_system cp on cp.id = ct.price_system_id ");
		fromBuilder.append(" left join user u on o.biz_user_id = u.id ");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate, String printStatus,String stockInStatus ,String dataArea) {
		String select = "select r.*, c.customer_name,c.contact,c.mobile,w.name as warehouseName,ct.name as customerTypeName,u.realname ";
		StringBuilder fromBuilder = new StringBuilder(" from `cc_sales_refund_instock` r");
		fromBuilder.append(" left join cc_seller_customer cc ON r.customer_id = cc.id ");
		fromBuilder.append(" left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append(" left join cc_warehouse w on r.warehouse_id = w.id ");
		fromBuilder.append(" left join cc_customer_type ct on r.customer_type_id = ct.id ");
		fromBuilder.append(" left join user u on r.biz_user_id = u.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1 ");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and r.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and r.create_date <= ?");
			params.add(endDate);
		}
		if (StrKit.notBlank(stockInStatus)) {
			fromBuilder.append(" and r.status = ?");
			params.add(stockInStatus);
		}
		
		if (StrKit.notBlank(printStatus)) {
			fromBuilder.append(" and r.is_print = ?");
			params.add(printStatus);
		}

		fromBuilder.append(" and ( r.instock_sn like '%"+keyword+"%' or c.customer_name like '%"+keyword+"%' ) order by r.create_date desc");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public boolean insert(Map<String, String[]> paraMap, String instockId, String instockSn, String sellerId,
			String userId, Date date, String deptId, String dataArea, String outStockId) {
		SalesRefundInstock salesRefundInstock = new SalesRefundInstock();
		
		salesRefundInstock.setId(instockId);
		salesRefundInstock.setInstockSn(instockSn);
		salesRefundInstock.setWarehouseId(StringUtils.getArrayFirst(paraMap.get("warehouseId")));
		salesRefundInstock.setSellerId(sellerId);
		salesRefundInstock.setCustomerId(StringUtils.getArrayFirst(paraMap.get("customerId")));
		salesRefundInstock.setCustomerTypeId(StringUtils.getArrayFirst(paraMap.get("customerType")));
		salesRefundInstock.setBizUserId(StringUtils.getArrayFirst(paraMap.get("biz_user_id")));
		salesRefundInstock.setInputUserId(userId);
		salesRefundInstock.setStatus(Consts.SALES_REFUND_INSTOCK_DEFUALT);
		salesRefundInstock.setOutstockId(outStockId);
		String total = StringUtils.getArrayFirst(paraMap.get("total"));
		String type = StringUtils.getArrayFirst(paraMap.get("paymentType"));
		
		salesRefundInstock.setTotalRejectAmount(new BigDecimal(total));
		salesRefundInstock.setPaymentType(StringUtils.isNumeric(type)? Integer.parseInt(type) : 0);
		salesRefundInstock.setRemark(StringUtils.getArrayFirst(paraMap.get("remark")));
		salesRefundInstock.setCreateDate(date);
		salesRefundInstock.setDeptId(deptId);
		salesRefundInstock.setDataArea(dataArea);
		
		return salesRefundInstock.save();
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

	public boolean inStock(Map<String, String[]> paraMap, String sellerId, Date date, String deptId, String dataArea,
			Integer index, String userId, String inStockSN, String wareHouseId, String sellerProductId) {
		SalesRefundInstockDetail detail = SalesRefundInstockDetailQuery.me().
				findById(StringUtils.getArrayFirst(paraMap.get("outstockDetailId" + index)));
		String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
		String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
		String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));
		String price = StringUtils.getArrayFirst(paraMap.get("bigPrice" + index));
		
		Integer bigCount = Integer.valueOf(bigNum);
		Integer productConvert = Integer.valueOf(convert);
		Integer smallCount = Integer.valueOf(smallNum);
		
		Integer productCount = bigCount * productConvert + smallCount;
		String productAmount = StringUtils.getArrayFirst(paraMap.get("rowTotal" + index));
		BigDecimal productPrice = new BigDecimal(price);
		
		detail.setRejectProductCount(productCount);
		detail.setRejectAmount(new BigDecimal(productAmount));
		detail.setRejectProductPrice(productPrice);
		detail.setModifyDate(date);
		
		
		if (!detail.update()) {
			return false;
		}
		
		BigDecimal smallStoreCount = new BigDecimal(smallCount).divide(new BigDecimal(productConvert), 2, BigDecimal.ROUND_HALF_UP);
		
		String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
		Inventory inventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId, productId, wareHouseId);
		if (inventory == null) {
			return false;
		}
		inventory.setInCount(inventory.getInCount().add(new BigDecimal(bigCount)).add(smallStoreCount));
		inventory.setInAmount(inventory.getInAmount().add(detail.getProductAmount()));
		inventory.setBalanceCount(inventory.getBalanceCount().add(new BigDecimal(bigCount))
				.add(smallStoreCount));
		inventory.setBalanceAmount(inventory.getBalanceAmount().add(detail.getProductAmount()));
		inventory.setModifyDate(date);
		
		if (!inventory.update()) {
			return false;
		}
		
		InventoryDetail oldDetail = InventoryDetailQuery.me().findBySellerProductId(sellerProductId, wareHouseId);
		InventoryDetail inventoryDetail = new InventoryDetail();
		inventoryDetail.setId(StrKit.getRandomUUID());
		inventoryDetail.setWarehouseId(inventory.getWarehouseId());
		inventoryDetail.setSellProductId(detail.getSellProductId());
		inventoryDetail.setInAmount(detail.getProductAmount());
		inventoryDetail.setInCount(new BigDecimal(bigCount).add(smallStoreCount));
		inventoryDetail.setInPrice(inventory.getBalancePrice());
		inventoryDetail.setBalanceAmount(oldDetail.getBalanceAmount().add(detail.getProductAmount()));
		inventoryDetail.setBalanceCount(oldDetail.getBalanceCount().add(new BigDecimal(bigCount))
				.add(smallStoreCount));
		inventoryDetail.setBalancePrice(oldDetail.getBalancePrice());
		inventoryDetail.setBizBillSn(inStockSN);
		inventoryDetail.setBizDate(detail.getCreateDate());
		inventoryDetail.setBizType(Consts.BIZ_TYPE_SALES_REFUND_INSTOCK);
		inventoryDetail.setBizUserId(userId);
		inventoryDetail.setDeptId(deptId);
		inventoryDetail.setDataArea(dataArea);
		inventoryDetail.setCreateDate(date);
		
		if (!inventoryDetail.save()) {
			return false;
		}
		
		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		sellerProduct.setStoreCount(sellerProduct.getStoreCount().add(new BigDecimal(bigCount))
				.add(smallStoreCount));
		sellerProduct.setModifyDate(date);
		if (!sellerProduct.update()) {
			return false;
		}
		
		return true;
	}

	public boolean updateStatus(String inStockId, Date date) {
		SalesRefundInstock sales = this.findById(inStockId);
		List<SalesRefundInstockDetail> list = SalesRefundInstockDetailQuery.me().findByInId(inStockId);
		boolean status = true;
		for (SalesRefundInstockDetail detail : list) {
			if (detail.getRejectProductCount() != detail.getProductCount()) {
				status = false;
				break;
			}
		}
		
		if (status) {
			sales.setStatus(Consts.SALES_REFUND_INSTOCK_ALL_OUT);
		} else {
			sales.setStatus(Consts.SALES_REFUND_INSTOCK_PART_OUT);
		}
		sales.setModifyDate(date);
		
		if (!sales.update()) {
			return false;
		}
		return true;
	}

	public int updateConfirm(String inStockId, int status, Date date) {
		return Db.update(
				"update cc_sales_refund_instock set status = ?, modify_date = ? where id = ?",
				status, date, inStockId);
	}

	public String getNewSn(String sellerId) {
		String sql = "SELECT s.instock_sn FROM cc_sales_refund_instock s WHERE date(s.create_date) = curdate() AND s.seller_id = ? ORDER BY s.create_date desc";
		SalesRefundInstock sales = DAO.findFirst(sql, sellerId);
		String SN = "";
		if (sales == null || StringUtils.isBlank(sales.getInstockSn())) {
			SN = Consts.SALES_ORDER_SN;
		} else {
			String endSN = StringUtils.substringSN(Consts.SALES_ORDER_SN, sales.getInstockSn());
			SN = new BigDecimal(endSN).add(new BigDecimal(1)).toString();
		}
		return SN;
	}

	public SalesRefundInstock insertByApp(String instockId, Record record, String userId,
			String sellerId, String sellerCode, String paymentType, Date date, String remark) {
		String newSn = SalesRefundInstockQuery.me().getNewSn(record.getStr("seller_id"));
		// SR + (机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
		String instockSn = "SR" + sellerCode +  record.get("customerTypeCode")
		+ record.get("warehouseCode")+ DateUtils.format("yyMMdd", new Date()) + newSn;
		
		SalesRefundInstock salesRefundInstock = new SalesRefundInstock();
		
		salesRefundInstock.setId(instockId);
		salesRefundInstock.setInstockSn(instockSn);
		salesRefundInstock.setWarehouseId(record.getStr("warehouse_id"));
		salesRefundInstock.setSellerId(sellerId);
		salesRefundInstock.setCustomerId(record.getStr("customer_id"));
		salesRefundInstock.setCustomerTypeId(record.getStr("customer_type_id"));
		salesRefundInstock.setBizUserId(record.getStr("biz_user_id"));
		salesRefundInstock.setInputUserId(userId);
		salesRefundInstock.setStatus(Consts.SALES_REFUND_INSTOCK_DEFUALT);
		salesRefundInstock.setOutstockId(record.getStr("id"));
		
		salesRefundInstock.setPaymentType(StringUtils.isNumeric(paymentType)? Integer.parseInt(paymentType) : 1);
		salesRefundInstock.setCreateDate(date);
		salesRefundInstock.setDeptId(record.getStr("dept_id"));
		salesRefundInstock.setDataArea(record.getStr("data_area"));
		salesRefundInstock.setRemark(remark);
		
		return salesRefundInstock;
		
	}

	public List<Record> findByOutstockId(String outstockId) {
		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id, sp.product_id, t1.valueName ");
		sqlBuilder.append(" from `cc_sales_refund_instock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail cd ON cd.id = sod.outstock_detail_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock co ON co.id = cd.outstock_id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append(" WHERE co.id = ? ");

		return Db.find(sqlBuilder.toString(), outstockId);
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String status,
			String customerTypeId, String startDate, String endDate, String sellerId, String dataArea) {
		String select = "select o.*, c.customer_name, ct.name as customerTypeName, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, ct.name as customerTypeName ";
		StringBuilder fromBuilder = new StringBuilder("from cc_sales_refund_instock o ");
		fromBuilder.append("left join cc_seller_customer cc ON o.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer c on cc.customer_id = c.id ");
		fromBuilder.append("left join cc_customer_type ct on o.customer_type_id = ct.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.instock_sn", keyword, params, needWhere);
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
	
	public SalesRefundInstock findBySn(String refundSn){
		String sql = "select * from cc_sales_refund_instock where instock_sn = ?";
		return DAO.findFirst(sql, refundSn);
	}
	
	public  Record findStockInForPrint(String inStockId) {
	StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append(" SELECT sr.id as salesRefundInstockId, sr.instock_sn, sr.payment_type, sr.remark AS stockInRemark, o.delivery_address, sr.total_reject_amount, ");
	stringBuilder.append(" cs.customer_kind, c.id AS customerId, c.customer_name, c.contact AS ccontact, c.mobile AS cmobile, c.address AS ");
	stringBuilder.append(" caddress, ct. NAME AS customerTypeName, ct. CODE AS customerTypeCode, u.realname, u.mobile, w. CODE AS ");
	stringBuilder.append(" warehouseCode, cp.factor, w.`name` AS warehouseName, w.phone AS warehousePhone, sr.create_date AS returnOrderTime, ");
	stringBuilder.append(" so.remark, sn.seller_name as sellerName, so.total_amount, so.id AS orderId, so.biz_user_id, o.id AS salesOutStockId, sn.id AS sellerId, pt.context AS printFootContext ");
	stringBuilder.append(" FROM cc_sales_refund_instock  sr LEFT JOIN cc_sales_outstock o on o.id = sr.outstock_id LEFT JOIN cc_seller_customer cs ON o.customer_id = cs.id LEFT JOIN cc_sales_order_join_outstock sj ");
	stringBuilder.append(" ON sj.outstock_id = o.id LEFT JOIN cc_customer c ON cs.customer_id = c.id LEFT JOIN cc_sales_order so ON so.id = ");
	stringBuilder.append(" sj.order_id LEFT JOIN cc_seller sn ON sn.id = so.seller_id LEFT JOIN cc_customer_type ct ON o.customer_type_id = ");
	stringBuilder.append(" ct.id LEFT JOIN cc_price_system cp ON cp.id = ct.price_system_id LEFT JOIN USER u ON so.biz_user_id = u.id LEFT ");
	stringBuilder.append(" JOIN cc_warehouse w ON o.warehouse_id = w.id LEFT JOIN cc_seller_join_template cjt ON cjt.seller_id = sn.id LEFT ");
	stringBuilder.append(" JOIN cc_print_template pt ON pt.id = cjt.print_template_id WHERE sr.id = ? ");
	
	return Db.findFirst(stringBuilder.toString(), inStockId);
	}
	
	
	public List<Record> findPrintProductInfo(String inStockId) {
	   StringBuilder stringBuilder = new StringBuilder();
	   stringBuilder.append(" SELECT sod.id as refundInstockDetailId, sod.refund_instock_id, sod.is_gift, sod.sell_product_id, sp.custom_name, p.big_unit, p.small_unit, ");
	   stringBuilder.append(" p.convert_relate, sp.seller_id, sp.product_id, t1.valueName, sp.bar_code, sod.reject_product_price as big_Price, CONVERT ( ");
	   stringBuilder.append(" sod.reject_product_price / p.convert_relate, DECIMAL (18, 2) ) AS small_price, floor(sod.reject_product_count / ");
	   stringBuilder.append(" p.convert_relate ) AS bigCount, MOD (sod.reject_product_count, p.convert_relate ) AS smallCount, sod.reject_amount, sod.reject_product_count, cso.warehouse_id ");
	   stringBuilder.append(" FROM `cc_sales_refund_instock_detail` sod LEFT JOIN cc_sales_refund_instock cso ON cso.id = sod.refund_instock_id LEFT JOIN cc_seller_product sp ON ");
	   stringBuilder.append(" sod.sell_product_id = sp.id LEFT JOIN cc_product p ON sp.product_id = p.id LEFT JOIN (SELECT sv.id, ");
	   stringBuilder.append(" cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv RIGHT JOIN ");
	   stringBuilder.append(" cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id ");
	   stringBuilder.append(" ) t1 ON t1.product_set_id = p.id WHERE sod.refund_instock_id = ? ");
		
	   return Db.find(stringBuilder.toString(),inStockId);
	}

	public boolean updatePrintStatus(String id) {
		String sql = "update cc_sales_refund_instock cc set is_print = 1 where cc.id = '"+id+"'";
		int i = Db.update(sql);
		return (i > 0) ? true : false;
	}
	
	
	public boolean updateStockInStatus(String id, String userId, Date stockInDate,int salesInStockStatusOut,Date modifyDate, String remark) {
		String sql = "update cc_sales_refund_instock cc set cc.biz_user_id=? , cc.biz_date=? , cc.status = ? , cc.modify_date = ?,cc.remark = ? where cc.id = ?";
		int i = Db.update(sql, userId, stockInDate, salesInStockStatusOut, modifyDate,remark, id);
		return (i > 0) ? true : false;
	}
}
