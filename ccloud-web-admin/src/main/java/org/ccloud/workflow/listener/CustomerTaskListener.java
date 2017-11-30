package org.ccloud.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class CustomerTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {

		//String customerId = task.getVariable("customerId").toString();
		//task.setAssignee("");
		//task.setVariable("customer_id", customerId);
	}

}
