package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.Dict;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.core.ShiroKit;
import org.ccloud.wechat.WechatJSSDKInterceptor;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
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
	
	public void save() {
		
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
	
}
