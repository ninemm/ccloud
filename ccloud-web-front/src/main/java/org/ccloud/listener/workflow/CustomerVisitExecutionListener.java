package org.ccloud.listener.workflow;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.Message;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.joda.time.DateTime;

import com.jfinal.kit.Kv;

public class CustomerVisitExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		CustomerVisit customerVisit = (CustomerVisit) execution.getVariable("visit");
		Object isPass = execution.getVariable("status");
		if (customerVisit != null) {
			
			if (isPass != null) {
				int pass = Integer.valueOf(isPass.toString());
				if (pass == 1) {
					customerVisit.setStatus(2);
				} else {
					customerVisit.setStatus(1);
				}
			}
			
			if (customerVisit.update()) {
				if (customerVisit.getStatus() == 2) {
					// 发送一个消息
					Kv kv = Kv.create();

					String defKey = customerVisit.getProcDefKey();

					WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(defKey);

					kv.set("touser", execution.getVariable("openId"));
					kv.set("templateId", messageTemplate.getTemplateId());
					kv.set("customerName", SellerCustomerQuery.me().findById(customerVisit.getSellerCustomerId()).getCustomer().getCustomerName());
					kv.set("questionType", customerVisit.getQuestionType());
					kv.set("submit", execution.getVariable("userName"));

					kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
					kv.set("status", "已审核");

					MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE, kv);
				} else {
					Message message = (Message) execution.getVariable("message");
					message.saveOrUpdate();
					// 记录正常的消息
				}
			}
		}
		
	}

}
