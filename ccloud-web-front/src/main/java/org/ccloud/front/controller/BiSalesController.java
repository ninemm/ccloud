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
package org.ccloud.front.controller;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.callback.AroundCustomerCallback;
import org.ccloud.model.query.BiSalesQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RouterMapping(url = "/biSales")
public class BiSalesController extends BaseFrontController {

	public void area() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 近一天， 1: 近一周， 2: 近一月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Map<String, Object>> result = BiSalesQuery.me().findAreaArray(sellerId, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	public void areaByCustomerType() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();

		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findAreaListByCusTypeId(sellerId, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	public void customerType() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findByCusTypeId(sellerId, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	//产品销售排行
	public void product() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findProductList(sellerId, provName, cityName, countryName,
				startDate, endDate);

		renderJson(result);

	}

	//产品客户分布
	public void customerTypeByProduct() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String cInvCode = getPara("cInvCode", "").trim();

		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findCustomerTypeListByProduct(sellerId, provName, cityName,
				countryName, startDate, endDate, cInvCode);

		renderJson(result);

	}

	//产品区域分布
	public void areaByProduct() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String cInvCode = getPara("cInvCode", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findAreaListByProduct(sellerId, provName, cityName, countryName,
				startDate, endDate, null);

		renderJson(result);

	}

	public void productByAreaList() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> countryResult = BiSalesQuery.me().findAreaListByCusTypeId(sellerId, provName, cityName,
				countryName, startDate, endDate, null);

		for (int i = 0; i < countryResult.size(); i++) {
			if (StrKit.notBlank(getPara("cityName", "").trim())) {
				countryName = countryResult.get(i).getStr("country_name");
			} else if (StrKit.notBlank(getPara("provName", "").trim())) {
				cityName = countryResult.get(i).getStr("city_name");
			} else {
				provName = countryResult.get(i).getStr("prov_name");
			}
			List<Record> result = BiSalesQuery.me().findProductListByCusType(sellerId, provName, cityName, countryName,
					startDate, endDate, null);
			rows.add(result);
		}

		setAttr("rows", rows);
		setAttr("provName", getPara("provName", "").trim());
		setAttr("cityName", getPara("cityName", "").trim());
		setAttr("dateType", dateType);
		render("bi_product_Area.html");

	}

	public void productByCustomerTypeList() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> typeResult = BiSalesQuery.me().findCustomerTypeList(sellerId, provName, cityName, countryName,
				startDate, endDate);

		for (Record rec : typeResult) {
			String customerTypeName = rec.get("name");
			List<Record> result = BiSalesQuery.me().findProductListByCusType(sellerId, provName, cityName,
					countryName, startDate, endDate, customerTypeName);
			rows.add(result);
		}
		setAttr("rows", rows);
		render("bi_product_customer_type.html");

	}

	public void productByCustomerType() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findProductListByCusType(sellerId, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	//经销商销售排行
	public void dealer() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = "";
		if (StrKit.notBlank(sellerId)) dataArea = DepartmentQuery.me().findBySellerId(sellerId).getDataArea();

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findsalesList(true, provName, cityName, countryName, sellerId,
				startDate, endDate, dataArea);

		renderJson(result);

	}

	//经销商详细
	public void dealerDetail() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dataArea = getPara("data_area", "").trim();

		if (StrKit.isBlank(sellerId)) {
			sellerId = getPara("dealerCode", "").trim();
		}

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();

		//经销商销售情况
		List<Record> sellerList = BiSalesQuery.me().findsalesList(false, provName, cityName, countryName, sellerId,
				startDate, endDate, dataArea);
		//经销商产品销售排行
		List<Record> productList = BiSalesQuery.me().findProductListByDealer(provName, cityName, countryName,
				sellerId, startDate, endDate);

		result.put("sellerList", sellerList);
		result.put("productList", productList);

		renderJson(result);

	}

	public void productBySeller() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		if (StrKit.notBlank(getPara("sellerId"))) sellerId = getPara("sellerId").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findProductListByDealer(provName, cityName, countryName, sellerId, startDate, endDate);

		renderJson(result);

	}

	public void queryMapData() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> salesFactList = BiSalesQuery.me().findAreaListByCusTypeId(sellerId, provName, cityName,
				countryName, startDate, endDate, null);

		renderJson(salesFactList);
	}

	@SuppressWarnings("unchecked")
	public void aroundCustomerSales() throws SQLException {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
//		String dateType = getPara("dateType", "0").trim();

//		String startDate = DateUtils.getDateByType(dateType);
//		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		AroundCustomerCallback callback = new AroundCustomerCallback();
		callback.setLongitude(longitude);
		callback.setLatitude(latitude);
		callback.setDist(dist);
		callback.setSellerId(sellerId);

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

	public void productByCustomerId() {

		String dealerCode = getSessionAttr("dealerCode");

		String customerId = getPara("customerId", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = BiSalesQuery.me().findProductListByCustomerId(dealerCode, customerId, startDate,
				endDate);

		renderJson(result);

	}

}
