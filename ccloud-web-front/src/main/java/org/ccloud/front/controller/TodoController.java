package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.route.RouterMapping;

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
		
	}
	
	public void viste() {
		
		
	}
	
}
