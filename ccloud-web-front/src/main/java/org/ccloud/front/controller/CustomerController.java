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
import org.ccloud.model.compare.BeanCompareUtils;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.ccloud.model.vo.CustomerVO;
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

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Page<Record> customerList = SellerCustomerQuery.me().findByUserTypeForApp(getPageNumber(), getPageSize(), getUserIdList(user), getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

		setAttr("customerList", customerList);

		render("customer.html");
	}

	public void getCustomerRegionAndType() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

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
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

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
			html.append("				<a class=\"weui-flex__item relative\" href=\"/customer/edit?sellerCustomerId=" + customer.getStr("sellerCustomerId") + "\">\n");
			html.append("					<i class=\"icon-chevron-right gray\"></i>\n");
			html.append("				</a>\n");
			html.append("			</div>\n");
			html.append("		</div>\n");
			html.append("	</div>\n");
			html.append("	<hr />\n");
			html.append("	<div class=\"operate-btn\">\n");
			html.append("		<div class=\"button white-button fl\" href=\"/visitAdd\">客户拜访</div>\n");
			html.append("		<div class=\"button blue-button fr\" href=\"/product\" onclick=\"newOrder({customerName:'" + customer.getStr("customer_name") + "',\n" +
					"                                                                                            sellerCustomerId:'" + customer.getStr("sellerCustomerId") + "',\n" +
					"                                                                                            contact:'" + customer.getStr("contact") + "',\n" +
					"                                                                                            mobile:'" + customer.getStr("mobile") + "',\n" +
					"                                                                                            address:'" + customer.getStr("address") + "'})\" >下订单</div>\n");
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

	public void refreshHistoryOrder() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Page<Record> orderList = new Page<>();
		orderList = SalesOrderQuery.me().findBySellerCustomerId(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("sellerCustomerId"), getUserDeptDataArea(user.getDataArea()));

		StringBuilder html = new StringBuilder();
		for(Record order : orderList.getList()){
			order.set("statusName", getStatusName(order.getInt("status")));
			order.set("receive_Name", getReceiveName(order.getInt("receive_type")));
			html.append("<div class=\"weui-panel weui-panel_access\">\n" +
					"                        <a href=\"/order/orderDetail?orderId=" + order.getStr("id") + "\">\n" +
					"                        <div class=\"ft14\">\n");
			if (order.get("receive_type").toString().equals("0")) html.append("                                <span class=\"tag\">" + order.getStr("receive_Name") + "</span>\n");
			html.append(order.getStr("order_sn") + "\n" +
					"                            <span class=\"fr blue\">" + order.getStr("statusName") + "</span>\n" +
					"                        </div>\n" +
					"                        <div class=\"gray\">\n" +
					"                            <p>数量：" + order.getStr("total_count") + "件\n" +
					"                                <span class=\"fr\">时间：" + order.get("create_date").toString() + "</span>\n" +
					"                            </p>\n" +
					"                            <p>金额：" + order.get("total_amount").toString() + "" +
					"							 <span class=\"fr\">业务员：" + order.getStr("realname") + "</span>" +
					"							 </p>\n" +
					"                        </div>\n" +
					"                        </a>\n" +
					"                        <hr>\n" +
					"                        <div>\n");
			if(order.get("status").toString().equals("0")) html.append("<div class=\"button blue-button fl\">撤销</div>\n" +
					"                            <div class=\"button white-button fr\">再次购买</div>\n" +
					"                        </div>\n" +
					"                    </div>");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", orderList.getTotalRow());
		map.put("totalPage", orderList.getTotalPage());
		renderJson(map);
	}

	public void historyOrder() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Page<Record> orderList = SalesOrderQuery.me().findBySellerCustomerId(getPageNumber(), getPageSize(), getPara("sellerCustomerId"), getUserDeptDataArea(user.getDataArea()));

		for(Record record : orderList.getList()){
			record.set("statusName", getStatusName(record.getInt("status")));
			record.set("receive_Name", getReceiveName(record.getInt("receive_type")));
		}

		setAttr("orderList", orderList);
		setAttr("sellerCustomerId", getPara("sellerCustomerId"));
		setAttr("customerName", getPara("customerName"));

		render("customer_historyOrder.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void edit() {
		
		String id = getPara("sellerCustomerId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		if (StrKit.notBlank(id)) {
			String selectDataArea = getUserDeptDataArea(user.getDataArea());
			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
			String dealerDataArea = DataAreaUtil.getUserDealerDataArea(selectDataArea);
			List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeIdListBySellerCustomerId(id, dealerDataArea + "%");

			List<String> typeName = new ArrayList<>();
			for(String type : typeList)
				typeName.add(CustomerTypeQuery.me().findById(type).getStr("name"));

			setAttr("sellerCustomer", sellerCustomer);
			setAttr("cTypeList",typeList);
			setAttr("cTypeListStr", Joiner.on(",").join(typeList.iterator()));
			setAttr("cTypeName", Joiner.on(",").join(typeName.iterator()));
		}
		
		render("customer_edit.html");
	}

	public void getCustomerType(){
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

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

	@Before(Tx.class)
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

		String custTypeNames = getPara("customer_type");

		List<String> custTypeList = Splitter.on(",")
				.trimResults()
				.omitEmptyStrings()
				.splitToList(customerTypeIds);

		List<String> custTypeNameList = Splitter.on(",")
				.trimResults()
				.omitEmptyStrings()
				.splitToList(custTypeNames);

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
			temp.setCustTypeNameList(custTypeNameList);
			temp.setContact(customer.getContact());
			
			temp.setMobile(customer.getMobile());
			temp.setAddress(customer.getAddress());
			temp.setNickname(sellerCustomer.getNickname());
			temp.setCustomerName(customer.getCustomerName());
			
			temp.setImageListStore(JSON.toJSONString(list));
			map.put("customerVO", temp);
//			开始审核流程
			updated = startProcess(sellerCustomer.getId(), map);

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
			sellerCustomer.setImageListStore(JSON.toJSONString(list));
			
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
		


//		MessageKit.sendMessage(action, map);

		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	public void review() {
		String id = getPara("id");
		String taskId = getPara("taskId");

		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}

		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
		setAttr("sellerCustomer", sellerCustomer);
		setAttr("taskId", taskId);

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = DataAreaUtil.getUserDealerDataArea(selectDataArea);
		List<String> custTypeNameList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(id, dealerDataArea);
		String custTypeNames = Joiner.on(",").skipNulls().join(custTypeNameList);
		setAttr("custTypeNames", custTypeNames);

		WorkFlowService workflowService = new WorkFlowService();
		Object customerVO = workflowService.getTaskVariableByTaskId(taskId, "customerVO");
		Object applyer = workflowService.getTaskVariableByTaskId(taskId, "applyUsername");
		if (applyer != null) {
			User user = UserQuery.me().findUserByUsername(applyer.toString());
			setAttr("applyer", user);
		}

		if (customerVO != null) {
			CustomerVO src = new CustomerVO();
			CustomerVO dest = (CustomerVO) customerVO;

			src.setNickname(sellerCustomer.getNickname());
			src.setSellerCustomerId(sellerCustomer.getId());
			src.setCustomerId(sellerCustomer.getCustomerId());

			src.setContact(sellerCustomer.getStr("contact"));
			src.setMobile(sellerCustomer.getStr("mobile"));
			src.setAddress(sellerCustomer.getStr("address"));
			src.setCustomerName(sellerCustomer.getStr("customer_name"));

			String areaName = Joiner.on(",").skipNulls()
				.join(sellerCustomer.getStr("prov_name")
					, sellerCustomer.getStr("city_name")
					, sellerCustomer.getStr("country_name"));
			src.setAreaName(areaName);

			String areaCode = Joiner.on(",").skipNulls()
				.join(sellerCustomer.getStr("prov_code")
					, sellerCustomer.getStr("city_code")
					, sellerCustomer.getStr("country_code"));
			src.setAreaCode(areaCode);

			src.setCustTypeNameList(CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(id, DataAreaUtil.getUserDealerDataArea(selectDataArea)));


			List<String> diffAttrList = BeanCompareUtils.contrastObj(src, dest);
			setAttr("diffAttrList", diffAttrList);
		} else {
			List<String> diffAttrList = new ArrayList<>();
			diffAttrList.add("申请停用");
			setAttr("diffAttrList", diffAttrList);

	}

		render("customer_review.html");
	}


	public void enable() {

		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");
		if(StrKit.notBlank(id)) {

			boolean updated = startProcess(id, new HashMap<String, Object>());

			if (updated) {
				renderAjaxResultForSuccess("操作成功");
			} else {
				renderAjaxResultForError("操作失败");
			}
		}else {
			renderError(500);
		}
	}

	@Before(Tx.class)
	public void complete() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		String taskId = getPara("taskId");
		Integer status = getParaToInt("status");
		String sellerCustomerId = getPara("id");
		String comment;

		if(status == 1) comment = "批准";
		else comment = "拒绝";

		SellerCustomer sellerCustomer = new SellerCustomer();

		boolean updated = true;

		sellerCustomer = SellerCustomerQuery.me().findById(sellerCustomerId);
		sellerCustomer.setStatus(status == 1 ? SellerCustomer.CUSTOMER_NORMAL : SellerCustomer.CUSTOMER_REJECT);

		WorkFlowService workFlowService = new WorkFlowService();

		if (status == 1) {

			String sellerId = getSessionAttr("sellerId");
			CustomerVO customerVO = (CustomerVO) workFlowService.getTaskVariableByTaskId(taskId,"customerVO");

			if(customerVO != null) {

				Customer customer = CustomerQuery.me().findById(sellerCustomer.getCustomerId());
				Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customerVO.getCustomerName(), customerVO.getMobile());

				if (StrKit.notBlank(customerVO.getAreaCode())) {

					List<String> areaCodeList = Splitter.on(",")
							.omitEmptyStrings()
							.trimResults()
							.splitToList(customerVO.getAreaCode());

					List<String> areaNameList = Splitter.on(",")
							.omitEmptyStrings()
							.trimResults()
							.splitToList(customerVO.getAreaName());

					if (areaCodeList.size() == 3 && areaNameList.size() == 3) {

						customer.setProvCode(areaCodeList.get(0));
						customer.setProvName(areaNameList.get(0));
						customer.setCityCode(areaCodeList.get(1));
						customer.setCityName(areaNameList.get(1));

						customer.setCountryCode(areaCodeList.get(2));
						customer.setCountryName(areaNameList.get(2));
					}
				}

				customer.setContact(customerVO.getContact());
				customer.setMobile(customerVO.getMobile());
				customer.setAddress(customerVO.getAddress());

				if (persiste != null) {
					customer.setId(persiste.getId());
				} else customer.setId(null);
				updated = updated && customer.saveOrUpdate();

				sellerCustomer.setNickname(customerVO.getNickname());

				if (customerVO.getCustTypeList() != null || customerVO.getCustTypeList().size() != 0)
					sellerCustomer.setCustomerTypeIds(Joiner.on(",").join(customerVO.getCustTypeList().iterator()));

				if (StrKit.notBlank(customerVO.getImageListStore()))
					sellerCustomer.setImageListStore(customerVO.getImageListStore());

				sellerCustomer.setSellerId(sellerId);
				sellerCustomer.setCustomerId(customer.getId());
				sellerCustomer.setIsEnabled(1);
				sellerCustomer.setIsArchive(1);
				sellerCustomer.setImageListStore(customerVO.getImageListStore());

				String deptDataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());
				Department department =  DepartmentQuery.me().findByDataArea(deptDataArea);
				sellerCustomer.setDataArea(deptDataArea);
				sellerCustomer.setDeptId(department.getId());

				updated = updated && sellerCustomer.saveOrUpdate();

				sellerCustomerId = sellerCustomer.getId();

				if(customerVO.getCustTypeList() != null || customerVO.getCustTypeList().size() != 0) {

					CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);

					String[] customerTypes = sellerCustomer.getCustomerTypeIds().split(",");

					for (String custType : customerTypes) {
						CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
						ccType.setSellerCustomerId(sellerCustomerId);
						ccType.setCustomerTypeId(custType);
						updated = updated && ccType.save();
					}
				}

			}else{
				sellerCustomer.setIsEnabled(0);
				updated = updated && sellerCustomer.saveOrUpdate();
			}
		}

		workFlowService.completeTask(taskId, comment, null);

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

	private String getStatusName (int statusCode) {
		if (statusCode == Consts.SALES_ORDER_STATUS_PASS) return "已审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_DEFAULT) return "待审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_CANCEL) return "取消";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT) return "部分出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT_CLOSE) return "部分出库-订单关闭";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT) return "全部出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT_CLOSE) return "全部出库-订单关闭";
		return "无";
	}

	private String getReceiveName(int receiveCode) {
		if (receiveCode == Consts.SALES_ORDER_RECEIVE_TYPE_ACCOUNT) return "账期";
		if (receiveCode == Consts.SALES_ORDER_RECEIVE_TYPE_CASH) return "现金";
		return "";
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
