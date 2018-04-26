/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import com.google.gson.annotations.SerializedName;

/**
 * @author wally
 *
 */
public class JpSellerAccountResponseEntity {
	@SerializedName("DealerMarketCode") 
	private String dealerMarketCode;
	@SerializedName("DealerMarketName") 
	private String dealerMarketName;
	public String getDealerMarketCode() {
		return dealerMarketCode;
	}
	public void setDealerMarketCode(String dealerMarketCode) {
		this.dealerMarketCode = dealerMarketCode;
	}
	public String getDealerMarketName() {
		return dealerMarketName;
	}
	public void setDealerMarketName(String dealerMarketName) {
		this.dealerMarketName = dealerMarketName;
	}
	
}
