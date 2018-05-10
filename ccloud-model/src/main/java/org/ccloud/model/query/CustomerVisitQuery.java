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
import org.ccloud.model.CustomerVisit;
import org.ccloud.utils.DateUtils;

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

	public Page<CustomerVisit> paginate(int pageNumber, int pageSize, String keyword, String dataArea,String customerType,String questionType,String groupBy, String orderby, String status,String bizUserId, String createDate) {
		
		String select = "select cc_v.*,cc.customer_name,GROUP_CONCAT(cc_t.`name`) customer_type ";
		boolean needWhere = true;
		StringBuilder fromBuilder = new StringBuilder("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer_join_customer_type cc_ct on cc_ct.seller_customer_id = cc_s.id left join cc_customer_type cc_t on cc_t.id = cc_ct.customer_type_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_v.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc.customer_name", keyword, params, needWhere);

		if(StrKit.notBlank(customerType)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_t.`id`", customerType, params, needWhere);
		}
		
		if(StrKit.notBlank(questionType)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.question_type", questionType, params, needWhere);
		}

		if(StrKit.notBlank(status)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.status", status, params, needWhere);
		}
		
		if(StrKit.notBlank(bizUserId)) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.user_id", bizUserId, params, needWhere);
		}
		
		if(StrKit.notBlank(createDate)) {
			if (needWhere) {
				fromBuilder.append(" WHERE cc_v.create_date >= ? ");
				params.add(createDate);
			} else {
				fromBuilder.append(" AND cc_v.create_date >= ? ");
				params.add(createDate);
			}
		}		

		fromBuilder.append(" GROUP BY cc_v.id ");
		fromBuilder.append(" ORDER BY " + orderby);
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	@Deprecated
	public Page<Record> paginateForApp(int pageNumber, int pageSize, String id, String type, String nature,String user, String subType, String status, String dataArea, String dealerDataArea, String searchKey) {

		boolean needwhere = false;
		List<Object> params = new LinkedList<Object>();

		String select  ="SELECT ccv.id, cc.customer_name, cc.contact, cc.mobile, ccv.create_date, ccv.`status`, ccv.question_type ";
		StringBuilder sql = new StringBuilder("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");

		sql.append("LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(sql, "c1.data_area", dealerDataArea, params, true);
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
		needwhere = appendIfNotEmpty(sql, "ccv.user_id", user, params, needwhere);
		sql.append("ORDER BY  ccv.create_date desc, ccv.`status` ");
		return Db.paginate(pageNumber, pageSize,select ,sql.toString(), params.toArray());
	}

	public Page<Record> _paginateForApp(int pageNumber, int pageSize, String id, String type, String nature,String user, String subType, String status, String dataArea, String dealerDataArea, String searchKey) {

		boolean needwhere = true;
		List<Object> params = new LinkedList<Object>();

		String select  ="SELECT ccv.id, cc.customer_name, cc.contact, cc.mobile, ccv.create_date, ccv.`status`, ccv.question_type ";
		StringBuilder sql = new StringBuilder("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON csc.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");


		needwhere = appendIfNotEmptyWithLike(sql, "ccv.data_area", dataArea, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "ct.id", type, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "csc.sub_type", subType, params, needwhere);
		needwhere = appendIfNotEmpty(sql,"csc.id", id, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "cc.customer_name", searchKey, params, needwhere);

		needwhere = appendIfNotEmpty(sql, "ccv.status", status, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "ccv.user_id", user, params, needwhere);
		sql.append("GROUP BY ccv.id ORDER BY  ccv.create_date desc, ccv.`status` ");
		return Db.paginate(pageNumber, pageSize,select ,sql.toString(), params.toArray());
	}

	public CustomerVisit findMoreById(String id) {
		StringBuilder sql = new StringBuilder("SELECT ccv.id,ccv.user_id,ccv.visit_user realname,ccv.seller_customer_id,ccv.question_type,ccv.question_desc,ccv.advice,ccv.photo,ccv.vedio,ccv.lng,ccv.lat,ccv.location,ccv.review_id,");
		sql.append("ccv.review_user,ccv.solution,ccv.comment,ccv.review_lng,ccv.review_lat,ccv.review_address,ccv.review_date,ccv.image_list_store,ccv.status,ccv.proc_def_key,ccv.proc_inst_id,ccv.dept_id,ccv.data_area,");
		sql.append("ccv.create_date,ccv.modify_date,ccv.active_apply_id,ccv.activity_execute_id,");
		sql.append("cc.prov_name,cc.city_name,cc.country_name,cc.address ,cc.customer_name, cc.contact, cc.mobile,d.name as typeName ");
		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN dict d ON ccv.question_type = d.value ");
//		sql.append("LEFT JOIN cc_customer_join_customer_type ccjct ON csc.id = ccjct.seller_customer_id ");
		sql.append("WHERE ccv.id = ?");

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
		fromBuilder.append(" where c.is_enabled = 1 and FIND_IN_SET(?, u.USER_ID_) ");
		
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
		sql.append(" where c.is_enabled = 1 and FIND_IN_SET(?, u.USER_ID_) ");
		
		return DAO.find(sql.toString(), username);
	}
	
	public Long findToDoCustomerVisitReviewCount(String username) {
		StringBuilder sb = new StringBuilder("SELECT count(*)");
		sb.append(" FROM cc_customer_visit cv ");
//		sb.append(" JOIN cc_seller_customer sc on cv.seller_customer_id = sc.id");
//		sb.append(" JOIN cc_customer c on sc.customer_id = c.id");
		sb.append(" JOIN act_ru_task a on cv.proc_inst_id = a.PROC_INST_ID_");
//		sb.append(" JOIN act_ru_identitylink u on cv.proc_inst_id = u.PROC_INST_ID_");
//		sb.append(" where c.is_enabled = 1 AND FIND_IN_SET(?, a.ASSIGNEE_) ");
		sb.append(" where FIND_IN_SET(?, a.ASSIGNEE_) ");
		return Db.queryLong(sb.toString(), username);
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
	
	public List<Record> exportVisit(String keyword, String dataArea,String customerType,String questionType,String groupBy, String orderby, String status,String bizUserId){

		StringBuilder fromBuilder = new StringBuilder("select cc_v.*,(select `name` from dict where type='customer_audit' and `value`=cc_v.`status`) visitStatus,cc.customer_name,GROUP_CONCAT(cc_t.`name`) customer_type, d.name questionName, art.ID_ taskId,cc.mobile customerMobile ");
		fromBuilder.append("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id ");
		fromBuilder.append("left join cc_customer_join_customer_type cc_ct on cc_ct.seller_customer_id = cc_s.id left join cc_customer_type cc_t on cc_t.id = cc_ct.customer_type_id ");
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
		
		if(StrKit.notBlank(bizUserId)) {
			fromBuilder.append("and cc_v.user_id = '"+bizUserId+"' ");
		}

		fromBuilder.append(" GROUP BY cc_v.id ");
		fromBuilder.append(" ORDER BY " + orderby);

		return Db.find(fromBuilder.toString());
	}

	@Deprecated
	public List<Record> findPhoto(String customerType, String customerName, String questionType, String data_area, String dealerDataArea){

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("SELECT ccv.id, ccv.visit_user realname, GROUP_CONCAT(ccv.photo SEPARATOR '_') as photo, cc.customer_name, ccv.create_date ");

		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON csc.id = ccv.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer cc ON cc.id = csc.customer_id ");

		sql.append("LEFT JOIN ( SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(sql, "c1.data_area", dealerDataArea, params, true);
		sql.append("GROUP BY c1.id) t1 ON t1.id = csc.id ");

		sql.append(" WHERE LENGTH(ccv.photo) > 2 ");

		appendIfNotEmpty(sql,"ccv.seller_customer_id", customerName, params, false);
		appendIfNotEmpty(sql, "ccv.question_type", questionType, params, false);
		appendIfNotEmptyWithLike(sql,"t1.customerTypeNames", customerType, params, false);
		appendIfNotEmptyWithLike(sql, "ccv.data_area", data_area, params, false);

		sql.append("GROUP BY ccv.visit_user, cc.id, DATE_FORMAT(ccv.create_date,'%m-%d-%Y') ");
		sql.append("ORDER BY ccv.create_date ");

		return Db.find(sql.toString(), params.toArray());
	}

	public List<Record> _findPhoto(String customerType, String customerName, String questionType, String data_area, String dealerDataArea){

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("SELECT ccv.id, ccv.visit_user realname, GROUP_CONCAT(ccv.photo SEPARATOR '_') as photo, cc.customer_name, ccv.create_date ");

		sql.append("FROM cc_customer_visit ccv ");
		sql.append("LEFT JOIN cc_seller_customer csc ON csc.id = ccv.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer cc ON cc.id = csc.customer_id ");

		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON csc.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");

		sql.append(" WHERE LENGTH(ccv.photo) > 2 ");

		appendIfNotEmpty(sql,"ccv.seller_customer_id", customerName, params, false);
		appendIfNotEmpty(sql, "ccv.question_type", questionType, params, false);
		appendIfNotEmptyWithLike(sql,"ct.id", customerType, params, false);
		appendIfNotEmptyWithLike(sql, "ccv.data_area", data_area, params, false);

		sql.append("GROUP BY ccv.visit_user, cc.id, DATE_FORMAT(ccv.create_date,'%m-%d-%Y') ");
		sql.append("ORDER BY ccv.create_date ");

		return Db.find(sql.toString(), params.toArray());
	}

	public List<Record> findLngLat(String userId, String startDate, String endDate, String status) {

		StringBuilder sql = new StringBuilder("SELECT ccv.id,ccv.user_id,ccv.visit_user realname,");
		sql.append("ccv.seller_customer_id,ccv.question_type,ccv.question_desc,ccv.advice,");
		sql.append("ccv.photo,ccv.vedio,ccv.lng,ccv.lat,ccv.location,ccv.review_id,ccv.review_user,");
		sql.append("ccv.solution,ccv.comment,ccv.review_lng,ccv.review_lat,ccv.review_address,ccv.review_date,");
		sql.append("ccv.image_list_store,ccv.status,ccv.proc_def_key,ccv.proc_inst_id,ccv.dept_id,ccv.data_area,");
		sql.append("ccv.create_date,ccv.modify_date,ccv.active_apply_id,ccv.activity_execute_id, ");
		sql.append("cc.customer_name ,GROUP_CONCAT(cct.`name`) as customerTypeName, d.name ");
		sql.append("FROM cc_customer_visit ccv ");

		sql.append("LEFT JOIN cc_seller_customer csc ON ccv.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
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
	
	public List<CustomerVisit> findByDataArea(String dataArea){
		StringBuilder sql = new StringBuilder("SELECT ccv.id,ccv.user_id,ccv.visit_user realname,");
		sql.append("ccv.seller_customer_id,ccv.question_type,ccv.question_desc,ccv.advice,");
		sql.append("ccv.photo,ccv.vedio,ccv.lng,ccv.lat,ccv.location,ccv.review_id,ccv.review_user,");
		sql.append("ccv.solution,ccv.comment,ccv.review_lng,ccv.review_lat,ccv.review_address,ccv.review_date,");
		sql.append("ccv.image_list_store,ccv.status,ccv.proc_def_key,ccv.proc_inst_id,ccv.dept_id,ccv.data_area,");
		sql.append("ccv.create_date,ccv.modify_date,ccv.active_apply_id,ccv.activity_execute_id ");
		sql.append("from cc_customer_visit ccv where ccv.data_area like ? GROUP BY ccv.user_id");
		return DAO.find(sql.toString(), dataArea);
	}
	
	//查询被拜访的客户数
	public int findBySellerId(String sellerId,String dayTag) {
		String startDate = "";
		String endDate = "";
		if (dayTag != null) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		String sql = "SELECT cv.* from cc_customer_visit cv where cv.seller_customer_id in (select id from cc_seller_customer where seller_id = '"+sellerId+"') "
				+ " and cv.create_date >= '"+startDate+"' and cv.create_date <= '"+endDate+"' and cv.status in (100101) GROUP BY seller_customer_id";
		return DAO.find(sql).size();
	}

	//查询被拜访的客户数
	public int _findBySellerId(String sellerId,String dayTag) {
		String startDate = "";
		String endDate = "";
		if (dayTag != null) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		String sql = "SELECT cv.* from cc_customer_visit cv where cv.seller_customer_id in (select id from cc_seller_customer where seller_id = '"+sellerId+"') "
				+ " and cv.create_date >= '"+startDate+"' and cv.create_date <= '"+endDate+"' and cv.status in (100101) GROUP BY cv.id";
		return DAO.find(sql).size();
	}
	//查询拜访客户次数相同
	public List<Record> getBySellerId(String sellerId ,String dayTag) {
		String startDate = "";
		String endDate = "";
		if (dayTag != null) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		String sql = "SELECT count(t.count) as CountNum,t.count from (SELECT COUNT(cv.seller_customer_id) as count from cc_customer_visit cv where cv. seller_customer_id in (select id from cc_seller_customer where seller_id = '"+sellerId+"') and cv.create_date >= '"+startDate+"' and cv.create_date <= '"+endDate+"' and cv.status in (100101) GROUP BY cv.seller_customer_id ORDER BY count) t GROUP BY t.count;";
		return Db.find(sql);
	}
	//查询拜访次数对应的订单金额总数
	public List<Record> getAmountBySellerId(String sellerId,String dayTag){
		String startDate = "";
		String endDate = "";
		if (dayTag != null) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		String sql = "SELECT count(t2.count) as CountNum,t2.count,CASE when TRUNCATE(SUM(t2.sum)/count(t2.count),2) IS NOT NULL THEN TRUNCATE(SUM(t2.sum)/count(t2.count),2) ELSE 0 END as amountNum from " 
					+"(SELECT cv.seller_customer_id as sellerCustomerId, COUNT(cv.seller_customer_id) as count ,t1.amount as sum " 
					+"from cc_customer_visit cv " 
					+"LEFT JOIN (SELECT so.customer_id as sellerCustomerId, SUM(so.total_amount) as amount " 
					+"from cc_sales_outstock so " 
					+"LEFT JOIN cc_sales_order_join_outstock jo on jo.outstock_id = so.id " 
					+"LEFT JOIN cc_sales_order cso on cso.id = jo.order_id " 
					+"where cso.seller_id = '"+sellerId+"' and so.`status` in ("+Consts.SALES_OUT_STOCK_STATUS_OUT+","+Consts.SALES_OUT_STOCK_STATUS_PART_OUT+") and so.create_date >= '"+startDate+"' and so.create_date <= '"+endDate+"' " 
					+"GROUP BY so.customer_id) t1 on t1.sellerCustomerId = cv.seller_customer_id "
					+ "where  cv.create_date >= '"+startDate+"' and cv.create_date <= '"+endDate+"' and cv.seller_customer_id in (select id from cc_seller_customer where seller_id = '"+sellerId+"') and cv.status in (100101) " 
					+"GROUP BY cv.seller_customer_id ORDER BY count) t2  GROUP BY t2.count";
		return Db.find(sql);
	}	
	
		
	public CustomerVisit findByActivityApplyIdAndComeFrom(String activityApplyId){
		String sql = "select * from cc_customer_visit where active_apply_id = '"+activityApplyId+"'";
		return DAO.findFirst(sql);
	}
	
	public List<CustomerVisit> findByActivityApplyId(String activityApplyId){
		String sql = "select * from cc_customer_visit where active_apply_id = '"+activityApplyId+"' and status not in (100103) ORDER BY create_date desc";
		return DAO.find(sql);
	}
	
	public CustomerVisit findByApplyIdAndExecteIdAndSellerCustomerId(String applyId,String activityExecuteId,String sellerCustomerId) {
		return DAO.doFindFirst("active_apply_id = ? and activity_execute_id = ? and seller_customer_id = ?", applyId, activityExecuteId, sellerCustomerId);
	}
	
	public CustomerVisit findByActivityApplyIdAndOrderList(String activityApplyId, String orderList) {
		String sql = "SELECT * from cc_customer_visit "
				+ "where activity_execute_id in ( SELECT e.id from cc_activity_execute e where e.activity_id in "
				+ "(SELECT activity_id from cc_activity_apply where id = '"+activityApplyId+"') "
				+ "and e.order_list = '"+orderList+"') "
				+ "and active_apply_id = '"+activityApplyId+"'";
		return DAO.findFirst(sql);
	}
	
	public List<CustomerVisit> findByApplyIdAndSellerCustomerId(String activityApplyId , String sellerCustomerId) {
		String sql = "select * from cc_customer_visit where seller_customer_id = '"+sellerCustomerId+"' and active_apply_id = '"+activityApplyId+"' and status not in (100103)";
		return DAO.find(sql);
	}
	
	public List<CustomerVisit> _findByActivityApplyId(String activityApplyId){
		String sql = "select * from cc_customer_visit where active_apply_id = '"+activityApplyId+"' and status = '"+Consts.CUSTOMER_VISIT_STATUS_PASS+"' ORDER BY create_date desc";
		return DAO.find(sql);
	}
	
}
