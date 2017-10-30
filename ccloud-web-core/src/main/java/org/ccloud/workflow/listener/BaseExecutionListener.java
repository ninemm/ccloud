package org.ccloud.workflow.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

public class BaseExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	public void notify(DelegateExecution execution) throws Exception {
//		String order_id = execution.getVariable("order_id").toString();
//		String eventName = execution.getEventName();
	}

}
