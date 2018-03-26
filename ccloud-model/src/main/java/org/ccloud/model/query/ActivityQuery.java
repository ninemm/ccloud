/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.model.query;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import org.ccloud.Consts;
import org.ccloud.model.Activity;

import java.util.LinkedList;
import java.util.List;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ActivityQuery extends JBaseQuery {

    protected static final Activity DAO = new Activity();
    private static final ActivityQuery QUERY = new ActivityQuery();

    public static ActivityQuery me() {
        return QUERY;
    }

	public Activity findById(final String id) {
				return DAO.findById(id);
	}

	public Record findMoreById(final String id) {
		StringBuilder fromBuilder = new StringBuilder(" select o.*, ct.name as customerTypeName ");
		fromBuilder.append(" from `cc_activity` o ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct on o.customer_type = ct.id");
		fromBuilder.append(" where o.id = ? ");

		return Db.findFirst(fromBuilder.toString(), id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword,String startDate, String endDate,String sellerId) {
		String select = "select ca.*,case when ca.category='"+Consts.CATEGORY_NORMAL+"' then '商品销售' else '投入活动' end as activityCategory ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity` ca ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "title", keyword, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ca.start_time >= ?");
			params.add(startDate+" 00:00:00");
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ca.end_time <= ?");
			params.add(endDate + "23:59:59");
		}
		fromBuilder.append(" and ca.seller_id='"+sellerId+"' ORDER BY ca.is_publish desc,ca.create_date desc");
		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
        return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
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

    public List<Record> findActivityListForApp(String sellerId, String keyword, String tag) {
        StringBuilder fromBuilder = new StringBuilder(" SELECT DISTINCT a.*, d.name as categoryName, ct.name as customerTypeName, dd.name as timeIntervalName,a.total_customer_num as surplusNum FROM cc_activity a");
        fromBuilder.append(" left join dict d on a.category = d.value ");
	    fromBuilder.append(" left join cc_customer_type ct on a.customer_type = ct.id ");
	    fromBuilder.append(" left join dict dd on a.time_interval = dd.value ");
        fromBuilder.append(" WHERE a.is_publish = 1 ");

        LinkedList<Object> params = new LinkedList<Object>();
        appendIfNotEmpty(fromBuilder, "a.seller_id", sellerId, params, false);
        appendIfNotEmptyWithLike(fromBuilder, "a.title", keyword, params, false);

        if (StrKit.notBlank(tag)) {
            fromBuilder.append(" AND FIND_IN_SET(?, a.tags)");
            params.add(tag);
        }

        fromBuilder.append(" ORDER BY a.category, a.start_time desc, a.end_time asc ");

        return Db.find(fromBuilder.toString(), params.toArray());
    }
    
    public List<Activity> findAll(){
    	String sql = "select * from cc_activity where is_publish = 1";
    	return DAO.find(sql);
    }
    
	public List<Activity> findBySellerId(String sellerId) {
		return DAO.doFind("seller_id = ? and is_publish = 1 ", sellerId);
	}
    
	public String getCustomerTypes(String customerTypeIds){
		if (StrKit.isBlank(customerTypeIds)) {
			return null;
		}
		String[] customerTypes = customerTypeIds.split(",");
		String types = "";
		String typeNames = "";
		if(customerTypes!=null){
			for(int i = 0;i<customerTypes.length;i++){
				types += CustomerTypeQuery.me().findById(customerTypes[i]).getStr("name")+",";
			}
			typeNames = types.substring(0, types.length()-1);
		}
		return typeNames;
	}

	public List<Record> findByCustomerId(String customerId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT caa.id as activityApplyId, a.title,a.id,cea.* FROM cc_activity a ");
        fromBuilder.append(" LEFT JOIN cc_activity_apply caa on caa.activity_id = a.id ");
        fromBuilder.append(" LEFT JOIN cc_expense_detail cea on caa.expense_detail_id = cea.id ");
	    fromBuilder.append(" where caa.seller_customer_id ='"+customerId+"' and a.is_publish = 1");
	    fromBuilder.append(" GROUP BY a.id");
        return Db.find(fromBuilder.toString());
	}

	public boolean isExist(String flowNo) {
		boolean exist = false;
		Activity activity = DAO.doFindFirst("proc_code = ?", flowNo);
		if (activity != null) {
			exist = true;
		}
		return exist;
	}

	public Page<Record> activityPutPaginate(int pageNumber, int pageSize, String keyword,String startDate, String endDate,String sellerId, String invest_type) {
		String select = "select DATE(ca.start_time) start_time1,DATE(ca.end_time) end_time1,IFNULL(t1.putNum , 0) putNum, IFNULL(t2.executeNum , 0) executeNum, IFNULL(ca.invest_num , 0) invest_num,IFNULL(ca.invest_amount , 0) invest_amount,ca.* ,";
		select=select+"( SELECT d.`name` FROM dict d WHERE d.`key` = ca.invest_type AND d.type = '"+Consts.INVEST_TYPE+"') investType ,( SELECT d.`name` FROM dict d WHERE d.`key` = ca.time_interval AND d.type = '"+Consts.ACTIVE_TIME_INTERVAL+"') timeInterval ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity` ca ");
		fromBuilder.append(" LEFT JOIN (SELECT caa.activity_id,COUNT(1) putNum FROM cc_activity_apply caa GROUP BY caa.activity_id)t1 ON ca.id=t1.activity_id");
		fromBuilder.append(" LEFT JOIN (SELECT caa.activity_id,COUNT(1) executeNum FROM cc_customer_visit ccv LEFT JOIN cc_activity_apply caa ON ccv.active_apply_id=caa.id GROUP BY caa.activity_id)t2 ON ca.id=t2.activity_id");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" where (ca.title like '%"+keyword+"%' or ca.proc_code like '%"+keyword+"%')");
			needWhere=false;
		}
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}
		
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ca.start_time >= ?");
			params.add(startDate+" 00:00:00");
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ca.end_time <= ?");
			params.add(endDate + "23:59:59");
		}
		if (StrKit.notBlank(invest_type)) {
			fromBuilder.append(" and ca.invest_type = "+invest_type);
		}
		fromBuilder.append(" and ca.seller_id='"+sellerId+"' ORDER BY ca.is_publish desc,ca.create_date desc");
		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
        return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
    }

	public Page<Record> putDetailsPaginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate,String id, String status) {
		String select = "SELECT caa.`status`,caa.id activityApplyId,u.id userId,u.realname,csc.id customerId,cc.customer_name,CONCAT(cc.prov_name,cc.city_name,cc.country_name,cc.address) address,caa.create_date putDate,IFNULL(t1.executeNum , 0) executeNum,";	
		select=select+"(SELECT d.`name` FROM dict d WHERE d.`key`= ca.invest_type AND d.type='"+Consts.INVEST_TYPE+"') investType,( SELECT d.`name` FROM dict d WHERE d.`key` = ca.time_interval AND d.type = '"+Consts.ACTIVE_TIME_INTERVAL;
		select=select+"') timeInterval,( SELECT group_concat(cct.`name`) FROM cc_customer_type cct WHERE LOCATE(cct.id,csc.customer_type_ids) > 0 GROUP BY csc.customer_type_ids) customer_type_ids,ca.*";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_activity_apply caa ");
		fromBuilder.append(" LEFT JOIN cc_activity ca ON ca.id=caa.activity_id");
		fromBuilder.append(" LEFT JOIN `user` u ON u.id=caa.biz_user_id");
		fromBuilder.append(" LEFT JOIN cc_seller_customer csc ON csc.id=caa.seller_customer_id");
		fromBuilder.append(" LEFT JOIN cc_customer cc ON cc.id = csc.customer_id");
		fromBuilder.append(" LEFT JOIN(  SELECT ccv.active_apply_id,COUNT(1) executeNum FROM cc_activity_apply aa LEFT JOIN cc_customer_visit ccv ON aa.id = ccv.active_apply_id WHERE aa.activity_id = '");
		fromBuilder.append(id+"' GROUP BY ccv.active_apply_id) t1 ON t1.active_apply_id=caa.id");
		fromBuilder.append(" WHERE ca.id='"+id+"' ");
		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (cc.customer_name like '%"+keyword+"%' or u.realname like '%"+keyword+"%')");
		}
		LinkedList<Object> params = new LinkedList<Object>();
		if (StrKit.notBlank(status)) {
			fromBuilder.append(" and caa.status=?");
			params.add(status);
		}
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ca.start_time >= ?");
			params.add(startDate+" 00:00:00");
		}
		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ca.end_time <= ?");
			params.add(endDate + "23:59:59");
		}
		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
        return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> visitDetailsPaginate(int pageNumber, int pageSize, String keyword, String startDate,
			String endDate, String activityApplyId) {
		String select = "SELECT ccv.id customerVisitId,u.realname,ca.proc_code,(SELECT d.`name` FROM dict d WHERE d.`key` = ca.invest_type AND d.type = '"+Consts.INVEST_TYPE+"') investType,cc.customer_name,CONCAT(cc.prov_name,cc.city_name,cc.country_name,cc.address) address";
		select = select+",caa.create_date putDate ,ccv.photo,( SELECT group_concat(cct.`name`) FROM cc_customer_type cct WHERE LOCATE(cct.id , csc.customer_type_ids) > 0 GROUP BY csc.customer_type_ids) customer_type";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_customer_visit ccv ");
		fromBuilder.append(" LEFT JOIN cc_activity_apply caa ON caa.id=ccv.active_apply_id ");
		fromBuilder.append(" LEFT JOIN cc_activity ca ON ca.id=caa.activity_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_customer csc ON csc.id=ccv.seller_customer_id");
		fromBuilder.append(" LEFT JOIN cc_customer cc ON cc.id=csc.customer_id");
		fromBuilder.append(" LEFT JOIN `user` u ON u.id=ccv.user_id");
		fromBuilder.append(" WHERE ccv.active_apply_id='"+activityApplyId+"'");
		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (cc.customer_name like '%"+keyword+"%' or u.realname like '%"+keyword+"%')");
		}
		LinkedList<Object> params = new LinkedList<Object>();
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ccv.create_date >= ?");
			params.add(startDate+" 00:00:00");
		}
		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ccv.create_date <= ?");
			params.add(endDate + "23:59:59");
		}
		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
        return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Record findYxActivity(String activityApplyId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT ced.item1 ChannelID , ced.item2 ShowType , ca.invest_type , ca.proc_code 'FlowIDNO' ,( SELECT qb.YX_FeeTypeID FROM dict d LEFT JOIN qy_basicfeetype qb ");
		fromBuilder.append("ON d.`name` = qb.FeeTypeName WHERE d.`key` = ced.item1 AND qb.IsEnable = 1) CostType , caa.create_date ActivityTime , cc.customer_name CustomerName , ");
		fromBuilder.append("CONCAT( cc.prov_name , cc.city_name , cc.country_name , cc.address) ActivityAddress , cc.mobile Telephone , ccv.review_address Position , caa.apply_amount WriteOffAmount ,");
		fromBuilder.append("u.realname CreateManName , caa.create_date CreateTime , u.realname ModifyManName , caa.create_date ModifyTime , cc.prov_name ProvinceName , cc.city_name CityName ,");
		fromBuilder.append("cc.country_name CountyName , cc.create_date ShopCreateTime , cc.contact ShopLinkMan , cc.mobile ShopPhone , caa.num Num ,");
		fromBuilder.append("u.realname ExecuteManName , caa.create_date ExecuteTime , ca.time_interval InvestDay , caa.create_date BeginTime , ca.end_time EndTime , caa.apply_amount GrantAmount ,");
		fromBuilder.append("IFNULL(( SELECT COUNT(*) FROM cc_customer_visit cv WHERE cv.active_apply_id = caa.id GROUP BY caa.id) , 0) ShopVisitCount , IFNULL(( SELECT COUNT(*) FROM cc_customer_visit cv WHERE cv.active_apply_id = caa.id GROUP BY caa.id) , 0) ShopXCJHCount");
		fromBuilder.append(" FROM cc_activity_apply caa");
		fromBuilder.append(" LEFT JOIN cc_expense_detail ced ON caa.expense_detail_id = ced.id");
		fromBuilder.append(" LEFT JOIN cc_activity ca ON ca.id = caa.activity_id");
		fromBuilder.append(" LEFT JOIN cc_seller_customer csc ON csc.id = caa.seller_customer_id");
		fromBuilder.append(" LEFT JOIN cc_customer cc ON cc.id = csc.customer_id");
		fromBuilder.append(" LEFT JOIN `user` u ON u.id = caa.biz_user_id");
		fromBuilder.append(" LEFT JOIN cc_customer_visit ccv ON ccv.active_apply_id = caa.id");
		fromBuilder.append(" where caa.id ='"+activityApplyId+"' GROUP BY caa.id ");
		return Db.findFirst(fromBuilder.toString());
	}
	
	public Record findByApplyId(String applyId) {
		String sql = "SELECT cc.customer_name,cc.contact,caa.activity_id,caa.seller_customer_id,caa.id,cc.mobile,cc.prov_name,cc.city_name,cc.country_name,ca.title,t1.`name` as expenseDetailName from cc_activity_apply caa " + 
				"LEFT JOIN cc_activity ca on ca.id = caa.activity_id " + 
				"LEFT JOIN cc_seller_customer csc on csc.id = caa.seller_customer_id " + 
				"LEFT JOIN cc_customer cc on cc.id = csc.customer_id " + 
				"LEFT JOIN (SELECT ced.id,d.`name` from cc_expense_detail ced LEFT JOIN dict d on d.type = ced.flow_dict_type and ced.item1 = d.`value`) t1 on t1.id = caa.expense_detail_id " + 
				"where caa.id = '"+applyId+"' " + 
				"GROUP BY caa.id";
		return Db.findFirst(sql);
	}
	
	
	public List<Record> _findByCustomerId(String customerId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT a.id as activityApplyId, ca.title,d.`name`,a.create_date from cc_activity_apply a  ");
		fromBuilder.append(" LEFT JOIN cc_activity ca on ca.id = a.activity_id ");
		fromBuilder.append(" LEFT JOIN cc_expense_detail cea on a.expense_detail_id = cea.id ");
		fromBuilder.append(" LEFT JOIN dict d on d.type=cea.flow_dict_type and d.`value` = cea.item1 ");
		fromBuilder.append(" where a.seller_customer_id ='"+customerId+"' and a.`status` = '"+Consts.ACTIVITY_APPLY_STATUS_PASS+"'");
		fromBuilder.append(" GROUP BY a.id");
		return Db.find(fromBuilder.toString());
	}

	public Page<Record> visitAllDetailsPaginate(int pageNumber, int pageSize, String keyword, String startDate,
			String endDate, String activityId) {
		String select = "SELECT ccv.id customerVisitId,u.realname,ca.proc_code,(SELECT d.`name` FROM dict d WHERE d.`key` = ca.invest_type AND d.type = '"+Consts.INVEST_TYPE+"') investType,cc.customer_name,CONCAT(cc.prov_name,cc.city_name,cc.country_name,cc.address) address";
		select = select+",caa.create_date putDate ,ccv.photo,( SELECT group_concat(cct.`name`) FROM cc_customer_type cct WHERE LOCATE(cct.id , csc.customer_type_ids) > 0 GROUP BY csc.customer_type_ids) customer_type";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_customer_visit ccv ");
		fromBuilder.append(" LEFT JOIN cc_activity_apply caa ON caa.id=ccv.active_apply_id ");
		fromBuilder.append(" LEFT JOIN cc_activity ca ON ca.id=caa.activity_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_customer csc ON csc.id=ccv.seller_customer_id");
		fromBuilder.append(" LEFT JOIN cc_customer cc ON cc.id=csc.customer_id");
		fromBuilder.append(" LEFT JOIN `user` u ON u.id=ccv.user_id");
		fromBuilder.append(" WHERE ca.id='"+activityId+"'");
		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (cc.customer_name like '%"+keyword+"%' or u.realname like '%"+keyword+"%')");
		}
		LinkedList<Object> params = new LinkedList<Object>();
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ccv.create_date >= ?");
			params.add(startDate+" 00:00:00");
		}
		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ccv.create_date <= ?");
			params.add(endDate + "23:59:59");
		}
		fromBuilder.append(" ORDER BY putDate DESC");
		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
        return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Record findByActivityApplyId(String activityApplyId) {
		String sql = "SELECT ca.* FROM cc_activity ca LEFT JOIN cc_activity_apply caa ON caa.activity_id=ca.id WHERE caa.id='"+activityApplyId+"'";
		return Db.findFirst(sql);
	}

}
