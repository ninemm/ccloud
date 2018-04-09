package org.ccloud.listener;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;
import org.ccloud.model.query.OptionQuery;

import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
import com.jfinal.qyweixin.sdk.api.SendMessageApi;
import com.jfinal.qyweixin.sdk.msg.send.QiYeTextMsg;
import com.jfinal.qyweixin.sdk.msg.send.Text;

@Listener(action = Actions.NotifyWechatMessage.ORDER_AUDIT_MESSAGE)
public class OrderAuditListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		Kv param = (Kv) message.getData();
		
		String touser = param.getStr("toWorkUserId");
		
		PropKit.use("ccloud.properties");
		String title = PropKit.get("rejectOrderFirstTitle")
				.replace("${1}", param.getStr("submit"))
				.replace("${2}", param.getStr("orderId"));
		
		StringBuilder content = new StringBuilder();
		content.append(title);
		content.append("\n").append("审核状态：<a href=\"#\">").append(param.get("status")).append("</a>");
		content.append("\n").append("客户名称：").append(param.get("customerName"));
		content.append("\n").append("下单金额：").append(param.get("total"));
		
		content.append("\n").append("商品信息：").append(param.get("product"));
		content.append("\n").append("下单时间：").append(param.get("createTime"));
		content.append("\n").append(param.get("remark"));
		Text text = new Text(content.toString());
		
		String agentId = OptionQuery.me().findValue("qywechat_agentId");
		QiYeTextMsg textMsg = new QiYeTextMsg();
		textMsg.setTouser(touser);
		textMsg.setMsgtype("text");
		textMsg.setAgentid(agentId);
		textMsg.setText(text);
		
		SendMessageApi.sendTextMsg(textMsg);
	}

}
