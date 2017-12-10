package org.ccloud.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class CustomerVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String customerId;
	private String sellerCustomerId;
	private BigDecimal lng;
	private BigDecimal lat;
	
	@JSONField(name = "客户名称")
	private String customerName;
	@JSONField(name = "客户昵称")
	private String nickname;
	
	@JSONField(name = "联系人")
	private String contact;
	@JSONField(name = "联系电话")
	private String mobile;
	
	@JSONField(name = "区域编号")
	private String areaCode;
	@JSONField(name = "区域地址")
	private String areaName;
	
	@JSONField(name = "详细地址")
	private String address;
	@JSONField(name = "店招图")
	private String imageListStore;
	
	@JSONField(name = "客户类型")
	private List<String> custTypeList;
	@JSONField(name = "定位地址")
	private String location;
	
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getSellerCustomerId() {
		return sellerCustomerId;
	}

	public void setSellerCustomerId(String sellerCustomerId) {
		this.sellerCustomerId = sellerCustomerId;
	}

	public String getCustomerName() {
		return customerName;
	}
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

//	public String getProvName() {
//		return provName;
//	}
//
//	public void setProvName(String provName) {
//		this.provName = provName;
//	}
//
//	public String getProvCode() {
//		return provCode;
//	}
//
//	public void setProvCode(String provCode) {
//		this.provCode = provCode;
//	}
//
//	public String getCityName() {
//		return cityName;
//	}
//
//	public void setCityName(String cityName) {
//		this.cityName = cityName;
//	}
//
//	public String getCityCode() {
//		return cityCode;
//	}
//
//	public void setCityCode(String cityCode) {
//		this.cityCode = cityCode;
//	}
//
//	public String getCountryName() {
//		return countryName;
//	}
//
//	public void setCountryName(String countryName) {
//		this.countryName = countryName;
//	}
//
//	public String getCountryCode() {
//		return countryCode;
//	}
//
//	public void setCountryCode(String countryCode) {
//		this.countryCode = countryCode;
//	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public BigDecimal getLng() {
		return lng;
	}

	public void setLng(BigDecimal lng) {
		this.lng = lng;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImageListStore() {
		return imageListStore;
	}

	public void setImageListStore(String imageListStore) {
		this.imageListStore = imageListStore;
	}

	public List<String> getCustTypeList() {
		return custTypeList;
	}

	public void setCustTypeList(List<String> custTypeList) {
		this.custTypeList = custTypeList;
	}
	
	
}
