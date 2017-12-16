package org.ccloud.listener.message;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

@Listener(action = Actions.NotifyWechatMessage.ORDER_AUDIT_MESSAGE)
public class OrderAuditListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		Kv param = (Kv) message.getData();
		
		Ret ret = Ret.create();
		ret.set("touser", param.get("touser"));
		ret.set("template_id", param.get("templateId"));
		
		Ret data = Ret.create();
		data.set("orderId", Ret.create("value", param.get("orderId")).set("color", "#173177"));
		data.set("submit", Ret.create("value", param.get("submit")));
		data.set("createTime", Ret.create("value", param.get("createTime")));

		data.set("product", Ret.create("value", param.get("product")).set("color", "#ea6f5a"));
		data.set("total", Ret.create("value", param.get("total")).set("color", "#ea6f5a"));
		data.set("status", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
		
		ret.set("data", data);
		String jsonStr = JsonKit.toJson(ret);
		TemplateMsgApi.send(jsonStr);
	}

}
