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
		String select = "select ca.*,case when ca.category='"+Consts.CATEGORY_NORMAL+"' then '商品销售营销活动' else '投入活动' end as activityCategory ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity` ca ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct on ct.id=ca.customer_type");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "title", keyword, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ca.start_time >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ca.end_time <= ?");
			params.add(endDate);
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
		StringBuilder fromBuilder = new StringBuilder("SELECT a.title,a.id FROM cc_activity a ");
        fromBuilder.append(" LEFT JOIN cc_activity_apply aa ON a.id=aa.activity_id ");
	    fromBuilder.append(" WHERE aa.seller_customer_id='"+customerId);
	    fromBuilder.append(" ' GROUP BY a.id");
        return Db.find(fromBuilder.toString());
	}
}
