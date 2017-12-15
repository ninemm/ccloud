package org.ccloud.listener.workflow;

import java.util.Date;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.Consts;
import org.ccloud.model.User;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;

public class OrderReviewExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		Object _user = execution.getVariable(Consts.WORKFLOW_APPLY_USER);
		User user = (User) _user;
		Object _sellerId = execution.getVariable(Consts.WORKFLOW_APPLY_SELLER_ID);
		String sellerId = _sellerId.toString();
		Object _sellerCode = execution.getVariable(Consts.WORKFLOW_APPLY_SELLER_CODE);

		String sellerCode = _sellerCode.toString();
		Object _orderId = execution.getVariable("orderId");
		String orderId = _orderId.toString();
		Object _pass = execution.getVariable("pass");
		int pass = _pass == null ? 1 : Integer.valueOf(_pass.toString());

		if (pass == 1) {
			SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);
		} else {
			SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_STATUS_REJECT, user.getId(), new Date());// 已审核拒绝
		}

		System.err.println("--------------执行完成---------------");
	}

}
