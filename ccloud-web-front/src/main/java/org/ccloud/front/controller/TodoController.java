package org.ccloud.front.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/todo")
public class TodoController extends BaseFrontController {

	public void customer() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		List<SellerCustomer> list = SellerCustomerQuery.me().getToDo(username);
		if(list.size() !=0 )setAttr("todoList", list);

		Page <Record> historyList = SellerCustomerQuery.me().getHisProcessList(getPageNumber(), getPageSize(), "_customer_audit", username);
		if(historyList.getList().size() != 0) setAttr("historyList", historyList);

		render("todo_customer.html");
	}
	
	//订单审核
	@RequiresPermissions(value = { "/admin/salesOrder/check", "/admin/dealer/all" }, logical = Logical.OR)
	public void order() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		List<SalesOrder> list = SalesOrderQuery.me().getToDo(username);
		if(list.size() !=0 )setAttr("todoList", list);
		
		//Page<CustomerVisit> page = SalesOrderQuery.me().getToDo(getPageNumber(), getPageSize(), username);
		//Page <Record> historyList = SalesOrderQuery.me().getHisProcessList(getPageNumber(), getPageSize(), "_customer_visit_review", username);
		//if(historyList.getList().size() != 0) setAttr("historyList", historyList);
		
		
		setAttr("username", username);
		render("todo_order.html");
	}

	//拜访审核
	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all" }, logical = Logical.OR)
	public void visit() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page<CustomerVisit> page = CustomerVisitQuery.me().getToDo(getPageNumber(), getPageSize(), username);
		
		Page <Record> historyList = CustomerVisitQuery.me().getHisProcessList(getPageNumber(), getPageSize(), "_customer_visit_review", username);
		if(historyList.getList().size() != 0) setAttr("historyList", historyList);

		setAttr("page", page);
		render("todo_customer_visit.html");
	}

	public void historyAuditRefresh() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();

		Page <Record> historyList = SellerCustomerQuery.me().getHisProcessList(getParaToInt("pageNumber"), getParaToInt("pageSize"), "_customer_audit", username);
		StringBuilder html = new StringBuilder();

		for(Record customer : historyList.getList()) {
			html.append(
					"                    <div class=\"weui-panel weui-panel_access\">\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                            <div class=\"weui-cell__bd ft16\">\n" +
					"                                <p class=\"customer_name\">" + customer.getStr("customer_name") + "</p>\n" +
					"                                <div class=\"ft14 gray\">\n" +
					"                                    <p>联系人：" + customer.getStr("contact") + "/" + customer.getStr("mobile") + "</p>\n" +
					"                                    <p>客户类型：" + customer.getStr("customerType") + "</p>\n" +
					"                                    <p>配送地址：" + customer.getStr("prov_name") + " " + customer.getStr("city_name") + " "
														+ customer.getStr("country_name") + " " + customer.getStr("address")+ "</p>\n" +
					"                                    <p>\n" +
					"                                        <i class=\"icon-map-pin ft16 green\"></i>&nbsp;&nbsp; ");

			if (StrKit.notBlank(customer.getStr("location"))) html.append(customer.getStr("location"));

			html.append( "\n" +
					"                                    </p>\n" +
					"                                </div>\n" +
					"                            </div>\n" +
					"                        </div>\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                        </div>\n" +
					"                    </div>\n");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", historyList.getTotalRow());
		map.put("totalPage", historyList.getTotalPage());

		renderJson(map);

	}
	
	public void visitHistoryAuditRefresh() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page <Record> historyList = SellerCustomerQuery.me().getHisProcessList(getParaToInt("pageNumber"), getParaToInt("pageSize"), "_customer_visit_review", username);
		StringBuilder html = new StringBuilder();
		
		for(Record visit : historyList.getList()) {
			html.append(
					"                    <div class=\"weui-panel weui-panel_access\">\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                            <div class=\"weui-cell__bd\">\n" +
					"                                <p class=\"ft16\">" + visit.getStr("customer_name") + "</p>\n" +
					"                                <div class=\"ft14 gray\">\n" +
					"                                    <p>联系人：" + visit.getStr("contact") + "/" + visit.getStr("mobile") + "</p>\n" +
					"                                    <p>客户类型：终端</p>\n" +
					"                                    <p>配送地址：" + visit.getStr("prov_name") + " " + visit.getStr("city_name") + " "
														+ visit.getStr("country_name") + " " + visit.getStr("address")+ "</p>\n" +
					"                                    <p>\n" +
					"                                        <i class=\"icon-map-pin ft16 green\"></i>&nbsp;&nbsp; ");

			if (StrKit.notBlank(visit.getStr("location"))) html.append(visit.getStr("location"));

			html.append( "\n" +
					"                                    </p>\n" +
					"                                </div>\n" +
					"                            </div>\n" +
					"                        </div>\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                        </div>\n" +
					"                    </div>\n");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", historyList.getTotalRow());
		map.put("totalPage", historyList.getTotalPage());

		renderJson(map);
	}

}
