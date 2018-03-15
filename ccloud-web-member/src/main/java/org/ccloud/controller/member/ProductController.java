/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.controller.member;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.Member;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.wechat.WechatJSSDKInterceptor;

import java.io.Serializable;
import java.util.*;

@RouterMapping(url = "/member/product")
public class ProductController extends BaseFrontController {

	public void index() {
		render("member_product.html");
	}

	public void productList() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		String keyword = getPara("keyword");
		String tag = getPara("tag");

		List<Record> productList = MemberJoinSellerQuery.me().findProductListForApp(member.getId(), keyword, tag);

		Set<String> tagSet = new LinkedHashSet<String>();

		for (Record record : productList) {
			String tags = record.getStr("tags");
			if (tags != null) {
				String[] tagArray = tags.split(",", -1);
				for (String str : tagArray) {
					tagSet.add(str);
				}
			}
		}

		Map<String, Collection<? extends Serializable>> map = ImmutableMap.of("productList", productList, "tags", tagSet);
		renderJson(map);
	}

	public void shoppingCart() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		List<Record> productList = MemberJoinSellerQuery.me().findProductListForApp(member.getId(), "", "");

		Map<String, Object> sellerProductInfoMap = new HashMap<String, Object>();
		List<Map<String, Object>> sellerProductItems = new ArrayList<>();

		for (Record record : productList) {
			Map<String, Object> item = new HashMap<>();

			String sellProductId = record.get("sell_product_id");
			item.put("title", record.getStr("custom_name"));
			item.put("value", sellProductId);

			sellerProductItems.add(item);
			sellerProductInfoMap.put(sellProductId, record);
		}

		setAttr("sellerProductInfoMap", JSON.toJSON(sellerProductInfoMap));
		setAttr("sellerProductItems", JSON.toJSON(sellerProductItems));

		render("member_shopping_cart.html");
	}

	@Before(WechatJSSDKInterceptor.class)
	public void order() {
		Member member = getSessionAttr(Consts.SESSION_LOGINED_MEMBER);

		setAttr("customerInfo", JSON.toJSONString(CustomerQuery.me().findById(member.getCustomerId())));
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

		render("member_order.html");
	}
}
