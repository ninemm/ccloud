package org.ccloud.workflow.listener.order;

import com.google.common.base.Joiner;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Message;
import org.ccloud.model.query.UserGroupRelQuery;
import org.ccloud.model.query.UserQuery;

import java.util.List;

public class OrderReviewUtil {

	public static void sendOrderMessage(String sellerId, String title, String content, String fromUserId, String toUserId,
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

	public static List<Record> getAcount(String userId) {
		List<String> userIdList = UserGroupRelQuery.me().findUserIdsByGroup(Consts.GROUP_CODE_PREFIX_DATA, userId);
		String userIds = Joiner.on(",").join(userIdList);
		List<Record> userList = UserGroupRelQuery.me().findUsersByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE,
				Consts.ROLE_CODE_020, userIds);

		return userList;
	}

	public static List<Record> getDirector(String dataArea) {
		List<String> userIdList = UserQuery.me().findUserIdsByDeptDataArea(dataArea);
		String userIds = Joiner.on(",").join(userIdList);
		List<Record> userList = UserGroupRelQuery.me().findUsersByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE,
				Consts.ROLE_CODE_011, userIds);

		return userList;
	}

	public static List<Record> getTreasurer(String dataArea) {
		List<String> userIdList = UserQuery.me().findUserIdsByDeptDataArea(dataArea);
		String userIds = Joiner.on(",").join(userIdList);
		List<Record> userList = UserGroupRelQuery.me().findUsersByRoleCode(Consts.GROUP_CODE_PREFIX_ROLE,
				Consts.ROLE_CODE_007, userIds);

		return userList;
	}


}
