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

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Brand;
import org.ccloud.model.Customer;
import org.ccloud.model.Department;
import org.ccloud.model.Product;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerBrand;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.SellerBrandQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;

import com.alibaba.fastjson.JSONArray;
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
	
	@Override
	public void index() {
		render("index.html");
	}
	
	public void list() {
		User user=getSessionAttr("user");
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<Seller> page = SellerQuery.me().paginate(getPageNumber(), getPageSize(),keyword,  "id",user.getUsername());
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
		
	}
	
	@Override
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Seller seller = SellerQuery.me().findById(id);
			setAttr("seller", seller);
		}
		
	}
	
	//保存经销商信息
	/* (non-Javadoc)
	 * @see org.ccloud.core.JBaseCRUDController#save()
	 */
	public void save() {
		final Seller seller = getModel(Seller.class);
		final SellerBrand sellerBrand = getModel(SellerBrand.class);
		final Customer customer = getModel(Customer.class);
		final UserJoinCustomer userJoinCustomer = getModel(UserJoinCustomer.class);
		String brandList =getPara("brandList");
		String sellerId = seller.getId();
		seller.setProvCode(getPara("userProvinceId"));
		seller.setProvName(getPara("userProvinceText"));
		seller.setCityCode(getPara("userCityId"));
		seller.setCityName(getPara("userCityText"));
		seller.setCountryCode(getPara("userDistrictId"));
		seller.setCountryName(getPara("userDistrictText"));
		
		String [] brandIds= brandList.split(",");
		Department department=DepartmentQuery.me().findById(getPara("dept_id"));
		User user=getSessionAttr("user");
		if (StrKit.isBlank(sellerId)) {
			sellerId = StrKit.getRandomUUID();
			seller.set("id", sellerId);
			seller.set("seller_name",getPara("seller_name"));
			seller.set("seller_code",getPara("seller_code"));
			seller.set("contact", getPara("contact"));
			seller.set("phone", getPara("phone"));
			seller.set("is_enabled", getPara("is_enabled"));
			seller.set("market_name", getPara("market_name"));
			seller.set("market_code", getPara("market_code"));
			seller.set("jywx_open_id", getPara("jywx_open_id"));
			seller.set("jpwx_open_id", getPara("jpwx_open_id"));
			seller.set("product_type_store", getPara("product_type_store"));
			seller.set("remark", getPara("remark"));
			seller.set("create_date", new Date());
			seller.set("modify_user_id", user.getId());
			seller.set("is_inited", 1);
			if(user.getUsername().equals("admin")){
				seller.set("dept_id",getPara("dept_id"));
				seller.set("seller_type", getPara("seller_type"));
			}else{
				seller.set("dept_id",user.getDepartmentId());
				seller.set("seller_type", 1);
				//保存客户
				String customerId= StrKit.getRandomUUID();
				customer.set("id", customerId);
				customer.set("customer_code", "s");
				customer.set("customer_name", getPara("seller_name"));
				customer.set("contact", getPara("seller_name"));
				customer.set("mobile",getPara("phone"));
				customer.set("customer_kind", 2);
				customer.set("is_enabled",getPara("is_enabled"));
				customer.set("is_archive", 1);
				customer.save();
				
				//用户、客户、组织中间表
				userJoinCustomer.set("customer_id", customerId);
				userJoinCustomer.set("user_id", user.getId());
				userJoinCustomer.set("data_area", user.getDataArea());
				userJoinCustomer.set("dept_id", user.getDepartmentId());
				userJoinCustomer.save();
				
			}
			seller.save();
			
			
			
			for(int i=0;i<brandIds.length;i++){
				if(!brandIds[i].equals("")){
					String sellerBrandId = StrKit.getRandomUUID();
					sellerBrand.set("id",sellerBrandId);
					sellerBrand.set("brand_id", brandIds[i]);
					sellerBrand.set("seller_id",sellerId);
					if(user.getUsername().equals("admin")){
						sellerBrand.set("data_area",department.getDataArea());
						sellerBrand.set("dept_id", department.getId());
					}else{
						sellerBrand.set("data_area",user.getDataArea());
						sellerBrand.set("dept_id", user.getDepartmentId());
					}
					sellerBrand.save();
				}
			}
		} else {
			seller.set("seller_name",getPara("seller_name"));
			seller.set("seller_code",getPara("seller_code"));
			seller.set("contact", getPara("contact"));
			seller.set("phone", getPara("phone"));
			seller.set("market_name", getPara("market_name"));
			seller.set("is_enabled", getPara("is_enabled"));
			seller.set("market_code", getPara("market_code"));
			seller.set("jywx_open_id", getPara("jywx_open_id"));
			seller.set("jpwx_open_id", getPara("jpwx_open_id"));
			seller.set("product_type_store", getPara("product_type_store"));
			seller.set("remark", getPara("remark"));
			seller.set("modify_user_id", user.getId());
			seller.set("is_inited", 1);
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
					if(user.getUsername().equals("admin")){
						sellerBrand.set("data_area",department.getDataArea());
						sellerBrand.set("dept_id", department.getId());
					}else{
						sellerBrand.set("data_area",user.getDataArea());
						sellerBrand.set("dept_id", user.getDepartmentId());
					}
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
	
	public void productManage(){
		String id = getPara("id");
		setAttr("sellerId", id);
		render("show_product.html");
	}	
	
	
	//添加产品信息
	public void showProduct(){
		User user=getSessionAttr("user");
		String keyword = getPara("k");
	        if (StrKit.notBlank(keyword)) {
	            keyword = StringUtils.urlDecode(keyword);
	            setAttr("k", keyword);
	        }
	        
	        Page<SellerProduct> page = SellerProductQuery.me().paginate_sel(getPageNumber(), getPageSize(),keyword,user.getId());

	        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
	        renderJson(map);
	}
	
	public void change(){
		String id = getPara("id");
		int isEnable =Integer.parseInt(getPara("is_enable"));
		if(isEnable == 1){
			isEnable = 0;
		}else{
			isEnable = 1;
		}
		SellerProduct sellerProducts = SellerProductQuery.me().findById(id);
		sellerProducts.set("is_enable", isEnable);
		if(sellerProducts!=null){
			sellerProducts.set("modify_date", new Date());
			sellerProducts.update();
		}
		setAttr("sellerId", sellerProducts.getSellerId());
		render("show_product.html");
	}
	
	public void addProduct(){
			String sellerId = getPara("sellerId");
			setAttr("sellerId", sellerId);
			render("add_product.html");
	}
	
	public void productList(){
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        
        Page<Product> page = ProductQuery.me().paginate_pro(getPageNumber(), getPageSize(),keyword,  "cp.id");

        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	//保存产品信息
		public void savePro(){
			final SellerProduct sellerProducts= getModel(SellerProduct.class);
			User user=getSessionAttr("user");
			String ds = getPara("orderItems");
			boolean result=false;
			Seller seller=SellerQuery.me().findByUserId(user.getId());
			JSONArray jsonArray = JSONArray.parseArray(ds);
			List<SellerProduct> imageList = jsonArray.toJavaList(SellerProduct.class);
			for (SellerProduct sellerProduct : imageList) {
				  SellerProduct issellerProducts = SellerProductQuery.me().findById(sellerProduct.getId());
					if(issellerProducts==null){
						String Id = StrKit.getRandomUUID();
						sellerProducts.set("id",Id);
						sellerProducts.set("product_id",sellerProduct.getProductId());
						sellerProducts.set("seller_id",seller.getId());
						sellerProducts.set("custom_name",sellerProduct.getCustomName());
						sellerProducts.set("store_count",sellerProduct.getStoreCount());
						sellerProducts.set("price", sellerProduct.getPrice());
						sellerProducts.set("is_enable", sellerProduct.getIsEnable());
						sellerProducts.set("order_list", sellerProduct.getOrderList());
						sellerProducts.set("create_date", new Date());
						result=sellerProducts.save();
						if(result == false){
							break;
						}
					}else{
						issellerProducts.set("custom_name",sellerProduct.getCustomName());
						issellerProducts.set("price", sellerProduct.getPrice());
						issellerProducts.set("modify_date", new Date());
						result=issellerProducts.update();
						if(result == false){
							break;
						}
					}
			}
			renderJson(result);
		}		
			
	
	public void changeSeller() {
		String id = getPara("sellerId");
		String name = getPara("sellerName");
		setSessionAttr("sellerId", id);
		setSessionAttr("sellerName", name);
		renderAjaxResultForSuccess("切换成功");
	}
	//对销售商进行启用和停用的操作
	public void changeIsenabled(){
		String id = getPara("id");
		Seller seller = SellerQuery.me().findById(id);
		boolean flang = false;
		if(seller.getIsEnabled()==1){
			seller.set("is_enabled", 0);
		}else{
			seller.set("is_enabled", 1);
		}
		seller.set("modify_date", new Date());
		flang=seller.update();
		renderJson(flang);
	}
}

