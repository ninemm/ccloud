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
public class JpPurchaseStockInResponseBody extends JpBaseResponseBody {
	@SerializedName("Data") 
	private List<JpPurchaseStockInMainResponse> data;
	@SerializedName("Flsg") 
	private String flsg;
	@SerializedName("Remark") 
	private String remark;

	public List<JpPurchaseStockInMainResponse> getData() {
		return data;
	}

	public void setData(List<JpPurchaseStockInMainResponse> data) {
		this.data = data;
	}

	public String getFlsg() {
		return flsg;
	}

	public void setFlsg(String flsg) {
		this.flsg = flsg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
