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
import org.ccloud.model.ReceivablesDetail;
import org.ccloud.model.SalesOrderDetail;
import org.ccloud.model.SalesOutstockDetail;
import org.ccloud.model.SellerProduct;
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
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id, sp.product_id, t1.valueName");
		sqlBuilder.append(" from `cc_sales_outstock_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");		
		sqlBuilder.append(" WHERE sod.outstock_id = ? ");

		return Db.find(sqlBuilder.toString(), outstockId);
	}

	public boolean insert(String outstockId, Record orderDetail, Date date, Record order) {
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
		
		ReceivablesDetail receivablesDetail = new ReceivablesDetail();
		receivablesDetail.setId(StrKit.getRandomUUID());
		receivablesDetail.setObjectId(order.getStr("customer_id"));
		receivablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
		receivablesDetail.setReceiveAmount(detail.getProductAmount());
		receivablesDetail.setActAmount(new BigDecimal(0));
		receivablesDetail.setBalanceAmount(detail.getProductAmount());
		receivablesDetail.setBizDate(date);
		receivablesDetail.setRefSn(order.getStr("order_sn"));
		receivablesDetail.setRefType(Consts.BIZ_TYPE_SALES_ORDER);
		receivablesDetail.setDeptId(orderDetail.getStr("dept_id"));
		receivablesDetail.setDataArea(orderDetail.getStr("data_area"));
		receivablesDetail.setCreateDate(date);
		
		return detail.save() && receivablesDetail.save();
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

	public boolean outStock(Map<String, String[]> paraMap, String sellerId, Date date, String deptId,
			String dataArea, Integer index, String userId, String outStockSN, String wareHouseId, String sellerProductId) {
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
		
		BigDecimal smallStoreCount = new BigDecimal(smallCount).divide(new BigDecimal(productConvert), 2, BigDecimal.ROUND_HALF_UP);
		
		String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
		Inventory inventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId, productId, wareHouseId);
		if (inventory == null) {
			return false;
		}
		inventory.setOutCount(inventory.getOutCount().add(new BigDecimal(bigCount)).add(smallStoreCount));
		inventory.setOutAmount(inventory.getOutAmount().add(detail.getProductAmount()));
		inventory.setOutPrice(productPrice);
		inventory.setBalanceCount(inventory.getBalanceCount().subtract(new BigDecimal(bigCount))
				.subtract(smallStoreCount));
		inventory.setBalanceAmount(inventory.getBalanceAmount().subtract(detail.getProductAmount()));
		inventory.setModifyDate(date);
		
		if (!inventory.update()) {
			return false;
		}
		
		InventoryDetail inventoryDetail = new InventoryDetail();
		inventoryDetail.setId(StrKit.getRandomUUID());
		inventoryDetail.setWarehouseId(inventory.getWarehouseId());
		inventoryDetail.setSellProductId(detail.getSellProductId());
		inventoryDetail.setOutAmount(detail.getProductAmount());
		inventoryDetail.setOutCount(new BigDecimal(bigCount).add(smallStoreCount));
		inventoryDetail.setOutPrice(inventory.getOutPrice());
		inventoryDetail.setBalanceAmount(inventory.getBalanceAmount());
		inventoryDetail.setBalanceCount(inventory.getBalanceCount());
		inventoryDetail.setBalancePrice(inventory.getBalancePrice());
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
		
		return true;
	}

}
