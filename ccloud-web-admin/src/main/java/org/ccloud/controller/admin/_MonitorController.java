package org.ccloud.controller.admin;

import org.ccloud.core.JBaseController;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;

@RouterMapping(url = "/admin/monitor", viewPath = "/WEB-INF/admin/monitor")
@RouterNotAllowConvert
public class _MonitorController extends JBaseController {

	public void sql() {}
	
}
