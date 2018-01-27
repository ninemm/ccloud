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
				" SELECT sod.*, sp.custom_name, sp.price, p.big_unit, p.small_unit, p.convert_relate, p.id as productId, p.product_sn, g.product_image_list_store, w.code as warehouseCode, t1.valueName,w.name as warehouseName ");
		sqlBuilder.append(" from `cc_sales_order_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append(" LEFT JOIN cc_goods g ON g.id = p.goods_id ");
		sqlBuilder.append(" LEFT JOIN cc_warehouse w ON sod.warehouse_id = w.id ");
		sqlBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		sqlBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		sqlBuilder.append(" WHERE order_id = ? ");
		sqlBuilder.append(" ORDER BY sod.warehouse_id, sod.is_gift ");

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
		Map<String, Object> result = this.getWarehouseId(productId, sellerId, sellerCode, productCount, Integer.parseInt(convert), userId, sellerProductId);
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

		List<Record> list = InventoryQuery.me().findProductStoreByUser(sellerId, productId, userId);
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
		BigDecimal count = this.findMoreWareHouse(list, countList, new BigDecimal(productCount).subtract(defaultCount), convert);
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
		Integer productCount = (int) Math.round(compositionCount * convert * number);
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
			BigDecimal productAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert))
					                           .multiply(product.getPrice());
			detail.setProductAmount(productAmount);
			detail.setIsGift(product.getInt("is_gift"));
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
		Integer productCount = (int) Math.round(compositionCount * convert * number);
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
			BigDecimal productAmount = new BigDecimal(detail.getProductCount()).divide(new BigDecimal(convert))
					                           .multiply(product.getPrice());
//			BigDecimal amount = new BigDecimal(product.getInt("productCount")).multiply(product.getPrice());
			detail.setProductAmount(productAmount);
			detail.setIsGift(product.getIsGift());
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

}
