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

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Map<String, Object>> result = Bi2SalesQuery.me().findAreaArray(dataArea, provName, cityName,
				countryName, startDate, endDate, brandId);

		renderJson(result);

	}

	public void areaByCustomerType() {
		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, customerTypeName, brandId);

		renderJson(result);

	}

	public void customerType() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, brandId);

		renderJson(result);

	}

	//产品销售排行
	public void product() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findProductList(dataArea, provName, cityName, countryName,
				startDate, endDate, brandId);

		renderJson(result);

	}

	//产品客户分布
	public void customerTypeByProduct() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String cInvCode = getPara("cInvCode", "").trim();

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findCustomerTypeListByProduct(dataArea, provName, cityName,
				countryName, startDate, endDate, cInvCode, brandId);

		renderJson(result);

	}

	//产品区域分布
	public void areaByProduct() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String cInvCode = getPara("cInvCode", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findAreaListByProduct(dataArea, provName, cityName, countryName,
				startDate, endDate, cInvCode, brandId);

		renderJson(result);

	}

/*	public void productByAreaList() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> countryResult = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, null, brandId);

		for (int i = 0; i < countryResult.size(); i++) {
			if (StrKit.notBlank(getPara("cityName", "").trim())) {
				countryName = countryResult.get(i).getStr("country_name");
			} else if (StrKit.notBlank(getPara("provName", "").trim())) {
				cityName = countryResult.get(i).getStr("city_name");
			} else {
				provName = countryResult.get(i).getStr("prov_name");
			}
			List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName, countryName,
					startDate, endDate, null, brandId);
			rows.add(result);
		}

		setAttr("rows", rows);
		setAttr("provName", getPara("provName", "").trim());
		setAttr("cityName", getPara("cityName", "").trim());
		setAttr("dateType", dateType);
		render("bi_product_area.html");

	}*/

/*	public void productByCustomerTypeList() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> typeResult = Bi2SalesQuery.me().findCustomerTypeList(dataArea, provName, cityName, countryName,
				startDate, endDate);

		for (Record rec : typeResult) {
			String customerTypeName = rec.get("name");
			List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName,
					countryName, startDate, endDate, customerTypeName, brandId);
			rows.add(result);
		}
		setAttr("rows", rows);
		render("bi_product_customer_type.html");

	}*/

	public void productByCustomerType() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findProductListByCusType(dataArea, provName, cityName,
				countryName, startDate, endDate, customerTypeName, brandId);

		renderJson(result);

	}

	//经销商销售排行
	public void dealer() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findsalesList(true, provName, cityName, countryName, dataArea
				, startDate, endDate, brandId);

		renderJson(result);

	}

	//经销商详细
	public void dealerDetail() {

		String dataArea = getPara("dataArea", "").trim();
		String dataAreaArray[] = new String[]{dataArea};
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Map<String, Object> result = new HashMap<String, Object>();

		//经销商订单数
		List<Record> orderNumList = Bi2SalesQuery.me().findOrderCountList(dataAreaArray, provName, cityName, countryName,
				startDate, endDate, brandId);

		//经销商产品销售排行
		List<Record> productList = Bi2SalesQuery.me().findProductListByDealer(true, provName, cityName, countryName,
				dataArea, startDate, endDate, brandId);

		result.put("orderNumList", orderNumList);
		result.put("productList", productList);

		renderJson(result);

	}

	public void productBySeller() {

		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dataArea = getPara("sellerId").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findProductListByDealer(false, provName, cityName, countryName, dataArea, startDate, endDate, brandId);

		renderJson(result);

	}

	public void queryMapData() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> salesFactList = Bi2SalesQuery.me().findAreaListByCusTypeId(dataArea, provName, cityName,
				countryName, startDate, endDate, null, brandId);

		renderJson(salesFactList);
	}

	@SuppressWarnings("unchecked")
	public void aroundCustomer() throws SQLException {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String dataAreaIn = dataArea[0];
		for (int i = 1; i < dataArea.length; i++) {
			dataAreaIn += "," + dataArea[i];
		}
		String[] brandId = this.getBrandId(getPara("brandId"));
		String brandIdIn = brandId[0];
		for (int i = 1; i < brandId.length; i++) {
			brandIdIn += "," + brandId[i];
		}

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		AroundCustomerBiSalesCallback salesCallback = new AroundCustomerBiSalesCallback();
		salesCallback.setLongitude(longitude);
		salesCallback.setLatitude(latitude);
		salesCallback.setDist(dist);
		salesCallback.setStartDate(startDate);
		salesCallback.setEndDate(endDate);
		salesCallback.setDataArea(dataAreaIn);
		salesCallback.setCustomerKind(Consts.CUSTOMER_KIND_COMMON);
		salesCallback.setBrandId(brandIdIn);

		AroundCustomerBiVisitCallback visitCallback = new AroundCustomerBiVisitCallback();
		visitCallback.setLongitude(longitude);
		visitCallback.setLatitude(latitude);
		visitCallback.setDist(dist);
		visitCallback.setStartDate(startDate);
		visitCallback.setEndDate(endDate);
		visitCallback.setDataArea(dataAreaIn);
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
		String[] brandId = this.getBrandId(getPara("brandId"));

		String customerId = getPara("customerId", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findProductList(customerId, startDate,
				endDate, brandId);

		renderJson(result);

	}

	public void visitByCustomerId() {

		String customerId = getPara("customerId", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findVistList(customerId, startDate,
				endDate);

		renderJson(result);

	}

	@SuppressWarnings("unchecked")
	public void aroundCustomerPosition() throws SQLException {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String dataAreaIn = dataArea[0];
		for (int i = 1; i < dataArea.length; i++) {
			dataAreaIn += "," + dataArea[i];
		}

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		AroundCustomerBiPositionCallback callback = new AroundCustomerBiPositionCallback();
		callback.setLongitude(longitude);
		callback.setLatitude(latitude);
		callback.setDist(dist);
		callback.setStartDate(startDate);
		callback.setEndDate(endDate);
		callback.setDataArea(dataAreaIn);
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

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String dataAreaIn = dataArea[0];
		for (int i = 1; i < dataArea.length; i++) {
			dataAreaIn += "," + dataArea[i];
		}

		BigDecimal longitude = new BigDecimal(getPara("longitude"));
		BigDecimal latitude = new BigDecimal(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));

		AroundCustomerBiUndevelopedCallback callback = new AroundCustomerBiUndevelopedCallback();
		callback.setLon(longitude);
		callback.setLat(latitude);
		callback.setDist(dist);
		callback.setSearchKey("");
		callback.setSellerId(dataAreaIn);

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

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findAreaSales(dataArea, provName, cityName,
				countryName, startDate, endDate, brandId);

		renderJson(result);

	}

	public void customerHotMap() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		List<Record> result = Bi2SalesQuery.me().findAreaCustomer(dataArea, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	private String[] getDataArea(String dataArea) {
		if (StrKit.notBlank(dataArea)) {
			return new String[]{dataArea};
		}
		return getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
	}

	private String[] getBrandId(String brandId) {
		if (StrKit.notBlank(brandId)) {
			return new String[]{brandId};
		}
		return getSessionAttr(Consts.SESSION_BRAND_ID_ARRAY);
	}

}
