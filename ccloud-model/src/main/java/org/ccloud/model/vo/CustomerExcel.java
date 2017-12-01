package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class CustomerExcel {

	@Excel(name = "客户名称")
	private String customerName;
	@Excel(name = "客户昵称")
	private String nickname;
	@Excel(name = "联系人")
	private String contact;
	@Excel(name = "手机号")
	private String mobile;
	@Excel(name = "邮箱")
	private String email;

	@Excel(name = "省")
	private String provName;
	@Excel(name = "市")
	private String cityName;
	@Excel(name = "区")
	private String countyName;
	@Excel(name = "详细地址")
	private String address;

	@Excel(name = "客户类型")
	private String customerTypeName;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getCustomerTypeName() {
		return customerTypeName;
	}

	public void setCustomerTypeName(String customerTypeName) {
		this.customerTypeName = customerTypeName;
	}

}