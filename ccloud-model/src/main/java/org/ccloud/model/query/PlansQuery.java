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
import org.ccloud.model.Plans;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PlansQuery extends JBaseQuery { 

	protected static final Plans DAO = new Plans();
	private static final PlansQuery QUERY = new PlansQuery();

	public static PlansQuery me() {
		return QUERY;
	}

	public Plans findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	
	
	
	
	public Page<Plans> paginate(int pageNumber, int pageSize, String keyword, String orderby, String dataArea,String type,String startDate,String endDate,String dateType) {
		String select = "SELECT cp.id,cp.user_id,cp.type,cp.seller_product_id,csp.price,cp.start_date,cp.end_date,cs.seller_name,u.realname,csp.custom_name ,sum(plan_num) as planNum, sum(complete_num) as completeNum ,cp.complete_ratio  ";
		StringBuilder fromBuilder = new StringBuilder("FROM cc_plans cp  ");
		fromBuilder.append("LEFT JOIN cc_seller cs on cs.id = cp.seller_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = cp.user_id ");
		fromBuilder.append("LEFT JOIN cc_seller_product csp on csp.id = cp.seller_product_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "u.realname", keyword, params, true);

		
		if(needWhere) {
			fromBuilder.append("where 1=1 ");
		}
		/*if(!type.equals("")) {
			fromBuilder.append(" and cp.type = '"+type+"' ");
			if(!type.equals(Consts.WEEK_PLAN) && StrKit.notBlank(dateType)) {
				fromBuilder.append(" and cp.start_date >= '"+startDate+"' and cp.start_date <= '"+endDate+" 23:59:59' ");
			}
		}*/
		if(StrKit.notBlank(dateType)) {
			fromBuilder.append("and cp.plans_month = '"+dateType+"-01 00:00:00' ");
		}
		fromBuilder.append("and cp.data_area like '"+dataArea+"' ");
		fromBuilder.append("GROUP BY cp.seller_id,cp.user_id, cp.type, cp.seller_product_id ,cp.start_date,cp.end_date ");
		fromBuilder.append("order by cp.type,cp.start_date desc,cp.end_date desc ,cp.user_id");
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String userId, String type,
	                                   String startDate, String endDate, String sellerId, String dataArea, String showType, String sellerProductId) {
		String select = "SELECT pd.*,d.name as typeName,u.realname,csp.custom_name,cp.start_date,cp.end_date,SUM(pd.plan_num*csp.price) as AmountPlan,SUM(pd.complete_num*csp.price) as AmountComplete  ";
		StringBuilder fromBuilder = new StringBuilder("from cc_plans_detail pd ");
		fromBuilder.append("LEFT JOIN cc_plans cp on cp.id = pd.plans_id ");
		fromBuilder.append("LEFT JOIN cc_seller_product csp on csp.id = pd.seller_product_id ");
		fromBuilder.append("LEFT JOIN `user` u on u.id = pd.user_id ");
		fromBuilder.append("left join dict d ON cp.type = d.value ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "u.realname", keyword, params, needWhere);
		if(showType != null && showType.equals(Consts.PLAN_SHOW_SELLER_PRODUCT)) {
			needWhere = appendIfNotEmpty(fromBuilder, "pd.seller_product_id", sellerProductId, params, needWhere);
		}else {
			needWhere = appendIfNotEmpty(fromBuilder, "pd.user_id", userId, params, needWhere);
		}
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cp.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cp.seller_id", sellerId, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cp.start_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cp.end_date <= ?");
			params.add(endDate);
		}
		if(showType != null && showType.equals(Consts.PLAN_SHOW_SELLER_PRODUCT)) {
			fromBuilder.append(" GROUP BY pd.seller_product_id,cp.id  order by cp.create_date desc ");
		}else {
			fromBuilder.append(" GROUP BY pd.user_id,cp.id order by cp.create_date desc ");
		}

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public List<Plans> findBySales(String userId, String sellerProductId,String date) {
		return DAO.doFind("user_id = ? and seller_product_id = ? and start_date <= ? and end_date >= ? ", userId, sellerProductId, date, date);
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

	public List<Plans> findbyUserNameAndTypeNameAndPlanId(String userId,String typeName, String plansId){
		String sql  = "select o.*,sp.custom_name "
				+ "from `cc_plans_detail` o "
				+ "left join cc_seller_product sp ON o.seller_product_id = sp.id "
				+ "where o.user_id = '"+userId+"' and o.plans_id = '"+plansId+"' "
				+ " ORDER BY sp.custom_name";
		return DAO.find(sql);
	}
	
	public List<Plans> findbyDateArea(String dataArea){
		String sql = "SELECT cp.*,csp.custom_name from cc_plans cp "
				+ "LEFT JOIN cc_seller_product csp on csp.id = cp.seller_product_id "
				+ "where cp.data_area like '"+dataArea+"' GROUP BY cp.seller_product_id";
		return DAO.find(sql);
	}
	
	public List<Plans> findbySTSE(String sellerProductId,String typeName, String plansId){
		String sql  = "select o.*,u.realname "
				+ "from `cc_plans_detail` o "
				+ "join user u ON o.user_id = u.id "
				+ "where o.seller_product_id = '"+sellerProductId+"' and o.plans_id = '"+plansId+"' "
				+ " ORDER BY u.realname";
		return DAO.find(sql);
	}
	
	public Plans findbySSEU(String sellerProductId,String startDate,String endDate,String userId) {
		return DAO.doFindFirst("seller_product_id = ? and start_date = ? and end_date = ? and user_id = ?", sellerProductId,startDate,endDate,userId);
	}
	
	public Page<Record> paginateForAppMyPlan(int pageNumber, int pageSize, String keyword,
            String startDate, String endDate, String sellerId, String dataArea,String userId,String sellerProductId) {
			String select = "select o.*, u.realname, d.name as typeName, sp.custom_name,SUM(o.plan_num*sp.price) AS planNumAmount,SUM(o.complete_num*sp.price) AS completeNumAmount ";
			StringBuilder fromBuilder = new StringBuilder("from `cc_plans` o ");
			fromBuilder.append("join user u ON o.user_id = u.id ");
			fromBuilder.append("left join cc_seller_product sp ON o.seller_product_id = sp.id ");
			fromBuilder.append("left join dict d ON o.type = d.value ");
			
			LinkedList<Object> params = new LinkedList<Object>();
			boolean needWhere = true;
			
			needWhere = appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, needWhere);
//			needWhere = appendIfNotEmpty(fromBuilder, "o.type", type, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "o.user_id", userId, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "o.seller_product_id", sellerProductId, params, needWhere);
			needWhere = appendIfNotEmptyWithLike(fromBuilder, "o.data_area", dataArea, params, needWhere);
			needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);
			
			if (needWhere) {
			fromBuilder.append(" where 1 = 1");
			}
			
			if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.start_date >= ?");
			params.add(startDate);
			}
			
			if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.start_date <= ?");
			params.add(endDate);
			}
			fromBuilder.append(" and o.type in ('"+Consts.MONTH_PLAN+"') GROUP BY o.seller_id,o.user_id, o.type, o.seller_product_id ,o.start_date,o.end_date order by o.start_date desc,o.complete_ratio desc, o.create_date desc ");
			
			if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());
			
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
}
