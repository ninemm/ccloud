package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;

import com.alibaba.fastjson.JSON;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by chen.xuebing on 2017/12/08.
 */
@RouterMapping(url = "/order")
public class OrderController extends BaseFrontController {

	String sellerId = "05a9ad0a516c4c459cb482f83bfbbf33";
	String sellerCode = "QG";
	User user = UserQuery.me().findById("1f797c5b2137426093100f082e234c14");
	String dataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());

	public void myOrder() {

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
		render("myOrder.html");
	}

	public void orderList() {

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> orderList = SalesOrderQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, dataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("orderList", orderList.getList());
		renderJson(map);
	}

	public void orderDetial() {
		render("myOrder.html");
	}

}
