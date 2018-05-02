/**
StockInRequestBody.java
StockInResponseBody.java * Copyright (c) 2015-${year}, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo;

import java.util.Date;
import java.util.List;

/**
 * 入库接口请求主体
 * @author wally
 *
 */
public class StockInRequestBody extends BaseRequestBody<StockInResponseBody> {
	private String sellerCode;
	private String supplierCode;
	private List<StockInRequestProduct> products;
	private Date dealDate;
	private Integer payType;
	private String remark;
	
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getSupplierCode() {
		return supplierCode;
	}
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}
	public List<StockInRequestProduct> getProducts() {
		return products;
	}
	public void setProducts(List<StockInRequestProduct> products) {
		this.products = products;
	}
	
	public Date getDealDate() {
		return dealDate;
	}
	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}
	public Integer getPayType() {
		return payType;
	}
	public void setPayType(Integer payType) {
		this.payType = payType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
