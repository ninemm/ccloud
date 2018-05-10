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

import com.alibaba.fastjson.JSON;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.User;
import org.ccloud.model.query.Bi2SalesQuery;
import org.ccloud.model.query.BiManagerQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;
import org.ccloud.wwechat.WorkWechatJSSDKInterceptor;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/")
public class BiIndexController extends BaseFrontController {

	@Before(WorkWechatJSSDKInterceptor.class)
	public void index() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (user == null) {
			renderNull();
			return;
		}

		//====================================本地开发放开此段注释================================================
//		User user = UserQuery.me().findById("7aee55cb56534e92a346467b9f3a262a");
//		List<Record> sellerByUser = BiManagerQuery.me().findSellerByUser(user.getId());
//		String sellerArray[] = new String[sellerByUser.size()];
//		String sellerNameArray[] = new String[sellerByUser.size()];
//		for (int i = 0; i < sellerByUser.size(); i++) {
//			sellerArray[i] = sellerByUser.get(i).getStr("dealer_data_area");
//			sellerNameArray[i] = sellerByUser.get(i).getStr("seller_name");
//		}
//
//		List<Record> brandByUser = BiManagerQuery.me().findBrandByUser(user.getId());
//		String brandArray[] = new String[brandByUser.size()];
//		String brandNameArray[] = new String[brandByUser.size()];
//		for (int i = 0; i < brandByUser.size(); i++) {
//			brandArray[i] = brandByUser.get(i).getStr("brand_id");
//			brandNameArray[i] = brandByUser.get(i).getStr("name");
//		}
//
//		List<Record> productByUser = BiManagerQuery.me().findProductByUser(user.getId());
//		String productArray[] = new String[productByUser.size()];
//		for (int i = 0; i < productByUser.size(); i++) {
//			productArray[i] = productByUser.get(i).getStr("product_id");
//		}
//
//		setSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY, sellerArray);
//		setSessionAttr(Consts.SESSION_DEALER_NAME_ARRAY, sellerNameArray);
//		setSessionAttr(Consts.SESSION_BRAND_ID_ARRAY, brandArray);
//		setSessionAttr(Consts.SESSION_BRAND_NAME_ARRAY, brandNameArray);
//		setSessionAttr(Consts.SESSION_PRODUCT_ID_ARRAY, productArray);

		//==============================================================================================================

//		//从session取
		String[] dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
		String[] brandId = getSessionAttr(Consts.SESSION_BRAND_ID_ARRAY);

		setAttr("orderCustomerCount", Bi2SalesQuery.me().findCustomerCount(dataArea, null, null, brandId));

		render("index.html");
	}

	public void selectDealer() {

		String dataArea = getPara("dataArea");

		Map<String, Object> result = new HashMap<String, Object>();

		if (StrKit.isBlank(dataArea)) {
			result.put("provName", "");
			result.put("cityName", "");
			result.put("countryName", "");
		} else {
			Record seller = Bi2SalesQuery.me().findSellerByDataArea(dataArea);
			result.put("provName", seller.getStr("prov_name"));
			result.put("cityName", seller.getStr("city_name"));
			result.put("countryName", seller.getStr("country_name"));
		}

		renderJson(result);
	}


	public void total() {

		String[] dataArea = this.getDataArea(getPara("dataArea"));
		String[] brandId = this.getBrandId(getPara("brandId"));

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Map<String, Object> result = new HashMap<String, Object>();

		result.put("totalCustomerCount",
				Bi2SalesQuery.me().findCustomerCount(dataArea, startDate, endDate, brandId));
		result.put("totalOrderCount",
				Bi2SalesQuery.me().findOrderCount(dataArea, startDate, endDate, brandId));
		result.put("totalOrderAmount",
				Bi2SalesQuery.me().findTotalAmount(dataArea, startDate, endDate, brandId));

		result.put("totalVisitCount",
				Bi2SalesQuery.me().findVistCount(dataArea, startDate, endDate));

		renderJson(result);

	}

/*	public void orderAmount() {

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
	}*/

	public void area() {
		setAttr("cur_nav", "area");
		render("bi_area.html");
	}

	@Before(WorkWechatJSSDKInterceptor.class)
	public void customer() {
		//initWechatConfig();
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

	private String[] getDataArea(String dataArea) {
		if (StrKit.notBlank(dataArea)) {
			return new String[]{dataArea};
		}
		return getSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY);
	}

	private String[] getBrandId(String brandId) {
		if (StrKit.notBlank(brandId)) {
			return new String[]{brandId};
		}
		return getSessionAttr(Consts.SESSION_BRAND_ID_ARRAY);
	}

}
