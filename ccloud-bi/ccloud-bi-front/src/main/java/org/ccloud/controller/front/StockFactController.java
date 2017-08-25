package org.ccloud.controller.front;

import java.util.List;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.StockFactQuery;
import org.ccloud.route.RouterMapping;

import com.google.common.collect.Lists;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/stock")
public class StockFactController extends BaseFrontController {

    public void index() {

    	String provName = getPara("provName", "").trim();
        String cityName = getPara("cityName", "").trim();
        String countryName = getPara("countryName", "").trim();
        String cInvCode = getPara("cInvCode", "").trim();
        
        List<Record> stock125List = Lists.newArrayList();
        List<Record> stock258List = Lists.newArrayList();
        List<Record> stock512List = Lists.newArrayList();
        
        if (StrKit.notBlank(cInvCode)) {
        	String[] codes = cInvCode.split(",");
        		
    		List<Record> result = StockFactQuery.me().findListByInvCode(provName, cityName, countryName, cInvCode);
    		for (Record record : result) {
    			
    			if (codes[0].equals(record.getStr("cInvCode"))) {
    				stock125List.add(record);
    			} else if (codes[1].equals(record.getStr("cInvCode"))) {
    				stock258List.add(record);
    			} else {
    				stock512List.add(record);
    			}
    			
    		}
        		
        	Kv data = Kv.create().set("_stock125", stock125List).set("_stock258", stock258List).set("_stock512", stock512List);
        	renderJson(data);
        	return ;
        }
        
        renderJson();
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
