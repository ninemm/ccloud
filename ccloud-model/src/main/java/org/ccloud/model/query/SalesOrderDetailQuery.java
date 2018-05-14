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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.SalesOrderDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesOrderDetailQuery extends JBaseQuery {

	protected static final SalesOrderDetail DAO = new SalesOrderDetail();
	private static final SalesOrderDetailQuery QUERY = new SalesOrderDetailQuery();

	public static SalesOrderDetailQuery me() {
		return QUERY;
	}

	public List<Record> findByOrderId(String orderId) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.seller_id, sp.custom_name,sp.tax_price, sp.price,sp.bar_code, p.big_unit, p.small_unit, p.convert_relate, p.id as productId, p.product_sn, g.product_image_list_store, w.code as warehouseCode, t1.valueName,w.name as warehouseName,IFNULL(cpc.main_product_count,cpc1.sub_product_count) as comCount ");
		sqlBuilder.append(" from `cc_sales_order_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append(" LEFT JOIN cc_goods g ON g.id = p.goods_id ");
		sqlBuilder.append(" LEFT JOIN cc_warehouse w ON sod.warehouse_id = w.id ");
		sqlBuilder.append(" LEFT JOIN (SELECT * FROM cc_product_composition GROUP BY parent_id) cpc ON cpc.parent_id = sod.composite_id AND cpc.seller_product_id = sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product_composition cpc1 ON cpc1.parent_id = sod.composite_id AND cpc1.sub_seller_product_id = sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append(" RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append(" WHERE order_id = ? ");
		sqlBuilder.append(" ORDER BY sod.warehouse_id, sod.is_composite, sod.is_gift ");

		return Db.find(sqlBuilder.toString(), orderId);
	}
	
	public List<Record> orderAgainDetail(String orderId) {
		Record record =SellerQuery.me()._findByOrderId(orderId);
		String sellerId=record.getStr("id");
		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*,g.goods_category_id as categoryId, sp.custom_name,sp.tax_price,sp.account_price, sp.price,sp.bar_code, sp.cost, ");
		sqlBuilder.append(" p.big_unit, p.small_unit, p.convert_relate, p.id as productId, p.product_sn, g.product_image_list_store, ");
		sqlBuilder.append(" w.code as warehouseCode, t1.valueName,w.name as warehouseName, IFNULL(cpc1.seller_product_id,cpc.seller_product_id) as sub_id, IFNULL(cpc.name,cpc1.name) as comName, ");
		sqlBuilder.append(" IFNULL(cpc.main_product_count,cpc1.sub_product_count) as comCount, IFNULL(cpc.price,cpc1.price) as comPrice ");
		if (null != sellerId) {
			sqlBuilder.append(" ,IFNULL(t3.count,0) store_count");
		}
		sqlBuilder.append(" from `cc_sales_order_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append(" LEFT JOIN cc_goods g ON g.id = p.goods_id ");
		sqlBuilder.append(" LEFT JOIN cc_warehouse w ON sod.warehouse_id = w.id ");
		sqlBuilder.append(" LEFT JOIN (SELECT * FROM cc_product_composition GROUP BY parent_id) cpc ON cpc.parent_id = sod.composite_id AND cpc.seller_product_id = sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product_composition cpc1 ON cpc1.parent_id = sod.composite_id AND cpc1.sub_seller_product_id = sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append(" RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		if (null != sellerId) {
			sqlBuilder.append(" LEFT JOIN(SELECT SUM(sp.store_count) count,p.id FROM cc_seller_product sp LEFT JOIN cc_product p ON p.id=sp.product_id WHERE sp.seller_id='");
			sqlBuilder.append(sellerId+"' GROUP BY p.id) t3 ON t3.id=p.id");
		}
		sqlBuilder.append(" WHERE order_id = ? ");
		sqlBuilder.append(" ORDER BY sod.composite_id, sod.is_gift asc ");

		return Db.find(sqlBuilder.toString(), orderId);
	}	

	@SuppressWarnings("unchecked")
	public boolean insert(Map<String, String[]> paraMap, String orderId, String sellerId, String sellerCode, String userId, Date date,
	                      String deptId, String dataArea, int index) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
		String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
		String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
		String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));
		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
		String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
		String wareHouseId = StringUtils.getArrayFirst(paraMap.get("warehouse_id"));
		Map<String, Object> result = new HashMap<>();
		if (StrKit.notBlank(wareHouseId)) {
			result = this.getEnoughOrNot(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId, wareHouseId);
		} else {
			result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId);
		}
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return false;
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			BigDecimal count = new BigDecimal(map.get("productCount"));
			detail.setProductCount(count.intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			// 库存盘点写入库存总账未完成
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			String productPrice = StringUtils.getArrayFirst(paraMap.get("bigPrice" + index));
//			String productAmount = StringUtils.getArrayFirst(paraMap.get("rowTotal" + index));
			BigDecimal bigAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 0 , RoundingMode.DOWN)
					                       .multiply(new BigDecimal(productPrice));
			BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP);
			BigDecimal smallAmount = new BigDecimal(detail.getProductCount()).divideAndRemainder(new BigDecimal(convert))[1].multiply(smallPrice);
			BigDecimal productAmount = bigAmount.add(smallAmount);
			String isGift = StringUtils.getArrayFirst(paraMap.get("isGift" + index));
			detail.setProductPrice(new BigDecimal(productPrice));
			detail.setProductAmount(productAmount);
			detail.setIsGift(StringUtils.isNumeric(isGift)? Integer.parseInt(isGift) : 0);
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return false;
		}
		return true;
	}
	
	private Map<String, Object> getEnoughOrNot(String productId, String sellerId, String sellerCode,
			Integer productCount, int convert, String userId, String sellerProductId, String wareHouseId) {
		Map<String, Object> result = new HashMap<>();
		List<Map<String, String>> countList = new ArrayList<>();
		Boolean checkStore = OptionQuery.me().findValueAsBool(Consts.OPTION_SELLER_STORE_CHECK + sellerCode);
		boolean isCheckStore = (checkStore != null && checkStore == true) ? true : false;
		if (!isCheckStore) {
			Map<String, String> map = new HashMap<>();
			map.put("warehouse_id", wareHouseId);
			map.put("productCount", productCount.toString());
			countList.add(map);
			result.put("status", "enough");
			result.put("countList", countList);
			return result;			
		}
		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		if (sellerProduct.getStoreCount().multiply(new BigDecimal(convert)).compareTo(new BigDecimal(productCount)) == -1) {
			result.put("status", "notEnough");
			result.put("countList", countList);
			return result;
		}

		Record record = InventoryQuery.me().findProductStoreCountByWarehouseId(sellerProductId, wareHouseId);
		if (record == null) {
			result.put("status", "notEnough");
			result.put("countList", countList);
			return result;
		}
		BigDecimal defaultCount = record.getBigDecimal("balance_count").multiply(new BigDecimal(convert));
		if (defaultCount.compareTo(new BigDecimal(productCount)) == 1
				    || defaultCount.compareTo(new BigDecimal(productCount)) == 0) {
			Map<String, String> map = new HashMap<>();
			map.put("warehouse_id", record.getStr("warehouse_id"));
			map.put("productCount", productCount.toString());
			countList.add(map);
			result.put("status", "enough");
			result.put("countList", countList);
			return result;
		} else {
			result.put("status", "notEnough");
			result.put("countList", countList);		
		}
		return result;		
	}	

	private Map<String, Object> getWarehouseId(String productId, String sellerId, String sellerCode,
	                                           Integer productCount, Integer convert, String userId, String sellerProductId) {
		Map<String, Object> result = new HashMap<>();
		List<Map<String, String>> countList = new ArrayList<>();
		Boolean checkStore = OptionQuery.me().findValueAsBool(Consts.OPTION_SELLER_STORE_CHECK + sellerCode);
		boolean isCheckStore = (checkStore != null && checkStore == true) ? true : false;

		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		if (sellerProduct.getStoreCount().multiply(new BigDecimal(convert)).compareTo(new BigDecimal(productCount)) == -1 && isCheckStore) {
			result.put("status", "notEnough");
			result.put("countList", countList);
			return result;
		}

		List<Record> list = InventoryQuery.me().findProductStoreCountByDetail(sellerProductId, userId);
		if (list.size() == 0) {
			result.put("status", "notEnough");
			result.put("countList", countList);
			return result;
		}
		Record record = list.get(0);
		BigDecimal defaultCount = record.getBigDecimal("balance_count").multiply(new BigDecimal(convert));
		if (!isCheckStore || defaultCount.compareTo(new BigDecimal(productCount)) == 1
				    || defaultCount.compareTo(new BigDecimal(productCount)) == 0) {
			Map<String, String> map = new HashMap<>();
			map.put("warehouse_id", record.getStr("warehouse_id"));
			map.put("productCount", productCount.toString());
			countList.add(map);
			result.put("status", "enough");
			result.put("countList", countList);
			return result;
		}
		if (record.getStr("type").equals(Consts.WAREHOUSE_TYPE_CAR)) {
			result.put("status", "notEnough");
			result.put("countList", countList);
			return result;
		}
		if (defaultCount.compareTo(new BigDecimal(0)) == 1) {
			Map<String, String> map = new HashMap<>();
			map.put("warehouse_id", record.getStr("warehouse_id"));
			map.put("productCount", defaultCount.toString());
			countList.add(map);
		}
		list.remove(0);
		BigDecimal surplusCount = new BigDecimal(productCount);
		if (defaultCount.compareTo(new BigDecimal(0)) != -1) {
			surplusCount = surplusCount.subtract(defaultCount);
		}		
		BigDecimal count = this.findMoreWareHouse(list, countList, surplusCount, convert);
		if (count.compareTo(new BigDecimal(0)) == 1) {
			result.put("status", "notEnough");
		} else {
			result.put("status", "enough");
		}
		result.put("countList", countList);
		return result;
	}

	private BigDecimal findMoreWareHouse(List<Record> records, List<Map<String, String>> countList, BigDecimal productCount, Integer convert) {
		BigDecimal count = productCount;
		for (Record record : records) {
			BigDecimal store = record.getBigDecimal("balance_count").multiply(new BigDecimal(convert));
			if (store.compareTo(count) == 1
					    || store.compareTo(count) == 0) {
				Map<String, String> map = new HashMap<>();
				map.put("warehouse_id", record.getStr("warehouse_id"));
				map.put("productCount", productCount.toString());
				countList.add(map);
				count = new BigDecimal(0);
				break;
			} else {
				count = count.subtract(store);
				if (store.compareTo(new BigDecimal(0)) == 1) {
					Map<String, String> map = new HashMap<>();
					map.put("warehouse_id", record.getStr("warehouse_id"));
					map.put("produtCount", store.toString());
					countList.add(map);
				}
			}
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public String insertForApp(Map<String, String[]> paraMap, String orderId, String sellerId, String sellerCode, String userId, Date date,
	                            String deptId, String dataArea, int index) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = paraMap.get("sellProductId")[index];
		String convert = paraMap.get("convert")[index];
		String bigNum = paraMap.get("bigNum")[index];
		String smallNum = paraMap.get("smallNum")[index];
		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
		String productId = paraMap.get("productId")[index];
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return "库存不足";
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			String productPrice = paraMap.get("bigPrice")[index];

			BigDecimal bigAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 0 , RoundingMode.DOWN)
					                       .multiply(new BigDecimal(productPrice));
			BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP);
			BigDecimal smallAmount = new BigDecimal(detail.getProductCount()).divideAndRemainder(new BigDecimal(convert))[1].multiply(smallPrice);
			BigDecimal productAmount = bigAmount.add(smallAmount);

			detail.setProductPrice(new BigDecimal(productPrice));
			String isGiftStr = paraMap.get("isGift")[index];
			Integer isGift = isGiftStr != null ? Integer.valueOf(isGiftStr) : 0;
			detail.setProductAmount(isGift == 0 ? productAmount : new BigDecimal(0));
			detail.setIsGift(isGift);
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);

		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return "下单失败";
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public String insertForAppGift(Map<String, String[]> paraMap, String orderId, String sellerId, String sellerCode, String userId, Date date,
	                                String deptId, String dataArea, int index) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String giftSellerProductId = paraMap.get("giftSellProductId")[index];
		String convert = paraMap.get("giftConvert")[index];
		String giftNum = paraMap.get("giftNum")[index];
		String giftUnit = paraMap.get("giftUnit")[index];

		Integer productCount = 0;
		if ("bigUnit".equals(giftUnit)) {
			productCount = Integer.valueOf(giftNum) * Integer.valueOf(convert);
		} else {
			productCount = Integer.valueOf(giftNum);
		}

		String productId = paraMap.get("giftProductId")[index];
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, giftSellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return "库存不足";
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(giftSellerProductId);

			String productPrice = paraMap.get("giftBigPrice")[index];
			detail.setProductPrice(new BigDecimal(productPrice));

			BigDecimal productAmount = new BigDecimal(0);
			if ("bigUnit".equals(giftUnit)) {
				BigDecimal bigAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 0 , RoundingMode.DOWN)
						                       .multiply(new BigDecimal(productPrice));
				productAmount = bigAmount;
			} else {
				BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP);
				BigDecimal smallAmount = new BigDecimal(detail.getProductCount()).divideAndRemainder(new BigDecimal(convert))[1].multiply(smallPrice);
				productAmount = smallAmount;
			}

			detail.setProductAmount(productAmount);
			detail.setIsGift(1);//赠品
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return "下单失败";
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public String insertForAppComposition(SellerProduct product, String orderId, String sellerId, String sellerCode, String id,
	                                       Date date, String deptId, String dataArea, Integer number, String userId) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = product.getId();
		Integer convert = product.getInt("convert_relate");
		double compositionCount = Double.valueOf(product.getStr("productCount"));
		Integer productCount = (int) Math.round(compositionCount * number);
		String productId = product.getProductId();
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, convert, userId, sellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return "库存不足";
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);

			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			detail.setProductPrice(product.getPrice());
			BigDecimal productAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP)
					                           .multiply(product.getPrice());
			detail.setProductAmount(productAmount);
			detail.setIsGift(product.getInt("isGift"));
			detail.setIsComposite(1);
			detail.setCompositeId(product.getStr("parentId"));
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return "下单失败";
		}
		return "";
	}

	public boolean updateForApp(Map<String, String[]> paraMap, int index, Date date) {
		SalesOrderDetail detail = SalesOrderDetailQuery.me().findById(paraMap.get("orderDetailId")[index]);

		String convert = paraMap.get("convert")[index];
		String bigNum = paraMap.get("bigNum")[index];
		String smallNum = paraMap.get("smallNum")[index];
		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);

		detail.setProductCount(productCount);
		detail.setLeftCount(productCount);

		String productPrice = paraMap.get("bigPrice")[index];
		BigDecimal bigAmount = new BigDecimal(detail.getProductCount())
				                       .divide(new BigDecimal(convert), 0, RoundingMode.DOWN).multiply(new BigDecimal(productPrice));
		BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2,
				BigDecimal.ROUND_HALF_UP);
		BigDecimal smallAmount = new BigDecimal(productCount).divideAndRemainder(new BigDecimal(convert))[1]
				                         .multiply(smallPrice);
		BigDecimal productAmount = bigAmount.add(smallAmount);

		detail.setProductPrice(new BigDecimal(productPrice));
		detail.setProductAmount(productAmount);
		detail.setModifyDate(date);

		return detail.update();
	}

	public String addForApp(Map<String, String[]> paraMap, String orderId, String sellerId, String sellerCode, String userId, Date date,
	                           String deptId, String dataArea, int index) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = paraMap.get("addSellProductId")[index];
		String convert = paraMap.get("addConvert")[index];
		String bigNum = paraMap.get("addBigNum")[index];
		String smallNum = paraMap.get("addSmallNum")[index];
		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
		String productId = paraMap.get("addProductId")[index];

		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return "添加商品失败，库存不足";
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			String productPrice = paraMap.get("addBigPrice")[index];

			BigDecimal bigAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 0 , RoundingMode.DOWN)
					                       .multiply(new BigDecimal(productPrice));
			BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP);
			BigDecimal smallAmount = new BigDecimal(detail.getProductCount()).divideAndRemainder(new BigDecimal(convert))[1].multiply(smallPrice);
			BigDecimal productAmount = bigAmount.add(smallAmount);

			detail.setProductPrice(new BigDecimal(productPrice));
			String isGiftStr = paraMap.get("addIsGift")[index];
			Integer isGift = isGiftStr != null ? Integer.valueOf(isGiftStr) : 0;
			detail.setProductAmount(isGift == 0 ? productAmount : new BigDecimal(0));
			detail.setIsGift(isGift);
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return "下单失败";
		}
		return "";
	}


	public SalesOrderDetail findById(final String id) {
		return DAO.findById(id);
	}

	public Page<SalesOrderDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_order_detail` ");

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

	public List<SalesOrderDetail> findBySalesOrderId(String id) {
		return DAO.doFind("order_id = ?", id);
	}

	@SuppressWarnings("unchecked")
	public boolean insertDetailByComposition(SellerProduct product, String orderId, String sellerId, String sellerCode, String id,
	                                         Date date, String deptId, String dataArea, Integer index, Integer number, String userId) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = product.getId();
		Integer convert = product.getInt("convert_relate");
		double compositionCount = Double.valueOf(product.getStr("productCount"));
		Integer productCount = (int) Math.round(compositionCount * number);
		String productId = product.getProductId();
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, convert, userId, sellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return false;
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			// 库存盘点写入库存总账未完成
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			detail.setProductPrice(product.getPrice());
			BigDecimal productAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP)
					                           .multiply(product.getPrice());
//			BigDecimal amount = new BigDecimal(product.getInt("productCount")).multiply(product.getPrice());
			detail.setProductAmount(productAmount);
			detail.setIsGift(product.getInt("isGift"));
			detail.setIsComposite(1);
			detail.setCompositeId(product.getStr("parentId"));
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return false;
		}
		return true;
	}

	public List<Record> findByOrderSn(String order_sn) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, sp.price, p.big_unit, p.small_unit, p.convert_relate, p.id as productId, w.code as warehouseCode, t1.valueName ");
		sqlBuilder.append(" from `cc_sales_order_detail` sod ");
		sqlBuilder.append(" INNER JOIN cc_sales_order cso on cso.id = sod.order_id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append(" LEFT JOIN cc_warehouse w ON sod.warehouse_id = w.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append(" WHERE cso.order_sn = ? ");
		sqlBuilder.append(" ORDER BY sod.warehouse_id, sod.is_gift ");

		return Db.find(sqlBuilder.toString(), order_sn);
	}
	
	//通过订单详情ID查找活动名
	public Record getOrderDetailId(String orderDetailId) {
		String sql = "SELECT cs.order_id,ca.title, pc.activity_id FROM cc_sales_order_detail cs " 
					+"LEFT JOIN cc_product_composition pc ON cs.composite_id = pc.id "  
					+"LEFT JOIN cc_activity ca ON ca.id = pc.activity_id "
					+ "where cs.id = '"+orderDetailId+"'";
		return Db.findFirst(sql);
	}

	@SuppressWarnings("unchecked")
	public String memberInsert(Map<String, String> paraMap, String orderId, String sellerId, String sellerCode, String userId, Date date,
							   String deptId, String dataArea) {
		List<SalesOrderDetail> detailList = new ArrayList<>();
		String sellerProductId = paraMap.get("sellProductId");
		String convert = paraMap.get("convert");
		String bigNum = paraMap.get("bigNum");
		String smallNum = paraMap.get("smallNum");
		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
		String productId = paraMap.get("productId");
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId);
		String status = result.get("status").toString();
		List<Map<String, String>> list = (List<Map<String, String>>) result.get("countList");

		if (!status.equals("enough")) {
			return "库存不足";
		}
		for (Map<String, String> map : list) {
			SalesOrderDetail detail = new SalesOrderDetail();
			detail.setProductCount(new BigDecimal(map.get("productCount").toString()).intValue());
			detail.setLeftCount(detail.getProductCount());
			detail.setOutCount(0);
			detail.setWarehouseId(map.get("warehouse_id").toString());

			detail.setId(StrKit.getRandomUUID());
			detail.setOrderId(orderId);
			detail.setSellProductId(sellerProductId);

			String productPrice = paraMap.get("bigPrice");

			BigDecimal bigAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert), 0 , RoundingMode.DOWN)
					.multiply(new BigDecimal(productPrice));
			BigDecimal smallPrice = new BigDecimal(productPrice).divide(new BigDecimal(convert), 2, BigDecimal.ROUND_HALF_UP);
			BigDecimal smallAmount = new BigDecimal(detail.getProductCount()).divideAndRemainder(new BigDecimal(convert))[1].multiply(smallPrice);
			BigDecimal productAmount = bigAmount.add(smallAmount);

			detail.setProductPrice(new BigDecimal(productPrice));
			detail.setProductAmount(productAmount);
			detail.setIsGift(0);
			detail.setCreateDate(date);
			detail.setDeptId(deptId);
			detail.setDataArea(dataArea);
			detailList.add(detail);
		}
		int[] i = Db.batchSave(detailList, detailList.size());
		int count = 0;
		for (int j : i) {
			count = count + j;
		}
		if (count != detailList.size()) {
			return "下单失败";
		}
		return "";
	}

	public List<Record> findOrderByDataArea(String dataArea, String status, String startDate, String endDate, String isGift) {
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("SELECT o.sell_product_id, csp.custom_name,u.realname,cs.biz_user_id,FORMAT((o.product_count - IFNULL(t1.product_count,0))/cp.convert_relate,2) as count,o.is_gift ");
		sqlBuilder.append(" FROM cc_sales_order_detail o");
		sqlBuilder.append(" LEFT JOIN cc_sales_order cs ON o.order_id = cs.id");
		sqlBuilder.append(" LEFT JOIN cc_seller_product csp ON o.sell_product_id = csp.id");
		sqlBuilder.append(" LEFT JOIN cc_product cp ON csp.product_id = cp.id");
		sqlBuilder.append(" LEFT JOIN `user` u ON cs.biz_user_id = u.id");
		sqlBuilder.append(" LEFT JOIN ( SELECT cs.input_user_id,o.sell_product_id,o.product_count,o.is_gift FROM cc_sales_refund_instock_detail o");
		sqlBuilder.append(" LEFT JOIN cc_sales_refund_instock cs ON cs.id = o.refund_instock_id");
		sqlBuilder.append(" WHERE cs.status not in (?, ?) AND o.data_area LIKE ?");
		sqlBuilder.append(" AND cs.create_date >= ? AND cs.create_date <= ?");
		if (status.equals("print")) {
			sqlBuilder.append("AND cs.is_print = 1");
		}
		if (isGift.equals("0")) {
			sqlBuilder.append(" AND o.is_gift = 0");
		} else {
			sqlBuilder.append(" AND o.is_gift = 1");
		}
		params.add(Consts.SALES_REFUND_INSTOCK_CANCEL);
		params.add(Consts.SALES_REFUND_INSTOCK_REFUSE);
		params.add(dataArea);
		params.add(startDate);
		params.add(endDate);		
		sqlBuilder.append(" GROUP BY o.sell_product_id, cs.input_user_id,o.is_gift");
		sqlBuilder.append(" ) t1 on t1.input_user_id = cs.biz_user_id AND t1.sell_product_id = o.sell_product_id AND t1.is_gift = o.is_gift");
		sqlBuilder.append(" WHERE o.data_area LIKE ?");
		params.add(dataArea);
		if (status.equals("print")) {
			sqlBuilder.append(" AND cs.print_time >= ? AND cs.print_time <= ?");
		} else {
			sqlBuilder.append(" AND cs.create_date >= ? AND cs.create_date <= ?");
		}
		params.add(startDate);
		params.add(endDate);
		if (isGift.equals("0")) {
			sqlBuilder.append(" AND o.is_gift = 0");
		} else {
			sqlBuilder.append(" AND o.is_gift = 1");
		}		
		sqlBuilder.append(" AND EXISTS(SELECT os.status FROM cc_sales_order_status os WHERE os.status = cs.status and os.status != ? and os.status != ?)");
		params.add(Consts.SALES_ORDER_STATUS_CANCEL);
		params.add(Consts.SALES_ORDER_STATUS_REJECT);		
		sqlBuilder.append(" GROUP BY o.sell_product_id, cs.biz_user_id,o.is_gift");
		sqlBuilder.append(" ORDER BY cs.biz_user_id");
		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findSellerOrderByDataArea(String dataArea, String keyword, String startDate, String endDate,
			String isGift) {
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("SELECT o.sell_product_id, csp.custom_name,s.id sellerId,s.seller_name,FORMAT((o.product_count - IFNULL(t1.product_count,0))/cp.convert_relate,2) as count,o.is_gift ");
		sqlBuilder.append(" FROM cc_sales_order_detail o");
		sqlBuilder.append(" LEFT JOIN cc_sales_order cs ON o.order_id = cs.id");
		sqlBuilder.append(" LEFT JOIN cc_seller_product csp ON o.sell_product_id = csp.id");
		sqlBuilder.append(" LEFT JOIN cc_product cp ON csp.product_id = cp.id");
		sqlBuilder.append(" LEFT JOIN cc_seller s ON s.id = csp.seller_id");
		sqlBuilder.append(" LEFT JOIN ( SELECT cs.seller_id,o.sell_product_id,o.product_count,o.is_gift FROM cc_sales_refund_instock_detail o");
		sqlBuilder.append(" LEFT JOIN cc_sales_refund_instock cs ON cs.id = o.refund_instock_id");
		sqlBuilder.append(" WHERE cs.status not in (?, ?) AND o.data_area LIKE ?");
		sqlBuilder.append(" AND cs.create_date >= ? AND cs.create_date <= ?");
		if (keyword.equals("print")) {
			sqlBuilder.append("AND cs.is_print = 1");
		}
		if (isGift.equals("0")) {
			sqlBuilder.append(" AND o.is_gift = 0");
		} else {
			sqlBuilder.append(" AND o.is_gift = 1");
		}
		params.add(Consts.SALES_REFUND_INSTOCK_CANCEL);
		params.add(Consts.SALES_REFUND_INSTOCK_REFUSE);
		params.add(dataArea);
		params.add(startDate);
		params.add(endDate);		
		sqlBuilder.append(" GROUP BY o.sell_product_id, cs.seller_id,o.is_gift");
		sqlBuilder.append(" ) t1 on t1.seller_id = cs.seller_id AND t1.sell_product_id = o.sell_product_id AND t1.is_gift = o.is_gift");
		sqlBuilder.append(" WHERE o.data_area LIKE ?");
		params.add(dataArea);
		if (keyword.equals("print")) {
			sqlBuilder.append(" AND cs.print_time >= ? AND cs.print_time <= ?");
		} else {
			sqlBuilder.append(" AND cs.create_date >= ? AND cs.create_date <= ?");
		}
		params.add(startDate);
		params.add(endDate);
		if (isGift.equals("0")) {
			sqlBuilder.append(" AND o.is_gift = 0");
		} else {
			sqlBuilder.append(" AND o.is_gift = 1");
		}		
		sqlBuilder.append(" AND EXISTS(SELECT os.status FROM cc_sales_order_status os WHERE os.status = cs.status and os.status != ? and os.status != ?)");
		params.add(Consts.SALES_ORDER_STATUS_CANCEL);
		params.add(Consts.SALES_ORDER_STATUS_REJECT);		
		sqlBuilder.append(" GROUP BY o.sell_product_id, cs.seller_id,o.is_gift");
		sqlBuilder.append(" ORDER BY cs.biz_user_id");
		return Db.find(sqlBuilder.toString(), params.toArray());
	}

}
