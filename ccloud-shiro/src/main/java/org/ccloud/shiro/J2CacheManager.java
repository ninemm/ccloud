package org.ccloud.shiro;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;
import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.ccloud.shiro.ShiroJ2Cache;

public class J2CacheManager extends AbstractCacheManager implements Initializable, Destroyable {
	protected CacheChannel channel;

	public J2CacheManager() {
	}

	public void init() throws ShiroException {
		this.channel = J2Cache.getChannel();
	}

	public void destroy() throws Exception {
		if (this.channel != null) {
			this.channel.close();
		}

	}

	protected Cache<Object, Object> createCache(String name) throws CacheException {
		if (channel == null)
			channel = J2Cache.getChannel();
		return new ShiroJ2Cache(name, this.channel);
	}
}
