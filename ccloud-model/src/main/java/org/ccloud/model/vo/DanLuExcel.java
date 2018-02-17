package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class DanLuExcel {

	@Excel(name = "采购商")
	private String customerName;

	@Excel(name = "省")
	private String provName;
	@Excel(name = "市")
	private String cityName;
	@Excel(name = "区")
	private String countyName;
	@Excel(name = "采购商注册地址")
	private String address;
	
	@Excel(name = "配送商")
	private String sellerName;
	
	@Excel(name = "订单号")
	private String order_sn;
	@Excel(name = "订单状态")
	private String status;
	@Excel(name = "实得金额")
	private String pay_amount;
	@Excel(name = "订单金额")
	private String total_amount;
	@Excel(name = "数量")
	private String total_count;
	@Excel(name = "支付方式")
	private String receive_type;
	@Excel(name = "付款时间")
	private String pay_date;
	@Excel(name = "丹露红包抵扣")
	private String coupon_reduce_price;
	@Excel(name = "发货人姓名")
	private String send_user_name;
	@Excel(name = "下单时间")
	private String create_date;
	@Excel(name = "订单改价")
	private String change_price;
	@Excel(name = "确认收货时间")
	private String delivery_date;
	@Excel(name = "商品名称")
	private String productName;
	@Excel(name = "商品实付金额")
	private String productAmount;
	@Excel(name = "产品条码" )
	private String product_code;
	@Excel(name = "规格")
	private String unit;
	
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
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
	public String getDelivery_date() {
		return delivery_date;
	}
	public void setDelivery_date(String delivery_date) {
		this.delivery_date = delivery_date;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(String productAmount) {
		this.productAmount = productAmount;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
