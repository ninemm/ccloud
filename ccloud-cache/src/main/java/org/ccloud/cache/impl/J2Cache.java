/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> T get(String cacheName, Object key) {
		try {
			CacheObject cacheObject = cache.get(cacheName, key.toString());
			return cacheObject != null ? (T) cacheObject.getValue() : null;
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
		return null;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {
		try {
			cache.set(cacheName, key.toString(), (Serializable) value);
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
	}
	
	public void put(String cacheName, Object key, Object value, int liveSeconds) {
		try {
			cache.set(cacheName, key.toString(), (Serializable) value, liveSeconds);
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
    }

	@Override
	public List<?> getKeys(String cacheName) {
		try {
			Collection<String> keys = cache.keys(cacheName);
			return keys != null ? new ArrayList<String>(keys) : null;
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
		return null;
	}

	@Override
	public void remove(String cacheName, Object keys) {
		try {
			cache.evict(cacheName, keys.toString());
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
	}

	@Override
	public void removeAll(String cacheName) {
		try {
			cache.clear(cacheName);
		} catch (IOException e) {
			LogKit.error(e.toString(), e);
		}
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
