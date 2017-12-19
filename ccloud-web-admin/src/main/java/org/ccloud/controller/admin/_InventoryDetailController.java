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

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.query.InventoryDetailQuery;

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
		render("index.html");
	}
	@RequiresPermissions("/admin/salesOrder/check")
	public void list() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr("sellerId");
		String stock_taking_sn = getPara("stock_taking_sn");
		String name = getPara("name");
		if (StrKit.notBlank(stock_taking_sn)) {
			stock_taking_sn = StringUtils.urlDecode(stock_taking_sn);
			setAttr("stock_taking_sn", stock_taking_sn);
		}
		if (StrKit.notBlank(name)) {
			name = StringUtils.urlDecode(name);
			setAttr("name", name);
		}
		Page<InventoryDetail> page = InventoryDetailQuery.me()._in_paginate(getPageNumber(), getPageSize(),name,stock_taking_sn,"cid.create_date",sellerId,dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void out(){
		render("out.html");
	}
	
	public void outList(){
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
		Page<InventoryDetail> page = InventoryDetailQuery.me()._out_paginate(getPageNumber(), getPageSize(),keyword,"cid.create_date",sellerId,dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
}
