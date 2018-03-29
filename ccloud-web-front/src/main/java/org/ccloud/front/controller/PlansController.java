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
import org.ccloud.model.PlansDetail;
import org.ccloud.model.SellerProduct;
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
		String datetimePicker = getPara("datetime-picker");
		String startDate = getPara("start-date");
//		try {
//			if(sdf.parse(startDate).before(sdf.parse(startDateM)) || sdf.parse(startDate).after(sdf.parse(endDateM))) {
//				renderAjaxResultForError("计划的开始时间不在计划月内");
//				return;
//			}
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		String endDate = getPara("end-date");
		String[] productIds = getParaValues("productId");
		String[] productNum = getParaValues("productNum");
		if(productNum==null) {
			renderAjaxResultForError("计划的产品不能为空");
			return;
		}
		Plans plans = new Plans();
		String plansId = StrKit.getRandomUUID();
		plans.setId(plansId);
		plans.setSellerId(sellerId);
		plans.setUserId(user.getId());
		plans.setType(type);
		try {
			plans.setStartDate(( new SimpleDateFormat("yyyy-MM-dd")).parse(startDate));
			plans.setEndDate(( new SimpleDateFormat("yyyy-MM-dd")).parse(endDate));
			plans.setPlansMonth(( new SimpleDateFormat("yyyy-MM-dd")).parse(datetimePicker));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		plans.setDeptId(user.getDepartmentId());
		plans.setDataArea(user.getDataArea());
		plans.setCreateDate(new Date());
		BigDecimal planAmount = new BigDecimal(0);
		for(int i=0;i<productIds.length;i++) {
			PlansDetail detail = PlansDetailQuery.me().findbySSEU(productIds[i],startDate,endDate,user.getId());
			SellerProduct sellerProduct = SellerProductQuery.me().findById(productIds[i]);
			if(detail!=null) {
				renderAjaxResultForError("已经存在产品："+SellerProductQuery.me().findById(productIds[i]).getCustomName()+"的计划");
				return;
			}
			
			PlansDetail plansDetail = new PlansDetail();
			plansDetail.setId(StrKit.getRandomUUID());
			plansDetail.setPlansId(plansId);
			plansDetail.setSellerProductId(productIds[i]);
			plansDetail.setPlanNum(new BigDecimal(productNum[i]));
			plansDetail.setCompleteNum(new BigDecimal(0));
			plansDetail.setCompleteRatio(new BigDecimal(0));
			plansDetail.setUserId(user.getId());
			plansDetail.save();
			planAmount =  planAmount.add(sellerProduct.getPrice().multiply(new BigDecimal(productNum[i])));  
		}
		plans.setPlanNum(planAmount);
		plans.save();
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
		
		List<User> userList = UserQuery.me().findByData(selectDataArea);
		for (User user : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", user.getRealname());
			item.put("value", user.getId());
			userIds.add(item);
		}
		
		List<PlansDetail> plansDetails = PlansDetailQuery.me().findbyDateArea(selectDataArea);
		for(PlansDetail planDetail : plansDetails) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", planDetail.get("custom_name"));
			item.put("value", planDetail.getSellerProductId());
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
		String plansId = getPara("plansId");
		List<Plans> list = PlansQuery.me().findbyUserNameAndTypeNameAndPlanId(userId,typeName,plansId);
		renderJson(list);
	}
	
	public void getSellerProductPlans() {
		String sellerProductId = getPara("sellerProductId");
		String typeName = getPara("typeName");
		String plansId = getPara("plansId");
		List<Plans> list = PlansQuery.me().findbySTSE(sellerProductId,typeName,plansId);
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
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		List<Map<String, Object>> sellerProducts = new ArrayList<>();
		sellerProducts.add(all);
		List<PlansDetail> plansDetails = PlansDetailQuery.me().findbyDateAreaAndUserId(selectDataArea,user.getId());
		for(PlansDetail plansDetail : plansDetails) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", plansDetail.get("custom_name"));
			item.put("value", plansDetail.getSellerProductId());
			sellerProducts.add(item);
		}

		setAttr("sellerProducts", JSON.toJSON(sellerProducts));
		render("plan_my_list.html");
	}
	
	public void myPlanList() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String keyword = getPara("keyword");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerProductId = getPara("sellerProductId");
		Page<Record> planList = PlansDetailQuery.me().paginateForAppMyPlan(getPageNumber(), getPageSize(), keyword, startDate, endDate, sellerId, selectDataArea,user.getId(),sellerProductId);

		Map<String, Object> map = new HashMap<>();
		map.put("planList", planList.getList());

		renderJson(map);
	}
}
