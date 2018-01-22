package org.ccloud.front.controller;

import java.text.ParseException;
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
import org.ccloud.model.query.ActivityApplyQuery;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/todo")
public class TodoController extends BaseFrontController {

	//客户审核
	public void customer() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		List<SellerCustomer> list = SellerCustomerQuery.me().getToDo(username);
		if(list.size() !=0 )setAttr("todoList", list);

		Page <Record> historyList = SellerCustomerQuery.me().getHisProcessList(getPageNumber(), getPageSize(), Consts.PROC_CUSTOMER_REVIEW, username);
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
		Page <Record> historyList = SalesOrderQuery.me().getHisProcessList(getPageNumber(), getPageSize(), "", username);
		if(historyList.getList().size() != 0) setAttr("historyList", historyList);

		setAttr("username", username);
		render("todo_order.html");
	}

	//拜访审核
	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all" }, logical = Logical.OR)
	public void visit() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page<CustomerVisit> page = CustomerVisitQuery.me().getToDo(getPageNumber(), getPageSize(), username);
		
		Page <Record> historyList = CustomerVisitQuery.me().getHisProcessList(getPageNumber(), getPageSize(), Consts.PROC_CUSTOMER_VISIT_REVIEW, username);
		if(historyList.getList().size() != 0) setAttr("historyList", historyList);

		setAttr("page", page);
		render("todo_customer_visit.html");
	}

	//活动审核
	@RequiresPermissions(value = { "/admin/activityApply/check", "/admin/dealer/all" }, logical = Logical.OR)
	public void activityApply() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();

		List<Record> list = ActivityApplyQuery.me().getToDo(username);
		if(list.size() !=0 )setAttr("todoList", list);
		Page <Record> historyList = SalesOrderQuery.me().getHisProcessList(getPageNumber(), getPageSize(), Consts.PROC_ACTIVITY_APPLY_REVIEW, username);
		if(historyList.getList().size() != 0) setAttr("historyList", historyList);

		setAttr("username", username);
		render("todo_activity_apply.html");
	}

	public void historyAuditRefresh() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();

		Page <Record> historyList = SellerCustomerQuery.me().getHisProcessList(getParaToInt("pageNumber"), getParaToInt("pageSize"), Consts.PROC_CUSTOMER_REVIEW, username);
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
	
	public void visitHistoryAuditRefresh() throws ParseException {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page <Record> historyList = CustomerVisitQuery.me().getHisProcessList(getParaToInt("pageNumber"), getParaToInt("pageSize"), Consts.PROC_CUSTOMER_VISIT_REVIEW, username);
		StringBuilder html = new StringBuilder();
		for(Record visit : historyList.getList()) {
			html.append(
					"                    <div class=\"weui-panel weui-panel_access\">\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"<a href=\""+getRequest().getContextPath()+"/customerVisit/detail?id="+visit.getStr("id")+"\">"+		
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
					"							</a>"+
					"                        </div>\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                        	<a class=\"weui-cell__bd weui-cell_link\" href=\""+getRequest().getContextPath()+"/customerVisit/detail?id="+visit.getStr("id")+"\">客户拜访详情</a>"+
					"                        	<span class=\"weui-cell__ft\">"+visit.getStr("endTime")+"</span>"+
					"                        </div>\n" +
					"                    </div>\n");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("html", html.toString());
		map.put("totalRow", historyList.getTotalRow());
		map.put("totalPage", historyList.getTotalPage());

		renderJson(map);
	}
	
	public void orderHistoryAuditRefresh() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page <Record> historyList = SalesOrderQuery.me().getHisProcessList(getParaToInt("pageNumber"), getParaToInt("pageSize"), "", username);
		StringBuilder html = new StringBuilder();
		for(Record order : historyList.getList()) {
			html.append(
					"                    <div class=\"weui-panel weui-panel_access\">\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"							<a href=\""+getRequest().getContextPath()+"/order/orderDetail?orderId="+order.getStr("id")+"\">\n");		
					if(order.get("receive_type").equals("0")) {
					html.append("								<span class=\"tag\">账期</span>");
					}
					html.append("                            	<div class=\"weui-cell__bd ft16\">\n" +
					"                                	<p class=\"customer_name\">" + order.getStr("customer_name") + "</p>\n" +
					"                                	<div class=\"ft14 gray\">\n" +
					"                                    	<p>订单号：" + order.getStr("order_sn")+"</p>\n" +
					"                                    	<p>联系人：<span>"+order.getStr("ccontact")+" / "+order.getStr("cmobile")+"</span><span class=\"fr\">"+order.getStr("customerTypeName")+"</span></p>\n" +
					"                                    	<p>金额：￥<span>"+order.getStr("total_amount")+"</span><span class=\"fr\" id=\"date\">时间："+order.getStr("endTime")+"</span></p>\n");
					html.append("                    				</div>\n" +
					"                            	</div>\n" +
					"							</a>\n"+
					"                        </div>\n" +
					"                        <div class=\"weui-cell weui-cell_access\">\n" +
					"                        	<a class=\"weui-cell__bd\" style=\"color: gray\" href=\""+getRequest().getContextPath()+"/order/orderDetail?orderId="+order.getStr("id")+"\">订单详情</a>\n"+
					"							<span class=\"weui-cell__ft\"></span>"+
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
