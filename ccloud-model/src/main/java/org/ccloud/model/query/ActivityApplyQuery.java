/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.model.query;

import java.util.LinkedList;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import org.ccloud.Consts;
import org.ccloud.model.ActivityApply;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ActivityApplyQuery extends JBaseQuery { 

	protected static final ActivityApply DAO = new ActivityApply();
	private static final ActivityApplyQuery QUERY = new ActivityApplyQuery();

	public static ActivityApplyQuery me() {
		return QUERY;
	}

	public ActivityApply findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				StringBuilder sql = new StringBuilder("SELECT caa.*, cc.customer_name, DATE_FORMAT(ca.start_time, '%Y-%m-%d') as start_date, DATE_FORMAT(ca.end_time, '%Y-%m-%d') as end_date, " +
						"d.name, ca.invest_type, cc.contact, cc.mobile, u.realname, DATE_FORMAT(caa.create_date, '%Y-%m-%d') as format_create_date, ca.invest_amount, ca.title, ca.code ");

				sql.append("FROM cc_activity_apply caa ");
				sql.append("LEFT JOIN cc_activity ca ON caa.activity_id = ca.id ");
				sql.append("LEFT JOIN cc_seller_customer csc ON caa.seller_customer_id = csc.id ");
				sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");

				sql.append("LEFT JOIN dict d ON ca.category = d.`key` ");
				sql.append("LEFT JOIN `user` u ON caa.biz_user_id = u.id ");
				sql.append("WHERE caa.id = ? limit 1 ");
				return DAO.findFirst(sql.toString(), id);
			}
		});

	}

	public Page<ActivityApply> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity_apply` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public Page<Record> findList(int pageNumber, int pageSize, String dataArea, String category, String status, String startDate, String endDate, String keyword ){
		String select = "SELECT caa.id,caa.activity_id, cc.customer_name, caa.`status`,caa.seller_customer_id,caa.apply_num,caa.apply_amount, DATE_FORMAT(ca.start_time,'%m-%d') as start_time, " +
				"DATE_FORMAT(ca.end_time, '%m-%d') as end_time, ca.invest_type,ca.title, d.`name`, ca.invest_amount,ca.code,t1.name as ExpenseDetailName ";
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sql = new StringBuilder("FROM cc_activity_apply caa ");
		sql.append("LEFT JOIN cc_seller_customer csc ON csc.id = caa.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_activity ca ON ca.id = caa.activity_id ");
		sql.append("LEFT JOIN dict d ON ca.category = d.`key` ");
		sql.append("LEFT JOIN (SELECT ed.id,d.`name` from cc_expense_detail ed LEFT JOIN dict d on d.type = ed.flow_dict_type and d.`value` = ed.item1) t1 on t1.id = caa.expense_detail_id ");

		boolean needwhere = true;
		needwhere = appendIfNotEmptyWithLike(sql, "caa.data_area", dataArea, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "ca.category", category, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "caa.status", status, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "cc.customer_name", keyword, params, needwhere);

		if (StrKit.notBlank(startDate)) {
			sql.append(" and caa.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			sql.append(" and caa.create_date <= ?");
			params.add(endDate);
		}
		sql.append(" order by caa.create_date desc ");
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());
	}

	public List<Record> getToDo(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT o.*,d.name as expenseDetailName,CASE o.status WHEN "+Consts.ACTIVITY_APPLY_STATUS_WAIT+" THEN '待审' WHEN "+Consts.ACTIVITY_APPLY_STATUS_PASS+" THEN '已审' WHEN "+Consts.ACTIVITY_APPLY_STATUS_CANCEL+" THEN '撤回' WHEN "+Consts.ACTIVITY_APPLY_STATUS_REJECT+" THEN '拒绝' ELSE '结束' END AS activityApplyStatus, ca.title, ca.invest_amount, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, t1.customerTypeNames, a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime ");
		sb.append(" FROM cc_activity_apply o ");
		sb.append(" left join cc_activity ca on o.activity_id = ca.id");
		sb.append(" left join cc_seller_customer cc ON o.seller_customer_id = cc.id ");
		sb.append(" left join cc_customer c on cc.customer_id = c.id ");
		sb.append(" left join cc_expense_detail ce on ce.id = o.expense_detail_id ");
		sb.append(" left join dict d on d.`value` = ce.item1 ");
		
		sb.append(" LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sb.append(" FROM cc_seller_customer c1 ");
		sb.append(" LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sb.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		sb.append(" GROUP BY c1.id) t1 ON o.seller_customer_id = t1.id ");

		sb.append(" JOIN act_ru_task a on o.proc_inst_id = a.PROC_INST_ID_ ");
		sb.append(" where FIND_IN_SET(?, a.ASSIGNEE_) ");

		sb.append(" GROUP BY o.id ");
		sb.append(" order by o.create_date DESC");
		return Db.find(sb.toString(), username);
	}

	public Page<Record> getHisProcessList(int pageNumber, int pageSize, String procKey, String username) {

		String select = "SELECT o.*,d.name as expenseDetailName,CASE o.status WHEN "+Consts.ACTIVITY_APPLY_STATUS_WAIT+" THEN '待审' WHEN "+Consts.ACTIVITY_APPLY_STATUS_PASS+" THEN '已审' WHEN "+Consts.ACTIVITY_APPLY_STATUS_CANCEL+" THEN '撤回' WHEN "+Consts.ACTIVITY_APPLY_STATUS_REJECT+" THEN '拒绝' ELSE '结束' END AS activityApplyStatus, ca.title, ca.invest_amount, c.customer_name, c.contact as ccontact, c.mobile as cmobile, c.address as caddress, t1.customerTypeNames, i.TASK_ID_ taskId, i.ACT_NAME_ taskName, i.ASSIGNEE_ assignee, i.END_TIME_ endTime  ";

		LinkedList<Object> params = new LinkedList<>();
		StringBuilder sql = new StringBuilder(" FROM cc_activity_apply o ");
		sql.append(" left join cc_activity ca on o.activity_id = ca.id");
		sql.append(" left join cc_seller_customer cc ON o.seller_customer_id = cc.id ");
		sql.append(" left join cc_customer c on cc.customer_id = c.id ");
		sql.append(" left join cc_expense_detail ce on ce.id = o.expense_detail_id ");
		sql.append(" left join dict d on d.`value` = ce.item1 ");

		sql.append(" LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append(" FROM cc_seller_customer c1 ");
		sql.append(" LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		sql.append(" GROUP BY c1.id) t1 ON o.seller_customer_id = t1.id ");

		sql.append(" JOIN act_hi_actinst i on o.proc_inst_id = i.PROC_INST_ID_ ");
		sql.append(" JOIN act_re_procdef p on p.ID_ = i.PROC_DEF_ID_ ");
		sql.append(" WHERE i.DURATION_ is not null AND p.KEY_ = ? ");
		params.add(procKey);
		if (StrKit.notBlank(username)) {
			sql.append(" AND FIND_IN_SET(?, i.ASSIGNEE_)");
			params.add(username);
		}
		sql.append(" GROUP BY o.id ");
		sql.append(" order by i.END_TIME_ desc ");

		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());
	}
	
	public long findByActivityId(String activityId){
		String sql = "activity_id = ?";
		return DAO.doFindCount(sql, activityId);
	}
	
	public Long findBySellerCustomerIdAndActivityId(String activityId,String sellerCustomerId,String date){
		String sql = "activity_id = ? and seller_customer_id = ? and create_date > ? and status not in ("+Consts.ACTIVITY_APPLY_STATUS_REJECT+","+Consts.ACTIVITY_APPLY_STATUS_CANCEL+")";
		return DAO.doFindCount(sql, activityId, sellerCustomerId, date);
	}
	
	public List<ActivityApply> findByUserIdAndActivityId(String activityId,String userId){
		String sql = "Select * from cc_activity_apply where activity_id = ? and biz_user_id = ? and status not in ("+Consts.ACTIVITY_APPLY_STATUS_REJECT+","+Consts.ACTIVITY_APPLY_STATUS_CANCEL+") GROUP BY seller_customer_id";
		return DAO.find(sql, activityId,userId);
	}
	
	public List<ActivityApply> findSellerCustomerIdAndActivityIdAndUserId(String sellerCustomerId,String activityId,String userId,String expenseDetailId) {
		return DAO.doFind(" seller_customer_id = ? and activity_id = ? and biz_user_id = ? and expense_detail_id = ? and status not in ("+Consts.ACTIVITY_APPLY_STATUS_REJECT+","+Consts.ACTIVITY_APPLY_STATUS_CANCEL+")", sellerCustomerId,activityId,userId,expenseDetailId);
	}

	public List<Record> findBySellerCustomerId(String sellerCustomerId) {
		return Db.find(" select ap.id, a.title from cc_activity_apply ap join cc_activity a on ap.activity_id = a.id where ap.seller_customer_id = ? and ap.status in ("+Consts.ACTIVITY_APPLY_STATUS_PASS+","+Consts.ACTIVITY_APPLY_STATUS_END+") order by ap.status, ap.create_date desc", sellerCustomerId);
	}
}
