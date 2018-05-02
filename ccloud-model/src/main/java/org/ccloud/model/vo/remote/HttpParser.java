/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo.remote;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.ccloud.model.vo.remote.jp.pull.JpBaseRequestBody;
import org.ccloud.model.vo.remote.jp.pull.JpBaseResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * @author wally
 *
 */
public class HttpParser {
	public static <T extends JpBaseResponseBody> T parseResult(JpBaseRequestBody<T> requestBody, String result) {
		Class<T> responseClazz = null;
		try {
			// 得到返回的clazz
			Type requestClazz = requestBody.getClass().getGenericSuperclass();
			responseClazz = (Class<T>) ((ParameterizedType) requestClazz)
					.getActualTypeArguments()[0];

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
			
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
}
