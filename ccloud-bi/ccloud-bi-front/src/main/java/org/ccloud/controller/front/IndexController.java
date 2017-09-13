/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.controller.front;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.qyweixin.sdk.api.ApiConfigKit;
import com.jfinal.qyweixin.sdk.api.JsTicket;
import com.jfinal.qyweixin.sdk.api.JsTicketApi;
import com.jfinal.qyweixin.sdk.api.JsTicketApi.JsApiType;

@RouterMapping(url = "/")
public class IndexController extends BaseFrontController {

	public void index() {

		initWechatConfig();

		render("index.html");
	}
	
	public void total() {
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalCustomerCount", SalesFactQuery.me().findCustomerCount(provName, cityName,
				countryName, startDate, endDate));
		result.put("totalOrderCount", SalesFactQuery.me().findOrderCount(provName, cityName,
				countryName, startDate, endDate));
		result.put("totalOrderAmount", SalesFactQuery.me().findTotalAmount(provName, cityName,
				countryName, startDate, endDate));
		
		renderJson(result);

	}
	
	public void orderAmount() {
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		
		List<LinkedList<Map<String, Object>>> result = Lists.newLinkedList();
		List<Record> list = SalesFactQuery.me().findOrderAmount(provName, cityName, null, null, null);
		for (Record map : list) {
			
			if (! StrKit.equals(countryName, map.getStr("countryName"))) {
				LinkedList<Map<String, Object>> linkedList = new LinkedList<>();
				Map<String, Object> fromMap = Maps.newHashMap();
				fromMap.put("name", countryName);
				linkedList.add(fromMap);
				
				Map<String, Object> toMap = Maps.newHashMap();
				toMap.put("name", map.get("countryName"));
				toMap.put("value", map.get("totalAmount"));
				linkedList.add(toMap);
				
				result.add(linkedList);
			}
		}
		
		renderJson(result);
	}
	
	public void renderSales() {
		
	    render("sales.html");
	}
	
	public void area() {
		setAttr("cur_nav", "area");
		render("area.html");
	}
	
	public void customer() {
		setAttr("cur_nav", "customer");
		render("customer.html");
	}
	
	public void product() {
		setAttr("cur_nav", "product");
		render("product.html");
	}
	
    public void dealer() {
    	setAttr("cur_nav", "dealer");
        render("dealer.html");
    }
	
	public void initWechatConfig() {
		
		String jsapi_ticket = CacheKit.get("ccloud", "jsapi_ticket");
		if (StrKit.isBlank(jsapi_ticket)) {
			JsTicket jsApiTicket = JsTicketApi.getTicket(JsApiType.jsapi);
			jsapi_ticket = jsApiTicket.getTicket();
			CacheKit.put("ccloud", "jsapi_ticket", jsapi_ticket);
		}
		
		String nonce_str = create_nonce_str();
		// 注意 URL 一定要动态获取，不能 hardcode.
		String url = "http://" + getRequest().getServerName() // 服务器地址
//				 + ":"
//				 + getRequest().getServerPort() //端口号
				+ getRequest().getContextPath() // 项目名称
				+ getRequest().getServletPath();// 请求页面或其他地址
		String qs = getRequest().getQueryString(); // 参数
		if (qs != null) {
			url = url + "?" + (getRequest().getQueryString());
		}
		// url="http://javen.tunnel.mobi/my_weixin/_front/share.jsp";
		//System.out.println("url>>>>" + url);
		String timestamp = create_timestamp();
		// 这里参数的顺序要按照 key 值 ASCII 码升序排序
		// 注意这里参数名必须全部小写，且必须有序
		String str = "jsapi_ticket=" + jsapi_ticket +
        "&noncestr=" + nonce_str +
        "&timestamp=" + timestamp +
        "&url=" + url;

		String signature = HashKit.sha1(str);

//		System.out.println("corpId " + ApiConfigKit.getApiConfig().getCorpId()
//				+ "  nonceStr " + nonce_str + " timestamp " + timestamp);
//		System.out.println("url " + url + " signature " + signature);
//		System.out.println("nonceStr " + nonce_str + " timestamp " + timestamp);
//		System.out.println(" jsapi_ticket " + jsapi_ticket);
//		System.out.println("nonce_str  " + nonce_str);
		setAttr("appId", ApiConfigKit.getApiConfig().getCorpId());
		setAttr("nonceStr", nonce_str);
		setAttr("timestamp", timestamp);
		setAttr("url", url);
		setAttr("signature", signature);
		setAttr("jsapi_ticket", jsapi_ticket);
	}
 	
	private static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	private static String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

}
