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
package org.ccloud.cache;

import org.ccloud.cache.impl.JEhCache;

import com.jfinal.plugin.IPlugin;

public class JCachePlugin implements IPlugin {

	public JCachePlugin() {
		CacheManager.me().init(JEhCache.class);
	}

	public JCachePlugin(Class<? extends ICache> clazz) {
		if (clazz == null) {
			throw new RuntimeException("clazz must not be null");
		}
		CacheManager.me().init(clazz);
	}

	public com.jfinal.plugin.activerecord.cache.ICache getCache() {
		return CacheManager.me().getCache();

	}

	@Override
	public boolean start() {
		return true;
	}

	@Override
	public boolean stop() {
		CacheManager.me().destroy();
		return true;
	}

}
