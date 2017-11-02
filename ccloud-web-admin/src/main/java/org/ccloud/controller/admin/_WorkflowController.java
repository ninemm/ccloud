package org.ccloud.controller.admin;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.route.RouterMapping;
import org.ccloud.workflow.model.ActReProcdef;
import org.ccloud.workflow.query.ActReProcdefQuery;
import org.ccloud.workflow.service.WorkFlowService;

import com.google.common.collect.ImmutableMap;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/admin/workflow", viewPath = "/WEB-INF/admin/workflow")
@RequiresPermissions(value={"/admin/workflow","/admin/all"},logical=Logical.OR)
public class _WorkflowController extends JBaseCRUDController<ActReProcdef> {

	public void list() {
		Page<ActReProcdef> page = ActReProcdefQuery.me().getDefPage(getPageNumber(), getPageSize());
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	/***
	 * 挂起/激活
	 */
	public void updateState() {
		String state = getPara("state");
		String procDefId = getPara("defid");
		WorkFlowService service = new WorkFlowService();
		String message = service.updateState(state, procDefId);
		renderAjaxResultForSuccess(message);
	}
	
	
	/***
	 * 转化为模型
	 */
	public void convertToModel() {
		String defid = getPara("defid");
		WorkFlowService service = new WorkFlowService();
		try {
			service.convertToModel(defid);
		} catch (Exception e) {
			renderAjaxResultForError("转化模型失败");
			e.printStackTrace();
			return ;
		}
		renderAjaxResultForSuccess("转换模型成功");
	}
	
	/**
	 * 读取资源，通过部署ID
	 * @param processDefinitionId  流程定义ID
	 * @param processInstanceId 流程实例ID
	 * @param resourceType 资源类型(xml|image)
	 * @param response
	 * @throws Exception
	 */
	public void resourceRead() throws Exception {
		String procDefId = getPara("procDefId");
		String proInsId = getPara("proInsId");
		String resType = getPara("resType");
		WorkFlowService service = new WorkFlowService();
		InputStream resourceAsStream = service.resourceRead(procDefId, proInsId, resType);
		byte[] b = new byte[1024];
		int len = -1;
		HttpServletResponse response = this.getResponse();
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
		renderNull();
	}
	
	/***
	 * 删除
	 */
	public void deleteDeployment(){
		String deployid = getPara("deployid");  
		WorkFlowService service = new WorkFlowService();
		service.deleteDeployment(deployid);
		renderAjaxResultForSuccess("删除成功");
	}
	
}
