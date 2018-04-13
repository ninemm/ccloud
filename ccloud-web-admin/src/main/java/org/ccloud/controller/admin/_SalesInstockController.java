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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.PrintTemplate;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.SalesRefundInstockDetail;
import org.ccloud.model.User;
import org.ccloud.model.query.PrintTemplateQuery;
import org.ccloud.model.query.SalesRefundInstockDetailQuery;
import org.ccloud.model.query.SalesRefundInstockQuery;
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
@RouterMapping(url = "/admin/salesInstock", viewPath = "/WEB-INF/admin/sales_instock")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesInstock")
public class _SalesInstockController extends JBaseCRUDController<SalesOrder> {

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
		String printStatus = getPara("printStatus");
		String stockInStatus = getPara("stockInStatus");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		//获取排序相关信息
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<Record> page = SalesRefundInstockQuery.me()._paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate, printStatus,stockInStatus,dataArea,sort,order);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}
	
	public void getDetail() {
		String id = getPara("id");
		setAttr("id", id);
		render("in_stock_detail.html");
	}	
	
	public void detail() {

		String refundId = getPara(0);

		Record refund = SalesRefundInstockQuery.me().findMoreById(refundId);
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);
		SalesRefundInstockDetail instock = SalesRefundInstockDetailQuery.me().findByInstockId(refundId);
		setAttr("rejectAmount", instock.getStr("rejectAmount"));
		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("detail.html");

	}
	
	public void inDetail() {

		String refundId = getPara("instockId");

		Record refund = SalesRefundInstockQuery.me().findMoreById(refundId);
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("refund", refund);
		result.put("refundDetail", refundDetail);

		renderJson(result);		

	}
	
	@RequiresPermissions("/admin/salesInstock/check")
	public void inStock() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");
		boolean isSave = this.in(paraMap, user, sellerId, sellerCode);
        if (isSave) {
        	renderAjaxResultForSuccess("入库成功");
        } else {
        	renderAjaxResultForError("入库失败!");
        }

	}
	
	public boolean in(final Map<String, String[]> paraMap, final User user, final String sellerId, final String sellerCode) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
	            String order_user = StringUtils.getArrayFirst(paraMap.get("order_user"));
	            String order_date = StringUtils.getArrayFirst(paraMap.get("order_date"));
        		String deptId = StringUtils.getArrayFirst(paraMap.get("deptId"));
        		String dataArea = StringUtils.getArrayFirst(paraMap.get("dataArea"));
        		String inStockId =  StringUtils.getArrayFirst(paraMap.get("salesStockId"));
        		String inStockSN =  StringUtils.getArrayFirst(paraMap.get("salesStockSN"));
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
        				if (!SalesRefundInstockQuery.me().inStock(paraMap, sellerId, 
        						date, deptId, dataArea, index, user.getId(), inStockSN, wareHouseId, sellProductId, order_user, order_date)) {
        					return false;
        				}
        				count++;
        			}

        		}
        		if (!SalesRefundInstockQuery.me().updateStatus(inStockId, date,user)) {
        			return false;
        		}
        		return true;
            }
        });
        return isSave;
	}	
	
	public void detailBySn(){
		String refundSn = getPara(0);
		SalesRefundInstock salesRefundInstock = SalesRefundInstockQuery.me().findBySn(refundSn);
		Record refund = SalesRefundInstockQuery.me().findMoreById(salesRefundInstock.getId());
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(salesRefundInstock.getId());
		SalesRefundInstockDetail instock = SalesRefundInstockDetailQuery.me().findByInstockId(salesRefundInstock.getId());
		setAttr("rejectAmount", instock.getStr("rejectAmount"));

		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("detail.html");
	}
	
	public void renderPrintPage() {
        setAttr("inStockId", getPara(0));
        //获取销售商的配置模板地址
        String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
        List<PrintTemplate> printTemplates = PrintTemplateQuery.me().findPrintTemplateBySellerId(sellerId);
        if (printTemplates.size() == 0) {
        	renderAjaxResultForError("请配置一个打印模板");
		}else {
			String url = printTemplates.get(0).getUrl();
			render(url + ".html");
		}
       
	}
	
	   //获取退货单打印的信息
		public void getPrintInfo() {
			String getPrintInfo = getPara("inStockId");
			String[] outId = getPrintInfo.split(",");
	        List<Map<String, Object>> printAllNeedInfos = new ArrayList<>();

			for (String s : outId) {
				Record printInfo = SalesRefundInstockQuery.me().findStockInForPrint(s);
				if (StrKit.isBlank(printInfo.getStr("salesOutStockId"))) {
					printInfo = SalesRefundInstockQuery.me().findStockInForPrintByNoOrder(s);
				}
				List<Record> ProductInfos = SalesRefundInstockQuery.me().findPrintProductInfo(s);
				Map<String, Object> map = new HashMap<>();
				map.put("printInfo", printInfo);
				map.put("ProductInfos", ProductInfos);
				printAllNeedInfos.add(map);
			}
			renderJson("rows",printAllNeedInfos);
		}
		
		
	public void recordPrintInfo() {
		String inStockId = getPara("inStockId");
		String[] id = inStockId.split(",");
		boolean isUpdate = false;
		for (final String s : id) {
			isUpdate = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					if (!SalesRefundInstockQuery.me().updatePrintStatus(s)) {
						return false;
					}
					return true;
				}
			});
		}
		if (isUpdate) {
			HashMap<String, Object> result = Maps.newHashMap();
			result.put("result", 200);
			renderJson(result);
		}
	}
	
	//退货单批量入库
	public void batchStockIn() throws ParseException {
		String inStockId = getPara("inStockId");
	    String[] inId = inStockId.split(",");
		String oStockDate = getPara("oStockDate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date StockDate = sdf.parse(oStockDate);
		String remark = getPara("remark");
		if (StrKit.notBlank(remark)) {
			remark = StringUtils.urlDecode(remark);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");
		Date date = new Date();
		//检查批量入库提交上来单子是否有已入库的
		boolean isStockIn = this.checkIsStockIn(inId);
		if (!isStockIn) {
		  renderAjaxResultForError("批量入库失败,单子已有入库，请检查！");
		}else {
			boolean isSave = this.saveBatchStockIn(inId,StockDate,remark,user,sellerId,sellerCode,date);
			 if (isSave) {
		        	renderAjaxResultForSuccess("批量入库成功");
		        } else {
		        	renderAjaxResultForError("批量入库失败!");
		      }	
		}
	}
	
	
     	//检查前台提交的批量出库单子是否有已出库的
		public boolean checkIsStockIn(String[] inId) {
			boolean isStockIn = true;
			for (String s : inId) {
				SalesRefundInstock salesRefundInstock = SalesRefundInstockQuery.me().findById(s);
				if (salesRefundInstock.getStatus().toString().equals(String.valueOf(Consts.SALES_REFUND_INSTOCK_PART_OUT)) || salesRefundInstock.getStatus().toString().equals(String.valueOf(Consts.SALES_REFUND_INSTOCK_ALL_OUT))) {
					isStockIn = false;
				}
			}
			return isStockIn;
		}
		
		
		public boolean saveBatchStockIn(final String[] inId, final Date stockDate, final String remark, final User user, final String sellerId, String sellerCode, final Date date) {
			boolean isSave =  Db.tx(new IAtom() {
				@Override
			  public boolean run() throws SQLException {
			  for (String s : inId) {
				Record salesRefundInstockRecord = SalesRefundInstockQuery.me().findStockInForPrint(s);
				List<Record> ProductInfos = SalesRefundInstockQuery.me().findPrintProductInfo(s);
				
				if (!SalesRefundInstockDetailQuery.me().batchInStock(ProductInfos, sellerId, 
						date, user.getDepartmentId(), user.getDataArea(), user.getId(), salesRefundInstockRecord.getStr("instock_sn"),
						salesRefundInstockRecord.getStr("biz_user_id"), salesRefundInstockRecord.getStr("order_date"))) {
					return false;
				}
				
				if (!SalesRefundInstockQuery.me().updateStockInStatus(salesRefundInstockRecord.getStr("salesRefundInstockId"), user.getId(), stockDate, Consts.SALES_REFUND_INSTOCK_ALL_OUT, date,remark)) {
						return false;	
					  }
				}
				return true;
			  }
		       });
			return isSave;
		}
		

}
