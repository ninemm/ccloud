/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.cache.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ccloud.cache.ICache;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

public class J2Cache implements ICache {

	private static CacheChannel cache;

	public static void init(CacheChannel cache) {
		J2Cache.cache = cache;
	}

	public static CacheChannel getCacheChannel() {
		return cache;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <T> T get(String cacheName, Object key) {
		CacheObject cacheObject = cache.get(cacheName, key.toString());
		return cacheObject != null ? (T) cacheObject.getValue() : null;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		cache.set(cacheName, key.toString(), value);
	}

	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		cache.set(cacheName, key.toString(), value, liveSeconds);
	}

	@Override
	public List<?> getKeys(String cacheName) {
		Collection<String> keys = cache.keys(cacheName);
		return keys != null ? new ArrayList<String>(keys) : null;
	}

	@Override
	public void remove(String cacheName, Object keys) {
		cache.evict(cacheName, keys.toString());
	}

	@Override
	public void removeAll(String cacheName) {
		cache.clear(cacheName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
		Object value = get(cacheName, key);
		if (value == null) {
			value = dataLoader.load();
			if (value != null) {
				put(cacheName, key, value);
			}
		}
		return (T) value;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader, int liveSeconds) {
		if (liveSeconds <= 0) {
			return get(cacheName, key, dataLoader);
		}

		Object data = get(cacheName, key);
		if (data == null) {
			data = dataLoader.load();
			put(cacheName, key, data, liveSeconds);
		}
		return (T) data;
	}
}
