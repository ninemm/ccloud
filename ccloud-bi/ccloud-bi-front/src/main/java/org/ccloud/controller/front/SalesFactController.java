package org.ccloud.controller.front;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesFact;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
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
		String dateType = getPara("dateType", "0").trim();// 0: 近一周， 1: 近一月， 2: 近一年

		String beginDate = getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<Map<String, Object>> result = SalesFactQuery.me().findAreaArray(provName, cityName, countryName, beginDate,
				endDate);

		renderJson(result);

    }

    public void areaByCustomerType() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        Integer customerType = getParaToInt("customerType");
        
        String dateType = getPara("dateType", "0").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

        List<Record> result = SalesFactQuery.me().findAreaListByCustomerType(provName, cityName,
                countryName, startDate, endDate, customerType);

        renderJson(result);

    }
    
    public void areaByProduct() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();

        String cInvCode = getPara("cInvCode", "").trim();
        String dateType = getPara("dateType", "0").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

        List<Record> result = SalesFactQuery.me().findAreaListByProduct(provName, cityName,
                countryName, startDate, endDate, cInvCode);

        renderJson(result);

    }

    public void customerType() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

        List<Record> result = SalesFactQuery.me().findCustomerTypeList(provName, cityName,
                countryName, startDate, endDate);

        renderJson(result);

    }


    public void customerTypeByProduct() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String cInvCode = getPara("cInvCode", "").trim();
        
        String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

        List<Record> result = SalesFactQuery.me().findCustomerTypeListByProduct(provName, cityName,
                countryName, startDate, endDate, cInvCode);

        renderJson(result);

    }


    public void product() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String dateType = getPara("dateType", "0").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);
        
        List<Record> result = SalesFactQuery.me().findProductList(provName, cityName, countryName,
                startDate, endDate);

        renderJson(result);

    }

    public void productByAreaList() {

        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String dateType = getPara("dateType", "0").trim();// 0: 昨天， 1: 最近1周， 2: 最近1月

        String startDate = getDateByType(dateType);
        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

        List<List<Record>> rows = new ArrayList<List<Record>>();
        List<Record> countryResult = SalesFactQuery.me().findAreaList(provName, cityName,
                countryName, startDate, endDate);
        
        for (int i = 0; i < countryResult.size(); i++) {
            if (StrKit.notBlank(getPara("cityName", "").trim())) {
                countryName = countryResult.get(i).getStr("countryName");
            } else if (StrKit.notBlank(getPara("provName", "").trim())) {
                cityName = countryResult.get(i).getStr("cityName");
            } else {
                provName = countryResult.get(i).getStr("provName");
            }
            List<Record> result = SalesFactQuery.me().findProductListByArea(provName, cityName,
                    countryName, startDate, endDate);
            rows.add(result);
        }

        setAttr("rows", rows);
        setAttr("provName", getPara("provName", "").trim());
        setAttr("cityName", getPara("cityName", "").trim());
        setAttr("dateType", dateType);
        render("productByArea.html");

    }


	public void productByCustomerTypeList() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String beginDate = getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

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
	
	   public void productByCustomerType() {

	        String provName = getPara("provName", "").trim();
	        String cityName = getPara("cityName", "").trim();
	        String countryName = getPara("countryName", "").trim();
	        String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

	        String beginDate = getDateByType(dateType);
	        String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

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

	public void queryMapData() {

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim();

		String beginDate = getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		List<SalesFact> salesFactList = SalesFactQuery.me().queryMapData(provName, cityName, countryName, beginDate,
				endDate);
		renderJson(salesFactList);
	}

	private String getDateByType(String dateType) {
		
		DateTime dateTime = DateTime.now();
		
		if (dateType.equals("0")) {// 最近一天
			return dateTime.plusDays(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER);
		} else if (dateType.equals("1")) {// 近一周
			return dateTime.plusWeeks(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER);
		} else if (dateType.equals("2")) {// 近一月
			return dateTime.plusMonths(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER);
		} else {// 近一年
			return dateTime.plusYears(-1).toString(DateUtils.DEFAULT_NORMAL_FORMATTER);
		}
	}
}
