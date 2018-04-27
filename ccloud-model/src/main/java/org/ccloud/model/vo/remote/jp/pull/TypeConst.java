/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote.jp.pull;

import java.lang.reflect.Type;
import java.util.List;

import com.google.common.reflect.TypeToken;

/**
 * @author wally
 *
 */
public class TypeConst {
	public static final Type JP_GOODS_CATEGORY_RESPONSE_ENTITY_LIST_TYPE = (new TypeToken<List<JpGoodsCategoryResponseEntity>>() {
	}).getType();
	public static final Type JP_PRODUCT_RESPONSE_ENTITY_LIST_TYPE = (new TypeToken<List<JpProductResponseEntity>>() {
	}).getType();
	public static final Type JP_SELLER_RESPONSE_ENTITY_LIST_TYPE = (new TypeToken<List<JpSellerResponseEntity>>() {
	}).getType();
	public static final Type JP_SELLER_ACCOUNT_RESPONSE_ENTITY = (new TypeToken<List<JpSellerAccountResponseEntity>>() {
	}).getType();
}
