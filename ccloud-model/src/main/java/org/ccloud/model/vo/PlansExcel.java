package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class PlansExcel {
	@Excel(name = "开始时间")
	private String startDate;
	@Excel(name = "结束时间")
	private String endDate;
	@Excel(name = "计划类型")
	private String type;
	@Excel(name = "业务员")
	private String bizUser;
	@Excel(name = "业务员电话")
	private String mobile;
	@Excel(name = "计划产品")
	private String sellerProduct;
	@Excel(name = "计划量（件）")
	private String planNum;
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBizUser() {
		return bizUser;
	}
	public void setBizUser(String bizUser) {
		this.bizUser = bizUser;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getSellerProduct() {
		return sellerProduct;
	}
	public void setSellerProduct(String sellerProduct) {
		this.sellerProduct = sellerProduct;
	}
	public String getPlanNum() {
		return planNum;
	}
	public void setPlanNum(String planNum) {
		this.planNum = planNum;
	}
	
	
}
