package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class OutDetailExcel {
	@Excel(name = "业务单号")
	private String biz_bill_sn;
	@Excel(name = "业务员")
	private String realname;
	@Excel(name = "客户名")
	private String customer_name;
	@Excel(name = "仓库")
	private String cc_warehouse;
	@Excel(name = "产品名")
	private String sellerName;
	@Excel(name = "出库数量")
	private String out_count;
	@Excel(name = "出库金额")
	private String out_amount;
	@Excel(name = "出库单价")
	private String out_price;
	@Excel(name = "库存")
	private String balance_count;
	@Excel(name = "出库类型")
	private String biz_type;
	@Excel(name = "创建时间")
	private String create_date;

	public String getBiz_bill_sn() {
		return biz_bill_sn;
	}

	public void setBiz_bill_sn(String biz_bill_sn) {
		this.biz_bill_sn = biz_bill_sn;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public String getCc_warehouse() {
		return cc_warehouse;
	}

	public void setCc_warehouse(String cc_warehouse) {
		this.cc_warehouse = cc_warehouse;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getOut_count() {
		return out_count;
	}

	public void setOut_count(String out_count) {
		this.out_count = out_count;
	}

	public String getOut_amount() {
		return out_amount;
	}

	public void setOut_amount(String out_amount) {
		this.out_amount = out_amount;
	}

	public String getOut_price() {
		return out_price;
	}

	public void setOut_price(String out_price) {
		this.out_price = out_price;
	}

	public String getBalance_count() {
		return balance_count;
	}

	public void setBalance_count(String balance_count) {
		this.balance_count = balance_count;
	}

	public String getBiz_type() {
		return biz_type;
	}

	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
}
