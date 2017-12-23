package org.ccloud.listener;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PropKit;
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

		PropKit.use("ccloud.properties");

		String title = PropKit.get("rejectOrderFirstTitle").replace("${1}", param.getStr("submit")).replace("${2}",
				param.getStr("orderId"));
		data.set("first", Ret.create("value", title).set("color", "#173177"));
		data.set("keyword1", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
		data.set("keyword2", Ret.create("value", param.get("customerName")));

		data.set("keyword3", Ret.create("value", param.get("total")));
		data.set("keyword4", Ret.create("value", param.get("product")));
		data.set("keyword5", Ret.create("value", param.get("createTime")));
		data.set("remark", PropKit.get("remark"));

		ret.set("data", data);
		String jsonStr = JsonKit.toJson(ret);
		TemplateMsgApi.send(jsonStr);
	}

}
