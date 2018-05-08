package org.ccloud.controller.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.activiti.engine.task.Comment;
import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.wechat.WechatJSSDKInterceptor;
import org.ccloud.workflow.listener.order.OrderReviewUtil;
import org.ccloud.workflow.service.WorkFlowService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by chen.xuebing on 2017/12/08.
 */
@RouterMapping(url = "/member/order")
public class OrderController extends BaseFrontController {

	public void init() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		List<Map<String, Object>> dataList = new ArrayList<>();

		JSONArray productList = JSON.parseArray(getPara("productList"));
		List<String> sellerIdList = new ArrayList<>();

		//抽sellerId
		for (int i = 0; i < productList.size(); i++) {
			JSONObject obj = productList.getJSONObject(i);
			String sellerId = obj.getString("sellerId");
			if(!sellerIdList.contains(sellerId)) sellerIdList.add(sellerId);
		}

		for(int i = 0; i < sellerIdList.size(); i++)
		{
			List<Object> prodList = new ArrayList<>();
			for(int j = 0 ; j < productList.size(); j++){
				JSONObject obj = productList.getJSONObject(j);
				if(obj.getString("sellerId").equals(sellerIdList.get(i))) prodList.add(productList.get(j));
			}
			String dealerSellerId = getDealerSellerId(sellerIdList.get(i));
			List<Record> customerTypes =  CustomerTypeQuery.me().findByMember(member.getCustomerId(), dealerSellerId);

			List<Map<String, Object>> customerTypeList = new ArrayList<>();

			for(int k = 0; k < customerTypes.size(); k++) {

				String id = customerTypes.get(k).get("id");
				String name = customerTypes.get(k).get("name");

				Map<String, Object> item = new HashMap<>();
				item.put("title", name);
				item.put("value", id);
				customerTypeList.add(item);
			}

			Map<String, Object> map = new HashMap<>();
			map.put("productList", prodList);
			map.put("customerTypeList", customerTypeList);

			Seller seller = SellerQuery.me().findById(sellerIdList.get(i));
			map.put("seller", seller.getSellerName());

			List<Record> users = MemberJoinSellerQuery.me().findUsers(member.getId(), sellerIdList.get(i));
			List<Map<String, Object>> userList = new ArrayList<>();

			for(int k = 0; k < users.size(); k++) {

				String id = users.get(k).get("id");
				String name = users.get(k).get("realname");

				Map<String, Object> item = new HashMap<>();
				item.put("title", name);
				item.put("value", id);
				userList.add(item);
			}

			map.put("userList", userList);
			dataList.add(map);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("customerInfo", JSON.toJSONString(CustomerQuery.me().findById(member.getCustomerId())));
		data.put("deliverDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		data.put("data", dataList);
		renderJson(data);
	}

	public synchronized void salesOrder() {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

				Map<String, String[]> paraMap = getParaMap();

				//存储通用的数据
				Map<String, String> moreInfo = new HashMap<>();
				moreInfo.put("customerId", StringUtils.getArrayFirst(paraMap.get("customerId")));
				moreInfo.put("customerName", StringUtils.getArrayFirst(paraMap.get("customerName")));
				moreInfo.put("contact", StringUtils.getArrayFirst(paraMap.get("contact")));
				moreInfo.put("mobile", StringUtils.getArrayFirst(paraMap.get("mobile")));
				moreInfo.put("address", StringUtils.getArrayFirst(paraMap.get("address")));
				moreInfo.put("deliveryAddress", StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
				moreInfo.put("deliveryDate", StringUtils.getArrayFirst(paraMap.get("deliveryDate")));
				moreInfo.put("lat", StringUtils.getArrayFirst(paraMap.get("lat")));
				moreInfo.put("lng", StringUtils.getArrayFirst(paraMap.get("lng")));
				moreInfo.put("location", StringUtils.getArrayFirst(paraMap.get("location")));

				String[] sellerIds = paraMap.get("sellerId");

				List<String> sellerIdList = new ArrayList<>();
				//找出所有sellerId
				for (int i = 0; i < sellerIds.length; i++)
					if (!sellerIdList.contains(sellerIds[i])) sellerIdList.add(sellerIds[i]);

				for (int i = 0; i < sellerIdList.size(); i++) {
					String sellerId = sellerIdList.get(i);
					String sellerCode = SellerQuery.me().findById(sellerId).getSellerCode();

					List<Map<String, String>> paraList = new ArrayList<>();
					Double totalNum = 0.00;
					double total = 0;
					Integer memberProcNumber = OptionQuery.me().findValueAsInteger(Consts.OPTION_WEB_MEMBER_NUMBER_LIMIT + sellerCode);
					int index = 0;

					for (int j = 0; j < sellerIds.length; j++) {
						//根据sellerId筛选产品
						if (sellerIds[j].equals(sellerId)) {
							index = Integer.valueOf(paraMap.get("index")[j]);
							Map<String, String> para = new HashMap<>();
							para.put("sellProductId", paraMap.get("sellProductId")[j]);
							para.put("productId", paraMap.get("productId")[j]);
							para.put("convert", paraMap.get("convert")[j]);
							para.put("bigNum", paraMap.get("bigNum")[j]);
							para.put("bigPrice", paraMap.get("bigPrice")[j]);
							para.put("smallNum", paraMap.get("smallNum")[j]);
							para.put("smallPrice", paraMap.get("smallPrice")[j]);
							para.put("rowTotal", paraMap.get("rowTotal")[j]);
							total = total + Double.parseDouble(paraMap.get("rowTotal")[j]);

							Double prodTotalNum = Double.parseDouble(paraMap.get("bigNum")[j]) +
									Double.parseDouble(paraMap.get("smallNum")[j]) / Double.parseDouble(paraMap.get("convert")[j]);

							totalNum = totalNum + prodTotalNum;
							paraList.add(para);

							if(memberProcNumber != null) {
								if(prodTotalNum > memberProcNumber) {
									renderAjaxResultForError(SellerProductQuery.me().findById(paraMap.get("sellProductId")[j]).getCustomName() + "数量超过上限" + memberProcNumber.toString());
									return false;
								}
							}
						}
					}
					total = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					totalNum = new BigDecimal(totalNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

					moreInfo.remove("total");
					moreInfo.remove("totalNum");
					moreInfo.put("total", Double.valueOf(total).toString());
					moreInfo.put("totalNum", Double.valueOf(totalNum).toString());

					moreInfo.remove("receiveType");
					moreInfo.remove("customerType");
					moreInfo.remove("userId");
					moreInfo.remove("remark");

					moreInfo.put("receiveType", paraMap.get("receiveType")[index]);
					moreInfo.put("customerType", paraMap.get("customerType")[index]);
					moreInfo.put("userId", paraMap.get("user")[index]);
					moreInfo.put("remark", paraMap.get("remark")[index]);

					String userId = paraMap.get("user")[index];
					User user = UserQuery.me().findById(userId);
					//一个sellerId去生产一个订单
					String result = saveOrder(paraList, moreInfo, user, sellerId, sellerCode, member.getId());

					if (StrKit.notBlank(result)) {
						renderAjaxResultForError(result);
						return false;
					}
				}
				renderAjaxResultForSuccess("下单成功");
				return true;
			}
		});
	}

	private String saveOrder(List<Map<String, String>> paraList, Map<String, String> moreInfo, User user,
							 String sellerId, String sellerCode, String memberId) {

		String orderId = StrKit.getRandomUUID();
		Date date = new Date();

		String dealerSellerId = getDealerSellerId(sellerId);

		Record customerType = CustomerTypeQuery.me().findById(moreInfo.get("customerType"));

		String customerTypeProcDefKey = customerType.getStr("proc_def_key");

		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);
		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
		String orderSn = "SO" + sellerCode + customerType.getStr("code") + DateUtils.format("yyMMdd", date) + OrderSO;

		String salesOrderId = SalesOrderQuery.me().memberInsert(moreInfo, customerType.getStr("id"), orderId, orderSn, sellerId, user.getId(), date,
				user.getDepartmentId(), user.getDataArea(), dealerSellerId);
		if(StrKit.isBlank(salesOrderId)) return  "下单失败";

		//做关联
		MemberSalesOrder memberSalesOrder = new MemberSalesOrder();
		memberSalesOrder.setMemberId(memberId);
		memberSalesOrder.setOrderId(salesOrderId);
		if(!memberSalesOrder.save()) return "下单失败";


		// 常规商品
		if (paraList != null && paraList.size() > 0) {
			for (int index = 0; index < paraList.size(); index++) {
				String message = SalesOrderDetailQuery.me().memberInsert(paraList.get(index), orderId, sellerId, sellerCode, user.getId(), date,
							user.getDepartmentId(), user.getDataArea());
				if (StrKit.notBlank(message)) {
					return message;
				}
			}
		}

		//是否开启
		boolean isStartProc = isStart(sellerCode, moreInfo);
		String proc_def_key = customerTypeProcDefKey;

		String message = "";

		if (isStartProc && StrKit.notBlank(proc_def_key)) {
			proc_def_key = Consts.PROC_MEMBER + proc_def_key ;
		} else {
			proc_def_key = Consts.PROC_MEMBER_ORDER_REVIEW_ZERO;
		}

		message = start(orderId, moreInfo.get("customerName"), proc_def_key, user, sellerId, sellerCode);

		return message;
	}

	private boolean isStart(String sellerCode, Map<String, String> moreInfo) {
		//是否开启
		Boolean startProc = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROCEDURE_REVIEW + sellerCode);
		if(startProc != null && startProc) {
			return true;
		}
		//超过数量(件)
		Float startNum = OptionQuery.me().findValueAsFloat(Consts.OPTION_WEB_PROC_NUM_LIMIT + sellerCode);
		Float totalNum = Float.valueOf(moreInfo.get("totalNum"));
		if(startNum != null && totalNum > startNum) {
			return true;
		}
		//超过金额(元)
		Float startPrice = OptionQuery.me().findValueAsFloat(Consts.OPTION_WEB_PROC_PRICE_LIMIT + sellerCode);
		Float total = Float.valueOf(moreInfo.get("total"));
		if(startPrice != null && total > startPrice) {
			return true;
		}

		return false;
	}

	private String start(String orderId, String customerName, String proc_def_key, User user, String sellerId, String sellerCode) {

		WorkFlowService workflow = new WorkFlowService();

		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);

		Map<String, Object> param = Maps.newHashMap();
		param.put(Consts.WORKFLOW_APPLY_USERNAME, user.getUsername());
		param.put(Consts.WORKFLOW_APPLY_SELLER_ID, sellerId);
		param.put(Consts.WORKFLOW_APPLY_SELLER_CODE, sellerCode);
		param.put("customerName", customerName);
		param.put("orderId", orderId);


		if(Consts.PROC_MEMBER_ORDER_REVIEW_ZERO.equals(proc_def_key)) {

			param.put("orderReviewer", user.getUsername());

			OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), user.getId(), user.getDepartmentId(), user.getDataArea(),orderId);
		}

		String procInstId = workflow.startProcess(orderId, proc_def_key, param);

		salesOrder.setProcKey(proc_def_key);
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);
		salesOrder.setProcInstId(procInstId);

		if(!salesOrder.update()) {
			return "下单失败";
		}

		return "";
	}

	//我的订单
	public void myOrder() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<Record> customerTypeList = MemberSalesOrderQuery.me()
				                                      .findOrderCustomerTypeByCustomer(member.getCustomerId());
		for (Record record : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.getStr("name"));
			item.put("value", record.getStr("name"));
			customerTypes.add(item);
		}

		String history = getPara("history");
		setAttr("history", history);
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("member_order_list.html");
	}

	public void orderList() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> orderList = MemberSalesOrderQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, member.getCustomerId());
		Record record = MemberSalesOrderQuery.me()
				                .getOrderListCount(keyword, status, customerTypeId, startDate, endDate, member.getCustomerId());

		Map<String, Object> map = new HashMap<>();
		map.put("orderList", orderList.getList());
		map.put("orderCount", record.getStr("orderCount"));
		map.put("orderAmount", record.getStr("totalAmount"));
		renderJson(map);
	}

	public void getOrderProductDetail() {
		String orderId = getPara("orderId");

		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("orderDetail", orderDetail);
		renderJson(map);
	}

	public void orderDetail() {

		String orderId = getPara("orderId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().findByOrderId(orderId);
		List<Map<String, String>> images = getImageSrc(orderDetailList);
		order.set("statusName", getStatusName(order.getInt("status")));

		setAttr("order", order);
		setAttr("orderDetailList", orderDetailList);
		setAttr("images", images);
		render("member_order_detail.html");
	}

	public void cancel() {

		String orderId = getPara("orderId");
		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
		WorkFlowService workflow = new WorkFlowService();

		String procInstId = salesOrder.getProcInstId();
		if (StrKit.notBlank(procInstId)) {
			if(salesOrder.getStatus()==Consts.SALES_ORDER_STATUS_DEFAULT) {
				workflow.deleteProcessInstance(salesOrder.getProcInstId());
			}
		}
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_CANCEL);
		if (!salesOrder.saveOrUpdate()) {
			renderAjaxResultForError("取消订单失败");
			return;
		}
		renderAjaxResultForSuccess("订单撤销成功");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void getOldOrder() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);
		String orderId = getPara("orderId");

		Record order = SalesOrderQuery.me().findMoreById(orderId);
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		setAttr("id", orderId);
		setAttr("order", order);
		setAttr("customerInfo", CustomerQuery.me().findById(member.getCustomerId()));

		Record customerType = CustomerTypeQuery.me().findById(order.getStr("customer_type_id"));
		setAttr("customerTypeId", customerType.getStr("id"));
		setAttr("customerTypeName", customerType.getStr("name"));

		User user = UserQuery.me().findById(order.getStr("biz_user_id"));
		setAttr("userId", user.getId());
		setAttr("userName", user.getRealname());

		render("member_order_again.html");
	}

	public void orderAgain() {
		String orderId = getPara("orderId");

		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("orderDetail", orderDetail);
		renderJson(map);
	}

	private List<Map<String, String>> getImageSrc(List<Record> orderDetailList) {
		List<Map<String, String>> imagePaths = new ArrayList<>();
		for (Record record : orderDetailList) {
			JSONArray jsonArray = JSONArray.parseArray(record.getStr("product_image_list_store"));
			List<ImageJson> imageList = jsonArray.toJavaList(ImageJson.class);
			Map<String, String> map = new HashMap<>();
			map.put("productSn", record.getStr("product_sn"));
			if (imageList.size() == 0) {
				map.put("savePath", null);
				imagePaths.add(map);
			}
			for (int i = 0; i < imageList.size(); i++) {
				if (imageList.get(i).getImgName().indexOf(record.getStr("product_sn") + "_1") != -1) {
					map.put("savePath", imageList.get(i).getSavePath());
					imagePaths.add(map);
					break;
				}
				if (i == imageList.size() - 1) {
					map.put("savePath", null);
					imagePaths.add(map);
				}
			}
		}
		return imagePaths;
	}

	private String getStatusName(int statusCode) {
		if (statusCode == Consts.SALES_ORDER_STATUS_PASS)
			return "已审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_DEFAULT)
			return "待审核";
		if (statusCode == Consts.SALES_ORDER_STATUS_CANCEL)
			return "订单取消";
		if (statusCode == Consts.SALES_ORDER_STATUS_REJECT)
			return "订单拒绝";
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

	private String getDealerSellerId(String sellerId) {
		String deptId = DepartmentQuery.me().findBySellerId(sellerId).getId();
		List<Department> departmentList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(deptId);
		//根据sellerId查出他的经销商sellerId
		for(Department department :departmentList)
			if(department.getStr("seller_type").equals("0")) {
				return department.getStr("seller_id");
			}
		return "";
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

		render("member_operate_history.html");
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
}
