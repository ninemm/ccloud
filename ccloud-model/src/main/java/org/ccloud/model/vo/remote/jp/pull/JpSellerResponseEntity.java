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
public class JpSellerResponseEntity {
	@SerializedName("DealerCode") 
	private String dealerCode;
	@SerializedName("DealerName") 
	private String dealerName;
	@SerializedName("IsDelete") 
	private boolean isDelete;
	@SerializedName("AddTime") 
	private Date addTime;
	@SerializedName("UpdateTime") 
	private Date updateTime;
	@SerializedName("ProvinceCode") 
	private String provinceCode;
	@SerializedName("ProvinceName") 
	private String provinceName;
	@SerializedName("CityCode") 
	private String cityCode;
	@SerializedName("CityName") 
	private String cityName;
	@SerializedName("CountyCode") 
	private String countyCode;
	@SerializedName("CountyName") 
	private String countyName;
	public String getDealerCode() {
		return dealerCode;
	}
	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getProvinceName() {
		return provinceName;
	}
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountyCode() {
		return countyCode;
	}
	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}
	public String getCountyName() {
		return countyName;
	}
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	
}
