package org.ccloud.controller.front;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.callback.AroundCustomerSalesCallback;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.joda.time.DateTime;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/sales")
public class SalesFactController extends BaseFrontController {

	public void index() {

	}

	public void area() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 近一天， 1: 近一周， 2: 近一月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Map<String, Object>> result = SalesFactQuery.me().findAreaArray(dealerCode, provName, cityName,
				countryName, startDate, endDate);

		renderJson(result);

	}

	public void areaByCustomerType() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();

		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findAreaListByCustomerType(dealerCode, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	public void areaByProduct() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String cInvCode = getPara("cInvCode", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findAreaListByProduct(dealerCode, provName, cityName, countryName,
				startDate, endDate, cInvCode);

		renderJson(result);

	}

	public void customerType() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findCustomerTypeList(dealerCode, provName, cityName, countryName,
				startDate, endDate);

		renderJson(result);

	}

	public void customerTypeByProduct() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String cInvCode = getPara("cInvCode", "").trim();

		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findCustomerTypeListByProduct(dealerCode, provName, cityName,
				countryName, startDate, endDate, cInvCode);

		renderJson(result);

	}

	public void product() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findProductList(dealerCode, provName, cityName, countryName,
				startDate, endDate);

		renderJson(result);

	}

	public void productByAreaList() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> countryResult = SalesFactQuery.me().findAreaList(dealerCode, provName, cityName, countryName,
				startDate, endDate);

		for (int i = 0; i < countryResult.size(); i++) {
			if (StrKit.notBlank(getPara("cityName", "").trim())) {
				countryName = countryResult.get(i).getStr("countryName");
			} else if (StrKit.notBlank(getPara("provName", "").trim())) {
				cityName = countryResult.get(i).getStr("cityName");
			} else {
				provName = countryResult.get(i).getStr("provName");
			}
			List<Record> result = SalesFactQuery.me().findProductListByArea(dealerCode, provName, cityName, countryName,
					startDate, endDate);
			rows.add(result);
		}

		setAttr("rows", rows);
		setAttr("provName", getPara("provName", "").trim());
		setAttr("cityName", getPara("cityName", "").trim());
		setAttr("dateType", dateType);
		render("productByArea.html");

	}

	public void productByCustomerTypeList() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> typeResult = SalesFactQuery.me().findCustomerTypeList(dealerCode, provName, cityName, countryName,
				startDate, endDate);

		for (Record rec : typeResult) {
			String customerTypeName = rec.get("customerTypeName");
			List<Record> result = SalesFactQuery.me().findProductListByCustomerType(dealerCode, provName, cityName,
					countryName, startDate, endDate, customerTypeName);
			rows.add(result);
		}
		setAttr("rows", rows);
		render("productByCustomerType.html");

	}

	public void productByCustomerType() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String customerTypeName = getPara("customerTypeName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findProductListByCustomerType(dealerCode, provName, cityName,
				countryName, startDate, endDate, customerTypeName);

		renderJson(result);

	}

	public void dealer() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findsalesList(true, provName, cityName, countryName, dealerCode,
				startDate, endDate);

		renderJson(result);

	}

	public void dealerDetail() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		if (StrKit.isBlank(dealerCode)) {
			dealerCode = getPara("dealerCode", "").trim();
		}

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();

		List<Record> sellerList = SalesFactQuery.me().findsalesList(false, provName, cityName, countryName, dealerCode,
				startDate, endDate);

		List<Record> productList = SalesFactQuery.me().findProductListByDealer(provName, cityName, countryName,
				dealerCode, startDate, endDate);

		result.put("sellerList", sellerList);
		result.put("productList", productList);

		renderJson(result);

	}

	public void productBySeller() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String sellerCode = getPara("sellerCode", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> result = SalesFactQuery.me().findProductListBySeller(provName, cityName, countryName, dealerCode,
				sellerCode, startDate, endDate);

		renderJson(result);

	}

	public void queryMapData() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Record> salesFactList = SalesFactQuery.me().findAreaList(dealerCode, provName, cityName, countryName,
				startDate, endDate);
		renderJson(salesFactList);
	}

	@SuppressWarnings("unchecked")
	public void aroundCustomerSales() throws SQLException {

		String dealerCode = getSessionAttr("dealerCode");

		double longitude = Double.parseDouble(getPara("longitude"));
		double latitude = Double.parseDouble(getPara("latitude"));
		double dist = Double.parseDouble(getPara("dist"));
		String dateType = getPara("dateType", "0").trim();

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		AroundCustomerSalesCallback callback = new AroundCustomerSalesCallback();
		callback.setLongitude(longitude);
		callback.setLatitude(latitude);
		callback.setDist(dist);
		callback.setStartDate(startDate);
		callback.setEndDate(endDate);
		callback.setDealerCode(dealerCode);

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

		List<Record> result = SalesFactQuery.me().findProductListByCustomerId(dealerCode, customerId, startDate,
				endDate);

		renderJson(result);

	}

}
