/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.remote.jp.pull.http;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.Charsets;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ccloud.model.vo.remote.HttpParser;
import org.ccloud.model.vo.remote.jp.pull.JpBaseRequestBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseResponseBody;
import org.ccloud.utils.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfinal.kit.StrKit;

/**
 * @author wally
 *
 */
public class JpHttpClientExecute {
	private static CloseableHttpClient httpclient;
	
	private static final String REQUEST_URL = "http://im.jingpai.com/api/http/v1/";
	private static final String IM_CLIENT_CODE = "8605a70f-107a-45dd-b124-fa4c45c0a14d";
	private static final String IM_SECRET_KEY = "754953903190a8d29ee5bcf937208449";
	private static final String CONTENT_TYPE = "application/json";
	
	static {
		init();
	}
	private static void init() {
    	RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(60000).build();  
        httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
	
	/**
	 * 整理传输数据
	 * @param requestUrl：请求地址
	 * @param request：请求主体
	 * @param params：请求参数
	 * @param headers：请求头信息
	 * @return
	 */
	public static <T extends JpBaseResponseBody> T executeGet(String requestUrl, JpBaseRequestBody<T> requestBody, Map<String, String> params, Map<String, String> headers) {
		String result = null;
		try {
			result = executeGet(requestUrl, params, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StrKit.isBlank(result))
			return null;
		System.out.println(result);
		
		return HttpParser.parseResult(requestBody, result);
	}
	
	/**
	 * 整理传输数据
	 * @param requestUrl：请求地址
	 * @param request：请求主体
	 * @param params：请求参数
	 * @param headers：请求头信息
	 * @return
	 */
	public static <T extends JpBaseResponseBody> T executePost(String requestUrl, JpBaseRequestBody<T> requestBody, Map<String, String> params, Map<String, String> headers) {
		String result = null;
		try {
			result = executePost(requestUrl, params, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StrKit.isBlank(result))
			return null;
		System.out.println(result);
		
		return HttpParser.parseResult(requestBody, result);
	}

	/**
	 * HttpClient Get请求
	 * @param urlStr：请求url
	 * @param params：请求参数
	 * @param headers：请求头信息
	 * @return
	 * @throws Exception
	 */
	public static String executeGet(String urlStr, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		try {
			httpGet = new HttpGet();
			if (headers == null) {
				headers = new HashedMap<String, String>();
	        }
			headers.put("imClientCode", IM_CLIENT_CODE);
			headers.put("imSecretKey", IM_SECRET_KEY);
			headers.put("Content-Type", CONTENT_TYPE);
			
			Entry<String, String> entry = null;
            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
            	entry = iterator.next();
				httpGet.addHeader(entry.getKey(), entry.getValue());
				entry = null;
            }
			
			List<NameValuePair> pairs = null;
	        if (params != null && !params.isEmpty()) {  
	            pairs = new ArrayList<NameValuePair>(params.size());
	            for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					entry = null;
				}
	            String requestParam = URLEncodedUtils.format(pairs, Charsets.UTF_8.displayName());
	            httpGet.setURI(URI.create(urlStr + "?" + requestParam));
	        }

			response = httpclient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200 || code == 500) {// 正常
				HttpEntity httpEntity = response.getEntity();
				String result = null; 
				if (httpEntity != null) {  
		            result = EntityUtils.toString(httpEntity, "utf-8");  
		            EntityUtils.consume(httpEntity);  
		            return result;  
		        } else {  
		             return null;  
		        }  
			} else if (code >= 300 && code < 400) {// 30x 跳转
				Header header = httpGet.getFirstHeader("Location");
				if (header != null) {
					String newuri = header.getValue();
					return executeGet(newuri, params, headers);
				}
			} else if (code >= 400 && code < 500) {// 40x权限不够
			} else if (code >= 500 && code < 600) {// 50x系统出错
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(response != null)
				response.close();
			if(httpGet != null)
				httpGet.abort();
		}
		return null;
	}
	
	/**
	 * 传输数据
	 * @param params
	 * @return
	 */
	private static String executePost(String urlStr, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		HttpPost post = null;
		CloseableHttpResponse response = null;
		try {
			post = new HttpPost(urlStr);
			if (headers != null && !headers.isEmpty()) {
	        	Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					post.addHeader(entry.getKey(), entry.getValue());
	            }
	        }
			Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
	        post.setEntity(new StringEntity(gson.toJson(params), Charsets.UTF_8));

			response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200 || code == 500) {// 正常
				HttpEntity httpEntity = response.getEntity();
				String result = null; 
				if (httpEntity != null) {  
		            result = EntityUtils.toString(httpEntity, "utf-8");  
		            EntityUtils.consume(httpEntity);  
		            response.close();  
		            return result;  
		        } else {  
		             return null;  
		        }  
			} else if (code >= 300 && code < 400) {// 30x 跳转
				Header header = post.getFirstHeader("Location");
				if (header != null) {
					String newuri = header.getValue();
					return executePost(newuri, params, headers);
				}
			} else if (code >= 400 && code < 500) {// 40x权限不够
			} else if (code >= 500 && code < 600) {// 50x系统出错
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if(response != null)
				response.close();
			if(post != null)
				post.abort();
		}
		return null;
	}
	
	/**
	 * 根据接口名称获得请求地址
	 * @param apiName：接口名称
	 * @return
	 */
	public static String getRequestUrl(String apiName) {
		StringBuilder stringBuilder = new StringBuilder(REQUEST_URL).append(apiName);
		return stringBuilder.toString();
	}
}
