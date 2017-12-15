
package org.ccloud.listener.workflow;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.ccloud.Consts;
import org.ccloud.model.User;
import org.ccloud.model.query.UserGroupRelQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.workflow.plugin.ActivitiPlugin;

import com.google.common.base.Joiner;

public class OrderReviewTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {
		Object object = task.getVariable(Consts.WORKFLOW_APPLY_USER);
		User user = (User) object;
		String executeInstanceId = task.getProcessInstanceId();
		ProcessEngine processEngine = ActivitiPlugin.buildProcessEngine();
		List<Task> taskList = processEngine.getTaskService().createTaskQuery().executionId(executeInstanceId).list();  
		
		if (taskList.size() > 0 && taskList!=null) {
//			String lastassignee = listtask.get(0).getAssignee();   //取得ID  
//			System.err.println("lastassignee = " + lastassignee);
//            String prcessName = listtask.get(0).getProcessDefinitionId();
//            System.err.println("prcessName = " + prcessName);
//            String[] processDefinitionid =  prcessName.split(":");
//            
//            String processDefId = processDefinitionid[0];          //就去intershipone  
            
            List<HistoricTaskInstance> list = processEngine.getHistoryService()
            		.createHistoricTaskInstanceQuery().executionId(executeInstanceId).list();  
            int size = list.size();
            
            if (size == 1) {// 业务主管
				User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
            	task.setAssignee(manager.getUsername());
//            	task.setVariable("manager", "zhuguan");
            } else if (size == 2) {// 财务
            	String userNames = getTreasurer(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
            	task.setAssignee(userNames);
            	//task.setVariable("financer", "caiwu");
            } 
		}
	}
	
	private String getTreasurer(String dataArea) {
		List<String> userIdList = UserQuery.me().findUserIdsByDeptDataArea(dataArea);
		String userIds = Joiner.on(",").join(userIdList);
		List<String> userNameList = UserGroupRelQuery.me().findUserNamesByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE, Consts.ROLE_CODE_007, userIds);

		return Joiner.on(",").join(userNameList);
	}

}