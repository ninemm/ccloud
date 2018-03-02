package org.ccloud.model.excel;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.CustomerInfo;
import org.ccloud.model.OrderDetailInfo;
import org.ccloud.model.OrderInfo;
import org.ccloud.model.ProductInfo;
import org.ccloud.model.SellerInfo;
import org.ccloud.model.query.CustomerInfoQuery;
import org.ccloud.model.query.OrderInfoQuery;
import org.ccloud.model.query.ProductInfoQuery;
import org.ccloud.model.query.SellerInfoQuery;
import org.ccloud.model.vo.AliExcel;
import org.ccloud.model.vo.CustomerExcel;
import org.ccloud.model.vo.DanLuExcel;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;

public class ExcelUploadUtils {
	private static final Log log = Log.getLog(ExcelUploadUtils.class);
	
	@Before(Tx.class)
	public static int[] aliUpload(File file, ImportParams params, String sellerId) {
		int inCnt = 0;
		int existCnt = 0;
		String lastSn = "";
		String lastId = "";
		String lastCreateDate = "";
		String lastExistSn = "";		
		List<AliExcel> list = ExcelImportUtil.importExcel(file, AliExcel.class, params);
		while (list.size() > 0) {
			for (AliExcel excel : list) {
				String id = StrKit.getRandomUUID();
				String orderSn = excel.getOrder_sn();
				if (!orderSn.equals(lastSn)) {
					if (OrderInfoQuery.me().isExist(orderSn)) {
						existCnt++;
						lastExistSn = orderSn;
						continue;
					}
					OrderInfo order = new OrderInfo();
					OrderDetailInfo detailInfo = new OrderDetailInfo();
					order.setId(id);
					order.set("seller_id", sellerId);
					order.set("order_sn", excel.getOrder_sn());
					String sellerName = excel.getSellerName();
					String customerName = excel.getCustomerName();
					String address = excel.getAddress();
					String status = excel.getStatus();
					String productName = excel.getProductName();
					String productType = excel.getProductType();
					String recevieType = excel.getReceive_type();
					String productAmount = excel.getProductAmount();
					String contact = excel.getContact();
					String postalcode = excel.getPostalcode();
					String unit = excel.getUnit();
					
					order.set("pay_amount",excel.getPay_amount());
					order.set("total_amount",excel.getTotal_amount());
					order.set("delivery_address",excel.getAddress());
					order.set("pay_date",excel.getPay_date());
					order.set("coupon_reduce_price",excel.getCoupon_reduce_price());
					order.set("send_user_name",excel.getSend_user_name());
					order.set("create_date",excel.getCreate_date());
					order.set("change_price",excel.getChange_price());
					order.set("export_date", new Date());
					order.set("platform_name", Consts.TEMPLATE_NAME_ALI);
					CustomerInfo info = CustomerInfoQuery.me().findByAddress(customerName,address);
					if (info != null) {
						order.set("customer_info_id", info.getId());
					} else {
						order.set("customer_info_id", CustomerInfoQuery.me().insertCustomer(customerName, address, contact, postalcode));
						log.info("-----导入客户名称: " + customerName + "----客户地址: " + address);
					}
					SellerInfo sellerInfo = SellerInfoQuery.me().findByName(sellerName);
					
					if (sellerInfo != null) {
						order.set("seller_info_id", sellerInfo.getId());
					} else {
						order.set("seller_info_id", SellerInfoQuery.me().insertSeller(sellerName));
						log.info("-----导入销售商名称: " + sellerName);
					}
					
					if (recevieType == null) {
						order.set("receive_type", recevieType);
					} else if (recevieType.equals("赊购")) {
						order.set("receive_type", 2);
					} else if (recevieType.equals("现金")) {
						order.set("receive_type", 1);
					} else {
						order.set("receive_type", 0);
					}
					
					if (status.equals("交易关闭")) {
						order.set("status",3);
					} else if (status.equals("待确认收货")) {
						order.set("status",1);
					} else if (status.equals("待发货")) {
						order.set("status",0);
					} else if (status.equals("交易成功")) {
						order.set("status",2);
					} else if (status.equals("交易完成")) { 
						order.set("status",2);
					} else {
						order.set("status",excel.getStatus());
					}
					
					detailInfo.set("id", StrKit.getRandomUUID());
					detailInfo.set("order_id", id);
					ProductInfo productInfo = ProductInfoQuery.me().findByType(productName, productType);
					if (productInfo != null) {
						detailInfo.set("sell_product_id", productInfo.getId());
					} else {
						detailInfo.set("sell_product_id", ProductInfoQuery.me().
								insertProduct(productName, productType, productAmount, unit, null));
						log.info("-----导入产品名称: " + productName + "----产品单价: " + productAmount);
					}
					BigDecimal total_amount = new BigDecimal(productAmount).multiply(new BigDecimal(excel.getTotal_count()));
					
					detailInfo.set("relevance_sn", excel.getRelevance_sn());
					detailInfo.set("product_count", excel.getTotal_count());
					detailInfo.set("product_amount", total_amount.toString());
					detailInfo.set("product_price", productAmount);
					detailInfo.set("create_date", excel.getCreate_date());
					detailInfo.set("modify_date", new Date());
					order.save();
					log.info("-----导入订单编号: " + orderSn);
					detailInfo.save();
					log.info("-----导入子订单编号: " + orderSn);
					lastSn = excel.getOrder_sn();
					lastId = id;
					lastCreateDate = excel.getCreate_date();
				} else {
					if (orderSn.equals(lastExistSn)) {
						existCnt++;
						continue;
					}
					OrderDetailInfo detailInfo = new OrderDetailInfo();
					String productName = excel.getProductName();
					String productType = excel.getProductType();
					String productAmount = excel.getProductAmount();
					String unit = excel.getUnit();
					detailInfo.set("id", StrKit.getRandomUUID());
					detailInfo.set("order_id", lastId);
					ProductInfo productInfo = ProductInfoQuery.me().findByType(productName, productType);
					if (productInfo != null) {
						detailInfo.set("sell_product_id", productInfo.getId());
					} else {
						detailInfo.set("sell_product_id", ProductInfoQuery.me().
								insertProduct(productName, productType, productAmount, unit, null));
						log.info("-----导入产品名称: " + productName + "----产品单价: " + productAmount);
					}
					BigDecimal total_amount = new BigDecimal(productAmount).multiply(new BigDecimal(excel.getTotal_count()));
					
					detailInfo.set("relevance_sn", excel.getRelevance_sn());
					detailInfo.set("product_count", excel.getTotal_count());
					detailInfo.set("product_amount", total_amount.toString());
					detailInfo.set("product_price", productAmount);
					detailInfo.set("create_date", lastCreateDate);
					detailInfo.set("modify_date", new Date());
					detailInfo.save();
					log.info("-----导入子订单编号: " + orderSn);
				}
				
				inCnt++;
			}
			params.setStartRows(params.getStartRows() + list.size());
			list = ExcelImportUtil.importExcel(file, CustomerExcel.class, params);
		}
		
		int[] num = new int[2];
		num[0] = inCnt;
		num[1] = existCnt;
		return num;
	}
	
	@Before(Tx.class)
	public static int[] danLuUpload(File file, ImportParams params, String sellerId) {
		int inCnt = 0;
		int existCnt = 0;
		String lastSn = "";
		String lastId = "";
		String lastCreateDate = "";
		String lastExistSn = "";		
		List<DanLuExcel> list = ExcelImportUtil.importExcel(file, DanLuExcel.class, params);
		while (list.size() > 0) {
			for (DanLuExcel excel : list) {
				String id = StrKit.getRandomUUID();
				String orderSn = excel.getOrder_sn();
				if (!orderSn.equals(lastSn)) {
					if (OrderInfoQuery.me().isExist(orderSn)) {
						existCnt++;
						lastExistSn = orderSn;
						continue;
					}
					OrderInfo order = new OrderInfo();
					OrderDetailInfo detailInfo = new OrderDetailInfo();
					order.setId(id);
					order.set("seller_id", sellerId);
					order.set("order_sn", excel.getOrder_sn());
					String sellerName = excel.getSellerName();
					String customerName = excel.getCustomerName();
					String address = excel.getAddress();
					String status = excel.getStatus();
					String productName = excel.getProductName();
					String recevieType = excel.getReceive_type();
					String unit = excel.getUnit();
					String code = excel.getProduct_code();
					
					order.set("pay_amount",excel.getPay_amount());
					order.set("total_amount",excel.getTotal_amount());
					order.set("delivery_address",excel.getAddress());
					order.set("pay_date",excel.getPay_date());
					order.set("coupon_reduce_price",excel.getCoupon_reduce_price());
					order.set("send_user_name",excel.getSend_user_name());
					order.set("create_date",excel.getCreate_date());
					order.set("delivery_date",excel.getDelivery_date());
					order.set("change_price",excel.getChange_price());
					order.set("export_date", new Date());
					order.set("platform_name", Consts.TEMPLATE_NAME_DANLU);
					CustomerInfo info = CustomerInfoQuery.me().findByAddress(customerName,address);
					if (info != null) {
						order.set("customer_info_id", info.getId());
					} else {
						order.set("customer_info_id", CustomerInfoQuery.me().insertCustomer(customerName, address, null, null));
						log.info("-----导入客户名称: " + customerName + "----客户地址: " + address);
					}
					SellerInfo sellerInfo = SellerInfoQuery.me().findByName(sellerName);
					
					if (sellerInfo != null) {
						order.set("seller_info_id", sellerInfo.getId());
					} else {
						order.set("seller_info_id", SellerInfoQuery.me().insertSeller(sellerName));
						log.info("-----导入销售商名称: " + sellerName);
					}
					
					if (recevieType == null) {
						order.set("receive_type", recevieType);
					} else if (recevieType.equals("赊购")) {
						order.set("receive_type", 2);
					} else if (recevieType.equals("现金")) {
						order.set("receive_type", 1);
					} else {
						order.set("receive_type", 0);
					}
					
					if (status.equals("交易关闭")) {
						order.set("status",3);
					} else if (status.equals("待确认收货")) {
						order.set("status",1);
					} else if (status.equals("待发货")) {
						order.set("status",0);
					} else if (status.equals("交易成功")) {
						order.set("status",2);
					} else if (status.equals("交易完成")) { 
						order.set("status",2);
					} else {
						order.set("status",excel.getStatus());
					}
					
					detailInfo.set("id", StrKit.getRandomUUID());
					detailInfo.set("order_id", id);
					BigDecimal productPrice = new BigDecimal(excel.getProductAmount()).
							divide(new BigDecimal(excel.getTotal_count()), 2, BigDecimal.ROUND_HALF_UP);
					ProductInfo productInfo = ProductInfoQuery.me().findByType(productName, null);
					if (productInfo != null) {
						detailInfo.set("sell_product_id", productInfo.getId());
					} else {
						detailInfo.set("sell_product_id", ProductInfoQuery.me().
								insertProduct(productName, null, productPrice.toString(), unit, code));
						log.info("-----导入产品名称: " + productName + "----产品单价: " + productPrice);
					}
					
					detailInfo.set("relevance_sn", excel.getOrder_sn());
					detailInfo.set("product_count", excel.getTotal_count());
					detailInfo.set("product_amount", excel.getProductAmount());
					detailInfo.set("product_price", productPrice.toString());
					detailInfo.set("create_date", excel.getCreate_date());
					detailInfo.set("modify_date", new Date());
					order.save();
					log.info("-----导入订单编号: " + orderSn);
					detailInfo.save();
					log.info("-----导入子订单编号: " + orderSn);
					lastSn = excel.getOrder_sn();
					lastId = id;
					lastCreateDate = excel.getCreate_date();
				} else {
					if (orderSn.equals(lastExistSn)) {
						existCnt++;
						continue;
					}
					OrderDetailInfo detailInfo = new OrderDetailInfo();
					String productName = excel.getProductName();
					String unit = excel.getUnit();
					String code = excel.getProduct_code();
					detailInfo.set("id", StrKit.getRandomUUID());
					detailInfo.set("order_id", lastId);
					BigDecimal productPrice = new BigDecimal(excel.getProductAmount()).
							divide(new BigDecimal(excel.getTotal_count()), 2, BigDecimal.ROUND_HALF_UP);
					ProductInfo productInfo = ProductInfoQuery.me().findByType(productName, null);
					if (productInfo != null) {
						detailInfo.set("sell_product_id", productInfo.getId());
					} else {
						detailInfo.set("sell_product_id", ProductInfoQuery.me().
								insertProduct(productName, null, productPrice.toString(), unit, code));
						log.info("-----导入产品名称: " + productName + "----产品单价: " + productPrice);
					}
					
					detailInfo.set("relevance_sn", lastSn);
					detailInfo.set("product_count", excel.getTotal_count());
					detailInfo.set("product_amount", excel.getProductAmount());
					detailInfo.set("product_price", productPrice);
					detailInfo.set("create_date", lastCreateDate);
					detailInfo.set("modify_date", new Date());
					detailInfo.save();
					log.info("-----导入子订单编号: " + orderSn);
				}
				
				inCnt++;
			}
			params.setStartRows(params.getStartRows() + list.size());
			list = ExcelImportUtil.importExcel(file, CustomerExcel.class, params);
		}
		
		int[] num = new int[2];
		num[0] = inCnt;
		num[1] = existCnt;
		return num;
	}
	
}
