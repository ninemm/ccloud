
package org.ccloud.workflow.listener.order;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.ccloud.Consts;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.workflow.plugin.ActivitiPlugin;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class OrderReviewTaskListener implements TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask task) {
		Object object = task.getVariable(Consts.WORKFLOW_APPLY_USER);
		User user = (User) object;
		Object _sellerId = task.getVariable(Consts.WORKFLOW_APPLY_SELLER_ID);
		String sellerId = _sellerId.toString();
		Object _customerName = task.getVariable("customerName");
		String customerName = _customerName.toString();
		Object _orderId = task.getVariable("orderId");
		String orderId = _orderId.toString();

		String executeInstanceId = task.getProcessInstanceId();
		ProcessEngine processEngine = ActivitiPlugin.buildProcessEngine();
		List<Task> taskList = processEngine.getTaskService().createTaskQuery().executionId(executeInstanceId).list();

		if (taskList != null && taskList.size() == 0) {
			String acountUserName = "";
			List<Record> acounts = OrderReviewUtil.getAcount(user.getId());
			for (Record record : acounts) {
				if (StrKit.notBlank(acountUserName)) {
					acountUserName = acountUserName + ",";
				}
				acountUserName += record.getStr("username");
				OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), record.getStr("id"),
						user.getDepartmentId(), user.getDataArea(), orderId);
			}
			task.setAssignee(acountUserName);
		} else if (taskList != null && taskList.size() > 0) {
			// String lastassignee = listtask.get(0).getAssignee(); //取得ID
			// System.err.println("lastassignee = " + lastassignee);
			// String prcessName = listtask.get(0).getProcessDefinitionId();
			// System.err.println("prcessName = " + prcessName);
			// String[] processDefinitionid = prcessName.split(":");
			//
			// String processDefId = processDefinitionid[0]; //就去intershipone

			List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
					                                  .executionId(executeInstanceId).list();
			int size = list.size();

			if (size == 1) {// 订单审核人
				User orderReviewer = UserQuery.me().findOrderReviewerByDeptId(user.getDepartmentId());
				task.setAssignee(orderReviewer.getUsername());
				OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), orderReviewer.getId(), user.getDepartmentId(),
						user.getDataArea(), orderId);
			} else if (size == 2) {// 财务
				String treasurerUserName = "";
				List<Record> treasurers = OrderReviewUtil.getTreasurer(
						DepartmentQuery.me().findDealerDataArea(user.getDepartmentId()));
				for (Record record : treasurers) {
					if (StrKit.notBlank(treasurerUserName)) {
						treasurerUserName = treasurerUserName + ",";
					}

					treasurerUserName += record.getStr("username");
					OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核",  user.getId(), record.getStr("id"),
							user.getDepartmentId(), user.getDataArea(), orderId);
				}
				task.setAssignee(treasurerUserName);
			}
		}
	}

}