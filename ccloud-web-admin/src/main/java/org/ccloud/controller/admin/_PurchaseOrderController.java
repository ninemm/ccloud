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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Product;
import org.ccloud.model.PurchaseInstock;
import org.ccloud.model.PurchaseInstockDetail;
import org.ccloud.model.PurchaseOrder;
import org.ccloud.model.PurchaseOrderDetail;
import org.ccloud.model.PurchaseOrderJoinInstock;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.PurchaseInstockQuery;
import org.ccloud.model.query.PurchaseOrderDetailQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/purchaseOrder", viewPath = "/WEB-INF/admin/purchase_order")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/purchaseOrder")
public class _PurchaseOrderController extends JBaseCRUDController<PurchaseOrder> { 

	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");

		setAttr("startDate", date);
		setAttr("endDate", date);

		render("index.html");
	}
	
	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr("sellerId");

		Page<Record> page = PurchaseOrderQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate,getSessionAttr(Consts.SESSION_SELECT_DATAAREA).toString(),sellerId);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void detail(){
		String orderId = getPara(0);
		setAttr("orderId", orderId);
		render("detail.html");
	}
	
	public void listOther(){
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> page = PurchaseOrderQuery.me().paginateO(getPageNumber(), getPageSize(), keyword, startDate, endDate,getSessionAttr(Consts.SESSION_SELECT_DATAAREA).toString(),user.getId());

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	//入库订单及明细
	@Before(Tx.class)
	public void pass(){
		String orderId = getPara("id");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Seller seller = SellerQuery.me().findById(getSessionAttr("sellerId").toString());
		PurchaseOrder purchaseOrder=PurchaseOrderQuery.me().findById(orderId);
		purchaseOrder.set("status", 1000);
		Warehouse warehouse = WarehouseQuery.me().findOneByUserId(user.getId());
		final PurchaseInstock purchaseInstock = getModel(PurchaseInstock.class);
		PurchaseInstockDetail purchaseInstockDetail = getModel(PurchaseInstockDetail.class);
		String purchaseInstockId = StrKit.getRandomUUID();
		//采购入库单： PO + 100000(机构编号或企业编号6位) + 20171108(时间) + 100001(流水号)
		int m=PurchaseInstockQuery.me().findByUserId(user.getId(),user.getDataArea());
		m++;
		String n=Integer.toString(m);
		int countt =n.length();
		for(int k=0;k<(6-countt);k++){
			n= "0"+n;
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fomatDate = sdf.format(date);
		String pwarehouseSn="PO"+seller.getSellerCode().substring(0, 6)+fomatDate.substring(0,8)+n;
		purchaseInstock.set("id", purchaseInstockId);
		purchaseInstock.set("pwarehouse_sn", pwarehouseSn);
		purchaseInstock.set("supplier_id", purchaseOrder.getSupplierId());
		purchaseInstock.set("warehouse_id", warehouse.getId());
		purchaseInstock.set("biz_user_id", user.getId());
		purchaseInstock.set("biz_date", new Date());
		purchaseInstock.set("input_user_id", user.getId());
		purchaseInstock.set("status", 0);//待审核
		purchaseInstock.set("total_amount",purchaseOrder.getTotalAmount());
		purchaseInstock.set("payment_type", purchaseOrder.getPaymentType());
		purchaseInstock.set("remark",  purchaseOrder.getRemark());
		purchaseInstock.set("dept_id", user.getDepartmentId());
		purchaseInstock.set("data_area", user.getDataArea());
		purchaseInstock.set("create_date", date);
		purchaseInstock.save();
		purchaseOrder.update();
		
		List<PurchaseOrderDetail> purchaseOrderDetails =  PurchaseOrderDetailQuery.me().findByPurchaseOrderId(orderId);
		HttpServletRequest request = getRequest();
		for(PurchaseOrderDetail purchaseOrderDetail : purchaseOrderDetails){
			Product product = ProductQuery.me().findById(purchaseOrderDetail.getProductId());
			List<SellerProduct> sellerProducts = SellerProductQuery.me()._findByProductIdAndSellerId(purchaseOrderDetail.getProductId(),seller.getId());
			if(sellerProducts.size()==0){
				SellerProduct sellerProduct = SellerProductQuery.me().newProduct(seller.getId(), date, fomatDate, product, request);
				sellerProducts.add(sellerProduct);
			}
			for(int i=0;i<sellerProducts.size();i++){
				purchaseInstockDetail = new PurchaseInstockDetail();
				purchaseInstockDetail.set("id", StrKit.getRandomUUID());
				purchaseInstockDetail.set("purchase_instock_id", purchaseInstockId);
				purchaseInstockDetail.set("product_count", purchaseOrderDetail.getProductCount());
				purchaseInstockDetail.set("product_price", purchaseOrderDetail.getProductPrice());
				purchaseInstockDetail.set("product_amount", purchaseOrderDetail.getProductAmount());
				purchaseInstockDetail.set("purchase_order_detail_id", purchaseOrderDetail.getId());
				purchaseInstockDetail.set("order_list", purchaseOrderDetail.getOrderList());
				purchaseInstockDetail.set("create_date", date);
				purchaseInstockDetail.set("dept_id", user.getDepartmentId());
				purchaseInstockDetail.set("data_area", user.getDataArea());
				purchaseInstockDetail.set("seller_product_id", sellerProducts.get(i).getId());
				purchaseInstockDetail.save();
			}
		}
		
		renderAjaxResultForSuccess("OK");
	}


	
	@Override
	@Before(Tx.class)
	public void save(){
		final PurchaseInstock purchaseInstock = getModel(PurchaseInstock.class);
		final PurchaseInstockDetail purchaseInstockDetail = getModel(PurchaseInstockDetail.class);
		final PurchaseOrderJoinInstock purchaseOrderJoinInstock = getModel(PurchaseOrderJoinInstock.class);
		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Seller seller = SellerQuery.me().findById(getSessionAttr("sellerId").toString());
		Warehouse warehouse = WarehouseQuery.me().findOneByUserId(user.getId());
		String purchaseInstockId = StrKit.getRandomUUID();

		//采购入库单： PO + 100000(机构编号或企业编号6位) + 20171108(时间) + 100001(流水号)
		int m=PurchaseInstockQuery.me().findByUserId(user.getId(),user.getDataArea());
		m++;
		String n=Integer.toString(m);
		int countt =n.length();
		for(int k=0;k<(6-countt);k++){
			n= "0"+n;
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fomatDate = sdf.format(date);
		String purchaseOrderId=StringUtils.getArrayFirst(paraMap.get("purchaseOrderId"));
		PurchaseOrder purchaseOrder = PurchaseOrderQuery.me().findById(purchaseOrderId);
		String pwarehouseSn="PO"+user.getDepartmentId().substring(0, 6)+fomatDate.substring(0,8)+n;
		purchaseInstock.set("id", purchaseInstockId);
		purchaseInstock.set("pwarehouse_sn", pwarehouseSn);
		purchaseInstock.set("supplier_id", StringUtils.getArrayFirst(paraMap.get("supplierId")));
		purchaseInstock.set("warehouse_id", warehouse.getId());
		purchaseInstock.set("biz_user_id", user.getId());
		purchaseInstock.set("biz_date", new Date());
		purchaseInstock.set("input_user_id", user.getId());
		purchaseInstock.set("status", 0);//待审核
		purchaseInstock.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		purchaseInstock.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		purchaseInstock.set("remark",  StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseInstock.set("dept_id", user.getDepartmentId());
		purchaseInstock.set("data_area", user.getDataArea());
		purchaseInstock.set("create_date", date);
		
		PurchaseOrder order = PurchaseOrderQuery.me().findById(purchaseOrderId);
		order.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		order.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		order.set("status", 1000);
		order.set("modify_date", new Date());
		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		
		Integer index = 0;

		for ( Integer count = 0;count<productNum;count++) {
			index++;
			HttpServletRequest request = getRequest();
			String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
			String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
			String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));
			Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
			String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
			String purchaseOrserDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseOrderDetailId" + index));
			PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetailQuery.me().findById(purchaseOrserDetailId);
			Product product = ProductQuery.me().findById(productId);
			List<SellerProduct> sellerProducts = SellerProductQuery.me()._findByProductIdAndSellerId(productId,seller.getId());
			if(sellerProducts.size()==0){
				SellerProduct sellerProduct = SellerProductQuery.me().newProduct(seller.getId(), date, fomatDate, product, request);
				sellerProducts.add(sellerProduct);
			}
			purchaseOrderDetail.set("product_count", productCount);
			purchaseOrderDetail.set("product_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
			purchaseOrderDetail.update();
			for(int i=0;i<sellerProducts.size();i++){
				
				
				purchaseInstockDetail.set("id", StrKit.getRandomUUID());
				purchaseInstockDetail.set("purchase_instock_id", purchaseInstockId);
				purchaseInstockDetail.set("seller_product_id", sellerProducts.get(i).getId());
				purchaseInstockDetail.set("product_count", productCount);
				purchaseInstockDetail.set("product_price", StringUtils.getArrayFirst(paraMap.get("bigPrice" + index)));
				purchaseInstockDetail.set("product_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
				purchaseInstockDetail.set("purchase_order_detail_id", purchaseOrserDetailId);
				purchaseInstockDetail.set("order_list", index);
				purchaseInstockDetail.set("create_date", date);
				purchaseInstockDetail.set("dept_id", user.getDepartmentId());
				purchaseInstockDetail.set("data_area", user.getDataArea());
				purchaseInstockDetail.save();
				purchaseOrder.set("status", 4000);
				purchaseOrder.update();
				
			}

		}
		order.update();
		purchaseInstock.save();
		purchaseOrderJoinInstock.set("id", StrKit.getRandomUUID());
		purchaseOrderJoinInstock.set("purchase_order_id", StringUtils.getArrayFirst(paraMap.get("purchaseOrderId")));
		purchaseOrderJoinInstock.set("purchase_instock_id", purchaseInstockId);
		purchaseOrderJoinInstock.save();
		renderAjaxResultForSuccess("OK");
	} 
	
	
}
