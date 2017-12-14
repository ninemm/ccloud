package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.Dict;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.core.ShiroKit;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.wechat.WechatJSSDKInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

@RouterMapping(url = "/customerVisit")
public class CustomerVisitController extends BaseFrontController {
	
	public void index() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String userId = ShiroKit.getUserId();

		Page<Record> visitList = CustomerVisitQuery.me().paginateForApp(getPageNumber(), getPageSize(), getPara("type"), getPara("nature"), getPara("subType"), DataAreaUtil.getUserDeptDataArea(user.getDataArea()));

		transform(visitList.getList());

		setAttr("visitList", visitList);
		render("customer_visit_list.html");
	}

	public void getSelect() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<CustomerType> customerTypeList = CustomerTypeQuery.me().findByDataArea(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
		List<Map<String, Object>> customerTypeList2 = new ArrayList<>();
		customerTypeList2.add(all);

		for(CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypeList2.add(item);
		}

		List<Record> subTypeList = SellerCustomerQuery.me().findSubTypeByUserID(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
		List<Map<String, Object>> customerLevel = new ArrayList<>();
		customerLevel.add(all);

		for(Record subType : subTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", subType.getStr("sub_type"));
			item.put("value", subType.getStr("sub_type"));
			customerLevel.add(item);
		}

		List<Map<String, Object>> nature = new ArrayList<>();
		nature.add(all);

		Map<String, List<Map<String, Object>>> data = ImmutableMap.of( "type", customerTypeList2, "nature", nature, "level", customerLevel);
		renderJson(data);
	}

	public void refresh() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Page<Record> visitList = new Page<>();
		visitList = CustomerVisitQuery.me().paginateForApp(getParaToInt("pageNumber"), getParaToInt("pageSize"), getPara("type"), getPara("nature"), getPara("subType"), DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
		transform(visitList.getList());

		StringBuilder html = new StringBuilder();
		for (Record visit : visitList.getList())
		{
			html.append("<a class=\"weui-cell weui-cell_access\" href=\"/customerVisit/detail?id=" + visit.getStr("id") + "\">\n" +
					"                <div class=\"weui-cell__bd ft14\">\n" +
					"                    <p>${(visit.customer_name)!}</p>\n" +
					"                    <p>${(visit.customer_name)!}</p>\n" +
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
		
		String userId = ShiroKit.getUserId();
		
		//List<Record> customer_list = UserQuery.me().getCustomerInfoByUserId(user_id,data_area);
	    List<Dict> problem_list = DictQuery.me().findByCode("visit");
	    
	    //setAttr("customer",JSON.toJSONString(customer_list));
	    setAttr("problem",JSON.toJSONString(problem_list));
		
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
		
		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}
		
		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}
		setAttr("customerVisit", customerVisit);
		
		
		render("customer_visit_review.html");
	}


	// 用户新增拜访页面显示
	public void visitAdd() {
	    //String data_area = "0010010016410";
	    User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
	    String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<Record> customer_list = SellerCustomerQuery.me().paginateForApp(getPageNumber(), getPageSize(), "",selDataArea, user.getId(), "", "","");
		List<Record> customerList = new ArrayList<Record>();
		if(customer_list!=null) {
			customerList = customer_list.getList();
		}

	    List<Dict> problem_list = DictQuery.me().findByCode("visit");

	    setAttr("customer",JSON.toJSONString(customerList));
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
	// 用户新增拜访保存
	public void save() {

		 CustomerVisit customerVisit = getModel(CustomerVisit.class);
		 User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		 List<ImageJson> list = Lists.newArrayList();
		 String picJson = getPara("pic");
		 String userId = ShiroKit.getUserId();

		 customerVisit.setUserId(userId);
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
		 if (!customerVisit.saveOrUpdate()) {
			 renderAjaxResultForError("添加失败");
			 return ;
		 }
		 renderAjaxResultForSuccess("添加成功");
	}

	public void visitCustomerInfo() {
		List<Dict> problem_list = DictQuery.me().findByCode("visit");
		setAttr("problem",JSON.toJSONString(problem_list));
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("customer_visit_add.html");
	}


	private void transform(List<Record> list) {

		List<Dict> problem_list = DictQuery.me().findByCode("visit");

		for(Record visit : list) {
			if(visit.getInt("status") == 1) visit.set("statusName", "已审核");
			else if (visit.getInt("status") == 0) visit.set("statusName", "待审核");
			else visit.set("statusName", "无");

			for(Dict dict : problem_list)
				if(visit.getStr("question_type").equals(dict.getId().toString())) visit.set("typeName", dict.getName());
		}
	}

	private boolean startProcess(String id, Map<String, Object> param) {

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		boolean isUpdated = true;
//		Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
		Boolean isCustomerVisit = true;

		if (customerVisit != null) {
			if (isCustomerVisit != null && isCustomerVisit.booleanValue()) {
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
				User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
				if (manager == null) {
					renderError(500);
					return false;
				}

				String defKey = "_customer_visit_review";

				param.put("applyUsername", user.getUsername());
				param.put("manager", manager.getUsername());

				WorkFlowService workflow = new WorkFlowService();
				String procInstId = workflow.startProcess(id, defKey, param);

//				customerVisit.setProcDefKey(defKey);
//				customerVisit.setProcInstId(procInstId);
				customerVisit.setStatus(0);
				isUpdated = customerVisit.update();

				if (isUpdated) {

					Kv kv = Kv.create();

					WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(defKey);

					kv.set("touser", manager.getWechatOpenId());
					kv.set("templateId", messageTemplate.getTemplateId());
					kv.set("customerName", SellerCustomerQuery.me().findById(customerVisit.getSellerCustomerId()).getCustomer().getCustomerName());
					kv.set("submit", user.getRealname());

					kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
					kv.set("status", "待审核");

					MessageKit.sendMessage(Actions.NotifyMessage.CUSTOMER_VISIT_AUDIT_MESSAGE, kv);
				}
			} else {
				isUpdated = customerVisit.update();
			}
		}
		return isUpdated;
	}
}
