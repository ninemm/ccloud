package org.ccloud.controller.front;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesFact;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;

@RouterMapping(url = "/sales")
public class SalesFactController extends BaseFrontController {

	public void index() {
		
	}
	
	public void area() {
		
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		
		String type = getPara("type");// 0: 昨天， 1: 最近1周， 2: 最近1月
		
		List<Map<String, Object>> result = SalesFactQuery.me().findAreaList(provName, cityName, countryName, null, null);
		
		renderJson(result);
		
	}
	
    public void customerType() {
        
        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        
        String type = getPara("type");// 0: 昨天， 1: 最近1周， 2: 最近1月
        
        List<Map<String, Object>> result = SalesFactQuery.me().findCustomerTypeList(provName, cityName, countryName, null, null);
        
        renderJson(result);
        
     }
    
    public void product() {
        
        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        
        String type = getPara("type");// 0: 昨天， 1: 最近1周， 2: 最近1月
        
        List<Map<String, Object>> result = SalesFactQuery.me().findProductList(provName, cityName, countryName, null, null);
        
        renderJson(result);
        
     }
		public void queryMapData() {

				String provName = getPara("provName", "").trim();
				String cityName = getPara("cityName", "").trim();
				String countryName = getPara("countryName", "").trim();
				String dateType = getPara("dateType", "").trim();

				String beginDate;
				String endDate;

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();

				calendar.setTime(new Date());

				calendar.add(Calendar.DATE, - 1);
				endDate = format.format(calendar.getTime());

				if(dateType.equals("0")) {

						beginDate = format.format(calendar.getTime());

				} else if(dateType.equals("1")) {

						calendar.add(Calendar.DATE, - 7);
						beginDate = format.format(calendar.getTime());

				} else {

						calendar.add(Calendar.DATE, - 30);
						beginDate = format.format(calendar.getTime());

				}
				List<SalesFact> salesFactList = SalesFactQuery.me().queryMapData(provName, cityName, countryName, beginDate, endDate);
				renderJson(salesFactList);
		}
}
