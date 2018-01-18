package org.ccloud.front.controller;

import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.compare.BeanCompareUtils;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.CustomerVO;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.ImageUtils;
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
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Created by WT on 2017/11/29.
 */
@RouterMapping(url = "/customer")
@RequiresPermissions(value = { "/admin/sellerCustomer", "/admin/dealer/all" }, logical = Logical.OR)
public class CustomerController extends BaseFrontController {

	@Before(WechatJSSDKInterceptor.class)
	public void index() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
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

		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(dataArea);
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();
		customerTypeList2.add(all);

		for(CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		List<Dict> nearByList = DictQuery.me().findDictByType("area_coverage");
		List<Map<String, Object>> nearBy = new ArrayList<>();
		for(Dict dict : nearByList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", dict.get("name"));
			item.put("value", dict.get("value"));
			nearBy.add(item);
		}

		setAttr("region", JSON.toJSON(region));
		setAttr("customerType", JSON.toJSON(customerTypeList2));
		setAttr("searchArea", JSON.toJSON(nearBy));

		String history = getPara("history");
		setAttr("history", history);		
		render("customer.html");
	}

	public void refresh() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";
		boolean visitAdd = SecurityUtils.getSubject().isPermitted("/admin/customerVisit/add");
		boolean salesOrderAdd = SecurityUtils.getSubject().isPermitted("/admin/salesOrder/add");
		boolean salesOrder = SecurityUtils.getSubject().isPermitted("/admin/salesOrder");
		boolean visit = SecurityUtils.getSubject().isPermitted("/admin/customerVisit");
		
		Page<Record> customerList = new Page<>();
		if (StrKit.notBlank(getPara("region"))) {
			String dataArea = UserQuery.me().findById(getPara("region")).getDataArea();
			customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), dataArea, getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));
		} else customerList = SellerCustomerQuery.me().findByUserTypeForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), selectDataArea, getPara("customerType"), getPara("isOrdered"), getPara("searchKey"));

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
			html.append("				<a href=\"tel:" + customer.getStr("mobile") + "\" class=\"weui-flex__item\">\n");
			html.append("					<p><i class=\"icon-phone green\"></i></p>\n");
			html.append("					<p>电话</p>\n");
			html.append("				</a>\n");
			if (salesOrder) {
				html.append("				<a class=\"weui-flex__item\" href=\"/customer/historyOrder?sellerCustomerId=" + customer.getStr("sellerCustomerId") + "&customerName=" + customer.getStr("customer_name") + "\">\n");
				html.append("					<p><i class=\"icon-file-text-o blue\"></i></p>\n");
				html.append("					<p>订单</p>\n");
				html.append("				</a>\n");
			}
			if (visit) {
				html.append("				<a class=\"weui-flex__item\" href=\"/customerVisit/one?id=" + customer.getStr("sellerCustomerId") +"&name=" + customer.getStr("customer_name") + "\">\n");
				html.append("					<p><i class=\"icon-paw\" style=\"color:#ff9800\"></i></p>\n");
				html.append("					<p>拜访</p>\n");
				html.append("				</a>\n");
			}
			html.append("				<a class=\"weui-flex__item relative\" href=\"/customer/edit?sellerCustomerId=" + customer.getStr("sellerCustomerId") + "\">\n");
			html.append("					<i class=\"icon-chevron-right gray\"></i>\n");
			html.append("				</a>\n");
			html.append("			</div>\n");
			html.append("		</div>\n");
			html.append("	</div>\n");
			html.append("	<hr />\n");
			if (visitAdd || salesOrderAdd) {
				html.append("	<div class=\"operate-btn\">\n");
				if (visitAdd) {
					html.append("		<div class=\"button white-button fl border-1px\" onclick=\"newVisit({customerName:'" + customer.getStr("customer_name") + "',\n" +
							"                                                                     sellerCustomerId:'" + customer.getStr("sellerCustomerId") + "',\n" +
							"                                                                     contact:'" + customer.getStr("contact") + "',\n" +
							"                                                                     mobile:'" + customer.getStr("mobile") + "',\n" +
							"                                                                     address:'" + customer.getStr("address") + "'})\">客户拜访</div>\n");				
				}
				if (salesOrderAdd) {
					html.append("		<div class=\"button red-button fr\" onclick=\"newOrder({customerName:'" + customer.getStr("customer_name") + "',\n" +
							"                                                                    sellerCustomerId:'" + customer.getStr("sellerCustomerId") + "',\n" +
							"                                                                    contact:'" + customer.getStr("contact") + "',\n" +
							"                                                                    mobile:'" + customer.getStr("mobile") + "',\n" +
							"                                                                    address:'" + customer.getStr("address") + "'})\" >下订单</div>\n");				
				}
				html.append("	</div>\n");
			}
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

	public void getAreaCustomer() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Double dist = 100d;
		String lon = getPara("lon");
		String lat = getPara("lat");

		if(StrKit.notBlank(getPara("searchArea"))) {
			dist = Double.valueOf(getPara("searchArea", "100")).doubleValue();

			BigDecimal latitude = new BigDecimal(lat);
			BigDecimal longitude = new BigDecimal(lon);

			List<Map<String, Object>> customerList = SellerCustomerQuery.me().queryCustomerNearby(dist, longitude, latitude, user.getId());
			Map<String, Object> map = new HashMap<>();

			StringBuilder html = new StringBuilder();
			for (Map<String, Object> customer : customerList)
			{
				html.append("<div class=\"weui-panel weui-panel_access\">\n");
				html.append("	<div class=\"weui-flex\">\n");
				html.append("		<div class=\"weui-flex__item customer-info\">\n");
				html.append("			<p class=\"ft14\">" + customer.get("customer_name").toString() + "</p>\n");
				html.append("			<p class=\"gray\">" + customer.get("contact").toString() + "/" + customer.get("mobile").toString() + "</p>\n");
				html.append("		</div>\n");
				html.append("		<div class=\"weui-flex__item customer-href\">\n");
				html.append("			<div class=\"weui-flex\">\n");
				html.append("				<a href=\"tel:\"" + customer.get("mobile").toString() + " class=\"weui-flex__item\">\n");
				html.append("					<p><i class=\"icon-phone green\"></i></p>\n");
				html.append("					<p>电话</p>\n");
				html.append("				</a>\n");
				html.append("				<a class=\"weui-flex__item\" href=\"/customer/historyOrder?sellerCustomerId=" + customer.get("id").toString() + "&customerName=" + customer.get("customer_name").toString() + "\">\n");
				html.append("					<p><i class=\"icon-file-text-o blue\"></i></p>\n");
				html.append("					<p>订单</p>\n");
				html.append("				</a>\n");
				html.append("				<a class=\"weui-flex__item\" href=\"/customerVisit/one?id=" + customer.get("id").toString() +"&name=" + customer.get("customer_name").toString() + "\">\n");
				html.append("					<p><i class=\"icon-paw\" style=\"color:#ff9800\"></i></p>\n");
				html.append("					<p>拜访</p>\n");
				html.append("				</a>\n");
				html.append("				<a class=\"weui-flex__item relative\" href=\"/customer/edit?sellerCustomerId=" + customer.get("id").toString() + "\">\n");
				html.append("					<i class=\"icon-chevron-right gray\"></i>\n");
				html.append("				</a>\n");
				html.append("			</div>\n");
				html.append("		</div>\n");
				html.append("	</div>\n");
				html.append("	<hr />\n");
				html.append("	<div class=\"operate-btn\">\n");
				html.append("		<div class=\"button white-button fl border-1px\" onclick=\"newVisit({customerName:'" + customer.get("customer_name").toString() + "',\n" +
						"                                                                     sellerCustomerId:'" + customer.get("id").toString() + "',\n" +
						"                                                                     contact:'" + customer.get("contact").toString() + "',\n" +
						"                                                                     mobile:'" + customer.get("mobile").toString() + "',\n" +
						"                                                                     address:'" + customer.get("address").toString() + "'})\">客户拜访</div>\n");
				html.append("		<div class=\"button red-button fr\" onclick=\"newOrder({customerName:'" + customer.get("customer_name").toString() + "',\n" +
						"                                                                    sellerCustomerId:'" + customer.get("id").toString() + "',\n" +
						"                                                                    contact:'" + customer.get("contact").toString() + "',\n" +
						"                                                                    mobile:'" + customer.get("mobile").toString() + "',\n" +
						"                                                                    address:'" + customer.get("address").toString() + "'})\" >下订单</div>\n");
				html.append("	</div>\n");
				html.append("	<p class=\"gray\">\n");
				html.append("		<span class=\"icon-map-marker ft16 green\"></span>\n");
				html.append(		customer.get("prov_name").toString() + " " + customer.get("city_name").toString() + " " + customer.get("country_name").toString() + " " + customer.get("address").toString() + "\n");
				html.append("	</p>\n");
				html.append("</div>\n" );
			}

			map.put("html", html.toString());
			map.put("totalRow", 9);
			map.put("totalPage", 1);
			renderJson(map);
			return;
		}
	}

	public void refreshHistoryOrder() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

		Page<Record> orderList = new Page<>();
		orderList = SalesOrderQuery.me().findBySellerCustomerId(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("sellerCustomerId"), selectDataArea);

		DecimalFormat df   = new DecimalFormat("######0.00");
		StringBuilder html = new StringBuilder();
		for(Record order : orderList.getList()){
			order.set("statusName", getStatusName(order.getInt("status")));
			order.set("receive_Name", getReceiveName(order.getInt("receive_type")));
			html.append("<div class=\"weui-panel weui-panel_access\">\n" +
					"                        <a href=\"/order/orderDetail?orderId=" + order.getStr("id") + "\">\n" +
					"                        <div class=\"ft14\">\n");
			if (order.get("receive_type").toString().equals("0")) 
				html.append("                                <span class=\"tag\">" + order.getStr("receive_Name") + "</span>\n");
			html.append(order.getStr("order_sn") + "\n" +
					"                            <span class=\"fr blue\">" + order.getStr("statusName") + "</span>\n" +
					"                        </div>\n" +
					"                        <div class=\"gray\">\n" +
					"                            <p>数量：" );
			if(order.get("total_count")!=null) html.append(df.format(Double.parseDouble(order.get("total_count").toString())));
			else html.append("0.00");
			html.append( "件\n" +
					"                                <span class=\"fr\">时间：" + order.get("create_date").toString() + "</span>\n" +
					"                            </p>\n" +
					"                            <p>金额：" );
			if(order.get("total_amount")!=null) html.append(df.format(Double.parseDouble(order.get("total_amount").toString())));
			else html.append("0.00");
			html.append("							 <span class=\"fr\">业务员：" + order.getStr("realname") + "</span>" +
					"							 </p>\n" +
					"                        </div>\n" +
					"                        </a>\n" +
					"                        <hr>\n" +
					"                        <div>\n");
			if(order.get("status").toString().equals("0")) html.append("<div class=\"button white-button fl\">撤销</div>\n");
			html.append(
					"                            <div class=\"button red-button fr\">再次购买</div>\n" +
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
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";
		String sellerCustomerId = getPara("sellerCustomerId");

		Page<Record> orderList = SalesOrderQuery.me().findBySellerCustomerId(getPageNumber(), getPageSize(), sellerCustomerId, selectDataArea);

		// 需要修改，使用字典来显示，不用在这个地方做查询
		for(Record record : orderList.getList()){
			record.set("statusName", getStatusName(record.getInt("status")));
			record.set("receive_Name", getReceiveName(record.getInt("receive_type")));
		}

		setAttr("orderList", orderList);
		setAttr("sellerCustomerId", getPara("sellerCustomerId"));
		setAttr("customerName", getPara("customerName"));

		render("customer_history_order.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void edit() {
		
		String id = getPara("sellerCustomerId");
		
		if (StrKit.notBlank(id)) {

			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
			setAttr("sellerCustomer", sellerCustomer);

		}
		String history = getPara("history");
		setAttr("history", history);	
		setAttr("customerType", JSON.toJSONString(getCustomerType()));
		
		render("customer_edit.html");
	}

	public List<Map<String, Object>> getCustomerType(){

		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(dataArea);
		List<Map<String, Object>> list = new ArrayList<>();

		for(CustomerType customerType : customerTypeList)
		{
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			list.add(item);
		}

		return list;
	}

	@Before(Tx.class)
	public void save() {
		
		boolean updated = true;
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, Object> map = Maps.newHashMap();
		List<ImageJson> list = Lists.newArrayList();
		
		Customer customer = getModel(Customer.class);
		SellerCustomer sellerCustomer = getModel(SellerCustomer.class);
		
		String picJson = getPara("pic");
		String oldPic = getPara("oldPic");
		String areaCode = getPara("areaCode");
		String areaName = getPara("areaName");

		String customerTypeIds = getPara("customerTypeIds", "");
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
				String originalPath = qiniuUpload(pic);
				/*Boolean isEnable = OptionQuery.me().findValueAsBool("cdn_enable");
				
				if (isEnable != null && isEnable) {
					newPath = qiniuUpload(pic);
				} else {
					newPath = upload(pic);
				}*/

				String waterFont1 = customer.getCustomerName();
				String waterFont2 = user.getRealname() + DateUtils.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss" );
				String waterFont3 = sellerCustomer.getLocation();
				String savePath = qiniuUpload(ImageUtils.waterMark(pic, Color.WHITE, waterFont1, waterFont2, waterFont3));

				image.setSavePath(savePath.replace("\\", "/"));
				image.setOriginalPath(originalPath.replace("\\", "/"));
				list.add(image);
			}
		}

		if(StrKit.notBlank(oldPic)) {
			JSONArray picList = JSON.parseArray(oldPic);
			int len = OptionQuery.me().findValue("cdn_domain").length()+1;

			for (int i = 0; i <picList.size(); i++) {

				JSONObject obj = picList.getJSONObject(i);
				String savePath = obj.getString("savePath");
				String originalPath = obj.getString("originalPath");

				ImageJson image = new ImageJson();
				image.setOriginalPath(originalPath.substring(len, originalPath.length()));
				image.setSavePath(savePath.substring(len, savePath.length()));
				list.add(image);
			}
		}

		Boolean isChecked = OptionQuery.me().findValueAsBool("web_proc_customer_review_" + getSessionAttr("sellerCode"));

		if(isChecked == null || !isChecked) {
			//如果不走流程直接做操作
			updated = doSave(sellerCustomer, customer, areaCode, areaName, customerTypeIds, list, custTypeList, SellerCustomer.CUSTOMER_NORMAL);

			if (updated)
				renderAjaxResultForSuccess("操作成功");
			else
				renderAjaxResultForError("操作失败");

			return;
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

		} else {

			updated = doSave(sellerCustomer, customer, areaCode, areaName, customerTypeIds, list, custTypeList, SellerCustomer.CUSTOMER_AUDIT);
			if (!updated) {
				renderError(404);
				return;
			}
		}

		if (isChecked)
			updated = startProcess(sellerCustomer.getId(), map, 0);

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

		//判断是否审核过了
		String task_id=CookieUtils.get(this, taskId);
		if (task_id!=null) {
			redirect("../");
			return;
		}
		CookieUtils.put(this, taskId,taskId);
		
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		List<String> custTypeNameList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(id, dealerDataArea);
		String custTypeNames = Joiner.on(",").skipNulls().join(custTypeNameList);
		setAttr("custTypeNames", custTypeNames);

		WorkFlowService workflowService = new WorkFlowService();
		Object customerVO = workflowService.getTaskVariableByTaskId(taskId, "customerVO");
		Object applyer = workflowService.getTaskVariableByTaskId(taskId, "applyUsername");
		String isEnable = workflowService.getTaskVariableByTaskId(taskId, "isEnable").toString();

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

			src.setCustTypeNameList(CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(id, getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString()));

			List<String> diffAttrList = BeanCompareUtils.contrastObj(src, dest);
			setAttr("diffAttrList", diffAttrList);
		} else if(isEnable.equals("0")) {
			List<String> diffAttrList = new ArrayList<>();
			diffAttrList.add("新增客户");
			setAttr("diffAttrList", diffAttrList);
		}else {
			List<String> diffAttrList = new ArrayList<>();
			diffAttrList.add("申请停用");
			setAttr("diffAttrList", diffAttrList);
		}
		
		//审核后将message中是否阅读改为是
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id,user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}
		
		render("customer_review.html");
	}

	public void enable() {

		String id = getPara("id");
		//int isEnabled = getParaToInt("isEnabled");
		if(StrKit.notBlank(id)) {

			boolean updated = startProcess(id, new HashMap<String, Object>(), 1);

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
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String taskId = getPara("taskId");
		Integer status = getParaToInt("status");
		String sellerCustomerId = getPara("id");
		String comment = (status == 1) ? "客户审核批准" : "客户审核拒绝";

		boolean updated = true;

		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(sellerCustomerId);
		sellerCustomer.setStatus(status == 1 ? SellerCustomer.CUSTOMER_NORMAL : SellerCustomer.CUSTOMER_REJECT);

		WorkFlowService workFlowService = new WorkFlowService();
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);

		if (status == 1) {

			CustomerVO customerVO = (CustomerVO) workFlowService.getTaskVariableByTaskId(taskId,"customerVO");
			String isEnable = workFlowService.getTaskVariableByTaskId(taskId, "isEnable").toString();

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
				customer.setCustomerName(customerVO.getCustomerName());

				if (persiste != null) {
					customer.setId(persiste.getId());
				} else customer.setId(null);
				updated = updated && customer.saveOrUpdate();

				if (StrKit.notBlank(customerVO.getNickname()))
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

				String deptDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
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

			}else if(isEnable.equals("1")){
				sellerCustomer.setIsEnabled(0);
				updated = sellerCustomer.saveOrUpdate();
			}
		} else {
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_audit");

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", sellerCustomer.getCustomer().getCustomerName());
			kv.set("submit", toUser.getRealname());

			kv.set("contact", sellerCustomer.getCustomer().getContact());
			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE, kv);
		}
		
		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", status);
		workFlowService.completeTask(taskId, comment, var);
		
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(user.getId());
		
		message.setToUserId(toUser.getId());
		message.setDeptId(user.getDepartmentId());
		message.setDataArea(user.getDataArea());
		message.setType(Message.CUSTOMER_REVIEW_TYPE_CODE);
		
		message.setObjectId(sellerCustomerId);
		message.setIsRead(0);
		message.setObjectType(Consts.OBJECT_TYPE_CUSTOMER);
		
		message.setTitle(sellerCustomer.getCustomer().getCustomerName());
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
		
		if (updated){
			renderAjaxResultForSuccess("操作成功");
		}
		else
			renderAjaxResultForError("操作失败");
	}

	private boolean startProcess(String customerId, Map<String, Object> param, int isEnable) {
		
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(customerId);
		boolean isUpdated = true;
//		Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
		Boolean isCustomerAudit = true;
		
		if (sellerCustomer == null) {
			renderError(404);
			return false;
		}
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
		
		if (isCustomerAudit != null && isCustomerAudit.booleanValue()) {
			
			if (manager == null) {
				return false;
			}
			
			String defKey = "_customer_audit";
			param.put("manager", manager.getUsername());
			param.put("isEnable", isEnable);

			
			WorkFlowService workflow = new WorkFlowService();
			String procInstId = workflow.startProcess(customerId, defKey, param);

			sellerCustomer.setProcDefKey(defKey);
			sellerCustomer.setProcInstId(procInstId);
			sellerCustomer.setStatus(SellerCustomer.CUSTOMER_AUDIT);
		}
		
		isUpdated = sellerCustomer.update();
		
		if (!isUpdated)
			return false;
		
		Message message = new Message();
		message.setFromUserId(user.getId());
		message.setToUserId(manager.getId());
		message.setDeptId(user.getDepartmentId());
		message.setDataArea(user.getDataArea());
		message.setSellerId(sellerId);
		message.setType(Message.CUSTOMER_REVIEW_TYPE_CODE);
		message.setTitle(sellerCustomer.getCustomer().getCustomerName());
		message.setObjectId(customerId);
		message.setIsRead(Consts.NO_READ);
		message.setObjectType(Consts.OBJECT_TYPE_CUSTOMER);
		
		Object customerVO = param.get("customerVO");
		if (customerVO == null && isEnable == 0) {
			message.setContent("新增待审核");
		} else if(customerVO == null && isEnable == 1) {
			message.setContent("停用待审核");
		}else {
			List<String> list = BeanCompareUtils.contrastObj(sellerCustomer, customerVO);
			if (list != null)
				message.setContent(JsonKit.toJson(list));
		}
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
		
		return isUpdated;
	}

	private String getStatusName (int statusCode) {
		if (statusCode == Consts.SALES_ORDER_STATUS_PASS) return "已审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_DEFAULT) return "待审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_CANCEL) return "订单取消";
		if (statusCode == Consts.SALES_ORDER_STATUS_REJECT) return "订单拒绝";
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

	private boolean doSave(SellerCustomer sellerCustomer, Customer customer, String areaCode, String areaName, String customerTypeIds,
						   List<ImageJson>  list, List<String>  custTypeList, String status  ) {

		boolean updated;

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
			return false;
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
		sellerCustomer.setStatus(status);

		String deptDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		Department department = DepartmentQuery.me().findByDataArea(deptDataArea);
		sellerCustomer.setDataArea(deptDataArea);
		sellerCustomer.setDeptId(department.getId());
		sellerCustomer.setImageListStore(JSON.toJSONString(list));

		updated = sellerCustomer.saveOrUpdate();
		if (!updated) {
			return false;
		}

		CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomer.getId());
		for (String custTypeId : custTypeList) {
			CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
			ccType.setSellerCustomerId(sellerCustomer.getId());
			ccType.setCustomerTypeId(custTypeId);
			ccType.save();
		}

		UserJoinCustomerQuery.me().deleteBySelerCustomerIdAndUserId(sellerCustomer.getId(), user.getId());
		UserJoinCustomer userJoinCustomer = new UserJoinCustomer();

		userJoinCustomer.setSellerCustomerId(sellerCustomer.getId());
		userJoinCustomer.setUserId(user.getId());
		userJoinCustomer.setDeptId(user.getDepartmentId());
		userJoinCustomer.setDataArea(user.getDataArea());

		updated = userJoinCustomer.save();

		return updated;
	}
	
	public void detail() {
		String id = getPara("id");
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
		setAttr("sellerCustomer", sellerCustomer);
		//审核后将message中是否阅读改为是
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id,user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}
		render("customer_detail.html");
	}

	public void importCustomer() {
		render("customer_import.html");
	}

	public void  getImportCustomerList(){
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		User user = (User) getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = user.getDataArea();
		String customerName = getPara("keyword");

		Page<Record> customerList = SellerCustomerQuery.me().findImportCustomer(getPageNumber(), getPageSize(), dealerDataArea, dataArea, customerName);
		Map<String, Object> map = new HashMap<>();
		map.put("customerList", customerList.getList());
		renderJson(map);
	}

	@Before(WechatJSSDKInterceptor.class)
	public void gotoEdit() {
		String id = getPara("id");

		if (StrKit.notBlank(id)) {

			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
			setAttr("sellerCustomer", sellerCustomer);

		}
		setAttr("type", "save");
		setAttr("customerType", JSON.toJSONString(getCustomerType()));
		render("customer_edit.html");
	}

	public void receive(){
		String sellerCustomerId = getPara("id");
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(sellerCustomerId);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		UserJoinCustomer userJoinCustomer = new UserJoinCustomer();

		userJoinCustomer.setSellerCustomerId(sellerCustomer.getId());
		userJoinCustomer.setUserId(user.getId());
		userJoinCustomer.setDeptId(user.getDepartmentId());
		userJoinCustomer.setDataArea(user.getDataArea());

		if (userJoinCustomer.save()) renderAjaxResultForSuccess("操作成功");
		else renderAjaxResultForError("操作失败");

	}
}
