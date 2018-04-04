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
import java.text.SimpleDateFormat;
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
import org.ccloud.model.Activity;
import org.ccloud.model.Message;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.excel.ExcelUploadUtils;
import org.ccloud.model.query.ActivityQuery;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.OrderDetailInfoQuery;
import org.ccloud.model.query.OrderInfoQuery;
import org.ccloud.model.query.OutstockPrintQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.SalesOrderExcel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.listener.order.OrderReviewUtil;
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
import com.jfinal.upload.UploadFile;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesOrder", viewPath = "/WEB-INF/admin/sales_order")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/salesOrder","/admin/salesOrder/otherOrder"},logical=Logical.OR)
public class _SalesOrderController extends JBaseCRUDController<SalesOrder> {

	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		Seller seller = SellerQuery.me().findById(sellerId);
		String dataArea = seller.get("data_area");
		List<Seller> sellers = SellerQuery.me().findByDataArea(dataArea);

		List<Activity> actList = new ArrayList<Activity>();
		for (Seller se : sellers) {
			List<Activity> list = ActivityQuery.me().findBySellerId(se.getId());
			actList.addAll(list);
		}

		setAttr("startDate", date);
		setAttr("endDate", date);
		setAttr("actList", actList);
		setAttr("sellers", sellers);
		render("index.html");
	}
	
	//第三方订单
	@RequiresPermissions("/admin/salesOrder/otherOrder")
	public void otherOrder() {
		render("other_order.html");
	}
	
	//第三方订单统计
	@RequiresPermissions("/admin/salesOrder/otherOrder")
	public void getOtherCount() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String platformName = getPara("platformName");
		
		if (StrKit.notBlank(platformName)) {
			platformName = StringUtils.urlDecode(platformName);
		}
		
		String status = getPara("status");
		String receiveType = getPara("receiveType");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		Record record = OrderInfoQuery.me().getCountInfo(keyword, startDate, endDate, platformName, status, receiveType, null, sellerId);
		renderJson(record);
	}
	
	//第三方订单列表
	public void otherList() {
		
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String platformName = getPara("platformName");
		
		if (StrKit.notBlank(platformName)) {
			platformName = StringUtils.urlDecode(platformName);
		}
		
		String status = getPara("status");
		String receiveType = getPara("receiveType");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		
		// 获取排序相关信息
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<Record> page = OrderInfoQuery.me().paginate(getPageNumber(), getPageSize(), keyword, 
				startDate, endDate, order, sort, platformName, status, receiveType, sellerId);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
		
	}
	
	//第三方订单详情
	@RequiresPermissions("/admin/salesOrder/otherOrder")
	public void otherDetail() {

		String orderId = getPara(0);

		Record order = OrderInfoQuery.me().findMoreById(orderId);
		List<Record> orderDetail = OrderDetailInfoQuery.me().findByOrderId(orderId);

		setAttr("order", order);
		setAttr("orderDetail", orderDetail);

		render("other_detail.html");

	}
	
	//第三方订单页面
	public void upload() {
		render("upload.html");
	}
	
	//第三方订单读取模板
	public void otherOrderTemplate() {
		String type = getPara("type");
		String realPath = "";
		if (type.equals(Consts.TEMPLATE_ALI)) {
			realPath = getSession().getServletContext().getRealPath("\\")+ "\\WEB-INF\\admin\\sales_order\\aliTemplate.xls";
		} else {
			realPath = getSession().getServletContext().getRealPath("\\")+ "\\WEB-INF\\admin\\sales_order\\danluTemplate.xls";
		}
		renderFile(new File(realPath.replace("\\", "/")));
	}
	
	//第三方订单页面导入
	public void uploading() {
		
		UploadFile uploadFile = getFile();
		String type = getPara("type");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		ImportParams params = new ImportParams();
		params.setReadRows(99);//一次读100条
		if (type.equals(Consts.TEMPLATE_ALI)) {
			if (!uploadFile.getOriginalFileName().equals(Consts.TEMPLATE_FILE_NAME_ALI)) {
				renderAjaxResultForError("导入模板文件名错误,请核对后导入");
				return;
			}
			int[] num = ExcelUploadUtils.aliUpload(uploadFile.getFile(), params, sellerId);
			renderAjaxResultForSuccess("成功导入订单" + num[0] + "单,已存在订单" + num[1] + "个");
		} else {
			if (!uploadFile.getOriginalFileName().equals(Consts.TEMPLATE_FILE_NAME_DANLU)) {
				renderAjaxResultForError("导入模板文件名错误,请核对后导入");
				return;
			}
			int[] num = ExcelUploadUtils.danLuUpload(uploadFile.getFile(), params, sellerId);
			renderAjaxResultForSuccess("成功导入订单" + num[0] + "单,已存在订单" + num[1] + "个");
		}
	}
	
	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getPara("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String activityId = getPara("activity");

		Page<Record> page = SalesOrderQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate, sellerId, dataArea, activityId);

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
        			OrderReviewUtil.sendOrderMessage(sellerId, StringUtils.getArrayFirst(paraMap.get("customerName")), "订单审核通过", user.getId(), user.getId(),
        					user.getDepartmentId(), user.getDataArea(), orderId);
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
		param.put("orderId", orderId);

		String toUserId = "";

		if(Consts.PROC_ORDER_REVIEW_ONE.equals(proc_def_key)) {

			List<User> orderReviewers = UserQuery.me().findOrderReviewerByDeptId(user.getDepartmentId());
			if (orderReviewers == null || orderReviewers.size() == 0) {
				return false;
			}

			String orderReviewUserName = "";
			for (User u : orderReviewers) {
				if (StrKit.notBlank(orderReviewUserName)) {
					orderReviewUserName = orderReviewUserName + ",";
				}

				orderReviewUserName += u.getStr("username");
				OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核",  user.getId(), u.getStr("id"),
						user.getDepartmentId(), user.getDataArea(), orderId);
			}
			param.put("manager", orderReviewUserName);
		}

		String procInstId = workflow.startProcess(orderId, proc_def_key, param);

		salesOrder.setProcKey(proc_def_key);
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);
		salesOrder.setProcInstId(procInstId);

		if(!salesOrder.update()) {
			return false;
		}

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
	
	@Before(Tx.class)
	public void complete() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String taskId = getPara("taskId");
		String comment = getPara("comment");
		String refuseReson = getPara("refuseReson","");
		Integer pass = getParaToInt("pass", 1);
		Integer edit = getParaToInt("edit", 0);
		
		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", pass);
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
			var.put("comment", comment);
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
			if (!record.getInt("out_count").equals(record.getInt("product_count"))) {
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
		String tax = getPara("tax");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String keyword = getPara("k");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getPara("sellerId");
		String activityId = getPara("activity");
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\sales_outstock\\"
				+ "salesOrderInfo.xls";
		Page<Record> page = SalesOrderQuery.me().paginate(1, Integer.MAX_VALUE, keyword, startDate, endDate, sellerId, dataArea, activityId);
		List<Record> salesOderList = page.getList();
		
		List<SalesOrderExcel> excellist = Lists.newArrayList();
		for (Record record : salesOderList) {
		
			String orderId = record.get("id");
			//客户信息
			String customerInfo = record.getStr("customer_name")+"," + record.get("prov_name")+record.get("city_name")+record.get("country_name")+record.get("address");
			//下单日期
			String saveDate =record.getStr("create_date").substring(0, 10); 
			//下单时间
			String createDate = record.getStr("create_date");
			//打印状态
			
			//打印时间
			List<Record> outstockPrints = OutstockPrintQuery.me().findByOrderId(record.getStr("id"));
			String printDate = "";
			if(outstockPrints.size()>0) {
				printDate =(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(outstockPrints.get(0).get("create_date")) ;
			}
			List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
			for(Record re : orderDetail){
				String activityTitle = "";
				if(re.getStr("is_composite").equals("1")) {
					Record r = SalesOrderDetailQuery.me().getOrderDetailId(re.getStr("id"));
					activityTitle= r.getStr("title");
				}
				BigDecimal creatconverRelate = new BigDecimal(re.getStr("convert_relate"));
				BigDecimal bigPrice;
				//0 税务人员   1  非税务人员
				if (tax.equals("0")) {
					 bigPrice = new BigDecimal(re.getStr("tax_price"));
				}else {
					 bigPrice = new BigDecimal(re.getStr("product_price"));
				}
				BigDecimal count = new BigDecimal(re.getStr("product_count"));
				String bigCount = (count.intValue()) / (creatconverRelate.intValue()) + "";
				String smallCount = (count.intValue()) % (creatconverRelate.intValue()) + "";
				BigDecimal smallPrice = bigPrice.divide(creatconverRelate, 2, BigDecimal.ROUND_HALF_UP);
				if(!bigCount.equals("0")) {
					SalesOrderExcel excel = new SalesOrderExcel();
					excel = saveExcel(re,record,bigPrice,bigCount,customerInfo,saveDate,createDate,printDate,re.getStr("big_unit"),activityTitle);
					excellist.add(excel);
				}
				if(!smallCount.equals("0")){
					SalesOrderExcel excel = new SalesOrderExcel();
					excel = saveExcel(re,record,smallPrice,smallCount,customerInfo,saveDate,createDate,printDate,re.getStr("small_unit"),activityTitle);
					excellist.add(excel);
				}
				
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
	
	public SalesOrderExcel saveExcel(Record re,Record record,BigDecimal price,String count,String customerInfo,String saveDate,String createDate,String printDate,String unit,String activity) {
		SalesOrderExcel excel = new SalesOrderExcel();
		excel.setProductName(re.getStr("custom_name"));
		excel.setValueName(re.getStr("valueName"));
		excel.setProductCount(count);
		excel.setPrintDate(printDate);
		if(printDate.equals("")) {
			excel.setIsPrint("否");
		}else {
			excel.setIsPrint("是");
		}
		excel.setCustomer(customerInfo);
		excel.setUnit(unit);
		excel.setProductPrice(price.toString());
		excel.setTotalAmount(price.multiply(new BigDecimal(count)).toString());
		excel.setCreatconvertRelate(re.getStr("convert_relate")+re.getStr("small_unit")+"/"+re.getStr("big_unit"));
		excel.setOrderSn(record.getStr("order_sn"));
		excel.setCustomerType(record.getStr("customerTypeName"));
		if(record.getStr("realname")==null){
			excel.setBizUser("");
		}else{
			excel.setBizUser(record.getStr("realname"));
		}
		if(record.getStr("receive_type").equals("0")){
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
		excel.setBarCode(re.getStr("bar_code"));
		excel.setWarehouse(re.get("warehouseName").toString());
		excel.setOrderDate(saveDate);
		excel.setActivity(activity);
		excel.setCreateDate(record.get("create_date").toString());
		return excel;
	}
}
