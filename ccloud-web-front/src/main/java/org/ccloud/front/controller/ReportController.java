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
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("report.html");
	}
	
	public void orderAmount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String customerType = getPara("customerType");
		String deptId = getPara("deptId");
		Record record = SalesOrderQuery.me().getMyOrderAmount(startDate, endDate, dayTag, customerType, deptId, sellerId, dataArea);
		renderJson(record);
	}
	
	public void orderTypeCount() {
		String dayTag = "today";
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> record = SalesOrderQuery.me().getMyOrderStatucCount(null, null, dayTag, sellerId, dataArea);
		renderJson(record);
	}
	
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
	
	public void productCount() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");	
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String productType = getPara("productType");
		List<Record> record = SalesOrderQuery.me().getMyOrderByProduct(startDate, endDate, dayTag, productType, sellerId, dataArea);
		renderJson(record);
	}
	
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
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
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
	
	public void managerReport() {
		render("manager_report.html");
	}	
	
	public void getUserRank() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dayTag = getPara("dayTag");
		String sellerId = getSessionAttr("sellerId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());
		String deptId = getPara("deptId");
		List<Record> record = SalesOrderQuery.me().getUserRank(startDate, endDate, dayTag, deptId, sellerId, dataArea);
		renderJson(record);
	}
	
}
