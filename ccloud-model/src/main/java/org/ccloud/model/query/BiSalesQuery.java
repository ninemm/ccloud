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
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class BiSalesQuery extends JBaseQuery {

	private static final BiSalesQuery QUERY = new BiSalesQuery();

	public static BiSalesQuery me() {
		return QUERY;
	}

	// 订单总金额
	public Double findTotalAmount(String sellerId, String provName, String cityName, String countryName,
	                              String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("SELECT ifnull(SUM(cso.total_amount),0) as totalAmount FROM cc_sales_order so");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sojo ON sojo.order_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso ON cso.id=sojo.outstock_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and cso.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and cso.biz_date <= ?");
			params.add(endDate);
		}

		return Db.queryBigDecimal(sqlBuilder.toString(), params.toArray()).doubleValue();

	}

	// 订单记录总数
	public Long findOrderCount(String sellerId, String provName, String cityName, String countryName,
	                           String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder(" SELECT count(1) FROM cc_sales_order so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sojo ON sojo.order_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso ON cso.id=sojo.outstock_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and cso.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and cso.biz_date <= ?");
			params.add(endDate);
		}
		return Db.queryLong(sqlBuilder.toString(), params.toArray());
	}

	// 订单客户总数
	public Long findCustomerCount(String sellerId, String provName, String cityName, String countryName,
	                              String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("select count(1) from (");
		sqlBuilder.append(" SELECT so.customer_id FROM cc_sales_order so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sojo ON sojo.order_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso ON cso.id=sojo.outstock_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and cso.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and cso.biz_date <= ?");
			params.add(endDate);
		}
		sqlBuilder.append(" GROUP BY so.customer_id ) t1");
		return Db.queryLong(sqlBuilder.toString(), params.toArray());

	}

	// 客户总数
	public Long findAllCustomerCount(String sellerId, String provName, String cityName, String countryName) {

		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sqlBuilder = new StringBuilder("SELECT count(1) FROM cc_seller_customer csc");
		sqlBuilder.append(" LEFT JOIN cc_customer cc ON cc.id = csc.customer_id ");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "csc.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		sqlBuilder.append(" and csc.is_enabled = 1 ");
		return Db.queryLong(sqlBuilder.toString(), params.toArray());

	}

	// 订单平均金额
	public List<Record> findOrderAvgAmountList(String sellerId, String provName, String cityName, String countryName,
	                                           String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT CONCAT(YEAR(cso.create_date) ,'-', MONTH(cso.create_date),'-',DAY(cso.create_date)) idate,");
		sqlBuilder.append(" TRUNCATE((SUM(cso.total_amount)/COUNT(1)),2)as avgAmount FROM cc_sales_order so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sojo ON sojo.order_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock cso ON cso.id=sojo.outstock_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and cso.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and cso.biz_date <= ?");
			params.add(endDate);
		}
		sqlBuilder.append(" GROUP BY YEAR(cso.create_date) , MONTH(cso.create_date),DAY(cso.create_date) ");
		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findAreaListByCustomerType(String dealerCode, String provName, String cityName,
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
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
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

	//产品区域分布
	public List<Record> findAreaListByProduct(String dealerCode, String provName, String cityName, String countryName,
	                                          String startDate, String endDate, String cInvCode) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select sp.custom_name cInvName ,");
		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(" c.country_name countryName");
		} else if (StrKit.notBlank(provName)) {
			sqlBuilder.append(" c.city_name cityName");
		} else {
			sqlBuilder.append(" c.prov_name provName");
		}

		sqlBuilder.append(", TRUNCATE( SUM(sod.product_count) / p.convert_relate , 2) totalNum , TRUNCATE( sum(sod.product_amount) / 10000 , 2) totalAmount ");

		sqlBuilder.append(" FROM cc_sales_outstock so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail sod ON sod.outstock_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id=sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON p.id=sp.product_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "sod.sell_product_id", cInvCode, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}
		if (StrKit.notBlank(startDate)) {
			sqlBuilder.append(" and so.biz_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			sqlBuilder.append(" and so.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" group by c.prov_name");
		if (StrKit.notBlank(provName)) {
			sqlBuilder.append(", c.city_name");
		}

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(", c.country_name");
		}
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());

	}

	public List<Record> findCustomerTypeList(String sellerId, String provName, String cityName, String countryName,
	                                         String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT ct.`name` ");
		sqlBuilder.append("FROM cc_sales_outstock o LEFT JOIN cc_seller_customer cc ON o.customer_id = cc.id ");
		sqlBuilder.append("LEFT JOIN cc_customer cu on cc.customer_id = cu.id ");
		sqlBuilder.append("LEFT JOIN cc_customer_type ct on ct.id = o.customer_type_id ");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where o.status != ? and cc.customer_kind = ? ");
		} else {
			sqlBuilder.append(" and o.status != ? and cc.customer_kind = ? ");
		}
		params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		params.add(Consts.CUSTOMER_KIND_COMMON);
		if (startDate != null) {
			sqlBuilder.append(" and o.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and o.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" group by o.customer_type_id");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	//产品客户分布
	public List<Record> findCustomerTypeListByProduct(String sellerId, String provName, String cityName,
	                                                  String countryName, String startDate, String endDate, String cInvCode) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT ct.`name` customerTypeName , TRUNCATE( SUM(sod.product_count) / p.convert_relate , 2) totalNum , TRUNCATE( sum(sod.product_amount) / 10000 , 2) totalAmount ");
		sqlBuilder.append(" FROM cc_sales_outstock so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail sod ON sod.outstock_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id=sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON p.id=sp.product_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer_join_customer_type cjct ON so.customer_id=cjct.seller_customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer_type ct ON ct.id=cjct.customer_type_id ");
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "sod.sell_product_id", cInvCode, params, needWhere);
		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and so.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and so.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" GROUP BY ct.id");
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());

	}

	//产品销售排行
	public List<Record> findProductList(String dealerCode, String provName, String cityName, String countryName,
	                                    String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT sp.custom_name,sp.id, TRUNCATE(SUM(sod.product_count)/p.convert_relate,2) productCount,TRUNCATE(sum(sod.product_amount)/10000,2) totalAmount ");
		sqlBuilder.append(" FROM cc_sales_outstock so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail sod ON sod.outstock_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id=sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON p.id=sp.product_id ");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and so.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and so.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" GROUP BY sp.id");
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findProductListByArea(String dealerCode, String provName, String cityName, String countryName,
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
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
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

	public List<Record> findProductListByCustomerType(String dealerCode, String provName, String cityName,
	                                                  String countryName, String startDate, String endDate, String customerTypeName) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select customerTypeName, cInvName");

		sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
		sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

		sqlBuilder.append(" from sales_fact");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
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

	public List<Record> findOrderAmount(String dealerCode, String provName, String cityName, String countryName,
	                                    String startDate, String endDate, int divideFlg) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select ");

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(" countryName");
		} else if (StrKit.notBlank(provName)) {
			sqlBuilder.append(" cityName");
		} else {
			sqlBuilder.append(" provName");
		}

		if (divideFlg == 1) {
			sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");
		} else if (divideFlg == 2) {
			sqlBuilder.append(", TRUNCATE(SUM(totalSales)/10000000, 2) as totalAmount");
		} else if (divideFlg == 3) {
			sqlBuilder.append(", TRUNCATE(SUM(totalSales)/4000000, 2) as totalAmount");
		} else if (divideFlg == 4) {
			sqlBuilder.append(", TRUNCATE(SUM(totalSales)/2000000, 2) as totalAmount");
		}

		sqlBuilder.append(" from sales_fact");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			sqlBuilder.append(" and idate >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
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

	//经销商销售排行和销售情况
	public List<Record> findsalesList(boolean isDealer, String provName, String cityName, String countryName,
	                                  String sellerId, String startDate, String endDate, String dataArea) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT s.id, d.data_area, TRUNCATE( sum(sod.product_amount) / 10000 , 2) totalAmount, ");
		if (isDealer) {
			sqlBuilder.append(" s.seller_name dealerName,s.id dealerCode ");
		} else {
			sqlBuilder.append(" s.seller_name sellerName,s.id dealerCode ");
		}
		sqlBuilder.append(" FROM cc_sales_outstock so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail sod ON sod.outstock_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller s ON so.seller_id=s.id ");
		sqlBuilder.append(" LEFT JOIN department d ON d.id=s.dept_id ");
		if (isDealer) {
			sqlBuilder.append(" where s.seller_type=" + Consts.SELLER_TYPE_DEALER);
		} else {
			sqlBuilder.append(" WHERE d.data_area LIKE (SELECT CONCAT('" + dataArea + "','%') FROM cc_seller s LEFT JOIN department d ON d.id=s.dept_id WHERE s.id='" + sellerId + "')");
		}

		boolean needWhere = false;
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);


		if (startDate != null) {
			sqlBuilder.append(" and so.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and so.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" GROUP BY s.id");
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());

	}

	//经销商产品销售排行
	public List<Record> findProductListByDealer(String provName, String cityName, String countryName, String sellerId,
	                                            String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT sp.custom_name cInvName, TRUNCATE(SUM(sod.product_count)/p.convert_relate,2) totalNum,TRUNCATE(sum(sod.product_amount)/10000,2) totalAmount ");
		sqlBuilder.append(" FROM cc_sales_outstock so ");
		sqlBuilder.append(" LEFT JOIN cc_seller_customer sc ON sc.id=so.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_customer c ON c.id=sc.customer_id ");
		sqlBuilder.append(" LEFT JOIN cc_sales_outstock_detail sod ON sod.outstock_id=so.id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id=sod.sell_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON p.id=sp.product_id ");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "c.country_name", countryName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "so.seller_id", sellerId, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where 1 = 1");
		}

		if (startDate != null) {
			sqlBuilder.append(" and so.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and so.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" GROUP BY sp.id");
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());

	}

	public List<Record> findProductListBySeller(String provName, String cityName, String countryName, String dealerCode,
	                                            String sellerCode, String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select cInvName");

		sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
		sqlBuilder.append(", TRUNCATE(SUM(totalSales)/1000000, 2) as totalAmount");

		sqlBuilder.append(" from sales_fact");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "provName", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cityName", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "countryName", countryName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "sellerCode", sellerCode, params, needWhere);

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
		sqlBuilder.append(" order by totalNum desc");

		return Db.find(sqlBuilder.toString(), params.toArray());

	}

	public List<Record> findProductListByCustomerId(String dealerCode, String customerId, String startDate,
	                                                String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select idate, customerTypeName, cInvName");

		sqlBuilder.append(", TRUNCATE(SUM(totalSmallAmount/cInvMNum), 2) as totalNum");
		sqlBuilder.append(", TRUNCATE(SUM(totalSales)/100, 2) as totalAmount");

		sqlBuilder.append(" from sales_fact");

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "dealerCode", dealerCode, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "customerId", customerId, params, needWhere);

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

		sqlBuilder.append(" group by idate, cInvCode");
		sqlBuilder.append(" order by idate desc, totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findByCusTypeId(String sellerId, String provName, String cityName, String countryName,
	                                    String startDate, String endDate) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT cc.id,cu.customer_name,cu.country_name,cu.city_name,cu.prov_name,o.customer_type_id,ct.`name`,IFNULL(SUM(o.total_amount),0) as totalAmount, ");
		sqlBuilder.append("IFNULL(SUM(t1.refundAmount),0) as refundAmount, TRUNCATE((IFNULL(SUM(o.total_amount),0) - IFNULL(SUM(t1.refundAmount),0))/10000,2) as realAmount ");
		sqlBuilder.append("FROM cc_sales_outstock o LEFT JOIN cc_seller_customer cc ON o.customer_id = cc.id ");
		sqlBuilder.append("LEFT JOIN cc_customer cu on cc.customer_id = cu.id ");
		sqlBuilder.append("LEFT JOIN cc_customer_type ct on ct.id = o.customer_type_id ");
		sqlBuilder.append("LEFT JOIN (SELECT cr.outstock_id,SUM(cr.total_reject_amount) as refundAmount FROM cc_sales_refund_instock cr ");
		sqlBuilder.append("WHERE cr.`status` in (?,?) GROUP BY cr.outstock_id) t1 ON t1.outstock_id = o.id ");
		params.add(Consts.SALES_REFUND_INSTOCK_PART_OUT);
		params.add(Consts.SALES_REFUND_INSTOCK_ALL_OUT);
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where o.status != ? and cc.customer_kind = ? ");
		} else {
			sqlBuilder.append(" and o.status != ? and cc.customer_kind = ? ");
		}
		params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		params.add(Consts.CUSTOMER_KIND_COMMON);
		if (startDate != null) {
			sqlBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" group by o.customer_type_id");
		sqlBuilder.append(" order by realAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findProductListByCusType(String sellerId, String provName, String cityName, String countryName,
	                                             String startDate, String endDate, String customerTypeName) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT ct.`name` , cs.custom_name, TRUNCATE((IFNULL(SUM(cc.product_amount),0) - IFNULL(SUM(t1.refundAmount),0))/10000,2) as totalAmount ");
		if (StrKit.notBlank(countryName)) {
			sqlBuilder.append(", cu.country_name");
		} else if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(", cu.city_name");
		} else {
			sqlBuilder.append(", cu.prov_name");
		}
		sqlBuilder.append(", TRUNCATE(IFNULL(SUM(cc.product_count/cp.convert_relate),0) - IFNULL(SUM(t1.refundCount),0),2) as productCount FROM cc_sales_outstock_detail cc ");
		sqlBuilder.append("LEFT JOIN cc_sales_outstock o ON o.id = cc.outstock_id ");
		sqlBuilder.append("LEFT JOIN cc_seller_customer csu ON csu.id = o.customer_id ");
		sqlBuilder.append("LEFT JOIN cc_customer cu ON cu.id = csu.customer_id ");
		sqlBuilder.append("LEFT JOIN cc_customer_type ct ON ct.id = o.customer_type_id ");
		sqlBuilder.append("LEFT JOIN cc_seller_product cs ON cs.id = cc.sell_product_id ");
		sqlBuilder.append("LEFT JOIN cc_product cp on cp.id = cs.product_id ");
		sqlBuilder.append("LEFT JOIN (SELECT cd.outstock_detail_id, SUM(cd.product_amount) as refundAmount, SUM(cd.product_count/pr.convert_relate) as refundCount ");
		sqlBuilder.append("FROM cc_sales_refund_instock_detail cd ");
		sqlBuilder.append("LEFT JOIN cc_sales_refund_instock cr on cr.id = cd.refund_instock_id ");
		sqlBuilder.append("LEFT JOIN cc_seller_product sp on cd.sell_product_id = sp.id ");
		sqlBuilder.append("LEFT JOIN cc_product pr on pr.id = sp.product_id ");
		sqlBuilder.append("WHERE cr.`status` in (?,?) ");
		sqlBuilder.append("GROUP BY cd.outstock_detail_id) t1 on t1.outstock_detail_id = cc.id ");
		params.add(Consts.SALES_REFUND_INSTOCK_PART_OUT);
		params.add(Consts.SALES_REFUND_INSTOCK_ALL_OUT);

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "ct.name", customerTypeName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where o.status != ? and csu.customer_kind = ? ");
		} else {
			sqlBuilder.append(" and o.status != ? and csu.customer_kind = ? ");
		}
		params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		params.add(Consts.CUSTOMER_KIND_COMMON);

		if (startDate != null) {
			sqlBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append("GROUP BY cc.sell_product_id ");
		sqlBuilder.append(" order by totalAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Record> findAreaListByCusTypeId(String sellerId, String provName, String cityName, String countryName,
	                                            String startDate, String endDate, String customerTypeName) {
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("SELECT cc.id,cu.customer_name,o.customer_type_id,ct.`name`,IFNULL(SUM(o.total_amount),0) as totalAmount, ");

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(" cu.country_name,");
		} else if (StrKit.notBlank(provName)) {
			sqlBuilder.append(" cu.city_name,");
		} else {
			sqlBuilder.append(" cu.prov_name,");
		}
		sqlBuilder.append("IFNULL(SUM(t1.refundAmount),0) as refundAmount, TRUNCATE((IFNULL(SUM(o.total_amount),0) - IFNULL(SUM(t1.refundAmount),0))/10000,2) as realAmount ");
		sqlBuilder.append("FROM cc_sales_outstock o LEFT JOIN cc_seller_customer cc ON o.customer_id = cc.id ");
		sqlBuilder.append("LEFT JOIN cc_customer cu on cc.customer_id = cu.id ");
		sqlBuilder.append("LEFT JOIN cc_customer_type ct on ct.id = o.customer_type_id ");
		sqlBuilder.append("LEFT JOIN (SELECT cr.outstock_id,SUM(cr.total_reject_amount) as refundAmount FROM cc_sales_refund_instock cr ");
		sqlBuilder.append("WHERE cr.`status` in (?,?) GROUP BY cr.outstock_id) t1 ON t1.outstock_id = o.id ");
		params.add(Consts.SALES_REFUND_INSTOCK_PART_OUT);
		params.add(Consts.SALES_REFUND_INSTOCK_ALL_OUT);

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "ct.name", customerTypeName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cc.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where o.status != ? and cc.customer_kind = ? ");
		} else {
			sqlBuilder.append(" and o.status != ? and cc.customer_kind = ? ");
		}
		params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		params.add(Consts.CUSTOMER_KIND_COMMON);

		if (startDate != null) {
			sqlBuilder.append(" and o.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and o.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" group by cu.prov_name");
		if (StrKit.notBlank(provName)) {
			sqlBuilder.append(", cu.city_name");
		}

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(", cu.country_name");
		}
		sqlBuilder.append(" order by realAmount desc");

		return Db.find(sqlBuilder.toString(), params.toArray());
	}

	public List<Map<String, Object>> findAreaArray(String sellerId, String provName, String cityName,
	                                               String countryName, String startDate, String endDate) {

		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sqlBuilder = new StringBuilder("select ");

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(" cu.country_name");
		} else if (StrKit.notBlank(provName)) {
			sqlBuilder.append(" cu.city_name");
		} else {
			sqlBuilder.append(" cu.prov_name");
		}

		sqlBuilder.append(" ,TRUNCATE((IFNULL(SUM(cc.product_amount),0) - IFNULL(SUM(t1.refundAmount),0))/10000,2) as totalAmount ");
		sqlBuilder.append(" FROM cc_sales_outstock_detail cc ");
		sqlBuilder.append(" JOIN cc_sales_outstock o ON o.id = cc.outstock_id ");
		sqlBuilder.append(" JOIN cc_seller_customer csu ON csu.id = o.customer_id ");
		sqlBuilder.append(" JOIN cc_customer cu ON cu.id = csu.customer_id ");
		sqlBuilder.append(" JOIN cc_customer_type ct ON ct.id = o.customer_type_id ");
		sqlBuilder.append(" JOIN cc_seller_product cs ON cs.id = cc.sell_product_id ");
		sqlBuilder.append(" JOIN cc_product cp on cp.id = cs.product_id ");
		sqlBuilder.append(" LEFT JOIN (SELECT cd.outstock_detail_id, SUM(cd.product_amount) as refundAmount, SUM(cd.product_count/pr.convert_relate) as refundCount ");
		sqlBuilder.append(" FROM cc_sales_refund_instock_detail cd ");
		sqlBuilder.append(" LEFT JOIN cc_sales_refund_instock cr on cr.id = cd.refund_instock_id ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp on cd.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product pr on pr.id = sp.product_id ");
		sqlBuilder.append(" GROUP BY cd.outstock_detail_id) t1 on t1.outstock_detail_id = cc.id ");

		boolean needWhere = true;

		needWhere = appendIfNotEmpty(sqlBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "cu.country_name", countryName, params, needWhere);

		if (needWhere) {
			sqlBuilder.append(" where o.status != ? and csu.customer_kind = ? ");
		} else {
			sqlBuilder.append(" and o.status != ? and csu.customer_kind = ? ");
		}
		params.add(Consts.SALES_OUT_STOCK_STATUS_DEFUALT);
		params.add(Consts.CUSTOMER_KIND_COMMON);

		if (startDate != null) {
			sqlBuilder.append(" and o.biz_date >= ?");
			params.add(startDate);
		}

		if (endDate != null) {
			sqlBuilder.append(" and o.biz_date <= ?");
			params.add(endDate);
		}

		sqlBuilder.append(" group by cu.prov_name");
		if (StrKit.notBlank(provName)) {
			sqlBuilder.append(", cu.city_name");
		}

		if (StrKit.notBlank(cityName)) {
			sqlBuilder.append(", cu.country_name");
		}
		sqlBuilder.append(" order by totalAmount desc");
		return Db.query(sqlBuilder.toString(), params.toArray());

	}

}
