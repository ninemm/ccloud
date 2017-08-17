package org.ccloud.controller.front;

import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
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
	
	
}
