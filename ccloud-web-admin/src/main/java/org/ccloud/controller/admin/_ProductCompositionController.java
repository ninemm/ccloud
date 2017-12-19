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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.ProductComposition;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.ProductCompositionQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerProductQuery;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/productComposition", viewPath = "/WEB-INF/admin/product_composition")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ProductCompositionController extends JBaseCRUDController<ProductComposition> { 

	@Override
	@RequiresPermissions(value={"/admin/productComposition","/admin/all"},logical=Logical.OR)
	public void index() {
		
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) setAttr("k", keyword);
		
		String sellerId = getSessionAttr("sellerId");
		
		Page<ProductComposition> page = ProductCompositionQuery.me().paginate(getPageNumber(), getPageSize(), keyword, sellerId, null);
		if (page != null) {
			setAttr("page", page);
		}
		
	}
	
	@Override
	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"/admin/productComposition","/admin/all"},logical=Logical.OR)
	public void save() {
		Map<String, String[]> map = getParaMap();
		boolean status = this.saveProduct(map);
		if (status) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("保存失败");
		}		
	}
	
	public boolean saveProduct(final Map<String, String[]> map) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
					List<ProductComposition> saveList = new ArrayList<>();
					List<ProductComposition> updateList = new ArrayList<>();
					List<String> newIds = new ArrayList<>();
					String mainId = StringUtils
							.getArrayFirst(map.get("parentId"));					
					String[] factIndex = map.get("factIndex");
					String productId = StringUtils
							.getArrayFirst(map.get("productComposition[0].id"));
					String name = StringUtils
							.getArrayFirst(map.get("productComposition.name"));
					String price = StringUtils
							.getArrayFirst(map.get("productComposition.price"));						
					List<ProductComposition> oldList = new ArrayList<>();
					if (mainId != null) {
						oldList = ProductCompositionQuery.me().findByParentId(mainId);
					}
					for (int i = 2; i < factIndex.length; i++) {
						String compositionId = StringUtils
								.getArrayFirst(map.get("composition[" + factIndex[i] + "].id"));
						String subProductId = StringUtils
								.getArrayFirst(map.get("productComposition[" + factIndex[i] + "].id"));
						String subProductCount = StringUtils
								.getArrayFirst(map.get("productComposition[" + factIndex[i] + "].product_count"));						
						ProductComposition composition = new ProductComposition();
						composition.setName(name);
						composition.setPrice(new BigDecimal(price));
						composition.setSellerProductId(productId);
						composition.setSubProductCount(subProductCount);
						composition.setSubSellerProductId(subProductId);
						if (compositionId != null) {
							composition.setId(compositionId);
							composition.setParentId(mainId);
							newIds.add(compositionId);
							updateList.add(composition);
						} else {
							composition.setId(StrKit.getRandomUUID());
							if (i == 2) {
								mainId = composition.getId();
							}
							composition.setParentId(mainId);
							saveList.add(composition);
						}						
					}
					List<String> deleteIds = getDiffrent(oldList, newIds);
					try {
						ProductCompositionQuery.me().batchDelete(deleteIds);
						Db.batchSave(saveList, saveList.size());
						Db.batchUpdate(updateList, updateList.size());
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				return true;
			}
		});
		return isSave;
	}
	
	/** 
	 * 获取两个List的不同元素(耗时最低)
	 * @param list1 
	 * @param list2 
	 * @return 
	 */  
	private static List<String> getDiffrent(List<ProductComposition> proList, List<String> newIds) {
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = newIds;
		for (ProductComposition composition : proList) {
			list1.add(composition.getId());
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
	
	@Override
	@RequiresPermissions(value={"/admin/productComposition/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		List<Record> list = new ArrayList<>();
		if (StringUtils.isNotBlank(id)) {
			list = ProductCompositionQuery.me().findDetailByProductId(id, "", "");
			String name = list.get(0).getStr("name");
			BigDecimal price = list.get(0).getBigDecimal("price");
			setAttr("productCompositionName", name);
			setAttr("productCompositionPrice", price);
		}
		setAttr("list", list);
		setAttr("parentId", id);
	}
	
	@Override
	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"/admin/productComposition/edit","/admin/all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		int i = ProductCompositionQuery.me().deleteByParentId(id);
		if (i > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForSuccess("删除失败");
		}
	}
	
	@RequiresPermissions(value={"/admin/productComposition/edit","/admin/all"},logical=Logical.OR)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		int count = ProductCompositionQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}	
	
	public void getProductInfo() {
		String sellerId = getSessionAttr("sellerId");
		if (StringUtils.isNotBlank(sellerId)) {
			List<SellerProduct> list = SellerProductQuery.me().findBySellerId(sellerId);
			List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();
			for (SellerProduct sellerProduct : list) {
				Map<String, String> productOptionMap = new HashMap<String, String>();
				
				String sellProductId = sellerProduct.getId();
				String customName = sellerProduct.getCustomName();
				String speName = sellerProduct.getStr("valueName");				
				productOptionMap.put("id", sellProductId);
				productOptionMap.put("custom_name", customName + "/" + speName);

				productOptionList.add(productOptionMap);				
			}
			renderJson(productOptionList);
		}
	}
	
	public void add() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		if (user == null || StrKit.isBlank(sellerId)) {
			// TODO
		}

		List<Record> productlist = ProductCompositionQuery.me().findProductBySeller(sellerId, "");
		Map<String, Object> productInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();

		for (Record record : productlist) {
			Map<String, String> productOptionMap = new HashMap<String, String>();

			String sellProductId = record.getStr("id");
			String customName = record.getStr("name");
			String storeCount = record.getStr("store_count");

			productInfoMap.put(sellProductId, record);

			productOptionMap.put("id", sellProductId);
			productOptionMap.put("text", customName);
			productOptionMap.put("store", storeCount);
			
			productOptionList.add(productOptionMap);
		}

		List<Record> customerList = SalesOrderQuery.me().findCustomerListByUser(user.getId());

		Map<String, Object> customerInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> customerOptionList = new ArrayList<Map<String, String>>();

		for (Record record : customerList) {
			Map<String, String> customerOptionMap = new HashMap<String, String>();

			String customerId = record.getStr("id");
			String customerName = record.getStr("customer_name");

			customerInfoMap.put(customerId, record);

			customerOptionMap.put("id", customerId);
			customerOptionMap.put("text", customerName);

			customerOptionList.add(customerOptionMap);
		}

		setAttr("productInfoMap", JSON.toJSON(productInfoMap));
		setAttr("productOptionList", JSON.toJSON(productOptionList));

		setAttr("customerInfoMap", JSON.toJSON(customerInfoMap));
		setAttr("customerOptionList", JSON.toJSON(customerOptionList));

		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

		render("add.html");
	}	
	
	@RequiresPermissions(value={"/admin/productComposition","/admin/salesOrder"},logical=Logical.OR)
	public void getDetail () {
		String id = getPara("id");
		List<Record> list = new ArrayList<>();
		if (StringUtils.isNotBlank(id)) {
			list = ProductCompositionQuery.me().findDetailByProductId(id, "", "");
		}
		setAttr("list", list);
		render("product_detail.html");
	}
	
	@RequiresPermissions(value={"/admin/salesOrder"})
	public synchronized void saveOrder() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");

		if (this.saveDetail(paraMap, user, sellerId, sellerCode)) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("库存不足或仓库中未找到对应商品");
		}
	}
	
	private boolean saveDetail(final Map<String, String[]> paraMap, final User user, 
			final String sellerId, final String sellerCode) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
        		Integer productNum = Integer.valueOf(productNumStr);
        		Integer count = 0;
        		Integer index = 0;
        		
        		String orderId = StrKit.getRandomUUID();
        		Date date = new Date();
        		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);

        		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
        		String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
        				+ DateUtils.format("yyMMdd", date) + OrderSO;

        		if(!SalesOrderQuery.me().insertOrderByComposition(paraMap, orderId, orderSn, sellerId, user.getId(), date, user.getDepartmentId(),
        				user.getDataArea())) {
        			return false;
        		}

        		while (productNum > count) {
        			index++;
        			String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
//        			String isGift = StringUtils.getArrayFirst(paraMap.get("isGift" + index));
        			String number = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
//        			Integer gift = StringUtils.isNumeric(isGift)? Integer.parseInt(isGift) : 0;
        			if (StrKit.notBlank(productId)) {
	        			List<SellerProduct> list = SellerProductQuery.me().findByCompositionId(productId);
	        			for (SellerProduct sellerProduct : list) {
	        				if(!SalesOrderDetailQuery.me().insertDetailByComposition(sellerProduct, orderId, sellerId, user.getId(), date,
	        						user.getDepartmentId(), user.getDataArea(), index, Integer.parseInt(number))) {
	        					return false;
	        				}
						}
	        			count++;
        			}
        		}
            	return true;
            }
        });
        return isSave;
	}	
	
}
