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
public class JpGoodsCategoryResponseEntity implements Comparable<JpGoodsCategoryResponseEntity> {
	@SerializedName("parent") 
	private String parent;
	@SerializedName("cInvCCode") 
	private String cInvCCode;
	@SerializedName("cInvCName") 
	private String cInvCName;
	@SerializedName("iInvCGrade") 
	private Integer iInvCGrade;
	@SerializedName("AddTime") 
	private Date addTime;
	@SerializedName("ModifyTime") 
	private Date modifyTime;
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getcInvCCode() {
		return cInvCCode;
	}
	public void setcInvCCode(String cInvCCode) {
		this.cInvCCode = cInvCCode;
	}
	public String getcInvCName() {
		return cInvCName;
	}
	public void setcInvCName(String cInvCName) {
		this.cInvCName = cInvCName;
	}
	public Integer getiInvCGrade() {
		return iInvCGrade;
	}
	public void setiInvCGrade(Integer iInvCGrade) {
		this.iInvCGrade = iInvCGrade;
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
	@Override
	public int compareTo(JpGoodsCategoryResponseEntity g) {
		return this.parent.compareTo(g.getParent());
	}
	
	
}
