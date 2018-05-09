package org.ccloud.model.vo;

import java.math.BigDecimal;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class ReceivablesAmountExcel {
	@Excel(name = "客户名称")
	private String customerName;//客户名字
	
	@Excel(name = "应收账款")
	private BigDecimal receiveAmount;//应收账款
	
	@Excel(name = "已收账款")
	private BigDecimal actAmount;//已收账款
	
	@Excel(name = "未收账款")
	private BigDecimal balanceAmount;//未收账款

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public BigDecimal getReceiveAmount() {
		return receiveAmount;
	}

	public void setReceiveAmount(BigDecimal receiveAmount) {
		this.receiveAmount = receiveAmount;
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
