package org.ccloud.front.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;


/**
 * Created by chen.xuebing on 2018/3/28.
 */
@RouterMapping(url = "/outstock")
//@RequiresPermissions(value = { "/front/outstock", "/admin/dealer/all" }, logical = Logical.OR)
public class OutstockController extends BaseFrontController {

	//出库单
	public void index() {
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				                                      .findByDataArea(dealerDataArea);
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getName());
			customerTypes.add(item);
		}

		List<Map<String, Object>> bizUsers = new ArrayList<>();
		bizUsers.add(all);
		List<SalesOrder> orders = SalesOrderQuery.me().findBySellerIdAndDataArea(sellerId, selectDataArea);
		for (SalesOrder order : orders) {
			Map<String, Object> items = new HashMap<>();
			items.put("title", order.getStr("realname"));
			items.put("value", order.getBizUserId());
			bizUsers.add(items);
		}
		String history = getPara("history");
		setAttr("history", history);
		setAttr("bizUsers", JSON.toJSON(bizUsers));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("outstock_list.html");
	}

	public void outstockList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String bizUserId = getPara("bizUserId");

		Page<Record> outstockList = SalesOutstockQuery.me()._paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea, bizUserId);

		Map<String, Object> map = new HashMap<>();
		map.put("outstockList", outstockList.getList());
		map.put("user", user);
		renderJson(map);
	}

	public void outstockDetail() {

		String outstockId = getPara("outstockId");
		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetailList = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		List<Map<String, String>> images = getImageSrc(outstockDetailList);
		outstock.set("statusName", getStatusName(outstock.getInt("status")));

		setAttr("outstock", outstock);
		setAttr("outstockDetailList", outstockDetailList);
		setAttr("images", images);
		render("outstock_detail.html");
	}

	private List<Map<String, String>> getImageSrc(List<Record> outstockDetailList) {
		List<Map<String, String>> imagePaths = new ArrayList<>();
		for (Record record : outstockDetailList) {
			JSONArray jsonArray = JSONArray.parseArray(record.getStr("product_image_list_store"));
			List<ImageJson> imageList = jsonArray.toJavaList(ImageJson.class);
			Map<String, String> map = new HashMap<>();
			map.put("productSn", record.getStr("product_sn"));
			if (imageList.size() == 0) {
				map.put("savePath", null);
				imagePaths.add(map);
			}
			for (int i = 0; i < imageList.size(); i++) {
				if (imageList.get(i).getImgName().indexOf(record.getStr("product_sn") + "_1") != -1) {
					map.put("savePath", imageList.get(i).getSavePath());
					imagePaths.add(map);
					break;
				}
				if (i == imageList.size() - 1) {
					map.put("savePath", null);
					imagePaths.add(map);
				}
			}
		}
		return imagePaths;
	}

	// 销售订货单出库
	public void outStock() {

		String result = this.out();
		if (StrKit.isBlank(result)) {
			renderAjaxResultForSuccess("出库成功");
		} else {
			renderAjaxResultForError(result + ",出库失败");
		}

	}

	public String out() {
		final String[] result = {""};
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				String outstockId = getPara("outstockId");
				Date date = new Date();
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

				SalesOutstock salesOutstock = SalesOutstockQuery.me().findById(outstockId);
				List<Record> outstockDetailList = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);

				for (Record record : outstockDetailList) {

					Inventory inventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(salesOutstock.getSellerId(), record.getStr("productId"), salesOutstock.getWarehouseId());
					if (inventory == null) {
						result[0] = record.getStr("custom_name") + "库存总账不存在";
						return false;
					}
					BigDecimal oldOutCount = inventory.getOutCount() == null ? new BigDecimal(0) : inventory.getOutCount();
					BigDecimal oldOutAmount = inventory.getOutAmount() == null ? new BigDecimal(0) : inventory.getOutAmount();
					BigDecimal oldBalanceAmount = inventory.getBalanceAmount() == null ? new BigDecimal(0) : inventory.getBalanceAmount();
					BigDecimal oldBalanceCount = inventory.getBalanceCount() == null ? new BigDecimal(0) : inventory.getBalanceCount();

					BigDecimal storeCount = (new BigDecimal(record.getInt("product_count"))).divide(new BigDecimal(record.getInt("convert_relate")), 2, BigDecimal.ROUND_HALF_UP);

					inventory.setOutCount(oldOutCount.add(storeCount));
					inventory.setOutAmount(oldOutAmount.add(record.getBigDecimal("product_amount")));
					inventory.setOutPrice(record.getBigDecimal("price"));
					inventory.setBalanceCount(oldBalanceCount.subtract(storeCount));
					inventory.setBalanceAmount(oldBalanceAmount.subtract(record.getBigDecimal("product_amount")));
					inventory.setModifyDate(date);

					if (!inventory.update()) {
						result[0] = record.getStr("custom_name") + "库存总账更新错误";
						return false;
					}

					InventoryDetail oldDetail = InventoryDetailQuery.me().findBySellerProductId(record.getStr("sell_product_id"), salesOutstock.getWarehouseId());
					InventoryDetail inventoryDetail = new InventoryDetail();
					inventoryDetail.setId(StrKit.getRandomUUID());
					inventoryDetail.setWarehouseId(inventory.getWarehouseId());
					inventoryDetail.setSellProductId(record.getStr("sell_product_id"));
					inventoryDetail.setOutAmount(record.getBigDecimal("product_amount"));
					inventoryDetail.setOutCount(storeCount);
					inventoryDetail.setOutPrice(inventory.getOutPrice());
					inventoryDetail.setBalanceAmount(oldDetail.getBalanceAmount().subtract(record.getBigDecimal("product_amount")));
					inventoryDetail.setBalanceCount(storeCount);
					inventoryDetail.setBalancePrice(oldDetail.getBalancePrice());
					inventoryDetail.setBizBillSn(salesOutstock.getOutstockSn());
					inventoryDetail.setBizDate(record.getDate("create_date"));
					inventoryDetail.setBizType(Consts.BIZ_TYPE_SALES_OUTSTOCK);
					inventoryDetail.setBizUserId(user.getId());
					inventoryDetail.setDeptId(user.getDepartmentId());
					inventoryDetail.setDataArea(user.getDataArea());
					inventoryDetail.setCreateDate(date);

					if (!inventoryDetail.save()) {
						result[0] = record.getStr("custom_name") + "库存总账明细更新错误";
						return false;
					}

					SellerProduct sellerProduct = SellerProductQuery.me().findById(record.getStr("sell_product_id"));
					sellerProduct.setStoreCount(sellerProduct.getStoreCount().subtract(storeCount));
					sellerProduct.setModifyDate(date);
					if (!sellerProduct.update()) {
						result[0] = record.getStr("custom_name") + "库存数量更新错误";
						return false;
					}

					SalesOrderDetail salesOrderDetail = SalesOrderDetailQuery.me().findById(record.getStr("order_detail_id"));
					salesOrderDetail.setOutCount(salesOrderDetail.getOutCount() + record.getInt("product_count"));
					salesOrderDetail.setLeftCount(salesOrderDetail.getLeftCount() - record.getInt("product_count"));
					salesOrderDetail.setModifyDate(date);
					if (!salesOrderDetail.update()) {
						return false;
					}

					ReceivablesDetail receivablesDetail = new ReceivablesDetail();
					receivablesDetail.setId(StrKit.getRandomUUID());
					receivablesDetail.setObjectId(salesOutstock.getCustomerId());
					receivablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
					receivablesDetail.setReceiveAmount(record.getBigDecimal("product_amount"));
					receivablesDetail.setActAmount(new BigDecimal(0));
					receivablesDetail.setBalanceAmount(record.getBigDecimal("product_amount"));
					receivablesDetail.setBizDate(date);
					receivablesDetail.setRefSn(salesOutstock.getOutstockSn());
					receivablesDetail.setRefType(Consts.BIZ_TYPE_SALES_ORDER);
					receivablesDetail.setDeptId(salesOutstock.getDeptId());
					receivablesDetail.setDataArea(salesOutstock.getDataArea());
					receivablesDetail.setCreateDate(date);

					if (!receivablesDetail.save()) {
						result[0] = record.getStr("custom_name") + "应收账款明细生成错误";
						return false;
					}

				}

				SalesOrder salesOrder = SalesOrderQuery.me().findByOutstockId(outstockId);

				String customeId = salesOrder.getCustomerId();

				Receivables receivables = ReceivablesQuery.me().findByCustomerId(customeId);
				if (receivables == null) {
					receivables = new Receivables();
					receivables.setObjectId(customeId);
					receivables.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
					receivables.setReceiveAmount(salesOutstock.getTotalAmount());
					receivables.setActAmount(new BigDecimal(0));
					receivables.setBalanceAmount(salesOutstock.getTotalAmount());
					receivables.setDeptId(salesOrder.getDeptId());
					receivables.setDataArea(salesOrder.getDataArea());
					receivables.setCreateDate(date);
				} else {
					receivables.setReceiveAmount(receivables.getReceiveAmount().add(salesOutstock.getTotalAmount()));
					receivables.setBalanceAmount(receivables.getBalanceAmount().add(salesOutstock.getTotalAmount()));
				}

				if (!receivables.saveOrUpdate()) {
					result[0] = "应收账款生成错误";
					return false;
				}

				salesOutstock.setStatus(Consts.SALES_OUT_STOCK_STATUS_OUT);
				salesOutstock.setBizUserId(user.getId());
				salesOutstock.setBizDate(date);
				salesOutstock.setModifyDate(date);

				if (!salesOutstock.update()) {
					result[0] = "出库单状态更新错误";
					return false;
				}

				salesOrder.setStatus(Consts.SALES_ORDER_STATUS_ALL_OUT);
				salesOrder.setModifyDate(date);

				if (!salesOrder.update()) {
					result[0] = "出库单状态更新错误";
					return false;
				}

				// 如果客户种类是直营商，则生成直营商的采购入库单
				SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(salesOutstock.getCustomerId());
				if (Consts.CUSTOMER_KIND_SELLER.equals(sellerCustomer.getCustomerKind()))

				{
					Record seller = SellerQuery.me().findByCustomerId(sellerCustomer.getCustomerId());
					String purchaseInstockId = StrKit.getRandomUUID();
					// PS + 100000(机构编号或企业编号6位) + 20171108(时间) + 000001(流水号)
					String pwarehouseSn = "PS" + seller.getStr("seller_code") + DateUtils.format("yyyyMMdd", date)
							                      + PurchaseInstockQuery.me().getNewSn(seller.getStr("id"));

					Warehouse warehouse = WarehouseQuery.me().findBySellerId(seller.getStr("id"));

					PurchaseInstock purchaseInstock = new PurchaseInstock();
					purchaseInstock.set("id", purchaseInstockId);
					purchaseInstock.set("pwarehouse_sn", pwarehouseSn);
					purchaseInstock.set("warehouse_id", warehouse.getId());
					purchaseInstock.set("biz_user_id", salesOrder.getBizUserId());
					purchaseInstock.setSupplierId(salesOrder.getSellerId());
					purchaseInstock.set("input_user_id", salesOrder.getBizUserId());
					purchaseInstock.set("status", 0);// 待入库
					purchaseInstock.set("total_amount", salesOutstock.getTotalAmount());
					purchaseInstock.set("payment_type", salesOutstock.getReceiveType());
					purchaseInstock.set("remark", salesOutstock.getRemark());
					purchaseInstock.set("dept_id", seller.get("dept_id"));
					purchaseInstock.set("data_area", seller.get("data_area"));
					purchaseInstock.set("create_date", date);

					if (!purchaseInstock.save()) {
						result[0] = "生成入库单错误";
						return false;
					}

					// 直营商的应付账款
					String payablesType = Consts.RECEIVABLES_OBJECT_TYPE_SUPPLIER;
					Payables payables = PayablesQuery.me().findByObjIdAndDeptId(sellerCustomer.getSellerId(), payablesType,
							seller.getStr("dept_id"));
					if (payables == null) {
						payables = new Payables();
						payables.setObjId(sellerCustomer.getSellerId());
						payables.setObjType(payablesType);
						payables.setPayAmount(salesOutstock.getTotalAmount());
						payables.setActAmount(new BigDecimal(0));
						payables.setBalanceAmount(salesOutstock.getTotalAmount());
						payables.setDeptId(seller.getStr("dept_id"));
						payables.setDataArea(seller.getStr("data_area"));
					} else {
						payables.setPayAmount(payables.getPayAmount().add(salesOutstock.getTotalAmount()));
						payables.setBalanceAmount(payables.getBalanceAmount().add(salesOutstock.getTotalAmount()));
					}

					if (payables.saveOrUpdate()) {
						result[0] = "生成直营商的应付账款错误";
						return false;
					}

					for (Record record : outstockDetailList) {
						String sellerId = seller.getStr("id");
						String productId = record.getStr("productId");
						Product product = ProductQuery.me().findById(productId);
						List<SellerProduct> sellerProducts = SellerProductQuery.me()._findByProductIdAndSellerId(productId, sellerId);
						if (sellerProducts.size() == 0) {
							SellerProduct sellerProduct = SellerProductQuery.me().newProduct(sellerId, date,
									DateUtils.format("yyMMdd", date), product, getRequest());
							sellerProducts.add(sellerProduct);
						}
						for (int i = 0; i < sellerProducts.size(); i++) {
							PurchaseInstockDetail purchaseInstockDetail = new PurchaseInstockDetail();
							purchaseInstockDetail.setId(StrKit.getRandomUUID());
							purchaseInstockDetail.setPurchaseInstockId(purchaseInstockId);

							purchaseInstockDetail.set("seller_product_id", sellerProducts.get(i).getId());
							purchaseInstockDetail.setProductCount(record.getInt("product_count"));
							purchaseInstockDetail.setProductAmount(record.getBigDecimal("product_amount"));
							purchaseInstockDetail.setProductPrice(record.getBigDecimal("product_price"));
							purchaseInstockDetail
									.setPurchaseOrderDetailId(record.getStr("id"));

							purchaseInstockDetail.setDeptId(seller.getStr("dept_id"));
							purchaseInstockDetail.setDataArea(seller.getStr("data_area"));
							purchaseInstockDetail.setCreateDate(date);

							if (!purchaseInstockDetail.save()) {
								result[0] = "生成入库明细错误";
								return false;
							}
						}

						PayablesDetail payablesDetail = new PayablesDetail();
						payablesDetail.setId(StrKit.getRandomUUID());
						payablesDetail.setObjectId(sellerCustomer.getSellerId());
						payablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_SUPPLIER);
						payablesDetail.setPayAmount(record.getBigDecimal("product_amount"));
						payablesDetail.setActAmount(new BigDecimal(0));
						payablesDetail.setBalanceAmount(record.getBigDecimal("product_amount"));
						payablesDetail.setRefSn(pwarehouseSn);
						payablesDetail.setBizDate(date);
						payablesDetail.setRefType(Consts.BIZ_TYPE_INSTOCK);
						payablesDetail.setDeptId(seller.getStr("dept_id"));
						payablesDetail.setDataArea(seller.getStr("data_area"));
						payablesDetail.setCreateDate(date);
						payablesDetail.save();

					}

				}

				//如果有活动关联
				if (StrKit.notBlank(salesOrder.getActivityApplyId()) && (!Consts.SALES_ORDER_ACTIVITY_APPLY_ID_OTHER.equals(salesOrder.getActivityApplyId())))

				{
					ActivityApply activityApply = ActivityApplyQuery.me().findById(salesOrder.getActivityApplyId());
					activityApply.setStatus(Consts.ACTIVITY_APPLY_STATUS_END);
					if (!activityApply.update()) {
						return false;
					}
				}

				return true;
			}
		});
		return result[0];
	}

	private String getStatusName(int statusCode) {
		if (statusCode == Consts.SALES_OUT_STOCK_STATUS_DEFUALT)
			return "待出库";
		if (statusCode == Consts.SALES_OUT_STOCK_STATUS_OUT)
			return "全部出库";
		if (statusCode == Consts.SALES_OUT_STOCK_STATUS_PART_OUT)
			return "部分出库";
		return "无";
	}

	public void getOutstockProductDetail() {
		String outstockId = getPara("outstockId");

		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		Map<String, Object> map = new HashMap<>();
		map.put("outstockDetail", outstockDetail);
		renderJson(map);
	}

}
