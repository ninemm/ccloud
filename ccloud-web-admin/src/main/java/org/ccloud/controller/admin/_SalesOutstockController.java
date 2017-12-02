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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Inventory;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SalesOutstock;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
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

		Page<Record> page = SalesOutstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void detail() {

		String outstockId = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("detail.html");

	}
	
	public void out() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	Map<String, String[]> map = getParaMap();
        		String outstockId = getPara("outstockSn");
//        		SalesOutstock outstock = SalesOutstockQuery.me().findById(outstockId);
//        		SalesOrder salesOrder = SalesOrderQuery.me().findById(outstockId);
//        		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_ALL_OUT);
//        		outstock.setStatus(Consts.SALES_OUT_STOCK_STATUS_OUT);
//        		if (!outstock.update()) {
//        			return false;
//        		}
//        		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
//        		for (Record record : outstockDetail) {
//        			Inventory inventory = InventoryQuery.me().
//        					findBySellerIdAndProductId(record.getStr("seller_id"), record.getStr("product_id"));
//        			inventory.set
//				}
        		
        		return true;
            }
        });
        if (isSave) {
        	renderAjaxResultForSuccess("出库成功");
        } else {
        	renderAjaxResultForError("出库失败!");
        }
        
	}

}
