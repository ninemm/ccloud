package org.ccloud.front.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.query.ActivityQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/activity")
public class ActivityController extends BaseFrontController {

	public void index() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		List<Record> activityRecords = ActivityQuery.me().findActivityListForApp(sellerId, "", "");

		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();

		Set<String> tagSet = new LinkedHashSet<String>();

		for (Record record : activityRecords) {
			activityList.add(record.getColumns());
			String tags = record.getStr("tags");
			if (StrKit.notBlank(tags)) {
				String[] tagArray = tags.split(",", -1);
				for (String tag : tagArray) {
					tagSet.add(tag);
				}
			}
		}

		setAttr("activityList", JSON.toJSON(activityList));
		setAttr("tags", JSON.toJSON(tagSet));

		render("activity.html");
	}

	public void activityList() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");
		String tag = getPara("tag");

		List<Record> activityList = ActivityQuery.me().findActivityListForApp(sellerId, keyword, tag);

		Set<String> tagSet = new LinkedHashSet<String>();

		for (Record record : activityList) {
			String tags = record.getStr("tags");
			if (tags != null) {
				String[] tagArray = tags.split(",", -1);
				for (String str : tagArray) {
					tagSet.add(str);
				}
			}
		}

		Map<String, Collection<? extends Serializable>> map = ImmutableMap.of("activityList", activityList, "tags",
				tagSet);
		renderJson(map);
	}

	public void activityApply() {
		String activity_id = getPara("activity_id");
		setAttr("activity_id", activity_id);
		Record activity = ActivityQuery.me().findMoreById(activity_id);

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		List<Record> activityList = ActivityQuery.me().findActivityListForApp(sellerId, "", "");

		Map<String, Object> activityInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> activityItems = new ArrayList<>();

		for (Record record : activityList) {
			Map<String, Object> item = new HashMap<>();

			String activityId = record.get("id");
			item.put("title", record.getStr("title"));
			item.put("value", activityId);

			activityItems.add(item);
			activityInfoMap.put(activityId, record);
		}

		setAttr("activityInfoMap", JSON.toJSON(activityInfoMap));
		setAttr("activityItems", JSON.toJSON(activityItems));


		//客户选择部分
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		List<Map<String, Object>> customerTypes = new ArrayList<>();

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}

		//客户类型限制
		String customer_type = activity.getStr("customer_type");
		if(StrKit.isBlank(customer_type)) {
			customerTypes.add(all);
			List<CustomerType> customerTypeList = CustomerTypeQuery.me()
					                                      .findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
			for (CustomerType customerType : customerTypeList) {
				Map<String, Object> item = new HashMap<>();
				item.put("title", customerType.getName());
				item.put("value", customerType.getId());
				customerTypes.add(item);
			}
		}else {
			Map<String, Object> item = new HashMap<>();
			item.put("title", activity.getStr("customerTypeName"));
			item.put("value", customer_type);
			customerTypes.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		setAttr("areaType", activity.getStr("area_type"));

		render("activity_apply.html");
	}

	public void apply() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		final String content = getPara("content");
		final Date createDate = new Date();

		String[] sellerCustomerIdArray = getParaValues("sellerCustomerId");
		String[] sellerCustomerNameArray = getParaValues("sellerCustomerName");


		Boolean startProc = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROC_ACTIVITY_APPLY + sellerCode);

		String[] activity_ids = getParaValues("activity_id");
		Integer[] visit_nums = getParaValuesToInt("visit_num");
		for (String sellerCustomerId : sellerCustomerIdArray) {
			for (int i = 0; i < activity_ids.length; i++) {
				//活动申请check
				String result = this.check(activity_ids[i], sellerCustomerId, sellerCustomerNameArray[i]);
				
				if(StrKit.notBlank(result)) {
					renderAjaxResultForError(result);
				}

				ActivityApply activityApply = new ActivityApply();
				String activityApplyId = StringUtils.getUUID();
				activityApply.setId(activityApplyId);
				activityApply.setActivityId(activity_ids[i]);
				activityApply.setSellerCustomerId(sellerCustomerId);
				activityApply.setBizUserId(user.getId());
				activityApply.setNum(visit_nums[i]);
				activityApply.setContent(content);

				if (startProc != null && startProc) {
					activityApply.setStatus(Consts.ACTIVITY_APPLY_STATUS_WAIT);
					activityApply.setProcInstId(Consts.PROC_ACTIVITY_APPLY_REVIEW);
					String procInstId = this.start(activityApplyId, sellerCustomerNameArray[i], Consts.PROC_ACTIVITY_APPLY_REVIEW);
					activityApply.setProcInstId(procInstId);
				}else {
					activityApply.setStatus(Consts.ACTIVITY_APPLY_STATUS_PASS);
				}

				activityApply.setDataArea(user.getDataArea());
				activityApply.setCreateDate(createDate);
				activityApply.save();
			}
		}
		renderAjaxResultForSuccess("申请成功");
	}

	private String start(String activityApplyId, String customerName, String proc_def_key) {

		WorkFlowService workflow = new WorkFlowService();

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);

		Map<String, Object> param = Maps.newHashMap();
		param.put(Consts.WORKFLOW_APPLY_USER, user);
		param.put(Consts.WORKFLOW_APPLY_SELLER_ID, sellerId);
		param.put(Consts.WORKFLOW_APPLY_SELLER_CODE, sellerCode);
		param.put("customerName", customerName);
		param.put("orderId", activityApplyId);


		String toUserId = "";

		User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
		if (manager == null) {
			renderAjaxResultForError("你的申请没有对应的人审核，请联系管理员");
		}
		param.put("manager", manager.getUsername());
		toUserId = manager.getId();

		String procInstId = workflow.startProcess(activityApplyId, proc_def_key, param);


		sendOrderMessage(sellerId, customerName, "活动审核", user.getId(), toUserId, user.getDepartmentId(), user.getDataArea(),activityApplyId);

		return procInstId;
	}

	private String check(String activityId, String sellerCustomerId, String customerName) {
		Activity activity = ActivityQuery.me().findById(activityId);
		long total = ActivityApplyQuery.me().findByActivityId(activityId);
		if (total >= activity.getTotalCustomerNum()) {
			return "活动参与的人数已经达到上限";
		}
		int interval = this.getStartDate(activity.getTimeInterval());
		DateTime dateTime = new DateTime(new Date());
		long cnt = ActivityApplyQuery.me().findBySellerCustomerIdAndActivityId(activityId, sellerCustomerId, DateUtils.format(dateTime.plusMonths(-interval).toDate()));
		if (cnt >= activity.getJoinNum()) {
			return customerName + "参与的次数已经达到上限，每个客户" + interval + "个月中参与次数为：" + activity.getJoinNum();
		}

		return "";
	}

	private int getStartDate(String timeInterval){
		if(Consts.TIME_INTERVAL_ONE.equals(timeInterval)){
			return 1;
		}
		if(Consts.TIME_INTERVAL_TWO.equals(timeInterval)){
			return 2;
		}
		if(Consts.TIME_INTERVAL_THREE.equals(timeInterval)){
			return 3;
		}
		if(Consts.TIME_INTERVAL_FOUR.equals(timeInterval)){
			return 4;
		}
		if(Consts.TIME_INTERVAL_FIVE.equals(timeInterval)){
			return 5;
		}
		if(Consts.TIME_INTERVAL_SIX.equals(timeInterval)){
			return 6;
		}
		if(Consts.TIME_INTERVAL_SEVEN.equals(timeInterval)){
			return 7;
		}
		if(Consts.TIME_INTERVAL_EIGHT.equals(timeInterval)){
			return 8;
		}
		if(Consts.TIME_INTERVAL_NINE.equals(timeInterval)){
			return 9;
		}
		if(Consts.TIME_INTERVAL_TEN.equals(timeInterval)){
			return 10;
		}
		if(Consts.TIME_INTERVAL_ELEVEN.equals(timeInterval)){
			return 11;
		}
		if(Consts.TIME_INTERVAL_TWELVE.equals(timeInterval)){
			return 12;
		}


		return 1;
	}

	private void sendOrderMessage(String sellerId, String title, String content, String fromUserId, String toUserId, String deptId, String dataArea, String orderId) {

		Message message = new Message();
		message.setType(Message.ACTIVITY_APPLY_REVIEW_TYPE_CODE);

		message.setSellerId(sellerId);
		message.setTitle(title);
		message.setContent(content);

		message.setObjectId(orderId);
		message.setIsRead(Consts.NO_READ);
		message.setObjectType(Consts.OBJECT_TYPE_ACTIVITY_APPLY);

		message.setFromUserId(fromUserId);
		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);

		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);

	}

	@Before(Tx.class)
	public void complete() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String activityApplyId = getPara("id");
		String taskId = getPara("taskId");
		String comment = getPara("comment");
		Integer pass = getParaToInt("pass", 1);

		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", pass);
		var.put(Consts.WORKFLOW_APPLY_COMFIRM, user);

		comment = (pass == 1 ? "通过" : "拒绝") + " " + (comment == null ? "" : comment);
		var.put("comment", comment);

		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, var);

		ActivityApply activityApply = ActivityApplyQuery.me().findById(activityApplyId);
		String customerName = activityApply.get("customer_name");
		String toUserId = activityApply.get("biz_user_id");
		sendOrderMessage(sellerId, customerName, (pass == 1 ? "活动审核通过" : "活动审核拒绝"), user.getId(), toUserId, user.getDepartmentId(), user.getDataArea(), activityApplyId);
		activityApply.setStatus(pass == 1 ? Consts.ACTIVITY_APPLY_STATUS_PASS : Consts.ACTIVITY_APPLY_STATUS_REJECT);
		activityApply.update();

		//审核订单后将message中是否阅读改为是
		Message message = MessageQuery.me().findByObjectIdAndToUserId(activityApplyId, user.getId());
		if (null != message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}

		renderAjaxResultForSuccess("活动审核成功");
	}

	public void activityApplyReview() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		String activityApplyId = getPara("activityApplyId");
		String taskId = getPara("taskId");
		ActivityApply activityApply = ActivityApplyQuery.me().findById(activityApplyId);

		boolean isCheck = false;
		if (user != null && getPara("assignee", "").contains(user.getUsername())) {
			isCheck = true;
		}
		setAttr("isCheck", isCheck);

		//审核订单后将message中是否阅读改为是
		Message message = MessageQuery.me().findByObjectIdAndToUserId(activityApplyId, user.getId());
		if (null != message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}

		setAttr("taskId", taskId);
		setAttr("activityApply", activityApply);
		setAttr("statusName", getStatusName(activityApply.getInt("status")));
		render("activity_apply_review.html");
	}

	private String getStatusName(int status) {
		if (status == Consts.ACTIVITY_APPLY_STATUS_WAIT)
			return "待审核";
		if (status == Consts.ACTIVITY_APPLY_STATUS_PASS)
			return "审核通过";
		if (status == Consts.ACTIVITY_APPLY_STATUS_CANCEL)
			return "申请取消";
		if (status == Consts.ACTIVITY_APPLY_STATUS_REJECT)
			return "审核拒绝";
		if (status == Consts.ACTIVITY_APPLY_STATUS_END)
			return "活动结束";

		return "无";
	}

	public void applyList() {
		List<Dict> activityTypeList = DictQuery.me().findDictByType("activity_category");

		List<Map<String, Object>> activityType = new ArrayList<>();
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		activityType.add(all);
		for(Dict record : activityTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("name"));
			item.put("value", record.get("value"));
			activityType.add(item);
		}
		setAttr("activityType", JSON.toJSON(activityType));

		String history = getPara("history");
		setAttr("history", history);

		render("activity_apply_list.html");
	}

	public void refreshApply() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";

		String category = getPara("category");
		String status = getPara("status");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String keyword = getPara("keyword");


		Page<Record> applyList = ActivityApplyQuery.me().findList(getParaToInt("pageNumber"), getParaToInt("pageSize"), selectDataArea, category, status, startDate, endDate, keyword);

		DecimalFormat df   = new DecimalFormat("######0.00");
		StringBuilder html = new StringBuilder();
		for (Record apply : applyList.getList()) {
			html.append("<section>\n");
			html.append("<div class=\"weui-cells__title\"></div>");

			if (apply.getStr("status").equals("0")) html.append("<div class=\"checking\">\n");
			else if (apply.getStr("status").equals("1")) html.append("<div class=\"passed\">\n");
			else if (apply.getStr("status").equals("2")) html.append("<div class=\"widthdraw\">\n");
			else if (apply.getStr("status").equals("3")) html.append("<div class=\"failed\">\n");
			else html.append("<div class=\"checking\">\n");

			html.append("                        <a class=\"weui-cell weui-cell_access\" href=\"/activity/applyDetail?id=" + apply.getStr("id") + "\">\n" +
					"                            <i class=\"icon-tags\"></i>\n" +
					"                            <div class=\"weui-cell__bd\">" + apply.getStr("customer_name") + "</div>\n" +
					"                            <span class=\"weui-cell__ft\">\n" );
			if (apply.getStr("status").equals("0")) html.append("待审核\n");
			else if (apply.getStr("status").equals("1")) html.append("已通过\n");
			else if (apply.getStr("status").equals("2")) html.append("已撤回\n");
			else if (apply.getStr("status").equals("3")) html.append("已拒绝\n");
			else html.append("结束\n");

			html.append("                            </span>\n" +
					"                        </a>\n" +
					"                        <div class=\"weui-flex\">\n" +
					"                            <div class=\"weui-flex__item\">\n" +
					"                                <p>开始日期</p>\n" +
					"                                <p>" + apply.getStr("start_time") + "</p>\n" +
					"                            </div>\n" +
					"                            <div class=\"weui-flex__item\">\n" +
					"                                <p>结束日期</p>\n" +
					"                                <p>" + apply.getStr("end_time") + "</p>\n" +
					"                            </div>\n" +
					"                            <div class=\"weui-flex__item\">\n" +
					"                                <p>活动类型</p>\n" +
					"                                <p>" + apply.getStr("name") + "</p>\n" +
					"                            </div>\n" +
					"                            <div class=\"weui-flex__item\">\n" +
					"                                <p>预计费用</p>\n" +
					"                                <p>");
			if(apply.get("invest_amount")!=null) html.append(df.format(Double.parseDouble(apply.get("invest_amount").toString())));
			else html.append("0.00");
			html.append( "</p>\n" +
					"                            </div>\n" +
					"                        </div>\n" +
					"                    </div>\n" +
					"                </section>");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", applyList.getTotalRow());
		map.put("totalPage", applyList.getTotalPage());
		renderJson(map);
	}

	public void applyDetail() {
		String id = getPara("id");
		ActivityApply activityApply = ActivityApplyQuery.me().findById(id);

		setAttr("apply", activityApply);

		render("apply_detail.html");

	}

	public void withdraw(){
		String id = getPara("id");
		ActivityApply activityApply = ActivityApplyQuery.me().findById(id);
		activityApply.setStatus(2);
		if( activityApply.saveOrUpdate()) renderAjaxResultForSuccess("操作成功");
		else renderAjaxResultForError("操作失败");
	}
}
