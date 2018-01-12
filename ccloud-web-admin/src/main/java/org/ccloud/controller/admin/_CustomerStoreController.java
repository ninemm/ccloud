/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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
package org.ccloud.controller.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.CustomerStore;
import org.ccloud.model.query.CustomerStoreQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.HttpUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customerStore", viewPath = "/WEB-INF/admin/customer_store")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerStoreController extends JBaseCRUDController<CustomerStore> {

	@Override
	@RequiresPermissions(value = { "/admin/customerStore", "/admin/all" }, logical = Logical.OR)

	public void index() {
		render("index.html");
	}

	@RequiresPermissions(value = { "/admin/customerStore", "/admin/all" }, logical = Logical.OR)
	public void list() {

		Map<String, String[]> paraMap = getParaMap();
		String keyword = StringUtils.getArrayFirst(paraMap.get("k"));
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		Page<Record> page = CustomerStoreQuery.me().paginate(getPageNumber(), getPageSize(), keyword);
		List<Record> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);

	}

	@RequiresPermissions(value = { "/admin/customerStore", "/admin/all" }, logical = Logical.OR)
	public void enable() {

		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");

		if (CustomerStoreQuery.me().enable(id, isEnabled)) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}

	}

	@RequiresPermissions(value = { "/admin/customerStore", "/admin/all" }, logical = Logical.OR)
	public void importing() {
	}

	@RequiresPermissions(value = { "/admin/customerStore", "/admin/all" }, logical = Logical.OR)
	@Before(Tx.class)
	public void save() {

		String query = getPara("query");// 关键词
		String region = getPara("region");
		Integer page = getParaToInt("page", 0);

		String url = "http://api.map.baidu.com/place/v2/search";

		Map<String, Object> params = Maps.newHashMap();
		params.put("query", query);
		params.put("region", region);
		params.put("scope", 2);
		params.put("page_num", page);
		params.put("page_size", 10);// 与关键字的个数有关

		params.put("output", "json");
		params.put("ak", "IF8oL2gwIMYer9dGwKS102Iu5qAXMPg9");
		//http://api.map.baidu.com/place/v2/search?query=餐厅&page_size=10&page_num=0&scope=2&region=武汉&output=json&ak=XOmrQWipDSlB74BYZCuZrjkfuazlnGx7

		try {

			Map<String, Integer> resultMap = doSave(params, url);
			Integer save = resultMap.get("save");
			Integer exist = resultMap.get("exist");

			if (save > 0 || exist > 0) {
				renderAjaxResultForSuccess("成功拉取" + save + "个客户,更新" + exist + "个客户");
			} else {
				renderAjaxResultForError(region + ", 未搜索到" + query);
			}

			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderAjaxResultForError("拉取失败");
	}

	@SuppressWarnings("unchecked")
	private Map<String, Integer> doSave(Map<String, Object> params, String url) throws Exception {

		Integer status = 0;
//		Integer pageNum = (Integer) params.get("page_num");
		Integer pageNum = 40;
		Map<String, Integer> resultMap = Maps.newHashMap();
		List<CustomerStore> save = Lists.newArrayList();
		List<CustomerStore> exist = Lists.newArrayList();

		String locationUrl = "http://api.map.baidu.com/geocoder/v2/";

		Map<String, Object> locationParams = Maps.newHashMap();
		locationParams.put("output", "json");
		locationParams.put("ak", "IF8oL2gwIMYer9dGwKS102Iu5qAXMPg9");
		// http://api.map.baidu.com/geocoder/v2/?ak=xZmo3i3XbZoV0jqtaz3Fpo1CNAl8psRm&output=json&location=30.596683,114.350924

		do {
			params.put("page_num", pageNum);

			String storeJsonData = HttpUtils.get(url, params);
			Map<String, Object> storeMap = JSON.parseObject(storeJsonData);

			status = (Integer) storeMap.get("status");
			Object _results = storeMap.get("results");

			if (status == 0 && _results != null) {

				JSONArray results = (JSONArray) _results;
				for (int i = 0; i < results.size(); i++) {
					Map<String, Object> map = (Map<String, Object>) results.get(i);

					String uid = (String) map.get("uid");
					CustomerStore customerStore = CustomerStoreQuery.me().findByUid(uid);
					if (customerStore == null) {

						customerStore = new CustomerStore();
					}

					customerStore.setName((String) map.get("name"));

					Map<String, Object> locatioMap = (Map<String, Object>) map.get("location");
					BigDecimal lng = (BigDecimal) locatioMap.get("lng");
					BigDecimal lat = (BigDecimal) locatioMap.get("lat");
					customerStore.setLng(lng);
					customerStore.setLat(lat);

					locationParams.put("location", lat + "," + lng);
					String locationData = HttpUtils.get(locationUrl, locationParams);
					Map<String, Object> locationDataMap = (Map<String, Object>) JSON.parseObject(locationData).get("result");
					Map<String, Object> addressComponent = (Map<String, Object>) locationDataMap.get("addressComponent");
					customerStore.setProvName((String) addressComponent.get("province"));
					customerStore.setCityName((String) addressComponent.get("city"));
					customerStore.setCountryName((String) addressComponent.get("district"));

					customerStore.setAddress((String) map.get("address"));
					customerStore.setTelephone((String) map.get("telephone"));
					customerStore.setUid(uid);
					customerStore.setStreetId((String) map.get("street_id"));

					Integer detail = (Integer) map.get("detail");
					customerStore.setDetail(detail == 1 ? true : false);
					if (detail == 1) {
						Map<String, Object> detailInfoMap = (Map<String, Object>) map.get("detail_info");
						customerStore.setType((String) detailInfoMap.get("type"));
						customerStore.setTag((String) detailInfoMap.get("tag"));
						customerStore.setDetailUrl((String) detailInfoMap.get("detail_url"));

						Object price = detailInfoMap.get("price");
						if (price != null) {
							customerStore.setPrice(new BigDecimal((String) price));
						}

						Object overallRating = detailInfoMap.get("overall_rating");
						if (overallRating != null) {
							customerStore.setOverallRating(new BigDecimal((String) overallRating));
						}

						Object commentNum = detailInfoMap.get("comment_num");
						if (commentNum != null) {
							customerStore.setCommentNum(Integer.valueOf((String) commentNum));
						}
						Object grouponNum = detailInfoMap.get("groupon_num");
						if (grouponNum != null) {
							customerStore.setGrouponNum(Integer.valueOf((String) grouponNum));
						}
					}

					customerStore.setPage(pageNum);
					if (customerStore.getId() == null) {
						customerStore.setCreateDate(new Date());
						save.add(customerStore);
					} else {
						customerStore.setModifyDate(new Date());
						exist.add(customerStore);
					}
				}

				if ((save.size() == 0 && exist.size() == 0) || results.size() < 10) {
					break;
				}

			} else {
				break;
			}
			pageNum++;
		} while (true);// TODO测试次数不要太多

		if (status == 302) {
			renderAjaxResultForError("地图调用次数超过限制");
			resultMap.put("total", 0);
			resultMap.put("exsit", 0);
			return resultMap;
		}

		for (CustomerStore customerStore : save) {
			customerStore.save();
		}

		for (CustomerStore customerStore : exist) {
			customerStore.update();
		}

		resultMap.put("save", save.size());
		resultMap.put("exist", exist.size());
		return resultMap;
	}

}
