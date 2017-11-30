package org.ccloud.workflow.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.model.Customer;
import org.ccloud.model.query.CustomerQuery;

public class CustomerExecuionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		Object _customerId = execution.getVariable("customer_id");
		String status = execution.getVariable("status").toString();
		if (_customerId == null)
			return ;
		
		String customerId = _customerId.toString();
		Customer customer = CustomerQuery.me().findById(customerId);
		customer.setStatus(status);
		customer.update();
		
	}

}
