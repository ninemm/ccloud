package org.ccloud.workflow.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

public class BaseTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	public void notify(DelegateTask task) {
//		String order_id = task.getVariable("order_id").toString();
//		String finance = task.getVariable("financer").toString();
//		String manager = task.getVariable("manager").toString();
//		String storekeeper = task.getVariable("storer").toString();
	}

}
