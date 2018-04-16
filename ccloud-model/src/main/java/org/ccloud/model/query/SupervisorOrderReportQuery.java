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
 * 主管的业务员订单报表
 * @author wally
 *
 */
public class SupervisorOrderReportQuery extends JBaseQuery {
	private static final SupervisorOrderReportQuery QUERY = new SupervisorOrderReportQuery();

	public static SupervisorOrderReportQuery me() {
		return QUERY;
	}
	
	public List<Record> getMySalesmenOrderReport(String startDate, String endDate, String dayTag,
			String orderTag, String dataArea, String print, String receiveType) {
		if (StrKit.notBlank(dayTag)) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder fromBuilder = new StringBuilder("SELECT IFNULL(SUM(cc.total_amount),0) as totalAmount, ");
		fromBuilder.append("IFNULL(SUM(cc.total_count),0) as productCount, COUNT(cc.id) as orderCount, ");
		fromBuilder.append("cc.biz_user_id as id FROM cc_sales_order cc where EXISTS");
		fromBuilder.append("(SELECT u.id FROM `user` u WHERE u.id = cc.biz_user_id ");
		fromBuilder.append("and u.data_area like ?) AND EXISTS");
		params.add(dataArea);
		boolean needWhere = true;
		
		fromBuilder.append("(SELECT os.status FROM cc_sales_order_status os WHERE ");
		fromBuilder.append("os.status = cc.status and os.status != 1001 and os.status != 1002) ");
		if (StrKit.notBlank(receiveType)&&!receiveType.equals("all")) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc.receive_type", receiveType, params, needWhere);
		}
		if (StrKit.notBlank(print)) {
			if (StrKit.notBlank(startDate)) {
				fromBuilder.append(" and cc.print_time >= ? ");
				params.add(startDate);
			}

			if (StrKit.notBlank(endDate)) {
				fromBuilder.append(" and cc.print_time <= ? ");
				params.add(endDate);
			}
		} else {
			if (StrKit.notBlank(startDate)) {
				fromBuilder.append(" and cc.create_date >= ? ");
				params.add(startDate);
			}

			if (StrKit.notBlank(endDate)) {
				fromBuilder.append(" and cc.create_date <= ? ");
				params.add(endDate);
			}
		}

		fromBuilder.append("GROUP BY cc.biz_user_id ");
		
		if (StrKit.notBlank(orderTag)) {
			fromBuilder.append("ORDER BY "+ orderTag + " desc ");
		} else {
			fromBuilder.append("ORDER BY totalAmount desc ");
		}
		
		if (params.isEmpty())
			return Db.find(fromBuilder.toString());

		return Db.find(fromBuilder.toString(), params.toArray());
	}
}
