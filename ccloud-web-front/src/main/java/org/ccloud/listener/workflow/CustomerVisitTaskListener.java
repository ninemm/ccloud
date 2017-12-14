package org.ccloud.listener.workflow;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class CustomerVisitTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {
		
		Object _applyUsername = task.getVariable("applyUsername");
		if (_applyUsername != null) {
			String applyUsername = (String) _applyUsername;
			// 找到主管
			
			task.setAssignee("");
		}
	}

}
