/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.business;

import java.util.Iterator;
import java.util.List;

import org.ccloud.model.Customer;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.SalesmanOrderReportQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserQuery;

import com.jfinal.plugin.activerecord.Record;

/**
 * 业务员的客户订单报表
 * @author wally
 *
 */
public class SalesmanOrderReportBiz {
	private static final SalesmanOrderReportBiz BIZ = new SalesmanOrderReportBiz();
	private SalesmanOrderReportBiz() {}
	
	public static SalesmanOrderReportBiz me() {
		return BIZ;
	}
	
	/**
	 * 查询当前登录业务员的客户订单报表
	 * @param startDate：开始时间
	 * @param endDate：结束时间
	 * @param dayTag：天数标识，今天，前天等
	 * @param userId：登录业务员ID
	 * @param orderTag：订单排序属性
	 * @param print：是否按打印时间查询
	 * @param receiveType：收账类型
	 * @return
	 */
	public List<Record> getMyOrderReport(String startDate, String endDate, String dayTag,
			String userId, String orderTag, String print, String receiveType) {
		List<Record> records = SalesmanOrderReportQuery.me().getSalesmanOrder(startDate, endDate, dayTag, userId, orderTag, print, receiveType);
		if (records != null && records.size() > 0) {
			Record record = null;
			SellerCustomer sellerCustomer = null;
			String customerName = null;
			Customer customer = null;
			for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
				record = (Record) iterator.next();
				sellerCustomer = SellerCustomerQuery.me().findById( String.valueOf(record.getColumns().get("customerId")));
				customer = CustomerQuery.me().findById(sellerCustomer.getCustomerId());
				customerName = sellerCustomer != null ? customer.getCustomerName() : "";
				record.getColumns().put("customer_name", customerName);
				record = null;
				customer = null;
				customerName = null;
			}
		}
		return records;
	}
	
	
	/**
	 * 查询登录业务员所在部门的订单销售排行
	 * @param startDate：开始时间
	 * @param endDate：结束时间
	 * @param dayTag：天数标识
	 * @param orderTag：订单标识
	 * @param deptId：登录业务员所在部门ID
	 * @param receiveType：收款类型
	 * @return
	 */
	public List<Record> getOrderRankOfMyDepartment(String startDate, String endDate, String dayTag,
			String orderTag, String deptId, String receiveType) {
		List<Record> records = SalesmanOrderReportQuery.me().getOrderRankOfMyDepartment(startDate, endDate, dayTag, orderTag, deptId, receiveType);
		if(records != null && records.size() > 0) {
			Record record = null;
			User user = null;
			String realname = null;
			String avatar = null;
			for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
				record = (Record) iterator.next();
				user = UserQuery.me().findById(String.valueOf(record.getColumns().get("id")));
				realname = user != null ? user.getRealname() : "";
				avatar = user != null ? user.getAvatar() : "";
				record.getColumns().put("realname", realname);
				record.getColumns().put("avatar", avatar);
				record = null;
				user = null;
				realname = null;
				avatar = null;
			}
		}
		return records;
	}
}
