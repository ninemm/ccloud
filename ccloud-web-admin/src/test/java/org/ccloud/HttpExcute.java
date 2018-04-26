package org.ccloud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ccloud.model.vo.BaseRequestBody;
import org.ccloud.model.vo.BaseResponseBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseRequestBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseResponseBody;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.EncryptUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;


/**
 * 数据请求服务
 * 
 * @author wally
 */
public class HttpExcute {
	private CloseableHttpClient httpclient;  
    
    public HttpExcute() {
    	init();
    }
    public HttpExcute(String requestUrl) {
    	init();
    }
    public HttpExcute(String secretKey, String requestUrl) {
    	init();
    }
    
    private void init() {
    	RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(60000).build();  
        httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
    
	public <T extends BaseResponseBody> T execute(String requestUrl, String secretKey, BaseRequestBody<T> request, Map<String, String> params, Map<String, String> headers) {
		return execute(requestUrl, secretKey, request, params, headers, null);
	}
	
	
	/**
	 * 整理传输数据
	 * 
	 * @param <T>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends BaseResponseBody> T execute(String requestUrl, String secretKey, BaseRequestBody<T> request, Map<String, String> params, Map<String, String> headers, File file) {
		Class<T> responseClazz = null;
		try {
			// 得到返回的clazz
			Type requestClazz = request.getClass().getGenericSuperclass();
			responseClazz = (Class<T>) ((ParameterizedType) requestClazz)
					.getActualTypeArguments()[0];

			//
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			params.put("sign_method", "1");
			String md5 = EncryptUtils.signForRequest(params, secretKey);
			params.put("sign", md5);
			System.out.println(ToStringBuilder.reflectionToString(params, ToStringStyle.MULTI_LINE_STYLE));
//			String url = BASE_URL+";jsessionid="+getSession2File();
			String result = post(requestUrl, params, headers, URLEncodedUtils.CONTENT_TYPE, file);
			try {
				T t = gson.fromJson(result, responseClazz);
				writeSession2File(t);
				return t;
			} catch (JsonSyntaxException e) {
				throw new Exception("json转对象出错");
			}

		} catch (Exception e) {
			if (null != responseClazz) {
				try {
					T response = responseClazz.newInstance();
					return response;
				} catch (Exception e1) {

				}
			}
			return null;
		}
	}
	
	/**
	 * 传输数据
	 * @param params
	 * @return
	 */
	private String post(String urlStr, Map<String, String> params, Map<String, String> headers, String contentType, File file)
			throws Exception {
		try {
			HttpPost post = new HttpPost(urlStr);
			writeSessionId(post);
			//
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			if (headers != null && !headers.isEmpty()) {
	        	Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					post.addHeader(entry.getKey(), entry.getValue());
	            }
	        }
			
			List<NameValuePair> pairs = null;
	        if (params != null && !params.isEmpty()) {  
	            pairs = new ArrayList<NameValuePair>(params.size());
	            Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
				}
	            String entityString = URLEncodedUtils.format(pairs, Charsets.UTF_8.displayName());
	            ContentType contentTypeObject = ContentType.create(contentType, Charsets.UTF_8.displayName());
	            
	            post.setEntity(new StringEntity(entityString, contentTypeObject));
	        }
			// 创建一个请求头的字段，比如content-type,text/plain
			if(file != null){
				FileBody fileBody = new FileBody(file);
				builder.addPart("mediaFile", fileBody);
				HttpEntity entity = builder.build();
				post.setEntity(entity);
			}

			CloseableHttpResponse response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			readSessionId(response);

			if (code == 200 || code==500) {// 正常
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
				// Header header = post.getResponseHeader("location");
				Header header = post.getFirstHeader("Location");
				if (header != null) {
					String newuri = header.getValue();
					return post(newuri, params, headers, contentType,null);
				}
			} else if (code >= 400 && code < 500) {// 40x权限不够
			} else if (code >= 500 && code < 600) {// 50x系统出错
			}
		} catch (Exception e) {
			if (e instanceof Exception) {
				e.printStackTrace();
				throw (Exception) e;
			}
		}
		return null;
	}

	/**
	 * 读取cookie
	 * 
	 * @param response
	 */
	private void readSessionId(HttpResponse response) {
//		Header header = response.getFirstHeader("Set-Cookie");
//		if (header != null) {
//			try {
//				FileUtils.writeStringToFile(new File("cookie2"),
//						header.getValue(), false);
//			} catch (IOException e) {
//			}
//		}
	}

	/**
	 * 写入cookie
	 * 
	 * @param post
	 */
	private void writeSessionId(HttpPost post) {
//		try {
//			String cookies = FileUtils.readFileToString(new File("cookie2"));
//			if (cookies != null && cookies.length() > 0) {
//				post.addHeader("cookie", cookies);
//
//			}
//		} catch (IOException e) {
//
//		}
	}
	
	
	
	private void writeSession2File(BaseResponseBody baseResponseBody) {
//		try {
//			FileUtils.writeStringToFile(new File("cookie"),
//					baseResponseBody.getJsessionid(), false);
//		} catch (IOException e) {
//		}
	}
	
	/**
	 * 整理传输数据
	 * 
	 * @param <T>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends JpBaseResponseBody> T executeGet(String requestUrl, JpBaseRequestBody<T> request, Map<String, String> params, Map<String, String> headers) {
		Class<T> responseClazz = null;
		try {
			// 得到返回的clazz
			Type requestClazz = request.getClass().getGenericSuperclass();
			responseClazz = (Class<T>) ((ParameterizedType) requestClazz)
					.getActualTypeArguments()[0];

			//
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String result = get(requestUrl, params, headers);
			
			System.out.println(result);
			try {
				T t = gson.fromJson(result, responseClazz);
				return t;
			} catch (JsonSyntaxException e) {
				throw new Exception("json转对象出错");
			}

		} catch (Exception e) {
			if (null != responseClazz) {
				try {
					T response = responseClazz.newInstance();
					return response;
				} catch (Exception e1) {

				}
			}
			return null;
		}
	}
	
	/**
	 * 整理传输数据
	 * 
	 * @param <T>
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends JpBaseResponseBody> T executePost(String requestUrl, JpBaseRequestBody<T> request, Map<String, String> params, Map<String, String> headers) {
		Class<T> responseClazz = null;
		try {
			// 得到返回的clazz
			Type requestClazz = request.getClass().getGenericSuperclass();
			responseClazz = (Class<T>) ((ParameterizedType) requestClazz)
					.getActualTypeArguments()[0];

			//
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			String result = jpPost(requestUrl, params, headers);
			System.out.println(result);
			try {
				T t = gson.fromJson(result, responseClazz);
				return t;
			} catch (JsonSyntaxException e) {
				throw new Exception("json转对象出错");
			}

		} catch (Exception e) {
			if (null != responseClazz) {
				try {
					T response = responseClazz.newInstance();
					return response;
				} catch (Exception e1) {

				}
			}
			return null;
		}
	}

	protected String readString(InputStream in, String charset)
			throws IOException {
		byte[] data = new byte[1024];
		int length = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((length = in.read(data)) != -1) {
			bout.write(data, 0, length);
		}
		return new String(bout.toByteArray(), charset);
	}
	
	/**
	 * 传输数据
	 * @param params
	 * @return
	 */
	private String get(String urlStr, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		try {
			HttpGet httpGet = new HttpGet();
			
			if (headers != null && !headers.isEmpty()) {
	        	Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					httpGet.addHeader(entry.getKey(), entry.getValue());
	            }
	        }
			
			List<NameValuePair> pairs = null;
	        if (params != null && !params.isEmpty()) {  
	            pairs = new ArrayList<NameValuePair>(params.size());
	            Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = params.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
				}
	            String requestParam = URLEncodedUtils.format(pairs, Charsets.UTF_8.displayName());
	            httpGet.setURI(URI.create(urlStr + "?" + requestParam));
	        }

			CloseableHttpResponse response = httpclient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();

			if (code == 200 || code==500) {// 正常
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
				// Header header = post.getResponseHeader("location");
				Header header = httpGet.getFirstHeader("Location");
				if (header != null) {
					String newuri = header.getValue();
					return get(newuri, params, headers);
				}
			} else if (code >= 400 && code < 500) {// 40x权限不够
//				throw new Exception(BaseResponseBody.CODE_40x);
			} else if (code >= 500 && code < 600) {// 50x系统出错
//				throw new Exception(BaseResponseBody.CODE_50x);
			}
		} catch (Exception e) {
			if (e instanceof Exception) {
				e.printStackTrace();
				throw (Exception) e;
			}
		}
//		throw new Exception(BaseResponseBody.CODE_NETWORK_ERROR);
		return null;
	}
	
	/**
	 * 传输数据
	 * @param params
	 * @return
	 */
	private String jpPost(String urlStr, Map<String, String> params, Map<String, String> headers)
			throws Exception {
		try {
			HttpPost post = new HttpPost(urlStr);
			writeSessionId(post);
			//
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			if (headers != null && !headers.isEmpty()) {
	        	Entry<String, String> entry = null;
	            for (Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext();) {
					entry = iterator.next();
					post.addHeader(entry.getKey(), entry.getValue());
	            }
	        }
			Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
	        post.setEntity(new StringEntity(gson.toJson(params), Charsets.UTF_8));

			CloseableHttpResponse response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			readSessionId(response);

			if (code == 200 || code==500) {// 正常
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
				// Header header = post.getResponseHeader("location");
				Header header = post.getFirstHeader("Location");
				if (header != null) {
					String newuri = header.getValue();
					return jpPost(newuri, params, headers);
				}
			} else if (code >= 400 && code < 500) {// 40x权限不够
			} else if (code >= 500 && code < 600) {// 50x系统出错
			}
		} catch (Exception e) {
			if (e instanceof Exception) {
				e.printStackTrace();
				throw (Exception) e;
			}
		}
		return null;
	}
}

