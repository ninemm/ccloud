package org.ccloud;
/**
 * Copyright (c) 2015-2018, Wally Wang 王勇(wally8292@163.com)
 */

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.ccloud.model.vo.SellerSynchronizeRequestBody;
import org.ccloud.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wally
 *
 */
public class SellerSynchronizeTest extends BaseTest {
	private static final String APP_KEY = "WMS_SELLER_SYNCHRONIZE";
	private static final String SECRET_KEY = "8eQab6qAQPQUzjNp";
	private static final String METHOD = "saveSellerSynchronize";

	@Before
	public void setUp() {
		setSecretKey(SECRET_KEY);
	}
	
	@Test
	public void testSellerSynchronize() {
		Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		SellerSynchronizeRequestBody requestBody = new SellerSynchronizeRequestBody();
		requestBody.setSellerCode("WJ0003");
		requestBody.setSellerName("王酒");
		requestBody.setContact("王勇");;
		requestBody.setPhone("18521579960");
		requestBody.setProvName("湖北省");
		requestBody.setCityName("武汉市");
		requestBody.setCountryName("洪山区");
		Map<String, String> params = new HashedMap<String, String>();
		params.put("appkey", APP_KEY);
		params.put("method", METHOD);
		params.put("data", gson.toJson(requestBody));
		execute(requestBody, params, null);
	}

}
