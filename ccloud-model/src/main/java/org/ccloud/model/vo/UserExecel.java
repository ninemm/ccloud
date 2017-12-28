package org.ccloud.model.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class UserExecel {
	@Excel(name = "用户名")
	private String userName;
	@Excel(name = "呢称")
	private String nickname;
	@Excel(name = "真实姓名")
	private String contact;
	@Excel(name = "手机")
	private String mobile;
	@Excel(name = "密码")
	private String password;

	@Excel(name = "用户分组")
	private String userGroup;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	
	
}
