package org.ccloud.controller.front;

import java.util.List;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.StockFactQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/stock")
public class StockFactController extends BaseFrontController {

	public void index() {
		
	}
	
	public void area() {
		
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		
		List<Record> result = StockFactQuery.me().findAreaList(provName, cityName, countryName);
		
		renderJson(result);
		
	}
    
    public void date() {
        
        String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String cInvCode = getPara("cInvCode", "").trim();
        
        List<Record> result = StockFactQuery.me().findDateList(provName, cityName, countryName, cInvCode);
        
        renderJson(result);
        
     }
	
	
}
