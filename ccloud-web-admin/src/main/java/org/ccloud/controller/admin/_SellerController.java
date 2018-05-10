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

import org.apache.shiro.SecurityUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
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
import org.ccloud.model.PrintTemplate;
import org.ccloud.model.Product;
import org.ccloud.model.Role;
import org.ccloud.model.RoleOperationRel;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerBrand;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.SellerJoinTemplate;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GoodsCategoryQuery;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.PrintTemplateQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.RoleOperationRelQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.SellerBrandQuery;
import org.ccloud.model.query.SellerJoinTemplateQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
		String sellerId = getSessionAttr("sellerId");
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

        Page<Seller> page = SellerQuery.me().paginate(getPageNumber(), getPageSize(),keyword,  "cs.id",user.getUsername(),childs,sellerId);
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
		/*if(user.getUsername().equals("admin")){
			if(department.getDeptLevel()<2){
				renderAjaxResultForError("部门选择错误，请重新选择！");
				return;
			}
		}else{
			if(department.getDeptLevel()<3){
				renderAjaxResultForError("部门选择错误，请重新选择！");
				return;
			}
		}*/
		final Seller seller = getModel(Seller.class);
		SellerBrand sellerBrand = new SellerBrand();
		Customer customer = new Customer();
		String address = getPara("address");
		String brandList =getPara("brandList");
		String productType = getPara("productTypeList");
		String sellerId = seller.getId();
		String areaCodes = getPara("areaCodes");
		String areaNames = getPara("areaNames");
		String sellerType = getPara("seller_type");
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
			Seller sr = SellerQuery.me().findbyCode(seller.getSellerCode());
			if(sr!=null){
				renderAjaxResultForError("销售商编码不可重复，请重新输入！");
				return;
			}
			Seller seller1=SellerQuery.me().findByDeptId(department.getId());
			if(seller1!=null){
					renderAjaxResultForError("该公司部门已有一个经销商或者直营商，请确认");
					return;
			}
			seller.setSellerType(Integer.parseInt(sellerType));
			this.saveSeller(seller, department, user,productType);

			this.saveOption(seller.getSellerCode());
			
			for(int i=0;i<brandIds.length;i++){
				if(!brandIds[i].equals("")){
					this.saveSellerBrand(brandIds[i], department, seller.getId());
				}
			}
			
			//找到最近的经销商
			String sId = "";
			String dataArea = "";
			String deptId = "";
			List<Department> depts = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(department.getId());
			if(depts.size()>0){
				sId = depts.get(0).getStr("seller_id");
				dataArea = depts.get(0).getStr("data_area");
				deptId = depts.get(0).getStr("id");
				//添加直营商客户时 初始化数据
				if(!user.getUsername().equals("admin") || sellerType.equals(Consts.SELLER_TYPE_SELLER)){
					String customerId = StrKit.getRandomUUID();
					//添加客户
					customer.setId(customerId);
					customer.setCustomerCode(seller.getSellerCode());
					customer.setCustomerName(seller.getSellerName());
					customer.setContact(seller.getContact());
					customer.setMobile(seller.getPhone());
					customer.setIsEnabled(1);
					customer.setAddress(address);
					customer.setCreateDate(new Date());
					customer.setStatus("0");
					customer.save();
					//添加直营商客户
					this.saveSellerCustomer(customer.getId(), user, department,sId,dataArea,deptId);
					seller.setCustomerId(customer.getId());
					}
			}else{
				deptId = user.getDepartmentId();
			}
			//保存通用打印模板,通用模板的ID：0
			this.saveSellerJoinTemplate(seller.getId());
			
			if(sellerType.equals(Consts.SELLER_TYPE_DEALER)) {
				//新建销售商时默认创建分组  角色  及中间表 客户类型
				this.saveOther(department,deptId);
			}
			
			
			seller.save();
		} else {
			Seller s = SellerQuery.me().findById(seller.getId());
			if(!s.getDeptId().equals(department.getId())){
				Seller seller1=SellerQuery.me().findByDeptId(department.getId());
				if(seller1!=null){
						renderAjaxResultForError("该公司部门已有一个经销商或者直营商，请确认");
						return;
				}	
			}
			this.updateSeller(seller, department, user,productType);
			SellerBrandQuery.me().deleteBySellertId(sellerId);
			for(int i=0;i<brandIds.length;i++){
				String sellerBrandId = StrKit.getRandomUUID();
				if(!brandIds[i].equals("")){
					sellerBrand.setId(sellerBrandId);
					sellerBrand.setBrandId(brandIds[i]);
					sellerBrand.setSellerId(sellerId);
						sellerBrand.setDataArea(department.getDataArea());
						sellerBrand.setDeptId(department.getId());
					}
					sellerBrand.save();
				}
			
		}
		renderAjaxResultForSuccess();
	}
	
	public void getBrand() {
		String id = getPara("id");
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Brand> brands = new ArrayList<Brand>();
		if(user.getUsername().equals("admin")) {
			brands = BrandQuery.me().findAll();
		}else {
			brands = BrandQuery.me().findBySellerId(sellerId);
		}
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
//		String id = getPara("id");
//		setAttr("sellerId", id);
		User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Record> records = SellerProductQuery.me().findByUserId(user.getId());
		setAttr("records",records);
		render("show_product.html");
	}	
	
	
	//展示销售商产品信息
		public void showProduct(){
			User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
			String keyword = getPara("k");
			String sta = getPara("sta");
	        if (StrKit.notBlank(keyword)) {
	            keyword = StringUtils.urlDecode(keyword);
	            setAttr("k", keyword);
	        }
	        String sellerId = getPara("sellerId");
	        String categoryId = getPara("categoryId");
	        String sId = getSessionAttr(Consts.SESSION_SELLER_ID);
	        List<SellerProduct> sellerProducts = SellerProductQuery.me().findBySellerId(sellerId);
	        String sellerProductIds = "";
	        if(sta.equals("1") && sellerProducts.size()>0) {
	        	String sPIds = "";
	        	for(SellerProduct sellerProduct : sellerProducts) {
	        		SellerProduct sellerP = SellerProductQuery.me().findbyCustomerNameAndSellerIdAndProductId(sellerProduct.getCustomName(), sId);
	        		if(sellerP !=null) {
	        			sPIds += "'"+sellerP.getId()+"',";
	        		}
	        	}
	        	if(!sPIds.equals("")) {
	        		sellerProductIds = sPIds.substring(0, sPIds.length()-1);
	        	}
	        }
	        Page<SellerProduct> page = SellerProductQuery.me().paginate_sel(getPageNumber(), getPageSize(),keyword,user.getId(),sta,sellerProductIds,categoryId);
	        Map<String, Object> map = new HashMap<String, Object>();
	        if(sta.equals("1")) {
	    	   map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList(),"sellerid", sellerId);
	       }else {
	    	   map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
	       }
	        renderJson(map);
		}
	
	public void change(){
		String id = getPara("sellerProductId");
		boolean flang=false;
		SellerProduct sellerProducts = SellerProductQuery.me().findById(id);
		if(sellerProducts.getIsEnable() == 1){
			sellerProducts.setIsEnable(0);
		}else{
			sellerProducts.setIsEnable(1);
		}
		
		
		if(sellerProducts!=null){
			sellerProducts.setModifyDate(new Date());
			flang=sellerProducts.update();
		}
		renderJson(flang);
	}
	
	public void addProduct(){
			String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
			List<Record> categories =  GoodsCategoryQuery.me().findBySellerId(sellerId, "");
			setAttr("categories",categories);
			render("add_product.html");
	}
	
	public void productList(){
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        String categoryId = getPara("categoryId");
        User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
        String sellerId =getSessionAttr(Consts.SESSION_SELLER_ID);
        Seller seller = SellerQuery.me().findById(sellerId);
        Page<Product> page = ProductQuery.me().paginate_pro(getPageNumber(), getPageSize(),keyword,  "cp.name",seller.getId(),user.getId(),categoryId);

        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	//保存产品信息
	@Before(Tx.class)
		public void savePro(){
			final SellerProduct sellerProducts= getModel(SellerProduct.class);
			String ds = getPara("orderItems");
			boolean result=false;
			int count = 0;
			int errCount = 0;
			JSONArray jsonArray = JSONArray.parseArray(ds);
			List<SellerProduct> imageList = jsonArray.toJavaList(SellerProduct.class);
			for (SellerProduct sellerProduct : imageList) {
				//前台传给后台的产品规格
				  String cpsName = sellerProduct.getQrcodeUrl();
				  SellerProduct issellerProducts = SellerProductQuery.me().findById(sellerProduct.getId());
					if(issellerProducts==null){
						List<SellerProduct> products = SellerProductQuery.me().checkSellerProduct(sellerProduct.getCustomName(), getSessionAttr(Consts.SESSION_SELLER_ID).toString(),cpsName);
						if(products.size()>0) {
							errCount++;
							continue;
						}else {
							String Id = StrKit.getRandomUUID();
							sellerProducts.setId(Id);
							sellerProducts.setProductId(sellerProduct.getProductId());
							sellerProducts.setSellerId(getSessionAttr(Consts.SESSION_SELLER_ID).toString());
							sellerProducts.setCustomName(sellerProduct.getCustomName());
							sellerProducts.setStoreCount(new BigDecimal(0));
							sellerProducts.setPrice(sellerProduct.getPrice());
							sellerProducts.setAccountPrice(sellerProduct.getPrice());
							sellerProducts.setCost(sellerProduct.getPrice());
							sellerProducts.setTaxPrice(sellerProduct.getPrice());
							Seller seller = SellerQuery.me().findById(getSessionAttr(Consts.SESSION_SELLER_ID).toString());
							if(seller.getSellerType()==Integer.parseInt(Consts.SELLER_TYPE_SELLER)) {
								sellerProducts.setIsSource(1);
							}else {
								sellerProducts.setIsSource(0);
							}
							sellerProducts.setMarketPrice(sellerProduct.getMarketPrice());
							sellerProducts.setIsEnable(sellerProduct.getIsEnable());
							sellerProducts.setOrderList(sellerProduct.getOrderList());
							sellerProducts.setCreateDate(new Date());
							//生成二维码
							
							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							String  fileName = sdf.format(date)+".png";
							String contents =getRequest().getScheme() + "://" + getRequest().getServerName()+"/admin/seller/fu"+"?id="+Id; 
							//部署之前上传
							//String contents = getRequest().getScheme() + "://" + getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
							String imagePath = getRequest().getSession().getServletContext().getRealPath(Consts.QRCODE_PATH);
							QRCodeUtils.genQRCode(contents, imagePath, fileName);
							sellerProducts.setQrcodeUrl(imagePath+"\\"+fileName);
							result=sellerProducts.save();
							count++;
							if(result == false){
								break;
							}
						}
					}else{
							if(issellerProducts.getQrcodeUrl()!=null){
								File file = new File(issellerProducts.getQrcodeUrl());
								file.delete();
							}
							issellerProducts.setCustomName(sellerProduct.getCustomName());
							issellerProducts.setOrderList(sellerProduct.getOrderList());
							issellerProducts.setTaxPrice(sellerProduct.getTaxPrice());
							issellerProducts.setPrice(sellerProduct.getPrice());
							issellerProducts.setCost(sellerProduct.getCost());
							issellerProducts.setAccountPrice(sellerProduct.getAccountPrice());
							issellerProducts.setTags(sellerProduct.getTags());
							issellerProducts.setBarCode(sellerProduct.getBarCode());
							issellerProducts.setModifyDate(new Date());
							
							//生成二维码
							
							Date date = new Date();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							String  fileName = sdf.format(date)+".png";
							String contents =getRequest().getScheme() + "://" + getRequest().getServerName()+"/admin/seller/fu"+"?id="+issellerProducts.getId(); 
							//部署之前上传
							//String contents = getRequest().getScheme() + "://" + getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
							String imagePath = getRequest().getSession().getServletContext().getRealPath(Consts.QRCODE_PATH);
							QRCodeUtils.genQRCode(contents, imagePath, fileName);
							issellerProducts.setQrcodeUrl(imagePath+"\\"+fileName);
							result=issellerProducts.update();
							if(result == false){
								break;
							}
					}
			}
			renderAjaxResultForSuccess("成功添加"+count+"条商品，有"+errCount+"条商品重复");
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
			seller.setIsEnabled(0);
		}else{
			seller.setIsEnabled(1);
		}
		seller.setModifyDate(new Date());
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
				issellerProducts.setIsEnable(1);
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
	
	@Before(Tx.class)
	public void saveSeller(Seller seller,Department department,User user,String productTypes){
		/*//销售商自动生成销售商编码
		List<Seller> list = SellerQuery.me().findAll();
		int s = list.size();
		s++;
		String j=Integer.toString(s);
		String w = "1";
		int countt =j.length();
		for(int m=0;m<(5-countt);m++){
			j= "0"+j;
		}
		w += j;*/
		String sellerId = StrKit.getRandomUUID();
		seller.setId(sellerId);
		seller.setProductTypeStore(productTypes);
		seller.setCreateDate(new Date());
		seller.setModifyUserId(user.getId());
		seller.setIsInited(0);
		seller.setDeptId(department.getId());
		seller.setDataArea(department.getDataArea());
	}
	
	@Before(Tx.class)
	public void saveOption(String sellerCode){
		Option option = new Option();
		option.setOptionKey(Consts.OPTION_SELLER_STORE_CHECK + sellerCode);
		option.setOptionValue("true");
		option.save();
		Option option1 = new Option();
		option1.setOptionKey(Consts.OPTION_WEB_PROCEDURE_REVIEW + sellerCode);
		option1.setOptionValue("true");
		option1.save();
		Option option2 = new Option();
		option2.setOptionKey(Consts.OPTION_WEB_PROC_CUSTOMER_REVIEW + sellerCode);
		option2.setOptionValue("true");
		option2.save();
		Option option3 = new Option();
		option3.setOptionKey(Consts.OPTION_WEB_PROC_CUSTOMER_VISIT + sellerCode);
		option3.setOptionValue("true");
		option3.save();
	}
	@Before(Tx.class)
	public void saveSellerBrand(String brandId,Department department,String sellerId){
		SellerBrand sellerBrand = new SellerBrand();
		String sellerBrandId = StrKit.getRandomUUID();
		sellerBrand.setId(sellerBrandId);
		sellerBrand.setBrandId(brandId);
		sellerBrand.setSellerId(sellerId);
		sellerBrand.setDataArea(department.getDataArea());
		sellerBrand.setDeptId(department.getId());
		sellerBrand.save();
	}
	
	public void updateSeller(Seller seller,Department department,User user,String productTypes){
		seller.setDeptId(department.getId());
		seller.setProductTypeStore(productTypes);
		seller.setModifyUserId(user.getId());
		seller.setIsInited(1);
		seller.setModifyDate(new Date());
		seller.update();
	}
	@Before(Tx.class)
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

	@Before(Tx.class)
	public void saveOther(Department department,String deptId){
		List<Group> groupList = GroupQuery.me()._findByDeptId(deptId);
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
		List<Role> roleList = RoleQuery.me()._findByDeptId(deptId);
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
	@Before(Tx.class)
	public void saveSellerCustomer(String customerId,User user,Department department,String sellerId,String dataArea,String deptId){
		String code = Consts.CUSTOMER_TYPE_CODE_SELLER;
		CustomerType customerType = CustomerTypeQuery.me().findDataAreaAndName(dataArea,code);
		
		SellerCustomer sellerCustomer = new SellerCustomer();
		String sellerCustomerId = StrKit.getRandomUUID();
		sellerCustomer.setId(sellerCustomerId);
		sellerCustomer.setSellerId(sellerId);
		sellerCustomer.setCustomerId(customerId);
		sellerCustomer.setNickname(getPara("seller.seller_name"));
		sellerCustomer.setIsChecked(1);
		sellerCustomer.setIsEnabled(1);
		sellerCustomer.setIsArchive(1);
		sellerCustomer.setCustomerTypeIds(customerType.getId());
		sellerCustomer.setCustomerKind(Consts.CUSTOMER_KIND_SELLER);
		sellerCustomer.setStatus("0");
		sellerCustomer.setDataArea(dataArea);
		sellerCustomer.setDeptId(deptId);
		sellerCustomer.setCreateDate(new Date());
		sellerCustomer.save();
		
		//添加用户客户中间表
		UserJoinCustomer userJoinCustomer = new UserJoinCustomer();
		userJoinCustomer.setSellerCustomerId(sellerCustomerId);
		userJoinCustomer.setUserId(user.getId());
		userJoinCustomer.setDataArea(dataArea);
		userJoinCustomer.setDeptId(deptId);
		userJoinCustomer.save();
		
		if(customerType!=null){
			CustomerJoinCustomerType customerJoinCustomerType = new CustomerJoinCustomerType();
			customerJoinCustomerType.setSellerCustomerId(sellerCustomerId);
			customerJoinCustomerType.setCustomerTypeId(customerType.getId());
			customerJoinCustomerType.save();
		}
	}
	@Before(Tx.class)
	public int saveProduct(SellerProduct sellerProduct,String sellerId){
		
		SellerProduct sPro = new SellerProduct();
		int m = 0;
		sPro = SellerProductQuery.me().findbyCustomerNameAndSellerIdAndProductId(sellerProduct.getCustomName(),sellerId);
		boolean flang = false;
		if(sPro == null) {
			sPro = new SellerProduct();
			String sellerProductId = StrKit.getRandomUUID();
			sPro.setId(sellerProductId);
			sPro.setProductId(sellerProduct.getProductId());
			sPro.setSellerId(sellerId);
			sPro.setCustomName(sellerProduct.getCustomName());
			sPro.setStoreCount(new BigDecimal(0));
			sPro.setPrice(sellerProduct.getPrice());
			sPro.setAccountPrice(sellerProduct.getPrice());
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
			flang = sPro.save();
			if(flang == true) {
				m=1;
			}else {
				m=0;
			}
		}else {
			m=2;
		}
		return m;
	}
	
	public void upIsHot() {
		String isHot = getPara("isHot");
		String sellerProductId = getPara("sellerProductId");
		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		if(isHot.equals("1")) {
			sellerProduct.setIisHot(0);
		}else {
			sellerProduct.setIisHot(1);
		}
		boolean success = false;
		success = sellerProduct.update();
		renderJson(success);
	}
	
	public void upIsGift() {
		String isGift = getPara("isGift");
		String sellerProductId = getPara("sellerProductId");
		SellerProduct sellerProduct = SellerProductQuery.me().findById(sellerProductId);
		if(isGift.equals("1")) {
			sellerProduct.setIsGift(0);
		}else {
			sellerProduct.setIsGift(1);
		}
		boolean success = false;
		success = sellerProduct.update();
		renderJson(success);
	}
	
	@Before(Tx.class)
	public void savePt() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sId = getPara("sellerId");
		boolean result = false;
		int m = 0;
		String status = getPara("status");
		if(status.equals("0")) {
			List<SellerProduct> sellerProducts = SellerProductQuery.me().findBySellerId(sellerId);
			for(SellerProduct sellerProduct : sellerProducts) {
				m = saveProduct(sellerProduct, sId);
				if(m==0) {
					renderAjaxResultForError("保存失败！");
					result = false;
					return;
				}else if(m==2) {
					result = true;
					continue;
				}else {
					result = true;
				}
			}
		}else {
			String ds = getPara("orderItems");
			
			JSONArray jsonArray = JSONArray.parseArray(ds);
			List<SellerProduct> imageList = jsonArray.toJavaList(SellerProduct.class);
			for(SellerProduct sp : imageList) {
				SellerProduct sellerProduct = SellerProductQuery.me().findById(sp.get("id").toString());
				m = saveProduct(sellerProduct, sId);
				if(m==0) {
					renderAjaxResultForError("保存失败！");
					result = false;
					return;
				}else if(m==2) {
					result = true;
					continue;
				}else {
					result = true;
				}
			}
		}
		renderJson(result);
	}
	
	public void saveSellerJoinTemplate(String sellerId) {
		//通用模板的ID：0
		SellerJoinTemplate sellerJoinTemplate = new SellerJoinTemplate();
		String id = "0";
		PrintTemplate printTemplate = PrintTemplateQuery.me().findById(id);
		sellerJoinTemplate = SellerJoinTemplateQuery.me().findByTemplateId(id, sellerId);
		if(sellerJoinTemplate == null) {
			sellerJoinTemplate = new SellerJoinTemplate();
			sellerJoinTemplate.setId(StrKit.getRandomUUID());
			sellerJoinTemplate.setSellerId(sellerId);
			sellerJoinTemplate.setPrintTemplateId(id);
			sellerJoinTemplate.setName(printTemplate.getTemplateName());
			sellerJoinTemplate.save();                                                                                                                        
		}
	}
	
	//检查用户填写的编码是否已经存在了
	public void checkCode() {
		String code = getPara("code");
		Seller sr = SellerQuery.me().findbyCode(code);
		boolean result = false;
		if(sr!=null){
			result = true;
		}
		renderJson(result);
	}
	
	//前台检查产品名是否重复--通过产品名和规格进行查询
	public void checkCustomName() {
		String cpsName = getPara("cpsName");
		String customName = getPara("customName");
		List<SellerProduct> products = SellerProductQuery.me().checkSellerProduct(customName, getSessionAttr(Consts.SESSION_SELLER_ID).toString(),cpsName);
		renderJson(products.size());
	}
	
	public void department_tree() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		List<Map<String, Object>> list = DepartmentQuery.me().findDeptListAsTree(1, dataArea, isSuperAdmin);
		setAttr("treeData", JSON.toJSON(list));
	}
}

