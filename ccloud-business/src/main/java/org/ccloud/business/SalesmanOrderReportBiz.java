/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.business;

import java.util.Iterator;
import java.util.List;

import org.ccloud.model.Customer;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.SalesmanOrderReportQuery;

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
			Customer customer = null;
			for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
				record = (Record) iterator.next();
				customer = CustomerQuery.me().findById(String.valueOf(record.getColumns().get("customerId")));
				if(customer != null) {
					record.getColumns().put("customer_name", customer.getCustomerName());
				}
				record = null;
				customer = null;
			}
		}
		return records;
	}
}
