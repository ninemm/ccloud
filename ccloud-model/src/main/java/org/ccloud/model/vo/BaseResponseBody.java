/**
 * Copyright (c) 2015-2018, Wally Wang 王勇(wally8292@163.com)
 */
package org.ccloud.model.vo;

/**
 * @author wally
 *
 */
public class BaseResponseBody {
	private String message;
	private int errorCode = 0; // 0: normal , >=1 : error
	private Object data;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
