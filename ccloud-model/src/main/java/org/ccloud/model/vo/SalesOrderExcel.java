package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class SalesOrderExcel {
	@Excel(name = "订单号")
	private String orderSn;
	@Excel(name = "客户")
	private String customer;
	@Excel(name = "客户类型")
	private String customerType;
	@Excel(name = "联系人")
	private String ccontact;
	@Excel(name = "联系电话")
	private String cmobile;
	@Excel(name = "业务员")
	private String bizUser;
	@Excel(name = "订单金额")
	private String totalAmount;
	@Excel(name = "商品名	")
	private String productName;
	@Excel(name = "规格")
	private String valueName;
	@Excel(name = "单位换算")
	private String creatconvertRelate;
	@Excel(name = "大单位数量")
	private String productCount;
	@Excel(name = "大单位价格")
	private String productPrice;
	@Excel(name = "小单位数量")
	private String smallCount;
	@Excel(name = "小单位价格")
	private String smallPrice;
	@Excel(name = "已出库数量（大）")
	private int bigOutCount;
	@Excel(name = "已出库数量（小）")
	private int smallOutCount;
	@Excel(name = "未出库数量（大）")
	private int bigLeftCount;
	@Excel(name = "未出库数量（小）")
	private int smallLeftCount;
	@Excel(name = "是否为组合商品")
	private String isComposite;
	@Excel(name = "是否为赠品")
	private String isGift;
	@Excel(name = "仓库")
	private String warehouse;
	@Excel(name = "付款方式")
	private String receiveType;
	@Excel(name = "出库状态")
	private String status;
	@Excel(name = "创建时间")
	private String createDate;
	public String getOrderSn() {
		return orderSn;
	}
	public void setOrderSn(String orderSn) {
		this.orderSn = orderSn;
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
	public String getSmallCount() {
		return smallCount;
	}
	public void setSmallCount(String smallCount) {
		this.smallCount = smallCount;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getSmallPrice() {
		return smallPrice;
	}
	public void setSmallPrice(String smallPrice) {
		this.smallPrice = smallPrice;
	}
	public String getIsComposite() {
		return isComposite;
	}
	public void setIsComposite(String isComposite) {
		this.isComposite = isComposite;
	}
	public String getIsGift() {
		return isGift;
	}
	public void setIsGift(String isGift) {
		this.isGift = isGift;
	}
	public String getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	public int getBigOutCount() {
		return bigOutCount;
	}
	public void setBigOutCount(int bigOutCount) {
		this.bigOutCount = bigOutCount;
	}
	public int getSmallOutCount() {
		return smallOutCount;
	}
	public void setSmallOutCount(int smallOutCount) {
		this.smallOutCount = smallOutCount;
	}
	public int getBigLeftCount() {
		return bigLeftCount;
	}
	public void setBigLeftCount(int bigLeftCount) {
		this.bigLeftCount = bigLeftCount;
	}
	public int getSmallLeftCount() {
		return smallLeftCount;
	}
	public void setSmallLeftCount(int smallLeftCount) {
		this.smallLeftCount = smallLeftCount;
	}
	public String getCcontact() {
		return ccontact;
	}
	public void setCcontact(String ccontact) {
		this.ccontact = ccontact;
	}
	public String getCmobile() {
		return cmobile;
	}
	public void setCmobile(String cmobile) {
		this.cmobile = cmobile;
	}
	
	
	
}
