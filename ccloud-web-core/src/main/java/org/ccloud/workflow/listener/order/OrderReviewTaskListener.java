
package org.ccloud.workflow.listener.order;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.ccloud.Consts;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Message;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserGroupRelQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.workflow.plugin.ActivitiPlugin;

import com.google.common.base.Joiner;
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
			List<Record> acounts = getAcount(user.getId());
			for (Record record : acounts) {
				if (StrKit.notBlank(acountUserName)) {
					acountUserName = acountUserName + ",";
				}
				acountUserName += record.getStr("username");
				sendOrderMessage(sellerId, customerName, "订单审核", record.getStr("id"), user.getId(),
						user.getDepartmentId(), user.getDataArea(),orderId);
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

			if (size == 1) {// 业务主管
				User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
				task.setAssignee(manager.getUsername());
				sendOrderMessage(sellerId, customerName, "订单审核", manager.getId(), user.getId(), user.getDepartmentId(),
						user.getDataArea(),orderId);
			} else if (size == 2) {// 财务
				String treasurerUserName = "";
				List<Record> treasurers = getTreasurer(
						DepartmentQuery.me().findDealerDataArea(user.getDepartmentId()));
				for (Record record : treasurers) {
					if (StrKit.notBlank(treasurerUserName)) {
						treasurerUserName = treasurerUserName + ",";
					}

					treasurerUserName += record.getStr("username");
					sendOrderMessage(sellerId, customerName, "订单审核", record.getStr("id"), user.getId(),
							user.getDepartmentId(), user.getDataArea(),orderId);
				}
				task.setAssignee(treasurerUserName);
			}
		}
	}

	private List<Record> getAcount(String userId) {
		List<String> userIdList = UserGroupRelQuery.me().findUserIdsByGroup(Consts.GROUP_CODE_PREFIX_DATA, userId);
		String userIds = Joiner.on(",").join(userIdList);
		List<Record> userList = UserGroupRelQuery.me().findUsersByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE,
				Consts.ROLE_CODE_020, userIds);

		return userList;
	}

	private List<Record> getTreasurer(String dataArea) {
		List<String> userIdList = UserQuery.me().findUserIdsByDeptDataArea(dataArea);
		String userIds = Joiner.on(",").join(userIdList);
		List<Record> userList = UserGroupRelQuery.me().findUsersByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE,
				Consts.ROLE_CODE_007, userIds);

		return userList;
	}

	private void sendOrderMessage(String sellerId, String title, String content, String fromUserId, String toUserId,
			String deptId, String dataArea, String orderId) {

		Message message = new Message();
		message.setType(Message.ORDER_REVIEW_TYPE_CODE);

		message.setSellerId(sellerId);
		message.setTitle(title);
		message.setContent(content);

		message.setObjectId(orderId);
		message.setIsRead(Consts.NO_READ);
		message.setObjectType(Consts.OBJECT_TYPE_ORDER);

		
		message.setFromUserId(fromUserId);
		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);

		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);

	}

}