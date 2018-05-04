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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Message;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SalesOutstock;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.OutstockPrintQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.wechat.WechatJSSDKInterceptor;
import org.ccloud.workflow.listener.order.OrderReviewUtil;
import org.ccloud.workflow.service.WorkFlowService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
		String selectDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				                                      .findByDataArea(selectDataArea);
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		List<Map<String, Object>> bizUsers = new ArrayList<>();
		bizUsers.add(all);
		List<User> users = new ArrayList<User>();

		Subject subject = SecurityUtils.getSubject();
		if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
			users = UserQuery.me().findByDeptDataArea(dataArea);
		} else {
			users.add(user);
		}

		for (User u : users) {
			Map<String, Object> items = new HashMap<>();
			items.put("title", u.getRealname());
			items.put("value", u.getId());
			bizUsers.add(items);
		}

		String history = getPara("history");
		setAttr("history", history);
		setAttr("bizUsers",JSON.toJSON(bizUsers));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("myOrder.html");
	}

	public void orderList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String bizUserId = getPara("bizUserId");

		Page<Record> orderList = SalesOrderQuery.me()._paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea,bizUserId);
		Record record = SalesOrderQuery.me()
				.getOrderListCount(keyword, status, customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("orderList", orderList.getList());
		map.put("user", user);
		map.put("orderCount", record.getStr("orderCount"));
		map.put("orderAmount", record.getStr("totalAmount"));
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
		render("order_detail.html");
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

	public void orderReview() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String orderId = getPara("orderId");
		String taskId = getPara("taskId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().orderAgainDetail(orderId);
		List<Map<String, String>> images = getImageSrc(orderDetailList);

		order.set("statusName", getStatusName(order.getInt("status")));

		boolean isCheck = false;
		if (user != null && getPara("assignee", "").contains(user.getUsername())) {
			isCheck = true;
		}
		setAttr("isCheck", isCheck);

		//审核订单后将message中是否阅读改为是
		Message message=MessageQuery.me().findByObjectIdAndToUserId(orderId,user.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}

		Boolean isEdit = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROCEDURE_REVIEW_EDIT + sellerCode) ;
		isEdit = (isEdit != null && isEdit) ? true : false;
		setAttr("isEdit", isEdit);

		setAttr("taskId", taskId);
		setAttr("order", order);
		setAttr("images", images);
		setAttr("orderDetailList", orderDetailList);

		List<Record> productList = SellerProductQuery.me().findProductListForApp(sellerId, "", "","",null,null);

		Map<String, Object> sellerProductInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> sellerProductItems = new ArrayList<>();

		for (Record record : productList) {
			Map<String, Object> item = new HashMap<>();

			String sellProductId = record.get("sell_product_id");
			item.put("title", record.getStr("custom_name"));
			item.put("value", sellProductId);

			sellerProductItems.add(item);
			sellerProductInfoMap.put(sellProductId, record);
		}

		setAttr("sellerProductInfoMap", JSON.toJSON(sellerProductInfoMap));
		setAttr("sellerProductItems", JSON.toJSON(sellerProductItems));

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

		List<SalesOutstock> outList = SalesOutstockQuery.me().findListByOrderId(id);

		if (outList.size() > 0) {
			StringBuilder outRemark = new StringBuilder();
			int num = 1;
			for (int i = 0; i < outList.size(); i++) {
				if (StrKit.notBlank(outList.get(i).getRemark())) {
					outRemark.append(num + "." + outList.get(i).getRemark() + "<br>");
					num++;
				}
			}
			if (outRemark.length() > 0) {
				setAttr("outRemark", outRemark.toString());
			}
		}

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
		if (comments.size()==0) {
			String modifyPrice = modifyPrice(id);
			setAttr("modifyPrice", modifyPrice);
		}
		render("operate_history.html");
	}

	private String modifyPrice(String ordedId) {
		List<Record> orderDetails = SalesOrderDetailQuery.me().findByOrderId(ordedId);
		StringBuilder stringBuilder = new StringBuilder();
		for (Record record : orderDetails) { // 若修改了产品价格或数量，则写入相关日志信息
			if (!record.getInt("price").equals(record.getInt("product_price"))) {
				double conver_relate = Double.parseDouble(record.getStr("convert_relate"));
				double price = Double.parseDouble(record.getStr("price"));
				double product_price = Double.parseDouble(record.getStr("product_price"));
				double smallproductPrice=product_price/conver_relate;
				double smallprice=price/conver_relate;
				smallproductPrice=new BigDecimal(smallproductPrice).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				smallprice=new BigDecimal(smallprice).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				stringBuilder.append("●" + record.getStr("custom_name") + "<br>");
				stringBuilder.append("-每" + record.getStr("big_unit") + "价格修改为"+ product_price+ "(" + price+ ")<br>");
				stringBuilder.append("-每" + record.getStr("small_unit") + "价格修改为"+ smallproductPrice+ "(" +  smallprice + ")<br>");
			}
		}

		return stringBuilder.toString();
	}

	private String priceChange(String orderId) {
		String[] productNames = getParaValues("productName");
		String[] bigUnits = getParaValues("bigUnit");
		String[] smallUnits = getParaValues("smallUnit");
		String[] bigPriceSpans = getParaValues("bigPriceSpan");
		String[] smallPriceSpans = getParaValues("smallPriceSpan");
		Record salesOrder = SalesOrderQuery.me().findRecordById(orderId);
		String salesOrderName="";
		if (null==salesOrder.getStr("customer_name")){
			salesOrderName=salesOrder.getStr("salesName");
		}else {
			salesOrderName=salesOrder.getStr("customer_name");
		}
		StringBuilder stringBuilder = new StringBuilder(" <div class=\"weui-cell weui-cell_access\">\n" +
				"	        <div></div>\n" +
				"	        <p>\n" +
				"	          价格修改\n" +
				"	          <span class=\"fr\">"+salesOrder.getStr("createDate")+"</span>\n" +
				"	        </p>\n" +
				"	        <p>操作人："+salesOrderName+"</p>\n" +
				"	         <p>");
		boolean priceChange=false;
		List<Record> orderDetails = SalesOrderDetailQuery.me().findByOrderId(orderId);
		for (int i = 0; i < productNames.length; i++) {
			if (Double.parseDouble(orderDetails.get(i).getStr("price"))!=Double.parseDouble(bigPriceSpans[i])) {
				double conver_relate = Double.parseDouble(orderDetails.get(i).getStr("convert_relate"));
				double price = Double.parseDouble(orderDetails.get(i).getStr("price"));
				double smallprice=price/conver_relate;
				smallprice=new BigDecimal(smallprice).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
				stringBuilder.append("●" + productNames[i] + "<br>");
				stringBuilder.append("-每" + bigUnits[i] + "价格修改为"+ bigPriceSpans[i]+ "(" + price+ ")<br>");
				stringBuilder.append("-每" + smallUnits[i] + "价格修改为"+ smallPriceSpans[i]+ "(" +  smallprice+ ")<br>");
				priceChange=true;
			}
		}
		stringBuilder.append("</p>\n" +
				"	      </div>");

		if (priceChange) {
			return stringBuilder.toString();
		}else {
			return null;
		}
	}

//	private String priceChange(String orderId) {
//		List<Record> orderDetails = SalesOrderDetailQuery.me().findByOrderId(orderId);
//		Record salesOrder = SalesOrderQuery.me().findRecordById(orderId);
//		String salesOrderName="";
//		if (null==salesOrder.getStr("customer_name")){
//			salesOrderName=salesOrder.getStr("salesName");
//		}else {
//			salesOrderName=salesOrder.getStr("customer_name");
//		}
//		StringBuilder stringBuilder = new StringBuilder(" <div class=\"weui-cell weui-cell_access\">\n" +
//				"	        <div></div>\n" +
//				"	        <p>\n" +
//				"	          价格修改\n" +
//				"	          <span class=\"fr\">"+salesOrder.getStr("createDate")+"</span>\n" +
//				"	        </p>\n" +
//				"	        <p>操作人："+salesOrderName+"</p>\n" +
//				"	         <p>");
//
//		boolean priceChange=false;
//		for (Record record : orderDetails) { // 若修改了产品价格或数量，则写入相关日志信息
//			if (!record.getInt("price").equals(record.getInt("product_price"))) {
//				double conver_relate = Double.parseDouble(record.getStr("convert_relate"));
//				double price = Double.parseDouble(record.getStr("price"));
//				double product_price = Double.parseDouble(record.getStr("product_price"));
//				double smallproductPrice=product_price/conver_relate;
//				double smallprice=price/conver_relate;
//				smallproductPrice=new BigDecimal(smallproductPrice).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//				smallprice=new BigDecimal(smallprice).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
//				stringBuilder.append("●" + record.getStr("custom_name") + "<br>");
//				stringBuilder.append("-每" + record.getStr("big_unit") + "价格修改为"+ product_price+ "(" + price+ ")<br>");
//				stringBuilder.append("-每" + record.getStr("small_unit") + "价格修改为"+ smallproductPrice+ "(" +  smallprice + ")<br>");
//				priceChange=true;
//			}
//		}
//		stringBuilder.append("</p>\n" +
//				"	      </div>");
//		if (priceChange) {
//			return stringBuilder.toString();
//		}else {
//			return "";
//		}
//	}


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

	public synchronized void salesOrder() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);

		Map<String, String[]> paraMap = getParaMap();
		String result = this.saveOrder(paraMap, user, sellerId, sellerCode);
		if (StrKit.isBlank(result)) {
			renderAjaxResultForSuccess("下单成功");
		} else {
			renderAjaxResultForError(result);
		}
	}

	private String saveOrder(final Map<String, String[]> paraMap, final User user, final String sellerId,
	                          final String sellerCode) {
		final String[] result = {""};
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				String orderId = StrKit.getRandomUUID();
				Date date = new Date();
				String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);
				// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
				String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
						                 + DateUtils.format("yyMMdd", date) + OrderSO;
				if (null==StringUtils.getArrayFirst(paraMap.get("customerType"))) {
					result[0] = "客户类型不能为空";
					return false;
				}
				if (!SalesOrderQuery.me().insertForApp(paraMap, orderId, orderSn, sellerId, user.getId(), date,
						user.getDepartmentId(), user.getDataArea())) {
					result[0] = "下单失败";
					return false;
				}

				String[] sellProductIds = paraMap.get("sellProductId");
				// 常规商品
				if (sellProductIds != null && sellProductIds.length > 0) {

					for (int index = 0; index < sellProductIds.length; index++) {
						if (StrKit.notBlank(sellProductIds[index])) {
							String message = SalesOrderDetailQuery.me().insertForApp(paraMap, orderId, sellerId, sellerCode, user.getId(), date,
									user.getDepartmentId(), user.getDataArea(), index);
							if (StrKit.notBlank(message)) {
								result[0] = message;
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
							String message = SalesOrderDetailQuery.me().insertForAppGift(paraMap, orderId, sellerId, sellerCode, user.getId(),
									date, user.getDepartmentId(), user.getDataArea(), index);
							if (StrKit.notBlank(message)) {
								result[0] = message;
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
							String message = SalesOrderDetailQuery.me().insertForAppComposition(sellerProduct, orderId, sellerId, sellerCode,
									user.getId(), date, user.getDepartmentId(), user.getDataArea(),
									Integer.parseInt(number), user.getId());
							if (StrKit.notBlank(message)) {
								result[0] = message;
								return false;
							}
						}
					}
				}

				//是否开启
				boolean isStartProc = isStart(sellerCode, paraMap);
				String proc_def_key = StringUtils.getArrayFirst(paraMap.get("proc_def_key"));

				if (isStartProc && StrKit.notBlank(proc_def_key)) {
					String message = start(orderId, StringUtils.getArrayFirst(paraMap.get("customerName")), proc_def_key);
					if (StrKit.notBlank(message)) {
						result[0] = message;
						return false;
					}
				} else {
					SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);
					OrderReviewUtil.sendOrderMessage(sellerId, StringUtils.getArrayFirst(paraMap.get("customerName")), "订单审核通过", user.getId(), user.getId(),
							user.getDepartmentId(), user.getDataArea(),orderId);
				}

				return true;
			}
		});
		return result[0];
	}

	private boolean isStart(String sellerCode, Map<String, String[]> paraMap) {
		//是否开启
		Boolean startProc = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_PROCEDURE_REVIEW + sellerCode);
		if(startProc != null && startProc) {
			return true;
		}
		//超过数量(件)
		Float startNum = OptionQuery.me().findValueAsFloat(Consts.OPTION_WEB_PROC_NUM_LIMIT + sellerCode);
		Float totalNum = Float.valueOf(StringUtils.getArrayFirst(paraMap.get("totalNum")));
		if(startNum != null && totalNum > startNum) {
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

	private String start(String orderId, String customerName, String proc_def_key) {

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
				return "您没有配置审核人,请联系管理员";
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
			return "下单失败";
		}

		return "";
	}

	@Before(Tx.class)
	public void complete() {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

				String orderId = getPara("id");
				String taskId = getPara("taskId");
				String comment = getPara("comment");
				String refuseReson = getPara("refuseReson", "");
				Integer pass = getParaToInt("pass", 1);
				Integer edit = getParaToInt("edit", 0);

				Record salesOrder = SalesOrderQuery.me().findRecordById(orderId);
				if (Integer.parseInt(salesOrder.getStr("status"))!=Consts.SALES_ORDER_STATUS_DEFAULT) {
					renderAjaxResultForError("订单已审核");
					return false;
				}

				Map<String, Object> var = Maps.newHashMap();
				var.put("pass", pass);
				var.put(Consts.WORKFLOW_APPLY_COMFIRM, user);
				StringBuilder stringBuilder = new StringBuilder();
				String priceChange = priceChange(orderId);
				if (null!=priceChange) {
					stringBuilder.append(priceChange);
				}

				//是否改价格
				if (pass == 1 && edit == 1) {

					Map<String, String[]> paraMap = getParaMap();
					String result = editOrder(paraMap, user);
					if (StrKit.notBlank(result)) {
						renderAjaxResultForError(result);
						return false;
					}
					String editInfo = buildEditInfo();
					String addInfo = buildAddInfo();
					comment = "通过" + " 修改订单<br>" + editInfo + addInfo;
				} else {
					comment = (pass == 1 ? "通过" : "拒绝") + " " + (comment == null ? "" : comment) + " "
							          + (refuseReson == "undefined" ? "" : refuseReson);
					var.put("comment", comment);
				}
				String comments = buildComments(Consts.OPERATE_HISTORY_TITLE_ORDER_REVIEW, DateUtils.now(), user.getRealname(), comment);
				stringBuilder.append(comments);
				WorkFlowService workflowService = new WorkFlowService();

				int completeTask = workflowService.completeTask(taskId, stringBuilder.toString(), var);
				if (completeTask==1) {
					renderAjaxResultForError("已审核");
					return false;
				}

				//审核订单后将message中是否阅读改为是
				Message message = MessageQuery.me().findByObjectIdAndToUserId(orderId, user.getId());
				if (null != message) {
					message.setIsRead(Consts.IS_READ);
					message.update();
				}

				renderAjaxResultForSuccess("订单审核成功");
				return true;
			}
		});

	}

	private String editOrder(Map<String, String[]> paraMap, User user) {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		Date date = new Date();

		if (!SalesOrderQuery.me().updateForApp(paraMap, user.getId(), date)) {
			return "订单审核修改订单失败";
		}

		String[] orderDetailIds = getParaValues("orderDetailId");
		for (int index = 0; index < orderDetailIds.length; index++) {
			if (!SalesOrderDetailQuery.me().updateForApp(paraMap, index, date)) {
				return "订单审核修改订单详细失败";
			}
		}

		String[] addSellProductIds = getParaValues("addSellProductId");

		if (addSellProductIds != null && addSellProductIds.length > 0) {

			for (int index = 0; index < addSellProductIds.length; index++) {
				if (StrKit.notBlank(addSellProductIds[index])) {
					String message = SalesOrderDetailQuery.me().addForApp(paraMap, StringUtils.getArrayFirst(paraMap.get("id")), sellerId, sellerCode, user.getId(), date,
							user.getDepartmentId(), user.getDataArea(), index);
					if (StrKit.notBlank(message)) {
						return message;
					}

				}
			}
		}

		return "";
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

	private String buildAddInfo() {
		String[] addSellProductIds = getParaValues("addSellProductId");
		String[] productNames = getParaValues("addProductName");
		String[] addIsGifts = getParaValues("addIsGift");
		String[] bigUnits = getParaValues("addBigUnit");
		String[] smallUnits = getParaValues("addSmallUnit");
		String[] bigPrices = getParaValues("addBigPrice");
		String[] bigNums = getParaValues("addBigNum");
		String[] smallPrices = getParaValues("addSmallPrice");
		String[] smallNums = getParaValues("addSmallNum");
		String[] bigPriceSpans = getParaValues("addBigPriceSpan");
		String[] smallPriceSpans = getParaValues("addSmallPriceSpan");

		StringBuilder stringBuilder = new StringBuilder();

		if (addSellProductIds != null && addSellProductIds.length > 0) {
			for (int index = 0; index < productNames.length; index++) { // 若修改了产品价格或数量，则写入相关日志信息
				if (StrKit.notBlank(addSellProductIds[index])) {
					if ("0".equals(addIsGifts[index])) {
						stringBuilder.append("●添加商品" + productNames[index] + "<br>");
					} else {
						stringBuilder.append("●添加赠品" + productNames[index] + "<br>");
					}

					if (!bigPrices[index].equals(bigPriceSpans[index])) {
						stringBuilder.append("-每" + bigUnits[index] + "价格为" + bigPrices[index] + "(" + bigPriceSpans[index] + ")<br>");
					}
					if (!smallPrices[index].equals(smallPriceSpans[index])) {
						stringBuilder.append("-每" + smallUnits[index] + "价格为" + smallPrices[index] + "(" + smallPriceSpans[index] + ")<br>");
					}

					stringBuilder.append("-" + bigUnits[index] + "数量为" + bigNums[index] + "<br>");

					stringBuilder.append("-" + smallUnits[index] + "数量为" + smallNums[index] + "<br>");
				}
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
		if (StrKit.notBlank(procInstId)) {
			if(salesOrder.getStatus()==Consts.SALES_ORDER_STATUS_DEFAULT) {
				workflow.deleteProcessInstance(salesOrder.getProcInstId());
			}
		}
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_CANCEL);
		salesOrder.setModifyDate(new Date());
		if (!salesOrder.saveOrUpdate()) {
			renderAjaxResultForError("取消订单失败");
			return;
		}
		renderAjaxResultForSuccess("订单撤销成功");
	}

	@Before(WechatJSSDKInterceptor.class)
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

	public void getOrderInfo() {
		String orderId = getPara("orderId");
		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetailList = SalesOrderDetailQuery.me().orderAgainDetail(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("order", order);
		map.put("orderDetail", orderDetailList);

		renderJson(map);
	}

	public void getOrderProductDetail() {
		String orderId = getPara("orderId");

		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);
		Map<String, Object> map = new HashMap<>();
		map.put("orderDetail", orderDetail);
		renderJson(map);
	}

	public void getBizUser() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<SalesOrder> orders = SalesOrderQuery.me().findBySellerIdAndDataArea(sellerId,dataArea,startDate,endDate);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		List<Map<String, Object>> bizUsers = new ArrayList<>();
		bizUsers.add(all);
		for (SalesOrder order : orders) {
			Map<String, Object> items = new HashMap<>();
			items.put("title", order.getStr("realname"));
			items.put("value", order.getBizUserId());
			bizUsers.add(items);
		}

		renderJson(bizUsers);
	}

}