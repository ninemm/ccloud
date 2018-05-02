/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.List;

/**
 * @author wally
 *
 */
public class JpSellerResponseBody extends JpBaseResponseBody {
	private List<JpSellerResponseEntity> sellers;

	public List<JpSellerResponseEntity> getSellers() {
		return sellers;
	}

	public void setSellers(List<JpSellerResponseEntity> sellers) {
		this.sellers = sellers;
	}
	
}
