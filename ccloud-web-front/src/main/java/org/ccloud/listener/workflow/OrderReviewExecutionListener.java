package org.ccloud.listener.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.Consts;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.query.SalesOrderQuery;

public class OrderReviewExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		
		Object _orderId = execution.getVariable("orderId");
		Object _pass = execution.getVariable("pass");
		int pass = _pass == null ? 1 : Integer.valueOf(_pass.toString());
		int status = Consts.SALES_ORDER_STATUS_PASS;
		
		if (pass == 0)
			status = Consts.SALES_ORDER_STATUS_REJECT;
		
		if (_orderId != null) {
			String orderId = _orderId.toString();
			SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
			salesOrder.setStatus(status);
			salesOrder.update();
			System.err.println("--------------执行完成---------------");
		}
	}

}
