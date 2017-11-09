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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Brand;
import org.ccloud.model.Department;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerBrand;
import org.ccloud.model.User;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SellerBrandQuery;
import org.ccloud.model.query.SellerQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/seller", viewPath = "/WEB-INF/admin/seller")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SellerController extends JBaseCRUDController<Seller> { 

	public void list() {
		
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<Seller> page = SellerQuery.me().paginate(getPageNumber(), getPageSize(),keyword,  "id");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
		
	}
	
	@Override
	@RequiresPermissions(value={"/admin/seller/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Seller seller = SellerQuery.me().findById(id);
	
			setAttr("seller", seller);
		}
		
	}
	
	//保存经销商信息
	public void save() {

		final Seller seller = getModel(Seller.class);
		final SellerBrand sellerBrand = getModel(SellerBrand.class);
		String brandList =getPara("brandList");
		String sellerId = seller.getId();
		seller.setProvCode(getPara("userProvinceId"));
		seller.setProvName(getPara("userProvinceText"));
		seller.setCityCode(getPara("userCityId"));
		seller.setCityName(getPara("userCityText"));
		seller.setCountryCode(getPara("userDistrictId"));
		seller.setCountryName(getPara("userDistrictText"));
		
		String [] brandIds= brandList.split(",");
		
		Department department=DepartmentQuery.me().findById(seller.getDeptId());
		User user=getSessionAttr("user");
		if (StrKit.isBlank(sellerId)) {
			sellerId = StrKit.getRandomUUID();
			seller.set("id", sellerId);
			seller.set("create_date", new Date());
			seller.set("modify_user_id", user.getId());
			seller.save();
			for(int i=0;i<brandIds.length;i++){
				if(!brandIds[i].equals("")){
					String sellerBrandId = StrKit.getRandomUUID();
					sellerBrand.set("id",sellerBrandId);
					sellerBrand.set("brand_id", brandIds[i]);
					sellerBrand.set("seller_id",sellerId);
					sellerBrand.set("data_area",department.getDataArea());
					sellerBrand.set("dept_id", seller.getDeptId());
					sellerBrand.save();
				}
			}
		} else {
			seller.set("modify_date", new Date());
			seller.set("modify_user_id", user.getId());
			seller.update();
			SellerBrandQuery.me().deleteBySellertId(sellerId);
			for(int i=0;i<brandIds.length;i++){
				String sellerBrandId = StrKit.getRandomUUID();
				if(!brandIds[i].equals("")){
					sellerBrand.set("id",sellerBrandId);
					sellerBrand.set("brand_id", brandIds[i]);
					sellerBrand.set("seller_id",sellerId);
					sellerBrand.set("data_area",department.getDataArea());
					sellerBrand.set("dept_id", seller.getDeptId());
					sellerBrand.save();
				}
			}
		}
		renderAjaxResultForSuccess();
	}
	
	public void getBrand() {
		String id = getPara("id");
		List<Brand> brands = BrandQuery.me().findAll();
		List<Map<String, Object>> list = new ArrayList<>();
		for (Brand brand : brands) {
			if (brand.getId().equals("")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			List<SellerBrand> sellerBrands = SellerBrandQuery.me().findBySellerId(id);
			map.put("id", brand.getId());
			map.put("name",brand.getName());
			if (!StringUtils.isBlank(id)) {
				for (int i = 0, len = sellerBrands.size(); i < len; i++) {
					if(brand.getId().equals(sellerBrands.get(i).getBrandId().toString())){
						map.put("isvalid", 1);
						break;
					} else {
						map.put("isvalid", 0);
					}
				}
			} else {
				map.put("isvalid", 0);
			}
			list.add(map);
		}
		renderJson(list);
	}
	
	//删除数据--删除销售商和销售商品牌链接（cc_seller_brand0）的数据
	public void delete(){
		String id = getPara("id");
		final Seller s = SellerQuery.me().findById(id);
		List<SellerBrand> list = SellerBrandQuery.me().findBySellerId(id);
		if (s != null) {
			if(list != null){
				SellerBrandQuery.me().deleteBySellertId(id);
			}
			boolean success = s.delete();
			if(success){
				renderAjaxResultForSuccess("删除成功");
			} else {
				renderAjaxResultForError("删除失败");
			}
		}
	}
}
