package org.ccloud.front.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.ActivityApply;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.ActivityQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

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
		setAttr("activity_id", getPara("activity_id"));

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
		customerTypes.add(all);

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				                                      .findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("customerTypes", JSON.toJSON(customerTypes));

		render("activity_apply.html");
	}

	public void apply() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		final String content = getPara("content");
		final Date createDate = new Date();

		String sellerCustomerIds = getPara("sellerCustomerIds");
		String[] sellerCustomerArray = sellerCustomerIds.split(",", -1);

		String[] activity_ids = getParaValues("activity_id");
		Integer[] visit_nums = getParaValuesToInt("visit_num");
		for (String sellerCustomerId : sellerCustomerArray) {
			for (int i = 0; i < activity_ids.length; i++) {
				ActivityApply activityApply = new ActivityApply();
				activityApply.setId(StringUtils.getUUID());
				activityApply.setActivityId(activity_ids[i]);
				activityApply.setSellerCustomerId(sellerCustomerId);
				activityApply.setBizUserId(user.getId());
				activityApply.setNum(visit_nums[i]);
				activityApply.setContent(content);
				activityApply.setStatus(Consts.ACTIVITY_APPLY_STATUS_WAIT);
				activityApply.setCreateDate(createDate);
				activityApply.save();
			}
		}
		renderAjaxResultForSuccess("申请成功");
	}

}
