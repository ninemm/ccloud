package org.ccloud.front.controller;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.ActivityApply;
import org.ccloud.model.ActivityExecute;
import org.ccloud.model.ActivityExecuteTemplate;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerType;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.CustomerVisitJoinActivity;
import org.ccloud.model.Dict;
import org.ccloud.model.ExpenseDetail;
import org.ccloud.model.Message;
import org.ccloud.model.User;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.ActivityApplyQuery;
import org.ccloud.model.query.ActivityExecuteQuery;
import org.ccloud.model.query.ActivityExecuteTemplateQuery;
import org.ccloud.model.query.ActivityQuery;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.ExpenseDetailQuery;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.ImageUtils;
import org.ccloud.wechat.WechatJSSDKInterceptor;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

@RouterMapping(url = "/customerVisit")
@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all" }, logical = Logical.OR)
public class CustomerVisitController extends BaseFrontController {
	
	//库存详情
	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all" }, logical = Logical.OR)
	public void index() {

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString();
		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(dealerDataArea);
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();
		customerTypeList2.add(all);

		for(CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		List<Dict> subTypeList = DictQuery.me().findDictByType("customer_subtype");
		List<Map<String, Object>> customerLevel = new ArrayList<>();
		customerLevel.add(all);

		for(Dict subType : subTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", subType.getName());
			item.put("value", subType.getValue());
			customerLevel.add(item);
		}
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByDataArea(selectDataArea);
		List<Map<String, Object>> bizUserList = new ArrayList<>();
		for(CustomerVisit customerVisit:customerVisits) {
			Map<String, Object> item = new HashMap<>();
			item.put("value", customerVisit.getStr("user_id"));
			item.put("title", customerVisit.getStr("realname"));
			bizUserList.add(item);
		}
		
		List<Map<String, Object>> nature = new ArrayList<>();
		nature.add(all);
		
		List<Dict> statusList = DictQuery.me().findDictByType("customer_audit");
		List<Map<String, Object>> statusList1 = new ArrayList<>();
		statusList1.add(all);

		for(Dict status: statusList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", status.getName());
			item.put("value", status.getValue());
			statusList1.add(item);
		}

		setAttr("type", JSON.toJSON(customerTypeList2));
		setAttr("bizUserList", JSON.toJSON(bizUserList));
		setAttr("nature", JSON.toJSON(nature));
		setAttr("level", JSON.toJSON(customerLevel));
		setAttr("status", JSON.toJSON(statusList1));

		String history = getPara("history");
		setAttr("history", history);	
		render("customer_visit_list.html");
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all" }, logical = Logical.OR)
	public void one() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";

		Page<Record> visitList = CustomerVisitQuery.me()._paginateForApp(getPageNumber(), getPageSize(), getPara("id"), null, null,null, null, null, selectDataArea, dealerDataArea, null);

		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}
		setAttr("visitList", visitList);

		String history = getPara("history");
		setAttr("history", history);

		render("customer_visit_one_list.html");
	}

	public void oneRefresh() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";

		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me()._paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), null,null, null, null, null, selectDataArea, dealerDataArea, null);

		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}

		StringBuilder html = new StringBuilder();
		for (Record visit : visitList.getList())
		{
/*			if(visit.getStr("status").equals(Customer.CUSTOMER_BULU)) {
				html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/review?id=" + visit.getStr("id") + "&one=1\">\n" +
						"                <div class=\"weui-cell__bd ft14\">\n" +
						"                    <p>" + visit.getStr("customer_name") + "</p>\n" +
						"                    <p class=\"gray ft12\">" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "\n" +
						"                        <span class=\"fr\">" + DateUtils.dateToStr((Date)visit.get("create_date"), DateUtils.DEFAULT_FORMATTER) + "</span>\n" +
						"                    </p>\n" +
						"                    <p>活动类型：\n" +
						"                        <span class=\"orange\">" + DictQuery.me().findName(visit.getStr("question_type")) + "</span>\n" +
						"                        <span class=\"green fr\">" + DictQuery.me().findName(visit.getStr("status")) + "</span>\n" +
						"                    </p>\n" +
						"                </div>\n" +
						"                <span class=\"weui-cell__ft\"></span>\n" +
						"            </a>");
			}else {
*/				html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/detail?id=" + visit.getStr("id") + "&one=1\">\n" +
						"                <div class=\"weui-cell__bd ft14\">\n" +
						"                    <p>" + visit.getStr("customer_name") + "</p>\n" +
						"                    <p class=\"gray ft12\">" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "\n" +
						"                        <span class=\"fr\">" + DateUtils.dateToStr((Date)visit.get("create_date"), DateUtils.DEFAULT_FORMATTER) + "</span>\n" +
						"                    </p>\n" +
						"                    <p>活动类型：\n" +
						"                        <span class=\"orange\">" + DictQuery.me().findName(visit.getStr("question_type")) + "</span>\n" +
						"                        <span class=\"green fr\">" + DictQuery.me().findName(visit.getStr("status")) + "</span>\n" +
						"                    </p>\n" +
						"                </div>\n" +
						"                <span class=\"weui-cell__ft\"></span>\n" +
						"            </a>");
//			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", visitList.getTotalRow());
		map.put("totalPage", visitList.getTotalPage());
		renderJson(map);
	}


	public void refresh() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		
		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me()._paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), getPara("type"), getPara("nature"),getPara("user"), getPara("level"), getPara("status"), selectDataArea, dealerDataArea, getPara("searchKey"));

		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}

		StringBuilder html = new StringBuilder();
		for (Record visit : visitList.getList())
		{
			if(visit.getStr("status").equals(Customer.CUSTOMER_BULU)) {
				html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/customerVisitWaiting?id=" + visit.getStr("id") + "&one=1\">\n");
			}else {
				html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/detail?id=" + visit.getStr("id") + "\">\n" );}
			html.append(	"                <div class=\"weui-cell__bd ft14\">\n" +
					"                    <p>" + visit.getStr("customer_name") + "</p>\n" +
					"                    <p class=\"gray ft12\">" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "\n" +
					"                        <span class=\"fr\">" + DateUtils.dateToStr((Date)visit.get("create_date"), DateUtils.DEFAULT_FORMATTER) + "</span>\n" +
					"                    </p>\n" +
					"                    <p>活动类型：\n" +
					"                        <span class=\"orange\">" + DictQuery.me().findName(visit.getStr("question_type")) + "</span>\n" +
					"                        <span class=\"green fr\">" + DictQuery.me().findName(visit.getStr("status")) + "</span>\n" +
					"                    </p>\n" +
					"                </div>\n" +
					"                <span class=\"weui-cell__ft\"></span>\n" +
					"            </a>");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", visitList.getTotalRow());
		map.put("totalPage", visitList.getTotalPage());
		renderJson(map);
	}

	@Before(WechatJSSDKInterceptor.class)
	public void edit() {
	    List<Map<String, String>> list = getVisitTypeList();
		setAttr("problem", JSON.toJSONString(list));
		render("customer_visit_edit.html");
	}
	
	public void activityChoose(){
		String customerId = getPara("customerId");
		List<Record> activityRecords = ActivityQuery.me()._findByCustomerId(customerId);
		List<Map<String, String>> activityList = Lists.newArrayList();
	    for (Record record : activityRecords) {
		    	Map<String, String> map = Maps.newHashMap();
		    	if(record.getStr("name")==null) {
		    		map.put("title", record.getStr("title")+" "+record.getStr("create_date"));
		    	}else {
		    		map.put("title", record.getStr("title")+" "+record.getStr("name")+" "+record.getStr("create_date"));
		    	}
		    	map.put("value", record.getStr("activityApplyId"));
		    	activityList.add(map);
	    }
	    renderJson("activityRecords",JSON.toJSONString(activityList));
	}
	
	public void detail() {
		String id = getPara("id");
		if(StrKit.notBlank(getPara("one"))) {
			setAttr("one", getPara("one"));
		}
		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		String imageListStore = customerVisit.getPhoto();
		ExpenseDetail expenseDetail = new ExpenseDetail();
		if(StrKit.notBlank(customerVisit.getActiveApplyId())) {
			if(StrKit.notBlank(ActivityApplyQuery.me().findById(customerVisit.getActiveApplyId()).getExpenseDetailId())) {
				expenseDetail = ExpenseDetailQuery.me().findById(ActivityApplyQuery.me().findById(customerVisit.getActiveApplyId()).getExpenseDetailId());
				setAttr("expenseDetail",expenseDetail);
			}
		}
		List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
		CustomerVisit visit = CustomerVisitQuery.me().findMoreById(id);
		List<Record> findByActivity = CustomerVisitQuery.me().findByActivity(id);
		List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findByCustomerVisitId(id);
		String activity="";
		for (Record record : findByActivity) {
			activity=activity+record.getStr("title")+",";
		}
		if (StrKit.notBlank(activity)) {
			activity = activity.substring(0, activity.length() - 1);  
		}
		setAttr("activity", activity);
		setAttr("visit", visit);
		setAttr("list",list);
		setAttr("activityExecutes",activityExecutes);

		//审核后将message中是否阅读改为是
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id,user.getId());
		if (null != message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}

		String history = getPara("history");
		setAttr("history", history);	
		render("customer_visit_detail.html");
	}

	public void success() {
		render("success.html");
	}
	
	@Before(WechatJSSDKInterceptor.class)
	public void review() {
		
		keepPara();
		
		String id = getPara("id");

		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}
		
		CustomerVisit customerVisit = CustomerVisitQuery.me().findMoreById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}
		ExpenseDetail expenseDetail = new ExpenseDetail();
		if(StrKit.notBlank(CustomerVisitQuery.me().findById(id).getActiveApplyId())) {
			
			if(StrKit.notBlank(ActivityApplyQuery.me().findById(CustomerVisitQuery.me().findById(id).getActiveApplyId()).getExpenseDetailId())) {
				expenseDetail = ExpenseDetailQuery.me().findById(ActivityApplyQuery.me().findById(CustomerVisitQuery.me().findById(id).getActiveApplyId()).getExpenseDetailId());
			}
		}
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(customerVisit.getSellerCustomerId(), dataArea);
		String imageListStore = customerVisit.getPhoto();
		List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
		List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findByCustomerVisitId(id);
		
		setAttr("customerVisit", customerVisit);
		setAttr("cTypeName", Joiner.on(",").join(typeList.iterator()));
		setAttr("list",list);
		setAttr("expenseDetail",expenseDetail);
		setAttr("activityExecutes",activityExecutes);
		
		//审核后将message中是否阅读改为是
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id,user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}
				
		render("customer_visit_review.html");
	}

	public void visitCustomerChoose() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

 		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString();
		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(dealerDataArea);
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("customer_visit_choose.html");
	}

	@Before(Tx.class)
	public void save() {
			
		CustomerVisit customerVisit = getModel(CustomerVisit.class);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String activityApplyId = getPara("activity_apply_id");
		String activityExceuteId = getPara("activity_execute_id");
		if(!activityApplyId.equals("")) {
			List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByActivityApplyId(activityApplyId);
			if(customerVisits.size()>0) {
				for(CustomerVisit visit:customerVisits) {
					if(!visit.getStatus() .equals(Consts.CUSTOMER_VISIT_STATUS_PASS)) {
						renderAjaxResultForError("您的拜访未审核通过！");
						return;
					}
				}
			}
		}
		Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_visit_" + getSessionAttr("sellerCode"));
		
		List<ImageJson> list = Lists.newArrayList();
		String picJson = getPara("pic");
		
		if (isChecked != null && isChecked) customerVisit.setStatus(Customer.CUSTOMER_AUDIT);
		else customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
		
		customerVisit.setUserId(user.getId());
		customerVisit.setDataArea(user.getDataArea());
		customerVisit.setDeptId(user.getDepartmentId());
		customerVisit.setActiveApplyId(activityApplyId);
		customerVisit.setActivityExecuteId(activityExceuteId);
		if (StrKit.notBlank(picJson)) {
			
			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");
				String orderList = obj.getString("orderList");
				ImageJson image = new ImageJson();
				image.setOrderList(orderList);
				image.setImgName(picname);
				if(pic.length() == 32) {
					image.setSavePath(obj.getString("savepath"));
					image.setOriginalPath(pic);
				}else {
					//原图
					String originalPath = qiniuUpload(pic);
					//添加的水印内容
					String waterFont1 = customerVisit.getSellerCustomer().getCustomer().getCustomerName();
					String waterFont2 = user.getRealname() +  DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
					String waterFont3 =  customerVisit.getLocation();
//					String waterFont3 = "湖北省-武汉市-洪山区";
					//图片添加水印  上传图片  水印图
					String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));
					
					image.setSavePath(savePath.replace("\\", "/"));
					image.setOriginalPath(originalPath.replace("\\", "/"));
				}
				list.add(image);
			}
		}
		if (list.size()!=0) customerVisit.setPhoto(JSON.toJSONString(list));
		
		boolean updated = customerVisit.saveOrUpdate();
		
		//获取选取活动的id
		if(StrKit.notBlank(activityApplyId)) {
			List<String> activityIdList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(ActivityApplyQuery.me().findById(activityApplyId).getActivityId());
			for (String activityId : activityIdList) {
				CustomerVisitJoinActivity customerVisitJoinActivity=new CustomerVisitJoinActivity();
				customerVisitJoinActivity.setCustomerVisitId(customerVisit.getId());
				customerVisitJoinActivity.setId(StrKit.getRandomUUID());
				customerVisitJoinActivity.setActivityId(activityId);
				customerVisitJoinActivity.save();
			}
		}
			
		if (!updated) {
			renderAjaxResultForError("保存客户拜访信息出错");
			return ;
		}

		String result = "";
		if (isChecked != null && isChecked) {
			result = startProcess(customerVisit);
		}
		 
		if (StrKit.isBlank(result))
			renderAjaxResultForSuccess("操作成功");
		else 
			renderAjaxResultForError(result);
	}

	public void visitCustomerInfo() {
		List<Map<String, String>> list = getVisitTypeList();
	    setAttr("problem", JSON.toJSONString(list));
		//setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("customer_visit_edit.html");
	}

	public void complete() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String id = getPara("id");
		String taskId = getPara("taskId");

		String location = getPara("location");
		String commentDesc = getPara("comment");
		String lat = getPara("lat");
		String lng = getPara("lng");

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		if (!customerVisit.getStatus().equals(Consts.CUSTOMER_VISIT_STATUS_DEFAULT)) {
			renderAjaxResultForError("拜访已审核");
			return;
		}
		Integer status = getParaToInt("status");
		String comment = (status == 1) ? "批准" : "拒绝";

		String picJson = getPara("pic");
		List<ImageJson> list = Lists.newArrayList();
		if (StrKit.notBlank(picJson)) {

			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");

				ImageJson image = new ImageJson();
				image.setImgName(picname);
				String originalPath = qiniuUpload(pic);

				String waterFont1 = customerVisit.getSellerCustomer().getCustomer().getCustomerName();
				String waterFont2 = user.getRealname() + "审核" + comment + "    " + DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
				String waterFont3 = location;
				String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));

				image.setSavePath(savePath.replace("\\", "/"));
				image.setOriginalPath(originalPath.replace("\\", "/"));
				list.add(image);
			}
		}

		if(StrKit.notBlank(location)) 
			customerVisit.setReviewAddress(location);
			if(StrKit.notBlank(customerVisit.getActiveApplyId())) {
				List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByActivityApplyId(customerVisit.getActiveApplyId());
				ActivityApply activityApply = ActivityApplyQuery.me().findById(customerVisit.getActiveApplyId());
				List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findByCustomerVisitId(id);
				if(customerVisits.size() == activityExecutes.size()) {
					activityApply.setStatus(Consts.ACTIVITY_APPLY_STATUS_VERIFICATION);
					activityApply.update();
				}
			}
		if (StrKit.notBlank(lat))
			customerVisit.setReviewLat(new BigDecimal(lat));
		if (StrKit.notBlank(lng))
			customerVisit.setReviewLng(new BigDecimal(lng));

		customerVisit.setReviewId(user.getId());
		customerVisit.setReviewDate(new Date());
		
		if (StrKit.notBlank(commentDesc))
			customerVisit.setComment(commentDesc);
		if (list.size() > 0)
			customerVisit.setImageListStore(JSON.toJSONString(list));
		
		WorkFlowService workFlowService = new WorkFlowService();
		Map<String,Object> var = new HashMap<>();
		var.put("pass", status);
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);
		
		if (status == 1) {
			customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
			
		} else {
			customerVisit.setStatus(Customer.CUSTOMER_REJECT);
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(Consts.PROC_CUSTOMER_VISIT_REVIEW);

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			kv.set("submit", toUser.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE, kv);
		}
		
		workFlowService.completeTask(taskId, comment, var);
		
		sendMessage(sellerId, comment, user.getId(), toUser.getId(), user.getDepartmentId(), user.getDataArea()
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName(),id);
		
		//审核订单后将message中是否阅读改为是
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id,user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}
		if (customerVisit.saveOrUpdate())
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	private String startProcess(CustomerVisit customerVisit) {

		//CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		Boolean isCustomerVisit = true;

		Map<String, Object> param = new HashMap<>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		
		if (isCustomerVisit != null && isCustomerVisit.booleanValue()) {

			List<User> managers = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
			if (managers == null || managers.size() == 0) {
				return "您没有配置审核人，请联系管理员";
			}

			String managerUserName = "";
			for (User u : managers) {
				if (StrKit.notBlank(managerUserName)) {
					managerUserName = managerUserName + ",";
				}

				managerUserName += u.getStr("username");
				sendMessage(sellerId, customerVisit.getQuestionDesc(), user.getId(), u.getId(), user.getDepartmentId(), user.getDataArea()
						, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName(),customerVisit.getId());
			}
			param.put("manager", managerUserName);
			param.put(Consts.WORKFLOW_APPLY_USERNAME, user.getUsername());

			String defKey = Consts.PROC_CUSTOMER_VISIT_REVIEW;
			param.put(Consts.WORKFLOW_APPLY_USERNAME, user.getUsername());

			WorkFlowService workflow = new WorkFlowService();
			String procInstId = workflow.startProcess(customerVisit.getId(), defKey, param);

			customerVisit.setProcDefKey(defKey);
			customerVisit.setProcInstId(procInstId);
//			customerVisit.setStatus(0);
		}
		

		if (!customerVisit.saveOrUpdate())
			return "新增拜访错误";

		
		return "";
	}

	private List<Map<String, String>> getVisitTypeList() {
		List<Dict> visitDictList = DictQuery.me().findDictByType("customer_visit");
	    List<Map<String, String>> list = Lists.newArrayList();
	    for (Dict dict : visitDictList) {
	    	Map<String, String> map = Maps.newHashMap();
	    	map.put("title", dict.getName());
	    	map.put("value", dict.getValue());
	    	list.add(map);
	    }
	    return list;
	}
	
	private void sendMessage(String sellerId, String comment, String fromUserId, String toUserId, String deptId
			, String dataArea, String type, String title, String id) {
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(fromUserId);
		
		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);
		message.setType(type);
		
		message.setTitle(title);
		
		message.setObjectId(id);
		message.setIsRead(Consts.NO_READ);
		message.setObjectType(Consts.OBJECT_TYPE_CUSTOMER_VISIT);
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
	}

	@Before(WechatJSSDKInterceptor.class)
	public void trajectory() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		List<Map<String, Object>> users = new ArrayList<>();

		for(Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			users.add(item);
		}
		setAttr("userId", user.getId());
		setAttr("userName", user.getRealname());
		setAttr("users", JSON.toJSON(users));

		render("visit_trajectory.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void trajectoryRefresh() {
		String userId = getPara("userId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if(StrKit.notBlank(userId)) user = UserQuery.me().findById(userId);

		String startDate = getPara("startDate");
//		String startDate = "2017-01-01 00:00:00";
		String endDate = getPara("endDate");
		List<Record> visitList = CustomerVisitQuery.me().findLngLat(user.getId(), startDate, endDate, "100101");
		setAttr("visitList", JSON.toJSON(visitList));
		renderJson(visitList);
	}
	
	public void getActivityExecute() {
		String activityApplyId = getPara("activityApplyId");
		Map<String, Object> map = new HashMap<>();
		List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByActivityApplyId(activityApplyId);
		String orderList = String.valueOf(customerVisits.size()+1);
		map.put("activityExecute", JSON.toJSON(ActivityExecuteQuery.me().findbyActivityIdAndOrderList(ActivityApplyQuery.me().findById(activityApplyId).getActivityId(),orderList)));
		if(customerVisits.size()>0) {
			if(customerVisits.get(0).getPhoto() == null && customerVisits.size()>1) {
				map.put("imgeLists",JSON.parseArray(customerVisits.get(1).getPhoto(), ImageJson.class));
			}else {
				map.put("imgeLists",JSON.parseArray(customerVisits.get(0).getPhoto(), ImageJson.class));
			}
			map.put("maxOrderList", customerVisits.size());
		}
		map.put("domain",OptionQuery.me().findValue("cdn_domain"));
		renderJson(map);
	}
	
	
	@Before(Tx.class)
	public void saveB() {
			
		CustomerVisit customerVisit = getModel(CustomerVisit.class);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String activityApplyId = getPara("activity_apply_id");
		String activityExceuteId = getPara("activity_execute_id");
		if(!activityApplyId.equals("")) {
			List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByActivityApplyId(activityApplyId);
			if(customerVisits.size()>0) {
				for(CustomerVisit visit:customerVisits) {
					if(!visit.getStatus() .equals(Consts.CUSTOMER_VISIT_STATUS_PASS)) {
						renderAjaxResultForError("您的拜访未审核通过！");
						return;
					}
				}
			}
		}
		List<ImageJson> list = Lists.newArrayList();
		String picJson = getPara("pic");
		
		customerVisit.setStatus(Customer.CUSTOMER_BULU);
		customerVisit.setUserId(user.getId());
		customerVisit.setDataArea(user.getDataArea());
		customerVisit.setDeptId(user.getDepartmentId());
		customerVisit.setActiveApplyId(activityApplyId);
		customerVisit.setActivityExecuteId(activityExceuteId);
		if (StrKit.notBlank(picJson)) {
			
			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");
				String orderList = obj.getString("orderList");
				ImageJson image = new ImageJson();
				image.setImgName(picname);
				image.setOrderList(orderList);
				if(pic.length() == 32) {
					image.setSavePath(obj.getString("savepath"));
					image.setOriginalPath(pic);
				}else {
					//原图
					String originalPath = qiniuUpload(pic);
					//添加的水印内容
					String waterFont1 = customerVisit.getSellerCustomer().getCustomer().getCustomerName();
					String waterFont2 = user.getRealname() +  DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
					String waterFont3 =  customerVisit.getLocation();
//					String waterFont3 = "湖北省-武汉市-洪山区";
					//图片添加水印  上传图片  水印图
					String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));
					
					image.setSavePath(savePath.replace("\\", "/"));
					image.setOriginalPath(originalPath.replace("\\", "/"));
					image.setOrderList(orderList);
				}
				list.add(image);
			}
		}
		if (list.size()!=0) customerVisit.setPhoto(JSON.toJSONString(list));
		
		boolean updated = customerVisit.saveOrUpdate();
		
		//获取选取活动的id
		if(StrKit.notBlank(activityApplyId)) {
			List<String> activityIdList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(ActivityApplyQuery.me().findById(activityApplyId).getActivityId());
			for (String activityId : activityIdList) {
				CustomerVisitJoinActivity customerVisitJoinActivity=new CustomerVisitJoinActivity();
				customerVisitJoinActivity.setCustomerVisitId(customerVisit.getId());
				customerVisitJoinActivity.setId(StrKit.getRandomUUID());
				customerVisitJoinActivity.setActivityId(activityId);
				customerVisitJoinActivity.save();
			}
		}
			
		if (!updated) {
			renderAjaxResultForError("保存客户拜访信息出错");
			return ;
		}

		/*if (isChecked != null && isChecked)
			updated = startProcess(customerVisit);
		 */
		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else 
			renderAjaxResultForError("操作失败");
	}
	
	@Before(Tx.class)
	public void saveWaiting() {
		String customerVisitId = getPara("customerVisitId");
		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(customerVisitId);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_visit_" + getSessionAttr("sellerCode"));
		
		List<ImageJson> list = Lists.newArrayList();
		String picJson = getPara("pic");
		
		if (isChecked != null && isChecked) customerVisit.setStatus(Customer.CUSTOMER_AUDIT);
		else customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
		
		if (StrKit.notBlank(picJson)) {
			
			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");
				String orderList = obj.getString("orderList");
				ImageJson image = new ImageJson();
				image.setImgName(picname);
				image.setOrderList(orderList);
				if(pic.length() == 32) {
					image.setSavePath(obj.getString("savepath"));
					image.setOriginalPath(pic);
				}else {
					//原图
					String originalPath = qiniuUpload(pic);
					//添加的水印内容
					String waterFont1 = customerVisit.getSellerCustomer().getCustomer().getCustomerName();
					String waterFont2 = user.getRealname() +  DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
					String waterFont3 =  customerVisit.getLocation();
//					String waterFont3 = "湖北省-武汉市-洪山区";
					//图片添加水印  上传图片  水印图
					String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));
					
					image.setSavePath(savePath.replace("\\", "/"));
					image.setOriginalPath(originalPath.replace("\\", "/"));
				}
				list.add(image);
			}
			if (list.size()!=0) customerVisit.setPhoto(JSON.toJSONString(list));
			
			boolean updated = customerVisit.saveOrUpdate();
			
			if (!updated) {
				renderAjaxResultForError("保存客户拜访信息出错");
				return ;
			}
			String result="";
			if (isChecked != null && isChecked)
				result = startProcess(customerVisit);
			 
			if (StrKit.isBlank(result))
				renderAjaxResultForSuccess("操作成功");
			else 
				renderAjaxResultForError("操作失败");
		}
	}

	public void customerVisitWaiting() {
		
		keepPara();
		
		String id = getPara("id");
		CustomerVisit customerVisit = CustomerVisitQuery.me().findMoreById(id);
		String imageListStore = customerVisit.getStr("photo");
		List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
		if(!customerVisit.getStr("active_apply_id").equals("")) {
			List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findbyActivityId(ActivityApplyQuery.me().findById(customerVisit.getStr("active_apply_id")).getActivityId());
			setAttr("activityExecute",activityExecutes);
			List<CustomerVisit> customerVisits = CustomerVisitQuery.me()._findByActivityApplyId(customerVisit.getActiveApplyId());
			setAttr("orderListNum",customerVisits.size());
		}
		setAttr("list",JSON.toJSON(list));
		setAttr("domain",OptionQuery.me().findValue("cdn_domain"));
		setAttr("customerVisit", customerVisit);		
		render("customer_visit_waiting.html");
	}
	@Before(WechatJSSDKInterceptor.class)
	public void addActivityApplyVisit() {
		String activityApplyId = getPara("applyId");
		String orderList = getPara("orderList");
		CustomerVisit cv = CustomerVisitQuery.me().findByActivityApplyIdAndOrderList(activityApplyId, orderList);
		List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByActivityApplyId(activityApplyId);
		if(customerVisits.size()>0) {
			setAttr("imgeLists",JSON.toJSON(JSON.parseArray(customerVisits.get(0).getPhoto(), ImageJson.class)));
			setAttr("customerVisit",CustomerVisitQuery.me().findMoreById(customerVisits.get(0).getId()));
		}
		if(cv != null) {
			setAttr("cv",cv);
		}
		Record record = ActivityQuery.me().findByApplyId(activityApplyId);
		List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findbyActivityIdAndOrderList(record.getStr("activity_id"),orderList);
		ActivityExecute activityExecute = ActivityExecuteQuery.me()._findbyActivityIdAndOrderList(record.getStr("activity_id"), orderList);
		List<Map<String, String>> list = getVisitTypeList();
		setAttr("problem", JSON.toJSONString(list));
		setAttr("activityExecutes",JSON.toJSONString(activityExecutes));
		setAttr("activityExecuteId",activityExecute.getId());
		setAttr("record",record);
		setAttr("domain",OptionQuery.me().findValue("cdn_domain"));
		setAttr("orderList",orderList);
		render("customer_visit_activity.html");
	}
	@Before(Tx.class)
	public void saveActivityVisit(){
		CustomerVisit customerVisit = getModel(CustomerVisit.class);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String activityApplyId = getPara("activity_apply_id");
		String activityExecuteId = getPara("activity_execute_id");
		Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_visit_" + getSessionAttr("sellerCode"));
		int completeNum = Integer.parseInt(getPara("completeNum"));
		if(completeNum>0) {
			String completeRemark = "";
			for(int i = 0 ; i < completeNum ; i++) {
				String activityTempleteName = "activityTempleteName"+i;
				String activityTemplete = "activityTemplete"+i;
				completeRemark += getPara(activityTempleteName)+" : "+getPara(activityTemplete)+";";
			}
			customerVisit.setTempleteRemark(completeRemark);
		}
		
		List<ImageJson> list = Lists.newArrayList();
		String picJson = getPara("pic");
		
		if (isChecked != null && isChecked) customerVisit.setStatus(Customer.CUSTOMER_AUDIT);
		else customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
		
		customerVisit.setUserId(user.getId());
		customerVisit.setDataArea(user.getDataArea());
		customerVisit.setDeptId(user.getDepartmentId());
		customerVisit.setActiveApplyId(activityApplyId);
		customerVisit.setActivityExecuteId(activityExecuteId);
		if (StrKit.notBlank(picJson)) {
			
			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");
				String orderList = obj.getString("orderList");
				ImageJson image = new ImageJson();
				image.setImgName(picname);
				image.setOrderList(orderList);
				if(pic.length() == 32) {
					image.setSavePath(obj.getString("savepath"));
					image.setOriginalPath(pic);
				}else {
					//原图
					String originalPath = qiniuUpload(pic);
					//添加的水印内容
					String waterFont1 = customerVisit.getSellerCustomer().getCustomer().getCustomerName();
					String waterFont2 = user.getRealname() +  DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
//					String waterFont3 =  customerVisit.getLocation();
					String waterFont3 = "湖北省-武汉市-洪山区";
					//图片添加水印  上传图片  水印图
					String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));
					
					image.setSavePath(savePath.replace("\\", "/"));
					image.setOriginalPath(originalPath.replace("\\", "/"));
				}
				list.add(image);
			}
		}
		if (list.size()!=0) customerVisit.setPhoto(JSON.toJSONString(list));
		
		boolean updated = customerVisit.saveOrUpdate();
		
		//获取选取活动的id
		if(StrKit.notBlank(activityApplyId)) {
			List<String> activityIdList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(ActivityApplyQuery.me().findById(activityApplyId).getActivityId());
			for (String activityId : activityIdList) {
				CustomerVisitJoinActivity customerVisitJoinActivity=new CustomerVisitJoinActivity();
				customerVisitJoinActivity.setCustomerVisitId(customerVisit.getId());
				customerVisitJoinActivity.setId(StrKit.getRandomUUID());
				customerVisitJoinActivity.setActivityId(activityId);
				customerVisitJoinActivity.save();
			}
		}
			
		if (!updated) {
			renderAjaxResultForError("保存客户拜访信息出错");
			return ;
		}

		String result="";
		if (isChecked != null && isChecked)
			result = startProcess(customerVisit);
		 
		if (StrKit.isBlank(result))
			renderAjaxResultForSuccess("操作成功");
		else 
			renderAjaxResultForError("操作失败");
	}
	
	public void checkCustomerVisit() {
		String activityApplyId = getPara("applyId");
		String orderList = getPara("orderList");
		String message = "";
		ActivityApply activityApply = ActivityApplyQuery.me().findById(activityApplyId);
		if(activityApply.getStatus()== Consts.ACTIVITY_APPLY_STATUS_PASS || activityApply.getStatus()== Consts.ACTIVITY_APPLY_STATUS_VERIFICATION || activityApply.getStatus()== Consts.ACTIVITY_APPLY_STATUS_END) {
			
			if(!orderList.equals("1")) {
				orderList = String.valueOf(Integer.parseInt(orderList)-1);
				CustomerVisit customerVisit = CustomerVisitQuery.me().findByActivityApplyIdAndOrderList(activityApplyId,orderList);
				if(customerVisit==null) {
					message = "上一执行步骤还未开始执行";
				}else if(!customerVisit.getStatus().equals(Consts.CUSTOMER_VISIT_STATUS_PASS)) {
					message = "上一执行步骤未审核通过";
				}
			}
		}else {
			message = "活动申请尚未通过";
		}
		Map<String, Object> map = new HashMap<>();
		map.put("message", message);
		renderJson(map);
	}
	//根据活动执行步骤查找活动执行步骤对应的模板
	public void getActivityTemplete() {
		String activityExecuteId = getPara("activityExecuteId");
		List<ActivityExecuteTemplate> list = ActivityExecuteTemplateQuery.me().findActivityExecuteId(activityExecuteId);
		renderJson(list);
	}
	
}