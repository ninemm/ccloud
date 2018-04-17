/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.business;

import java.util.Iterator;
import java.util.List;

import org.ccloud.model.Department;
import org.ccloud.model.query.BusinessManagerOrderReportQuery;
import org.ccloud.model.query.DepartmentQuery;

import com.jfinal.plugin.activerecord.Record;

/**
 * 业务经理的部门订单报表
 * @author wally
 */
public class BusinessManagerOrderReportBiz {
	private static final BusinessManagerOrderReportBiz BIZ = new BusinessManagerOrderReportBiz();
	private BusinessManagerOrderReportBiz() {}
	
	public static BusinessManagerOrderReportBiz me() {
		return BIZ;
	}
	
	/**
	 * 查询业务经理下的部门订单报表
	 * @param startDate：开始时间
	 * @param endDate：结束时间
	 * @param dayTag：天数标识
	 * @param orderTag：订单标识
	 * @param dataArea：主管数据域
	 * @param print：是否打印时间
	 * @param receiveType：收账类型
	 * @return
	 */
	public List<Record> getMyDepartmentsOrderReport(String startDate, String endDate, String dayTag,
			String orderTag, String dataArea, String print, String receiveType) {
		List<Record> records = BusinessManagerOrderReportQuery.me().getMyDepartmentsOrderReport(startDate, endDate, dayTag, orderTag, dataArea, print, receiveType);
		if(records != null && records.size() > 0) {
			Record record = null;
			Department dept = null;
			String deptName = null;
			for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
				record = (Record) iterator.next();
				dept = DepartmentQuery.me().findById(String.valueOf(record.getColumns().get("id")));
				deptName = dept != null ? dept.getDeptName() : "";
				record.getColumns().put("dept_name", deptName);
				record = null;
				dept = null;
				deptName = null;
			}
		}
		return records;
	}
}
