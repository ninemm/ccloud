package org.ccloud;
/**
 * Copyright (c) 2015-2018, Wally Wang 王勇(wally8292@163.com)
 */

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.ccloud.model.vo.SaveGoodsCategoryRequestBody;
import org.ccloud.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wally
 *
 */
public class GoodsCategorySynchronizeTest extends BaseTest {
	private static final String APP_KEY = "WMS_GOODS_CATEGORY_SYNCHRONIZE";
	private static final String SECRET_KEY = "l01fuKXkI0nf7TE7";
	private static final String METHOD = "saveGoodsCategory";

	@Before
	public void setUp() {
		setSecretKey(SECRET_KEY);
	}
	
	@Test
	public void testSaveGoodsCategory() {
		Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		SaveGoodsCategoryRequestBody requestBody = new SaveGoodsCategoryRequestBody();
		requestBody.setCategoryCode("503");
		requestBody.setCategoryName("劲酒3");
		requestBody.setParentCode("5");;
		requestBody.setSupplierCode("SUP001");
		Map<String, String> params = new HashedMap<String, String>();
		params.put("appkey", APP_KEY);
		params.put("method", METHOD);
		params.put("data", gson.toJson(requestBody));
		execute(requestBody, params, null);
	}

}
