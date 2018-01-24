package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.Plans;
import org.ccloud.model.User;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.route.RouterMapping;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

@RouterMapping(url = "/plans")
public class PlansController extends BaseFrontController { 
	public static final String WEEK_PLAN = "w";//周计划
	public static final String MONTH_PLAN = "m";//周计划
	public static final String YEAR_PLAN = "y";//周计划
	public void index() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", "");
		List<Map<String, Object>> productItems = new ArrayList<>();
		for(Record record : productRecords) {
			
			Map<String, Object> item = new HashMap<>();
			
			String sellerProductId = record.get("sell_product_id");
			item.put("title", record.getStr("custom_name"));
			item.put("value", sellerProductId);

			productItems.add(item);
		}
		setAttr("productItems",JSON.toJSON(productItems));
		render("plan.html");
	}
	public void getProduct() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", "");
		Map<String, Object> map = new HashMap<>();
		map.put("productList", productRecords);
		renderJson(map);
	}
	@Before(Tx.class)
	public void makePlan() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String type = getPara("typeId");
		String startDate = getPara("start-date")+" 0:00:00";
		String endDate = getPara("end-date")+" 23:59:59";
		String[] productIds = getParaValues("productId");
		String[] productNum = getParaValues("productNum");
		boolean result = false;
		for(int i=0;i<productIds.length;i++) {
			Plans plans = new Plans();
			plans.setId(StrKit.getRandomUUID());
			plans.setSellerId(sellerId);
			plans.setUserId(user.getId());
			plans.setType(type);
			plans.setSellerProductId(productIds[i]);
			plans.setPlanNum(new BigDecimal(productNum[i]));
			try {
				plans.setStartDate(( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(startDate));
				plans.setEndDate(( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(endDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plans.setDeptId(user.getDepartmentId());
			plans.setDataArea(user.getDataArea());
			plans.setCreateDate(new Date());
			result = plans.save();
			
		}
		renderAjaxResultForSuccess("申请成功");
	}
}
