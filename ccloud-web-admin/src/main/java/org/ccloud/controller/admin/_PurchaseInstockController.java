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
import org.ccloud.model.Inventory;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.PurchaseInstock;
import org.ccloud.model.PurchaseInstockDetail;
import org.ccloud.model.PurchaseOrder;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.PurchaseInstockDetailQuery;
import org.ccloud.model.query.PurchaseInstockQuery;
import org.ccloud.model.query.PurchaseOrderDetailQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/purchaseInstock", viewPath = "/WEB-INF/admin/purchase_instock")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PurchaseInstockController extends JBaseCRUDController<PurchaseInstock> { 
	
	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");

		setAttr("startDate", date);
		setAttr("endDate", date);

		render("index.html");
	}

	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> page = PurchaseInstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate,user.getId(),user.getDataArea());

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void listOther(){
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> page = PurchaseInstockQuery.me().paginateO(getPageNumber(), getPageSize(), keyword, startDate, endDate);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	public void detail() {
		String instockId = getPara(0);

		setAttr("instockId", instockId);
		render("detail.html");

	}
	
	public void add() {
		render("add.html");
	}
	
	public void orderIndex(){
		render("purchase_order_index.html");
	}
	
	public void orderDetail(){
		String orderId = getPara(0);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record order = PurchaseOrderQuery.me().findMoreById(orderId,user.getDataArea());
		List<Record> orderDetail = PurchaseOrderDetailQuery.me().findByOutstockId(orderId,user.getDataArea());

		setAttr("order", order);
		setAttr("orderDetail", orderDetail);

		render("purchase_order_detail.html");
	}
	
	public void refund() {
		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record order = PurchaseOrderQuery.me().findMoreById(orderId,user.getDataArea());
		List<Record> orderDetail = PurchaseOrderDetailQuery.me().findByOutstockId(orderId,user.getDataArea());

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("order", order);
		result.put("orderDetail", orderDetail);

		renderJson(result);
	}
	
	@Override
	@Before(Tx.class)
	public void save(){
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, String[]> paraMap = getParaMap();
		String purchaseInstockId = StringUtils.getArrayFirst(paraMap.get("purchaseInstockId"));
		PurchaseInstock purchaseInstock = PurchaseInstockQuery.me().findById(purchaseInstockId);
		List<PurchaseInstockDetail> instockDetails = PurchaseInstockDetailQuery.me().findAllByPurchaseInstockId(purchaseInstockId);
		List<PurchaseInstockDetail> details = instockDetails;
		/*purchaseInstock.set("status", 1000);//已通过
		purchaseInstock.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		purchaseInstock.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		purchaseInstock.set("remark",  StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseInstock.set("dept_id", user.getDepartmentId());
		purchaseInstock.set("data_area", user.getDataArea());
		purchaseInstock.set("modify_date", new Date());
		purchaseInstock.update();*/
		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;
		while (productNum > count) {
			index++;
			String purchaseInstockDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseInstockDetailId"+index));
			for(int i = 0;i<instockDetails.size();i++){
				if(purchaseInstockDetailId.equals(instockDetails.get(i).getId())){
						details.remove(instockDetails.get(i));
				}
			}
			PurchaseInstockDetail purchaseInstockDetail = PurchaseInstockDetailQuery.me().findById(purchaseInstockDetailId);
			String purchaseOrderDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseOrderDetailId"+index));
			PurchaseOrder purchaseOrder = PurchaseOrderQuery.me().findByPurchaseOrderDetailId(purchaseOrderDetailId);
			String productId = StringUtils.getArrayFirst(paraMap.get("purchaseInstockDetailId" + index));
			if (StrKit.notBlank(productId)) {
				String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
				String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
				String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));
				Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
				purchaseInstockDetail.set("product_count", productCount);
				purchaseInstockDetail.set("product_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
				purchaseInstockDetail.set("modify_date", new Date());
				purchaseInstockDetail.update();
				purchaseOrder.set("status", 4000);
				purchaseOrder.update();
				count++;
			}
		}
		for(PurchaseInstockDetail d: details){
			d.delete();
		}
		
		//对库存总账进行修改
		boolean flang = false;
		final InventoryDetail inventoryDetail= getModel(InventoryDetail.class);
		Seller seller = SellerQuery.me().findByUserId(user.getId());
		List<PurchaseInstockDetail> list= PurchaseInstockDetailQuery.me().findAllByPurchaseInstockId(purchaseInstockId);
		for(PurchaseInstockDetail pi : list){
			BigDecimal count1 = new BigDecimal(pi.getProductCount());
			BigDecimal convent = new BigDecimal(pi.get("convert_relate").toString());
			String inventoryDetailId = StrKit.getRandomUUID();
			inventoryDetail.set("id", inventoryDetailId);
			inventoryDetail.set("warehouse_id", pi.get("warehouse_id"));
			inventoryDetail.set("sell_product_id",pi.getSellerProductId());
			inventoryDetail.set("in_count", count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
			inventoryDetail.set("in_amount", pi.getProductAmount());
			inventoryDetail.set("in_price", pi.getProductPrice());
			inventoryDetail.set("biz_type", "采购入库");
			inventoryDetail.set("biz_bill_sn", pi.get("pwarehouse_sn"));
			inventoryDetail.set("biz_date", new Date());
			inventoryDetail.set("biz_user_id", user.getId());
			inventoryDetail.set("remark", pi.getRemark());
			inventoryDetail.set("dept_id",pi.getDeptId());
			inventoryDetail.set("data_area", pi.getDataArea());
			inventoryDetail.set("create_date", new Date());
			flang=inventoryDetail.save();
			if(flang==false){
				break;
			}
			Inventory inventory= InventoryQuery.me().findByWarehouseIdAndProductId(pi.get("warehouse_id").toString(), pi.getSellerProductId());
			if(inventory!=null){
				inventory.set("in_count", count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
				inventory.set("in_amount", pi.getProductAmount());
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", inventory.getBalanceCount().add(count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP)));
				inventory.set("balance_amount", inventory.getBalanceAmount().add(pi.getProductAmount()));
				inventory.set("modify_date", new Date());
				flang=inventory.update();
				if(flang==false){
					break;
				}
			}else{
				inventory=new Inventory();
				inventory.set("id", StrKit.getRandomUUID());
				inventory.set("warehouse_id", pi.get("warehouse_id").toString());
				inventory.set("product_id", pi.getSellerProductId());
				inventory.set("seller_id", seller.getId());
//				inventory.set("in_count", count1.divide(convent));
				inventory.set("in_amount",pi.getProductAmount());
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", new BigDecimal(pi.getProductCount()));
				inventory.set("balance_amount", pi.getProductAmount());
				inventory.set("balance_price", pi.getProductPrice());
				inventory.set("data_area",pi.getDataArea());
				inventory.set("dept_id", pi.getDeptId());
				inventory.set("create_date", new Date());
				flang=inventory.save();
				if(flang==false){
					break;
				}
			}
			List<SellerProduct> sellerProducts = SellerProductQuery.me().findByProductIdAndSellerId(pi.getSellerProductId(),seller.getId());
			List<Inventory> inventorys = InventoryQuery.me()._findBySellerIdAndProductId(seller.getId(),pi.getSellerProductId());
			BigDecimal count0 = new BigDecimal(0);
			for(Inventory inventory0:inventorys){
				count0 = count0.add(inventory0.getBalanceCount());
			}
			for(SellerProduct sellerProduct : sellerProducts){
				sellerProduct.set("store_count", count0);
				sellerProduct.set("modify_date", new Date());
				sellerProduct.update();
			}
		}
		if(flang==true){
			purchaseInstock.set("status", 1000);
			purchaseInstock.update();
		}
		
		
		renderAjaxResultForSuccess("OK");

	}
	
	
	public void pass(){
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String purchaseInstockId=getPara("id");
		boolean flang = false;
		final InventoryDetail inventoryDetail= getModel(InventoryDetail.class);
		Seller seller = SellerQuery.me().findByUserId(user.getId());
		PurchaseInstock purchaseInstock = PurchaseInstockQuery.me().findById(purchaseInstockId);
		List<PurchaseInstockDetail> list= PurchaseInstockDetailQuery.me().findAllByPurchaseInstockId(purchaseInstockId);
		for(PurchaseInstockDetail pi : list){
			String inventoryDetailId = StrKit.getRandomUUID();
			inventoryDetail.set("id", inventoryDetailId);
			inventoryDetail.set("warehouse_id", pi.get("warehouse_id"));
			inventoryDetail.set("sell_product_id",pi.getSellerProductId());
			inventoryDetail.set("in_count", pi.getProductCount());
			inventoryDetail.set("in_amount", pi.getProductAmount());
			inventoryDetail.set("in_price", pi.getProductPrice());
			inventoryDetail.set("biz_type", "采购入库");
			inventoryDetail.set("biz_bill_sn", pi.get("pwarehouse_sn"));
			inventoryDetail.set("biz_date", new Date());
			inventoryDetail.set("biz_user_id", user.getId());
			inventoryDetail.set("remark", pi.getRemark());
			inventoryDetail.set("dept_id",pi.getDeptId());
			inventoryDetail.set("data_area", pi.getDataArea());
			inventoryDetail.set("create_date", new Date());
			flang=inventoryDetail.save();
			if(flang==false){
				break;
			}
			
			Inventory inventory= InventoryQuery.me().findByWarehouseIdAndProductId(pi.get("warehouse_id").toString(), pi.getSellerProductId());
			if(inventory!=null){
				inventory.set("in_count", new BigDecimal(pi.getProductCount()));
				inventory.set("in_amount", pi.getProductAmount());
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", inventory.getBalanceCount().add(new BigDecimal(pi.getProductCount())));
				inventory.set("balance_amount", inventory.getBalanceAmount().add(pi.getProductAmount()));
				inventory.set("modify_date", new Date());
				flang=inventory.update();
				if(flang==false){
					break;
				}
			}else{
				inventory=new Inventory();
				inventory.set("id", StrKit.getRandomUUID());
				inventory.set("warehouse_id", pi.get("warehouse_id").toString());
				inventory.set("product_id", pi.getSellerProductId());
				inventory.set("seller_id", seller.getId());
				inventory.set("in_count", new BigDecimal(pi.getProductCount()));
				inventory.set("in_amount",pi.getProductAmount());
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", new BigDecimal(pi.getProductCount()));
				inventory.set("balance_amount", pi.getProductAmount());
				inventory.set("balance_price", pi.getProductPrice());
				inventory.set("data_area",pi.getDataArea());
				inventory.set("dept_id", pi.getDeptId());
				inventory.set("create_date", new Date());
				flang=inventory.save();
				if(flang==false){
					break;
				}
			}
			List<SellerProduct> sellerProducts = SellerProductQuery.me().findByProductIdAndSellerId(pi.getSellerProductId(),seller.getId());
			for(SellerProduct sellerProduct : sellerProducts){
				sellerProduct.set("store_count", inventory.getBalanceCount());
				sellerProduct.update();
			}
		}
		if(flang==true){
			purchaseInstock.set("status", 1000);
			purchaseInstock.update();
		}
		renderAjaxResultForSuccess("OK");

	}
	
	public void refund_instock(){
		String instockId = getPara("instockId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record instock = PurchaseInstockQuery.me().findMoreById(instockId,user.getDataArea());
		List<Record> instockDetail = PurchaseInstockDetailQuery.me().findByOutstockId(instockId,user.getDataArea());

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("instock", instock);
		result.put("instockDetail", instockDetail);
		renderJson(result);
	}
	
}
