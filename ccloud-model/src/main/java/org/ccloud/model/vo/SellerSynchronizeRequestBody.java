/**
 * Copyright (c) 2015-${year}, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo;

/**
 * 经销商同步接口请求主体
 * @author wally
 *
 */
public class SellerSynchronizeRequestBody extends BaseRequestBody<SellerSynchronizeResponseBody> {
	private String sellerCode;
	private String sellerName;
	private String contact;
	private String phone;
	private String provName;
	private String cityName;
	private String countryName;
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
}
