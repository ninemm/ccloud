package org.ccloud.front.controller;

import org.ccloud.core.BaseFrontController;
import org.ccloud.route.RouterMapping;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/user")
public class UserController extends BaseFrontController{

	public void index() {
		render("user.html");
	}

}
