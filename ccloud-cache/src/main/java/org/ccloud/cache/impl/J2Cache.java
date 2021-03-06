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

import java.util.List;

import org.ccloud.cache.ICache;

import com.jfinal.plugin.ehcache.IDataLoader;

public class J2Cache implements ICache {
	@Override
	public <T> T get(String cacheName, Object key) {
		return null;
	}

	@Override
	public void put(String cacheName, Object key, Object value) {

	}

	@Override
	public List<?> getKeys(String cacheName) {
		return null;
	}

	@Override
	public void remove(String cacheName, Object key) {

	}

	@Override
	public void removeAll(String cacheName) {

	}

	@Override
	public <T> T get(String cacheName, Object key, IDataLoader dataLoader) {
		return null;
	}
}
