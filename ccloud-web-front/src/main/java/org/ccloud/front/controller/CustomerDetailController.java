package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Department;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SellerBrandQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.ccloud.model.vo.CustomerVO;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Created by WT on 2017/12/7.
 */
@RouterMapping(url="/customerDetail")
public class CustomerDetailController extends BaseFrontController {

	public void index() {

		User user = getUser();
		String taskId = getPara("taskId");
		String sellerCustomerId = getPara("sellerCustomerId");
		String selectDataArea = getUserDeptDataArea(user.getDataArea());

		if (StrKit.notBlank(sellerCustomerId)) {
			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(sellerCustomerId);
			List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeIdListBySellerCustomerId(sellerCustomerId,
					DataAreaUtil.getUserDealerDataArea(selectDataArea));

			List<String> typeName = new ArrayList<>();
			for(String type : typeList)
				typeName.add(CustomerTypeQuery.me().findById(type).getStr("name"));

			setAttr("sellerCustomer", sellerCustomer);
			setAttr("cTypeList",typeList);
//			setAttr("cTypeName", org.apache.shiro.util.StringUtils.join(typeName.iterator(),","));
			setAttr("cTypeName", Joiner.on(",").join(typeName.iterator()));
		}
		
		if (StrKit.notBlank(taskId)) {
			
		}

		render("customer_detail.html");

	}

	public void getCustomerType(){
		User user = getUser();
		String selectDataArea = getUserDeptDataArea(user.getDataArea());

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(selectDataArea));
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();

		for(CustomerType customerType : customerTypeList)
		{
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("customerTypeList", customerTypeList2);
		renderJson(data);
	}

	//@Before(Tx.class)
	public void update() {
		
		boolean updated = false;
		Map<String, Object> map = new HashMap<>();
		List<ImageJson> list = Lists.newArrayList();
		
		Customer customer = getModel(Customer.class);
		SellerCustomer sellerCustomer = getModel(SellerCustomer.class);
		
		String picJson = getPara("pic");
		String areaCode = getPara("areaCode");
		String areaName = getPara("areaName");
		String customerTypeIds = getPara("customerTypeIds", "");
		
		System.err.println(customerTypeIds);
		
		List<String> custTypeList = Splitter.on(",")
				.trimResults()
				.omitEmptyStrings()
				.splitToList(customerTypeIds);
		
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
		
		CustomerVO temp = new CustomerVO();
		temp.setAreaCode(areaCode);
		temp.setAreaName(areaName);
		temp.setCustTypeList(custTypeList);
		temp.setContact(customer.getContact());
		
		temp.setMobile(customer.getMobile());
		temp.setAddress(customer.getAddress());
		temp.setNickname(sellerCustomer.getNickname());
		temp.setCustomerName(customer.getCustomerName());
		
		temp.setImageListStore(JSON.toJSONString(list));
		map.put("customerVO", temp);
		
		if (sellerCustomer != null) 
			updated = startProcessInstance(sellerCustomer.getId(), map);

		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	@Before(Tx.class)
	public void enable() {

		//获取的前台数据
		String id = getPara("id");
		Integer isEnabled = getParaToInt("isEnabled");

		Map<String, Object> map = new HashMap<>();
		map.put("sellerCustomerId", id);
		map.put("isEnabled", isEnabled);

		boolean updated = false;
		if (id != null) updated = startProcessInstance(id, map);

		//用来处理审核
		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	private User getUser(){
		User user = UserQuery.me().findById("53c87914b1ea416681701ed01c05cd21");
		return user;
	}

	private String getUserDeptDataArea(String dataArea) {
		if (dataArea.length() % 3 != 0) {
			return DataAreaUtil.getUserDeptDataArea(dataArea);
		} else return dataArea;
	}

	public void auditSellerCustomerComplete() {

		String taskId = getPara("taskId");
		String comment = getPara("comment");

		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, null);

		Customer customer = (Customer)workflowService.getTaskVariableByTaskId(taskId, "customer");
		SellerCustomer sellerCustomer = (SellerCustomer) workflowService.getTaskVariableByTaskId(taskId,"sellerCustomer");

		boolean updated = true;
		String sellerId = "7c1818ed7e3743d4829db05e653178bb";
		User user = getUser();

		Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customer.getCustomerName(),
				customer.getMobile());

		customer.setCountryName(customer.getProvName().split(" ")[2]);
		customer.setCityName(customer.getProvName().split(" ")[1]);
		customer.setProvName(customer.getProvName().split(" ")[0]);

		if (persiste == null) {
			customer.set("id", StrKit.getRandomUUID());
			customer.set("create_date", new Date());
			updated = updated && customer.save();
		} else {
			customer.set("id", persiste.getId());
			customer.set("modify_date", new Date());
			updated = updated && customer.update();
		}

		sellerCustomer.set("seller_id", sellerId);
		sellerCustomer.set("customer_id", customer.getId());
		sellerCustomer.set("is_enabled", 1);
		sellerCustomer.set("is_archive", 1);

		SellerBrandQuery.me().findBySellerId(sellerId);

		if (StrKit.isBlank(sellerCustomer.getId())) {
			sellerCustomer.set("id", StrKit.getRandomUUID());
			sellerCustomer.set("create_date", new Date());
			updated = updated && sellerCustomer.save();
		} else {
			String dept_dataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());
			Department dept =  DepartmentQuery.me().findByDataArea(dept_dataArea);
			sellerCustomer.set("data_area", dept_dataArea);
			sellerCustomer.set("dept_id", dept.getId());
			sellerCustomer.set("modify_date", new Date());
			updated = updated && sellerCustomer.update();
		}

		CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomer.getId());

		String[] customerTypes = getPara("customer_type").toString().split(",");
		for (String custTypeName : customerTypes) {
			CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
			ccType.setSellerCustomerId(sellerCustomer.getId());
			ccType.setCustomerTypeId(CustomerTypeQuery.me().findIdByName(custTypeName, getUserDeptDataArea(user.getDataArea())));
			updated = updated && ccType.save();
		}

		if (updated)
			renderAjaxResultForSuccess("客户修改审核成功");
		else
			renderAjaxResultForError("客户修改审核失败");
	}

	public void auditSellerCustomerEnableComplete() {

		String taskId = getPara("taskId");
		String comment = getPara("comment");

		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, null);


		String id = (String) workflowService.getTaskVariableByTaskId(taskId, "sellerCustomerId");
		int isEnabled = (Integer) workflowService.getTaskVariableByTaskId(taskId, "isEnabled");

		if (SellerCustomerQuery.me().enable(id, isEnabled)) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}

	private boolean startProcessInstance(String customerId, Map<String, Object> param) {

		//用来处理审核
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(customerId);
		boolean updated = true;

		if (sellerCustomer != null) {

			User user = getUser();
			
//			Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
			Boolean isCustomerAudit = true;

			if (isCustomerAudit != null && isCustomerAudit.booleanValue()) {

				User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());

				if (manager == null) {
					renderError(500);
					return false;
				}

				String defKey = "_customer_audit";

				param.put("applyUsername", user.getUsername());
				param.put("manager", manager.getUsername());

				WorkFlowService workflow = new WorkFlowService();
				String procInstId = workflow.startProcess(customerId, defKey, param);

				sellerCustomer.setProcDefKey(defKey);
				sellerCustomer.setProcInstId(procInstId);
				sellerCustomer.setStatus(SellerCustomer.CUSTOMER_AUDIT);
				updated = sellerCustomer.update();

				if (updated) {

					Kv kv = Kv.create();

					WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(defKey);

					kv.set("touser", manager.getWechatOpenId());
					kv.set("templateId", messageTemplate.getTemplateId());
					kv.set("customerName", sellerCustomer.getCustomer().getCustomerName());
					kv.set("submit", user.getRealname());

					kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
					kv.set("status", "待审核");

					MessageKit.sendMessage(Actions.NotifyMessage.CUSTOMER_AUDIT_MESSAGE, kv);
				}

			} else {
				updated = sellerCustomer.update();
			}
		}

		return updated;
	}
	
	public void complete() {
		
//		Customer customer = getModel(Customer.class);
//		SellerCustomer sellerCustomer = getModel(SellerCustomer.class);
		
		String taskId = getPara("taskId");
		String comment = getPara("comment");
		
		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, null);
		
		// 处理对应的业务逻辑
		Customer temp = (Customer) workflowService.getTaskVariableByTaskId(taskId, "tempCustomer");
		System.out.println(JSON.toJSON(temp));
		
		renderAjaxResultForSuccess();
	}
}
