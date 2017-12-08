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
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Inventory;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.PurchaseInstock;
import org.ccloud.model.PurchaseInstockDetail;
import org.ccloud.model.PurchaseRefundOutstock;
import org.ccloud.model.PurchaseRefundOutstockDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.PurchaseInstockDetailQuery;
import org.ccloud.model.query.PurchaseInstockQuery;
import org.ccloud.model.query.PurchaseRefundOutstockDetailQuery;
import org.ccloud.model.query.PurchaseRefundOutstockQuery;
import org.ccloud.model.query.SellerProductQuery;

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
@RouterMapping(url = "/admin/purchaseRefundOutstock", viewPath = "/WEB-INF/admin/purchase_refund_outstock")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PurchaseRefundOutstockController extends JBaseCRUDController<PurchaseRefundOutstock> { 

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

		Page<Record> page = PurchaseRefundOutstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate,
				endDate,user.getId(),user.getDataArea());

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void add() {
		render("add.html");
	}
	
	public void outstockIndex() {
		render("outstock_index.html");
	}
	
	public void outstockDetail() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String outstockId = getPara(0);

		Record outstock = PurchaseInstockQuery.me().findMoreById(outstockId,user.getDataArea());
		List<Record> outstockDetail = PurchaseInstockDetailQuery.me().findByOutstockId(outstockId,user.getDataArea());

		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("outstock_detail.html");

	}

	public void refund() {
		String outstockId = getPara("outstockId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record outstock = PurchaseInstockQuery.me().findMoreById(outstockId,user.getDataArea());
		List<Record> outstockDetail = PurchaseInstockDetailQuery.me().findByOutstockId(outstockId,user.getDataArea());

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("outstock", outstock);
		result.put("outstockDetail", outstockDetail);

		renderJson(result);
	}
	
	@Override
	@Before(Tx.class)
	public void save() {
		final PurchaseRefundOutstock purchaseRefundOutstock = getModel(PurchaseRefundOutstock.class);
		final PurchaseRefundOutstockDetail purchaseRefundOutstockDetail = getModel(PurchaseRefundOutstockDetail.class);
		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String purchaseInstockId = StringUtils.getArrayFirst(paraMap.get("purchaseInstockId"));
		String orderId = StrKit.getRandomUUID();
		Date date = new Date();
		PurchaseInstock purchaseInstock=PurchaseInstockQuery.me().findById(purchaseInstockId);
		//采购退货单： PR + 100000(机构编号或企业编号6位) + 20171108(时间) + 100001(流水号)
		List<PurchaseRefundOutstock> list =PurchaseRefundOutstockQuery.me().findByUser(user.getId(),user.getDataArea());
		int i = list.size();
		i++;
		String j=Integer.toString(i);
		int countt =j.length();
		for(int m=0;m<(6-countt);m++){
			j= "0"+j;
		}
		String orderSn = "PR" + user.getDepartmentId().substring(0, 6) + DateUtils.format("yyMMdd", date) + j;

		purchaseRefundOutstock.set("id", orderId);
		purchaseRefundOutstock.set("outstock_sn", orderSn);
		purchaseRefundOutstock.set("supplier_id", StringUtils.getArrayFirst(paraMap.get("supplierId")));
		purchaseRefundOutstock.set("warehouse_id", StringUtils.getArrayFirst(paraMap.get("warehouseId")));
		purchaseRefundOutstock.set("biz_user_id", user.getId());
		purchaseRefundOutstock.set("warehouse_in_id", purchaseInstockId);
		purchaseRefundOutstock.set("biz_date", date);
		purchaseRefundOutstock.set("input_user_id", user.getId());
		purchaseRefundOutstock.set("status", 0);
		purchaseRefundOutstock.set("status", 0);// 待审核
		purchaseRefundOutstock.set("total_reject_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		purchaseRefundOutstock.set("receive_type", StringUtils.getArrayFirst(paraMap.get("receiveType")));
		purchaseRefundOutstock.set("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseRefundOutstock.set("create_date", date);
		purchaseRefundOutstock.set("dept_id", user.getDepartmentId());
		purchaseRefundOutstock.set("data_area", user.getDataArea());
		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;

		while (productNum > count) {
			index++;
			String purchaseInstockDetailId = StringUtils.getArrayFirst(paraMap.get("purchaseInstockDetailId" + index));
			PurchaseInstockDetail purchaseInstockDetail = PurchaseInstockDetailQuery.me().findById(purchaseInstockDetailId);
			if (StrKit.notBlank(purchaseInstockDetailId)) {
				purchaseRefundOutstockDetail.set("id", StrKit.getRandomUUID());
				purchaseRefundOutstockDetail.set("purchase_refund_outstock_id", orderId);
				purchaseRefundOutstockDetail.set("seller_product_id", StringUtils.getArrayFirst(paraMap.get("sellerProductId" + index)));

				String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
				String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
				String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));

				Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);
				
				if(purchaseInstockDetail.getProductCount()<productCount){
					renderAjaxResultForError("出库的货物数量不可大于原订单的入库货物数量，请重新输入！");
					return;
				}
				
				purchaseRefundOutstockDetail.set("product_count", productCount);
				purchaseRefundOutstockDetail.set("product_price", StringUtils.getArrayFirst(paraMap.get("bigPrice" + index)));

				purchaseRefundOutstockDetail.set("product_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
				purchaseRefundOutstockDetail.set("cost", StringUtils.getArrayFirst(paraMap.get("smallPrice" + index)));
				purchaseRefundOutstockDetail.set("total_cost", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
				purchaseRefundOutstockDetail.set("purchase_instock_detail_id", StringUtils.getArrayFirst(paraMap.get("purchaseInstockDetailId" + index)));
				purchaseRefundOutstockDetail.set("reject_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
				purchaseRefundOutstockDetail.set("reject_product_count", productCount);
				purchaseRefundOutstockDetail.set("order_list", index);
				purchaseRefundOutstockDetail.set("create_date", date);
				purchaseRefundOutstockDetail.set("dept_id", user.getDepartmentId());
				purchaseRefundOutstockDetail.set("data_area", user.getDataArea());
				purchaseRefundOutstockDetail.save();
				count++;
			}

		}
		purchaseRefundOutstock.save();
		purchaseInstock.set("status", 1000);
		purchaseInstock.update();
		renderAjaxResultForSuccess("OK");

	}
	
	
	public void detail() {

		String refundId = getPara(0);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Record refund = PurchaseRefundOutstockQuery.me().findMoreById(refundId,user.getDataArea());
		List<Record> refundDetail = PurchaseRefundOutstockDetailQuery.me().findByRefundId(refundId,user.getDataArea());

		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("detail.html");

	}
	
	public void pass(){
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String purchaseRefundId=getPara("id");
		boolean flang = false;
		final InventoryDetail inventoryDetail= getModel(InventoryDetail.class);
		PurchaseRefundOutstock purchaseRefundOutstock = PurchaseRefundOutstockQuery.me().findById(purchaseRefundId);
		List<PurchaseRefundOutstockDetail> list= PurchaseRefundOutstockDetailQuery.me().findAllByPurchaseRefundId(purchaseRefundId,user.getDataArea());
		for(PurchaseRefundOutstockDetail pr : list){
			BigDecimal count1 = pr.getProductCount();
			BigDecimal convent = new BigDecimal(pr.get("convert_relate").toString());
			SellerProduct sellerProduct = SellerProductQuery.me().findById(pr.getSellerProductId());
			Inventory inventory= InventoryQuery.me().findByWarehouseIdAndProductId(pr.get("warehouse_id").toString(),sellerProduct.getProductId() );
			inventory.set("out_count", count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
			inventory.set("out_amount", pr.getProductAmount());
			inventory.set("out_price", pr.getProductPrice());
			inventory.set("balance_count", inventory.getBalanceCount().subtract(count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP)));
			inventory.set("balance_amount", inventory.getBalanceAmount().subtract(pr.getProductAmount()));
			inventory.set("modify_date", new Date());
			flang=inventory.update();
			if(flang==false){
				break;
			}
			String inventoryDetailId = StrKit.getRandomUUID();
			inventoryDetail.set("id", inventoryDetailId);
			inventoryDetail.set("warehouse_id", pr.get("warehouse_id"));
			inventoryDetail.set("sell_product_id",pr.getSellerProductId());
			inventoryDetail.set("out_count", count1.divide(convent, 2, BigDecimal.ROUND_HALF_UP));
			inventoryDetail.set("out_amount", pr.getProductAmount());
			inventoryDetail.set("out_price", pr.getProductPrice());
			inventoryDetail.set("balance_count", inventory.getBalanceCount());
			inventoryDetail.set("balance_amount", inventory.getBalanceAmount());
			inventoryDetail.set("balance_price", inventory.getBalancePrice());
			inventoryDetail.set("biz_type", "100203");
			inventoryDetail.set("biz_bill_sn", pr.get("outstock_sn"));
			inventoryDetail.set("biz_date", new Date());
			inventoryDetail.set("biz_user_id", user.getId());
			inventoryDetail.set("remark", pr.getRemark());
			inventoryDetail.set("dept_id",pr.getDeptId());
			inventoryDetail.set("data_area", pr.getDataArea());
			inventoryDetail.set("create_date", new Date());
			flang=inventoryDetail.save();
			if(flang==false){
				break;
			}
			
			List<Inventory> inventorys = InventoryQuery.me()._findBySellerIdAndProductId(inventory.getSellerId(),inventory.getProductId());
			BigDecimal count0 = new BigDecimal(0);
			for(Inventory inventory0:inventorys){
				count0 = count0.add(inventory0.getBalanceCount());
			}
			sellerProduct.set("store_count", count0);
			sellerProduct.set("modify_date", new Date());
			sellerProduct.update();
		}
		if(flang==true){
			purchaseRefundOutstock.set("status", 1000);
			purchaseRefundOutstock.update();
		}
		renderAjaxResultForSuccess("OK");

	}
}
