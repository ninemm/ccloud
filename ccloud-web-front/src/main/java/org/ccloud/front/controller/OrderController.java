package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Comment;
import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Message;
import org.ccloud.model.Receivables;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.OutstockPrintQuery;
import org.ccloud.model.query.ReceivablesQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderJoinOutstockQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.service.WorkFlowService;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Created by chen.xuebing on 2017/12/08.
 */
@RouterMapping(url = "/order")
public class OrderController extends BaseFrontController {

	public void myOrder() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("myOrder.html");
	}

	public void orderList() {
		
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> orderList = SalesOrderQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("orderList", orderList.getList());
		renderJson(map);
	}

	public void orderDetail() {
		
		String orderId = getPara("orderId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
		order.set("statusName", getStatusName(order.getInt("status")));
		
		setAttr("order", order);
		setAttr("orderDetailList", orderDetailList);
		render("order_detail.html");
	}
	
	public void orderReview() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		String orderId = getPara("orderId");
		String taskId = getPara("taskId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);

		order.set("statusName", getStatusName(order.getInt("status")));

		boolean isCheck = false;
		if (user != null && getPara("assignee", "").contains(user.getUsername())) {
			isCheck = true;
		}
		setAttr("isCheck", isCheck);

		setAttr("taskId", taskId);
		setAttr("order", order);
		setAttr("orderDetailList", orderDetailList);
		render("order_review.html");
	}
	
	public void operateHistory() {
		keepPara();

		String id = getPara("id");

		Record salesOrder = SalesOrderQuery.me().findMoreById(id);
		setAttr("salesOrder", salesOrder);

		String proc_inst_id = getPara("proc_inst_id");
		List<Comment> comments = WorkFlowService.me().getProcessComments(proc_inst_id);
		setAttr("comments", comments);
		
		List<String> printComments = new ArrayList<String>();
		List<Record> printRecord = OutstockPrintQuery.me().findByOrderId(id);
		for (Record record : printRecord) {
			int status = record.getInt("status");
			printComments.add(buildComments(Consts.OPERATE_HISTORY_TITLE_ORDER_PRINT, record.get("create_date").toString(), record.getStr("realname"),
					status == 1 ? "打印失败" : "打印成功"));
		}
		setAttr("printComment", printComments);
		
		render("operate_history.html");
	}

	private String getStatusName(int statusCode) {
		if (statusCode == Consts.SALES_ORDER_STATUS_PASS)
			return "已审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_DEFAULT)
			return "待审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_CANCEL)
			return "取消";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT)
			return "部分出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_PART_OUT_CLOSE)
			return "部分出库-订单关闭";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT)
			return "全部出库";
		if (statusCode == Consts.SALES_ORDER_STATUS_ALL_OUT_CLOSE)
			return "全部出库-订单关闭";
		return "无";
	}

	public synchronized void salesOrder() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);

		Map<String, String[]> paraMap = getParaMap();

		if (this.saveOrder(paraMap, user, sellerId, sellerCode)) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("库存不足或提交失败");
		}
	}

	private boolean saveOrder(final Map<String, String[]> paraMap, final User user, final String sellerId,
			final String sellerCode) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				String orderId = StrKit.getRandomUUID();
				Date date = new Date();
				String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);
				String allTotalAmount = StringUtils.getArrayFirst(paraMap.get("allTotalAmount"));
				// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
				String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
						+ DateUtils.format("yyMMdd", date) + OrderSO;

				if (!SalesOrderQuery.me().insertForApp(paraMap, orderId, orderSn, sellerId, user.getId(), date,	
						user.getDepartmentId(), user.getDataArea(), allTotalAmount)) {
					return false;
				}

				String[] sellProductIds = paraMap.get("sellProductId");
				// 常规商品
				if (sellProductIds != null && sellProductIds.length > 0) {
					for (int index = 0; index < sellProductIds.length; index++) {
						if (StrKit.notBlank(sellProductIds[index])) {
							if (!SalesOrderDetailQuery.me().insertForApp(paraMap, orderId, sellerId, user.getId(), date,
									user.getDepartmentId(), user.getDataArea(), index)) {
								return false;
							}
						}

					}
				}

				String[] giftSellProductIds = paraMap.get("giftSellProductId");
				// 赠品
				if (giftSellProductIds != null && giftSellProductIds.length > 0) {
					for (int index = 0; index < giftSellProductIds.length; index++) {
						if (StrKit.notBlank(giftSellProductIds[index])) {
							if (!SalesOrderDetailQuery.me().insertForAppGift(paraMap, orderId, sellerId, user.getId(),
									date, user.getDepartmentId(), user.getDataArea(), index)) {
								return false;
							}
						}

					}
				}

				String[] compositionIds = paraMap.get("compositionId");
				String[] compositionNums = paraMap.get("compositionNum");
				// 组合商品
				if (compositionIds != null && compositionIds.length > 0) {
					for (int index = 0; index < compositionIds.length; index++) {
						String productId = compositionIds[index];
						String number = compositionNums[index];
						List<SellerProduct> list = SellerProductQuery.me().findByCompositionId(productId);
						for (SellerProduct sellerProduct : list) {
							if (!SalesOrderDetailQuery.me().insertForAppComposition(sellerProduct, orderId, sellerId,
									user.getId(), date, user.getDepartmentId(), user.getDataArea(),
									Integer.parseInt(number))) {
								return false;
							}
						}
					}
				}
				String proc_def_key = StringUtils.getArrayFirst(paraMap.get("proc_def_key"));
				if (StrKit.notBlank(proc_def_key)) {
					if (!start(orderId, StringUtils.getArrayFirst(paraMap.get("customerName")), proc_def_key)) {
						return false;
					}
				}

				return true;
			}
		});
		return isSave;
	}

	private boolean start(String orderId, String customerName, String proc_def_key) {

		WorkFlowService workflow = new WorkFlowService();

		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		
		Map<String, Object> param = Maps.newHashMap();
		param.put(Consts.WORKFLOW_APPLY_USER, user);
		param.put(Consts.WORKFLOW_APPLY_SELLER_ID, sellerId);
		param.put(Consts.WORKFLOW_APPLY_SELLER_CODE, sellerCode);
		param.put("customerName", customerName);
		

		String toUserId = "";

		if(Consts.WORKFLOW_PROC_DEF_KEY_ORDER_REVIEW_ONE.equals(proc_def_key)) {
			
			User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());
			if (manager == null) {
				return false;
			}
			param.put("manager", manager.getUsername());
			toUserId = manager.getId();
		}

		String procInstId = workflow.startProcess(orderId, proc_def_key, param);

		salesOrder.setProcKey(proc_def_key);
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);
		salesOrder.setProcInstId(procInstId);
		
		if(!salesOrder.update()) {
			return false;
		}
		
		sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), toUserId, user.getDepartmentId(), user.getDataArea());
		
		return true;
	}

	private void sendOrderMessage(String sellerId, String title, String content, String fromUserId, String toUserId, String deptId, String dataArea) {
		
		Message message = new Message();
		message.setType(Message.ORDER_REVIEW_TYPE_CODE);
		
		message.setSellerId(sellerId);
		message.setTitle(title);
		message.setContent(content);
		
		message.setFromUserId(fromUserId);
		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);
		
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
		
	}

	@Before(Tx.class)
	public void complete() {
		String orderId = getPara("id");
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String taskId = getPara("taskId");
		String comment = getPara("comment");
		String refuseReson = getPara("refuseReson","");
		Integer pass = getParaToInt("pass", 1);
		Integer edit = getParaToInt("edit", 0);
		
		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", pass);
		var.put("orderId", orderId);
		var.put(Consts.WORKFLOW_APPLY_COMFIRM, user);
		
		if (pass == 1 && edit == 1) {
			editOrder(user.getId());
			comment = "通过" + " 修改了订单";
		} else {
			comment = (pass == 1 ? "通过" : "拒绝") + " " + (comment == null ? "" : comment) + " "
					+ (refuseReson == "undefined" ? "" : refuseReson);
		}

		String comments = buildComments(Consts.OPERATE_HISTORY_TITLE_ORDER_REVIEW, DateUtils.now(), user.getRealname(), comment);

		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comments, var);

		renderAjaxResultForSuccess("订单审核成功");
	}
	
	//是否改价格
	private void editOrder(String userId) {
		Map<String, String[]> paraMap = getParaMap();
		Date date = new Date();
		
		if(!SalesOrderQuery.me().updateForApp(paraMap, userId, date)) {
			renderAjaxResultForError("订单审核修改价格失败");
		}
		
		String[] orderDetailIds = getParaValues("orderDetailId");
		for(int index=0;index<orderDetailIds.length;index++) {
			if(!SalesOrderDetailQuery.me().updateForApp(paraMap, index, date)) {
				renderAjaxResultForError("订单审核修改价格失败");
			}
		}
		
	}
	
	private String buildComments(String title, String date, String realname, String comment) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("      <div class=\"weui-cell weui-cell_access\">\n");
		stringBuilder.append("        <p>");
		stringBuilder.append(title);
		stringBuilder.append("<span class=\"fr\">");
		stringBuilder.append(date);
		stringBuilder.append("</span></p>\n");
		stringBuilder.append("        <p>操作人：");
		stringBuilder.append(realname);
		stringBuilder.append("</p>\n");
		stringBuilder.append("        <p>备注：");
		stringBuilder.append(comment);
		stringBuilder.append("</p>\n");
		stringBuilder.append("      </div>\n");
		
		return stringBuilder.toString();
	}

	public void cancel() {

		String orderId = getPara("orderId");
		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
		WorkFlowService workflow = new WorkFlowService();

		String procInstId = salesOrder.getProcInstId();
		if (StrKit.notBlank(procInstId))
			workflow.deleteProcessInstance(salesOrder.getProcInstId());

		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_CANCEL);

		if (!salesOrder.saveOrUpdate()) {
			renderAjaxResultForError("取消订单失败");
			return;
		}

		renderAjaxResultForSuccess("订单撤销成功");
	}
	
	
	@Before(Tx.class)
	public void pass() {

		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");

		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
		this.createReceivables(order);

		Date date = new Date();

		String outstockId = "";
		String warehouseId = "";
		String outstockSn = "";
		for (Record orderDetail : orderDetailList) {
			if (!warehouseId.equals(orderDetail.getStr("warehouse_id"))) {
				
				outstockId = StrKit.getRandomUUID();
				warehouseId = orderDetail.getStr("warehouse_id");
				String OrderSO = SalesOutstockQuery.me().getNewSn(sellerId);
				// 销售出库单：SS + 100000(机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
				outstockSn = "SS" + sellerCode + order.getStr("typeCode") + orderDetail.getStr("warehouseCode")
						+ DateUtils.format("yyMMdd", date) + OrderSO;

				SalesOutstockQuery.me().insert(outstockId, outstockSn, warehouseId, sellerId, order, date);
				SalesOrderJoinOutstockQuery.me().insert(orderId, outstockId);
			}

			SalesOutstockDetailQuery.me().insert(outstockId, orderDetail, date, order);
		}

		SalesOrderQuery.me().updateConfirm(orderId, Consts.SALES_ORDER_AUDIT_STATUS_PASS, user.getId(), date);// 已审核通过

		renderAjaxResultForSuccess();

	}

	private void createReceivables(Record order) {
		String customeId = order.getStr("customer_id");
		Receivables receivables = ReceivablesQuery.me().findByCustomerId(customeId);
		if (receivables == null) {
			receivables = new Receivables();
			receivables.setObjectId(order.getStr("customer_id"));
			receivables.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
			receivables.setReceiveAmount(order.getBigDecimal("total_amount"));
			receivables.setActAmount(new BigDecimal(0));
			receivables.setBalanceAmount(order.getBigDecimal("total_amount"));
			receivables.setDeptId(order.getStr("dept_id"));
			receivables.setDataArea(order.getStr("data_area"));
			receivables.setCreateDate(new Date());
		} else {
			receivables.setReceiveAmount(receivables.getReceiveAmount()
					.add(order.getBigDecimal("total_amount")));
			receivables.setBalanceAmount(receivables.getBalanceAmount()
					.add(order.getBigDecimal("total_amount")));
		}
		receivables.saveOrUpdate();
	}
	
	public void getOldOrder() {
		String orderId = getPara("orderId");
		
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		setAttr("id", orderId);
		setAttr("order", order);
		render("order_again.html");
	}	
	
	public void orderAgain() {
		String orderId = getPara("orderId");
		
		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("orderDetail", orderDetail);
		renderJson(map);
	}
}
