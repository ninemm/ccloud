package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Page;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.Plans;
import org.ccloud.model.User;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

@RouterMapping(url = "/plans")
public class PlansController extends BaseFrontController {
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
				e.printStackTrace();
			}
			plans.setDeptId(user.getDepartmentId());
			plans.setDataArea(user.getDataArea());
			plans.setCreateDate(new Date());
			plans.save();
			
		}
		renderAjaxResultForSuccess("新增成功");
	}

	public void myPlans() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Map<String, Object>> sellerProducts = new ArrayList<>();
		sellerProducts.add(all);
		
		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}
		
		List<Plans> plans = PlansQuery.me().findbyDateArea(selectDataArea);
		for(Plans plan : plans) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", plan.get("custom_name"));
			item.put("value", plan.getSellerProductId());
			sellerProducts.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("sellerProducts", JSON.toJSON(sellerProducts));
		render("plan_list.html");
	}

	public void planList() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String userId = getPara("userId");
		String type = getPara("type");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String showType = getPara("show");
		String sellerProductId = getPara("sellerProductId");
		Page<Record> planList = PlansQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, userId, type, startDate, endDate, sellerId, selectDataArea,showType,sellerProductId);

		Map<String, Object> map = new HashMap<>();
		map.put("planList", planList.getList());

		renderJson(map);
	}
	
	public void getPlans() {
		String userId = getPara("userId");
		String typeName = getPara("typeName");
		String startDate = getPara("startDate").substring(5);
		String endDate = getPara("endDate").substring(5);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Plans> list = PlansQuery.me().findbyUserNameAndTypeNameAndStartDateAndEndDate(userId,typeName,startDate,endDate,sellerId);
		renderJson(list);
	}
	
	public void getSellerProductPlans() {
		String sellerProductId = getPara("sellerProductId");
		String typeName = getPara("typeName");
		String startDate = getPara("startDate").substring(5);
		String endDate = getPara("endDate").substring(5);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Plans> list = PlansQuery.me().findbySTSE(sellerProductId,typeName,startDate,endDate,sellerId);
		renderJson(list);
	}

	public void checkProduct() {
		String sellerProductId = getPara("sellerProductId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Plans plans = PlansQuery.me().findbySSEU(sellerProductId,startDate,endDate,user.getId());
		if(plans!=null) {
			renderJson(false);
		}else {
			renderJson(true);
		}
	}
	
	public void mPlans() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Map<String, Object>> sellerProducts = new ArrayList<>();
		sellerProducts.add(all);
		
		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}
		
		List<Plans> plans = PlansQuery.me().findbyDateArea(selectDataArea);
		for(Plans plan : plans) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", plan.get("custom_name"));
			item.put("value", plan.getSellerProductId());
			sellerProducts.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("sellerProducts", JSON.toJSON(sellerProducts));
		render("my_plan_list.html");
	}
}
