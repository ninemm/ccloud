package org.ccloud;

import java.io.File;
import java.util.Map;

import org.ccloud.model.vo.BaseRequestBody;
import org.ccloud.model.vo.BaseResponseBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseRequestBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseResponseBody;
import org.ccloud.utils.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

public class BaseTest extends TestCase {
	protected void printlnResult(BaseResponseBody response) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		System.out.println(gson.toJson(response));
	}
	
	protected void printlnJpResult(JpBaseResponseBody response) {
		Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
		System.out.println(gson.toJson(response));
	}
	
	/**
	 * @param requestUrl：请求地址
	 * @param request：请求实体
	 * @param params：请求参数
	 * @param headers：请求头信息
	 */
	protected void execute(String requestUrl, String secretKey, BaseRequestBody<? extends BaseResponseBody> request, Map<String, String> params, Map<String, String> headers) {
		execute(requestUrl, secretKey, request, params, headers, null);
	}
	
	protected void execute(String requestUrl, String secretKey, BaseRequestBody<? extends BaseResponseBody> request, Map<String, String> params, Map<String, String> headers, File file) {
		BaseResponseBody responseBody = new HttpExcute().execute(requestUrl, secretKey, request,params, headers, file);
		printlnResult(responseBody);
	}
	
	/**
	 * 劲牌拉取接口
	 * @param requestUrl
	 * @param secretKey
	 * @param request
	 * @param params
	 * @param headers
	 * @param file
	 */
	protected void execute(String requestUrl, JpBaseRequestBody<? extends JpBaseResponseBody> request, Map<String, String> params, Map<String, String> headers) {
		JpBaseResponseBody responseBody = new HttpExcute().executeGet(requestUrl, request, params, headers);
	}
	
	protected void executePost(String requestUrl, JpBaseRequestBody<? extends JpBaseResponseBody> request, Map<String, String> params, Map<String, String> headers) {
		JpBaseResponseBody responseBody = new HttpExcute().executePost(requestUrl, request, params, headers);
	}
}
