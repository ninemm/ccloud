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

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.User;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesOutstock", viewPath = "/WEB-INF/admin/sales_outstock")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesOutstock")
public class _SalesOutstockController extends JBaseCRUDController<SalesOrder> {

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
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Record> page = SalesOutstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate, dataArea);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void stockdetail() {

		String outstockId = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("detail.html");

	}	
	
	public void getDetail() {
		String id = getPara("id");
		setAttr("id", id);
		render("out_stock_detail.html");
	}
	
	public void detail() {

		String outstockId = getPara("outstockId");

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("outstock", outstock);
		result.put("outstockDetail", outstockDetail);

		renderJson(result);
	}
	
	@RequiresPermissions("/admin/salesOutstock/check")
	public void outStock() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");
		boolean isSave = this.out(paraMap, user, sellerId, sellerCode);
        if (isSave) {
        	renderAjaxResultForSuccess("出库成功");
        } else {
        	renderAjaxResultForError("出库失败!");
        }

	}	
	
	public boolean out(final Map<String, String[]> paraMap, final User user, final String sellerId, final String sellerCode) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		String deptId = StringUtils.getArrayFirst(paraMap.get("deptId"));
        		String dataArea = StringUtils.getArrayFirst(paraMap.get("dataArea"));
        		String outStockId =  StringUtils.getArrayFirst(paraMap.get("salesStockId"));
        		String outStockSN =  StringUtils.getArrayFirst(paraMap.get("salesStockSN"));
        		String wareHouseId =  StringUtils.getArrayFirst(paraMap.get("wareHouseId"));
        		Date date = new Date();
        		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
        		Integer productNum = Integer.valueOf(productNumStr);
        		Integer count = 0;
        		Integer index = 0;
        		
        		while (productNum > count) {
        			index++;
        			String sellProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
        			if (StrKit.notBlank(sellProductId)) {
        				if (!SalesOutstockDetailQuery.me().outStock(paraMap, sellerId, 
        						date, deptId, dataArea, index, user.getId(), outStockSN, wareHouseId, sellProductId)) {
        					return false;
        				}
        				count++;
        			}

        		}
        		if (!SalesOutstockQuery.me().updateStatus(outStockId, Consts.SALES_OUT_STOCK_STATUS_OUT, date) || 
        				!SalesOrderQuery.me().checkStatus(outStockId, date)) {
        			return false;
        		}
        		return true;
            }
        });
        return isSave;
	}

}
