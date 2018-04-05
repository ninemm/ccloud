/**
 * Copyright (c) 2015-${year}, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo;

import java.math.BigDecimal;

/**
 * 入库接口请求主体
 * @author wally
 *
 */
public class StockInRequestProduct {
	private String code;
	private BigDecimal totalPrice;
	private Integer num;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	
}
