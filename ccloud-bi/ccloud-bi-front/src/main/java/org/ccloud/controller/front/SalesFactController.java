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
		
		List<Map<String, Object>> result = SalesFactQuery.me().findAreaList(provName, cityName, countryName);
		
		renderJson(result);
		
	}
	
}
