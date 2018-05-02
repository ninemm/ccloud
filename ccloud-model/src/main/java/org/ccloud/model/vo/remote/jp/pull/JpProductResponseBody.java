/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.List;

/**
 * @author wally
 *
 */
public class JpProductResponseBody extends JpBaseResponseBody {
	private List<JpProductResponseEntity> products;

	public List<JpProductResponseEntity> getProducts() {
		return products;
	}

	public void setProducts(List<JpProductResponseEntity> products) {
		this.products = products;
	}
	
	
}
