/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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
package org.ccloud.controller.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Comment;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Message;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.User;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.OutstockPrintQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.SalesOrderExcel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.service.WorkFlowService;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesOrder", viewPath = "/WEB-INF/admin/sales_order")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesOrder")
public class _SalesOrderController extends JBaseCRUDController<SalesOrder> {

	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");

		setAttr("startDate", date);
		setAttr("endDate", date);

		render("index.html");
	}

	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Record> page = SalesOrderQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate, sellerId, dataArea);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	public void detail() {

		String orderId = getPara(0);

		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);

		setAttr("order", order);
		setAttr("orderDetail", orderDetail);

		render("detail.html");

	}

	public void add() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		String sellerId = getSessionAttr("sellerId");
		if (user == null || StrKit.isBlank(sellerId)) {
			// TODO
		}

		List<Record> productlist = SalesOrderQuery.me().findProductListBySeller(sellerId);

		Map<String, Object> productInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();

		for (Record record : productlist) {
			Map<String, String> productOptionMap = new HashMap<String, String>();

			String sellProductId = record.getStr("id");
			String customName = record.getStr("custom_name");
			String speName = record.getStr("valueName");

			productInfoMap.put(sellProductId, record);

			productOptionMap.put("id", sellProductId);
			productOptionMap.put("text", customName + "/" + speName);

			productOptionList.add(productOptionMap);
		}

		List<Record> customerList = SalesOrderQuery.me().findCustomerListByUser(user.getId());

		Map<String, Object> customerInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> customerOptionList = new ArrayList<Map<String, String>>();

		for (Record record : customerList) {
			Map<String, String> customerOptionMap = new HashMap<String, String>();

			String customerId = record.getStr("id");
			String customerName = record.getStr("customer_name");

			customerInfoMap.put(customerId, record);

			customerOptionMap.put("id", customerId);
			customerOptionMap.put("text", customerName);

			customerOptionList.add(customerOptionMap);
		}
		
		Boolean checkStore = OptionQuery.me().findValueAsBool(Consts.OPTION_SELLER_STORE_CHECK + sellerCode);
		boolean isCheckStore = (checkStore != null && checkStore == true) ? true : false;
		setAttr("isCheckStore", isCheckStore);
		setAttr("productInfoMap", JSON.toJSON(productInfoMap));
		setAttr("productOptionList", JSON.toJSON(productOptionList));

		setAttr("customerInfoMap", JSON.toJSON(customerInfoMap));
		setAttr("customerOptionList", JSON.toJSON(customerOptionList));

		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

		render("add.html");
	}

	public void customerTypeById() {
		String customerId = getPara("customerId");

		List<Record> customerTypeList = SalesOrderQuery.me().findCustomerTypeListByCustomerId(customerId,
				getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		setAttr("customerTypeList", customerTypeList);
		renderJson(customerTypeList);
	}

	@Override
	public synchronized void save() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);

		if (this.saveOrder(paraMap, user, sellerId, sellerCode)) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("库存不足或仓库中未找到对应商品");
		}
	}
	
	private boolean saveOrder(final Map<String, String[]> paraMap, final User user, 
			final String sellerId, final String sellerCode) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
        		Integer productNum = Integer.valueOf(productNumStr);
        		Integer count = 0;
        		Integer index = 0;
        		
        		String orderId = StrKit.getRandomUUID();
        		Date date = new Date();
        		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);

        		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
        		String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
        				+ DateUtils.format("yyMMdd", date) + OrderSO;

        		if(!SalesOrderQuery.me().insert(paraMap, orderId, orderSn, sellerId, user.getId(), date, user.getDepartmentId(),
        				user.getDataArea())) {
        			return false;
        		}

        		while (productNum > count) {
        			index++;
        			String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
        			if (StrKit.notBlank(productId)) {
        				if(!SalesOrderDetailQuery.me().insert(paraMap, orderId, sellerId, sellerCode, user.getId(), date,
        						user.getDepartmentId(), user.getDataArea(), index)) {
        					return false;
        				}
        				count++;
        			}

        		}
        		
				//是否开启
				boolean isStartProc = isStart(sellerCode, paraMap);
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
	
	private boolean isStart(String sellerCode, Map<String, String[]> paraMap) {
		//是否开启
		Boolean startProc = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROCEDURE_REVIEW + sellerCode);
		if(startProc != null && startProc) { 
			return true;
		}
		//超过数量(件)
		Float startNum = OptionQuery.me().findValueAsFloat(Consts.OPTION_WEB_PROC_NUM_LIMIT + sellerCode);
		Float productTotal = Float.valueOf(StringUtils.getArrayFirst(paraMap.get("productTotalCount")));
		if(startNum != null && productTotal > startNum) { 
			return true;
		}
		//超过金额(元)
		Float startPrice = OptionQuery.me().findValueAsFloat(Consts.OPTION_WEB_PROC_PRICE_LIMIT + sellerCode);
		Float total = Float.valueOf(StringUtils.getArrayFirst(paraMap.get("total")));
		if(startPrice != null && total > startPrice) { 
			return true;
		}
		
		return false;
	}
	
	public void orderProc() {
		
	}
	
	@RequiresPermissions("/admin/salesOrder/check")
	@Before(Tx.class)
	public void pass() {

		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");

		SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);

		renderAjaxResultForSuccess();

	}

	@RequiresPermissions("/admin/salesOrder/check")
	@Before(Tx.class)
	public void reject() {

		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		SalesOrderQuery.me().updateConfirm(orderId, 1001, user.getId(), new Date());// 已审核拒绝

		renderAjaxResultForSuccess();

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
	
	public void audit() {

		keepPara();
		
//		boolean isCheck = false;
//		String id = getPara("id");
//
//		SalesOrder salesOrder = SalesOrderQuery.me().findById(id);
//		setAttr("salesOrder", salesOrder);
		
//		HistoricTaskInstanceQuery query = ActivitiPlugin.buildProcessEngine().getHistoryService()  
//                .createHistoricTaskInstanceQuery();  
//        query.orderByProcessInstanceId().asc();  
//        query.orderByHistoricTaskInstanceEndTime().desc();  
//        List<HistoricTaskInstance> list = query.list();  
//        for (HistoricTaskInstance hi : list) {  
//            System.out.println(hi.getAssignee() + " " + hi.getName() + " "  
//                    + hi.getStartTime());  
//        }
        
//        String taskId = getPara("taskId");
//        List<Comment> comments = WorkFlowService.me().getProcessComments(taskId);
//		setAttr("comments", comments);
		
//		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
//		if (user != null && StrKit.equals(getPara("assignee"), user.getUsername())) {
//			isCheck = true;
//		}
//		setAttr("isCheck", isCheck);
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		String orderId = getPara("id");
		String taskId = getPara("taskId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);

		order.set("statusName", getStatusName(order.getInt("status")));

		boolean isCheck = false;
		if (user != null && getPara("assignee", "").contains(user.getUsername())) {
			isCheck = true;
		}
		//审核后将message中是否阅读改为是
		Message message=MessageQuery.me().findByObjectIdAndToUserId(orderId, user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}

		setAttr("isCheck", isCheck);

		setAttr("taskId", taskId);
		setAttr("order", order);
		setAttr("orderDetailList", orderDetailList);
		render("audit.html");
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
			return ;
		}
		
		renderAjaxResultForSuccess();
	}
	//审核通过，对库存总账进行修改
	@Before(Tx.class)
	public void passAll(){
		String ds = getPara("orderItems");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");
		boolean result=false;
		JSONArray jsonArray = JSONArray.parseArray(ds);
		List<SalesOrder> refunds = jsonArray.toJavaList(SalesOrder.class);
		for(SalesOrder order : refunds){
			String orderId = order.getId();

			result = SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);
			if(result == false){
				break;
			}
		}
		if (result) {
			renderAjaxResultForSuccess("审核成功");
			renderJson(result);
		} else {
			renderAjaxResultForError("审核失败");
			renderJson(result);
		}
	}
	
	public void operateHistory() {
		keepPara();
		
		String id = getPara(0);

		Record salesOrder = SalesOrderQuery.me().findRecordById(id);
		setAttr("salesOrder", salesOrder);

		String proc_inst_id = getPara(1);
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
	
	public void detailBySn() {

		String order_sn = getPara(0);

		Record order = SalesOrderQuery.me().findMoreBySn(order_sn);
		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderSn(order_sn);

		setAttr("order", order);
		setAttr("orderDetail", orderDetail);

		render("detail.html");

	}
	@RequiresPermissions(value = { "/admin/salesOrder/downloading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)
	public void downloading() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String keyword = getPara("k");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr("sellerId");
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\sales_outstock\\"
				+ "salesOrderInfo.xlsx";
		Page<Record> page = SalesOrderQuery.me().paginate(1, Integer.MAX_VALUE, keyword, startDate, endDate, sellerId, dataArea);
		List<Record> salesOderList = page.getList();
		
		List<SalesOrderExcel> excellist = Lists.newArrayList();
		for (Record record : salesOderList) {
		
			String orderId = record.get("id");
			List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
			for(Record re : orderDetail){
				SalesOrderExcel excel = new SalesOrderExcel();
				BigDecimal creatconverRelate = new BigDecimal(re.get("convert_relate").toString());
				BigDecimal bigPrice = new BigDecimal(re.get("product_price").toString());
				BigDecimal count = new BigDecimal(re.get("product_count").toString());
				int bCount = re.get("out_count");
				int sCount = re.get("left_count");
				int bigOutCount = bCount/(creatconverRelate.intValue());
				int smallOutCount = bCount%(creatconverRelate.intValue());
				int bigLeftCount = sCount/(creatconverRelate.intValue());
				int smallLeftCount = sCount%(creatconverRelate.intValue());
				String bigCount =(count.intValue())/(creatconverRelate.intValue())+"";
				String smallCount = (count.intValue())%(creatconverRelate.intValue())+"";
				BigDecimal smallPrice = bigPrice.divide(creatconverRelate, 2, BigDecimal.ROUND_HALF_UP);
				excel.setProductName(re.get("custom_name").toString());
				excel.setValueName(re.get("valueName").toString());
				excel.setProductCount(bigCount);
				excel.setCreatconvertRelate(re.get("convert_relate").toString()+re.get("small_unit").toString()+"/"+re.get("big_unit").toString());
				excel.setProductPrice(re.get("product_price").toString());
				excel.setSmallCount(smallCount);
				excel.setSmallPrice(smallPrice.toString());
				excel.setBigOutCount(bigOutCount);
				excel.setSmallOutCount(smallOutCount);
				excel.setBigLeftCount(bigLeftCount);
				excel.setSmallLeftCount(smallLeftCount);
				excel.setTotalAmount(re.get("product_amount").toString());
				excel.setOrderSn(record.get("order_sn").toString());
				excel.setCustomer(record.get("customer_name").toString());
				excel.setCustomerType(record.get("customerTypeName").toString());
				excel.setContact(record.get("contact").toString());
				excel.setMobile(record.get("mobile").toString());
				if(record.get("realname")==null){
					excel.setBizUser("");
				}else{
					excel.setBizUser(record.get("realname").toString());
				}
				if(record.get("receive_type").toString().equals("0")){
					excel.setReceiveType("应收账款");
				}else{
					excel.setReceiveType("现金");
				}
				/*
				 * 状态 0:待审核 1000:已审核 1001:订单取消 2000:部分出库 2001:部分出库-订单关闭 3000:全部出库 30001:全部出库-订单关闭
				 * */
				if(record.get("status").toString().equals("0")){
					excel.setStatus("待审核");
				}else if(record.get("status").toString().equals("1000")) {
					excel.setStatus("已审核");
				}else if(record.get("status").toString().equals("1001")) {
					excel.setStatus("订单取消");
				}else if(record.get("status").toString().equals("2000")) {
					excel.setStatus("部分出库");
				}else if(record.get("status").toString().equals("2001")) {
					excel.setStatus("部分出库-订单关闭");
				}else if(record.get("status").toString().equals("3000")) {
					excel.setStatus("全部出库");
				}else{
					excel.setStatus("全部出库-订单关闭");
				}
				if(re.get("is_gift").toString().equals("0")){
					excel.setIsGift("否");
				}else{
					excel.setIsGift("是");
				}
				if(re.get("is_composite").toString().equals("0")){
					excel.setIsComposite("否");
				}else{
					excel.setIsComposite("是");
				}
				excel.setWarehouse(re.get("warehouseName").toString());
				excel.setCreateDate(record.get("create_date").toString());
				excellist.add(excel);
				
			}
		}
		
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, SalesOrderExcel.class, excellist);
		File file = new File(filePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ExcelExportUtil.closeExportBigExcel();
		
		renderFile(new File(filePath));
	}
}
