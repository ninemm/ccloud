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

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.QRCodeUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Brand;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Department;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Group;
import org.ccloud.model.GroupRoleRel;
import org.ccloud.model.Option;
import org.ccloud.model.Product;
import org.ccloud.model.Role;
import org.ccloud.model.RoleOperationRel;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerBrand;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.RoleOperationRelQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.SellerBrandQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
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
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Department> departments = DepartmentQuery.me().findDeptList(dataArea, "id");
		String childId = "";
		for (int i = 0;i<departments.size();i++){
			childId += "'"+departments.get(i).getId()+"',";
		}
		String childs = childId.substring(0, childId.length()-1);
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<Seller> page = SellerQuery.me().paginate(getPageNumber(), getPageSize(),keyword,  "cs.id",user.getUsername(),childs);
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
	
	//保存销售商信息及对应的表
	/* (non-Javadoc)
	 * @see org.ccloud.core.JBaseCRUDController#save()
	 */
	@Before(Tx.class)
	public void save() {
		Department department=DepartmentQuery.me().findById(getPara("dept_id"));
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		if(user.getUsername().equals("admin")){
			if(department.getDeptLevel()<2){
				renderAjaxResultForError("部门选择错误，请重新选择！");
				return;
			}
		}else{
			if(department.getDeptLevel()<3){
				renderAjaxResultForError("部门选择错误，请重新选择！");
				return;
			}
		}
		final Seller seller = getModel(Seller.class);
		final SellerBrand sellerBrand = getModel(SellerBrand.class);
		final Customer customer = getModel(Customer.class);
		String address = getPara("address");
		String brandList =getPara("brandList");
		String productType = getPara("productTypeList");
		String sellerId = seller.getId();
		String areaCodes = getPara("areaCodes");
		String areaNames = getPara("areaNames");
		String[] areaCodeArray = areaCodes.split("/");
		String[] areaNameArray = areaNames.split("/");

		seller.setProvName(areaNameArray[0]);
		seller.setProvCode(areaCodeArray[0]);
		seller.setCityName(areaNameArray[1]);
		seller.setCityCode(areaCodeArray[1]);
		if(areaNameArray.length>2){
			seller.setCountryName(areaNameArray[2]);
			seller.setCountryCode(areaCodeArray[2]);
			customer.setCountryName(areaNameArray[2]);
			customer.setCountryCode(areaCodeArray[2]);
		}else{
			seller.setCountryName("");
			seller.setCountryCode("");
			customer.setCountryName("");
			customer.setCountryCode("");
		}
		customer.setProvName(areaNameArray[0]);
		customer.setProvCode(areaCodeArray[0]);
		customer.setCityName(areaNameArray[1]);
		customer.setCityCode(areaCodeArray[1]);
		
		String [] brandIds= brandList.split(",");
		if (StrKit.isBlank(sellerId)) {
			Seller seller1=SellerQuery.me().findByDeptAndSellerType(department.getId());
			if(seller1!=null){
				renderAjaxResultForError("该公司部门已有一个经销商，请确认");
				return;
			}
			
			Seller seller2 = this.saveSeller(seller, department, user,productType);

			this.saveOption(seller2.getId());
			
			for(int i=0;i<brandIds.length;i++){
				if(!brandIds[i].equals("")){
					this.saveSellerBrand(brandIds[i], department, seller2.getId());
				}
			}
			
			
			//添加直营商客户时 初始化数据
			if(department.getDeptLevel()>2){
				String customerId = StrKit.getRandomUUID();
				//添加客户
				customer.set("id", customerId);
				customer.set("customer_code", getPara("seller.seller_code"));
				customer.set("customer_name", getPara("seller.seller_name"));
				customer.set("contact", getPara("seller.contact"));
				customer.set("mobile", getPara("seller.phone"));
				customer.set("is_enabled", 1);
				customer.set("address", address);
				customer.set("create_date", new Date());
				customer.set("status", 0);
				customer.save();
				
				//添加直营商客户
				this.saveSellerCustomer(customer.getId(), user, department);
				//初始化直营商产品
				List<SellerProduct> sellerProducts = new ArrayList<SellerProduct>();
				if(user.getUsername().equals("admin")){
					Department department2 = DepartmentQuery.me().findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(department.getDataArea()));
					Seller sl = SellerQuery.me().findByDeptId(department2.getId());
					sellerProducts = SellerProductQuery.me().findBySellerIdAndIsEnable(sl.getId());
				}else{
					sellerProducts = SellerProductQuery.me().findBySellerId(getSessionAttr("sellerId").toString());
				}
				for(SellerProduct sellerProduct : sellerProducts){
					this.saveProduct(sellerProduct, seller2.getId());
				}
			}
			if(department.getDeptLevel()==2){
				seller2.set("seller_type", 0);
				seller2.update();
			}else{
				seller2.set("seller_type", 1);
				seller2.setCustomerId(customer.getId());
				seller2.update();
			}
			//新建销售商时默认创建分组  角色  及中间表 客户类型
			if(department.getDeptLevel()==2){
				this.saveOther(department);
			}
		} else {
			this.updateSeller(seller, department, user,productType);
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
	
	//产品类型
	public void getGoodsType(){
		String id = getPara("id");
		List<GoodsType> goodsTypes = GoodsTypeQuery.me().findAll();
		List<Map<String, Object>> list = new ArrayList<>();
		for (GoodsType goodsType : goodsTypes) {
			if (goodsType.getId().equals("")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("id", goodsType.getId());
			map.put("name",goodsType.getName());
			if (!StringUtils.isBlank(id)) {
				String gTs = SellerQuery.me().findById(id).getProductTypeStore();
				if(gTs!= null){
					String[] goodsTypeIds = gTs.split(",");
					for (int i = 0, len = goodsTypeIds.length; i < len; i++) {
						if(goodsType.getId().equals(goodsTypeIds[i])){
							map.put("isvalid", 1);
							break;
						} else {
							map.put("isvalid", 0);
						}
					}
				}else{
					map.put("isvalid", 0);
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
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
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
		String id = getPara("sellerProductId");
		boolean flang=false;
		SellerProduct sellerProducts = SellerProductQuery.me().findById(id);
		if(sellerProducts.getIsEnable() == 1){
			sellerProducts.set("is_enable",0);
		}else{
			sellerProducts.set("is_enable",1);
		}
		
		
		if(sellerProducts!=null){
			sellerProducts.set("modify_date", new Date());
			flang=sellerProducts.update();
		}
		renderJson(flang);
	}
	
	public void addProduct(){
			render("add_product.html");
	}
	
	public void productList(){
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
        Seller seller = SellerQuery.me().findById(getSessionAttr("sellerId").toString());
        Page<Product> page = ProductQuery.me().paginate_pro(getPageNumber(), getPageSize(),keyword,  "cp.name",seller.getId(),user.getId());

        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	//保存产品信息
	@Before(Tx.class)
		public void savePro(){
			final SellerProduct sellerProducts= getModel(SellerProduct.class);
			String ds = getPara("orderItems");
			boolean result=false;
			Seller seller=SellerQuery.me().findById(getSessionAttr("sellerId").toString());
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
						sellerProducts.set("store_count",new BigDecimal(0));
						sellerProducts.set("price", sellerProduct.getPrice());
						sellerProducts.setCost(sellerProduct.getCost());
						sellerProducts.setIsSource(1);
						sellerProducts.setMarketPrice(sellerProduct.getMarketPrice());
						sellerProducts.set("is_enable", sellerProduct.getIsEnable());
						sellerProducts.set("order_list", sellerProduct.getOrderList());
						sellerProducts.set("create_date", new Date());
						//生成二维码
						
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						String  fileName = sdf.format(date)+".png";
						String contents =getRequest().getScheme() + "://" + getRequest().getServerName()+"/admin/seller/fu"+"?id="+Id; 
						//部署之前上传
						//String contents = getRequest().getScheme() + "://" + getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
						String imagePath = getRequest().getSession().getServletContext().getRealPath(Consts.QRCODE_PATH);
						QRCodeUtils.genQRCode(contents, imagePath, fileName);
						sellerProducts.set("qrcode_url", imagePath+"\\"+fileName);
						result=sellerProducts.save();
						if(result == false){
							break;
						}
					}else{
						if(issellerProducts.getQrcodeUrl()!=null){
							File file = new File(issellerProducts.getQrcodeUrl());
							file.delete();
						}
						issellerProducts.set("custom_name",sellerProduct.getCustomName());
						issellerProducts.set("order_list", sellerProduct.getOrderList());
						issellerProducts.set("price", sellerProduct.getPrice());
						issellerProducts.set("bar_code", sellerProduct.getBarCode());
						issellerProducts.set("modify_date", new Date());
						
						//生成二维码
						
						Date date = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						String  fileName = sdf.format(date)+".png";
						String contents =getRequest().getScheme() + "://" + getRequest().getServerName()+"/admin/seller/fu"+"?id="+issellerProducts.getId(); 
						//部署之前上传
						//String contents = getRequest().getScheme() + "://" + getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
						String imagePath = getRequest().getSession().getServletContext().getRealPath("\\qrcode\\");
						QRCodeUtils.genQRCode(contents, imagePath, fileName);
						issellerProducts.set("qrcode_url", imagePath+"\\"+fileName);
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
	
	public void show_sellerName(){
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Seller> list = SellerQuery.me().findAllByUserId(user.getId());
		renderJson(list);
	}
	
	public void deleteProduct(){
		String id = getPara("id");
		final SellerProduct s = SellerProductQuery.me().findById(id);
			if (s != null) {
				if (s.delete()) {
					renderAjaxResultForSuccess("删除成功");
					return;
				}
			renderAjaxResultForError("删除失败");
		}
	}
	
	public void upIsenable(){
		String ds = getPara("orderItems");
		boolean flang = false;
		JSONArray jsonArray = JSONArray.parseArray(ds);
		List<SellerProduct> imageList = jsonArray.toJavaList(SellerProduct.class);
		for (SellerProduct sellerProduct : imageList) {
			  SellerProduct issellerProducts = SellerProductQuery.me().findById(sellerProduct.getId());
				issellerProducts.set("is_enable", 1);
				flang=issellerProducts.update();
		}
		renderJson(flang);
	}
	
	public void downIsenable(){
		String ds = getPara("orderItems");
		boolean flang = false;
		JSONArray jsonArray = JSONArray.parseArray(ds);
		List<SellerProduct> imageList = jsonArray.toJavaList(SellerProduct.class);
		for (SellerProduct sellerProduct : imageList) {
			  SellerProduct issellerProducts = SellerProductQuery.me().findById(sellerProduct.getId());
				issellerProducts.set("is_enable", 0);
				flang=issellerProducts.update();
		}
		renderJson(flang);
	}
	
	public void getQrcode(){
		String id = getPara("id");
		SellerProduct sellerProduct = SellerProductQuery.me().findById(id);
		renderJson(sellerProduct);
	}
	
	public void fu(){
		String id = getPara("id");
		SellerProduct sellerProduct = SellerProductQuery.me().findById(id);
		renderJson(sellerProduct);
	}
	
	public Seller saveSeller(Seller seller,Department department,User user,String productTypes){
		
		List<Seller> list = SellerQuery.me().findAll();
		int s = list.size();
		s++;
		String j=Integer.toString(s);
		String w = "1";
		int countt =j.length();
		for(int m=0;m<(5-countt);m++){
			j= "0"+j;
		}
		w += j;
		String sellerId = StrKit.getRandomUUID();
		seller.set("id", sellerId);
		seller.set("seller_name",seller.getSellerName());
		seller.set("seller_code",w);
		seller.set("contact", seller.getContact());
		seller.set("phone", seller.getPhone());
		seller.set("is_enabled", seller.getIsEnabled());
		seller.set("market_name", seller.getMarketName());
		seller.set("market_code", seller.getMarketCode());
		seller.set("jywx_open_id", seller.getJywxOpenId());
		seller.set("jpwx_open_id", seller.getJpwxOpenId());
		seller.set("product_type_store", productTypes);
		seller.set("remark", seller.getRemark());
		seller.set("create_date", new Date());
		seller.set("modify_user_id", user.getId());
		seller.set("is_inited", 0);
		seller.set("dept_id",department.getId());
		seller.save();
		return seller;
	}
	
	
	public void saveOption(String sellerId){
		Option option = new Option();
		option.setOptionKey("seller_store_check");
		option.setOptionValue("true");
		option.set("seller_id",sellerId );
		option.save();
		Option option01 = new Option();
		option01.setOptionKey("comment_need_procedure");
		option01.setOptionValue("true");
		option01.set("seller_id",sellerId );
		option01.save();
	}
	
	public void saveSellerBrand(String brandId,Department department,String sellerId){
		SellerBrand sellerBrand = new SellerBrand();
		String sellerBrandId = StrKit.getRandomUUID();
		sellerBrand.set("id",sellerBrandId);
		sellerBrand.set("brand_id", brandId);
		sellerBrand.set("seller_id",sellerId);
		sellerBrand.set("data_area",department.getDataArea());
		sellerBrand.set("dept_id", department.getId());
		sellerBrand.save();
	}
	
	public void updateSeller(Seller seller,Department department,User user,String productTypes){
		seller.set("dept_id",department.getId());
		seller.set("seller_name",seller.getSellerName());
		seller.set("contact", seller.getContact());
		seller.set("phone", seller.getPhone());
		seller.set("market_name", seller.getMarketName());
		seller.set("is_enabled", seller.getIsEnabled());
		seller.set("market_code", seller.getMarketCode());
		seller.set("jywx_open_id", seller.getJywxOpenId());
		seller.set("jpwx_open_id", seller.getJpwxOpenId());
		seller.set("product_type_store", productTypes);
		seller.set("remark", seller.getRemark());
		seller.set("modify_user_id", user.getId());
		seller.set("is_inited", 1);
		seller.set("modify_date", new Date());
		seller.set("modify_user_id", user.getId());
		seller.update();
	}
	
	public void saveCustomerType(CustomerType cT,Department department){
		CustomerType customerType = new CustomerType();
		customerType.setId(StrKit.getRandomUUID());
		customerType.setName(cT.getName());
		customerType.setCode(cT.getCode());
		customerType.setIsShow(cT.getIsShow());
		customerType.setType(cT.getType());
		customerType.setPriceSystemId(cT.getPriceSystemId());
		customerType.setProcDefKey(cT.getProcDefKey());
		customerType.set("dept_id",department.getId());
		customerType.set("data_area", department.getDataArea());
		customerType.setCreateDate(new Date());
		customerType.save();
	}
	
	public void saveOther(Department department){
		List<Group> groupList = GroupQuery.me().findByDeptId();
		if(groupList.size()>0){
			for (Group group : groupList) {
				Group newGroup=new Group();
				String groupId = StrKit.getRandomUUID();
				newGroup.setId(groupId);
				newGroup.setGroupName(group.getGroupName());
				newGroup.setGroupCode(group.getGroupCode());
				newGroup.setOrderList(group.getOrderList());
				newGroup.setDescription(group.getDescription());
				newGroup.set("dept_id", department.getId());
				newGroup.set("data_area", department.getDataArea());
				newGroup.setCreateDate(new Date());
				newGroup.save();
			}
		}
		List<Role> roleList = RoleQuery.me().findByDeptId();
		if(roleList.size()>0){
			for (Role role : roleList) {
				Role newRole = new Role();
				String roleId = StrKit.getRandomUUID();
				newRole.setId(roleId);
				newRole.setRoleName(role.getRoleName());
				newRole.setRoleCode(role.getRoleCode());
				newRole.setOrderList(role.getOrderList());
				newRole.setDescription(role.getDescription());
				newRole.set("dept_id", department.getId());
				newRole.set("data_area",department.getDataArea());
				newRole.setCreateDate(new Date());
				newRole.save();
				
				GroupRoleRel groupRoleRel = new GroupRoleRel();
				Group group = GroupQuery.me().findDeptIdAndDataAreaAndGroupCode(newRole.getDeptId(),newRole.getDataArea(),newRole.getRoleCode().substring(1));
				groupRoleRel.setId(StrKit.getRandomUUID());
				groupRoleRel.setGroupId(group.getId());
				groupRoleRel.setRoleId(roleId);
				groupRoleRel.save();
				
				
				List<RoleOperationRel> pRels = RoleOperationRelQuery.me().findByRoleId(role.getId());
				if(pRels.size()>0){
					for(RoleOperationRel pRel : pRels){
						RoleOperationRel operationRel = new RoleOperationRel();
						operationRel.setId(StrKit.getRandomUUID());
						operationRel.setOperationId(pRel.getOperationId());
						operationRel.setRoleId(roleId);
						operationRel.save();
						
					}
				}
			}
			
		}
		
		List<CustomerType> customerTypes = CustomerTypeQuery.me().findByDept("0");
		if(customerTypes.size()>0){
			for(CustomerType cT : customerTypes){
				this.saveCustomerType(cT, department);
			}
		}
	}
	
	public void saveSellerCustomer(String customerId,User user,Department department){
		SellerCustomer sellerCustomer = new SellerCustomer();
		String sellerCustomerId = StrKit.getRandomUUID();
		sellerCustomer.set("id", sellerCustomerId);
		if(user.getUsername().equals("admin")){
			Department department2 = DepartmentQuery.me().findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(department.getDataArea()));
			Seller sl = SellerQuery.me().findByDeptId(department2.getId());
			/*if(sl==null){
				renderAjaxResultForError("该公司部门"+department2.getDeptName()+"没有一个经销商，请先创建！");
				return;
			}*/
			sellerCustomer.set("seller_id",sl.getId());
		}else{
			sellerCustomer.set("seller_id",getSessionAttr("sellerId").toString());
		}
		sellerCustomer.set("customer_id", customerId);
		sellerCustomer.set("nickname", getPara("seller.seller_name"));
		sellerCustomer.set("is_checked", 1);
		sellerCustomer.set("is_enabled", 1);
		sellerCustomer.set("is_archive", 1);
		sellerCustomer.set("customer_type_ids", 7);
		sellerCustomer.set("customer_kind", 100402);
		sellerCustomer.set("status", 0);
		sellerCustomer.set("data_area", department.getDataArea());
		sellerCustomer.set("dept_id", department.getId());
		sellerCustomer.set("create_date", new Date());
		sellerCustomer.save();
		
		//添加用户客户中间表
		UserJoinCustomer userJoinCustomer = new UserJoinCustomer();
		userJoinCustomer.set("seller_customer_id", sellerCustomerId);
		userJoinCustomer.set("user_id", user.getId());
		userJoinCustomer.set("data_area",DataAreaUtil.getDealerDataAreaByCurUserDataArea(department.getDataArea()));
		userJoinCustomer.set("dept_id", department.getId());
		userJoinCustomer.save();
		
		String code = "G";
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		CustomerType customerType = CustomerTypeQuery.me().findDataAreaAndName(dataArea,code);
		if(customerType!=null){
			CustomerJoinCustomerType customerJoinCustomerType = new CustomerJoinCustomerType();
			customerJoinCustomerType.setSellerCustomerId(sellerCustomerId);
			customerJoinCustomerType.setCustomerTypeId(customerType.getId());
			customerJoinCustomerType.save();
		}
	}
	
	public void saveProduct(SellerProduct sellerProduct,String sellerId){
		SellerProduct sPro = new SellerProduct();
		String sellerProductId = StrKit.getRandomUUID();
		sPro.setId(sellerProductId);
		sPro.setProductId(sellerProduct.getProductId());
		sPro.setSellerId(sellerId);
		sPro.setCustomName(sellerProduct.getCustomName());
		sPro.setStoreCount(new BigDecimal(0));
		sPro.setPrice(sellerProduct.getPrice());
		sPro.setCost(sellerProduct.getCost());
		sPro.setMarketPrice(sellerProduct.getMarketPrice());
		sPro.setWeight(sellerProduct.getWeight());
		sPro.setWeightUnit(sellerProduct.getWeightUnit());
		sPro.setWarehouseId(sellerProduct.getWarehouseId());
		sPro.setIsSource(0);
		sPro.setIsEnable(sellerProduct.getIsEnable());
		sPro.setIsGift(sellerProduct.getIsGift());
		sPro.setFreezeStore(sellerProduct.getFreezeStore());
		sPro.setBarCode(sellerProduct.getBarCode());
		//生成二维码
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String  fileName = sdf.format(date)+".png";
		String contents =getRequest().getScheme() + "://" + getRequest().getServerName()+"/admin/seller/fu"+"?id="+sellerProductId; 
		//部署之前上传
		//String contents = getRequest().getScheme() + "://" + getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
		String imagePath = getRequest().getSession().getServletContext().getRealPath(Consts.QRCODE_PATH);
		QRCodeUtils.genQRCode(contents, imagePath, fileName);
		sPro.setQrcodeUrl(imagePath+"\\"+fileName);
		sPro.setOrderList(sellerProduct.getOrderList());
		sPro.setCreateDate(date);
		sPro.save();
	}
}

