package org.ccloud.listener.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.query.CustomerVisitQuery;

public class CustomerVisitExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		Object objId = execution.getVariable("objId");
		Object isPass = execution.getVariable("isPass");
		if (objId != null) {
			CustomerVisit customerVisit = CustomerVisitQuery.me().findById(objId.toString());
			
			if (isPass != null) {
				int pass = Integer.valueOf(isPass.toString());
				if (pass == 1) {
					customerVisit.setStatus(2);
				} else {
					customerVisit.setStatus(1);
				}
			}
			
			if (customerVisit.update()) {
				if (customerVisit.getStatus() == 2) {
					// 发送一个消息
				} else {
					// 记录正常的消息
				}
			}
		}
		
	}

}
