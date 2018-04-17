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
 * @author wally
 *
 */
public class BusinessManagerOrderReportQuery extends JBaseQuery {
	private static final BusinessManagerOrderReportQuery QUERY = new BusinessManagerOrderReportQuery();

	public static BusinessManagerOrderReportQuery me() {
		return QUERY;
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
		if (StrKit.notBlank(dayTag)) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder fromBuilder = new StringBuilder();
		fromBuilder.append("SELECT IFNULL(SUM(cc.total_count),0) as productCount, ");
		fromBuilder.append("IFNULL(sum(cc.total_amount),0) as totalAmount, ");
		fromBuilder.append("COUNT(*) as orderCount, cc.dept_id as id FROM cc_sales_order cc ");
		boolean needWhere = true;
		
		needWhere = appendIfNotEmptyWithLike(fromBuilder, " cc.data_area", dataArea, params, needWhere);
		fromBuilder.append(" AND EXISTS(SELECT os.`status` FROM cc_sales_order_status os ");
		fromBuilder.append("WHERE os.`status` = cc.`status` AND os.`status` != 1001 ");
		fromBuilder.append("AND os.`status` != 1002) ");
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
		
		fromBuilder.append("GROUP BY cc.dept_id ");
		
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
