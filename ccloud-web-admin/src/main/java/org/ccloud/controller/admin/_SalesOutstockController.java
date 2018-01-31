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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.SalesOutstockExcel;
import org.ccloud.model.vo.carSalesPrintNeedInfo;
import org.ccloud.model.vo.orderProductInfo;
import org.ccloud.model.vo.printAllNeedInfo;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
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
@RouterMapping(url = "/admin/salesOutstock", viewPath = "/WEB-INF/admin/sales_outstock")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SalesOutstockController extends JBaseCRUDController<SalesOrder> {

	@Override
	@RequiresPermissions(value = { "/admin/salesOutstock", "/admin/dealer/all",
			"/admin/salesRefund" }, logical = Logical.OR)
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
		String stockOutStatus = getPara("stockOutStatus");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String status = getPara("status");
		String salesmanId = getPara("salesman");//业务员Id
		// 获取排序相关信息
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");

		Page<Record> page = SalesOutstockQuery.me().paginate(getPageNumber(), getPageSize(), sellerId, keyword, startDate,
				endDate, printStatus, stockOutStatus, status, dataArea, order, sort,salesmanId);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@RequiresPermissions("/admin/salesOutstock")
	public void stockdetail() {

		String outstockId = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);
		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("detail.html");

	}

	@RequiresPermissions("/admin/salesOutstock")
	public void stockdetailBySn() {

		String outstockSn = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreBySn(outstockSn);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockSn(outstockSn);
		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("detail.html");

	}

	@RequiresPermissions("/admin/salesOutstock")
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

	public void renderPrintPage() {
		setAttr("outstockId", getPara(0));
		// 获取销售商的配置模板地址
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<PrintTemplate> printTemplates = PrintTemplateQuery.me().findPrintTemplateBySellerId(sellerId);
		if (printTemplates.size() == 0) {
			renderAjaxResultForError("请配置一个打印模板");
		} else {
			String url = printTemplates.get(0).getUrl();
			render(url + ".html");
		}

	}

	//业务员出库单汇总打印
	public void renderPrintAll() {
		setAttr("outstockId", getPara("stockOutId"));
		setAttr("userId", getPara("userId"));
		setAttr("beginDate", getPara("beginDate"));
		setAttr("endDate", getPara("endDate"));
		render("salesman.html");
	}

	@RequiresPermissions("/admin/salesOutstock")
	public void renderCarPrintPage() {
		setAttr("carWarehouseId", getPara("carWarehouseId"));
		setAttr("beginDate", getPara("beginDate"));
		setAttr("endDate", getPara("endDate"));

		render("carPrint.html");
	}

	// 获取出库单打印的信息
	public void getPrintInfo() {
		String outstockId = getPara("outstockId");
		String[] outId = outstockId.split(",");
		List<printAllNeedInfo> printAllNeedInfos = new ArrayList<>();
		for (String s : outId) {
			printAllNeedInfo printAllNeedInfo = SalesOutstockQuery.me().findStockOutForPrint(s);
			List<orderProductInfo> orderProductInfos = SalesOutstockDetailQuery.me().findPrintProductInfo(s);
			printAllNeedInfo.setOrderProductInfos(orderProductInfos);
			printAllNeedInfos.add(printAllNeedInfo);
		}
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("rows", printAllNeedInfos);
		renderJson(result);
	}

	public void queryCarStockDetail() {
		String carWarehouseId = getPara("carWarehouseId");
		String beginDate = getPara("beginDate");
		beginDate = beginDate + " 00:00:00";
		String endDate = getPara("endDate");
		endDate = endDate + " 23:59:59";
		List<carSalesPrintNeedInfo> carSalesPrintNeedInfos = SalesOutstockQuery.me()
				.getCarSalesPrintInfo(carWarehouseId, beginDate, endDate);
		HashMap<String, Object> result = Maps.newHashMap();
		result.put("rows", carSalesPrintNeedInfos);
		renderJson(result);
	}

	// 销售订货单出库
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

	// 销售订货单批量出库
	@RequiresPermissions("/admin/salesOutstock/check")
	public void batchStockOut() throws ParseException {
		String outstockId = getPara("outstockId");
		String[] outId = outstockId.split(",");
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
		// 检查批量出库提交上来单子是否有已出库的
		boolean isStockOut = this.checkIsStockOut(outId);
		if (!isStockOut) {
			renderAjaxResultForError("批量出库失败,单子已有出库，请检查！");
		} else {
			boolean isSave = this.saveBatchStockOut(outId, StockDate, remark, user, sellerId, sellerCode, date);
			if (isSave) {
				renderAjaxResultForSuccess("批量出库成功");
			} else {
				renderAjaxResultForError("批量出库失败!");
			}
		}
	}

	public void recordPrintInfo() {
		String outstockId = getPara("outstockId");
		String[] outId = outstockId.split(",");
		List<printAllNeedInfo> printAllNeedInfos = new ArrayList<>();
		for (String s : outId) {
			printAllNeedInfo printAllNeedInfo = SalesOutstockQuery.me().findStockOutForPrint(s);
			printAllNeedInfos.add(printAllNeedInfo);
			updateStockOutPrintStatus(printAllNeedInfo);
		}
		boolean saveOutStockPrint = saveOutStockPrint(printAllNeedInfos);
		if (saveOutStockPrint) {
			HashMap<String, Object> result = Maps.newHashMap();
			result.put("result", 200);
			renderJson(result);
		}

	}

	@Before(Tx.class)
	private void updateStockOutPrintStatus(printAllNeedInfo printAllNeedInfo) {
		SalesOutstockQuery.me().updatePrintStatus(printAllNeedInfo.getSalesOutStockId());
	}

	public boolean out(final Map<String, String[]> paraMap, final User user, final String sellerId,
			final String sellerCode) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				String order_user = StringUtils.getArrayFirst(paraMap.get("order_user"));
				String order_date = StringUtils.getArrayFirst(paraMap.get("order_date"));
				String deptId = StringUtils.getArrayFirst(paraMap.get("deptId"));
				String dataArea = StringUtils.getArrayFirst(paraMap.get("dataArea"));
				String outStockId = StringUtils.getArrayFirst(paraMap.get("salesStockId"));
				String outStockSN = StringUtils.getArrayFirst(paraMap.get("salesStockSN"));
				String wareHouseId = StringUtils.getArrayFirst(paraMap.get("wareHouseId"));
				String customerId = StringUtils.getArrayFirst(paraMap.get("customerId"));
				String sellerCustomerId = StringUtils.getArrayFirst(paraMap.get("sellerCustomerId"));
				Date date = new Date();
				String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
				Integer productNum = Integer.valueOf(productNumStr);
				String total = StringUtils.getArrayFirst(paraMap.get("total"));
				Integer count = 0;
				Integer index = 0;

				while (productNum > count) {
					index++;
					String sellProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
					if (StrKit.notBlank(sellProductId)) {
						if (!SalesOutstockDetailQuery.me().outStock(paraMap, sellerId, date, deptId, dataArea, index,
								user.getId(), outStockSN, wareHouseId, sellProductId, sellerCustomerId, order_user, order_date)) {
							return false;
						}
						count++;
					}

				}
				if (!SalesOrderQuery.me().checkStatus(outStockId, user.getId(), date, total)) {
					return false;
				}

				// 如果客户种类是直营商，则生成直营商的采购入库单
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

				String customerKind = StringUtils.getArrayFirst(paraMap.get("customerKind"));
				String sellerId = getSessionAttr("sellerId");
				if (Consts.CUSTOMER_KIND_SELLER.equals(customerKind)) {
					Record seller = SellerQuery.me().findByCustomerId(customerId);
					String purchaseInstockId = StrKit.getRandomUUID();
					SellerCustomer sellerCustomer = SellerCustomerQuery.me().findBySellerId(sellerId, customerId);
					// PS + 100000(机构编号或企业编号6位) + 20171108(时间) + 000001(流水号)
					String pwarehouseSn = "PS" + seller.getStr("seller_code") + DateUtils.format("yyyyMMdd", date)
							+ PurchaseInstockQuery.me().getNewSn();

					Warehouse warehouse = WarehouseQuery.me().findBySellerId(seller.getStr("id"));
					if (!PurchaseInstockQuery.me().insertBySalesOutStock(paraMap, seller, purchaseInstockId,
							pwarehouseSn, warehouse.getId(), user.getId(), date, sellerId)) {
						return false;
					}
					// 直营商的应付账款
					String countTotal = StringUtils.getArrayFirst(paraMap.get("total"));
					createPayables(sellerCustomer, countTotal,seller);
					count = 0;
					index = 0;
					while (productNum > count) {
						index++;
						String sellProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
						if (StrKit.notBlank(sellProductId)) {
							if (!PurchaseInstockDetailQuery.me().insertBySalesOrder(paraMap, purchaseInstockId, seller,
									index, date, getRequest(), pwarehouseSn, sellerCustomer)) {
								return false;
							}
							count++;
						}

					}

				}

				return true;
			}
		});
		return isSave;
	}

	private void createPayables(SellerCustomer sellerCustomer, String countAll,Record seller) {
		String payablesType = Consts.RECEIVABLES_OBJECT_TYPE_SUPPLIER;
		Payables payables = PayablesQuery.me().findByObjIdAndDeptId(sellerCustomer.getSellerId(), payablesType,
				seller.getStr("dept_id"));
		if (payables == null) {
			payables = new Payables();
			payables.setId(StrKit.getRandomUUID());
			payables.setObjId(sellerCustomer.getSellerId());
			payables.setObjType(payablesType);
			payables.setPayAmount(new BigDecimal(countAll));
			payables.setActAmount(new BigDecimal(0));
			payables.setBalanceAmount(new BigDecimal(countAll));
			payables.setDeptId(seller.getStr("dept_id"));
			payables.setDataArea(seller.getStr("data_area"));
			payables.setCreateDate(new Date());
			payables.save();
		} else {
			payables.setPayAmount(payables.getPayAmount().add(new BigDecimal(countAll)));
			payables.setBalanceAmount(payables.getBalanceAmount().add(new BigDecimal(countAll)));
			payables.setModifyDate(new Date());
			payables.update();
		}
	}

	// 把打印记录写到出库打印记录表里
	public boolean saveOutStockPrint(final List<printAllNeedInfo> printAllNeedInfos) {
		boolean isSave = Db.tx(new IAtom() {

			List<OutstockPrint> outstockPrints = new ArrayList<>();
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

			@Override
			public boolean run() throws SQLException {
				for (printAllNeedInfo printAllNeedInfo : printAllNeedInfos) {
					OutstockPrint outstockPrint = new OutstockPrint();
					outstockPrint.setId(StrKit.getRandomUUID());
					outstockPrint.setOrderId(printAllNeedInfo.getOrderId());
					outstockPrint.setBizUserId(printAllNeedInfo.getBizUserId());
					outstockPrint.setDeptId(user.getDepartmentId());
					outstockPrint.setDataArea(user.getDataArea());
					outstockPrint.setCreateDate(new Date());
					outstockPrint.setStatus(0);
					outstockPrints.add(outstockPrint);
				}
				try {
					Db.batchSave(outstockPrints, outstockPrints.size());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
		return isSave;
	}

	//批量出库  应收账款
	public boolean saveBatchStockOut(final String[] outId, final Date stockDate, final String remark, final User user,
			final String sellerId, String sellerCode, final Date date) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				for (String s : outId) {
					//获取打印单
					printAllNeedInfo printAllNeedInfo = SalesOutstockQuery.me().findStockOutForPrint(s);
					//获取订单明细
					List<orderProductInfo> orderProductInfos = SalesOutstockDetailQuery.me().findPrintProductInfo(s);
					
					SalesOutstock salesOutstock = SalesOutstockQuery.me().findById(s);
					//订单总金额
					BigDecimal productAmout=salesOutstock.getTotalAmount();
					
					String total=productAmout.toString();
					if (!SalesOutstockDetailQuery.me().batchOutStock(orderProductInfos, sellerId, date,
							user.getDepartmentId(), user.getDataArea(), user.getId(),
							printAllNeedInfo.getOutstockSn(),printAllNeedInfo.getCustomerId(),
							printAllNeedInfo.getBizUserId(),printAllNeedInfo.getPlaceOrderTime().toString())) {
						return false;
					}
					if (!SalesOutstockQuery.me().updateStockOutStatus(printAllNeedInfo.getSalesOutStockId(),
							user.getId(), stockDate, Consts.SALES_OUT_STOCK_STATUS_OUT, date, remark)
							| !SalesOrderQuery.me().checkStatus(printAllNeedInfo.getSalesOutStockId(), user.getId(),
									date, total)) {
						return false;
					}
					// 如果客户种类是直营商，则生成直营商的采购入库单
					if (Consts.CUSTOMER_KIND_SELLER.equals(printAllNeedInfo.getCustomerKind())) {
						Seller SellerCustomer = SellerQuery.me().findBySellerCustomerId(printAllNeedInfo.getCustomerId());
						Record seller = SellerQuery.me().findByCustomerId(SellerCustomer.getCustomerId());
						String purchaseInstockId = StrKit.getRandomUUID();

						// PS + 100000(机构编号或企业编号6位) + 20171108(时间) + 000001(流水号)
						String pwarehouseSn = "PS" + seller.getStr("seller_code") + DateUtils.format("yyMMdd", date)
								+ PurchaseInstockQuery.me().getNewSn();

						Warehouse warehouse = WarehouseQuery.me().findBySellerId(seller.getStr("id"));
						if (!PurchaseInstockQuery.me().insertByBatchSalesOutStock(printAllNeedInfo, seller,
								purchaseInstockId, pwarehouseSn, warehouse.getId(), user.getId(), date, sellerId)) {
							return false;
						}

						if (!PurchaseInstockDetailQuery.me().insertByBatchSalesOrder(orderProductInfos,
								purchaseInstockId, seller, date, getRequest())) {
							return false;
						}
					}
				}
				return true;
			}
		});
		return isSave;
	}

	// 获取车销仓库
	public void queryCarWarehouse() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Warehouse> carWarehouseList = WarehouseQuery.me().getCarWarehouseBySellerId(sellerId);
		renderAjaxResultForSuccess("success", JSON.toJSON(carWarehouseList));
	}

	// 检查前台提交的批量出库单子是否有已出库的
	public boolean checkIsStockOut(String[] outId) {
		boolean isStockOut = true;
		for (String s : outId) {
			SalesOutstock salesOutstock = SalesOutstockQuery.me().findById(s);
			if (salesOutstock.getStatus().toString().equals("1000")) {
				isStockOut = false;
			}
		}
		return isStockOut;
	}

	@RequiresPermissions(value = { "/admin/salesOutstock/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void download() {
		render("download.html");
	}

	@RequiresPermissions(value = { "/admin/salesOutstock/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {
		String keyword = new String(getPara("k").getBytes("ISO8859-1"), "UTF-8");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String printStatus = getPara("printStatus");
		String stockOutStatus = getPara("stockOutStatus");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\sales_outstock\\"
				+ "salesOutstockInfo.xlsx";

		Page<Record> page = SalesOutstockQuery.me().paginate(1, Integer.MAX_VALUE, sellerId, keyword, startDate, endDate,
				printStatus, stockOutStatus, null, dataArea, null, null,null);
		List<Record> salesOutstckList = page.getList();

		List<SalesOutstockExcel> excellist = Lists.newArrayList();
		for (Record record : salesOutstckList) {
			String outStockId = record.get("id");
			//客户信息
			String customerInfo = record.getStr("customer_name")+"," + record.get("prov_name")+record.get("city_name")+record.get("country_name")+record.get("address");
			//下单日期
			String saveDate =record.getStr("create_date").substring(0, 10); 
			//下单时间
			String createDate = record.getStr("create_date");
			List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outStockId);
			for (Record re : outstockDetail) {
				SalesOrder salesOrder = SalesOrderQuery.me().findOutOrderId(outStockId);
				//打印时间
				List<Record> outstockPrints = OutstockPrintQuery.me().findByOrderId(salesOrder.getId());
				String printDate = "";
				if(outstockPrints.size()>0) {
					printDate =(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(outstockPrints.get(0).get("create_date")) ;
				}
				BigDecimal creatconverRelate = new BigDecimal(re.getStr("convert_relate"));
				BigDecimal bigPrice = new BigDecimal(re.getStr("product_price"));
				BigDecimal count = new BigDecimal(re.getStr("product_count"));
				String bigCount = (count.intValue()) / (creatconverRelate.intValue()) + "";
				String smallCount = (count.intValue()) % (creatconverRelate.intValue()) + "";
				BigDecimal smallPrice = bigPrice.divide(creatconverRelate, 2, BigDecimal.ROUND_HALF_UP);
				if(!bigCount.equals("0")) {
					SalesOutstockExcel excel = new SalesOutstockExcel();
					excel = saveExcel(re,record,bigPrice,bigCount,customerInfo,saveDate,createDate,printDate,re.getStr("big_unit"));
					excellist.add(excel);
				}
				if(!smallCount.equals("0")){
					SalesOutstockExcel excel = new SalesOutstockExcel();
					excel = saveExcel(re,record,smallPrice,smallCount,customerInfo,saveDate,createDate,printDate,re.getStr("small_unit"));
					excellist.add(excel);
				}

			}
		}

		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, SalesOutstockExcel.class, excellist);
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
	
	public SalesOutstockExcel saveExcel(Record re,Record record,BigDecimal price,String count,String customerInfo,String saveDate,String createDate,String printDate,String unit) {
		SalesOutstockExcel excel = new SalesOutstockExcel();
		excel.setProductName(re.getStr("custom_name"));
		excel.setValueName(re.getStr("valueName"));
		excel.setProductCount(count);
		excel.setUnit(unit);
		excel.setCreatconvertRelate(re.getStr("convert_relate") + re.getStr("small_unit") + "/"
				+ re.getStr("big_unit"));
		excel.setProductPrice(price.toString());
		excel.setTotalAmount(price.multiply(new BigDecimal(count)).toString());
		excel.setOutstockSn(record.getStr("outstock_sn"));
		excel.setCustomer(customerInfo);
		excel.setCustomerType(record.getStr("customerName"));
		excel.setContact(record.getStr("contact"));
		excel.setMobile(record.getStr("mobile"));
		excel.setProductPrice(price.toString());
		excel.setSaveDate(saveDate);
		excel.setCreateDate(createDate);
		excel.setPrintDate(printDate);
		if (record.get("realname") == null) {
			excel.setBizUser("");
		} else {
			excel.setBizUser(record.getStr("realname"));
		}
		if (record.get("receive_type").toString().equals("0")) {
			excel.setReceiveType("应收账款");
		} else {
			excel.setReceiveType("现金");
		}
		if (record.get("is_print").toString().equals("0")) {
			excel.setIsPrint("未打印");
		} else {
			excel.setIsPrint("已打印");
		}
		if (record.get("status").toString().equals("0")) {
			excel.setStatus("待出库");
		} else {
			excel.setStatus("已出库");
		}
		if (re.get("is_gift").toString().equals("0")) {
			excel.setIsGift("否");
		} else {
			excel.setIsGift("是");
		}
		excel.setBarCode(re.getStr("bar_code"));
		excel.setCreateDate(record.getStr("create_date"));
		return excel;
	}
	
	//业务员汇总打印信息
	public void queryUserStockDetail() {
		String outstockId = getPara("outstockId");
		String[] outId = outstockId.split(",");
		String userId = getPara("userId");
		String beginDate = getPara("beginDate");
		beginDate = beginDate + " 00:00:00";
		String endDate = getPara("endDate");
		endDate = endDate + " 23:59:59";
	    
		List<Record> records = SalesOutstockQuery.me().getUserPrintInfo(outId, userId);
		renderJson(records);
	}
	
}
