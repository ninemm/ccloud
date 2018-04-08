package org.ccloud;
/**
 * Copyright (c) 2015-2018, Wally Wang 王勇(wally8292@163.com)
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.ccloud.model.vo.StockInRequestBody;
import org.ccloud.model.vo.StockInRequestProduct;
import org.ccloud.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wally
 *
 */
public class PurchaseStockInSynchroizeTest extends BaseTest {
	private static final String APP_KEY = "WMS_PURCHASE_STOCK_IN_SYNCHRONIZE";
	private static final String SECRET_KEY = "bAuLsLcIaS58kn6K";
	private static final String METHOD = "savePurchaseStockIn";
	
	@Before
	public void setUp() {
		setSecretKey(SECRET_KEY);
	}
	
	@Test
	public void testStockIn() {
		Calendar calendar = Calendar.getInstance();
		Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		StockInRequestBody requestBody = new StockInRequestBody();
		requestBody.setDealDate(calendar.getTime());
		requestBody.setPayType(1);
		requestBody.setSellerCode("100002");
		requestBody.setSupplierCode("SUP001");
		List<StockInRequestProduct> products = new ArrayList<StockInRequestProduct>();
		products.add(new StockInRequestProduct("C1008", new BigDecimal(300), 23));
		products.add(new StockInRequestProduct("C1007", new BigDecimal(260), 26));
		products.add(new StockInRequestProduct("C1003", new BigDecimal(228), 21));
		requestBody.setProducts(products);
		Map<String, String> params = new HashedMap<String, String>();
		params.put("appkey", APP_KEY);
		params.put("method", METHOD);
		params.put("data", gson.toJson(requestBody));
		execute(requestBody, params, null);
	}

}
