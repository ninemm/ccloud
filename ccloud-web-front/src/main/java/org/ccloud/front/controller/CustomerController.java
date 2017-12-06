package org.ccloud.front.controller;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WT on 2017/11/29.
 */
@RouterMapping(url = "/customer")
public class CustomerController extends BaseFrontController {

	public void index() {

		User user = getUser();

		Page<Record> customerList = SellerCustomerQuery.me().findByUserTypeForApp(getPageNumber(), getPageSize(), getUserIdList(user), getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

		setAttr("customerList", customerList);

		render("customer.html");
	}

	public void getCustomerRegionAndType() {

		User user = getUser();

		List<Record> userList = UserQuery.me().findNextLevelsUserList(user.getDataArea());
		List<Map<String, Object>> region = new ArrayList<>();
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		region.add(all);
		for(Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			region.add(item);
		}

		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(getUserDeptDataArea(user.getDataArea()));
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();
		customerTypeList2.add(all);

		for(CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		Map<String, List<Map<String, Object>>> data = ImmutableMap.of("region", region, "customerType", customerTypeList2);
		renderJson(data);
	}

	public void refresh() {
		User user = getUser();

		Page<Record> customerList = new Page<>();
		if (StrKit.notBlank(getPara("region"))) {
			Object[] region = {getPara("region")};
			 customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), region, getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));
		} else customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getUserIdList(user), getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

		StringBuilder html = new StringBuilder();
		for (Record customer : customerList.getList())
		{
			html.append("                    <div class=\"weui-panel weui-panel_access\">\n" +
					"                        <div class=\"weui-flex\">\n" +
					"                            <div class=\"weui-flex__item customer-info\">\n" +
					"                                <p class=\"ft14\">" + customer.getStr("customer_name") + "</p>\n" +
					"                                <p class=\"gray\">" + customer.getStr("contact") + "/" + customer.getStr("mobile")+ "</p>\n" +
					"                            </div>\n" +
					"                            <div class=\"weui-flex__item customer-href\">\n" +
					"                                <div class=\"weui-flex\">\n" +
					"                                    <a href=\"tel:\"" + customer.getStr("mobile") + " class=\"weui-flex__item\">\n" +
					"                                        <p>\n" +
					"                                            <i class=\"icon-phone green\"></i>\n" +
					"                                        </p>\n" +
					"                                        <p>电话</p>\n" +
					"                                    </a>\n" +
					"                                    <a class=\"weui-flex__item\" href=\"./historyOrder.html?customer_id=" + customer.getStr("id") + "&customer_name=" + customer.getStr("customer_name") + "\">\n" +
					"                                        <p>\n" +
					"                                            <i class=\"icon-file-text-o blue\"></i>\n" +
					"                                        </p>\n" +
					"                                        <p>订单</p>\n" +
					"                                    </a>\n" +
					"                                    <a class=\"weui-flex__item\" href=\"visitAdd.html\">\n" +
					"                                        <p>\n" +
					"                                            <i class=\"icon-paw\" style=\"color:#ff9800\"></i>\n" +
					"                                        </p>\n" +
					"                                        <p>拜访</p>\n" +
					"                                    </a>\n" +
					"                                    <a class=\"weui-flex__item relative\" href=\"./customerDetail.html\">\n" +
					"                                        <i class=\"icon-chevron-right gray\"></i>\n" +
					"                                    </a>\n" +
					"                                </div>\n" +
					"                            </div>\n" +
					"                        </div>\n" +
					"                        <hr />\n" +
					"                        <div class=\"weui-flex space-between\">\n" +
					"                            <div class=\"button blue-button\">下订单</div>\n" +
					"                            <div class=\"button blue-button\">客户拜访</div>\n" +
					"                        </div>\n" +
					"                        <p class=\"gray\">\n" +
					"                            <span class=\"icon-map-marker ft16 green\"></span>\n" +
					                            customer.getStr("prov_name") + " " + customer.getStr("city_name") + " " + customer.getStr("country_name") + " " + customer.getStr("address") + "\n" +
					"                        </p>\n" +
					"                    </div>\n" );
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", customerList.getTotalRow());
		map.put("totalPage", customerList.getTotalPage());
		renderJson(map);
	}

	private User getUser(){
		User user = UserQuery.me().findById("1f797c5b2137426093100f082e234c14");
		return user;
	}

	private String getUserDeptDataArea(String dataArea) {
		if (dataArea.length() % 3 != 0) {
			return DataAreaUtil.getUserDeptDataArea(dataArea);
		} else return dataArea;
	}

	private Object[] getUserIdList(User user) {
		List<Record> userList = UserQuery.me().findNextLevelsUserList(user.getDataArea());
		if (userList.size() == 0) return null;

		Object[] userIdList = new Object[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			userIdList[i] = userList.get(i).getStr("id");
		}
		return userIdList;
	}
}
