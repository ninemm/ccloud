package org.ccloud.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.redis.Redis;

/**
 * @Description：根据CacheName清除 必须配合 @CacheName 使用
 * @ClassName: RedisEvictInterceptor.java
 * @Author：eric
 * @Date：2017年7月19日
 * -----------------变更历史-----------------
 * 如：who  2017年7月19日  修改xx功能
 */
public class RedisEvictInterceptor implements Interceptor {

	private static final String PREFIX_KEY = "intercept_";
	
	@Override
	public void intercept(Invocation inv) {
		inv.invoke();
        Redis.use().del(buildCacheName(inv));

	}
	
	private String buildCacheName(Invocation inv) {
        CacheName cacheName = inv.getMethod().getAnnotation(CacheName.class);
        if (cacheName != null)
            return PREFIX_KEY + cacheName.value();

        cacheName = inv.getController().getClass().getAnnotation(CacheName.class);
        if (cacheName == null)
            throw new RuntimeException("EvictInterceptor need CacheName annotation in controller.");
        return PREFIX_KEY + cacheName.value();
    }

}
