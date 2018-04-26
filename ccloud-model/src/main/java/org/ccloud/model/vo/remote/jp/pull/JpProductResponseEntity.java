/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * @author wally
 *
 */
public class JpProductResponseEntity {
	@SerializedName("SInventoryID") 
	private Long sInventoryID;
	@SerializedName("cInvCode") 
	private String cInvCode;
	@SerializedName("cInvName") 
	private String cInvName;
	@SerializedName("cInvStd") 
	private String cInvStd;
	@SerializedName("cInvCCode") 
	private String cInvCCode;
	@SerializedName("cComUnitName") 
	private String cComUnitName;
	@SerializedName("mComUnitName") 
	private String mComUnitName;
	@SerializedName("cInvMNum") 
	private Integer cInvMNum;
	@SerializedName("ReqMaterial") 
	private BigDecimal reqMaterial;
	@SerializedName("SafeStock") 
	private Integer safeStock;
	@SerializedName("CurrentPrice") 
	private BigDecimal currentPrice;
	@SerializedName("InvariantPrice") 
	private BigDecimal invariantPrice;
	@SerializedName("iTaxRate") 
	private BigDecimal iTaxRate;
	@SerializedName("iInvVolume") 
	private BigDecimal iInvVolume;
	@SerializedName("iInvWeight") 
	private Double iInvWeight;
	@SerializedName("cQuality") 
	private String cQuality;
	@SerializedName("ProductCode") 
	private String productCode;
	@SerializedName("Istate") 
	private Integer istate;
	@SerializedName("SystemID") 
	private Integer systemID;
	@SerializedName("ISystemID") 
	private Integer iSystemID;
	@SerializedName("cInvContent") 
	private String cInvContent;
	@SerializedName("UserID") 
	private String userID;
	@SerializedName("UserName") 
	private String userName;
	@SerializedName("AddTime") 
	private Date addTime;
	@SerializedName("ModifyTime") 
	private Date modifyTime;
	public Long getsInventoryID() {
		return sInventoryID;
	}
	public void setsInventoryID(Long sInventoryID) {
		this.sInventoryID = sInventoryID;
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
	public String getcInvStd() {
		return cInvStd;
	}
	public void setcInvStd(String cInvStd) {
		this.cInvStd = cInvStd;
	}
	public String getcInvCCode() {
		return cInvCCode;
	}
	public void setcInvCCode(String cInvCCode) {
		this.cInvCCode = cInvCCode;
	}
	public String getcComUnitName() {
		return cComUnitName;
	}
	public void setcComUnitName(String cComUnitName) {
		this.cComUnitName = cComUnitName;
	}
	public String getmComUnitName() {
		return mComUnitName;
	}
	public void setmComUnitName(String mComUnitName) {
		this.mComUnitName = mComUnitName;
	}
	public Integer getcInvMNum() {
		return cInvMNum;
	}
	public void setcInvMNum(Integer cInvMNum) {
		this.cInvMNum = cInvMNum;
	}
	public BigDecimal getReqMaterial() {
		return reqMaterial;
	}
	public void setReqMaterial(BigDecimal reqMaterial) {
		this.reqMaterial = reqMaterial;
	}
	public Integer getSafeStock() {
		return safeStock;
	}
	public void setSafeStock(Integer safeStock) {
		this.safeStock = safeStock;
	}
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	public BigDecimal getInvariantPrice() {
		return invariantPrice;
	}
	public void setInvariantPrice(BigDecimal invariantPrice) {
		this.invariantPrice = invariantPrice;
	}
	public BigDecimal getiTaxRate() {
		return iTaxRate;
	}
	public void setiTaxRate(BigDecimal iTaxRate) {
		this.iTaxRate = iTaxRate;
	}
	public BigDecimal getiInvVolume() {
		return iInvVolume;
	}
	public void setiInvVolume(BigDecimal iInvVolume) {
		this.iInvVolume = iInvVolume;
	}
	public Double getiInvWeight() {
		return iInvWeight;
	}
	public void setiInvWeight(Double iInvWeight) {
		this.iInvWeight = iInvWeight;
	}
	public String getcQuality() {
		return cQuality;
	}
	public void setcQuality(String cQuality) {
		this.cQuality = cQuality;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Integer getIstate() {
		return istate;
	}
	public void setIstate(Integer istate) {
		this.istate = istate;
	}
	public Integer getSystemID() {
		return systemID;
	}
	public void setSystemID(Integer systemID) {
		this.systemID = systemID;
	}
	public Integer getiSystemID() {
		return iSystemID;
	}
	public void setiSystemID(Integer iSystemID) {
		this.iSystemID = iSystemID;
	}
	public String getcInvContent() {
		return cInvContent;
	}
	public void setcInvContent(String cInvContent) {
		this.cInvContent = cInvContent;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	
}
