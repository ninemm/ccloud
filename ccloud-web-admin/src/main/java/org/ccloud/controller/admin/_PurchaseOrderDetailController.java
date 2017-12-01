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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Department;
import org.ccloud.model.Product;
import org.ccloud.model.PurchaseOrder;
import org.ccloud.model.PurchaseOrderDetail;
import org.ccloud.model.Supplier;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.SupplierQuery;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/purchaseOrderDetail", viewPath = "/WEB-INF/admin/purchase_order_detail")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PurchaseOrderDetailController extends JBaseCRUDController<PurchaseOrderDetail> { 
	
	@Override
	public void index() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (user == null ) {
			// TODO
		}
		
		List<Product> productlist = ProductQuery.me().findAllByUserId(user.getId(),user.getDataArea());
		
		Map<String, Object> productInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();
		
		for (Product record : productlist) {
			Map<String, String> productOptionMap = new HashMap<String, String>();
			
			String productId = record.getStr("id");
			String customName = record.getStr("name");
			String productNamePig = record.getStr("cps_name");
			
			productInfoMap.put(productId, record);
			
			productOptionMap.put("id", productId);
			productOptionMap.put("text", customName+"/"+productNamePig);
			
			productOptionList.add(productOptionMap);
		}
		
		List<Supplier> suppliers = SupplierQuery.me().findAll();
		
		Map<String, Object> supplierInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> supplierOptionList = new ArrayList<Map<String, String>>();
		
		for (Supplier record : suppliers) {
			Map<String, String> supplierOptionMap = new HashMap<String, String>();
			
			String id = record.getStr("id");
			String name = record.getStr("name");
			
			supplierInfoMap.put(id, record);
			
			supplierOptionMap.put("id", id);
			supplierOptionMap.put("text", name);
			
			supplierOptionList.add(supplierOptionMap);
		}
		
		setAttr("productInfoMap", JSON.toJSON(productInfoMap));
		setAttr("productOptionList", JSON.toJSON(productOptionList));
		
		setAttr("supplierInfoMap", JSON.toJSON(supplierInfoMap));
		setAttr("supplierOptionList", JSON.toJSON(supplierOptionList));
		
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		
		render("index.html");
	}
		
	public void list(){
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Product> list = ProductQuery.me().findAllByUserId(user.getId(),user.getDataArea());
		renderJson(list);
	}
	
	public void showTr(){
		String id = getPara("id");
		Product product = ProductQuery.me().findByPId(id);
		renderJson(product);
	}
	
	@Override
	@Before(Tx.class)
	public void save() {
		final PurchaseOrder purchaseOrder =getModel(PurchaseOrder.class);
		final PurchaseOrderDetail purchaseOrderDetail = getModel(PurchaseOrderDetail.class);
		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		int i = PurchaseOrderQuery.me().findByUserId(user.getId(),user.getDataArea());
		Department department = DepartmentQuery.me().findByUserId(user.getId());
		/*采购订单：PO + 100000(机构编号或企业编号6位) + 20171108(时间) + 000001(流水号)*/
		i++;
		String j=Integer.toString(i);
		int countt =j.length();
		for(int m=0;m<(6-countt);m++){
			j= "0"+j;
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		String porderSn = "PO"+department.getId().substring(0, 6)+str.substring(0,8)+j;
		Date date1 = new Date();
		String Id = StrKit.getRandomUUID();
		purchaseOrder.set("id", Id);
		purchaseOrder.set("porder_sn", porderSn);
		purchaseOrder.set("supplier_id", StringUtils.getArrayFirst(paraMap.get("supplierId")));
		purchaseOrder.set("contact", StringUtils.getArrayFirst(paraMap.get("mobile")));
		purchaseOrder.set("mobile", StringUtils.getArrayFirst(paraMap.get("mobile")));
		purchaseOrder.set("biz_user_id", user.getId());
		purchaseOrder.set("biz_date", date1);
		purchaseOrder.set("status", 0);
		purchaseOrder.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		purchaseOrder.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		purchaseOrder.set("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseOrder.set("dept_id", user.getDepartmentId());
		purchaseOrder.set("data_area", user.getDataArea());
		purchaseOrder.set("deal_date", StringUtils.getArrayFirst(paraMap.get("deliveryDate")));
		purchaseOrder.set("create_date", date1);
		purchaseOrder.save();
		
		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;
		
		while (productNum > count) {
			index++;
			String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
			Product product = ProductQuery.me().findByUserId(user.getId(),productId);
			int big =product.getConvertRelate();
			if (StrKit.notBlank(productId)) {
				String Id2 = StrKit.getRandomUUID();
				int rowTotal=((Integer.parseInt(StringUtils.getArrayFirst(paraMap.get("smallNum"+ index)))+Integer.parseInt(StringUtils.getArrayFirst(paraMap.get("bigNum"+ index)))*big))*Integer.parseInt(StringUtils.getArrayFirst(paraMap.get("smallPrice"+ index)));
				purchaseOrderDetail.set("id", Id2);
				purchaseOrderDetail.set("purchase_order_id", Id);
				purchaseOrderDetail.set("product_id", productId);
				purchaseOrderDetail.set("product_count", (Integer.parseInt(StringUtils.getArrayFirst(paraMap.get("smallNum"+ index)))+Integer.parseInt(StringUtils.getArrayFirst(paraMap.get("bigNum"+ index)))*big));
				purchaseOrderDetail.set("product_amount", rowTotal);
				purchaseOrderDetail.set("product_price", StringUtils.getArrayFirst(paraMap.get("smallPrice"+ index)));
				purchaseOrderDetail.set("order_list",index);
				purchaseOrderDetail.set("create_date", date1);
				purchaseOrderDetail.set("dept_id", user.getDepartmentId());
				purchaseOrderDetail.set("data_area", user.getDataArea());
				purchaseOrderDetail.save();
				
				count++;
			}

		}
		renderAjaxResultForSuccess();

	}
}
