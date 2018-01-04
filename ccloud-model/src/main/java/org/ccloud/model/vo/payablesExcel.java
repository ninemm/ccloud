package org.ccloud.model.vo;

import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class payablesExcel {

	@Excel(name = "客户类型")
	private String customerType;//客户类型

	@Excel(name = "客户名称")
	private String customerName;//客户名字
	
	@Excel(name = "应付账款")
	private BigDecimal payAmount;//应付账款
	
	@Excel(name = "已付账款")
	private BigDecimal actAmount;//已付账款
	
	@Excel(name = "未付账款")
	private BigDecimal balanceAmount;//未付账款

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getPayAmount() {
		return payAmount;
	}

	public void setPayAmount(BigDecimal payAmount) {
		this.payAmount = payAmount;
	}

	public BigDecimal getActAmount() {
		return actAmount;
	}

	public void setActAmount(BigDecimal actAmount) {
		this.actAmount = actAmount;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	
	
}
