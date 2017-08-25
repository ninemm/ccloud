/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.model.query;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.model.SalesFact;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesFactQuery extends JBaseQuery {

    protected static final SalesFact DAO = new SalesFact();
    private static final SalesFactQuery QUERY = new SalesFactQuery();

    public static SalesFactQuery me() {
        return QUERY;
    }

    public SalesFact findById(final String id) {
        return DAO.getCache(id, new IDataLoader() {
            @Override
            public Object load() {
                return DAO.findById(id);
            }
        });
    }

    public Page<SalesFact> paginate(int pageNumber, int pageSize, String orderby) {
        String select = "select * ";
        StringBuilder fromBuilder = new StringBuilder("from `sales_fact` ");

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

    // 订单记录总数
    public Long findOrderCount() {

        StringBuilder sql = new StringBuilder("select count(*) from (");
        sql.append(" select stockId from sales_fact group by stockId) as sales");
        return Db.queryLong(sql.toString());

    }

    // 订单总金额
    public Double findTotalAmount(String provName, String cityName, String countryName) {

        LinkedList<Object> params = new LinkedList<Object>();
        StringBuilder sqlBuilder =
                new StringBuilder("select TRUNCATE(SUM(totalSales)/100, 2) as totalAmount");
        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        return Db.queryBigDecimal(sqlBuilder.toString()).doubleValue();
    }
    
    // 订单客户总数
    public Long findCustomerCount() {

        StringBuilder sql = new StringBuilder("select count(*) from (");
        sql.append(" select customerId from sales_fact group by customerId) as customer");
        return Db.queryLong(sql.toString());

    }
    

    public List<Map<String, Object>> findAreaArray(String provName, String cityName,
            String countryName, String startDate, String endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select ");

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(" countryName");
        } else if (StrKit.notBlank(provName)) {
            sqlBuilder.append(" cityName");
        } else {
            sqlBuilder.append(" provName");
        }

        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by provName");
        if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        }

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        }
        sqlBuilder.append(" order by totalAmount desc");

        return Db.query(sqlBuilder.toString(), params.toArray());

    }

    public List<Record> findAreaList(String provName, String cityName, String countryName,
            String startDate, String endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select countryName, cityName, provName");

        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by provName");
        if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        }

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        }
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());

    }

    public List<Record> findAreaListByCustomerType(String provName, String cityName,
            String countryName, String startDate, String endDate, String customerTypeName) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select customerTypeName");

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        } else if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        } else {
            sqlBuilder.append(", provName");
        }

        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "customerTypeName", customerTypeName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }

        sqlBuilder.append(" group by provName");
        if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        }

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        }
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());
    }

    public List<Record> findAreaListByProduct(String provName, String cityName, String countryName,
            String startDate, String endDate, String cInvCode) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select cInvName");

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        } else if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        } else {
            sqlBuilder.append(", provName");
        }

        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "cInvCode", cInvCode, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by provName");
        if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        }

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        }
        sqlBuilder.append(" order by totalNum desc");

        return Db.find(sqlBuilder.toString(), params.toArray());

    }

    public List<Record> findCustomerTypeList(String provName, String cityName, String countryName,
            String startDate, String endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select customerTypeName,customerType");

        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by customerType");
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());
    }

    public List<Record> findCustomerTypeListByProduct(String provName, String cityName,
            String countryName, String startDate, String endDate, String cInvCode) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select cInvName, customerTypeName");

        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "cInvCode", cInvCode, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by customerType");
        sqlBuilder.append(" order by totalNum desc");

        return Db.find(sqlBuilder.toString(), params.toArray());

    }

    public List<Record> findProductList(String provName, String cityName, String countryName,
            String startDate, String endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select cInvName, cInvCode");

        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by cInvCode");
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());
    }

    public List<Record> findProductListByArea(String provName, String cityName, String countryName,
            String startDate, String endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select cInvName");

        if (StrKit.notBlank(countryName)) {
            sqlBuilder.append(", countryName");
        } else if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", cityName");
        } else {
            sqlBuilder.append(", provName");
        }

        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by cInvCode");
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());
    }

    public List<Record> findProductListByCustomerType(String provName, String cityName,
            String countryName, String startDate, String endDate, String customerTypeName) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select customerTypeName, cInvName");

        sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "customerTypeName", customerTypeName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }

        sqlBuilder.append(" group by cInvCode");
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());
    }

    public List<Record> findOrderAmount(String provName, String cityName, String countryName,
            Date startDate, Date endDate) {

        LinkedList<Object> params = new LinkedList<Object>();

        StringBuilder sqlBuilder = new StringBuilder("select ");

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(" countryName");
        } else if (StrKit.notBlank(provName)) {
            sqlBuilder.append(" cityName");
        } else {
            sqlBuilder.append(" provName");
        }

        sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

        sqlBuilder.append(" from sales_fact");

        boolean needWhere = true;
        needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
        needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

        if (needWhere) {
            sqlBuilder.append(" where 1 = 1");
        }

        if (startDate != null) {
            sqlBuilder.append(" and idate >= ?");
            params.add(startDate);
        }

        if (endDate != null) {
            sqlBuilder.append(" and idate <= ?");
            params.add(endDate);
        }
        sqlBuilder.append(" and customerType != 7");

        sqlBuilder.append(" group by provName");
        if (StrKit.notBlank(provName)) {
            sqlBuilder.append(", cityName");
        }

        if (StrKit.notBlank(cityName)) {
            sqlBuilder.append(", countryName");
        }
        sqlBuilder.append(" order by totalAmount desc");

        return Db.find(sqlBuilder.toString(), params.toArray());

    }

    public List<SalesFact> queryMapData(String provName, String cityName, String countryName,
            String beginDate, String endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT provName, cityName, countryName, TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount ");
        sql.append(" FROM `sales_Fact` ");

        if (StrKit.notBlank(cityName)) {

            sql.append(" where provName = ? and cityName = ? and iDate >= ? and iDate <= ? ");
            sql.append(" GROUP BY countryName ");

            return DAO.find(sql.toString(), provName, cityName, beginDate, endDate);

        } else if (StrKit.notBlank(provName)) {

            sql.append(" where provName = ? and iDate >= ? and iDate <= ? ");
            sql.append("GROUP BY cityName ");

            return DAO.find(sql.toString(), provName, beginDate, endDate);

        } else {

            sql.append(" where iDate >= ? and iDate <= ? ");
            sql.append("GROUP BY provName ");

            return DAO.find(sql.toString(), beginDate, endDate);

        }
    }

}
