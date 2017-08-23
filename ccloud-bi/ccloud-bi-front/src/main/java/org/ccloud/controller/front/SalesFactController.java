package org.ccloud.controller.front;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesFact;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;
import org.joda.time.DateTime;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/sales")
public class SalesFactController extends BaseFrontController {

	public void index() {

	}

	public void area() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "").trim();// 0: 近一周， 1: 近一月， 2: 近一年

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<Map<String, Object>> result = SalesFactQuery.me().findAreaList(provName, cityName, countryName, beginDate,
				endDate);

		renderJson(result);

	}

	public void productByArea() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> countryResult = SalesFactQuery.me().findArea(provName, cityName, countryName, beginDate, endDate);
		for (int i = 0; i < countryResult.size(); i++) {
			if (StrKit.notBlank(getPara("cityName", "").trim())) {
				countryName = countryResult.get(i).getStr("countryName");
			} else if (StrKit.notBlank(getPara("provName", "").trim())) {
				cityName = countryResult.get(i).getStr("cityName");
			} else {
				provName = countryResult.get(i).getStr("provName");
			}
			List<Record> result = SalesFactQuery.me().findProductListByArea(provName, cityName, countryName, beginDate,
					endDate);
			rows.add(result);
		}

		setAttr("rows", rows);
		setAttr("provName", getPara("provName", "").trim());
		setAttr("cityName", getPara("cityName", "").trim());
		setAttr("dateType", dateType);
		render("productByArea.html");

	}

	public void customerType() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<Record> result = SalesFactQuery.me().findCustomerTypeList(provName, cityName, countryName, beginDate,
				endDate);

		renderJson(result);

	}

	public void productByCustomerType() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<List<Record>> rows = new ArrayList<List<Record>>();
		List<Record> typeResult = SalesFactQuery.me().findCustomerTypeList(provName, cityName, countryName, beginDate,
				endDate);

		for (Record rec : typeResult) {
			int customerType = rec.get("customerType");
			List<Record> result = SalesFactQuery.me().findProductListByCustomerType(provName, cityName, countryName,
					beginDate, endDate, customerType);
			rows.add(result);
		}
		setAttr("rows", rows);
		render("productByCustomerType.html");

	}

	public void product() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		String dateType = getPara("dateType", "").trim();
		;// 0: 昨天， 1: 最近1周， 2: 最近1月

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<Record> result = SalesFactQuery.me().findProductList(provName, cityName, countryName, beginDate, endDate);

		renderJson(result);

	}

	public void queryMapData() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "").trim();

		String beginDate = getDateByType(dateType);
		String endDate = getDate(-1);

		List<SalesFact> salesFactList = SalesFactQuery.me().queryMapData(provName, cityName, countryName, beginDate,
				endDate);
		renderJson(salesFactList);
	}

	private String getDate(int days) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(DateTime.now().plusDays(days).toDate());
	}

	private String getDateByType(String dateType) {
		if (dateType.equals("0")) {
			return getDate(-1);
		}

		if (dateType.equals("1")) {
			return getDate(-7);
		}

		if (dateType.equals("2")) {
			return getDate(-30);
		}

		return getDate(0);
	}
}
