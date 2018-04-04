package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Page;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.Plans;
import org.ccloud.model.PlansDetail;
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
		String endDate = getPara("end-date");
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		String ti = OptionQuery.me().findValue(Consts.OPTION_WEB_PROC_PLANS_LIMIT + sellerCode);
		if(StrKit.isBlank(ti)) {
			renderAjaxResultForError("未在定制化中配置销售计划的起止时间");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
//		try {
//			if(sdf.parse(startDate).before(sdf.parse(startDateM)) || sdf.parse(startDate).after(sdf.parse(endDateM))) {
//				renderAjaxResultForError("计划的开始时间不在计划月内");
//				return;
//			}
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
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
		int index = datetimePicker.indexOf("-");
		String day = datetimePicker +"-"+ti;
		Calendar cal = Calendar.getInstance();  
		//设置年份  
		try {
			if(StrKit.notBlank(ti)) {
				cal.setTime(sdf.parse(day));
				cal.set(Calendar.YEAR,Integer.parseInt(datetimePicker.substring(0,index)));  
				plans.setStartDate(sdf.parse(day));
				//设置月份  
				cal.set(Calendar.MONTH, Integer.parseInt(datetimePicker.substring(index+1,datetimePicker.length()))-1); 
				cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
				endDate= sdf.format(cal.getTime());//获得前一天
				startDate =  datetimePicker+"-"+ti;
				plans.setEndDate(sdf.parse(endDate));
				plans.setPlansMonth(sd.parse(datetimePicker));
			}else {
				startDate = datetimePicker + "-01";
				cal.setTime(sdf.parse(datetimePicker + "-01"));
				cal.set(Calendar.YEAR,Integer.parseInt(datetimePicker.substring(0,index)));  
				plans.setStartDate(sdf.parse(startDate));
				//设置月份  
				cal.set(Calendar.MONTH, Integer.parseInt(datetimePicker.substring(index+1,datetimePicker.length()))); 
				cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
				endDate= sdf.format(cal.getTime());//获得前一天
				plans.setEndDate(sdf.parse(endDate));
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		plans.setDeptId(user.getDepartmentId());
		plans.setDataArea(user.getDataArea());
		plans.setCreateDate(new Date());
		for(int i=0;i<productIds.length;i++) {
			PlansDetail detail = PlansDetailQuery.me().findbySSEU(productIds[i],startDate,endDate,user.getId());
			if(detail!=null) {
				renderAjaxResultForError("已经存在产品："+SellerProductQuery.me().findById(productIds[i]).getCustomName()+" 的该月计划");
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
			plansDetail.setCreateDate(new Date());
			plansDetail.setDataArea(user.getDataArea());
			plansDetail.save();
		}
		plans.save();
		renderAjaxResultForSuccess("新增成功");
	}

	public void myPlans() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
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
		
		List<PlansDetail> plansDetails = PlansDetailQuery.me().findbyDateArea(selectDataArea,sellerId);
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
		String datetimePicker = getPara("datetimePicker"); 
		Page<Record> planList = PlansQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, userId, type, startDate, endDate, sellerId, selectDataArea,showType,sellerProductId,datetimePicker);

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
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		List<Map<String, Object>> sellerProducts = new ArrayList<>();
		sellerProducts.add(all);
		List<PlansDetail> plansDetails = PlansDetailQuery.me().findbyDateAreaAndUserId(selectDataArea,user.getId(),sellerId);
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
		String datetimePicker = getPara("datetimePicker");
		Page<Record> planList = PlansDetailQuery.me().paginateForAppMyPlan(getPageNumber(), getPageSize(), keyword, startDate, endDate, sellerId, selectDataArea,user.getId(),sellerProductId,datetimePicker);

		Map<String, Object> map = new HashMap<>();
		map.put("planList", planList.getList());

		renderJson(map);
	}
}
