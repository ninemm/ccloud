/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.controller.bi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.User;
import org.ccloud.model.query.Bi2SalesQuery;
import org.ccloud.model.query.BiManagerQuery;
import org.ccloud.model.query.UserQuery;
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

@RouterMapping(url = "/biIndex")
public class BiIndexController extends BaseFrontController {

	public void index() {
		initWechatConfig();
//		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		User user = UserQuery.me().findById("7aee55cb56534e92a346467b9f3a262a");
		List<Record> sellerByUser = BiManagerQuery.me().findSellerByUser(user.getId());
		String sellerArray[] = new String[sellerByUser.size()];
		for (int i = 0; i < sellerByUser.size(); i++) {
			sellerArray[i] = sellerByUser.get(i).getStr("dealer_data_area");
		}

		List<Record> brandByUser = BiManagerQuery.me().findBrandByUser(user.getId());
		String brandArray[] = new String[brandByUser.size()];
		for (int i = 0; i < brandByUser.size(); i++) {
			brandArray[i] = brandByUser.get(i).getStr("brand_id");
		}

		List<Record> productByUser = BiManagerQuery.me().findProductByUser(user.getId());
		String productArray[] = new String[productByUser.size()];
		for (int i = 0; i < productByUser.size(); i++) {
			productArray[i] = productByUser.get(i).getStr("product_id");
		}

		setSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY, sellerArray);
		setSessionAttr(Consts.SESSION_BRAND_ID_ARRAY, brandArray);
		setSessionAttr(Consts.SESSION_PRODUCT_ID_ARRAY, productArray);

		render("bi_index.html");
	}

	// 顶部统计
	public void topTotal() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
		String[] dataAreaLike = new String[dataArea.length];
		for (int i = 0 ;i<dataArea.length;i++){
			dataAreaLike[i] = dataArea[i] + "%";
		}

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalAllCustomerCount",
				Bi2SalesQuery.me().findAllCustomerCount(dataAreaLike, provName, cityName, countryName));
		result.put("totalCustomerCount",
				Bi2SalesQuery.me().findCustomerCount(dataAreaLike, provName, cityName, countryName, null, null));

		renderJson(result);

	}

	public void total() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
		String[] dataAreaLike = new String[dataArea.length];
		for (int i = 0 ;i<dataArea.length;i++){
			dataAreaLike[i] = dataArea[i] + "%";
		}

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orderAvg", Bi2SalesQuery.me().findOrderAvgAmountList(dataArea, provName, cityName, countryName,
				DateUtils.plusDays(startDate, -2), endDate));

		result.put("totalCustomerCount",
				Bi2SalesQuery.me().findCustomerCount(dataAreaLike, provName, cityName, countryName, startDate, endDate));
		result.put("totalOrderCount",
				Bi2SalesQuery.me().findOrderCount(dataArea, provName, cityName, countryName, startDate, endDate));
		result.put("totalOrderAmount",
				Bi2SalesQuery.me().findTotalAmount(dataArea, provName, cityName, countryName, startDate, endDate));

		renderJson(result);

	}

	public void orderAmount() {

		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);

		String str = "countryName";
		int devideFlg = 1;
		if (StrKit.isBlank(provName)) {
			str = "provName";
			devideFlg = 2;
		} else if (StrKit.isBlank(cityName)) {
			str = "cityName";
			devideFlg = 3;
		} else if ("3".equals(dateType)) {
			devideFlg = 4;
		}

		List<LinkedList<Map<String, Object>>> result = Lists.newLinkedList();
		List<Record> list = Bi2SalesQuery.me().findOrderAmount(dataArea, provName, cityName, null, startDate,
				endDate, devideFlg);
		for (Record map : list) {

			LinkedList<Map<String, Object>> linkedList = new LinkedList<>();
			Map<String, Object> fromMap = Maps.newHashMap();
			fromMap.put("name", countryName);
			linkedList.add(fromMap);

			Map<String, Object> toMap = Maps.newHashMap();
			toMap.put("name", map.get(str));
			toMap.put("value", map.get("totalAmount"));
			linkedList.add(toMap);

			result.add(linkedList);
		}

		renderJson(result);
	}

	public void area() {
		setAttr("cur_nav", "area");
		render("bi_area.html");
	}

	public void customer() {
		initWechatConfig();
		setAttr("cur_nav", "customer");
		render("bi_customer.html");
	}

	public void product() {
		setAttr("cur_nav", "product");
		render("bi_product.html");
	}

	public void dealer() {
		setAttr("cur_nav", "dealer");
		render("bi_dealer.html");
	}

	public void dealerList(){


		Map<String, Object> result = new HashMap<String, Object>();
		result.put("dealerList",
				BiManagerQuery.me());

		renderJson(result);
	}

	public void selectSeller() {

		String dataArea = getPara("dataArea");
		String dataAreaArray[] = new String[]{dataArea};
		setSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY, dataAreaArray);
		List<Record> seller = Bi2SalesQuery.me().findSellerByDataArea(dataArea);

		renderJson(seller);
	}

	public void initWechatConfig() {

		String jsapi_ticket = CacheKit.get("ccloud", "jsapi_ticket");
		if (StrKit.isBlank(jsapi_ticket)) {
			JsTicket jsApiTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
			jsapi_ticket = jsApiTicket.getTicket();
			CacheKit.put("ccloud", "jsapi_ticket", jsapi_ticket);
		}

		String nonce_str = create_nonce_str();
		// 注意 URL 一定要动态获取，不能 hardcode.
		String url = "http://" + getRequest().getServerName() // 服务器地址
				             // + ":"
				             // + getRequest().getServerPort() //端口号
				             + getRequest().getContextPath() // 项目名称
				             + getRequest().getServletPath();// 请求页面或其他地址
		String qs = getRequest().getQueryString(); // 参数
		if (qs != null) {
			url = url + "?" + (getRequest().getQueryString());
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
