package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class SalesOutstockExcel {
	@Excel(name = "订单号")
	private String outstockSn;
	@Excel(name = "客户信息")
	private String customer;
	@Excel(name = "客户类型")
	private String customerType;
	@Excel(name = "联系人")
	private String contact;
	@Excel(name = "联系电话")
	private String mobile;
	@Excel(name = "业务员")
	private String bizUser;
	@Excel(name = "审核时间")
	private String completeDate;
	@Excel(name = "订单金额")
	private String totalAmount;
	@Excel(name = "付款方式")
	private String receiveType;
	@Excel(name = "打印状态")
	private String isPrint;
	@Excel(name = "打印时间")
	private String printDate;
	@Excel(name = "出库状态")
	private String status;
	@Excel(name = "下单日期")
	private String saveDate;
	@Excel(name = "下单时间")
	private String createDate;
	@Excel(name = "商品名	")
	private String productName;
	@Excel(name = "规格")
	private String valueName;
	@Excel(name = "数量")
	private String productCount;
	@Excel(name = "换算")
	private String creatconvertRelate;
	@Excel(name = "单位")
	private String unit;
	@Excel(name = "价格")
	private String productPrice;
	@Excel(name = "条码")
	private String barCode;
	@Excel(name = "是否为赠品")
	private String isGift;
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
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
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
	public String getBizUser() {
		return bizUser;
	}
	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
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
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSaveDate() {
		return saveDate;
	}
	public void setSaveDate(String saveDate) {
		this.saveDate = saveDate;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getValueName() {
		return valueName;
	}
	public void setValueName(String valueName) {
		this.valueName = valueName;
	}
	public String getProductCount() {
		return productCount;
	}
	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}
	public String getCreatconvertRelate() {
		return creatconvertRelate;
	}
	public void setCreatconvertRelate(String creatconvertRelate) {
		this.creatconvertRelate = creatconvertRelate;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getIsGift() {
		return isGift;
	}
	public void setIsGift(String isGift) {
		this.isGift = isGift;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	
	
}
