/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.remot.jp.pull;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.ccloud.BaseTest;
import org.ccloud.model.vo.remote.jp.pull.JpSellerRequestBody;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wally
 *
 */
public class SellerAccountSynchronizeTest extends BaseTest {
	private static final String REQUEST_URL = "http://im.jingpai.com/api/http/v1/GetDealerMarketCode";
//	private static final String REQUEST_URL = "http://im.jingpai.com/api/http/v1/GetInvClass";
	
	private static final String IM_CLIENT_CODE = "8605a70f-107a-45dd-b124-fa4c45c0a14d";
	private static final String IM_SECRET_KEY = "754953903190a8d29ee5bcf937208449";
	private static final String CLIENT_CODE = "b6d59a74baf34f93b7a5cfb5bfd67ba4";
//	private static final String CLIENT_CODE = "d94cdea079b041d09df21f71a1a6523e";
	private static final String CONTENT_TYPE = "application/json";
	
	private static final String DEALER_CODE = "56201001";

	@Before
	public void setUp() {
	}
	
	@Test
	public void testSellerSynchronize() {
		Map<String, String> params = new HashedMap<String, String>();
		params.put("clientCode", CLIENT_CODE);
		params.put("DealerCode", DEALER_CODE);
		
		Map<String, String> headers = new HashedMap<String, String>();
		headers.put("imClientCode", IM_CLIENT_CODE);
		headers.put("imSecretKey", IM_SECRET_KEY);
		headers.put("Content-Type", CONTENT_TYPE);
		JpSellerRequestBody requstBody = new JpSellerRequestBody();
		
		execute(REQUEST_URL, requstBody , params, headers);
		
	}
}