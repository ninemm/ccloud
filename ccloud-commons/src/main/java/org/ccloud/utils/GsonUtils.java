/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfinal.kit.StrKit;

/**
 * @author wally
 *
 */
public class GsonUtils {
	public static <T> List<T> deserializeList(String json, Type type) {
		if(StrKit.isBlank(json))
			return null;
	    Gson gson = new GsonBuilder().setDateFormat(DateUtils.DEFAULT_FORMATTER).create();
	    return  gson.fromJson(json, type);
	}
}
