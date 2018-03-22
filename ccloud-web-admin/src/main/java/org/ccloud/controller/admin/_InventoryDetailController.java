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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.StockTakingDetailQuery;
import org.ccloud.model.query.StockTakingQuery;
import org.ccloud.model.vo.OutDetailExcel;
import org.ccloud.model.vo.SalesOrderExcel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;

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
	// 入库明细
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
		Page<InventoryDetail> page = InventoryDetailQuery.me()._in_paginate(getPageNumber(), getPageSize(), keyword,
				sellerId, startDate, endDate, sellerProductId, sort, sortOrder);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}

	public void out() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("out.html");
	}

	public void outList() {
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
		Page<Record> page = InventoryDetailQuery.me()._out_paginate(getPageNumber(), getPageSize(), keyword, sellerId,
				startDate, endDate, sellerProductId, sort, sortOrder);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}

	public void show_sellerProductName() {
		String sellerId = getSessionAttr("sellerId");
		List<SellerProduct> lists = SellerProductQuery.me().findBySellerId(sellerId);
		renderJson(lists);
	}

	public void detailBySn() {
		String order_sn = getPara(0);
		Record order = StockTakingQuery.me().findBySn(order_sn);
		List<Record> orderDetail = StockTakingDetailQuery.me().findByStockTakingId(order.get("id").toString());
		setAttr("order", order);
		setAttr("orderDetail", orderDetail);
		render("detail.html");
	}

	public void downloadingOut() {
		String sellerId = getSessionAttr("sellerId");
		String keyword = getPara("k");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerProductId = getPara("sellerProductId");
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\plans\\"
				+ "出库明细.xls";
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		Page<Record> page = InventoryDetailQuery.me()._out_paginate(1, Integer.MAX_VALUE, keyword, sellerId, startDate,
				endDate, sellerProductId, null, null);
		List<Record> RecordList = page.getList();
		List<OutDetailExcel> excellist = Lists.newArrayList();
		for (Record record : RecordList) {
			if (record.getStr("biz_type").equals("100203")) {
				record.set("biz_type", "采购退货出库");
			} else if (record.getStr("biz_type").equals("100204")) {
				record.set("biz_type", "销售出库");
			} else if (record.getStr("biz_type").equals("100207")) {
				record.set("biz_type", "调拨出库");
			} else if (record.getStr("biz_type").equals("100209")) {
				record.set("biz_type", "盘亏出库");
			}
			OutDetailExcel outDetailExcel = new OutDetailExcel();
			outDetailExcel.setBiz_bill_sn(record.getStr("biz_bill_sn"));
			outDetailExcel.setRealname(record.getStr("realname"));
			outDetailExcel.setCustomer_name(record.getStr("customer_name"));
			outDetailExcel.setCc_warehouse(record.getStr("warehouseName"));
			outDetailExcel.setSellerName(record.getStr("sellerName"));
			outDetailExcel.setOut_count(record.getStr("out_count"));
			outDetailExcel.setOut_amount(record.getStr("out_amount"));
			outDetailExcel.setOut_price(record.getStr("out_price"));
			outDetailExcel.setBalance_count(record.getStr("balance_count"));
			outDetailExcel.setBiz_type(record.getStr("biz_type"));
			outDetailExcel.setCreate_date(record.getStr("create_date"));
			excellist.add(outDetailExcel);
		}
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, OutDetailExcel.class, excellist);
		File file = new File(filePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ExcelExportUtil.closeExportBigExcel();
		renderFile(new File(filePath));
	}
}
