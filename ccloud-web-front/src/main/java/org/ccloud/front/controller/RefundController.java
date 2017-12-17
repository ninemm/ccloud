package org.ccloud.front.controller;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SalesRefundInstockDetailQuery;
import org.ccloud.model.query.SalesRefundInstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/refund")
public class RefundController extends BaseFrontController{
	
	public void index() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

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
	
	public void myRefund() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("refund_list.html");
	}
	
	public void list() {
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
        				if (record.getStr("sell_product_id").equals(sellerProductIds[i])) {
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

}