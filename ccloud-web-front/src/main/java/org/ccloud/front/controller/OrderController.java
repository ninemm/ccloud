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
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
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

	public void myOrder() {
		
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
		render("myOrder.html");
	}

	public void orderList() {
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> orderList = SalesOrderQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("orderList", orderList.getList());
		renderJson(map);
	}

	public void orderDetail() {
		String orderId = getPara("orderId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);

		order.set("statusName", getStatusName(order.getInt("status")));

		setAttr("order", order);
		setAttr("orderDetailList", orderDetailList);
		render("orderDetail.html");
	}


	private String getStatusName (int statusCode) {
		if (statusCode == Consts.SALES_ORDER_STATUS_PASS) return "已审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_DEFAULT) return "待审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_CANCEL) return "取消";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT) return "部分出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT_CLOSE) return "部分出库-订单关闭";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT) return "全部出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT_CLOSE) return "全部出库-订单关闭";
		return "无";
	}
	
	public void getOldOrder() {
		String orderId = getPara("orderId");
		
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		setAttr("id", orderId);
		setAttr("order", order);
		render("order_again.html");
	}	
	
	public void orderAgain() {
		String orderId = getPara("orderId");
		
		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("orderDetail", orderDetail);
		renderJson(map);
	}
}
