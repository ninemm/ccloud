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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.ccloud.model.PurchaseOrderDetail;
import org.ccloud.model.PurchaseRefundOutstock;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.PurchaseInstockDetailQuery;
import org.ccloud.model.query.PurchaseInstockQuery;
import org.ccloud.model.query.PurchaseOrderDetailQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.PurchaseRefundOutstockQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.vo.PurchaseInstockDetailInfo;
import org.ccloud.model.vo.PurchaseSeller;
import org.ccloud.model.vo.SellerProductInfo;

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
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> page = PurchaseInstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate,user.getId(),dataArea);

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
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String p = "";
		List<PurchaseRefundOutstock> list = PurchaseRefundOutstockQuery.me().findByUser(user.getId(), user.getDataArea());
		if(list.size()>0){	
			String pro = "";
			for(int i=0;i<list.size();i++){
				pro += "'"+list.get(i).getWarehouseInId()+"',"; 
			}
			p = pro.substring(0, pro.length()-1);
		}else{
			p = "''";
		}
		Page<Record> page = PurchaseInstockQuery.me().paginateO(getPageNumber(), getPageSize(), keyword, startDate, endDate,user.getId(),user.getDataArea(),p);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	public void detail() {
		String instockId = getPara(0);

		setAttr("instockId", instockId);
		render("detail.html");

	}
	public void instockDetail() {
		String instockId = getPara(0);

		setAttr("instockId", instockId);
		render("getDetail.html");

	}
	//通过采购订单查看入库详情
	/*public void instock_detail(){
		String orderId = getPara(0);
		String instockId = PurchaseInstockDetailQuery.me().findOrderId(orderId).getPurchaseInstockId();
		setAttr("instockId", instockId);
		render("getDetail.html");
	}*/
	
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
		purchaseInstock.set("status", 1000);//已通过
		purchaseInstock.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		purchaseInstock.set("remark",  StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseInstock.set("dept_id", user.getDepartmentId());
		purchaseInstock.set("data_area", user.getDataArea());
		purchaseInstock.set("modify_date", new Date());
		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;
		BigDecimal totalAmount = new BigDecimal(0);
		Set<String> set = new HashSet<String>();
		for(int i = 1;i<=productNum;i++){
			String purchaseOederDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseOrderDetailId"+i));
			set.add(purchaseOederDetailId);
		}
		
		for(String pid : set){
			int productCount = 0;
			for(int j = 1; j<=productNum;j++){
				String purchaseOederDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseOrderDetailId"+j));
				String convert = StringUtils.getArrayFirst(paraMap.get("convert" + j));
				String bN = StringUtils.getArrayFirst(paraMap.get("bN" + j));
				String sN = StringUtils.getArrayFirst(paraMap.get("sN" + j));
				Integer productCount0 = Integer.valueOf(bN) * Integer.valueOf(convert) + Integer.valueOf(sN);
				if(purchaseOederDetailId.equals(pid)){
					productCount += productCount0;
				}else{
					break;
				}
			}
			PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetailQuery.me().findById(pid);
			if(productCount!=purchaseOrderDetail.getProductCount()){
				renderAjaxResultForError("商品数量输入有误，请核对后重新输入！");
				return;
			}
		}
		
		while (productNum > count) {
			index++;
			String sellerProductId = StringUtils.getArrayFirst(paraMap.get("sellerProductId"+index));
			for(int i = 0;i<instockDetails.size();i++){
				if(sellerProductId.equals(instockDetails.get(i).getSellerProductId())){
						details.remove(instockDetails.get(i));
				}
			}
			PurchaseInstockDetail purchaseInstockDetail = PurchaseInstockDetailQuery.me().findByPSId(purchaseInstockId,sellerProductId);
			PurchaseOrderDetail purchaseOrderDetail = PurchaseOrderDetailQuery.me().findById(purchaseInstockDetail.getPurchaseOrderDetailId());
			PurchaseOrder purchaseOrder = PurchaseOrderQuery.me().findById(purchaseOrderDetail.getPurchaseOrderId());
			if (StrKit.notBlank(sellerProductId)) {
				String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
				String bN = StringUtils.getArrayFirst(paraMap.get("bN" + index));
				String sN = StringUtils.getArrayFirst(paraMap.get("sN" + index));
				Integer productCount0 = Integer.valueOf(bN) * Integer.valueOf(convert) + Integer.valueOf(sN);
				BigDecimal productAmount =  purchaseOrderDetail.getProductPrice().multiply(new BigDecimal(bN)).add((purchaseOrderDetail.getProductPrice().divide(new BigDecimal(convert))).multiply(new BigDecimal(sN)));
				purchaseInstockDetail.set("product_count", productCount0);
				purchaseInstockDetail.set("product_amount", productAmount);
				purchaseInstockDetail.set("modify_date", new Date());
				purchaseInstockDetail.update();
				purchaseOrder.set("status", 4000);
				purchaseOrder.update();
				count++;
				totalAmount = totalAmount.add(productAmount);
			}
		}
		for(PurchaseInstockDetail d: details){
			d.delete();
		}
		purchaseInstock.set("total_amount",totalAmount);
		purchaseInstock.update();
		
		//对库存总账进行修改
		boolean flang = false;
		final InventoryDetail inventoryDetail= getModel(InventoryDetail.class);
		Seller seller = SellerQuery.me().findById(getSessionAttr("sellerId").toString());
		List<PurchaseInstockDetail> list= PurchaseInstockDetailQuery.me().findAllByPurchaseInstockId(purchaseInstockId);
		for(PurchaseInstockDetail pi : list){
			BigDecimal count2 = new BigDecimal(pi.getProductCount());
			BigDecimal convent = new BigDecimal(pi.get("convert_relate").toString()); 
			Inventory inventory= InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(seller.getId(), pi.get("productId").toString(), pi.get("warehouse_id").toString());
			if(inventory!=null){
				inventory.set("in_count", inventory.getInCount().add(count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP)));
				inventory.set("in_amount",inventory.getInAmount().add(pi.getProductAmount()));
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", inventory.getBalanceCount().add(count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP)));
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
				inventory.set("product_id", pi.get("productId"));
				inventory.set("seller_id", seller.getId());
				inventory.set("in_count", count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
				inventory.set("in_amount",pi.getProductAmount());
				inventory.set("in_price", pi.getProductPrice());
				inventory.set("balance_count", count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
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
			SellerProduct sellerProduct = SellerProductQuery.me().findById(pi.getSellerProductId());
			String inventoryDetailId = StrKit.getRandomUUID();
			BigDecimal  storeCount = new BigDecimal(0);
			if(sellerProduct.getStoreCount()==null){
				storeCount = count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP);
			}else{
				storeCount = sellerProduct.getStoreCount().add(count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
			}
			inventoryDetail.set("id", inventoryDetailId);
			inventoryDetail.set("warehouse_id", pi.get("warehouse_id"));
			inventoryDetail.set("sell_product_id",pi.getSellerProductId());
			inventoryDetail.set("in_count", count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
			inventoryDetail.set("in_amount", pi.getProductAmount());
			inventoryDetail.set("in_price", pi.getProductPrice());
			inventoryDetail.set("balance_count",storeCount );
			inventoryDetail.set("balance_amount", storeCount.multiply(pi.getProductPrice()));
			inventoryDetail.set("balance_price", pi.getProductPrice());
			inventoryDetail.set("biz_type", Consts.BIZ_TYPE_INSTOCK);
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
			if(sellerProduct.getStoreCount()==null)
			{
				sellerProduct.setStoreCount(new BigDecimal(0));
				sellerProduct.update();
			}
			BigDecimal count0 = count2.divide(convent, 2, BigDecimal.ROUND_HALF_UP);
			sellerProduct.setStoreCount(count0.add(sellerProduct.getStoreCount()));
			sellerProduct.set("modify_date", new Date());
			sellerProduct.update();
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
		List<SellerProductInfo> sProduct = new ArrayList<>(); 
		List<String> ls = new ArrayList<>();
		SellerProductInfo sellerProductInfo = new SellerProductInfo();
		for (Record record : instockDetail) {
			String id = record.get("purchase_order_detail_id").toString();
			if (ls.contains(id)){
				this.addChild(sProduct, id,record);
				continue;
			}
			sellerProductInfo = new SellerProductInfo();
			sellerProductInfo.setPurchaseInstockDetailId(record.get("id").toString());
			sellerProductInfo.setPurchaseOrderDetailId(record.get("purchase_order_detail_id").toString());
			ls.add(sellerProductInfo.getPurchaseOrderDetailId());
			sellerProductInfo.setWarehouseId(record.get("warehouse_id").toString());
			sellerProductInfo.setProductName(record.get("productName").toString());
			sellerProductInfo.setBigUnit(record.get("big_unit").toString());
			sellerProductInfo.setSmallUnit(record.get("small_unit").toString());
			sellerProductInfo.setProductCount(record.get("product_count").toString());
			sellerProductInfo.setConvertRelate(record.get("convert_relate").toString());
			sellerProductInfo.setCpsName(record.get("cps_name").toString());
			List<SellerProduct> product = new ArrayList<>();
			SellerProduct sellerProduct = new SellerProduct();
			sellerProduct.setId(record.get("seller_product_id").toString());
			sellerProduct.setCustomName(record.get("custom_name").toString());
			sellerProduct.setStoreCount(record.getBigDecimal("storeCount"));
			product.add(sellerProduct);
			sellerProductInfo.setList(product);
			sProduct.add(sellerProductInfo);
		}
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("instock", instock);
		result.put("instockDetail", sProduct);
		renderJson(result);
	}

	private void addChild(List<SellerProductInfo> sproduct, String id, Record record) {
		for (SellerProductInfo sellerProductInfo : sproduct) {
			if (sellerProductInfo.getPurchaseOrderDetailId().equals(id)) {
				List<SellerProduct> product = sellerProductInfo.getList();
				SellerProduct sellerProduct = new SellerProduct();
				sellerProduct.setId(record.get("seller_product_id").toString());
				sellerProduct.setCustomName(record.get("custom_name").toString());
				sellerProduct.setStoreCount(record.getBigDecimal("storeCount"));
				product.add(sellerProduct);
				sellerProductInfo.setList(product);
			}
		}
	}
	
	public void refund_instock_etail(){
		String instockId = getPara("instockId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record instock = PurchaseInstockQuery.me().findMoreById(instockId,user.getDataArea());
		List<Record> instockDetail = PurchaseInstockDetailQuery.me().findByOutstockId(instockId,user.getDataArea());
		List<PurchaseInstockDetailInfo> sProduct = new ArrayList<>(); 
		List<String> ls = new ArrayList<>();
		PurchaseInstockDetailInfo purchaseInstockDetailInfo = new PurchaseInstockDetailInfo();
		for (Record record : instockDetail) {
			String id = record.get("purchase_order_detail_id").toString();
			if (ls.contains(id)){
				this._addChild(sProduct, id,record);
				continue;
			}
			purchaseInstockDetailInfo = new PurchaseInstockDetailInfo();
			purchaseInstockDetailInfo.setPurchaseInstockDetailId(record.get("id").toString());
			purchaseInstockDetailInfo.setPurchaseOrderDetailId(record.get("purchase_order_detail_id").toString());
			ls.add(purchaseInstockDetailInfo.getPurchaseOrderDetailId());
			purchaseInstockDetailInfo.setWarehouseId(record.get("warehouse_id").toString());
			purchaseInstockDetailInfo.setProductName(record.get("productName").toString());
			purchaseInstockDetailInfo.setBigUnit(record.get("big_unit").toString());
			purchaseInstockDetailInfo.setSmallUnit(record.get("small_unit").toString());
			purchaseInstockDetailInfo.setProductCount(record.get("product_count").toString());
			purchaseInstockDetailInfo.setConvertRelate(record.get("convert_relate").toString());
			purchaseInstockDetailInfo.setCpsName(record.get("cps_name").toString());
			List<PurchaseSeller> product = new ArrayList<>();
			PurchaseSeller purchaseSeller = new PurchaseSeller();
			purchaseSeller.setSellerProductId(record.get("seller_product_id").toString());
			purchaseSeller.setCustomName(record.get("custom_name").toString());
			purchaseSeller.setPrivateCount(Integer.parseInt(record.get("product_count").toString()));
			product.add(purchaseSeller);
			purchaseInstockDetailInfo.setList(product);
			sProduct.add(purchaseInstockDetailInfo);
		}
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("instock", instock);
		result.put("instockDetail", sProduct);
		renderJson(result);
	}
	
	private void _addChild(List<PurchaseInstockDetailInfo> sproduct, String id, Record record) {
		for (PurchaseInstockDetailInfo purchaseInstockDetailInfo : sproduct) {
			if (purchaseInstockDetailInfo.getPurchaseOrderDetailId().equals(id)) {
				List<PurchaseSeller> product = purchaseInstockDetailInfo.getList();
				PurchaseSeller purchaseSeller = new PurchaseSeller();
				purchaseSeller.setSellerProductId(record.get("seller_product_id").toString());
				purchaseSeller.setCustomName(record.get("custom_name").toString());
				purchaseSeller.setPrivateCount(Integer.parseInt(record.get("product_count").toString()));
				product.add(purchaseSeller);
				purchaseInstockDetailInfo.setList(product);
			}
		}
	}
}
