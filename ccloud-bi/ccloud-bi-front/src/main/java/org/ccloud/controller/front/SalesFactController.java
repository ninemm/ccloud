package org.ccloud.controller.front;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesFact;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;
import org.joda.time.DateTime;

import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/sales")
public class SalesFactController extends BaseFrontController {

	public void index() {
		
	}
	
	public void area() {
		
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		
		String dateType = getPara("dateType", "").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月
		
        String beginDate = getDateByType(dateType);
        String endDate = getDate(-1) ;
		
		List<Map<String, Object>> result = SalesFactQuery.me().findAreaList(provName, cityName, countryName, beginDate, endDate);
		
		renderJson(result);
		
	}
	
    public void customerType() {
        
        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        
        String dateType = getPara("dateType", "").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月
        
        String beginDate = getDateByType(dateType);
        String endDate = getDate(-1) ;
        
        List<Record> result = SalesFactQuery.me().findCustomerTypeList(provName, cityName, countryName, beginDate, endDate);
        
        renderJson(result);
        
     }
    
    public void product() {
        
        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        
        String dateType = getPara("dateType", "").trim();;// 0: 昨天， 1: 最近1周， 2: 最近1月
        
        String beginDate = getDateByType(dateType);
        String endDate = getDate(-1) ;
        
        List<Record> result = SalesFactQuery.me().findProductList(provName, cityName, countryName, beginDate, endDate);
        
        renderJson(result);
        
     }
		public void queryMapData() {

				String provName = getPara("provName", "").trim();
				String cityName = getPara("cityName", "").trim();
				String countryName = getPara("countryName", "").trim();
				String dateType = getPara("dateType", "").trim();

				String beginDate = getDateByType(dateType);
				String endDate = getDate(-1) ;

				List<SalesFact> salesFactList = SalesFactQuery.me().queryMapData(provName, cityName, countryName, beginDate, endDate);
				renderJson(salesFactList);
		}

		private String getDate(int days) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(DateTime.now().plusDays(days).toDate());
		}
		
		private String getDateByType(String dateType) {
            if(dateType.equals("0")) {
                return getDate(-1);
            }

            if(dateType.equals("1")) {
                return getDate(-7);
            } 
            
            if(dateType.equals("2")) {
                return getDate(-30);
            }
		    
		    return getDate(0);
		}
}
