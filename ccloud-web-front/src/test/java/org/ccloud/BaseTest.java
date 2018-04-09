package org.ccloud;

import java.io.File;
import java.util.Map;

import org.ccloud.model.vo.BaseRequestBody;
import org.ccloud.model.vo.BaseResponseBody;
import org.ccloud.utils.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

public class BaseTest extends TestCase {
	private String secretKey;
	
	protected void printlnResult(BaseResponseBody response) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		System.out.println(gson.toJson(response));
	}
	
	/**
	 * 
	 * @param request
	 * @param params：请求参数
	 * @param headers：请求头信息
	 */
	protected void execute(BaseRequestBody<? extends BaseResponseBody> request, Map<String, String> params, Map<String, String> headers) {
		execute(request, params, headers, null);
	}
	
	protected void execute(BaseRequestBody<? extends BaseResponseBody> request, Map<String, String> params, Map<String, String> headers, File file) {
		BaseResponseBody responseBody = new HttpExcute(secretKey).execute(request,params, headers, file);
		printlnResult(responseBody);
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
