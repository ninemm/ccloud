
package org.ccloud.workflow.listener.order;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
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

import java.util.List;

public class MemberOrderReviewOneTaskListener implements TaskListener {

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

		if (taskList != null && taskList.size() == 0) {// 业务员
			task.setAssignee(user.getUsername());
			OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), user.getId(), user.getDepartmentId(),
					user.getDataArea(), orderId);
		} else if (taskList != null && taskList.size() > 0) {

			List<HistoricTaskInstance> list = processEngine.getHistoryService().createHistoricTaskInstanceQuery()
					                                  .executionId(executeInstanceId).list();
			int size = list.size();

			if (size == 1) {// 订单审核人
				String orderReviewerName = "";
				User orderReviewer = UserQuery.me().findOrderReviewerByDeptId(user.getDepartmentId());
				if (orderReviewer != null) {
					orderReviewerName = orderReviewer.getUsername();
				}

				task.setAssignee(orderReviewerName);
				OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), orderReviewer.getId(),user.getDepartmentId(),
						user.getDataArea(), orderId);
			}
		}
	}

}