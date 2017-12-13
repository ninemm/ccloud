package org.ccloud.front.controller;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.SalesRefundInstockDetail;
import org.ccloud.model.User;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SalesRefundInstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/refund")
public class RefundController extends BaseFrontController{
	
	public void index() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Page<Record> salesOutstockList= SalesOutstockQuery.me().findByBizUserId(getPageNumber(), getPageSize(),user.getId());
		setAttr("salesOutstockList", salesOutstockList);
		render("refund.html");
	}
	
	
	public void detail() {
		String outstock_id = getPara("id");
		List<Record> salesOutstockDetail = SalesOutstockDetailQuery.me().findById1(outstock_id);
		setAttr("salesOutstockDetail", salesOutstockDetail);
		setAttr("outstock_id", outstock_id);
		render("refundDetail.html");
	}
	
	//退货
	public void refundGood() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String outstock_id = getPara("outstock_id");
		String[] number=getParaValues("number");
		String[] sell_product_id=getParaValues("sell_product_id");
		//退货商品总价格  
		BigDecimal total_reject_amount=new BigDecimal(0);
		for (int i = 0; i < sell_product_id.length; i++) {
			//退货数量为0 跳过
			if (number[i].equals(0)) {
				break;
			}
			Record record = SalesOutstockDetailQuery.me().findByIdAndSellProductId(outstock_id,sell_product_id[i]);
			BigDecimal product_price = new BigDecimal(record.getStr("product_price"));
			BigDecimal amount=product_price.multiply(new BigDecimal(number[i]));
			total_reject_amount=total_reject_amount.add(amount);
		}
		//退货单总表
		Record record = SalesOutstockDetailQuery.me().findByIdAndSellProductId(outstock_id,sell_product_id[0]);
		SalesRefundInstock salesRefundInstock=new SalesRefundInstock();
		String salesRefundInstockId=StrKit.getRandomUUID();
		String newSn = SalesRefundInstockQuery.me().getNewSn(record.getStr("seller_id"));
		// SR + (机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
		String instockSn = "SR" + record.get("sellerCode") +  record.get("customerTypeCode")
		+ record.get("warehouseCode")+ DateUtils.format("yyMMdd", new Date()) + newSn;
		
		salesRefundInstock.set("id",salesRefundInstockId);
		salesRefundInstock.set("instock_sn",instockSn);
		salesRefundInstock.set("seller_id", record.getStr("seller_id"));
		salesRefundInstock.set("warehouse_id", record.getStr("warehouse_id"));
		salesRefundInstock.set("customer_id", record.getStr("customer_id"));
		salesRefundInstock.set("customer_type_id",record.getStr("customer_type_id"));
		salesRefundInstock.set("biz_user_id", record.getStr("biz_user_id"));
		salesRefundInstock.set("biz_date", record.getStr("biz_date"));
		salesRefundInstock.set("input_user_id", user.getId());
		salesRefundInstock.set("status", 0);
		salesRefundInstock.set("total_reject_amount", total_reject_amount);
//		salesRefundInstock.set("inventory_amount","");	商品库存价值
		salesRefundInstock.set("outstock_id",outstock_id);
		salesRefundInstock.set("payment_type", record.getStr("receive_type"));
		salesRefundInstock.set("proc_key", record.getStr("proc_key"));
		salesRefundInstock.set("proc_inst_id", record.getStr("proc_ins_id"));
		salesRefundInstock.set("remark", record.getStr("remark"));
		salesRefundInstock.set("dept_id", record.getStr("dept_id"));
		salesRefundInstock.set("data_area", record.getStr("data_area"));
		salesRefundInstock.set("create_date",new Date());
		salesRefundInstock.save();
		
		SalesRefundInstockDetail salesRefundInstockDetail=new SalesRefundInstockDetail();
		for (int i = 0; i < sell_product_id.length; i++) {
			//退货数量为0 跳过
			if (number[i].equals("0")) {
				break;
			}
			record = SalesOutstockDetailQuery.me().findByIdAndSellProductId(outstock_id,sell_product_id[i]);
			BigDecimal product_price = new BigDecimal(record.getStr("product_price"));
			BigDecimal amount=product_price.multiply(new BigDecimal(number[i]));
			salesRefundInstockDetail.set("id", StrKit.getRandomUUID());
			salesRefundInstockDetail.set("refund_instock_id", salesRefundInstockId);
			salesRefundInstockDetail.set("sell_product_id", sell_product_id[i]);
			salesRefundInstockDetail.set("product_count", record.getStr("product_count"));
			salesRefundInstockDetail.set("product_amount", record.getStr("product_amount"));
			salesRefundInstockDetail.set("product_price",product_price);
//			salesRefundInstockDetail.set("cost", "");
//			salesRefundInstockDetail.set("total_cost", "");
			salesRefundInstockDetail.set("outstock_detail_id", record.getStr("id"));
			salesRefundInstockDetail.set("reject_product_price", product_price);
			salesRefundInstockDetail.set("reject_amount",amount);
			salesRefundInstockDetail.set("reject_product_count", number[i]);
			salesRefundInstockDetail.set("is_gift", record.getStr("is_gift"));
			salesRefundInstockDetail.set("remark",  record.getStr("remark"));
			salesRefundInstockDetail.set("dept_id",record.getStr("dept_id"));
			salesRefundInstockDetail.set("data_area", record.getStr("data_area"));
			salesRefundInstockDetail.set("create_date", new Date());
			salesRefundInstockDetail.save();
		}
		
	}
}