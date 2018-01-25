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
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerType;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.Dict;
import org.ccloud.model.Message;
import org.ccloud.model.User;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DictQuery;
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
			item.put("value", customerType.getName());
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
		setAttr("nature", JSON.toJSON(nature));
		setAttr("level", JSON.toJSON(customerLevel));
		setAttr("status", JSON.toJSON(statusList1));

		String history = getPara("history");
		setAttr("history", history);	
		render("customer_visit_list.html");
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all" }, logical = Logical.OR)
	public void one() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

		Page<Record> visitList = CustomerVisitQuery.me().paginateForApp(getPageNumber(), getPageSize(), getPara("id"), null, null, null, null, selectDataArea, null);

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

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me().paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), null, null, null, null, selectDataArea, null);

		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}

		StringBuilder html = new StringBuilder();
		for (Record visit : visitList.getList())
		{
			html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/detail?id=" + visit.getStr("id") + "&one=1\">\n" +
					"                <div class=\"weui-cell__bd ft14\">\n" +
					"                    <p>" + visit.getStr("customer_name") + "</p>\n" +
					"                    <p class=\"gray ft12\">" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "\n" +
					"                        <span class=\"fr\">" + visit.get("create_date").toString() + "</span>\n" +
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


	public void refresh() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";
		
		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me().paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), getPara("type"), getPara("nature"), getPara("level"), getPara("status"), selectDataArea, getPara("searchKey"));

		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}

		StringBuilder html = new StringBuilder();
		for (Record visit : visitList.getList())
		{
			html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/detail?id=" + visit.getStr("id") + "\">\n" +
					"                <div class=\"weui-cell__bd ft14\">\n" +
					"                    <p>" + visit.getStr("customer_name") + "</p>\n" +
					"                    <p class=\"gray ft12\">" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "\n" +
					"                        <span class=\"fr\">" + visit.get("create_date").toString() + "</span>\n" +
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

	public void detail() {
		String id = getPara("id");
		if(StrKit.notBlank(getPara("one"))) {
			setAttr("one", getPara("one"));
		}

		CustomerVisit visit = CustomerVisitQuery.me().findMoreById(id);
		setAttr("visit", visit);

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
		
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(customerVisit.getSellerCustomerId(), dataArea);

		setAttr("customerVisit", customerVisit);
		setAttr("cTypeName", Joiner.on(",").join(typeList.iterator()));
		
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

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

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

		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString() + "%";
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

		 Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_visit_" + getSessionAttr("sellerCode"));

		 List<ImageJson> list = Lists.newArrayList();
		 String picJson = getPara("pic");

		if (isChecked != null && isChecked) customerVisit.setStatus(Customer.CUSTOMER_AUDIT);
		else customerVisit.setStatus(Customer.CUSTOMER_NORMAL);

		 customerVisit.setUserId(user.getId());
		 customerVisit.setDataArea(user.getDataArea());
		 customerVisit.setDeptId(user.getDepartmentId());
		 
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
				String waterFont2 = user.getRealname() +  DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
				String waterFont3 =  customerVisit.getLocation();
				String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));

				image.setSavePath(savePath.replace("\\", "/"));
				image.setOriginalPath(originalPath.replace("\\", "/"));
				list.add(image);
			}
		 }
		 if (list.size()!=0) customerVisit.setPhoto(JSON.toJSONString(list));

		 boolean updated = customerVisit.saveOrUpdate();
		 
		 if (!updated) {
			 renderAjaxResultForError("保存客户拜访信息出错");
			 return ;
		 }

		 if (isChecked != null && isChecked)
			updated = startProcess(customerVisit);
		 
		 if (updated)
			 renderAjaxResultForSuccess("操作成功");
		 else 
			 renderAjaxResultForError("操作失败");
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
		
		if (StrKit.notBlank(lat))
			customerVisit.setReviewLat(new BigDecimal(lat));
		if (StrKit.notBlank(lng))
			customerVisit.setReviewLng(new BigDecimal(lng));
		
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

	private boolean startProcess(CustomerVisit customerVisit) {

		//CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		boolean isUpdated = true;
		Boolean isCustomerVisit = true;

		Map<String, Object> param = new HashMap<>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
		
		if (manager == null)
			return false;
		
		if (isCustomerVisit != null && isCustomerVisit.booleanValue()) {

			String defKey = Consts.PROC_CUSTOMER_VISIT_REVIEW;
			param.put("manager", manager.getUsername());

			WorkFlowService workflow = new WorkFlowService();
			String procInstId = workflow.startProcess(customerVisit.getId(), defKey, param);

			customerVisit.setProcDefKey(defKey);
			customerVisit.setProcInstId(procInstId);
//			customerVisit.setStatus(0);
		}
		
		isUpdated = customerVisit.saveOrUpdate();
		
		if (!isUpdated)
			return false;
		
		sendMessage(sellerId, customerVisit.getQuestionDesc(), user.getId(), manager.getId(), user.getDepartmentId(), user.getDataArea()
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName(),customerVisit.getId());
		
		return isUpdated;
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

	public void trajectory() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";
		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		List<Map<String, Object>> users = new ArrayList<>();

		for(Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			users.add(item);
		}
		setAttr("users", JSON.toJSON(users));

		render("visit_trajectory.html");
	}

	public void trajectoryRefresh() {
		String userId = getPara("userId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if(StrKit.notBlank(userId)) user = UserQuery.me().findById(userId);

		String startDate = getPara("startDate");
//		String startDate = "2017-01-01 00:00:00";
		String endDate = getPara("endDate");
		List<Record> visitList = CustomerVisitQuery.me().findLngLat(user.getId(), startDate, endDate);
		setAttr("visitList", JSON.toJSON(visitList));
		renderJson(visitList);
	}
}
