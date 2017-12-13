package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
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
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/customerVisit")
public class CustomerVisitController extends BaseFrontController {
	
	public void index() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String userId = ShiroKit.getUserId();
		render("customer_visit_list.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void edit() {
		
		String userId = ShiroKit.getUserId();
		
		//List<Record> customer_list = UserQuery.me().getCustomerInfoByUserId(user_id,data_area);
	    List<Dict> problem_list = DictQuery.me().findByCode("visit");
	    
	    //setAttr("customer",JSON.toJSONString(customer_list));
	    setAttr("problem",JSON.toJSONString(problem_list));
		
		render("customer_visit_edit.html");
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
	    String user_id = "1f797c5b2137426093100f082e234c14";
	    //String data_area = "0010010016410";
	    //User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
	    String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<Record> customer_list = SellerCustomerQuery.me().paginateForApp(getPageNumber(), getPageSize(), "",selDataArea, user_id, "", "");
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
		 customerVisit.setDataArea(user.getDepartmentId());
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
	
}
