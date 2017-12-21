package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.ccloud.model.query.OptionQuery;
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
import com.beust.jcommander.internal.Maps;
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
	
	//库存详情
	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all" }, logical = Logical.OR)
	public void index() {
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
		String id = getPara("id");
		String type = getPara("type");
		String nature = getPara("nature");
		String subType = getPara("level");

		String status = getPara("status");
		String dataArea = selectDataArea + "%";

		Page<Record> visitList = CustomerVisitQuery.me().paginateForApp(getPageNumber(), getPageSize(), id, type, nature, subType, status, dataArea);

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

		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea));
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

		List<Dict> statusList = DictQuery.me().findDictByType("customer_audit");
		List<Map<String, Object>> statusList1 = new ArrayList<>();
		statusList1.add(all);

		for(Dict status: statusList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", status.getName());
			item.put("value", status.getValue());
			statusList1.add(item);
		}


		Map<String, List<Map<String, Object>>> data = ImmutableMap.of( "type", customerTypeList2, "nature", nature, "level", customerLevel, "status", statusList1);
		renderJson(data);
	}

	public void refresh() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me().paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("id"), getPara("type"), getPara("nature"), getPara("level"), getPara("status"), selectDataArea);

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
		CustomerVisit visit = CustomerVisitQuery.me().findMoreById(id);
		setAttr("visit", visit);
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
		
		CustomerVisit customerVisit = CustomerVisitQuery.me().findMoreById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}
		
		String dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea) + "%";
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(customerVisit.getSellerCustomerId(), dataArea);

		setAttr("customerVisit", customerVisit);
		setAttr("cTypeName", Joiner.on(",").join(typeList.iterator()));
		
		render("customer_visit_review.html");
	}

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
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
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
		 customerVisit.setStatus(Customer.CUSTOMER_AUDIT);
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

		 boolean updated = customerVisit.saveOrUpdate();
		 
		 if (!updated) {
			 renderAjaxResultForError("保存客户拜访信息出错");
			 return ;
		 }
		 
		 Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_review_" + getSessionAttr("sellerCode"));
		 if (isChecked)
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
		
		String location = getPara("location");
		String commentDesc = getPara("comment");
		String lat = getPara("lat");
		String lng = getPara("lng");

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		Integer status = getParaToInt("status");
		String comment = (status == 1) ? "批准" : "拒绝";
		if(StrKit.notBlank(location)) 
			customerVisit.setReviewAddress(location);
		
		if (StrKit.notBlank(lat))
			customerVisit.setReviewLat(new BigDecimal(lat));
		if (StrKit.notBlank(lng))
			customerVisit.setReviewLng(new BigDecimal(lng));
		
		if(StrKit.notBlank(commentDesc)) 
			customerVisit.setComment(commentDesc);
		if(list.size() > 0) 
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

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_visit_review");

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			kv.set("submit", user.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE, kv);
		}
		
		workFlowService.completeTask(taskId, comment, var);
		
		sendMessage(sellerId, comment, user.getId(), toUser.getId(), user.getDepartmentId(), user.getDataArea()
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName());
		
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

			String defKey = "_customer_visit_review";
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
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName());
		
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
			, String dataArea, String type, String title) {
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(fromUserId);
		
		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);
		message.setType(type);
		
		message.setTitle(title);
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
	}
}
