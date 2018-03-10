package org.ccloud.controller.member;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
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
					String userId = MemberJoinSellerQuery.me().findUserId(member.getId(), sellerId).getUserId();
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
		CustomerType customerType = CustomerTypeQuery.me().findBySellerCustomer(sellerId, moreInfo.get("customerId"));
		String customerTypeName = customerType.getName();
		String customerTypeProcDefKey = customerType.getProcDefKey();

		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);
		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
		String orderSn = "SO" + sellerCode + customerType.getCode() + DateUtils.format("yyMMdd", date) + OrderSO;

		String salesOrderId = SalesOrderQuery.me().memberInsert(moreInfo, customerType.getId(), orderId, orderSn, sellerId, user.getId(), date,
				user.getDepartmentId(), user.getDataArea());
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
}
