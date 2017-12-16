package org.ccloud.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class printAllNeedInfo {

	private String outstockSn;//发货单号
	private Date printDate;//制单时间
	private String customerName;//客户姓名
	private String customerAddress;//客户地址
	private String customerContacts;//客户联系人
	private String customerPhone;//客户电话
	private String placeOrderMan; //业务员姓名
    private String placeOrderPhone;//业务员电话
	private Date placeOrderTime;//下单时间
	private String warehouseName;//发货仓库名字
	private BigDecimal salesAmount;//订单应收合计
	private String remark;//备注
	private String warehousePhone;//仓库电话
	private String deliveryAddress;//配送地址
	private String sellerName;
	private List<orderProductInfo> orderProductInfos;//订单明细信息
	public String getOutstockSn() {
		return outstockSn;
	}
	public void setOutstockSn(String outstockSn) {
		this.outstockSn = outstockSn;
	}
	public Date getPrintDate() {
		return printDate;
	}
	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerAddress() {
		return customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	
	public String getCustomerContacts() {
		return customerContacts;
	}
	public void setCustomerContacts(String customerContacts) {
		this.customerContacts = customerContacts;
	}
	public String getCustomerPhone() {
		return customerPhone;
	}
	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}
	public String getPlaceOrderMan() {
		return placeOrderMan;
	}
	public void setPlaceOrderMan(String placeOrderMan) {
		this.placeOrderMan = placeOrderMan;
	}
	public String getPlaceOrderPhone() {
		return placeOrderPhone;
	}
	public void setPlaceOrderPhone(String placeOrderPhone) {
		this.placeOrderPhone = placeOrderPhone;
	}
	public Date getPlaceOrderTime() {
		return placeOrderTime;
	}
	public void setPlaceOrderTime(Date placeOrderTime) {
		this.placeOrderTime = placeOrderTime;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public BigDecimal getSalesAmount() {
		return salesAmount;
	}
	public void setSalesAmount(BigDecimal salesAmount) {
		this.salesAmount = salesAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getWarehousePhone() {
		return warehousePhone;
	}
	public void setWarehousePhone(String warehousePhone) {
		this.warehousePhone = warehousePhone;
	}
	public List<orderProductInfo> getOrderProductInfos() {
		return orderProductInfos;
	}
	public void setOrderProductInfos(List<orderProductInfo> orderProductInfos) {
		this.orderProductInfos = orderProductInfos;
	}
	public String getDeliveryAddress() {
		return deliveryAddress;
	}
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	
}
