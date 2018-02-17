package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class AliExcel {

	@Excel(name = "买家")
	private String customerName;
	@Excel(name = "买家会员名")
	private String contact;
	@Excel(name = "省")
	private String provName;
	@Excel(name = "市")
	private String cityName;
	@Excel(name = "区")
	private String countyName;
	@Excel(name = "收货地址")
	private String address;
	@Excel(name = "邮编")
	private String postalcode;
	@Excel(name = "卖家公司名")
	private String sellerName;
	@Excel(name = "卖家会员名")
	private String seller_contact;
	
	@Excel(name = "订单编号")
	private String order_sn;
	@Excel(name = "关联编号")
	private String relevance_sn;
	@Excel(name = "订单状态")
	private String status;
	@Excel(name = "实付款(元)")
	private String pay_amount;
	@Excel(name = "货品总价(元)")
	private String total_amount;
	@Excel(name = "数量")
	private String total_count;
	@Excel(name = "收款方式")
	private String receive_type;
	@Excel(name = "订单付款时间")
	private String pay_date;
	@Excel(name = "涨价或折扣(元)")
	private String coupon_reduce_price;
	@Excel(name = "发货方")
	private String send_user_name;
	@Excel(name = "订单创建时间")
	private String create_date;
	@Excel(name = "订单改价")
	private String change_price;
	@Excel(name = "货品标题")
	private String productName;
	@Excel(name = "货品种类")
	private String productType;
	@Excel(name = "单位")
	private String unit;
	@Excel(name = "单价(元)")
	private String productAmount;
	
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getProvName() {
		return provName;
	}
	public void setProvName(String provName) {
		this.provName = provName;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSeller_contact() {
		return seller_contact;
	}
	public void setSeller_contact(String seller_contact) {
		this.seller_contact = seller_contact;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public String getRelevance_sn() {
		return relevance_sn;
	}
	public void setRelevance_sn(String relevance_sn) {
		this.relevance_sn = relevance_sn;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPay_amount() {
		return pay_amount;
	}
	public void setPay_amount(String pay_amount) {
		this.pay_amount = pay_amount;
	}
	public String getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	public String getTotal_count() {
		return total_count;
	}
	public void setTotal_count(String total_count) {
		this.total_count = total_count;
	}
	public String getReceive_type() {
		return receive_type;
	}
	public void setReceive_type(String receive_type) {
		this.receive_type = receive_type;
	}
	public String getPay_date() {
		return pay_date;
	}
	public void setPay_date(String pay_date) {
		this.pay_date = pay_date;
	}
	public String getCoupon_reduce_price() {
		return coupon_reduce_price;
	}
	public void setCoupon_reduce_price(String coupon_reduce_price) {
		this.coupon_reduce_price = coupon_reduce_price;
	}
	public String getSend_user_name() {
		return send_user_name;
	}
	public void setSend_user_name(String send_user_name) {
		this.send_user_name = send_user_name;
	}
	public String getCreate_date() {
		return create_date;
	}
	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}
	public String getChange_price() {
		return change_price;
	}
	public void setChange_price(String change_price) {
		this.change_price = change_price;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(String productAmount) {
		this.productAmount = productAmount;
	}
	
}
