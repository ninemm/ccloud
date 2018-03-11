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
import org.ccloud.model.CustomerVisit;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerVisitQuery extends JBaseQuery { 

	protected static final CustomerVisit DAO = new CustomerVisit();
	private static final CustomerVisitQuery QUERY = new CustomerVisitQuery();

	public static CustomerVisitQuery me() {
		return QUERY;
	}

	public CustomerVisit findById(final String id) {
		
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				
				StringBuilder sql = new StringBuilder("SELECT cv.*, c.customer_code, c.customer_name, c.contact, c.mobile");
				sql.append(" FROM cc_customer_visit cv");
				sql.append(" JOIN cc_seller_customer sc ON sc.id = cv.seller_customer_id");
				sql.append(" JOIN cc_customer c ON sc.customer_id = c.id");
				sql.append(" WHERE cv.id = ? and c.is_enabled = 1 limit 1");
				return DAO.findFirst(sql.toString(), id);
			}
		});
	}

	public Page<CustomerVisit> paginate(int pageNumber, int pageSize, String keyword, String dataArea,String customerType,String questionType,String groupBy, String orderby, String status) {
		
		String select = "select cc_v.*,cc.customer_name,(select realname from `user` where id = cc_v.user_id) visit_user,u.realname review_user,GROUP_CONCAT(cc_t.`name`) customer_type, d.name questionName, art.ID_ taskId ";
		boolean needWhere = true;
		StringBuilder fromBuilder = new StringBuilder("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id ");
		fromBuilder.append("left join `user` u on u.id = cc_v.review_id left join cc_customer_join_customer_type cc_ct on cc_ct.seller_customer_id = cc_s.id left join cc_customer_type cc_t on cc_t.id = cc_ct.customer_type_id ");
		fromBuilder.append("left join dict d on d.value = cc_v.question_type ");
		fromBuilder.append("left JOIN act_ru_task art on cc_v.proc_inst_id = art.PROC_INST_ID_ ");
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_v.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc.customer_name", keyword, params, needWhere);

		if(StrKit.notBlank(customerType)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_t.`name`", customerType, params, needWhere);
		}

		if(StrKit.notBlank(questionType)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.question_type", questionType, params, needWhere);
		}

		if(StrKit.notBlank(status)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.status", status, params, needWhere);
		}

		fromBuilder.append(" GROUP BY cc_v.id ");
		fromBuilder.append(" ORDER BY " + orderby);
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String id, String type, String nature, String subType, String status, String dataArea, String searchKey) {

		boolean needwhere = false;
		List<Object> params = new LinkedList<Object>();

		String select  ="SELECT c.id, c.customer_name, c.contact, c.mobile, c.create_date, c.`status`, c.question_type ";
		StringBuilder sql = new StringBuilder("FROM ( SELECT DISTINCT(ccv.id), cc.customer_name, cc.contact, cc.mobile, ccv.create_date, ccv.`status`, ccv.question_type ");
		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_customer_join_customer_type ccjct ON csc.id = ccjct.seller_customer_id ");

		sql.append("LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		sql.append("GROUP BY c1.id) t1 ON csc.id = t1.id ");

		if (StrKit.notBlank(searchKey)) {
			sql.append("WHERE ( cc.customer_name LIKE ? OR cc.contact LIKE ? ) ");
			if (searchKey.contains("%")) {
				params.add(searchKey);
				params.add(searchKey);
			} else {
				params.add("%" + searchKey + "%");
				params.add("%" + searchKey + "%");
			}
		} else {
			sql.append("WHERE cc.customer_name is not null ");
			needwhere = false;
		}
		
		needwhere = appendIfNotEmptyWithLike(sql, "ccv.data_area", dataArea, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "t1.customerTypeNames", type, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "csc.sub_type", subType, params, needwhere);
		needwhere = appendIfNotEmpty(sql,"csc.id", id, params, needwhere);

		needwhere = appendIfNotEmpty(sql, "ccv.status", status, params, needwhere);

		sql.append("ORDER BY  ccv.create_date desc, ccv.`status` ");
		sql.append(") AS c");
		return Db.paginate(pageNumber, pageSize,select ,sql.toString(), params.toArray());
	}

	public CustomerVisit findMoreById(String id) {
		StringBuilder sql = new StringBuilder("SELECT ccv.*,cc.prov_name,cc.city_name,cc.country_name ,cc.customer_name, cc.contact, cc.mobile, u.realname, u.mobile as userMobile, d.name as typeName, t1.title, t1.name as expenseDetailName,t1.activitApplyId ");
		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN user u ON ccv.user_id = u.id ");
		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN dict d ON ccv.question_type = d.value ");
		sql.append("LEFT JOIN (SELECT a.id as activitApplyId,ca.title,d.name from cc_activity_apply a LEFT JOIN cc_activity ca on ca.id = a.activity_id LEFT JOIN cc_expense_detail ce on ce.id = a.expense_detail_id LEFT JOIN dict d on d.type = ce.flow_dict_type and d.`value` = ce.item1) t1"
				+ " on t1.activitApplyId = ccv.active_apply_id ");
//		sql.append("LEFT JOIN cc_customer_join_customer_type ccjct ON csc.id = ccjct.seller_customer_id ");
		sql.append("WHERE ccv.id = ? limit 1");

		return DAO.findFirst(sql.toString(), id);
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

	public Page<CustomerVisit> getToDo(int pageNumber, int pageSize, String username) {
		
		String select = "select cv.*, sc.nickname, c.customer_name, c.customer_code, c.contact, c.mobile, c.prov_name, c.city_name, c.country_name, c.address, a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime";
		
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_customer_visit cv");
		fromBuilder.append(" JOIN cc_seller_customer sc on cv.seller_customer_id = sc.id");
		fromBuilder.append(" JOIN cc_customer c on sc.customer_id = c.id");
		fromBuilder.append(" JOIN act_ru_task a on cv.proc_inst_id = a.PROC_INST_ID_");
		fromBuilder.append(" JOIN act_ru_identitylink u on cv.proc_inst_id = u.PROC_INST_ID_");
		fromBuilder.append(" where c.is_enabled = 1 and locate(?, u.USER_ID_) > 0");
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), username);
	}
	
	public List<CustomerVisit> getToDo(String username) {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select cv.*, sc.nickname, c.customer_name, c.customer_code, c.contact, c.mobile, c.prov_name, c.city_name, "
				+ "c.country_name, c.address, a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime");
		sql.append(" FROM cc_customer_visit cv");
		sql.append(" JOIN cc_seller_customer sc on cv.seller_customer_id = sc.id");
		sql.append(" JOIN cc_customer c on sc.customer_id = c.id");
		sql.append(" JOIN act_ru_task a on cv.proc_inst_id = a.PROC_INST_ID_");
		sql.append(" JOIN act_ru_identitylink u on cv.proc_inst_id = u.PROC_INST_ID_");
		sql.append(" where c.is_enabled = 1 and locate(?, u.USER_ID_) > 0");
		
		return DAO.find(sql.toString(), username);
	}
	
	public Page<Record> getHisProcessList(int pageNumber, int pageSize, String procKey, String username) {
		
		String select = "select cv.*, sc.nickname, c.customer_name, c.customer_code, c.contact, c.mobile, c.prov_name, c.city_name, c.country_name, c.address,i.TASK_ID_ taskId, i.ACT_NAME_ taskName, i.ASSIGNEE_ assignee, i.END_TIME_ endTime ";
		LinkedList<Object> params = new LinkedList<>();
		params.add(procKey);
		params.add(username);

		StringBuilder sql = new StringBuilder(" FROM cc_customer_visit cv ");
		sql.append(" JOIN cc_seller_customer sc on cv.seller_customer_id = sc.id");
		sql.append(" JOIN cc_customer c on sc.customer_id = c.id");
		sql.append(" JOIN act_hi_actinst i on cv.proc_inst_id = i.PROC_INST_ID_ ");
		sql.append(" JOIN act_re_procdef p on p.ID_ = i.PROC_DEF_ID_ ");
		sql.append(" WHERE p.KEY_ = ? and locate(?, i.ASSIGNEE_) > 0 AND i.DURATION_ is not null ");
		sql.append(" order by i.END_TIME_ desc ");

		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());
	}
	
	public Page<Record> queryVisitRecord(int pageNumber, int pageSize,String customerLevel,String customerType,String customerNature,String userId){
		String select = "select ccv.id,ccv.create_date,cc.customer_name,cc.contact,cc.mobile,d.`name` questionType,if(ccv.`status`>0,'已审核','未审核') visitStatus ";
		StringBuilder fromBuilder = new StringBuilder("from cc_customer_visit ccv left join cc_seller_customer csc on ccv.seller_customer_id = csc.id left join cc_customer cc on csc.customer_id = cc.id left join dict d on ccv.question_type = d.id ");
		fromBuilder.append("left join cc_customer_join_customer_type cjct on cjct.seller_customer_id = ccv.seller_customer_id inner join cc_customer_type cct on cjct.customer_type_id = cct.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "ccv.user_id", userId, params, true);
		appendIfNotEmpty(fromBuilder, "csc.sub_type", customerLevel, params, false);
		appendIfNotEmpty(fromBuilder, "cct.id", customerType, params, false);
		fromBuilder.append("ORDER BY ccv.create_date desc ");
		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Record queryVisitDetail(String userId,String visitId) {
		StringBuilder fromBuilder = new StringBuilder("select ccv.id,ccv.create_date,cc.customer_name,cc.contact,cc.mobile,ccv.photo picurl,d.`name` questionType,ccv.question_desc questionDesc,ccv.location ");
		fromBuilder.append("from cc_customer_visit ccv left join cc_seller_customer csc on ccv.seller_customer_id = csc.id left join cc_customer cc on csc.customer_id = cc.id left join dict d on ccv.question_type = d.id ");
		fromBuilder.append("left join cc_customer_join_customer_type cjct on cjct.seller_customer_id = ccv.seller_customer_id inner join cc_customer_type cct on cjct.customer_type_id = cct.id ");
		fromBuilder.append("where ccv.user_id ='"+userId+"' ");
		fromBuilder.append("and ccv.id =? ");
		return Db.findFirst(fromBuilder.toString(), visitId);
	}
	
	public List<Record> exportVisit(String keyword, String dataArea,String customerType,String questionType,String groupBy, String orderby, String status){

		StringBuilder fromBuilder = new StringBuilder("select cc_v.*,(select `name` from dict where type='customer_audit' and `value`=cc_v.`status`) visitStatus,cc.customer_name,(select realname from `user` where id = cc_v.user_id) visit_user,u.realname review_user,GROUP_CONCAT(cc_t.`name`) customer_type, d.name questionName, art.ID_ taskId,cc.mobile customerMobile ");
		fromBuilder.append("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id ");
		fromBuilder.append("left join `user` u on u.id = cc_v.review_id left join cc_customer_join_customer_type cc_ct on cc_ct.seller_customer_id = cc_s.id left join cc_customer_type cc_t on cc_t.id = cc_ct.customer_type_id ");
		fromBuilder.append("left join dict d on d.value = cc_v.question_type ");
		fromBuilder.append("left JOIN act_ru_task art on cc_v.proc_inst_id = art.PROC_INST_ID_ ");
		fromBuilder.append("where cc_v.data_area like '"+dataArea+"' ");
		
		if(StrKit.notBlank(keyword)) {
			fromBuilder.append("and cc.customer_name like '%"+keyword+"%' ");
		}

		if(StrKit.notBlank(customerType)) {
			fromBuilder.append("and cc_t.`name` = '"+customerType+"' ");
		}

		if(StrKit.notBlank(questionType)) {
			fromBuilder.append("and cc_v.question_type = '"+questionType+"' ");
		}

		if(StrKit.notBlank(status)) {
			fromBuilder.append("and cc_v.status = '"+status+"' ");
		}

		fromBuilder.append(" GROUP BY cc_v.id ");
		fromBuilder.append(" ORDER BY " + orderby);

		return Db.find(fromBuilder.toString());
	}

	public List<Record> findPhoto(String customerType, String customerName, String data_area){

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("SELECT ccv.id, u.realname, GROUP_CONCAT(ccv.photo SEPARATOR '_') as photo, cc.customer_name, ccv.create_date ");

		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON csc.id = ccv.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer cc ON cc.id = csc.customer_id ");

		sql.append("LEFT JOIN ( SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		sql.append("GROUP BY c1.id) t1 ON t1.id = csc.id ");

		sql.append("LEFT JOIN `user` u ON u.id = ccv.user_id ");
		sql.append(" WHERE LENGTH(ccv.photo) > 2 ");

		appendIfNotEmpty(sql,"ccv.seller_customer_id", customerName, params, false);
		appendIfNotEmptyWithLike(sql,"t1.customerTypeNames", customerType, params, false);
		appendIfNotEmptyWithLike(sql, "ccv.data_area", data_area, params, false);

		sql.append("GROUP BY u.realname, cc.id, DATE_FORMAT(ccv.create_date,'%m-%d-%Y') ");
		sql.append("ORDER BY ccv.create_date ");

		return Db.find(sql.toString(), params.toArray());
	}

	public List<Record> findLngLat(String userId, String startDate, String endDate, String status) {

		StringBuilder sql = new StringBuilder("SELECT ccv.*, cc.customer_name ,u.realname, " +
				"GROUP_CONCAT(cct.`name`) as customerTypeName, d.name ");
		sql.append("FROM cc_customer_visit ccv ");

		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN `user` u ON ccv.user_id = u.id ");
		sql.append("LEFT JOIN cc_customer_join_customer_type ccjct ON ccjct.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer_type cct ON cct.id = ccjct.customer_type_id ");
		sql.append("LEFT JOIN dict d ON d.`value` = ccv.question_type ");

		sql.append("WHERE ccv.user_id = ? AND ccv.create_date >= ? AND ccv.create_date <= ? AND ccv.status = ? AND ccv.lat is not null ");
		sql.append("GROUP BY ccv.id ");
		sql.append("ORDER BY ccv.create_date");
		return Db.find(sql.toString(), userId, startDate, endDate, status);

	}

	public List<Record> findByActivity(String id) {
		String sql="SELECT a.title FROM cc_customer_visit_join_activity cvja LEFT JOIN cc_activity a ON a.id = cvja.activity_id WHERE cvja.customer_visit_id =?";
		return Db.find(sql.toString(), id);
	}
	
	public CustomerVisit findByActivityApplyIdAndComeFrom(String activityApplyId){
		String sql = "select * from cc_customer_visit where active_apply_id = '"+activityApplyId+"' and come_from = 1";
		return DAO.findFirst(sql);
	}
}
