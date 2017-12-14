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

import org.ccloud.model.CustomerVisit;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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

	public Page<CustomerVisit> paginate(int pageNumber, int pageSize, String keyword, String dataArea,String customerType,String questionType,String groupBy, String orderby) {
		
		String select = "select cc_v.*,cc.customer_name,(select realname from `user` where id = cc_v.user_id) visit_user,u.realname review_user,GROUP_CONCAT(cc_t.`name`) customer_type ";
		boolean needWhere = true;
		StringBuilder fromBuilder = new StringBuilder("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id ");
		fromBuilder.append("left join `user` u on u.id = cc_v.review_id left join cc_customer_join_customer_type cc_ct on cc_ct.seller_customer_id = cc_s.id left join cc_customer_type cc_t on cc_t.id = cc_ct.customer_type_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_v.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc.customer_name", keyword, params, needWhere);
		if(!customerType.equals("0")) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_t.`id`", customerType, params, needWhere);
		}
		if(!questionType.equals("0")) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.question_type", questionType, params, needWhere);
		}
		fromBuilder.append(" GROUP BY cc_v.id ");
		fromBuilder.append(" ORDER BY " + orderby);
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

	public Page<CustomerVisit> getToDo(int pageNumber, int pageSize, String username) {
		
		String select = "select cv.*, sc.nickname, c.customer_name, c.customer_code, c.contact, c.mobile, a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime";
		
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_customer_visit cv");
		fromBuilder.append(" JOIN cc_seller_customer sc on cv.seller_customer_id = sc.id");
		fromBuilder.append(" JOIN cc_customer c on sc.customer_id = c.id");
		fromBuilder.append(" JOIN act_ru_task a on sc.proc_inst_id = a.PROC_INST_ID_");
		fromBuilder.append(" JOIN act_ru_identitylink u on sc.proc_inst_id = u.PROC_INST_ID_");
		fromBuilder.append(" where c.is_enabled = 1 and locate(?, u.USER_ID_) > 0");
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), username);
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
	
}
