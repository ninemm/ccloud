package org.ccloud.controller.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
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

	public void getCustomerType() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		JSONArray productList = JSON.parseArray(getPara("productList"));
		List<String> sellProductIdList = new ArrayList<>();
		for (int i = 0; i < productList.size(); i++) {
			JSONObject obj = productList.getJSONObject(i);
			sellProductIdList.add(obj.getString("sellProductId"));
		}

		List<Record> customerTypes = CustomerTypeQuery.me().findByMember(member.getCustomerId(), sellProductIdList);

		List<List<Map<String, Object>>> customerTypeList = new ArrayList<>();

		for(int i = 0; i < customerTypes.size(); i++) {

			String ids = customerTypes.get(i).get("id");
			String names = customerTypes.get(i).get("name");

			List<String> idList = Splitter.on(",")
					.trimResults()
					.omitEmptyStrings()
					.splitToList(ids);

			List<String> nameList = Splitter.on(",")
					.trimResults()
					.omitEmptyStrings()
					.splitToList(names);
			if (idList.size() > 1) {
				List<Map<String, Object>> customerType = new ArrayList<>();
				for (int j = 0; j < idList.size(); j++) {
					Map<String, Object> item = new HashMap<>();
					item.put("title", nameList.get(j));
					item.put("value", idList.get(j));
					customerType.add(item);
				}
				customerTypeList.add(customerType);
			}
		}

		renderJson(customerTypeList);
	}

	public synchronized void salesOrder() {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

				Map<String, String[]> paraMap = getParaMap();

				//存储通用的数据
				Map<String, String> moreInfo = new HashMap<>();
				moreInfo.put("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
				moreInfo.put("customerId", StringUtils.getArrayFirst(paraMap.get("customerId")));
				moreInfo.put("customerName", StringUtils.getArrayFirst(paraMap.get("customerName")));
				moreInfo.put("contact", StringUtils.getArrayFirst(paraMap.get("contact")));
				moreInfo.put("mobile", StringUtils.getArrayFirst(paraMap.get("mobile")));
				moreInfo.put("address", StringUtils.getArrayFirst(paraMap.get("address")));
				moreInfo.put("deliveryAddress", StringUtils.getArrayFirst(paraMap.get("deliveryAddress")));
				moreInfo.put("receiveType", StringUtils.getArrayFirst(paraMap.get("receiveType")));
				moreInfo.put("receiveTypeName", StringUtils.getArrayFirst(paraMap.get("receiveTypeName")));
				moreInfo.put("deliveryDate", StringUtils.getArrayFirst(paraMap.get("deliveryDate")));

				String[] sellerIds = paraMap.get("sellerId");

				List<String> sellerIdList = new ArrayList<>();
				//找出所有sellerId
				for (int i = 0; i < sellerIds.length; i++)
					if (!sellerIdList.contains(sellerIds[i])) sellerIdList.add(sellerIds[i]);

				for (int i = 0; i < sellerIdList.size(); i++) {
					String sellerId = sellerIdList.get(i);
					String sellerCode = SellerQuery.me().findById(sellerId).getSellerCode();
					String userId = MemberJoinSellerQuery.me().findUser(member.getId(), sellerId).getUserId();
					User user = UserQuery.me().findById(userId);

					List<Map<String, String>> paraList = new ArrayList<>();
					Double totalNum = 0.00;
					double total = 0;

					for (int j = 0; j < sellerIds.length; j++) {
						//根据sellerId筛选产品
						if (sellerIds[j].equals(sellerId)) {
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
							totalNum = totalNum + Double.parseDouble(paraMap.get("bigNum")[j]) +
									Double.parseDouble(paraMap.get("smallNum")[j]) / Double.parseDouble(paraMap.get("convert")[j]);
							paraList.add(para);
						}
					}
					total = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					totalNum = new BigDecimal(totalNum).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

					moreInfo.remove("total");
					moreInfo.remove("totalNum");
					moreInfo.put("total", Double.valueOf(total).toString());
					moreInfo.put("totalNum", Double.valueOf(totalNum).toString());

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

		String dealerSellerId = sellerId;
		String deptId = DepartmentQuery.me().findBySellerId(sellerId).getId();
		List<Department> departmentList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(deptId);
		//根据sellerId查出他的经销商sellerId
		for(Department department :departmentList)
			if(department.getStr("seller_type").equals("0")) {
				dealerSellerId = department.getStr("seller_id");
				break;
			}

		CustomerType customerType = CustomerTypeQuery.me().findBySellerCustomer(dealerSellerId, moreInfo.get("customerId"));
		String customerTypeName = customerType.getName();
		String customerTypeProcDefKey = customerType.getProcDefKey();

		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);
		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
		String orderSn = "SO" + sellerCode + customerType.getCode() + DateUtils.format("yyMMdd", date) + OrderSO;

		String salesOrderId = SalesOrderQuery.me().memberInsert(moreInfo, customerType.getId(), orderId, orderSn, sellerId, user.getId(), date,
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
//		boolean isStartProc = isStart(sellerCode, moreInfo);
		boolean isStartProc = false;
		String proc_def_key = customerTypeProcDefKey;

		if (isStartProc && StrKit.notBlank(proc_def_key)) {
			String message = start(orderId, customerTypeName, proc_def_key, user, sellerId, sellerCode);
			if (StrKit.notBlank(message)) {
				return message;
			}
		} else {
			SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);
			OrderReviewUtil.sendOrderMessage(sellerId, customerTypeName, "订单审核通过", user.getId(), user.getId(),
					user.getDepartmentId(), user.getDataArea(),orderId);
		}
		return "";
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
		param.put(Consts.WORKFLOW_APPLY_USER, user);
		param.put(Consts.WORKFLOW_APPLY_SELLER_ID, sellerId);
		param.put(Consts.WORKFLOW_APPLY_SELLER_CODE, sellerCode);
		param.put("customerName", customerName);
		param.put("orderId", orderId);


		String toUserId = "";

		if(Consts.PROC_ORDER_REVIEW_ONE.equals(proc_def_key)) {

			User orderReviewer = UserQuery.me().findOrderReviewerByDeptId(user.getDepartmentId());
			if (orderReviewer == null) {
				return "您没有配置审核人,请联系管理员";
			}
			param.put("manager", orderReviewer.getUsername());
			toUserId = orderReviewer.getId();
		}

		String procInstId = workflow.startProcess(orderId, proc_def_key, param);

		salesOrder.setProcKey(proc_def_key);
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_DEFAULT);
		salesOrder.setProcInstId(procInstId);

		if(!salesOrder.update()) {
			return "下单失败";
		}

		OrderReviewUtil.sendOrderMessage(sellerId, customerName, "订单审核", user.getId(), toUserId, user.getDepartmentId(), user.getDataArea(),orderId);

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
}
