/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

@Listener(action = {Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE, Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE})
public class CustomerAuditListener implements MessageListener {

	@Override
	public void onMessage(Message message) {

		Kv param = (Kv) message.getData();
		
		Ret ret = Ret.create();
		ret.set("touser", param.get("touser"));
		ret.set("template_id", param.get("templateId"));
		Ret data = Ret.create();
		
		PropKit.use("ccloud.properties");
		
		if (message.getAction().equals(Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE)) {
			String title = PropKit.get("rejectCustomerFirstTitle").replace("${1}", param.getStr("submit"))
				.replace("${2}", param.getStr("customerName"));
			data.set("first", Ret.create("value", title).set("color", "#173177"));
			data.set("keyword1", Ret.create("value", param.get("contact")));
			data.set("keyword2", "客户变更");
			
			data.set("keyword3", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
			data.set("keyword4", Ret.create("value", param.get("createTime")));
			data.set("remark", PropKit.get("rejectRemarkTitle"));
			
	//		data.set("customerName", Ret.create("value", param.get("customerName")).set("color", "#173177"));
	//		data.set("submit", Ret.create("value", param.get("submit")));
	//		data.set("createTime", Ret.create("value", param.get("createTime")));
	//		data.set("status", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
			
		} else if (message.getAction().equals(Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE)) {
			String title = PropKit.get("rejectCustomerVisitFirstTitle").replace("${1}", param.getStr("submit"))
				.replace("${2}", param.getStr("customerName"));
			data.set("first", Ret.create("value", title).set("color", "#173177"));
			data.set("keyword1", Ret.create("value", param.get("contact")));
			data.set("keyword2", param.getStr("submit"));
			
			data.set("keyword3", Ret.create("value", param.get("status")).set("color", "#ea6f5a"));
			data.set("keyword4", Ret.create("value", param.get("createTime")));
			data.set("keyword5", "无");
			data.set("remark", PropKit.get("rejectRemarkTitle"));
			
		}
		
		ret.set("data", data);
		String jsonStr = JsonKit.toJson(ret);
		TemplateMsgApi.send(jsonStr);
	}

}
