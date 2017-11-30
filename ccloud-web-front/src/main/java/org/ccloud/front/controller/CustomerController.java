package org.ccloud.front.controller;

import org.ccloud.core.BaseFrontController;
import org.ccloud.route.RouterMapping;

/**
 * Created by WT on 2017/11/29.
 */
@RouterMapping(url = "/customer")
public class CustomerController extends BaseFrontController {

	public void index() {
		render("customer.html");
	}

}
