/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@126.com).
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
package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/report")
public class ReportController extends BaseFrontController {
	
	public void index() {
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		String startDate = date + " 00:00:00";
		String endDate =  date + " 23:59:59";
		int orderCount = SalesOrderQuery.me().findOrderCount(selectDataArea, startDate, endDate);
		int customerCount = SellerCustomerQuery.me().getMySellerNum(selectDataArea);
		setAttr("orderNum", orderCount);
		setAttr("customerNum", customerCount);	
		render("report.html");
	}

	public void userRank() {
		render("user_rank.html");
	}
	
	public void departmenUserRank() {
		render("departmen_user_rank.html");
	}
	
	public void purchase() {
		render("purchase.html");
	}
	
	public void mySeller() {
		render("mySeller.html");
	}	
	
	public void customerVisitReport() {
		render("report_customer_visit.html");
	}
	
	public void managerReport() {
		render("manager_report.html");
	}
	
	public void departmentReport() {
		render("department_report.html");
	}
	
	public void otherOrder() {
		render("other_order_report.html");
	}
	
	public void sales() {
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("report_sales.html");
	}
	
	public void receivables() {
		render("receivables_report.html");
	}
	
	//经销商下或部门下业务员排行榜状态选择
	public void orderAmountMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			orderAmountByOutStock();
		} else {
			orderAmount();
		}
	}

	//业务员订单总额统计(订单或打印)
	public void orderAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String customerType = getPara("customerType");
		String deptId = getPara("deptId");
		String userId = getPara("userId");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		String receiveType = getPara("receiveType");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}
		Record record = SalesOrderQuery.me().getMyOrderAmount(startDate, endDate, dayTag, customerType, deptId, null, userId, dataArea, print,receiveType);
		renderJson(record);
	}
	
	//业务员订单总额统计(出库)
	public void orderAmountByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String customerType = getPara("customerType");
		String deptId = getPara("deptId");
		String userId = getPara("userId");
		String receiveType = getPara("receiveType");
		Record record = SalesOrderQuery.me().getMyOrderAmountByOutStock(startDate, endDate, dayTag, customerType, deptId, null, userId, dataArea,receiveType);
		renderJson(record);		
	}
	
	//业务员订单状态统计
	public void orderTypeCount() {
		String dayTag = getPara("dayTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");		
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getMyOrderStatucCount(startDate, endDate, dayTag, null, dataArea,receiveType);
		renderJson(record);
	}
	
	//业务员客户目录
	public void customerCountMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			customerCountByOutStock();
		} else {
			customerCount();
		}
	}
	
	//业务员客户统计(订单与打印)
	public void customerCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String customerType = getPara("customerType");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String userId = getPara("userId");
		String orderTag = getPara("orderTag");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		String receiveType = getPara("receiveType");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		List<Record> record = SalesOrderQuery.me().getMyOrderByCustomer(startDate, endDate, dayTag, customerType, null, userId, dataArea, orderTag, print,receiveType);
		renderJson(record);
	}
	
	//业务员客户统计(出库)
	public void customerCountByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String customerType = getPara("customerType");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String userId = getPara("userId");
		String orderTag = getPara("orderTag");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getMyOrderByCustomerOut(startDate, endDate, dayTag, customerType, null, userId, dataArea, orderTag,receiveType);
		renderJson(record);		
	}
	
	public void productCountMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			productCountByOutStock();
		} else {
			productCount();
		}
	}
	
	//业务员产品种类统计(订单或打印)
	public void productCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		String userId = getPara("userId");
		String isGift = getPara("isGift");
		String customerId = getPara("customerId");
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		String isHide = getPara("isHide");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		String receiveType = getPara("receiveType");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		List<Record> record = SalesOrderQuery.me().getMyOrderByProduct(startDate, endDate, dayTag, productType, null, userId, customerId, isGift, dataArea, deptId, orderTag, isHide, print,receiveType);
		renderJson(record);
	}
	
	//业务员产品种类统计(出库)
	public void productCountByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		String userId = getPara("userId");
		String isGift = getPara("isGift");
		String customerId = getPara("customerId");
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		String isHide = getPara("isHide");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getMyOrderByProductOut(startDate, endDate, dayTag, productType, null, userId, customerId, isGift, dataArea, deptId, orderTag, isHide,receiveType);
		renderJson(record);		
	}
	
	//业务员产品总额统计
	public void productAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		Record record = SalesOrderQuery.me().getProductAmountByMyOrder(startDate, endDate, dayTag, productType, null, dataArea);
		renderJson(record);
	}	
	
	//业务员退货统计
	public void refundCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String receiveType = getPara("receiveType");
		Record record = SalesOrderQuery.me().getMyRefundOrderCount(startDate, endDate, dayTag, null, dataArea,receiveType);
		renderJson(record);
	}
	
	public void customerDetail() {
		String dayTag = getPara("dayTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayText = dayText(dayTag);
		String receiveType = getPara("receiveType");
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		setAttr("dayTag", dayTag);
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("dayText", dayText);
		setAttr("receiveType", receiveType);
		render("report_customer.html");
	}
	
	public void productDetail() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dayTag = getPara("dayTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayText = dayText(dayTag);
		String receiveType = getPara("receiveType");
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> productTypes = new ArrayList<>();
		productTypes.add(all);		

		List<Record> productTypeList = SellerProductQuery.me().findProductTypeBySellerForApp(sellerId);
		for (Record record : productTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.getStr("name"));
			item.put("value", record.getStr("id"));
			productTypes.add(item);
		}
		setAttr("productTypeList", JSON.toJSON(productTypes));
		setAttr("dayTag", dayTag);
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("dayText", dayText);
		setAttr("receiveType", receiveType);
		render("report_product.html");
	}
	
	private String dayText(String day) {
		String dayText = "";
		if (day.equals("today")) {
			dayText = "今日";
		} else if (day.equals("yesterday")) {
			dayText = "昨日";
		} else if (day.equals("week")) {
			dayText = "本周";
		} else if (day.equals("lastweek")) {
			dayText = "上周";
		} else if (day.equals("month")) {
			dayText = "本月";
		} else {
			dayText = "上月";
		}
		return dayText;
	}
	
	public void userReportDetail() {
		String userId = getPara("userId");
		String dayTag = getPara("dayTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String receiveType = getPara("receiveType");
		setAttr("userName", UserQuery.me().findById(userId).getRealname());
		setAttr("userId", userId);
		setAttr("dayTag", dayTag);
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("receiveType", receiveType);
		render("user_report.html");
	}
	
	public void sellerReportDetail() {
		String sellerId = getPara("sellerId");
		String dayTag = getPara("dayTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");		
		String receiveType = getPara("receiveType");
		setAttr("sellerName", SellerQuery.me().findById(sellerId).getSellerName());
		setAttr("sellerId", sellerId);
		setAttr("dayTag", dayTag);
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);		
		setAttr("receiveType", receiveType);		
		render("seller_report.html");
	}
	
	public void sellerPurchaseReport() {
		String customerId = getPara("customerId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");		
		setAttr("sellerName", SellerCustomerQuery.me().findById(customerId).getStr("customer_name"));
		setAttr("customerId", customerId);
		setAttr("dayTag", dayTag);
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);		
		render("seller_purchase_report.html");
	}
	
	//经销商下或部门下业务员排行榜状态选择
	public void getUserRankMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			getUserRankByOutStock();
		} else {
			getUserRank();
		}
	}
	
	//经销商下或部门下业务员排行榜(订单或打印 ps:剔除没下单用户)
	public void getUserRank() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getUserRank(startDate, endDate, dayTag, deptId, sellerId, orderTag, dataArea, print,receiveType);
		renderJson(record);
	}
	
	//业务员销售排行榜
	public void getDepartmenUserRank() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String orderTag = getPara("orderTag");
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getUserRankZero(startDate, endDate, dayTag, sellerId, orderTag, dataArea,receiveType);
		renderJson(record);
	}
	
	//经销商下或部门下业务员排行榜(出库 ps:剔除没下单用户)
	public void getUserRankByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		List<Record> record = SalesOrderQuery.me().getUserRankByOutStock(startDate, endDate, dayTag, deptId, sellerId, orderTag, dataArea);
		renderJson(record);
	}
	
	//经销商下或部门下业务员排行榜(包含没下单用户)
	public void getUserRankZero() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getUserRankZero(startDate, endDate, dayTag, sellerId, orderTag, dataArea,receiveType);
		renderJson(record);
	}	
	
	public void getGiftCountByUserMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			getGiftCountByUserByOutStock();
		} else {
			getGiftCountByUser();
		}
	}
	
	//经销商或部门下业务员赠品统计(订单或打印)
	public void getGiftCountByUser() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getGiftCountByUser(startDate, endDate, dayTag, deptId, sellerId, dataArea, orderTag, print,receiveType);
		renderJson(record);
	}
	
	//经销商或部门下业务员赠品统计(出库)
	public void getGiftCountByUserByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
//		if (StrKit.isBlank(sellerId)) {
//			sellerId = getSessionAttr("sellerId");
//		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getGiftCountByUserByOutStock(startDate, endDate, dayTag, deptId, sellerId, dataArea, orderTag,receiveType);
		renderJson(record);
	}
	
	public void getGiftCountBySellerMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			getGiftCountBySellerByOutStock();
		} else {
			getGiftCountBySeller();
		}
	}
	
	//经销商下直营商赠品统计(订单或打印)
	public void getGiftCountBySeller() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getGiftCountBySeller(startDate, endDate, dayTag, dataArea, orderTag, print,receiveType);
		renderJson(record);
	}
	
	//经销商下直营商赠品统计(出库)
	public void getGiftCountBySellerByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getGiftCountBySellerByOutStock(startDate, endDate, dayTag, dataArea, orderTag,receiveType);
		renderJson(record);
	}	
	
	public void getSellerCountMenu() {
		String typeTag = getPara("typeTag");
		if (typeTag.equals("outStock")) {
			getSellerCountByOutStock();
		} else {
			getSellerCount();
		}
	}
	
	//经销商下直营商总额统计(订单或打印)
	public void getSellerCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		String print = getPara("print");
		String typeTag = getPara("typeTag");
		if (typeTag != null && typeTag.equals("print")) {
			print = "true";
		}		
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getSellerCount(startDate, endDate, dayTag, null, dataArea, orderTag, print,receiveType);
		renderJson(record);
	}
	
	//经销商下直营商总额统计(出库)
	public void getSellerCountByOutStock() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		String receiveType = getPara("receiveType");
		List<Record> record = SalesOrderQuery.me().getSellerCountByOutStock(startDate, endDate, dayTag, null, dataArea, orderTag,receiveType);
		renderJson(record);
	}	
	
	//直营商产品统计
	public void sellerProductCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
		String productType = getPara("productType");
		String isGift = getPara("isGift");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String customerId = getPara("customerId");
		List<Record> record = SalesOrderQuery.me().sellerProductCount(startDate, endDate, dayTag, productType, sellerId, isGift, customerId, dataArea);
		renderJson(record);
	}
	
	//直营商订单总额统计
	public void sellerOrderAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getPara("sellerId");
		String customerType = getPara("customerType");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String purchase = getPara("purchase");
		String customerId = getPara("customerId");
		Record record = SalesOrderQuery.me().sellerOrderAmount(startDate, endDate, dayTag, customerType, sellerId, purchase, customerId, dataArea);
		renderJson(record);
	}
	
	//直营商采购单统计
	public void getSellerPurchase() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getSellerPurchase(startDate, endDate, dayTag, null, dataArea);
		renderJson(record);
	}
	
	//直营商采购单赠品统计
	public void getSellerPurchaseGift() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getSellerPurchaseGift(startDate, endDate, dayTag, null, dataArea);
		renderJson(record);
	}
	
	//部门销售单统计
	public void getDepartmentCountForSales() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
//		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String orderTag = getPara("orderTag");
		List<Record> record = SalesOrderQuery.me().getDepartmentCount(startDate, endDate, dayTag, null, dataArea, orderTag);
		renderJson(record);		
	}
	
	//第三方订单金额统计
	public void getOtherOrderCount() {
		String platformTag = getPara("platformTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		Record record = OrderInfoQuery.me().getCountInfo(null, startDate, endDate, platformTag, null, null, dayTag, sellerId);
		renderJson(record);
	}
	
	//第三方产品单统计
	public void getOtherProductCount() {
		String platformTag = getPara("platformTag");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		List<Record> record = OrderDetailInfoQuery.me().getOtherProductCount(startDate, endDate, sellerId, dayTag, platformTag);
		renderJson(record);		
	}
	
	//业务员统计客户数、客户总拜访数
		public void customerInfoCount() {
			String dayTag = getPara("dayTag");
			String sellerId = getSessionAttr("sellerId");
			List<SellerCustomer> sellerCustomers = SellerCustomerQuery.me()._findBySellerId(sellerId); 
			int customerVisitCount =  CustomerVisitQuery.me()._findBySellerId(sellerId,dayTag);
			
			Map<String, Object> item = new HashMap<>();
			item.put("customerCount", sellerCustomers.size());
			item.put("customerVisitCount",customerVisitCount);
			renderJson(item);
		}
		//统计已拜访客户数、未拜访的客户数
		public void customerVisitCount() {
			String sellerId = getSessionAttr("sellerId");
			String dayTag = getPara("dayTag");
			//已拜访客户
			int customerVisitNum = CustomerVisitQuery.me().findBySellerId(sellerId,dayTag);
			//总客户数
			int customerNum = SellerCustomerQuery.me()._findBySellerId(sellerId).size();
			List<Map<String, Object>> customeVisitList = new ArrayList<>();
			for(int i = 0 ; i < 2 ; i++) {
				Map<String, Object> item = new HashMap<>();
				if(i == 0) {
					item.put("name", "已拜访数");
					item.put("value", customerVisitNum);
				}else {
					item.put("name", "未拜访数");
					item.put("value", customerNum-customerVisitNum);
				}
				customeVisitList.add(item);
			}
			renderJson(customeVisitList);
		}
		//统计多次拜访
		public void visitMoreInfo() {
			String sellerId = getSessionAttr("sellerId");
			String dayTag = getPara("dayTag");
			List<Record> lists = CustomerVisitQuery.me().getBySellerId(sellerId,dayTag);
			renderJson(lists);
		}
		
		//统计不同拜访订单金额的平均值 统计已经出库的订单
		public void getSales() {
			String sellerId  = getSessionAttr("sellerId");
			String dayTag = getPara("dayTag");
			List<Record> lists = CustomerVisitQuery.me().getAmountBySellerId(sellerId,dayTag);
			renderJson(lists);
		}
		
		public void receivableList() {
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			List<Record> lists = ReceivablesQuery.me().findByDataArea(dataArea);
			renderJson(lists);
		}
}

