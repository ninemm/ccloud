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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.SalesFactQuery;
import org.ccloud.route.RouterMapping;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/")
public class IndexController extends BaseFrontController {

	public void index() {
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		setAttr("totalOrderCount", SalesFactQuery.me().findOrderCount());
		setAttr("totalOrderAmount", SalesFactQuery.me().findTotalAmount(provName, cityName, countryName));
		
		render("index.html");
	}
	
	public void orderAmount() {
		String provName = getPara("provName", "").trim();
		String cityName = getPara("cityName", "").trim();
		String countryName = getPara("countryName", "").trim();
		
		List<LinkedList<Map<String, Object>>> result = Lists.newLinkedList();
		List<Record> list = SalesFactQuery.me().findAreaList(provName, cityName, null, null, null);
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

}
