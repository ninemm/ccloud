/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.query;

import java.util.LinkedList;
import java.util.List;

import org.ccloud.utils.DateUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * 业务员销售报表
 * @author wally
 */
public class SalesmanOrderReportQuery extends JBaseQuery {
	private static final SalesmanOrderReportQuery QUERY = new SalesmanOrderReportQuery();

	public static SalesmanOrderReportQuery me() {
		return QUERY;
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
	public List<Record> getSalesmanOrder(String startDate, String endDate, String dayTag,
			String userId, String orderTag, String print, String receiveType) {
		if (StrKit.notBlank(dayTag)) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder fromBuilder = new StringBuilder("SELECT IFNULL(SUM(cc.total_count),0) as productCount, IFNULL(sum(cc.total_amount),0) as totalAmount, ");
		fromBuilder.append("COUNT(*) as orderCount, cc.customer_id as customerId FROM cc_sales_order cc ");
		appendIfNotEmpty(fromBuilder, " cc.biz_user_id", userId, params, true);
		
		fromBuilder.append(" and EXISTS(SELECT os.status FROM cc_sales_order_status os ");
		fromBuilder.append("WHERE os.status = cc.status and os.status != 1001 and os.status != 1002) ");
		
		if (StrKit.notBlank(receiveType) && !receiveType.equals("all")) {
			appendIfNotEmpty(fromBuilder, "cc.receive_type", receiveType, params, Boolean.FALSE.booleanValue());
		}
		
		if (StrKit.notBlank(print)) {
			if (StrKit.notBlank(startDate)) {
				fromBuilder.append(" and cc.print_time >= ?");
				params.add(startDate);
			}

			if (StrKit.notBlank(endDate)) {
				fromBuilder.append(" and cc.print_time <= ?");
				params.add(endDate);
			}
		} else {
			if (StrKit.notBlank(startDate)) {
				fromBuilder.append(" and cc.create_date >= ?");
				params.add(startDate);
			}

			if (StrKit.notBlank(endDate)) {
				fromBuilder.append(" and cc.create_date <= ?");
				params.add(endDate);
			}
		}		

		fromBuilder.append(" GROUP BY cc.customer_id ");
		if (StrKit.notBlank(orderTag)) {
			fromBuilder.append(" ORDER BY "+ orderTag +" desc ");
		} else {
			fromBuilder.append(" ORDER BY totalAmount desc ");
		}
		if (params.isEmpty())
			return Db.find(fromBuilder.toString());

		return Db.find(fromBuilder.toString(), params.toArray());
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
		if (StrKit.notBlank(dayTag)) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT IFNULL(SUM(t1.totalAmount), 0) AS totalAmount,");
		sqlBuilder.append("IFNULL(SUM(t1.productCount), 0) AS productCount,");
		sqlBuilder.append("IFNULL(t1.orderCount, 0) AS orderCount, u.id ");
		sqlBuilder.append("FROM `user` u LEFT JOIN (");
		sqlBuilder.append("SELECT SUM(cc.total_amount) AS totalAmount,");
		sqlBuilder.append("SUM(cc.total_count) AS productCount,");
		sqlBuilder.append("COUNT(1) AS orderCount,cc.biz_user_id ");
		sqlBuilder.append("FROM cc_sales_order cc WHERE cc.dept_id = ? ");
		params.add(deptId);
		sqlBuilder.append("AND EXISTS(SELECT os.`status` FROM cc_sales_order_status os ");
		sqlBuilder.append("WHERE os.status = cc.status and os.status != 1001 and os.status != 1002) ");
		if (StrKit.notBlank(receiveType) && !receiveType.equals("all")) {
			appendIfNotEmpty(sqlBuilder, "cc.receive_type", receiveType, params, Boolean.FALSE.booleanValue());
		}
		sqlBuilder.append("AND cc.create_date >= ? AND cc.create_date <= ? ");
		sqlBuilder.append("GROUP BY cc.biz_user_id)  t1 ON u.id = t1.biz_user_id ");
		sqlBuilder.append("WHERE u.department_id = ? GROUP BY u.id ");
		params.add(startDate);
		params.add(endDate);
		params.add(deptId);
		if (StrKit.notBlank(orderTag)) {
			sqlBuilder.append("ORDER BY "+ orderTag + " desc ");
		} else {
			sqlBuilder.append("ORDER BY totalAmount desc ");
		}
		
		if (params.isEmpty())
			return Db.find(sqlBuilder.toString());

		return Db.find(sqlBuilder.toString(), params.toArray());
	}
}
