package org.ccloud.listener.message;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

@Listener(action = Actions.NotifyMessage.CUSTOMER_AUDIT_MESSAGE)
public class CustomerAuditListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		Kv param = (Kv) message.getData();
		
		Ret ret = Ret.create();
		ret.set("touser", param.get("touser"));
		ret.set("template_id", param.get("templateId"));
		
		Ret data = Ret.create();
		data.set("customerName", Ret.create("value", param.get("customerName")).set("color", "#173177"));
		data.set("submit", Ret.create("value", param.get("submit")));
		data.set("createTime", Ret.create("value", param.get("createTime")));
		data.set("status", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
		
		ret.set("data", data);
		String jsonStr = JsonKit.toJson(ret);
		TemplateMsgApi.send(jsonStr);
	}

}
