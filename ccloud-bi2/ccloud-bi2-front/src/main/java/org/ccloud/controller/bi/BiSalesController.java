/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.controller.bi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.callback.AroundCustomerBiPositionCallback;
import org.ccloud.model.callback.AroundCustomerBiSalesCallback;
import org.ccloud.model.callback.AroundCustomerBiUndevelopedCallback;
import org.ccloud.model.callback.AroundCustomerBiVisitCallback;
import org.ccloud.model.query.Bi2SalesQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RouterMapping(url = "/biSales")
public class BiSalesController extends BaseFrontController {

	public void area() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 近一天， 1: 近一周， 2: 近一月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Map<String, Object>> result = Bi2SalesQuery.me().findAreaArray(dataArea, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	public void areaByCustomerType() {
		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();

		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	public void customerType() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	//产品销售排行
	public void product() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findProductList(dataArea, provName, cityName, countryName,
				startDate, endDate);

		renderJson(result);

	}

	//产品客户分布
	public void customerTypeByProduct() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String cInvCode = getPara("cInvCode", "").trim();

		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findCustomerTypeListByProduct(dataArea, provName, cityName,
				countryName, startDate, endDate, cInvCode);

		renderJson(result);

	}

	//产品区域分布
	public void areaByProduct() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String cInvCode = getPara("cInvCode", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findAreaListByProduct(dataArea, provName, cityName, countryName,
				startDate, endDate, cInvCode);

		renderJson(result);

	}

	public void productByAreaList() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> countryResult = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, null);

		for (int i = 0; i < countryResult.size(); i++) {
			if (StrKit.notBlank(getPara("cityName", "").trim())) {
				countryName = countryResult.get(i).getStr("country_name");
			} else if (StrKit.notBlank(getPara("provName", "").trim())) {
				cityName = countryResult.get(i).getStr("city_name");
			} else {
				provName = countryResult.get(i).getStr("prov_name");
			}
			List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName, countryName,
					startDate, endDate, null);
			rows.add(result);
		}

		setAttr("rows", rows);
		setAttr("provName", getPara("provName", "").trim());
		setAttr("cityName", getPara("cityName", "").trim());
		setAttr("dateType", dateType);
		render("bi_product_area.html");

	}

	public void productByCustomerTypeList() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> typeResult = Bi2SalesQuery.me().findCustomerTypeList(dataArea, provName, cityName, countryName,
				startDate, endDate);

		for (Record rec : typeResult) {
			String customerTypeName = rec.get("name");
			List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName,
					countryName, startDate, endDate, customerTypeName);
			rows.add(result);
		}
		setAttr("rows", rows);
		render("bi_product_customer_type.html");

	}

	public void productByCustomerType() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	//经销商销售排行
	public void dealer() {

		String dataArea[] = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findsalesList(true, provName, cityName, countryName, dataArea
				, startDate, endDate);

		renderJson(result);

	}

	//经销商详细
	public void dealerDetail() {

		String dataArea = getPara("dataArea", "").trim();
		String dataAreaArray[] = new String[]{dataArea};

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();

		//直营商销售情况
		List<Record> sellerList = Bi2SalesQuery.me().findsalesList(false, provName, cityName, countryName, dataAreaArray
				, startDate, endDate);
		//经销商产品销售排行
		List<Record> productList = Bi2SalesQuery.me().findProductListByDealer(true, provName, cityName, countryName,
				dataArea, startDate, endDate);

		result.put("sellerList", sellerList);
		result.put("productList", productList);

		renderJson(result);

	}

	public void productBySeller() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String dataArea = getPara("sellerId").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findProductListByDealer(false, provName, cityName, countryName, dataArea, startDate, endDate);

		renderJson(result);

	}

	public void queryMapData() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> salesFactList = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, null);

		renderJson(salesFactList);
	}

	@SuppressWarnings("unchecked")
	public void aroundCustomer() throws SQLException {

		String[] dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
//		String dataAreaIn = "'" + dealerDataArea[0] + "'";
//		for (int i = 1 ;i<dealerDataArea.length;i++){
//			dataAreaIn += "," + "'" + dealerDataArea[i] + "'";
//		}

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		AroundCustomerBiSalesCallback salesCallback = new AroundCustomerBiSalesCallback();
		salesCallback.setLongitude(longitude);
		salesCallback.setLatitude(latitude);
		salesCallback.setDist(dist);
		salesCallback.setStartDate(startDate);
		salesCallback.setEndDate(endDate);
		if(dealerDataArea.length == 1) {
			salesCallback.setDataArea(dealerDataArea[0]);
		}
		salesCallback.setCustomerKind(Consts.CUSTOMER_KIND_COMMON);

		AroundCustomerBiVisitCallback visitCallback = new AroundCustomerBiVisitCallback();
		visitCallback.setLongitude(longitude);
		visitCallback.setLatitude(latitude);
		visitCallback.setDist(dist);
		visitCallback.setStartDate(startDate);
		visitCallback.setEndDate(endDate);
		if(dealerDataArea.length == 1) {
			visitCallback.setDataArea(dealerDataArea[0]);
		}
		visitCallback.setCustomerKind(Consts.CUSTOMER_KIND_COMMON);

		Connection conn = null;
		Map<String, Object> result = Maps.newHashMap();

		List<Map<String, Object>> allList = Lists.newArrayList();
		List<Map<String, Object>> salesList = null;
		List<Map<String, Object>> visitList = null;

		try {
			conn = DbKit.getConfig().getConnection();
			salesList = (List<Map<String, Object>>) salesCallback.call(conn);
			visitList = (List<Map<String, Object>>) visitCallback.call(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		for (int i = 0; i < salesList.size(); i++) {
			String customerId = salesList.get(i).get("customerId").toString();
			for (int j = 0; j < visitList.size(); j++) {
				if (customerId.equals(visitList.get(j).get("customerId"))) {
					Map<String, Object> allMap = salesList.get(i);
					allMap.put("totalNum", visitList.get(j).get("totalNum"));
					allList.add(allMap);
					salesList.remove(i);
					visitList.remove(j);
				}
			}

		}

		result.put("allList", allList);
		result.put("salesList", salesList);
		result.put("visitList", visitList);
		renderJson(result);
	}

	public void productByCustomerId() {

		String customerId = getPara("customerId", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findProductList(customerId, startDate,
				endDate);

		renderJson(result);

	}

	public void visitByCustomerId() {

		String customerId = getPara("customerId", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findVistList(customerId, startDate,
				endDate);

		renderJson(result);

	}

	@SuppressWarnings("unchecked")
	public void aroundCustomerPosition() throws SQLException {

		String[] dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		AroundCustomerBiPositionCallback callback = new AroundCustomerBiPositionCallback();
		callback.setLongitude(longitude);
		callback.setLatitude(latitude);
		callback.setDist(dist);
		callback.setStartDate(startDate);
		callback.setEndDate(endDate);
		if(dealerDataArea.length == 1) {
			callback.setDataArea(dealerDataArea[0] + "%");
		}
		callback.setCustomerKind(Consts.CUSTOMER_KIND_COMMON);

		Connection conn = null;
		List<Map<String, Object>> result = null;

		try {
			conn = DbKit.getConfig().getConnection();
			result = (List<Map<String, Object>>) callback.call(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		renderJson(result);
	}

	@SuppressWarnings("unchecked")
	public void aroundCustomerUndeveloped() throws SQLException {

		String[] dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		BigDecimal longitude = new BigDecimal(getPara("longitude"));
		BigDecimal latitude = new BigDecimal(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));

		AroundCustomerBiUndevelopedCallback callback = new AroundCustomerBiUndevelopedCallback();
		callback.setLon(longitude);
		callback.setLat(latitude);
		callback.setDist(dist);
		callback.setSearchKey("");
		callback.setSellerId(dealerDataArea[0] + "%");

		Connection conn = null;
		List<Map<String, Object>> result = null;

		try {
			conn = DbKit.getConfig().getConnection();
			result = (List<Map<String, Object>>) callback.call(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		renderJson(result);
	}

	public void salesHotMap() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 近一天， 1: 近一周， 2: 近一月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findAreaSales(dataArea, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	public void customerHotMap() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 近一天， 1: 近一周， 2: 近一月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		List<Record> result = Bi2SalesQuery.me().findAreaCustomer(dataArea, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

}
