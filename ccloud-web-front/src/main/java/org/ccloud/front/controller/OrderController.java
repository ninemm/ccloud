package org.ccloud.front.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Comment;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Message;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.OutstockPrintQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
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
@RequiresPermissions(value = { "/admin/salesOrder", "/admin/dealer/all" }, logical = Logical.OR)
public class OrderController extends BaseFrontController {
	
	//我的订单
	@RequiresPermissions(value = { "/admin/salesOrder", "/admin/dealer/all" }, logical = Logical.OR)
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

		Record salesOrder = SalesOrderQuery.me().findRecordById(id);
		setAttr("salesOrder", salesOrder);

		String proc_inst_id = getPara("proc_inst_id");
		List<Comment> comments = WorkFlowService.me().getProcessComments(proc_inst_id);
		setAttr("comments", comments);
		
		StringBuilder printComments = new StringBuilder();
		List<Record> printRecord = OutstockPrintQuery.me().findByOrderId(id);
		for (int i = 0; i < printRecord.size(); i++) {
			Record record = printRecord.get(i);
			int status = record.getInt("status");
			printComments.append(buildComments(Consts.OPERATE_HISTORY_TITLE_ORDER_PRINT + " 第" + (i+1) + "次", record.get("create_date").toString(), record.getStr("realname"),
					status == 1 ? "打印失败" : "打印成功"));
		}
		setAttr("printComment", printComments.toString());

		String outstockInfo = buildOutstockInfo(id);
		setAttr("outstockInfo", outstockInfo);
		
		render("operate_history.html");
	}
	
	private String buildOutstockInfo(String ordedId) {
		List<Record> orderDetails = SalesOrderDetailQuery.me().findByOrderId(ordedId);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (Record record : orderDetails) { // 若修改了产品价格或数量，则写入相关日志信息
			if (record.getInt("out_count") !=record.getInt("product_count")) {
					stringBuilder.append("●" + record.getStr("custom_name") + "<br>");
					int convert = record.getInt("convert_relate");
					stringBuilder.append("-" + record.getStr("big_unit") + "数量修改为"+ Math.round(record.getInt("out_count")/convert) + "(" + Math.round(record.getInt("product_count")/convert) + ")<br>");
					stringBuilder.append("-" + record.getStr("small_unit") + "数量修改为"+ Math.round(record.getInt("out_count")%convert) + "(" + Math.round(record.getInt("product_count")%convert) + ")<br>");
			}
		}
		
		return stringBuilder.toString();
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
			renderAjaxResultForSuccess("下单成功");
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
							if (!SalesOrderDetailQuery.me().insertForApp(paraMap, orderId, sellerId, sellerCode, user.getId(), date,
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
							if (!SalesOrderDetailQuery.me().insertForAppGift(paraMap, orderId, sellerId, sellerCode, user.getId(),
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
							if (!SalesOrderDetailQuery.me().insertForAppComposition(sellerProduct, orderId, sellerId, sellerCode,
									user.getId(), date, user.getDepartmentId(), user.getDataArea(),
									Integer.parseInt(number), user.getId())) {
								return false;
							}
						}
					}
				}
				
				Boolean startProc = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROCEDURE_REVIEW + sellerCode);
        		boolean isStartProc = startProc == true ? true : false;
        		String proc_def_key = StringUtils.getArrayFirst(paraMap.get("proc_def_key"));
        		
        		if (isStartProc && StrKit.notBlank(proc_def_key)) {
					if (!start(orderId, StringUtils.getArrayFirst(paraMap.get("customerName")), proc_def_key)) {
						return false;
					}
        		} else {
        			SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);
        			sendOrderMessage(sellerId, StringUtils.getArrayFirst(paraMap.get("customerName")), "订单审核通过", user.getId(), user.getId(),
        					user.getDepartmentId(), user.getDataArea());        			
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
		
		//是否改价格
		if (pass == 1 && edit == 1) {
			Map<String, String[]> paraMap = getParaMap();
			editOrder(paraMap, user.getId());
			String editInfo = buildEditInfo();
			
			comment = "通过" + " 修改订单<br>" + editInfo;
		} else {
			comment = (pass == 1 ? "通过" : "拒绝") + " " + (comment == null ? "" : comment) + " "
					+ (refuseReson == "undefined" ? "" : refuseReson);
		}

		String comments = buildComments(Consts.OPERATE_HISTORY_TITLE_ORDER_REVIEW, DateUtils.now(), user.getRealname(), comment);

		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comments, var);

		renderAjaxResultForSuccess("订单审核成功");
	}
	
	private void editOrder(Map<String, String[]> paraMap, String userId) {
		Date date = new Date();

		if (!SalesOrderQuery.me().updateForApp(paraMap, userId, date)) {
			renderAjaxResultForError("订单审核修改价格失败");
		}

		String[] orderDetailIds = getParaValues("orderDetailId");
		for (int index = 0; index < orderDetailIds.length; index++) {
			if (!SalesOrderDetailQuery.me().updateForApp(paraMap, index, date)) {
				renderAjaxResultForError("订单审核修改价格失败");
			}
		}

	}
	
	private String buildEditInfo() {
		String[] productNames = getParaValues("productName");
		String[] bigUnits = getParaValues("bigUnit");
		String[] smallUnits = getParaValues("smallUnit");
		String[] bigPrices = getParaValues("bigPrice");
		String[] bigNums = getParaValues("bigNum");
		String[] smallPrices = getParaValues("smallPrice");
		String[] smallNums = getParaValues("smallNum");
		String[] bigPriceSpans = getParaValues("bigPriceSpan");
		String[] bigNumSpans = getParaValues("bigNumSpan");
		String[] smallPriceSpans = getParaValues("smallPriceSpan");
		String[] smallNumSpans = getParaValues("smallNumSpan");
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int index = 0; index < productNames.length; index++) { // 若修改了产品价格或数量，则写入相关日志信息
			boolean flag = true;
			if (!bigPrices[index].equals(bigPriceSpans[index])) {
				if(flag) {
					stringBuilder.append("●" + productNames[index] + "<br>");
				}
				flag = false;
				stringBuilder.append("-每" + bigUnits[index] + "价格修改为"+ bigPrices[index]+ "(" + bigPriceSpans[index] + ")<br>");
			}
			if (!smallPrices[index].equals(smallPriceSpans[index])) {
				if(flag) {
					stringBuilder.append("●" + productNames[index] + "<br>");
				}
				flag = false;
				stringBuilder.append("-每" + smallUnits[index] + "价格修改为"+ smallPrices[index]+ "(" + smallPriceSpans[index] + ")<br>");
			}
			if (!bigNums[index].equals(bigNumSpans[index])) {
				if(flag) {
					stringBuilder.append("●" + productNames[index] + "<br>");
				}
				flag = false;
				stringBuilder.append("-" + bigUnits[index] + "数量修改为"+ bigNums[index]+ "(" + bigNumSpans[index] + ")<br>");
			}
			if (!smallNums[index].equals(smallNumSpans[index])) {
				if(flag) {
					stringBuilder.append("●" + productNames[index] + "<br>");
				}
				flag = false;
				stringBuilder.append("-" + smallUnits[index] + "数量修改为"+ smallNums[index]+ "(" + smallNumSpans[index] + ")<br>");
			}
		}
		
		return stringBuilder.toString();
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
