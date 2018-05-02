/**
 * Copyright (c) 2015-2018, Wally Wang 王勇(wally8292@163.com)
 */
package org.ccloud.front.api;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.ccloud.model.Department;
import org.ccloud.model.Product;
import org.ccloud.model.PurchaseOrder;
import org.ccloud.model.PurchaseOrderDetail;
import org.ccloud.model.Seller;
import org.ccloud.model.Supplier;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.SupplierQuery;
import org.ccloud.model.vo.StockInRequestBody;
import org.ccloud.model.vo.StockInRequestProduct;
import org.ccloud.utils.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfinal.kit.StrKit;

/**
 * @author wally
 *
 */
public class PurchaseStockInApi {
	/**
	 * 入库接口
	 */
//	public void purchaseStockIn(Map<String, String> paramMap) {
//		String jsonData = getPara("data");
//		if(StrKit.isBlank(jsonData)) {
//			renderAjaxResultForError("no data");
//			return;
//		}
//		Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
//		FastDateFormat fdf = FastDateFormat.getInstance(DateUtils.DEFAULT_FILE_NAME_FORMATTER);
//		StockInRequestBody stockInRequest = gson.fromJson(jsonData, StockInRequestBody.class);
//		List<StockInRequestProduct> products = stockInRequest.getProducts();
//		if(CollectionUtils.isEmpty(products)) {		// 校验产品列表是否为空
//			renderAjaxResultForError("no products");
//			return;
//		}
//		
//		// 校验产品列表中是否有重复产品   ------ start
//		Set<String> set = new HashSet<String>();
//		for(int i = 0; i < products.size(); i++) {
//			set.add(products.get(i).getCode());
//		}
//		if(set.size() < products.size()){
//			renderAjaxResultForError("订单中有两件及以上相同的产品，请重新选择！");
//			return;
//		}
//		// 校验产品列表中是否有重复产品   ------ end
//		
//		Calendar calendar = Calendar.getInstance();
//		Date nowDateTime = calendar.getTime();
//		String nowDateTimeStr = fdf.format(calendar);
//		
//		// 查询并校验经销商编码是否正确
//		Seller seller = SellerQuery.me().findbyCode(stockInRequest.getSellerCode());
//		if(seller == null) {
//			renderAjaxResultForError("seller code error");
//		}
//		
//		final PurchaseOrder purchaseOrder = getModel(PurchaseOrder.class);
//		final PurchaseOrderDetail purchaseOrderDetail = getModel(PurchaseOrderDetail.class);
//		Department department = DepartmentQuery.me().findById(seller.getDeptId());
//		String porderSn = "PO" + seller.getSellerCode() + nowDateTimeStr.substring(0,8)+PurchaseOrderQuery.me().getNewSn(seller.getId());
//		
//		// 查询并校验供应商编码是否正确
//		List<Supplier> suppliers = SupplierQuery.me().findByCode(stockInRequest.getSupplierCode());
//		if(CollectionUtils.isEmpty(suppliers)) {
//			renderAjaxResultForError("supplier code error");
//			return;
//		}
//		
//		Supplier supplier = suppliers.get(0);
//		String id = StrKit.getRandomUUID();
//		purchaseOrder.set("id", id);
//		purchaseOrder.set("porder_sn", porderSn);
//		purchaseOrder.set("supplier_id", supplier.getId());
//		purchaseOrder.set("contact", supplier.getContact());
//		purchaseOrder.set("mobile", supplier.getMobile());
////		purchaseOrder.set("biz_user_id", "");
//		purchaseOrder.set("biz_date", nowDateTime);
//		purchaseOrder.set("status", 0);
//		purchaseOrder.set("payment_type", stockInRequest.getPayType());
//		purchaseOrder.set("remark", stockInRequest.getRemark());
//		purchaseOrder.set("dept_id", seller.getDeptId());
//		purchaseOrder.set("data_area", department.getDataArea());
//		purchaseOrder.set("deal_date", stockInRequest.getDealDate());
//		purchaseOrder.set("create_date", nowDateTime);
//		
//		
//		int index = 0;
//		BigDecimal num = null;	// 大件数数量，由小单位数量除以换算关系
//		BigDecimal totalAmount = new BigDecimal(0);		// 所有产品总金额
//		BigDecimal singleAmount = new BigDecimal(0);	// 单个产品总金额：大件价格乘以大件数量
//		
//		for (Iterator<StockInRequestProduct> iterator = products.iterator(); iterator.hasNext();) {
//			index++;
//			StockInRequestProduct stockInRequestProduct = iterator.next();
//			String productCode = stockInRequestProduct.getCode();
//			Product product = ProductQuery.me().findbyProductSn(productCode);
//			num = new BigDecimal(stockInRequestProduct.getNum()).divide(new BigDecimal(product.getConvertRelate()), 2, BigDecimal.ROUND_HALF_UP);
//			singleAmount = stockInRequestProduct.getPrice().multiply(num);
//			totalAmount.add(singleAmount);
//			if(product != null) {
//				purchaseOrderDetail.set("id", StrKit.getRandomUUID());
//				purchaseOrderDetail.set("purchase_order_id", id);
//				purchaseOrderDetail.set("product_id", product.getId());
//				purchaseOrderDetail.set("product_count", stockInRequestProduct.getNum());
//				purchaseOrderDetail.set("product_amount", singleAmount);
//				purchaseOrderDetail.set("product_price", stockInRequestProduct.getPrice());
//				purchaseOrderDetail.set("order_list",index);
//				purchaseOrderDetail.set("create_date", nowDateTime);
//				purchaseOrderDetail.set("dept_id", seller.getDeptId());
//				purchaseOrderDetail.set("data_area", department.getDataArea());
//				purchaseOrderDetail.save();
//			}
//		}
//		
//		purchaseOrder.set("total_amount", totalAmount);
//		purchaseOrder.save();
//		renderAjaxResultForSuccess();
//	}
}
