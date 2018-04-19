package org.ccloud.workflow.listener.order;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.ccloud.Consts;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.User;
import org.ccloud.model.WxMessageTemplate;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.WxMessageTemplateQuery;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;

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

		Object _customerName = execution.getVariable("customerName");
		String customerName = _customerName.toString();
		Object _orderId = execution.getVariable("orderId");
		String orderId = _orderId.toString();
		Object _pass = execution.getVariable("pass");
		int pass = _pass == null ? 1 : Integer.valueOf(_pass.toString());

		if (pass == 1) {
			SalesOutstockQuery.me().pass(orderId, confirm.getId(), sellerId, sellerCode);
			OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核通过", confirm.getId(), user.getId(),
					confirm.getDepartmentId(), confirm.getDataArea(), orderId);
		} else {
			SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_STATUS_REJECT, confirm.getId(), new Date());// 已审核拒绝
			OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核拒绝", confirm.getId(), user.getId(),
					confirm.getDepartmentId(), confirm.getDataArea(), orderId);

			Object _comment = execution.getVariable("comment");
			String comment = _comment.toString();
			this.sendOrderWxMesssage(user.getWechatOpenId(), user.getWechatUseriId(), orderId, user.getRealname(), comment);
		}

		System.err.println("--------------执行完成---------------");
	}

	private void sendOrderWxMesssage(String toWechatOpenId, String toWorkWechatUserId, String orderId, String realname, String comment) {

		Kv kv = Kv.create();

		WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(Consts.PROC_ORDER_REVIEW);
		Record salesOrder = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);

		StringBuilder builder = new StringBuilder();
		for (Record record : orderDetailList) {
			int convert_relate = record.get("convert_relate");
			builder.append("\n" + record.get("custom_name") + " " + record.getInt("product_count") / convert_relate + " "
					               + record.get("big_unit") + "\n");
			builder.append(record.get("custom_name") + " " + record.getInt("product_count") % convert_relate + " "
					               + record.get("small_unit") + "\n");
		}

		kv.set("touser", toWechatOpenId);
		kv.set("toWorkUserId", toWorkWechatUserId);
		kv.set("templateId", messageTemplate.getTemplateId());

		kv.set("orderId", salesOrder.get("order_sn"));
		kv.set("customerName", salesOrder.get("customer_name"));
		kv.set("submit", realname);
		kv.set("createTime", salesOrder.get("create_date"));

		kv.set("product", builder.toString());
		kv.set("total", salesOrder.get("total_amount"));
		kv.set("status", comment);
		kv.set("remark", "审核时间：" + DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
		MessageKit.sendMessage(Actions.NotifyWechatMessage.ORDER_AUDIT_MESSAGE, kv);
	}

}
