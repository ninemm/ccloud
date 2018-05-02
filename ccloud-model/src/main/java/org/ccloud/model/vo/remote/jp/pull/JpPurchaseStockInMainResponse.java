/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author wally
 *
 */
public class JpPurchaseStockInMainResponse {
	@SerializedName("OrderDetails") 
	private List<JpPurchaseStockInDetailResponse> orderDetails;
	@SerializedName("OrderNum") 
	private String orderNum;

	public List<JpPurchaseStockInDetailResponse> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<JpPurchaseStockInDetailResponse> orderDetails) {
		this.orderDetails = orderDetails;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	
	
}
