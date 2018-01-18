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

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Inventory;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/inventory", viewPath = "/WEB-INF/admin/inventory")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _InventoryController extends JBaseCRUDController<Inventory> { 

	//库存总账  选择仓库
	public void getWarehouse() {
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		List<Record> list=new ArrayList<Record>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		if (isSuperAdmin) {
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			list = InventoryQuery.me().getWareHouse(dataArea,userId);
		}else {
			
			list = InventoryQuery.me().getWareHouseInfo(userId);
		}
		renderJson(list);
	}
	
	//库存总账
	public void list() {
		String warehouse_id = getPara("warehouse_id");
		String product_sn = getPara("product_sn");
		String product_name = getPara("product_name");
		if (StrKit.notBlank(product_sn)) {
			product_sn = StringUtils.urlDecode(product_sn);
			setAttr("product_sn", product_sn);
		}
		if (StrKit.notBlank(product_name)) {
			product_name = StringUtils.urlDecode(product_name);
			setAttr("product_name", product_name);
		}
		String seller_id= getSessionAttr("sellerId").toString();
		Map<String, Object> map;
		if(seller_id == null ||warehouse_id=="") {
			map = new HashMap<String, Object>();
		}else {
			Warehouse warehouse = WarehouseQuery.me().findById(warehouse_id);
			//判断仓库是不是自己的  是自己的的仓库查出此仓库所有商品
			Page<Inventory> page=new Page<>();
			if (seller_id.equals(warehouse.getSellerId())) {
				page = InventoryQuery.me().paginate(getPageNumber(), getPageSize(),product_sn,product_name,warehouse_id,null);
			}else {
				page = InventoryQuery.me().paginate(getPageNumber(), getPageSize(),product_sn,product_name,warehouse_id,seller_id);
			}
			map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		}
		
		renderJson(map);
	}
	
	//库存总账明细向页面跳转
	public void renderlist() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		setAttr("warehouse_id", getPara("warehouse_id"));
		setAttr("product_id", getPara("product_id"));
		setAttr("seller_id", getPara("seller_id"));
		render("detaillist.html");
	}
	
	//库存总账明细
	public void detaillist() {
		String warehouse_id = getPara("warehouse_id");
		String product_id = getPara("product_id");
		
		String start_date = getPara("startDate");
		String end_date = getPara("endDate");
		String seller_id= getSessionAttr("sellerId").toString();
		Warehouse warehouse = WarehouseQuery.me().findById(warehouse_id);
		//判断仓库是不是自己的  是自己的的仓库查出此仓库所有商品
		Page<InventoryDetail> page=new Page<>();
		if (seller_id.equals(warehouse.getSellerId())) {
			page = InventoryDetailQuery.me().paginate(getPageNumber(), getPageSize(),warehouse_id,product_id,null,start_date,end_date);
		}else {
			page = InventoryDetailQuery.me().paginate(getPageNumber(), getPageSize(),warehouse_id,product_id,seller_id,start_date,end_date);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
}
