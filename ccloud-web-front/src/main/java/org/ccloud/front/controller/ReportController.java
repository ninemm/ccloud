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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/report")
public class ReportController extends BaseFrontController {
	
	public void index() {
		render("report.html");
	}
	
	public void sales() {
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("report_sales.html");
	}
	
	//业务员订单总额统计
	public void orderAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String customerType = getPara("customerType");
		String deptId = getPara("deptId");
		String userId = getPara("userId");
		Record record = SalesOrderQuery.me().getMyOrderAmount(startDate, endDate, dayTag, customerType, deptId, sellerId, userId, dataArea);
		renderJson(record);
	}
	
	//业务员订单状态统计
	public void orderTypeCount() {
		String dayTag = "today";
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getMyOrderStatucCount(null, null, dayTag, sellerId, dataArea);
		renderJson(record);
	}
	
	//业务员客户统计
	public void customerCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String customerType = getPara("customerType");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getMyOrderByCustomer(startDate, endDate, dayTag, customerType, sellerId, dataArea);
		renderJson(record);
	}
	
	//业务员产品种类统计
	public void productCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		String userId = getPara("userId");
		String isGift = getPara("isGift");
		List<Record> record = SalesOrderQuery.me().getMyOrderByProduct(startDate, endDate, dayTag, productType, sellerId, userId, isGift, dataArea);
		renderJson(record);
	}
	
	//业务员产品总额统计
	public void productAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		Record record = SalesOrderQuery.me().getProductAmountByMyOrder(startDate, endDate, dayTag, productType, sellerId, dataArea);
		renderJson(record);
	}	
	
	//业务员退货统计
	public void refundCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Record record = SalesOrderQuery.me().getMyRefundOrderCount(startDate, endDate, dayTag, sellerId, dataArea);
		renderJson(record);
	}
	
	public void customerDetail() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}
		setAttr("customerTypes", JSON.toJSON(customerTypes));			
		
		render("report_customer.html");
	}
	
	public void productDetail() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		
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
		
		render("report_product.html");
	}
	
	public void userRank() {
		render("user_rank.html");
	}
	
	public void purchase() {
		render("purchase.html");
	}
	
	public void mySeller() {
		render("mySeller.html");
	}	
	
	public void managerReport() {
		render("manager_report.html");
	}
	
	public void userReportDetail() {
		String userId = getPara("userId");
		String userName = getPara("userName");
        try {
			setAttr("userName", new String(userName.getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		setAttr("userId", userId);
		render("user_report.html");
	}
	
	public void sellerReportDetail() {
		String sellerId = getPara("sellerId");
		String sellerName = getPara("sellerName");
        try {
			setAttr("sellerName", new String(sellerName.getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		setAttr("sellerId", sellerId);
		render("seller_report.html");
	}
	
	public void sellerPurchaseReport() {
		String customerId = getPara("customerId");
		String sellerName = getPara("sellerName");
        try {
			setAttr("sellerName", new String(sellerName.getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
		setAttr("customerId", customerId);
		render("seller_purchase_report.html");
	}		
	
	//经销商下或部门下业务员排行榜
	public void getUserRank() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
		String deptId = getPara("deptId");
		String orderTag = getPara("orderTag");
		List<Record> record = SalesOrderQuery.me().getUserRank(startDate, endDate, dayTag, deptId, sellerId, orderTag, dataArea);
		renderJson(record);
	}
	
	//经销商或部门下业务员赠品统计
	public void getGiftCountByUser() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
		String deptId = getPara("deptId");
		List<Record> record = SalesOrderQuery.me().getGiftCountByUser(startDate, endDate, dayTag, deptId, sellerId, dataArea);
		renderJson(record);
	}
	
	//经销商下直营商赠品统计
	public void getGiftCountBySeller() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getGiftCountBySeller(startDate, endDate, dayTag, dataArea);
		renderJson(record);
	}	
	
	//经销商下直营商总额统计
	public void getSellerCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getSellerCount(startDate, endDate, dayTag, sellerId, dataArea);
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
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getSellerPurchase(startDate, endDate, dayTag, sellerId, dataArea);
		renderJson(record);
	}
	
	//直营商采购单赠品统计
	public void getSellerPurchaseGift() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getSellerPurchaseGift(startDate, endDate, dayTag, sellerId, dataArea);
		renderJson(record);
	}
	
}
