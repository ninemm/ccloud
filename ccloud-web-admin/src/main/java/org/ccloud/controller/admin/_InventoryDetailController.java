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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.SellerProductQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/inventoryDetail", viewPath = "/WEB-INF/admin/inventory_detail")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _InventoryDetailController extends JBaseCRUDController<InventoryDetail> { 
	
	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		
		render("index.html");
	}
	@RequiresPermissions("/admin/salesOrder/check")
	public void list() {
		String sellerId = getSessionAttr("sellerId");
		String keyword = getPara("k");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerProductId = getPara("sellerProductId");
		String sort = getPara("sort");
		String sortOrder = getPara("sortOrder");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		Page<InventoryDetail> page = InventoryDetailQuery.me()._in_paginate(getPageNumber(), getPageSize(),keyword,sellerId,startDate, endDate,sellerProductId,sort,sortOrder);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void out(){
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("out.html");
	}
	
	public void outList(){
		String sellerId = getSessionAttr("sellerId");
        String keyword = getPara("k");
        String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerProductId = getPara("sellerProductId");
		String sort = getPara("sort");
		String sortOrder = getPara("sortOrder");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
		Page<InventoryDetail> page = InventoryDetailQuery.me()._out_paginate(getPageNumber(), getPageSize(),keyword,sellerId,startDate, endDate,sellerProductId,sort,sortOrder);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void show_sellerProductName(){
		String sellerId = getSessionAttr("sellerId");
		List<SellerProduct> lists = SellerProductQuery.me().findBySellerId(sellerId);
		renderJson(lists);
	}
}
