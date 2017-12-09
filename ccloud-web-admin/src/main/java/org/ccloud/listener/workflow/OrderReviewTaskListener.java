package org.ccloud.listener.workflow;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.ccloud.workflow.plugin.ActivitiPlugin;

public class OrderReviewTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {
		
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
            	task.setAssignee("zhuguan");
            	//task.setVariable("manager", "zhuguan");
            } else if (size == 2) {// 财务
            	task.setAssignee("caiwu");
            	//task.setVariable("financer", "caiwu");
            } else if (size == 3) {// 库管
            	task.setAssignee("kuguan");
            	//task.setVariable("storer", "kuguan");
            }
		}
	}

}
