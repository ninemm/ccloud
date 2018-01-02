package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class SalesRefundExcel {
	@Excel(name = "订单号")
	private String instockSn;
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
	@Excel(name = "商品名	")
	private String productName;
	@Excel(name = "规格")
	private String valueName;
	@Excel(name = "单位换算")
	private String creatconvertRelate;
	@Excel(name = "销售数量（大）")
	private int productCount;
	@Excel(name = "销售价格（大）")
	private String bigPrice;
	@Excel(name = "销售数量（小）")
	private int smallCount;
	@Excel(name = "销售价格（小）")
	private String smallPrice;
	@Excel(name = "销售金额")
	private String productAmount;
	@Excel(name = "退货价（大）")
	private String bigRejectProductPrice;
	@Excel(name = "退货数量（大）")
	private int bigRejectProductCount;
	@Excel(name = "退货价（小）")
	private String smallRejectProductPrice;
	@Excel(name = "退货数量（小）")
	private int smallRejectProductCount;
	@Excel(name = "退货总金额")
	private String rejectAmount;
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
	public String getInstockSn() {
		return instockSn;
	}
	public void setInstockSn(String instockSn) {
		this.instockSn = instockSn;
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
	public String getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(String productAmount) {
		this.productAmount = productAmount;
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
	public String getBigPrice() {
		return bigPrice;
	}
	public void setBigPrice(String bigPrice) {
		this.bigPrice = bigPrice;
	}
	public String getSmallPrice() {
		return smallPrice;
	}
	public void setSmallPrice(String smallPrice) {
		this.smallPrice = smallPrice;
	}
	public String getRejectAmount() {
		return rejectAmount;
	}
	public void setRejectAmount(String rejectAmount) {
		this.rejectAmount = rejectAmount;
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
	public int getProductCount() {
		return productCount;
	}
	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	public int getBigRejectProductCount() {
		return bigRejectProductCount;
	}
	public void setBigRejectProductCount(int bigRejectProductCount) {
		this.bigRejectProductCount = bigRejectProductCount;
	}
	public int getSmallRejectProductCount() {
		return smallRejectProductCount;
	}
	public void setSmallRejectProductCount(int smallRejectProductCount) {
		this.smallRejectProductCount = smallRejectProductCount;
	}
	public int getSmallCount() {
		return smallCount;
	}
	public void setSmallCount(int smallCount) {
		this.smallCount = smallCount;
	}
	public String getCreatconvertRelate() {
		return creatconvertRelate;
	}
	public void setCreatconvertRelate(String creatconvertRelate) {
		this.creatconvertRelate = creatconvertRelate;
	}
	public String getBigRejectProductPrice() {
		return bigRejectProductPrice;
	}
	public void setBigRejectProductPrice(String bigRejectProductPrice) {
		this.bigRejectProductPrice = bigRejectProductPrice;
	}
	public String getSmallRejectProductPrice() {
		return smallRejectProductPrice;
	}
	public void setSmallRejectProductPrice(String smallRejectProductPrice) {
		this.smallRejectProductPrice = smallRejectProductPrice;
	}
	
	
}
