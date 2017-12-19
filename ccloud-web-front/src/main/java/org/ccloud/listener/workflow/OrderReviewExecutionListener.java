package org.ccloud.listener.workflow;

import java.util.Date;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.Consts;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.User;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.joda.time.DateTime;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;

public class OrderReviewExecutionListener implements ExecutionListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateExecution execution) throws Exception {

		Object _user = execution.getVariable(Consts.WORKFLOW_APPLY_USER);
		User user = (User) _user;
		Object _confirm = execution.getVariable(Consts.WORKFLOW_APPLY_COMFIRM);
		User confirm = (User) _confirm;
		Object _sellerId = execution.getVariable(Consts.WORKFLOW_APPLY_SELLER_ID);
		String sellerId = _sellerId.toString();
		Object _sellerCode = execution.getVariable(Consts.WORKFLOW_APPLY_SELLER_CODE);

		String sellerCode = _sellerCode.toString();
		Object _orderId = execution.getVariable("orderId");
		String orderId = _orderId.toString();
		Object _pass = execution.getVariable("pass");
		int pass = _pass == null ? 1 : Integer.valueOf(_pass.toString());

		if (pass == 1) {
			SalesOutstockQuery.me().pass(orderId, confirm.getId(), sellerId, sellerCode);
		} else {
			SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_STATUS_REJECT, confirm.getId(), new Date());// 已审核拒绝
			this.sendOrderWxMesssage(user.getWechatOpenId(), orderId, confirm.getRealname());
		}

		System.err.println("--------------执行完成---------------");
	}
	
	
	private void sendOrderWxMesssage(String toWechatOpenId, String orderId, String confirmRealname) {
		
		Kv kv = Kv.create();
		
		WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(Consts.WX_MESSAGE_TEMPLATE_ORDER_REVIEW);
		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
		
		StringBuilder builder = new StringBuilder();
		for(Record record : orderDetailList) {
			int convert_relate  = record.get("convert_relate");
			builder.append(record.get("custom_name") + " " + record.getInt("product_count")/convert_relate + " " + record.get("big_unit") + "\n");
			builder.append(record.get("custom_name") + " " + record.getInt("product_count")%convert_relate + " " + record.get("big_unit") + "\n");
		}
				
			
		
		kv.set("touser", toWechatOpenId);
		kv.set("templateId", messageTemplate.getTemplateId());

		kv.set("orderId", salesOrder.getOrderSn());
		kv.set("submit", confirmRealname);
		kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
		
		kv.set("product", builder.toString());
		kv.set("total", salesOrder.getTotalAmount());
		kv.set("status", "已拒绝");
		MessageKit.sendMessage(Actions.NotifyWechatMessage.ORDER_AUDIT_MESSAGE, kv);
	}

}
