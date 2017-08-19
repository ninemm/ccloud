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

import org.ccloud.model.StockFact;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class StockFactQuery extends JBaseQuery { 

	protected static final StockFact DAO = new StockFact();
	private static final StockFactQuery QUERY = new StockFactQuery();

	public static StockFactQuery me() {
		return QUERY;
	}

	public StockFact findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<StockFact> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `stock_fact` ");

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
	
    public List<Record> findAreaList(String provName, String cityName, String countryName) {
        
        LinkedList<Object> params = new LinkedList<Object>();
        
        StringBuilder sqlBuilder = new StringBuilder("select cInvName");
        
        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        
        sqlBuilder.append(" from stock_fact");
        
        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);
        
        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }
        sqlBuilder.append(" and idate = DATE_SUB(CURDATE(), INTERVAL 1 DAY) ");
        
        sqlBuilder.append(" group by cInvCode");
        sqlBuilder.append(" order by totalNum asc");
        
        return Db.find(sqlBuilder.toString(), params.toArray());
        
     }
    
     public List<Record> findDateList(String provName, String cityName, String countryName, String cInvCode) {
        
        LinkedList<Object> params = new LinkedList<Object>();
        
        StringBuilder sqlBuilder = new StringBuilder("select idate");
        
        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 0) as totalNum");
        
        sqlBuilder.append(" from stock_fact");
        
        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);
        
        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }
        sqlBuilder.append(" and cInvCode = ?");
        params.add(cInvCode);
        sqlBuilder.append(" group by idate  ");
        sqlBuilder.append(" order by idate asc");
        
        return Db.find(sqlBuilder.toString(), params.toArray());
        
     }
	
}
