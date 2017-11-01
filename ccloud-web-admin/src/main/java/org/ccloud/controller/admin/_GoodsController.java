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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.AttachmentUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Brand;
import org.ccloud.model.Goods;
import org.ccloud.model.GoodsAttribute;
import org.ccloud.model.GoodsCategory;
import org.ccloud.model.GoodsGoodsAttributeMapStore;
import org.ccloud.model.GoodsGoodsSpecification;
import org.ccloud.model.GoodsSpecification;
import org.ccloud.model.GoodsSpecificationValue;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Product;
import org.ccloud.model.ProductGoodsSpecificationValue;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.GoodsAttributeQuery;
import org.ccloud.model.query.GoodsCategoryQuery;
import org.ccloud.model.query.GoodsGoodsAttributeMapStoreQuery;
import org.ccloud.model.query.GoodsGoodsSpecificationQuery;
import org.ccloud.model.query.GoodsQuery;
import org.ccloud.model.query.GoodsSpecificationQuery;
import org.ccloud.model.query.GoodsSpecificationValueQuery;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.ProductGoodsSpecificationValueQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.model.vo.ProductInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/goods", viewPath = "/WEB-INF/admin/goods")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _GoodsController extends JBaseCRUDController<Goods> { 
	
	public void list() {
		
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<Goods> page = GoodsQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "create_date");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
		
	}
	
	@Override
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Goods goods = GoodsQuery.me().findById(id);
			getGoodsChild(goods);
			setAttr("goods", goods);
			
    		String imgList = goods.getProductImageListStore();
			JSONArray jsonArray = JSONArray.parseArray(imgList);
			List<ImageJson> imageList = jsonArray.toJavaList(ImageJson.class);		
			setAttr("imageList", imageList);
			
			List<GoodsCategory> clist = GoodsCategoryQuery.me().findCategoryByBrandId(goods.getBrandId());
			setAttr("clist", clist);
			
			List<Record> attributeList = GoodsGoodsAttributeMapStoreQuery.me().findByGoodsId(goods);
			setAttr("attributeList", attributeList);
		}
		List<Brand> blist = BrandQuery.me().findAll();
		setAttr("blist", blist);
		
		List<GoodsType> tlist = GoodsTypeQuery.me().findAll();
		setAttr("tlist", tlist);
		
		List<Product> pList = ProductQuery.me().findByGoodId(id);
		getProductSpValue(pList);
		setAttr("pList", pList);
		
		List<GoodsSpecification> goodsSpecificationList = GoodsSpecificationQuery.me().findAll();
		setAttr("goodsSpecificationList", goodsSpecificationList);
	}
	
	private void getProductSpValue(List<Product> pList) {
		for (Product ccProduct : pList) {
			List<ProductGoodsSpecificationValue> list = ProductGoodsSpecificationValueQuery.me().findByPId(ccProduct.getId());
			List<GoodsSpecificationValue> goodsSpecificationValueSet = new ArrayList<>();
			for (ProductGoodsSpecificationValue ccProductGoodsSpecificationValue : list) {
				GoodsSpecificationValue ccGoodsSpecificationValue = 
						GoodsSpecificationValueQuery.me().findById(ccProductGoodsSpecificationValue.getGoodsSpecificationValueSetId());
				goodsSpecificationValueSet.add(ccGoodsSpecificationValue);
			}
			ccProduct.setGoodsSpecificationValueSet(goodsSpecificationValueSet);
		}
	}
	
	private void getGoodsChild(Goods ccGoods) {
		List<GoodsSpecification> list = new ArrayList<>();
		List<GoodsGoodsSpecification> relationList = GoodsGoodsSpecificationQuery.me().findByGoodsId(ccGoods.getId());
		for (GoodsGoodsSpecification ccGoodsGoodsSpecification : relationList) {
			GoodsSpecification ccGoodsSpecification = GoodsSpecificationQuery.me().findById(ccGoodsGoodsSpecification.getGoodsSpecificationId());
			List<GoodsSpecificationValue> childList = GoodsSpecificationValueQuery.me().findByParentId(ccGoodsSpecification.getId());
			ccGoodsSpecification.setChildList(childList);
			list.add(ccGoodsSpecification);
		}
		ccGoods.setSpecificationList(list);
	}
	
	@Override
	public void save() {
		final Goods ccGoods = getModel(Goods.class);
		Map<String, String[]> map = getParaMap();
		String tinymce = StringUtils.getArrayFirst(map.get("tinymce"));
		boolean update = false;
		if (StringUtils.isNotBlank(tinymce)) {
			ccGoods.setContent(tinymce);
		}
		String [] imagePath = getParaValues("imageUrl[]");
		String [] title = getParaValues("title[]");
		if (StringUtils.isBlank(ccGoods.getId())) {
			ccGoods.setId(StrKit.getRandomUUID());
			ccGoods.setCreateDate(new Date());
		} else {
			update = true;
		}
		this.setImagePath(imagePath, title, ccGoods);
		boolean status = saveProductInfo(map, ccGoods, update);
		
		if (status) {
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("货号有重复,请重新填写");
		}
	}
	
	private boolean saveProductInfo(final Map<String, String[]> map,final Goods goods,final boolean update) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		if (update) {
        			goods.saveOrUpdate();
        		} else {
        			goods.save();
        		}
        		List<Product> oldList = ProductQuery.me().findByGoodId(goods.getId());
        		int productSize = getLikeByMap(map, "product"); //map取出产品属性信息数量
        		int loop = productSize / 9; //算出产品数量与循环次数
        		int loopEnd = 0;
        		List<String> newProIds = new ArrayList<>();
        		int market = 0; //上架判断
        		
        		List<Product> saveProList = new ArrayList<>();
        		List<Product> updateProList = new ArrayList<>();
        		List<ProductGoodsSpecificationValue> psList = new ArrayList<>();
        		List<GoodsGoodsSpecification> gsList = new ArrayList<>();
        		List<String> cggIds = new ArrayList<>();
        		
        		if (loop > 0) {
        			for (int i = 0; i >= 0; i++) {
        				Product product = getModel(Product.class);
        				String productId = StringUtils.getArrayFirst(map.get("productList[" + i + "].id"));
        				String productSn = StringUtils.getArrayFirst(map.get("productList[" + i + "].productSn"));
        				if (StringUtils.isBlank(productSn)) {
        					continue;
        				} else {
        					loopEnd++;
        				}
        				String productPrice = StringUtils.getArrayFirst(map.get("productList[" + i + "].price"));
        				String productCost = StringUtils.getArrayFirst(map.get("productList[" + i + "].cost"));
        				String productMarketPrice = StringUtils.getArrayFirst(map.get("productList[" + i + "].marketPrice"));
        				String productWeight = StringUtils.getArrayFirst(map.get("productList[" + i + "].weight"));
        				String productWeightUnit = StringUtils.getArrayFirst(map.get("productList[" + i + "].weightUnit"));
        				String productStore = StringUtils.getArrayFirst(map.get("productList[" + i + "].store"));
        				String productStorePlace = StringUtils.getArrayFirst(map.get("productList[" + i + "].storePlace"));
        				String productIsMarketable = StringUtils.getArrayFirst(map.get("productList[" + i + "].isMarketable"));
        				
        				if (productId != null) {
        					newProIds.add(productId);
        				}
        				product.setId(productId);
        				product.setProductSn(productSn);
        				product.setPrice(StringUtils.isNumeric(productPrice)? new BigDecimal(productPrice) : new BigDecimal(0));
        				product.setCost(StringUtils.isNumeric(productCost)? new BigDecimal(productCost) : new BigDecimal(0));
        				product.setMarketPrice(StringUtils.isNumeric(productMarketPrice)? new BigDecimal(productMarketPrice) : new BigDecimal(0));
        				product.setWeight(productWeight == null ? null : Double.valueOf(productWeight));
        				product.setWeightUnit(productWeightUnit == null ? null : Integer.parseInt(productWeightUnit));
        				product.setStore(productStore == null ? null : Integer.parseInt(productStore));
        				product.setStorePlace(productStorePlace);
        				product.setIsMarketable(Boolean.valueOf(productIsMarketable));
        				if (product.getIsMarketable()) {
        					market++;
        				}
        				product.setGoodsId(goods.getId());
        				product.setName(goods.getName());
        				product.setFreezeStore(0);
        				
        				if (StringUtils.isBlank(productId)) {
        					product.setId(StrKit.getRandomUUID());
        					product.setCreateDate(new Date());
        					saveProList.add(product);
        				} else {
        					updateProList.add(product);
        				}
        				
        				String[] ids = map.get("goodsSpecificationIds");
        				
        				//存储关联表信息
        				for (int j = 0;j < ids.length;j++) {
        					String spId = ids[j];
        					String spvalueId = StringUtils.getArrayFirst(map.get(spId + "[" + i + "]"));
        					ProductGoodsSpecificationValue pgsValue = getModel(ProductGoodsSpecificationValue.class);
        					pgsValue.setGoodsSpecificationValueSetId(spvalueId);
        					pgsValue.setProductSetId(product.getId());
        					psList.add(pgsValue);
        					
        					if (cggIds.indexOf(spId) == -1) {
            					GoodsGoodsSpecification cggs = getModel(GoodsGoodsSpecification.class);
            					cggs.setGoodsSpecificationId(spId);
            					cggs.setGoodsId(goods.getId());
            					gsList.add(cggs);
            					cggIds.add(spId);
        					}
        				}
        				
        				if (loopEnd == loop) {
        					break;
        				}
        			}
        		}
        		
        		if (market > 0 && goods.getState() == 0) {
        			goods.setState(1);
        			goods.update();
        		}
        		
        		List<GoodsGoodsAttributeMapStore> attributeMap = new ArrayList<>();
        		//储存属性信息
        		List<GoodsAttribute> attributes = GoodsAttributeQuery.me().findByTypeId(goods.getGoodsTypeId());
        		for (GoodsAttribute goodsAttribute : attributes) {
        			String attribute = StringUtils.getArrayFirst(map.get(goodsAttribute.getId()));
        			if (attribute != null) {
        				GoodsGoodsAttributeMapStore attributeMapStore = getModel(GoodsGoodsAttributeMapStore.class);
        				attributeMapStore.setGoodsId(goods.getId());
        				attributeMapStore.setGoodsAttributeMapStoreElement(attribute);
        				attributeMapStore.setGoodsAttributeMapStoreMapkeyId(goodsAttribute.getId());
        				attributeMap.add(attributeMapStore);
        			}
        		}
        		
        		//删除用户删掉的产品及关联信息
        		List<String> ids = getDiffrent(oldList, newProIds);
        		try {
    				newProIds.addAll(ids);
    				ProductGoodsSpecificationValueQuery.me().batchDeleteByProIds(newProIds);
    				GoodsGoodsSpecificationQuery.me().deleteByGoodsId(goods.getId());
    				GoodsGoodsAttributeMapStoreQuery.me().deleteAllByGoodsId(goods.getId());
    				ProductQuery.me().batchDelete(ids);
                    Db.batchSave(saveProList, saveProList.size());
                    Db.batchUpdate(updateProList, updateProList.size());
                    Db.batchSave(psList, psList.size());
                    Db.batchSave(gsList, gsList.size());
                    Db.batchSave(attributeMap, attributeMap.size());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
        		return true;
            }
        });
        return isSave;
	}
	
	private void setImagePath(String [] path, String [] title, Goods goods) {
		List<ImageJson> imageList = new ArrayList<>();
		if (path != null) {
			for (int i = 0;i < path.length;i++) {
				ImageJson imageJson = new ImageJson();
				imageJson.setImgName(title[i]);
				imageJson.setSavePath(path[i].replace("\\", "/"));
				imageList.add(imageJson);
			}
		}
		String json = JSON.toJSONString(imageList);
		goods.setProductImageListStore(json);
	}
	
	//map key模糊查询
    private int getLikeByMap(Map<String, String[]>map, String keyLike){
        int i = 0;        
        for (Map.Entry<String, String[]> entity : map.entrySet()) {
	        if(entity.getKey().indexOf(keyLike)>-1){
	                i++;
	        }
        }    
        return i;
    }	
	
	/** 
	 * 获取两个List的不同元素(耗时最低)
	 * @param list1 
	 * @param list2 
	 * @return 
	 */  
	private static List<String> getDiffrent(List<Product> proList, List<String> newIds) {
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = newIds;
		for (Product product : proList) {
			list1.add(product.getId());
		}
		List<String> diff = new ArrayList<String>();  
	    List<String> maxList = list1;  
	    List<String> minList = list2;  
	    if(list2.size()>list1.size()) {  
	         maxList = list2;  
	         minList = list1;  
	    }  
	    Map<String,Integer> map = new HashMap<String,Integer>(maxList.size());  
	    for (String string : maxList) {  
	        map.put(string, 1);  
	    }  
	    for (String string : minList) {  
	        if(map.get(string)!=null) {  
	            map.put(string, 2);  
	            continue;  
	        }  
	        diff.add(string);  
	    }  
	    for(Map.Entry<String, Integer> entry:map.entrySet()) {  
	        if(entry.getValue()==1) {  
	            diff.add(entry.getKey());  
	        }  
	    }  
	    return diff;  
	}	
	
	public void getSpValue() {
		String id = getPara("id");
		GoodsSpecification goodsSpecification = GoodsSpecificationQuery.me().findById(id);
		List<GoodsSpecificationValue> childList = GoodsSpecificationValueQuery.me().findByParentId(id);
        Map<String, Object> map = ImmutableMap.of("bean", goodsSpecification, "child", childList);
        renderJson(map);
	}
	
	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		int count = GoodsQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}
	
	public void getCategory() {
        String id = getPara("brandId");
        List<GoodsCategory> categoryList = GoodsCategoryQuery.me().findCategoryByBrandId(id);
        List<Map<String, Object>> list = new ArrayList<>();

        for (GoodsCategory category : categoryList) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", category.getId());
            map.put("name", category.getName());

            list.add(map);
        }

        renderJson(list);
	}
	
	@Override
	public void delete() {
		String id = getPara("id");
		final Goods r = GoodsQuery.me().findById(id);
		if (r != null) {
			int count = GoodsQuery.me().deleteAbout(r);
            if (count > 0) {
                renderAjaxResultForSuccess("删除成功");
            } else {
                renderAjaxResultForError("删除失败");
            }
		}
	}	
	
    public void uploadImg() {
    	String id = getPara("id");
    	if (id != null) {
    		UploadFile uploadFile = getFile();
			Goods goods = GoodsQuery.me().findById(id);
    		String imgName = uploadFile.getFileName();
    		String imgList = goods.getProductImageListStore();
    		if (StringUtils.isNotEmpty(imgList)) {
    			JSONArray jsonArray = JSONArray.parseArray(imgList);
    			List<ImageJson> imageList = jsonArray.toJavaList(ImageJson.class);
    			ImageJson imageJson = new ImageJson();
    			imageJson.setImgName(imgName);
    			String newPath = AttachmentUtils.moveFile(uploadFile);
    			imageJson.setSavePath(newPath.replace("\\", "/"));
    			imageList.add(imageJson);
    			String json = JSON.toJSONString(imageList);
    			goods.setProductImageListStore(json);
    			goods.saveOrUpdate();
    		} else {
    			ImageJson[] imageJson = new ImageJson[1];
    			ImageJson image = new ImageJson();
    			image.setImgName(imgName);
    			String newPath = AttachmentUtils.moveFile(uploadFile);
    			image.setSavePath(newPath.replace("\\", "/"));
    			imageJson [0] = image; 
    			String json = JSON.toJSONString(imageJson);
    			goods.setProductImageListStore(json);
    			goods.saveOrUpdate();
    		}
    		renderAjaxResultForSuccess("上传成功");
    	} else {
    		renderAjaxResultForError("商品不存在!");
    	}
    }
    
    public void imageManager() {
        setAttr("id", getPara("id"));
        try {
			String name = new String(getPara("goodsName").getBytes("ISO-8859-1"),"UTF-8");
			setAttr("goodsName", name);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public void getImgList() {
    	String id = getPara("id");
    	if (id != null) {
    		Goods goods = GoodsQuery.me().findById(id);
    		String imgList = goods.getProductImageListStore();
    		JSONArray jsonArray = JSONArray.parseArray(imgList);
    		renderAjaxResultForSuccess("读取成功", jsonArray);
    	}
    }
    
    public void deleteImg() {
    	String imgName = getPara("name");
    	String id = getPara("id");
    	Goods goods = GoodsQuery.me().findById(id);
    	String imgList = goods.getProductImageListStore();
		JSONArray jsonArray = JSONArray.parseArray(imgList);
		List<ImageJson> imageList = jsonArray.toJavaList(ImageJson.class);
		for (int i = 0; i < imageList.size(); i++) {
			if (imageList.get(i).getImgName().equals(imgName)) {
				File file1 = new File(PathKit.getWebRootPath()+imageList.get(i).getSavePath());
				if (file1.exists() && file1.isFile()) {
					file1.delete();
				}
				imageList.remove(i);
				break;
			}
		}
		String json = JSON.toJSONString(imageList);
		goods.setProductImageListStore(json);
		goods.update();
		renderAjaxResultForSuccess("删除成功");
    }
    
	public void enable() {
		String id = getPara("id");
		int state = getParaToInt("state");
		Goods goods = GoodsQuery.me().findById(id);
		List<Product> list = ProductQuery.me().findByGoodId(id);
		for (Product product : list) {
			if (state == 0 && product.getIsMarketable()) {
				product.setIsMarketable(false);
				product.update();
			}
		}
		goods.setState(state);
		goods.update();
		renderAjaxResultForSuccess("更新成功");
	}
	
	public void category_tree() {
		String id = getPara("id");
		List<Map<String, Object>> list = GoodsCategoryQuery.me().findCategoryListAsTreeByBrand(1, id);
		setAttr("treeData", JSON.toJSON(list));
	}
	
	public void getProductInfo() {
		List<ProductInfo> productList = ProductQuery.me().getAllProductInfo();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ProductInfo productInfo : productList) {
           Map<String, Object> map = new HashMap<>();
            map.put("productList", productInfo);
            list.add(map);
		}
        renderJson(list);
	}
	
}
