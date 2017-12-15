package org.ccloud.front.controller;

import java.util.List;

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

import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/todo")
public class TodoController extends BaseFrontController {

	public void customer() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		List<SellerCustomer> list = SellerCustomerQuery.me().getToDo(username);
		setAttr("todoList", list);
		render("todo_customer.html");
	}
	
	public void order() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		List<SalesOrder> list = SalesOrderQuery.me().getToDo(username);
		setAttr("todoList", list);
		render("todo_order.html");
	}
	
	public void visit() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String username = user.getUsername();
		Page<CustomerVisit> page = CustomerVisitQuery.me().getToDo(getPageNumber(), getPageSize(), username);
		setAttr("page", page);
		render("todo_customer_visit.html");
	}
	
}
