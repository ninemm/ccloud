package org.ccloud.cache.plugins;

import com.jfinal.plugin.IPlugin;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2Cache;

public class J2CachePlugin implements IPlugin {

	private static CacheChannel cache;
	
	@Override
	public boolean start() {
		System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
		cache = J2Cache.getChannel();
		org.ccloud.cache.impl.J2Cache.init(cache);
		return true;
	}

	@Override
	public boolean stop() {
		cache.close();
		return true;
	}

}
