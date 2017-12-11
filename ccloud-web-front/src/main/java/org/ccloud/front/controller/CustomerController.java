package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Department;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.OptionQuery;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Created by WT on 2017/11/29.
 */
@RouterMapping(url = "/customer")
public class CustomerController extends BaseFrontController {

	public void index() {

		//User user = getUser();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Page<Record> customerList = SellerCustomerQuery.me().findByUserTypeForApp(getPageNumber(), getPageSize(), getUserIdList(user), getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

		setAttr("customerList", customerList);

		render("customer.html");
	}

	public void getCustomerRegionAndType() {

		User user = getUser();

		List<Record> userList = UserQuery.me().findNextLevelsUserList(user.getDataArea());
		List<Map<String, Object>> region = new ArrayList<>();
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		region.add(all);
		for(Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			region.add(item);
		}

		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(getUserDeptDataArea(user.getDataArea()));
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();
		customerTypeList2.add(all);

		for(CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		Map<String, List<Map<String, Object>>> data = ImmutableMap.of("region", region, "customerType", customerTypeList2);
		renderJson(data);
	}

	public void refresh() {
		User user = getUser();

		Page<Record> customerList = new Page<>();
		if (StrKit.notBlank(getPara("region"))) {
			Object[] region = {getPara("region")};
			 customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), region, getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));
		} else customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getUserIdList(user), getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

		StringBuilder html = new StringBuilder();
		for (Record customer : customerList.getList())
		{
			html.append("<div class=\"weui-panel weui-panel_access\">\n");
			html.append("	<div class=\"weui-flex\">\n");
			html.append("		<div class=\"weui-flex__item customer-info\">\n");
			html.append("			<p class=\"ft14\">" + customer.getStr("customer_name") + "</p>\n");
			html.append("			<p class=\"gray\">" + customer.getStr("contact") + "/" + customer.getStr("mobile")+ "</p>\n");
			html.append("		</div>\n");
			html.append("		<div class=\"weui-flex__item customer-href\">\n");
			html.append("			<div class=\"weui-flex\">\n");
			html.append("				<a href=\"tel:\"" + customer.getStr("mobile") + " class=\"weui-flex__item\">\n");
			html.append("					<p><i class=\"icon-phone green\"></i></p>\n");
			html.append("					<p>电话</p>\n");
			html.append("				</a>\n");
			html.append("				<a class=\"weui-flex__item\" href=\"/historyOrder?sellerCustomerId=" + customer.getStr("sellerCustomerId") + "&customerName=" + customer.getStr("customer_name") + "\">\n");
			html.append("					<p><i class=\"icon-file-text-o blue\"></i></p>\n");
			html.append("					<p>订单</p>\n");
			html.append("				</a>\n");
			html.append("				<a class=\"weui-flex__item\" href=\"/visitAdd\">\n");
			html.append("					<p><i class=\"icon-paw\" style=\"color:#ff9800\"></i></p>\n");
			html.append("					<p>拜访</p>\n");
			html.append("				</a>\n");
			html.append("				<a class=\"weui-flex__item relative\" href=\"/customerDetail\">\n");
			html.append("					<i class=\"icon-chevron-right gray href=\"/customerDetail?sellerCustomerId=" + customer.getStr("sellerCustomerId") + "\"></i>\n");
			html.append("				</a>\n");
			html.append("			</div>\n");
			html.append("		</div>\n");
			html.append("	</div>\n");
			html.append("	<hr />\n");
			html.append("	<div class=\"weui-flex space-between\">\n");
			html.append("		<div class=\"button blue-button\">下订单</div>\n");
			html.append("		<div class=\"button blue-button\">客户拜访</div>\n");
			html.append("	</div>\n");
			html.append("	<p class=\"gray\">\n");
			html.append("		<span class=\"icon-map-marker ft16 green\"></span>\n");
			html.append(		customer.getStr("prov_name") + " " + customer.getStr("city_name") + " " + customer.getStr("country_name") + " " + customer.getStr("address") + "\n");
			html.append("	</p>\n");
			html.append("</div>\n" );
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", customerList.getTotalRow());
		map.put("totalPage", customerList.getTotalPage());
		renderJson(map);
	}
	
	public void edit() {
		
		String id = getPara("id");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		if (StrKit.notBlank(id)) {
			String selectDataArea = getUserDeptDataArea(user.getDataArea());
			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
			String dealerDataArea = DataAreaUtil.getUserDealerDataArea(selectDataArea);
			List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeListBySellerCustomerId(id, dealerDataArea + "%");

			List<String> typeName = new ArrayList<>();
			for(String type : typeList)
				typeName.add(CustomerTypeQuery.me().findById(type).getStr("name"));

			setAttr("sellerCustomer", sellerCustomer);
			setAttr("cTypeList",typeList);
			setAttr("cTypeName", Joiner.on(",").join(typeName.iterator()));
		}
		
		render("customer_edit.html");
	}
	
	public void save() {
		
		boolean updated = false;
		Map<String, Object> map = Maps.newHashMap();
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
		
		if (sellerCustomer != null && StrKit.notBlank(sellerCustomer.getId())) {
			
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
			
		} else {
			// 查看客户库是否存在这个客户
			Customer persist = CustomerQuery.me().findByCustomerNameAndMobile(customer.getCustomerName(), customer.getMobile());
			
			List<String> areaCodeList = Splitter.on(",")
					.omitEmptyStrings()
					.trimResults()
					.splitToList(areaCode);
			
			List<String> areaNameList = Splitter.on(",")
					.omitEmptyStrings()
					.trimResults()
					.splitToList(areaName);
			
			if (areaCodeList.size() == 3 && areaNameList.size() == 3) {
				
				customer.setProvCode(areaCodeList.get(0));
				customer.setProvName(areaNameList.get(0));
				customer.setCityCode(areaCodeList.get(1));
				customer.setCityName(areaNameList.get(1));
				
				customer.setCountryCode(areaCodeList.get(2));
				customer.setCountryName(areaNameList.get(2));
			}
			
			if (persist != null) {
				customer.setId(persist.getId());
			}
			
			updated = customer.saveOrUpdate();
			
			if (!updated) {
				renderError(500);
				return ;
			}
			
			String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			
			sellerCustomer.setSellerId(sellerId);
			sellerCustomer.setCustomerId(customer.getId());
			sellerCustomer.setIsEnabled(1);
			sellerCustomer.setIsArchive(1);
			
			sellerCustomer.setCustomerTypeIds(customerTypeIds);
			sellerCustomer.setSubType("100301");
			sellerCustomer.setCustomerKind("100401");
			
			String deptDataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());
			Department department =  DepartmentQuery.me().findByDataArea(deptDataArea);
			sellerCustomer.setDataArea(deptDataArea);
			sellerCustomer.setDeptId(department.getId());
			
			updated = sellerCustomer.saveOrUpdate();
			
			if (!updated) {
				renderError(500);
				return ;
			}
			
			CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomer.getId());
	
			for (String custTypeId : custTypeList) {
				CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
				ccType.setSellerCustomerId(sellerCustomer.getId());
				ccType.setCustomerTypeId(custTypeId);
				ccType.save();
			}
			
			UserJoinCustomer userJoinCustomer = new UserJoinCustomer();
	
			userJoinCustomer.setSellerCustomerId(sellerCustomer.getId());
			userJoinCustomer.setUserId(user.getId());
			userJoinCustomer.setDeptId(user.getDepartmentId());
			userJoinCustomer.setDataArea(user.getDataArea());
	
			updated = userJoinCustomer.save();
		}
		
		if (sellerCustomer != null) 
			updated = startProcess(sellerCustomer.getId(), map);
//		MessageKit.sendMessage(action, map);

		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}
	
	private boolean startProcess(String customerId, Map<String, Object> param) {
		
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(customerId);
		boolean isUpdated = true;
//		Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
		Boolean isCustomerAudit = true;
		
		if (sellerCustomer != null) {
			if (isCustomerAudit != null && isCustomerAudit.booleanValue()) {
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
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
				isUpdated = sellerCustomer.update();
				
				if (isUpdated) {
	
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
				isUpdated = sellerCustomer.update();
			}
		}
		return isUpdated;
	}

	private User getUser(){
		User user = UserQuery.me().findById("ce05e9008ece42bc986e7bc41edcf4a0");
		return user;
	}

	private String getUserDeptDataArea(String dataArea) {
		if (dataArea.length() % 3 != 0) {
			return DataAreaUtil.getUserDeptDataArea(dataArea);
		} else {
			return dataArea;
		}
	}

	private Object[] getUserIdList(User user) {
		List<Record> userList = UserQuery.me().findNextLevelsUserList(user.getDataArea());
		if (userList.size() == 0) return null;

		Object[] userIdList = new Object[userList.size()];
		for (int i = 0; i < userList.size(); i++) {
			userIdList[i] = userList.get(i).getStr("id");
		}
		return userIdList;
	}
	
	@Before(Tx.class)
	public void update() {
		
		boolean updated = false;
		String id = getPara("id");
		SellerCustomer customer = SellerCustomerQuery.me().findById(id);
		
		
		if (customer != null) {
			
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
			isCustomerAudit = true;
			if (isCustomerAudit != null && isCustomerAudit.booleanValue()) {
			
				User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
				
				if (manager == null) {
					renderError(500);
					return ;
				}
				
				String defKey = "_customer_audit";
				Map<String, Object> param = Maps.newHashMap();
//				param.put("apply", user.getUsername());
				param.put("manager", manager.getUsername());
				
				WorkFlowService workflow = new WorkFlowService();
				String procInstId = workflow.startProcess(customer.getId(), defKey, param);
				
				customer.setProcDefKey(defKey);
				customer.setProcInstId(procInstId);
				customer.setStatus(SellerCustomer.CUSTOMER_AUDIT);
				updated = customer.update();
				
				if (updated) {
				
					Kv kv = Kv.create();
		
					WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_audit");
					
					kv.set("touser", manager.getWechatOpenId());
					kv.set("templateId", messageTemplate.getTemplateId());
					kv.set("customerName", customer.getCustomer().getCustomerName());
					kv.set("submit", user.getRealname());
		
					kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
					kv.set("status", "待审核");
		
					MessageKit.sendMessage(Actions.NotifyMessage.CUSTOMER_AUDIT_MESSAGE, kv);
				}
			
			} else {
				updated = customer.update();
			}
		}
		
		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}
}
