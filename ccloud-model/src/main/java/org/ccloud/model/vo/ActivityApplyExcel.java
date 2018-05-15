package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class ActivityApplyExcel {
	@Excel(name = "序号")
	private String num;	
	@Excel(name = "开始日期")
	private String startdate;
	@Excel(name = "结束日期")
	private String enddate;	
	@Excel(name = "终端名称")
	private String name;
	@Excel(name = "终端地址")
	private String address;
	@Excel(name = "终端关键人")
	private String contact;
	@Excel(name = "联系电话")
	private String mobile;
	@Excel(name = "营销人员")
	private String userName;
	@Excel(name = "投入类型")
	private String investName;
	@Excel(name = "申请金额")
	private String applyAmount;
	@Excel(name = "申请数量")
	private String applyNum;
	
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getInvestName() {
		return investName;
	}
	public void setInvestName(String investName) {
		this.investName = investName;
	}
	public String getApplyAmount() {
		return applyAmount;
	}
	public void setApplyAmount(String applyAmount) {
		this.applyAmount = applyAmount;
	}
	public String getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}

}
