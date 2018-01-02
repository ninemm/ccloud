package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Dict;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.ProductCompositionQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.wechat.WechatJSSDKInterceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/product")
public class ProductController extends BaseFrontController {

	public void index() {

		render("product.html");
	}

	public void productList() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");
		List<Record> productList = SellerProductQuery.me().findProductListForApp(sellerId, keyword);
		List<Record> compositionList = ProductCompositionQuery.me().findDetailByProductId("", sellerId, keyword);
		
		Map<String, List<Record>> map = ImmutableMap.of("productList", productList, "compositionList", compositionList);
		renderJson(map);
	}

	public void shoppingCart() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);

		List<Record> productList = SellerProductQuery.me().findProductListForApp(sellerId, "");

		Map<String, Object> sellerProductInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> sellerProductItems = new ArrayList<>();

		for (Record record : productList) {
			Map<String, Object> item = new HashMap<>();

			String sellProductId = record.get("sell_product_id");
			item.put("title", record.getStr("custom_name") + "/" + record.getStr("valueName"));
			item.put("value", sellProductId);

			sellerProductItems.add(item);
			sellerProductInfoMap.put(sellProductId, record);
		}

		setAttr("sellerProductInfoMap", JSON.toJSON(sellerProductInfoMap));
		setAttr("sellerProductItems", JSON.toJSON(sellerProductItems));

		Boolean isEdit = OptionQuery.me().findValueAsBool(Consts.OPTION_WEB_ORDER_PRICE_EDIT + sellerCode);
		isEdit = (isEdit != null && isEdit) ? true : false;

		setAttr("isEdit", isEdit);
		render("shopping_cart.html");
	}

	public void order() {
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("order.html");
	}

	public void customerChoose() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Record> userList = UserQuery.me().findNextLevelsUserList(selectDataArea);
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("customer_choose.html");
	}

	public void customerList() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		String keyword = getPara("keyword");
		String userId = getPara("userId");
		String customerTypeId = getPara("customerTypeId");
		String isOrdered = getPara("isOrdered");
		
		String customerKind = Consts.CUSTOMER_KIND_COMMON;
		Subject subject = SecurityUtils.getSubject();
		if(subject.isPermitted("/admin/salesOrder/seller")) {
			customerKind = Consts.CUSTOMER_KIND_SELLER;
		}

		Page<Record> customerList = SellerCustomerQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword,
				selectDataArea, userId, customerTypeId, isOrdered, customerKind);

		Map<String, Object> map = new HashMap<>();
		map.put("customerList", customerList.getList());
		renderJson(map);
	}

	public void customerTypeById() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String customerId = getPara("customerId");

		List<Record> customerTypeList = SalesOrderQuery.me().findCustomerTypeListByCustomerId(customerId,
				DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));

		renderJson(customerTypeList);
	}
	
	@Before(WechatJSSDKInterceptor.class)
	public void customerNearbyChose() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Double dist = 100d;
		String lon = getPara("lon");
		String lat = getPara("lat");
		//String lngAndLat = getPara("lngAndLat");
		if(StrKit.isBlank(lon) || StrKit.isBlank(lat)) {
			List<Dict> areaCoverageList = DictQuery.me().findDictByType("area_coverage");
			List<Map<String, Object>> areaCoverage = new ArrayList<>();
			for(Dict dict : areaCoverageList) {
				Map<String, Object> item = new HashMap<>();
				item.put("title", dict.get("name"));
				item.put("value", dict.get("value"));
				areaCoverage.add(item);
			}
			setAttr("areaCoverage", JSON.toJSONString(areaCoverage));
			render("customer_nearby_choose.html");
		} else {
			if(getPara("nearby")!=null)
				dist = Double.valueOf(getPara("nearby", "100")).doubleValue();
			
			
			//String[] address = lngAndLat.split(",");
			BigDecimal latitude = new BigDecimal(lat);
			BigDecimal longitude = new BigDecimal(lon);

			List<Map<String, Object>> customerList = SellerCustomerQuery.me().queryCustomerNearby(dist, longitude, latitude, user.getId());
			Map<String, Object> map = new HashMap<>();
			
			map.put("customerList", customerList);
			renderJson(map);
		}
	}
}
