package org.ccloud.wechat;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.qyweixin.sdk.api.ApiConfigKit;
import com.jfinal.qyweixin.sdk.api.JsTicket;
import com.jfinal.qyweixin.sdk.api.JsTicketApi;
import com.jfinal.qyweixin.sdk.api.JsTicketApi.JsApiType;

public class WechatJSSDKInterceptor implements Interceptor {

	private static final String CACHE_NAME = "_CC_JSSDK";
	private static final String CACHE_KEY = "_CC_JSAPI_TICKET";
	
	@Override
	public void intercept(Invocation inv) {

		Controller controller = inv.getController();
		HttpServletRequest request = controller.getRequest();
		
		String jsapi_ticket = CacheKit.get(CACHE_NAME, CACHE_KEY);
		if (StrKit.isBlank(jsapi_ticket)) {
			JsTicket jsApiTicket = JsTicketApi.getTicket(JsApiType.jsapi);
			jsapi_ticket = jsApiTicket.getTicket();
			CacheKit.put(CACHE_NAME, CACHE_KEY, jsapi_ticket);
		}

		String nonce_str = create_nonce_str();
		// 注意 URL 一定要动态获取，不能 hardcode.
		String url = "http://" + request.getServerName() // 服务器地址
		// + ":"
		// + getRequest().getServerPort() //端口号
				+ request.getContextPath() // 项目名称
				+ request.getServletPath();// 请求页面或其他地址
		String qs = request.getQueryString(); // 参数
		if (qs != null) {
			url = url + "?" + (request.getQueryString());
		}
		// url="http://javen.tunnel.mobi/my_weixin/_front/share.jsp";
		// System.out.println("url>>>>" + url);
		String timestamp = create_timestamp();
		// 这里参数的顺序要按照 key 值 ASCII 码升序排序
		// 注意这里参数名必须全部小写，且必须有序
		String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url="
				+ url;

		String signature = HashKit.sha1(str);

		// System.out.println("corpId " + ApiConfigKit.getApiConfig().getCorpId()
		// + " nonceStr " + nonce_str + " timestamp " + timestamp);
		// System.out.println("url " + url + " signature " + signature);
		// System.out.println("nonceStr " + nonce_str + " timestamp " + timestamp);
		// System.out.println(" jsapi_ticket " + jsapi_ticket);
		// System.out.println("nonce_str " + nonce_str);
		controller.setAttr("appId", ApiConfigKit.getApiConfig().getCorpId());
		controller.setAttr("nonceStr", nonce_str);
		controller.setAttr("timestamp", timestamp);
		controller.setAttr("url", url);
		controller.setAttr("signature", signature);
		controller.setAttr("jsapi_ticket", jsapi_ticket);
	}
	
	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

}
