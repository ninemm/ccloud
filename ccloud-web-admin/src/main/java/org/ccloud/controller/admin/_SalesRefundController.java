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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.User;
import org.ccloud.model.query.PayablesDetailQuery;
import org.ccloud.model.query.PayablesQuery;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SalesRefundInstockDetailQuery;
import org.ccloud.model.query.SalesRefundInstockQuery;
import org.ccloud.model.vo.SalesRefundExcel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesRefund", viewPath = "/WEB-INF/admin/sales_refund")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesRefund")
public class _SalesRefundController extends JBaseCRUDController<SalesRefundInstock> {

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
		//获取排序相关信息
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<Record> page = SalesRefundInstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate,
				endDate, null,null, dataArea,sort,order);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	public void detail() {

		String refundId = getPara(0);

		Record refund = SalesRefundInstockQuery.me().findMoreById(refundId);
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);

		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("detail.html");

	}

	public void add() {

		render("add.html");

	}

	public void outstockIndex() {

		render("outstock_index.html");

	}
	
	public void outstockDetail() {

		String outstockId = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);

		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("outstock_detail.html");

	}

	public void refund() {
		String outstockId = getPara("outstockId");

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("outstock", outstock);
		result.put("outstockDetail", outstockDetail);

		renderJson(result);
	}

	@Override
	@Before(Tx.class)
	public void save() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);		
		String deptId = StringUtils.getArrayFirst(paraMap.get("deptId"));
		String dataArea = StringUtils.getArrayFirst(paraMap.get("dataArea"));
		String outStockId = StringUtils.getArrayFirst(paraMap.get("outStockId"));

		String instockId = StrKit.getRandomUUID();
		Date date = new Date();
		String OrderSO = SalesRefundInstockQuery.me().getNewSn(sellerId);

		// SR + 100000(机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
		String instockSn = "SR" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
				+ StringUtils.getArrayFirst(paraMap.get("warehouseCode")) + DateUtils.format("yyMMdd", date) + OrderSO;

		SalesRefundInstockQuery.me().insert(paraMap, instockId, instockSn, sellerId, user.getId(), date, deptId,
				dataArea, outStockId);

		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;

		while (productNum > count) {
			index++;
			String sellProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
			if (StrKit.notBlank(sellProductId)) {
				SalesRefundInstockDetailQuery.me().insert(paraMap, instockId, sellerId, date, deptId, dataArea, index);

				count++;
			}

		}

		renderAjaxResultForSuccess();

	}
	
	@RequiresPermissions("/admin/salesRefund/check")
	public void pass() {

		String inStockId = getPara("inStockId");
		int status = getParaToInt("status");
		boolean isSave = this.passSalesRefund(inStockId, status);
		if (isSave) {
			renderAjaxResultForSuccess("审核成功");
		} else {
			renderAjaxResultForError("审核失败");
		}
	}
	
	public boolean passSalesRefund(final String inStockId,final int status) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	Date date = new Date();
            	User user=getSessionAttr(Consts.SESSION_LOGINED_USER);
            	int i = SalesRefundInstockQuery.me().updateConfirm(inStockId, status, date,user.getId());
            	if (i <= 0) {
            		return false;
            	}
            	if (status == 1000) {
            		Record refund = SalesRefundInstockQuery.me().findMoreById(inStockId);
            		String customerId = refund.getStr("customer_id");
            		String refundSn = refund.getStr("instock_sn");
            		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(inStockId);
            		if (!PayablesQuery.me().insert(refund, date)) {
            			return false;
            		}
            		for (Record record : refundDetail) {
    					if (!PayablesDetailQuery.me().insert(record, customerId, refundSn, date)) {
    						return false;
    					}
    				}
            	}
            	return true;
            }
        });
        return isSave;
	}
	
	//审核通过，对库存总账进行修改
		@Before(Tx.class)
		public void passAll(){
			String ds = getPara("orderItems");
			boolean result=false;
			JSONArray jsonArray = JSONArray.parseArray(ds);
			List<SalesRefundInstock> refunds = jsonArray.toJavaList(SalesRefundInstock.class);
			for(SalesRefundInstock instock : refunds){
				String inStockId = instock.getId();
				int status = instock.getStatus();
				result = this.passSalesRefund(inStockId, status);
				if(result == false){
					break;
				}
			}
			if (result) {
				renderAjaxResultForSuccess("审核成功");
				renderJson(result);
			} else {
				renderAjaxResultForError("审核失败");
				renderJson(result);
			}
		}
		@RequiresPermissions(value = { "/admin/salesRefund/downloading", "/admin/dealer/all",
		"/admin/all" }, logical = Logical.OR)
		public void downloading() {
			String startDate = getPara("startDate");
			String endDate = getPara("endDate");
			String keyword = getPara("k");
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\sales_outstock\\"
					+ "salesRefundInfo.xlsx";
			Page<Record> page = SalesRefundInstockQuery.me().paginate(1,Integer.MAX_VALUE, keyword, startDate,
					endDate, null,null, dataArea,null,null);
			List<Record> salesRefundList = page.getList();
			
			List<SalesRefundExcel> excellist = Lists.newArrayList();
			for (Record record : salesRefundList) {
			
				String refundId = record.get("id");
				List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);
				for(Record re : refundDetail){
					SalesRefundExcel excel = new SalesRefundExcel();
					//单位换算
					BigDecimal creatconverRelate = new BigDecimal(re.get("convert_relate").toString());
					//销售单价（大）
					BigDecimal bigPrice = new BigDecimal(re.get("product_price").toString());
					//销售数量
					int count = re.get("product_count");
					//退货数量
					int rCount = re.get("reject_product_count");
					int bigCount = count/(creatconverRelate.intValue());
					int smallCount = count%(creatconverRelate.intValue());
					int bigRefundCount = rCount/(creatconverRelate.intValue());
					int smallRefundCount = rCount%(creatconverRelate.intValue());
					//销售单价（小）
					BigDecimal smallPrice = bigPrice.divide(creatconverRelate, 2, BigDecimal.ROUND_HALF_UP);
					//退货单价
					BigDecimal refundBigPrice = re.get("reject_product_price");
					BigDecimal refundSmallPrice = refundBigPrice.divide(creatconverRelate, 2, BigDecimal.ROUND_HALF_UP);
					excel.setProductName(re.get("custom_name").toString());
					excel.setValueName(re.get("valueName").toString());
					excel.setBigPrice(bigPrice.toString());
					excel.setSmallPrice(smallPrice.toString());
					excel.setProductCount(bigCount);
					excel.setSmallCount(smallCount);
					excel.setBigRejectProductCount(bigRefundCount);
					excel.setSmallRejectProductCount(smallRefundCount);
					excel.setCreatconvertRelate(re.get("convert_relate").toString()+re.get("small_unit").toString()+"/"+re.get("big_unit").toString());
					excel.setBigPrice(re.get("product_price").toString());
					excel.setSmallCount(smallCount);
					excel.setSmallPrice(smallPrice.toString());
					excel.setBigRejectProductPrice(refundBigPrice.toString());
					excel.setSmallRejectProductPrice(refundSmallPrice.toString());
					excel.setProductAmount(re.get("product_amount").toString());
					excel.setRejectAmount(re.get("reject_amount").toString());
					excel.setInstockSn(record.get("instock_sn").toString());
					excel.setCustomer(record.get("customer_name").toString());
					excel.setCustomerType(record.get("customerTypeName").toString());
					excel.setContact(record.get("contact").toString());
					excel.setMobile(record.get("mobile").toString());
					if(record.get("realname")==null){
						excel.setBizUser("");
					}else{
						excel.setBizUser(record.get("realname").toString());
					}
					if(record.get("payment_type").toString().equals("0")){
						excel.setReceiveType("应收账款");
					}else{
						excel.setReceiveType("现金");
					}
					if(record.get("status").toString().equals("0")){
						excel.setStatus("退货单待审核");
					}else if(record.get("status").toString().equals("1000")) {
						excel.setStatus("退货单已审核");
					}else if(record.get("status").toString().equals("1001")) {
						excel.setStatus("退货单取消");
					}else if(record.get("status").toString().equals("2000")) {
						excel.setStatus("退货单部分入库");
					}else{
						excel.setStatus("退货单全部入库");
					}
					if(re.get("is_gift").toString().equals("0")){
						excel.setIsGift("否");
					}else{
						excel.setIsGift("是");
					}
					excel.setWarehouse(record.get("warehouseName").toString());
					excel.setCreateDate(record.get("create_date").toString());
					excellist.add(excel);
					
				}
			}
			
			ExportParams params = new ExportParams();
			Workbook wb = ExcelExportUtil.exportBigExcel(params, SalesRefundExcel.class, excellist);
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
		
		public void operateHistory() {
			keepPara();
			
			String id = getPara(0);

			Record salesRefund = SalesRefundInstockQuery.me()._findRecordById(id);
			setAttr("salesRefund", salesRefund);

			String salesRefundInfo = buildOutstockInfo(id);
			setAttr("salesRefundInfo", salesRefundInfo);
			
			render("operate_history.html");
		}
		
		private String buildOutstockInfo(String refundId) {
			List<Record> rfundDetails = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);
			
			StringBuilder stringBuilder = new StringBuilder();
			
			for (Record record : rfundDetails) { // 若修改了产品价格或数量，则写入相关日志信息
				if (!(record.get("reject_product_count").toString()).equals(record.getInt("product_count").toString())) {
						stringBuilder.append("●" + record.getStr("custom_name") + "<br>");
						int convert = record.getInt("convert_relate");
						stringBuilder.append("-" + record.getStr("big_unit") + "数量修改为"+ Math.round(record.getInt("reject_product_count")/convert) + "(" + Math.round(record.getInt("product_count")/convert) + ")<br>");
						stringBuilder.append("-" + record.getStr("small_unit") + "数量修改为"+ Math.round(record.getInt("reject_product_count")%convert) + "(" + Math.round(record.getInt("product_count")%convert) + ")<br>");
				}
			}
			
			return stringBuilder.toString();
		}
		
		public void initUser() {
			String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);		
			String startDate = getPara("startDate");
			String endDate = getPara("endDate");
			List<Map<String, Object>> userList = new ArrayList<>();
			List<Record> list = SalesOutstockQuery.me().findUserList(sellerId, startDate, endDate);
			for (Record record : list) {
				Map<String, Object> item = new HashMap<>();
				item.put("title", record.getStr("realname"));
				item.put("value", record.getStr("id"));
				userList.add(item);
			}		
			Map<String, Object> map = new HashMap<>();
			map.put("userList", userList);		
			renderJson(map);
		}
}
