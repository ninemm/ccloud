package org.ccloud.front.controller;


import java.util.List;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesOutstockDetail;
import org.ccloud.model.User;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/refund")
public class RefundController extends BaseFrontController{
	
	public void index() {
		User user = UserQuery.me().findById("1f797c5b2137426093100f082e234c14");
		Page<Record> salesOutstockList= SalesOutstockQuery.me().findByBizUserId(getPageNumber(), getPageSize(),user.getId());
		setAttr("salesOutstockList", salesOutstockList);
		render("refund.html");
	}
	
	
	public void detail() {
		String id = getPara("id");
		List<Record> salesOutstockDetail = SalesOutstockDetailQuery.me().findById1(id);
		setAttr("salesOutstockDetail", salesOutstockDetail);
		render("refundDetail.html");
	}
	
}
