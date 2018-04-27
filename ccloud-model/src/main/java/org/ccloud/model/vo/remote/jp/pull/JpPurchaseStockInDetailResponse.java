/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * @author wally
 *
 */
public class JpPurchaseStockInDetailResponse {
	@SerializedName("DealerMarketCode") 
	private String dealerMarketCode;
	@SerializedName("DealerMarketName") 
	private String dealerMarketName;
	@SerializedName("cInvCode") 
	private String cInvCode;
	@SerializedName("cInvName") 
	private String cInvName;
	@SerializedName("mSyscInvCode") 
	private String mSyscInvCode;
	@SerializedName("mSyscInvName") 
	private String mSyscInvName;
	@SerializedName("SaleNum") 
	private String saleNum;
	@SerializedName("cComUnitName") 
	private String cComUnitName;
	@SerializedName("BusinessDate") 
	private Date businessDate;
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
	public String getcInvCode() {
		return cInvCode;
	}
	public void setcInvCode(String cInvCode) {
		this.cInvCode = cInvCode;
	}
	public String getcInvName() {
		return cInvName;
	}
	public void setcInvName(String cInvName) {
		this.cInvName = cInvName;
	}
	public String getmSyscInvCode() {
		return mSyscInvCode;
	}
	public void setmSyscInvCode(String mSyscInvCode) {
		this.mSyscInvCode = mSyscInvCode;
	}
	public String getmSyscInvName() {
		return mSyscInvName;
	}
	public void setmSyscInvName(String mSyscInvName) {
		this.mSyscInvName = mSyscInvName;
	}
	public String getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(String saleNum) {
		this.saleNum = saleNum;
	}
	public String getcComUnitName() {
		return cComUnitName;
	}
	public void setcComUnitName(String cComUnitName) {
		this.cComUnitName = cComUnitName;
	}
	public Date getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	
}
