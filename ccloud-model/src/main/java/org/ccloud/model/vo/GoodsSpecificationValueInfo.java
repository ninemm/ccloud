package org.ccloud.model.vo;

import java.util.Date;

public class GoodsSpecificationValueInfo {

	private String id;
	private String name;
	private String goodsSpecificationId;
	private String imagePath;
	private Integer orderList;
	private Date createDate;
	private Date modifyDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGoodsSpecificationId() {
		return goodsSpecificationId;
	}
	public void setGoodsSpecificationId(String goodsSpecificationId) {
		this.goodsSpecificationId = goodsSpecificationId;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public Integer getOrderList() {
		return orderList;
	}
	public void setOrderList(Integer orderList) {
		this.orderList = orderList;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
	
}
