package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
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
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.wechat.WechatJSSDKInterceptor;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

@RouterMapping(url = "/customerVisit")
public class CustomerVisitController extends BaseFrontController {
	
	public void index() {
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
		String id = getPara("id");
		String type = getPara("type");
		String nature = getPara("nature");
		String subType = getPara("subType");
		String dataArea = selectDataArea + "%";

		Page<Record> visitList = CustomerVisitQuery.me().paginateForApp(getPageNumber(), getPageSize(), id, type, nature, subType, dataArea);

		transform(visitList.getList());
		if(StrKit.notBlank(getPara("id"))) {
			setAttr("id", getPara("id"));
			setAttr("name", getPara("name"));
		}
		setAttr("visitList", visitList);
		render("customer_visit_list.html");
	}

	public void getSelect() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(DataAreaUtil.getUserDealerDataArea(selectDataArea));
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

		List<Map<String, Object>> nature = new ArrayList<>();
		nature.add(all);

		Map<String, List<Map<String, Object>>> data = ImmutableMap.of( "type", customerTypeList2, "nature", nature, "level", customerLevel);
		renderJson(data);
	}

	public void refresh() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me().paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), getPara("type"), getPara("nature"), getPara("level"), selectDataArea);
		transform(visitList.getList());

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
					"                        <span class=\"orange\">" + visit.getStr("typeName") + "</span>\n" +
					"                        <span class=\"green fr\">" + visit.getStr("statusName") + "</span>\n" +
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
	    List<Dict> problem_list = DictQuery.me().findByCode("visit");
	    setAttr("problem", JSON.toJSONString(problem_list));
		render("customer_visit_detail.html");
	}

	public void detail() {

		String id = getPara("id");
		List<Record> visit = CustomerVisitQuery.me().findMoreById(id);
		transform(visit);

		String imageListStore = visit.get(0).get("photo");
		List<ImageJson> list = new ArrayList<>();
		if (StrKit.notBlank(imageListStore)) {
			list = JSON.parseArray(imageListStore, ImageJson.class);
		}
		visit.get(0).set("imageList",list);

		setAttr("visit", visit.get(0));
		render("customer_visit_detail.html");

	}

	public void success() {
		render("success.html");
	}
	
	@Before(WechatJSSDKInterceptor.class)
	public void review() {
		
		keepPara();
		
		String id = getPara("id");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}
		
		List<Record> customerVisit = CustomerVisitQuery.me().findMoreById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}
		
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeIdListBySellerCustomerId(customerVisit.get(0).getStr("seller_customer_id"), DataAreaUtil.getUserDealerDataArea(selectDataArea) + "%");
		List<String> typeName = new ArrayList<>();
		for(String type : typeList)
			typeName.add(CustomerTypeQuery.me().findById(type).getStr("name"));

		transform(customerVisit);

		String imageListStore = customerVisit.get(0).get("photo");
		List<ImageJson> list = new ArrayList<>();
		if (StrKit.notBlank(imageListStore)) {
			list = JSON.parseArray(imageListStore, ImageJson.class);
		}
		customerVisit.get(0).set("imageList",list);

		setAttr("customerVisit", customerVisit.get(0));
		setAttr("cTypeName", Joiner.on(",").join(typeName.iterator()));
		
		render("customer_visit_review.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void visitAdd() {
	    List<Dict> problem_list = DictQuery.me().findByCode("visit");
	    setAttr("problem",JSON.toJSONString(problem_list));
		render("customer_visit_add.html");
	}

	//拜访客户选择
	public void visitCustomerChoose() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
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

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
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

		 List<ImageJson> list = Lists.newArrayList();
		 String picJson = getPara("pic");
		 
		 customerVisit.setUserId(user.getId());
		 customerVisit.setStatus(0);
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
				String newPath = upload(pic);
				image.setSavePath(newPath.replace("\\", "/"));
				list.add(image);
			}
		 }
		 customerVisit.setPhoto(JSON.toJSONString(list));

		 boolean updated = true;
		 updated = customerVisit.saveOrUpdate();
		 
		 if (!updated) {
			 renderAjaxResultForError("包括客户拜访信息出错");
			 return ;
		 }
		 
		 updated = startProcess(customerVisit.getId());
		 
		 if (updated) {
			 renderAjaxResultForSuccess("添加成功");
		 }
		 else renderAjaxResultForError("添加失败");
	}

	public void visitCustomerInfo() {
		List<Dict> problem_list = DictQuery.me().findByCode("visit");
		setAttr("problem",JSON.toJSONString(problem_list));
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("customer_visit_add.html");
	}
	

	public void complete() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String taskId = getPara("taskId");
		String id = getPara("id");

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
				String newPath = upload(pic);
				image.setSavePath(newPath.replace("\\", "/"));
				list.add(image);
			}
		}
		String commentDesc = getPara("comment");
		String location = getPara("location");

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		Integer status = getParaToInt("status");
		String comment = (status == 1) ? "批准" : "拒绝";
		if(StrKit.notBlank(location)) customerVisit.setLocation(location);
		
		if(StrKit.notBlank(commentDesc)) customerVisit.setComment(commentDesc);
		if(list.size() > 0) customerVisit.setImageListStore(JSON.toJSONString(list));
		
		WorkFlowService workFlowService = new WorkFlowService();
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);
		
		if (status == 1) {
			customerVisit.setStatus(1);
		} else {
			customerVisit.setStatus(2);
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_visit_review");

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			kv.set("submit", user.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE, kv);
		}
		
		Map<String,Object> var = new HashMap<>();
		var.put("pass", status);
		workFlowService.completeTask(taskId, comment, var);
		
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(user.getId());
		
		message.setToUserId(toUser.getId());
		message.setDeptId(user.getDepartmentId());
		message.setDataArea(user.getDataArea());
		message.setType(Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE);
		
		message.setTitle(customerVisit.getSellerCustomer().getCustomer().getCustomerName());
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
		
		if (customerVisit.saveOrUpdate())
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	private void transform(List<Record> list) {

		List<Dict> problem_list = DictQuery.me().findByCode("visit");

		for(Record visit : list) {
			if (visit.getInt("status") == 2) visit.set("statusName", "正常");
			if (visit.getInt("status") == 1) visit.set("statusName", "已拒绝");
			if (visit.getInt("status") == 0) visit.set("statusName", "待审核");

			for(Dict dict : problem_list)
				if(visit.getStr("question_type").equals(dict.getId().toString())) visit.set("typeName", dict.getName());
		}
	}

	private boolean startProcess(String id) {

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		boolean isUpdated = true;
		Boolean isCustomerVisit = true;

		if (customerVisit == null) {
			return false;
		}
		
		Map<String, Object> param = new HashMap<>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
		
		if (isCustomerVisit != null && isCustomerVisit.booleanValue()) {
			
			if (manager == null) {
				return false;
			}

			String defKey = "_customer_visit_review";
			param.put("manager", manager.getUsername());
			WorkFlowService workflow = new WorkFlowService();
			String procInstId = workflow.startProcess(id, defKey, param);

			customerVisit.setProcDefKey(defKey);
			customerVisit.setProcInstId(procInstId);
			customerVisit.setStatus(0);
		}
		
		isUpdated = customerVisit.update();
		
		if (!isUpdated)
			return false;
		
		Message message = new Message();
		message.setFromUserId(user.getId());
		message.setToUserId(manager.getId());
		message.setDeptId(user.getDepartmentId());
		
		message.setSellerId(sellerId);
		message.setDataArea(user.getDataArea());
		message.setContent(customerVisit.getQuestionDesc());
		message.setType(Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE);
		message.setTitle(customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
		
		return isUpdated;
	}

}
