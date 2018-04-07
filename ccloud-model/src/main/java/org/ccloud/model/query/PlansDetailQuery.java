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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.PlansDetail;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PlansDetailQuery extends JBaseQuery { 

	protected static final PlansDetail DAO = new PlansDetail();
	private static final PlansDetailQuery QUERY = new PlansDetailQuery();

	public static PlansDetailQuery me() {
		return QUERY;
	}

	public PlansDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String sellerId, String dataArea,String userId,String sellerProductId,String plansId) {
			
			String select = "SELECT pd.*,sp.custom_name,u.realname ";
			StringBuilder fromBuilder = new StringBuilder("from cc_plans_detail pd ");
			fromBuilder.append("LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id ");
			fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
			fromBuilder.append("left join cc_plans cp ON pd.plans_id = cp.id ");
			
			LinkedList<Object> params = new LinkedList<Object>();
			boolean needWhere = true;
			
			needWhere = appendIfNotEmpty(fromBuilder, "cp.id", plansId, params, needWhere);
//			needWhere = appendIfNotEmpty(fromBuilder, "o.type", type, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "pd.user_id", userId, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "pd.seller_product_id", sellerProductId, params, needWhere);
			needWhere = appendIfNotEmptyWithLike(fromBuilder, "pd.data_area", dataArea, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "cp.seller_id", sellerId, params, needWhere);
			
			if (needWhere) {
			fromBuilder.append(" where 1 = 1");
			}
			
			fromBuilder.append("  ORDER BY pd.user_id,pd.seller_product_id ");
			
			if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
			
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<Record> paginateForAppMyPlan(int pageNumber, int pageSize, String keyword,
            String startDate, String endDate, String sellerId, String dataArea,String userId,String sellerProductId,String datetimePicker) {
			SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd" ); 
			String str = sdf.format(new Date());
			String select = "select o.*,cp.start_date,cp.end_date, u.realname, d.name as typeName, sp.custom_name,SUM(o.plan_num*sp.price) AS planNumAmount,SUM(o.complete_num*sp.price) AS completeNumAmount ";
			StringBuilder fromBuilder = new StringBuilder("from `cc_plans_detail` o ");
			fromBuilder.append("join user u ON o.user_id = u.id ");
			fromBuilder.append("left join cc_seller_product sp ON o.seller_product_id = sp.id ");
			fromBuilder.append("left join cc_plans cp ON o.plans_id = cp.id ");
			fromBuilder.append("left join dict d ON cp.type = d.value ");
			
			LinkedList<Object> params = new LinkedList<Object>();
			boolean needWhere = true;
			
			needWhere = appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, needWhere);
//			needWhere = appendIfNotEmpty(fromBuilder, "o.type", type, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "o.user_id", userId, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "o.seller_product_id", sellerProductId, params, needWhere);
//			needWhere = appendIfNotEmptyWithLike(fromBuilder, "cp.data_area", dataArea, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "cp.seller_id", sellerId, params, needWhere);
			
			if (needWhere) {
			fromBuilder.append(" where 1 = 1");
			}
			
			if(StrKit.notBlank(datetimePicker)){
				fromBuilder.append(" and cp.plans_month = '"+datetimePicker+"-01 00:00:00' ");
			}else {
				fromBuilder.append("and cp.start_date <= '"+str+"' and end_date >= '"+str+"' ");
			}
			/*if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cp.start_date >= ?");
			params.add(startDate);
			}
			
			if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cp.start_date <= ?");
			
			}*/
			fromBuilder.append(" and cp.type in ('"+Consts.MONTH_PLAN+"') GROUP BY cp.seller_id,o.user_id, cp.type, o.seller_product_id ,cp.start_date,cp.end_date order by cp.start_date desc,o.complete_ratio desc, cp.create_date desc ");
			
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

	public PlansDetail findbySSEU(String sellerProductId,String startDate,String endDate,String userId) {
		String sql = "SELECT pd.* from cc_plans_detail pd LEFT JOIN cc_plans cp on cp.id = pd.plans_id "
				+ "where pd.seller_product_id = '"+sellerProductId+"' "
				+ "and cp.start_date = '"+startDate+"' "
				+ "and cp.end_date = '"+endDate+"' "
				+ "and pd.user_id = '"+userId+"'  GROUP BY pd.id";
		return DAO.findFirst(sql);
	}
	
	public List<Record> findDataAreaAndDataType(String dataArea,String dateType,String type){
		StringBuilder fromBuilder = new StringBuilder();
		
		if(type.equals("sellerProudct")){
			fromBuilder.append("select pd.*,sp.custom_name,pd.seller_product_id from cc_plans_detail pd  ");
			fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
			fromBuilder.append("LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id ");
			fromBuilder.append("where cp.plans_month = '"+dateType+"-01 00:00:00' ");
			fromBuilder.append("and cp.data_area like '"+dataArea+"' ");
			fromBuilder.append("GROUP BY pd.seller_product_id ");
		}else {
			fromBuilder.append("select pd.*,u.realname from cc_plans_detail pd  ");
			fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
			fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
			fromBuilder.append("where cp.plans_month = '"+dateType+"-01 00:00:00' ");
			fromBuilder.append("and cp.data_area like '"+dataArea+"' ");
		}
		return Db.find(fromBuilder.toString());
	}
	public List<PlansDetail> findbyDateArea(String dataArea,String sellerId){
		String sql = "SELECT pd.*,csp.custom_name from cc_plans_detail pd "
				+ "LEFT JOIN cc_plans cp on cp.id = pd.plans_id "
				+ "LEFT JOIN cc_seller_product csp on csp.id = pd.seller_product_id "
				+ "where pd.data_area like '"+dataArea+"'  GROUP BY pd.seller_product_id";
		return DAO.find(sql);
	}
	
	public List<PlansDetail> findbyDateAreaAndUserId(String dataArea,String userId,String sellerId){
		String sql = "SELECT pd.*,csp.custom_name from cc_plans_detail pd "
				+ "LEFT JOIN cc_plans cp on cp.id = pd.plans_id "
				+ "LEFT JOIN cc_seller_product csp on csp.id = pd.seller_product_id "
				+ "where cp.seller_id = '"+sellerId+"' and pd.user_id = '"+userId+"' "
				+ " GROUP BY pd.seller_product_id";
		return DAO.find(sql);
	}
	
	public List<PlansDetail> findBySales(String userId, String sellerProductId,String date) {
		String sql = "select pd.* from cc_plans_detail pd LEFT JOIN cc_plans cp on cp.id = pd.plans_id "
				+ "where pd.user_id = '"+userId+"' and pd.seller_product_id = '"+sellerProductId+"' and cp.start_date <= '"+date+"' and cp.end_date >= '"+date+"'";
		return DAO.find(sql);
	}
	
	public List<Record> findBySellerId(String sellerId , String plansMonth,String dataArea){
		/*SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd" ); 
		String str = sdf.format(new Date());*/
		StringBuilder fromBuilder = new StringBuilder("select pd.seller_product_id,sp.custom_name,"
				+ "t1.plansAmount as totalPlansAmount,t1.completeAmount as totalCompleteAmount,t1.plansNum as pNum,t1.completeNum as cNum, "
				+ "convert((t1.completeAmount)/(t1.plansAmount)*100,decimal(10,2)) as completeRetio ");
		fromBuilder.append("from cc_plans_detail pd ");
		fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id ");
		fromBuilder.append("LEFT JOIN (SELECT pd.seller_product_id,convert(SUM(sp.price * pd.plan_num),decimal(10,2)) as plansAmount,convert(SUM(sp.price * pd.complete_num),decimal(10,2)) as completeAmount,"
				+ "SUM(pd.plan_num) as plansNum,SUM(pd.complete_num) as completeNum "
				+ "from cc_plans_detail pd LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id "
				+ "where pd.data_area like '"+dataArea+"' GROUP BY pd.seller_product_id) t1 "
				+ "on t1.seller_product_id = pd.seller_product_id ");
		fromBuilder.append("where cp.seller_id = '"+sellerId+"' ");
		if(StrKit.notBlank(plansMonth)){
			fromBuilder.append("and cp.plans_month = '"+plansMonth+"-01 00:00:00' ");
		}/*else {
			fromBuilder.append("and cp.start_date <= '"+str+"' and cp.end_date >= '"+str+"' ");
		}*/
		fromBuilder.append("GROUP BY pd.seller_product_id ORDER BY pd.seller_product_id");
		return Db.find(fromBuilder.toString());
	}
	
	public List<Record> findAllBySellerId(String sellerId , String plansMonth,String dataArea){
		/*SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd" ); 
		String str = sdf.format(new Date());*/
		StringBuilder fromBuilder = new StringBuilder("select pd.*,cp.start_date as startDate,cp.end_date as endDate,cp.plans_month as plansMonth,cp.type,u.realname,"
				+ "t1.plansAmount,t1.completeAmount,SUM(t1.plansAmount) as totalPlansAmount,SUM(t1.completeAmount) as totalCompleteAmount ");
		fromBuilder.append("from cc_plans_detail pd ");
		fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
		fromBuilder.append("LEFT JOIN (SELECT pd.seller_product_id,pd.user_id,pd.plans_id,convert(SUM(sp.price * pd.plan_num),decimal(10,2)) as plansAmount,convert(SUM(sp.price * pd.complete_num),decimal(10,2)) as completeAmount "
				+ "from cc_plans_detail pd LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id "
				+ "where pd.data_area like '"+dataArea+"' GROUP BY pd.plans_id,pd.user_id,pd.seller_product_id) t1 "
				+ "on t1.seller_product_id = pd.seller_product_id and t1.user_id = pd.user_id and t1.plans_id = pd.plans_id ");
		fromBuilder.append("where cp.seller_id = '"+sellerId+"' ");
		if(StrKit.notBlank(plansMonth)){
			fromBuilder.append("and cp.plans_month = '"+plansMonth+"-01 00:00:00' ");
		}/*else {
			fromBuilder.append("and cp.start_date <= '"+str+"' and cp.end_date >= '"+str+"' ");
		}*/
		fromBuilder.append("GROUP BY cp.id,pd.user_id, pd.seller_product_id ORDER BY cp.id,pd.user_id,pd.seller_product_id ");
		return Db.find(fromBuilder.toString());
	}
	
	public List<Record> _findAllBySellerId(String sellerId , String plansMonth,String dataArea){
		/*SimpleDateFormat sdf =   new SimpleDateFormat( " yyyy-MM-dd" ); 
		String str = sdf.format(new Date());*/
		StringBuilder fromBuilder = new StringBuilder("select pd.*,cp.start_date as startDate,cp.end_date as endDate,cp.plans_month as plansMonth,cp.type,u.realname,"
				+ "t1.plansAmount,t1.completeAmount,SUM(t1.plansAmount) as totalPlansAmount,SUM(t1.completeAmount) as totalCompleteAmount,convert((t1.completeAmount)/(t1.plansAmount)*100,decimal(10,2)) as completeRetio ");
		fromBuilder.append("from cc_plans_detail pd ");
		fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
		fromBuilder.append("LEFT JOIN (SELECT pd.seller_product_id,convert(SUM(sp.price * pd.plan_num),decimal(10,2)) as plansAmount,convert(SUM(sp.price * pd.complete_num),decimal(10,2)) as completeAmount "
				+ "from cc_plans_detail pd LEFT JOIN cc_seller_product sp on sp.id = pd.seller_product_id "
				+ "where pd.data_area like '"+dataArea+"' GROUP BY pd.plans_id,pd.user_id) t1 "
				+ "on t1.seller_product_id = pd.seller_product_id ");
		fromBuilder.append("where cp.seller_id = '"+sellerId+"' ");
		if(StrKit.notBlank(plansMonth)){
			fromBuilder.append("and cp.plans_month = '"+plansMonth+"-01 00:00:00' ");
		}/*else {
			fromBuilder.append("and cp.start_date <= '"+str+"' and cp.end_date >= '"+str+"' ");
		}*/
		fromBuilder.append("GROUP BY cp.id,pd.user_id ORDER BY cp.id,pd.user_id ");
		return Db.find(fromBuilder.toString());
	}
	
	public List<Record> findUserBySellerId(String sellerId , String plansMonth){
		StringBuilder fromBuilder = new StringBuilder("select pd.*,cp.start_date as startDate,cp.end_date as endDate,cp.plans_month as plansMonth,cp.type,u.realname ");
		fromBuilder.append("from cc_plans_detail pd ");
		fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
		fromBuilder.append("where cp.seller_id = '"+sellerId+"' ");
		if(StrKit.notBlank(plansMonth)){
			fromBuilder.append("and cp.plans_month = '"+plansMonth+"-01 00:00:00' ");
		}
		fromBuilder.append("GROUP BY pd.user_id ORDER BY pd.user_id ");
		return Db.find(fromBuilder.toString());
	}
	
	public List<PlansDetail> findbyDateAreaAndPlansId(String dataArea,String plansId){
		String sql = "SELECT pd.*,csp.custom_name from cc_plans_detail pd "
				+ "LEFT JOIN cc_plans cp on cp.id = pd.plans_id "
				+ "LEFT JOIN cc_seller_product csp on csp.id = pd.seller_product_id "
				+ "where pd.data_area like '"+dataArea+"' and pd.plans_id = '"+plansId+"' GROUP BY pd.seller_product_id";
		return DAO.find(sql);
	}
}
