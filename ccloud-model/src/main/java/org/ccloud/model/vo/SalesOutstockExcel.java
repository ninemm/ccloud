package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class SalesOutstockExcel {
	@Excel(name = "订单号")
	private String outstockSn;
	@Excel(name = "客户")
	private String customer;
	@Excel(name = "客户类型")
	private String customerType;
	@Excel(name = "联系人")
	private String contact;
	@Excel(name = "联系电话")
	private String mobile;
	@Excel(name = "业务员")
	private String bizUser;
	@Excel(name = "订单金额")
	private String totalAmount;
	@Excel(name = "付款方式")
	private String receiveType;
	@Excel(name = "打印状态")
	private String isPrint;
	@Excel(name = "出库状态")
	private String status;
	@Excel(name = "创建时间")
	private String createDate;
	public String getOutstockSn() {
		return outstockSn;
	}
	public void setOutstockSn(String outstockSn) {
		this.outstockSn = outstockSn;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getReceiveType() {
		return receiveType;
	}
	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}
	public String getIsPrint() {
		return isPrint;
	}
	public void setIsPrint(String isPrint) {
		this.isPrint = isPrint;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getBizUser() {
		return bizUser;
	}
	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
	}
	
	
}
