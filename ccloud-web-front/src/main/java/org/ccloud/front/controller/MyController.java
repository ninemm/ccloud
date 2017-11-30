package org.ccloud.front.controller;

import org.ccloud.core.BaseFrontController;
import org.ccloud.route.RouterMapping;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/my")
public class MyController extends BaseFrontController{

	public void index() {
		render("my.html");
	}

}
