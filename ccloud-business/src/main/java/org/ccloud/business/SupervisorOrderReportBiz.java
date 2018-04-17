/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.business;

import java.util.Iterator;
import java.util.List;

import org.ccloud.model.User;
import org.ccloud.model.query.SupervisorOrderReportQuery;
import org.ccloud.model.query.UserQuery;

import com.jfinal.plugin.activerecord.Record;

/**
 * 主管的业务员订单报表
 * @author wally
 *
 */
public class SupervisorOrderReportBiz {
	private static final SupervisorOrderReportBiz BIZ = new SupervisorOrderReportBiz();
	private SupervisorOrderReportBiz() {}
	
	public static SupervisorOrderReportBiz me() {
		return BIZ;
	}
	
	/**
	 * 查询主管的业务员订单报表
	 * @param startDate：开始时间
	 * @param endDate：结束时间
	 * @param dayTag：天数标识
	 * @param orderTag：订单标识
	 * @param dataArea：主管数据域
	 * @param print：是否打印时间
	 * @param receiveType：收账类型
	 * @return
	 */
	public List<Record> getMySalesmenOrderReport(String startDate, String endDate, String dayTag,
			String orderTag, String dataArea, String print, String receiveType) {
		List<Record> records = SupervisorOrderReportQuery.me().getMySalesmenOrderReport(startDate, endDate, dayTag, orderTag, dataArea, print, receiveType);
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
	
	/**
	 * 查询主管下业务员的订单销售排行
	 * @param startDate：开始时间
	 * @param endDate：结束时间
	 * @param dayTag：天数标识
	 * @param orderTag：订单标识
	 * @param dataArea：主管的数据域
	 * @param receiveType：收款类型
	 * @return
	 */
	public List<Record> getOrderRankOfMySalesmen(String startDate, String endDate, String dayTag,
			String orderTag, String dataArea, String receiveType) {
		List<Record> records = SupervisorOrderReportQuery.me().getOrderRankOfMySalesmen(startDate, endDate, dayTag, orderTag, dataArea, receiveType);
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
