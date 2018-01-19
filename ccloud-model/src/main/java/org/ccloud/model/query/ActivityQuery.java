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
	public Page<Activity> paginate(int pageNumber, int pageSize, String keyword,String startDate, String endDate,String sellerId) {
		String select = "select ca.*,ct.name as customerName ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity` ca ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct on ct.id=ca.customer_type");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "title", keyword, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

        if (params.isEmpty())
            return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and ca.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and ca.create_date <= ?");
			params.add(endDate);
		}
		fromBuilder.append(" and ca.seller_id='"+sellerId+"' ORDER BY ca.is_publish desc,ca.create_date desc");
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

    public List<Record> findActivityListForApp(String sellerId, String keyword, String tag) {
        StringBuilder fromBuilder = new StringBuilder(" SELECT a.*, d.name as categoryName FROM cc_activity a");
        fromBuilder.append(" left join dict d on a.category = d.value ");
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

}