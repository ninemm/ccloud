/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.util.List;

/**
 * @author wally
 *
 */
public class JpGoodsCategoryResponseBody extends JpBaseResponseBody {
	private List<JpGoodsCategoryResponseEntity> goodsCategories;

	public List<JpGoodsCategoryResponseEntity> getGoodsCategories() {
		return goodsCategories;
	}

	public void setGoodsCategories(List<JpGoodsCategoryResponseEntity> goodsCategories) {
		this.goodsCategories = goodsCategories;
	}
	
	
}
