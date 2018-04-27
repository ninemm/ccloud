/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.List;

/**
 * @author wally
 *
 */
public class JpSellerAccountResponseBody extends JpBaseResponseBody {
	private List<JpSellerAccountResponseEntity> sellerAccounts;

	public List<JpSellerAccountResponseEntity> getSellerAccounts() {
		return sellerAccounts;
	}

	public void setSellerAccounts(List<JpSellerAccountResponseEntity> sellerAccounts) {
		this.sellerAccounts = sellerAccounts;
	}
	
	
}
