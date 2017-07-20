package org.ccloud.interceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.RenderInfo;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.render.Render;

/**
 * @Description：JFinal中提供cacheInterceptor 只支持 ehcache
 * @ClassName: RedisCacheInterceptor.java
 * @Author：eric
 * @Date：2017年7月19日
 * -----------------变更历史-----------------
 * 如：who  2017年7月19日  修改xx功能
 */
public class RedisCacheInterceptor implements Interceptor {

	private static final String RENDER_KEY = "_renderKey";
    private static final String PREFIX_KEY = "intercept_";
    private static ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>();
	
	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
        String cacheName = buildCacheName(inv, controller);
        String cacheKey = buildCacheKey(inv, controller);
        Map<String, Object> cacheData = Redis.use().hget(cacheName, cacheKey);
        if (cacheData == null) {
            Lock lock = getLock(cacheName);
            lock.lock();
            try {
                cacheData = Redis.use().hget(cacheName, cacheKey);
                if (cacheData == null) {
                    inv.invoke();
                    cacheAction(cacheName, cacheKey, controller);
                    return;
                }
            } finally {
                lock.unlock();
            }
        }

        useCacheDataAndRender(cacheData, controller);

	}
	
	private ReentrantLock getLock(String key) {
        ReentrantLock lock = lockMap.get(key);
        if (lock != null)
            return lock;

        lock = new ReentrantLock();
        ReentrantLock previousLock = lockMap.putIfAbsent(key, lock);
        return previousLock == null ? lock : previousLock;
    }
	
	private String buildCacheName(Invocation inv, Controller controller) {
        CacheName cacheName = inv.getMethod().getAnnotation(CacheName.class);
        if (cacheName != null)
            return PREFIX_KEY + cacheName.value();
        cacheName = controller.getClass().getAnnotation(CacheName.class);
        return (cacheName != null) ? PREFIX_KEY + cacheName.value() : PREFIX_KEY + inv.getActionKey();
    }

    private String buildCacheKey(Invocation inv, Controller controller) {
        StringBuilder sb = new StringBuilder(inv.getActionKey());
        String urlPara = controller.getPara();
        if (urlPara != null)
            sb.append("/").append(urlPara);

        String queryString = controller.getRequest().getQueryString();
        if (queryString != null)
            sb.append("?").append(queryString);
        return sb.toString();
    }

    private void cacheAction(String cacheName, String cacheKey, Controller controller) {
        HttpServletRequest request = controller.getRequest();
        Map<String, Object> cacheData = new HashMap<String, Object>();
        for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            cacheData.put(name, request.getAttribute(name));
        }

        Render render = controller.getRender();
        if (render != null) {
            cacheData.put(RENDER_KEY, createRenderInfo(render));        // cache RenderInfo
        }
        Redis.use().hset(cacheName, cacheKey, cacheData);
    }

    private RenderInfo createRenderInfo(Render render) {
        return new RenderInfo(render);
    }

    private void useCacheDataAndRender(Map<String, Object> cacheData, Controller controller) {
        HttpServletRequest request = controller.getRequest();
        Set<Map.Entry<String, Object>> set = cacheData.entrySet();

        for (Map.Entry<String, Object> entry : set) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.removeAttribute(RENDER_KEY);

        RenderInfo renderInfo = (RenderInfo) cacheData.get(RENDER_KEY);
        if (renderInfo != null) {
            controller.render(renderInfo.createRender());
        }
    }

}
