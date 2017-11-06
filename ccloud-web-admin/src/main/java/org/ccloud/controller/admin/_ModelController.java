package org.ccloud.controller.admin;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.workflow.model.ActReModel;
import org.ccloud.workflow.plugin.ActivitiPlugin;
import org.ccloud.workflow.query.ActReModelQuery;
import org.ccloud.workflow.service.WorkFlowService;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/admin/model", viewPath = "/WEB-INF/admin/model")
@RequiresPermissions(value={"/admin/model","/admin/all"},logical=Logical.OR)
public class _ModelController extends JBaseCRUDController<ActReModel> {
	
	@Override
	public void index() {
		render("index.html");
	}	

	public void list() {
		Page<ActReModel> page = ActReModelQuery.me().getModelPage(getPageNumber(), getPageSize());
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	@Override
	@RequiresPermissions(value={"/admin/model/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			setAttr("model", ActReModelQuery.me().findById(id));
		}
	}
	
	@Before(UCodeInterceptor.class)
	public void save() {
		
		String name = getPara("name");
		String key = getPara("key");
		
		try {
			WorkFlowService service = new WorkFlowService();
			service.createModel(ActivitiPlugin.buildProcessEngine(), name, key);
			renderAjaxResultForSuccess("添加模型成功");
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			renderAjaxResultForError("新增模型失败");
			return ;
		}
	}
	
	@RequiresPermissions(value={"/admin/model/edit","/admin/all"},logical=Logical.OR)
	public void deploy() {
		String id = getPara("id");
		WorkFlowService service = WorkFlowService.me();
		String message = service.deploy(id);
		renderAjaxResultForSuccess(message);
	}
	
	@RequiresPermissions(value={"/admin/model/edit","/admin/all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		WorkFlowService service = new WorkFlowService();
		service.deleteModel(id);
		renderAjaxResultForSuccess("删除模型成功");
	}
	
}
