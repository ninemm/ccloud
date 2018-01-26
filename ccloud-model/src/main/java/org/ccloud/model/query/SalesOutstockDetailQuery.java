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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.*;
import org.ccloud.model.vo.orderProductInfo;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesOutstockDetailQuery extends JBaseQuery {

	protected static final SalesOutstockDetail DAO = new SalesOutstockDetail();
	private static final SalesOutstockDetailQuery QUERY = new SalesOutstockDetailQuery();

	public static SalesOutstockDetailQuery me() {
		return QUERY;
	}
	
	public List<Record> findByOutstockId(String outstockId) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id,sp.bar_code, sp.product_id, t1.valueName, cs.is_composite, IFNULL(t2.refundCount,0) as refundCount ");
		sqlBuilder.append(" from `cc_sales_outstock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_detail cs ON sod.order_detail_id = cs.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append("LEFT JOIN (SELECT IFNULL(SUM(cr.reject_product_count),0) as refundCount,cr.outstock_detail_id FROM cc_sales_refund_instock_detail cr ");
		sqlBuilder.append("LEFT JOIN cc_sales_refund_instock cri on cri.id = cr.refund_instock_id where cri.status != ? ");
		sqlBuilder.append("GROUP BY cr.outstock_detail_id) t2 on t2.outstock_detail_id = sod.id ");
		sqlBuilder.append(" WHERE sod.outstock_id = ? ");

		return Db.find(sqlBuilder.toString(), Consts.SALES_REFUND_INSTOCK_CANCEL, outstockId);
	}
	
	public List<Record> findByOutstockSn(String sn) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id, sp.product_id, t1.valueName, cs.is_composite, IFNULL(t2.refundCount,0) as refundCount ");
		sqlBuilder.append(" from `cc_sales_outstock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso ON cso.id = sod.outstock_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_detail cs ON sod.order_detail_id = cs.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append("LEFT JOIN (SELECT IFNULL(SUM(cr.reject_product_count),0) as refundCount,cr.outstock_detail_id FROM cc_sales_refund_instock_detail cr ");
		sqlBuilder.append("LEFT JOIN cc_sales_refund_instock cri on cri.id = cr.refund_instock_id where cri.status != ? ");
		sqlBuilder.append("GROUP BY cr.outstock_detail_id) t2 on t2.outstock_detail_id = sod.id ");
		sqlBuilder.append(" WHERE cso.outstock_sn = ? ");

		return Db.find(sqlBuilder.toString(), Consts.SALES_REFUND_INSTOCK_CANCEL, sn);
	}	
	
	public List<Record> getPrintDetailById(String outstockId) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id, sp.product_id, t1.valueName, cs.is_composite, IFNULL(t2.refundCount,0) as refundCount ");
		sqlBuilder.append(" from `cc_sales_outstock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_detail cs ON sod.order_detail_id = cs.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append("LEFT JOIN (SELECT SUM(cr.reject_product_count) as refundCount,cr.outstock_detail_id FROM cc_sales_refund_instock_detail cr GROUP BY cr.outstock_detail_id) t2 ");
		sqlBuilder.append("on t2.outstock_detail_id = sod.id ");
		sqlBuilder.append(" WHERE sod.outstock_id = ? ");

		return Db.find(sqlBuilder.toString(), outstockId);
	}

	public BigDecimal insert(String outstockId, Record orderDetail, Date date, Record order) {
		SalesOutstockDetail detail = new SalesOutstockDetail();
		detail.setId(StrKit.getRandomUUID());
		detail.setOutstockId(outstockId);
		detail.setSellProductId(orderDetail.getStr("sell_product_id"));
		detail.setProductCount(orderDetail.getInt("product_count"));
		detail.setProductPrice(orderDetail.getBigDecimal("product_price"));
		detail.setProductAmount(orderDetail.getBigDecimal("product_amount"));
		detail.setIsGift(orderDetail.getInt("is_gift"));
		detail.setOrderDetailId(orderDetail.getStr("id"));
		detail.setCreateDate(date);
		detail.setDeptId(orderDetail.getStr("dept_id"));
		detail.setDataArea(orderDetail.getStr("data_area"));
		
//		ReceivablesDetail receivablesDetail = new ReceivablesDetail();
//		receivablesDetail.setId(StrKit.getRandomUUID());
//		receivablesDetail.setObjectId(order.getStr("customer_id"));
//		receivablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
//		receivablesDetail.setReceiveAmount(detail.getProductAmount());
//		receivablesDetail.setActAmount(new BigDecimal(0));
//		receivablesDetail.setBalanceAmount(detail.getProductAmount());
//		receivablesDetail.setBizDate(date);
//		receivablesDetail.setRefSn(order.getStr("order_sn"));
//		receivablesDetail.setRefType(Consts.BIZ_TYPE_SALES_ORDER);
//		receivablesDetail.setDeptId(orderDetail.getStr("dept_id"));
//		receivablesDetail.setDataArea(orderDetail.getStr("data_area"));
//		receivablesDetail.setCreateDate(date);
		
		
//		return detail.save() && receivablesDetail.save();
		detail.save();
		BigDecimal totalMoney = orderDetail.getBigDecimal("product_amount");
		if (detail.getIsGift() == 1) {
			totalMoney = new BigDecimal(0);
		}
		return totalMoney;
		
	}
	
	
	public List<orderProductInfo> findPrintProductInfo(String outstockId) {
		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.outstock_id,sod.is_gift, sod.sell_product_id,sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id, sp.product_id, t1.valueName, cs.is_composite,sp.bar_code,sod.product_price,CONVERT( sod.product_price/p.convert_relate,decimal(18,2)) as small_price, ");
		sqlBuilder.append(" floor(sod.product_count/p.convert_relate) as bigCount,MOD(sod.product_count,p.convert_relate) as smallCount,sod.product_amount,sod.product_count,sod.id as salesOutDetaliId,cso.warehouse_id,sp.product_id as productId ");
		sqlBuilder.append(" from `cc_sales_outstock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso on cso.id = sod.outstock_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_detail cs ON sod.order_detail_id = cs.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append(" WHERE sod.outstock_id = ? ");

		List<Record> records = Db.find(sqlBuilder.toString(), outstockId);
		List<orderProductInfo> orderProductInfos = new ArrayList<>();
		for (Record record : records) {
			orderProductInfo orderProductInfo = new orderProductInfo();
			orderProductInfo.setProductName(record.getStr("custom_name"));//产品名称		
			orderProductInfo.setBarCode(record.getStr("bar_code"));//条码
			orderProductInfo.setBigUnit(record.getStr("big_unit"));//产品大单位
			orderProductInfo.setSmallUnit(record.getStr("small_unit"));//产品小单位
			orderProductInfo.setConvertRelate(record.getInt("convert_relate"));//换算关系
			orderProductInfo.setBigPrice(record.getBigDecimal("product_price"));//大单位价格
			orderProductInfo.setSmallPrice(record.getBigDecimal("small_price"));//小单位价格
			orderProductInfo.setBigCount(record.getInt("bigCount"));
			orderProductInfo.setSmallCount(record.getInt("smallCount"));
			orderProductInfo.setIsgift(record.getInt("is_gift"));
			orderProductInfo.setProductAmout(record.getBigDecimal("product_amount"));
			orderProductInfo.setProductCount(record.getInt("product_count"));
			orderProductInfo.setSellerProductId(record.getStr("sell_product_id"));
			orderProductInfo.setOutStockId(record.getStr("outstock_id"));
			orderProductInfo.setSalesOutDetaliId(record.getStr("salesOutDetaliId"));
			orderProductInfo.setWareHouseId(record.getStr("warehouse_id"));
			orderProductInfo.setProductId(record.getStr("productId"));
			orderProductInfos.add(orderProductInfo);
		}
		 return orderProductInfos;
	}

	public SalesOutstockDetail findById(final String id) {
		return DAO.findById(id);
	}

	public Page<SalesOutstockDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_outstock_detail` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
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

	public boolean outStock(Map<String, String[]> paraMap, String sellerId, Date date, String deptId, String dataArea,
	                        Integer index, String userId, String outStockSN, String wareHouseId, String sellerProductId, String customerId,
			                String order_user, String order_date) {
		SalesOutstockDetail detail = SalesOutstockDetailQuery.me().
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
		
		detail.setProductCount(productCount);
		detail.setProductAmount(new BigDecimal(productAmount));
		detail.setModifyDate(date);
		
		if (!detail.update()) {
			return false;
		}
		
		BigDecimal smallStoreCount = (new BigDecimal(smallCount)).divide(new BigDecimal(productConvert), 2, BigDecimal.ROUND_HALF_UP);
		
		String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
		Inventory inventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId, productId, wareHouseId);
		if (inventory == null) {
			return false;
		}
		BigDecimal oldOutCount = inventory.getOutCount() == null? new BigDecimal(0) : inventory.getOutCount();
		BigDecimal oldOutAmount = inventory.getOutAmount() == null? new BigDecimal(0) : inventory.getOutAmount();
		//BigDecimal oldOutPrice = inventory.getOutPrice() == null? new BigDecimal(0) : inventory.getOutPrice();
		BigDecimal oldBalanceAmount = inventory.getBalanceAmount() == null? new BigDecimal(0) : inventory.getBalanceAmount();
		BigDecimal oldBalanceCount = inventory.getBalanceCount() == null? new BigDecimal(0) : inventory.getBalanceCount();
				
		inventory.setOutCount(oldOutCount.add(new BigDecimal(bigCount)).add(smallStoreCount));
		inventory.setOutAmount(oldOutAmount.add(detail.getProductAmount()));
		inventory.setOutPrice(productPrice);
		inventory.setBalanceCount(oldBalanceCount.subtract(new BigDecimal(bigCount))
				.subtract(smallStoreCount));
		inventory.setBalanceAmount(oldBalanceAmount.subtract(detail.getProductAmount()));
		inventory.setModifyDate(date);
		
		if (!inventory.update()) {
			return false;
		}
		
		InventoryDetail oldDetail = InventoryDetailQuery.me().findBySellerProductId(sellerProductId, wareHouseId);
		InventoryDetail inventoryDetail = new InventoryDetail();
		inventoryDetail.setId(StrKit.getRandomUUID());
		inventoryDetail.setWarehouseId(inventory.getWarehouseId());
		inventoryDetail.setSellProductId(detail.getSellProductId());
		inventoryDetail.setOutAmount(detail.getProductAmount());
		inventoryDetail.setOutCount((new BigDecimal(bigCount)).add(smallStoreCount));
		inventoryDetail.setOutPrice(inventory.getOutPrice());
		inventoryDetail.setBalanceAmount(oldDetail.getBalanceAmount().subtract(detail.getProductAmount()));
		inventoryDetail.setBalanceCount(oldDetail.getBalanceCount().subtract(new BigDecimal(bigCount))
				.subtract(smallStoreCount));
		inventoryDetail.setBalancePrice(oldDetail.getBalancePrice());
		inventoryDetail.setBizBillSn(outStockSN);
		inventoryDetail.setBizDate(detail.getCreateDate());
		inventoryDetail.setBizType(Consts.BIZ_TYPE_SALES_OUTSTOCK);
		inventoryDetail.setBizUserId(userId);
		inventoryDetail.setDeptId(deptId);
		inventoryDetail.setDataArea(dataArea);
		inventoryDetail.setCreateDate(date);
		
		if (!inventoryDetail.save()) {
			return false;
		}
		
		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		sellerProduct.setStoreCount(sellerProduct.getStoreCount().subtract(new BigDecimal(bigCount))
				.subtract(smallStoreCount));
		sellerProduct.setModifyDate(date);
		if (!sellerProduct.update()) {
			return false;
		}
		
		SalesOrderDetail salesOrderDetail = SalesOrderDetailQuery.me().findById(detail.getOrderDetailId());
		salesOrderDetail.setOutCount(salesOrderDetail.getOutCount() + productCount);
		salesOrderDetail.setLeftCount(salesOrderDetail.getLeftCount() - productCount);
		salesOrderDetail.setModifyDate(date);
		if (!salesOrderDetail.update()) {
			return false;
		}
		
		
		ReceivablesDetail receivablesDetail = new ReceivablesDetail();
		receivablesDetail.setId(StrKit.getRandomUUID());
		receivablesDetail.setObjectId(customerId);
		receivablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
		receivablesDetail.setReceiveAmount(detail.getProductAmount());
		receivablesDetail.setActAmount(new BigDecimal(0));
		receivablesDetail.setBalanceAmount(detail.getProductAmount());
		receivablesDetail.setBizDate(date);
		receivablesDetail.setRefSn(outStockSN);
		receivablesDetail.setRefType(Consts.BIZ_TYPE_SALES_ORDER);
		receivablesDetail.setDeptId(deptId);
		receivablesDetail.setDataArea(dataArea);
		receivablesDetail.setCreateDate(date);		
		
		if (!receivablesDetail.save()) {
			return false;
		}

		//更新计划
		BigDecimal bigProductCount = new BigDecimal(bigCount).add(new BigDecimal(smallCount).divide(new BigDecimal(productConvert)));
		if (!updatePlans(order_user, sellerProductId, order_date, bigProductCount)) {
			return false;
		}
		
		return true;
	}

	private boolean updatePlans(String order_user, String sellerProductId, String orderDate, BigDecimal productCount) {

		List<Plans> plans = PlansQuery.me().findBySales(order_user, sellerProductId, orderDate.substring(0,10));
		for (Plans plan : plans) {
			BigDecimal planNum = plan.getPlanNum();
			BigDecimal completeNum = (productCount.add(plan.getCompleteNum())).setScale(2, BigDecimal.ROUND_HALF_UP);
			plan.setCompleteNum(completeNum);
			plan.setCompleteRatio(completeNum.multiply(new BigDecimal(100)).divide(planNum, 2, BigDecimal.ROUND_HALF_UP));
			plan.setModifyDate(new Date());
			if(!plan.update()){
				return  false;
			}
		}

		return true;
	}

	//批量出库
	public boolean batchOutStock(List<orderProductInfo> orderProductInfos, String sellerId, Date date, String deptId,String dataArea,String userId, String outStockSN, String customerId
								,String order_user, String order_date) {
		for (orderProductInfo orderProductInfo : orderProductInfos) {
			SalesOutstockDetail detail = SalesOutstockDetailQuery.me().findById(orderProductInfo.getSalesOutDetaliId());
			if (!detail.saveOrUpdate()) {
				return false;
			}
			
			BigDecimal smallStoreCount = (new BigDecimal(orderProductInfo.getSmallCount())).divide(new BigDecimal(orderProductInfo.getConvertRelate()), 2, BigDecimal.ROUND_HALF_UP);

			Inventory inventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId, orderProductInfo.getProductId(), orderProductInfo.getWareHouseId());
			if (inventory == null) {
				return false;
			}
			BigDecimal oldOutCount = inventory.getOutCount() == null? new BigDecimal(0) : inventory.getOutCount();
			BigDecimal oldOutAmount = inventory.getOutAmount() == null? new BigDecimal(0) : inventory.getOutAmount();
			//BigDecimal oldOutPrice = inventory.getOutPrice() == null? new BigDecimal(0) : inventory.getOutPrice();
			BigDecimal oldBalanceAmount = inventory.getBalanceAmount() == null? new BigDecimal(0) : inventory.getBalanceAmount();
			BigDecimal oldBalanceCount = inventory.getBalanceCount() == null? new BigDecimal(0) : inventory.getBalanceCount();
					
			inventory.setOutCount(oldOutCount.add(new BigDecimal(orderProductInfo.getBigCount())).add(smallStoreCount));
			inventory.setOutAmount(oldOutAmount.add(detail.getProductAmount()));
			inventory.setOutPrice(orderProductInfo.getBigPrice());
			inventory.setBalanceCount(oldBalanceCount.subtract(new BigDecimal(orderProductInfo.getBigCount()))
					.subtract(smallStoreCount));
			inventory.setBalanceAmount(oldBalanceAmount.subtract(detail.getProductAmount()));
			inventory.setModifyDate(new Date());
			
			if (!inventory.saveOrUpdate()) {
				return false;
			}
			
			InventoryDetail oldDetail = InventoryDetailQuery.me().findBySellerProductId(orderProductInfo.getSellerProductId(), orderProductInfo.getWareHouseId());
			InventoryDetail inventoryDetail = new InventoryDetail();
			inventoryDetail.setId(StrKit.getRandomUUID());
			inventoryDetail.setWarehouseId(inventory.getWarehouseId());
			inventoryDetail.setSellProductId(detail.getSellProductId());
			inventoryDetail.setOutAmount(detail.getProductAmount());
			inventoryDetail.setOutCount((new BigDecimal(orderProductInfo.getBigCount())).add(smallStoreCount));
			inventoryDetail.setOutPrice(inventory.getOutPrice());
			inventoryDetail.setBalanceAmount(oldDetail.getBalanceAmount().subtract(detail.getProductAmount()));
			inventoryDetail.setBalanceCount(oldDetail.getBalanceCount().subtract(new BigDecimal(orderProductInfo.getBigCount()))
					.subtract(smallStoreCount));
			inventoryDetail.setBalancePrice(oldDetail.getBalancePrice());
			inventoryDetail.setBizBillSn(outStockSN);
			inventoryDetail.setBizDate(detail.getCreateDate());
			inventoryDetail.setBizType(Consts.BIZ_TYPE_SALES_OUTSTOCK);
			inventoryDetail.setBizUserId(userId);
			inventoryDetail.setDeptId(deptId);
			inventoryDetail.setDataArea(dataArea);
			inventoryDetail.setCreateDate(new Date());
			
			if (!inventoryDetail.save()) {
				return false;
			}
			
			SellerProduct sellerProduct = SellerProductQuery.me().findById(orderProductInfo.getSellerProductId());
			sellerProduct.setStoreCount(sellerProduct.getStoreCount().subtract(new BigDecimal(orderProductInfo.getBigCount()))
					.subtract(smallStoreCount));
			sellerProduct.setModifyDate(new Date());
			if (!sellerProduct.update()) {
				return false;
			}
			
			SalesOrderDetail salesOrderDetail = SalesOrderDetailQuery.me().findById(detail.getOrderDetailId());
			salesOrderDetail.setOutCount(salesOrderDetail.getOutCount() + orderProductInfo.getProductCount());
			salesOrderDetail.setLeftCount(salesOrderDetail.getLeftCount() - orderProductInfo.getProductCount());
			salesOrderDetail.setModifyDate(new Date());
			if (!salesOrderDetail.update()) {
				return false;
			}
			
			ReceivablesDetail receivablesDetail = new ReceivablesDetail();
			receivablesDetail.setId(StrKit.getRandomUUID());
			receivablesDetail.setObjectId(customerId);
			receivablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
			receivablesDetail.setReceiveAmount(detail.getProductAmount());
			receivablesDetail.setActAmount(new BigDecimal(0));
			receivablesDetail.setBalanceAmount(detail.getProductAmount());
			receivablesDetail.setBizDate(date);
			receivablesDetail.setRefSn(outStockSN);
			receivablesDetail.setRefType(Consts.BIZ_TYPE_SALES_ORDER);
			receivablesDetail.setDeptId(deptId);
			receivablesDetail.setDataArea(dataArea);
			receivablesDetail.setCreateDate(date);		
			
			if (!receivablesDetail.save()) {
				return false;
			}

			//更新计划
			BigDecimal bigProductCount = new BigDecimal(orderProductInfo.getBigCount()).add(new BigDecimal(orderProductInfo.getSmallCount()).divide(new BigDecimal(orderProductInfo.getConvertRelate())));
			if (!updatePlans(order_user, orderProductInfo.getSellerProductId(), order_date, bigProductCount)) {
				return false;
			}
		}
		
		return true;
	}
	
	
}
