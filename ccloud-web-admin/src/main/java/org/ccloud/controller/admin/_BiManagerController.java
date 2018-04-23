/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.SecurityUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.BiUserJoinBrand;
import org.ccloud.model.BiUserJoinProduct;
import org.ccloud.model.BiUserJoinSeller;
import org.ccloud.model.User;
import org.ccloud.model.query.BiManagerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/bi_manager", viewPath = "/WEB-INF/admin/bi_manager")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _BiManagerController extends JBaseController {

	public void index() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword))
			setAttr("k", keyword);

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		String userId = null;
		if (!isSuperAdmin) {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			userId = user.getId();
		}

		Page<User> page = UserQuery.me().paginateUser(getPageNumber(), getPageSize(), keyword, dataArea, "u.create_date",
				userId);
		if (page != null) {
			setAttr("page", page);
		}
	}

	public void getUserSeller() {
		String id = getPara("userId");
		List<Record> sellersByUser = BiManagerQuery.me().findSellerByUser(id);
		List<String> userSellerList = new ArrayList<String>();
		for (Record record : sellersByUser) {
			userSellerList.add(record.getStr("dealer_data_area"));
		}

		List<Record> sellers = null;
		if(SecurityUtils.getSubject().isPermitted("/admin/all")){
			sellers = BiManagerQuery.me().findAllSeller();
		} else {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			sellers = BiManagerQuery.me().findSellerByUser(user.getId());
		}

		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : sellers) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("dealer_data_area"));
			map.put("name", record.getStr("seller_name"));
			if (userSellerList.contains(record.getStr("dealer_data_area"))) {
				checkList.add(map);
			} else {
				uncheckList.add(map);
			}
		}
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("checkList", checkList);
		data.put("uncheckList", uncheckList);
		renderJson(data);
	}

	@Before(Tx.class)
	public void saveUserSeller() {
		String id = getPara("userId");
		String[] ids = getParaValues("array[]");

		BiManagerQuery.me().delUSellerByUserId(id);
		if(ids != null) {
			for (String s : ids) {
				BiUserJoinSeller biUserJoinSeller = new BiUserJoinSeller();
				biUserJoinSeller.setUserId(id);
				biUserJoinSeller.setDealerDataArea(s);
				biUserJoinSeller.save();
			}
		}

		renderAjaxResultForSuccess("保存成功");
	}

	public void getUserBrand() {
		String id = getPara("userId");
		List<Record> brandsByUser = BiManagerQuery.me().findBrandByUser(id);
		List<String> userBrandList = new ArrayList<String>();
		for (Record record : brandsByUser) {
			userBrandList.add(record.getStr("brand_id"));
		}

		List<Record> brands = null;
		if(SecurityUtils.getSubject().isPermitted("/admin/all")){
			brands = BiManagerQuery.me().findAllBrand();
		} else {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			brands = BiManagerQuery.me().findBrandByUser(user.getId());
		}

		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : brands) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("brand_id"));
			map.put("name", record.getStr("name"));
			if (userBrandList.contains(record.getStr("brand_id"))) {
				checkList.add(map);
			} else {
				uncheckList.add(map);
			}
		}
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("checkList", checkList);
		data.put("uncheckList", uncheckList);
		renderJson(data);
	}

	@Before(Tx.class)
	public void saveUserBrand() {
		String id = getPara("userId");
		String[] ids = getParaValues("array[]");

		BiManagerQuery.me().delUBrandBByUserId(id);
		if(ids != null) {
			for (String s : ids) {
				BiUserJoinBrand biUserJoinBrand = new BiUserJoinBrand();
				biUserJoinBrand.setUserId(id);
				biUserJoinBrand.setBrandId(s);
				biUserJoinBrand.save();
			}
		}

		renderAjaxResultForSuccess("保存成功");
	}

	public void getUserProduct() {
		String id = getPara("userId");
		List<Record> productsByUser = BiManagerQuery.me().findProductByUser(id);
		List<String> userProductList = new ArrayList<String>();
		for (Record record : productsByUser) {
			userProductList.add(record.getStr("product_id"));
		}

		List<Record> products = null;
		if(SecurityUtils.getSubject().isPermitted("/admin/all")){
			products = BiManagerQuery.me().findAllProduct();
		} else {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			products = BiManagerQuery.me().findProductByUser(user.getId());
		}

		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : products) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("product_id"));
			map.put("name", record.getStr("name"));
			if (userProductList.contains(record.getStr("product_id"))) {
				checkList.add(map);
			} else {
				uncheckList.add(map);
			}
		}
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("checkList", checkList);
		data.put("uncheckList", uncheckList);
		renderJson(data);
	}

	@Before(Tx.class)
	public void saveUserProduct() {
		String id = getPara("userId");
		String[] ids = getParaValues("array[]");

		BiManagerQuery.me().delUProductByUserId(id);
		if(ids != null) {
			for (String s : ids) {
				BiUserJoinProduct biUserJoinProduct = new BiUserJoinProduct();
				biUserJoinProduct.setUserId(id);
				biUserJoinProduct.setProductId(s);
				biUserJoinProduct.save();
			}
		}

		renderAjaxResultForSuccess("保存成功");
	}

}
