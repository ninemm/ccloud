package org.ccloud.front.controller;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.utils.DateUtils;
import org.ccloud.workflow.service.WorkFlowService;

@RouterMapping(url = "/refund")
@RequiresPermissions(value = { "/admin/salesRefund", "/admin/dealer/all" }, logical = Logical.OR)
public class RefundController extends BaseFrontController{
	
	//申请退货
	public void index() {
		
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getName());
			customerTypes.add(item);
		}
		
		String history = getPara("history");
		setAttr("history", history);		
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("refund.html");		
	}
	
	public void stockOrder() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> outStockList = SalesOutstockQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("outStockList", outStockList.getList());
		renderJson(map);
	}	
	
	
	public void detail() {
		String outstock_id = getPara("id");
		Record outstock = SalesOutstockQuery.me().findMoreById(outstock_id);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstock_id);
		List<Record> refundDetail = SalesRefundInstockQuery.me().findByOutstockId(outstock_id);
		setAttr("salesRefundDetail", refundDetail);
		setAttr("salesOutstockDetail", outstockDetail);
		setAttr("outstock", outstock);
		setAttr("outstock_id", outstock_id);
		render("refund_detail.html");
	}
	
	//退货订单
	public void myRefund() {

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getName());
			customerTypes.add(item);
		}
		
		String history = getPara("history");
		setAttr("history", history);
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("refund_list.html");
	}
	
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String status = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> refundList = SalesRefundInstockQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword, status,
				customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("refundList", refundList.getList());
		map.put("user", user);
		renderJson(map);
	}
	
	public void refundDetail() {
		String refundId = getPara("id");

		Record refund = SalesRefundInstockQuery.me().findMoreById(refundId);
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);

		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("sales_refund_detail.html");
	}
		
	
	//退货
	public void refundGood() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
        		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);	
        		String outStockId = getPara("outStockId");
        		Record outstock = SalesOutstockQuery.me().findMoreById(outStockId);
        		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outStockId);        		
        		String[] bNum = getParaValues("bNum");
        		String[] sNum = getParaValues("sNum");
        		String[] sellerProductIds = getParaValues("sellProductId");
        		String[] isGift = getParaValues("isGift");
        		String remark = getPara("remark");
        		String paymentType = getPara("receiveType");
        		//退货商品总价格  
        		BigDecimal totalRejectAmount = new BigDecimal(0);
        		String instockId = StrKit.getRandomUUID();
        		Date date = new Date();
        		SalesRefundInstock salesRefundInstock = SalesRefundInstockQuery.me()
        				.insertByApp(instockId, outstock, user.getId(), sellerId, sellerCode, paymentType, date, remark);
        		for (int i = 0; i < sellerProductIds.length; i++) {
        			for (Record record : outstockDetail) {
        				if (record.getStr("sell_product_id").equals(sellerProductIds[i]) && record.getStr("is_gift").equals(isGift[i])) {
        					Map<String, Object> map = SalesRefundInstockDetailQuery.me().insertByApp(record, instockId, sellerId, date, 
        							bNum[i], sNum[i]);
        					String status = map.get("status").toString();
        					if (status.equals("false")) {
        						return false;
        					} else {
        						totalRejectAmount = totalRejectAmount.add(new BigDecimal(map.get("productAmount").toString()));
        					}
        				}
        			}
        		}
        		
        		salesRefundInstock.setTotalRejectAmount(totalRejectAmount);
        		if (!salesRefundInstock.save()) {            	
        			return false;
        		}
            	return true;
            }
        });
		if (isSave) {
			renderAjaxResultForSuccess("退货单保存成功");
		} else {
			renderAjaxResultForError("系统错误保存失败");
		}
	}

	public void operateHistory() {
		keepPara();
		
		String id = getPara("id");

		Record salesRefund = SalesRefundInstockQuery.me()._findRecordById(id);
		setAttr("salesRefund", salesRefund);

		String salesRefundInfo = buildOutstockInfo(id);
		setAttr("salesRefundInfo", salesRefundInfo);
		
		render("refund_operate_history.html");
	}
	
	private String buildOutstockInfo(String refundId) {
		List<Record> rfundDetails = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for (Record record : rfundDetails) { // 若修改了产品价格或数量，则写入相关日志信息
			if (!(record.get("reject_product_count").toString()).equals(record.getInt("product_count").toString())) {
					stringBuilder.append("●" + record.getStr("custom_name") + "<br>");
					int convert = record.getInt("convert_relate");
					stringBuilder.append("-" + record.getStr("big_unit") + "数量修改为"+ Math.round(record.getInt("reject_product_count")/convert) + "(" + Math.round(record.getInt("product_count")/convert) + ")<br>");
					stringBuilder.append("-" + record.getStr("small_unit") + "数量修改为"+ Math.round(record.getInt("reject_product_count")%convert) + "(" + Math.round(record.getInt("product_count")%convert) + ")<br>");
			}
		}
		
		return stringBuilder.toString();
	}

	public void cancel() {

		String orderId = getPara("orderId");
		SalesRefundInstock salesRefundInstock = SalesRefundInstockQuery.me().findById(orderId);
		WorkFlowService workflow = new WorkFlowService();

		//暂时退货没有流程
//		String procInstId = salesRefundInstock.getProcInstId();
//		if (StrKit.notBlank(procInstId)) {
//			if(salesRefundInstock.getStatus()==0) {
//				workflow.deleteProcessInstance(salesRefundInstock.getProcInstId());
//			}
//		}

		salesRefundInstock.setStatus(Consts.SALES_REFUND_INSTOCK_CANCEL);

		if (!salesRefundInstock.saveOrUpdate()) {

			renderAjaxResultForError("取消订单失败");
			return;
		}

		renderAjaxResultForSuccess("订单撤销成功");
	}

	@Before(Tx.class)
	public void complete() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		String orderId = getPara("id");
		Integer pass = getParaToInt("pass", 1);
		SalesRefundInstockQuery.me().updateConfirm(orderId, pass == 1 ? Consts.SALES_REFUND_INSTOCK_PASS : Consts.SALES_REFUND_INSTOCK_REFUSE, new Date(), user.getId());

		renderAjaxResultForSuccess("订单审核成功");
	}

}