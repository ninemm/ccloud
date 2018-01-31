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
package org.ccloud.front.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.qyweixin.sdk.api.ApiConfigKit;
import com.jfinal.qyweixin.sdk.api.JsTicket;
import com.jfinal.qyweixin.sdk.api.JsTicketApi;
import com.jfinal.qyweixin.sdk.api.JsTicketApi.JsApiType;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.BiSalesQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.wechat.WechatJSSDKInterceptor;
import org.joda.time.DateTime;

import java.util.*;

@RouterMapping(url = "/biIndex")
public class BiIndexController extends BaseFrontController {

	@Before(WechatJSSDKInterceptor.class)
	public void index() {

		String sellerId = "";

		sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		if (StrKit.isBlank(sellerId)) {
			sellerId = getPara(0);
			setSessionAttr(Consts.SESSION_SELLER_ID, sellerId);
		}

		render("bi_index.html");
	}

	// 顶部统计
	public void topTotal() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalAllCustomerCount",
				BiSalesQuery.me().findAllCustomerCount(sellerId, provName, cityName, countryName));
		result.put("totalCustomerCount",
				BiSalesQuery.me().findCustomerCount(sellerId, provName, cityName, countryName, null, null));

		renderJson(result);

	}

	public void total() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("orderAvg", BiSalesQuery.me().findOrderAvgAmountList(sellerId, provName, cityName, countryName,
				DateUtils.plusDays(startDate, -2), endDate));

		result.put("totalCustomerCount",
				BiSalesQuery.me().findCustomerCount(sellerId, provName, cityName, countryName, startDate, endDate));
		result.put("totalOrderCount",
				BiSalesQuery.me().findOrderCount(sellerId, provName, cityName, countryName, startDate, endDate));
		result.put("totalOrderAmount",
				BiSalesQuery.me().findTotalAmount(sellerId, provName, cityName, countryName, startDate, endDate));

		renderJson(result);

	}

	public void orderAmount() {

		String dealerCode = getSessionAttr("dealerCode");

		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		String dateType = getPara("dateType", "0").trim(); // 0: 昨天， 1: 最近1周， 2: 最近1月

		String startDate = DateUtils.getDateByType(dateType);
		String endDate = DateTime.now().toString(DateUtils.DEFAULT_NORMAL_FORMATTER);

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
		List<Record> list = BiSalesQuery.me().findOrderAmount(dealerCode, provName, cityName, null, startDate,
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

	@Before(WechatJSSDKInterceptor.class)
	public void customer() {
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

}
