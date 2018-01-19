package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class UserExecel {
	
	@Excel(name = "姓名")
	private String contact;
	@Excel(name = "用户名/员工号")
	private String username;
	@Excel(name = "手机")
	private String mobile;
	@Excel(name = "部门名称")
	private String deptName;
	@Excel(name = "职位")
	private String userGroup;


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	
}
