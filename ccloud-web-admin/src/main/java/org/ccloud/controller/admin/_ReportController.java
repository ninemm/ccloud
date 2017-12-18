package org.ccloud.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/admin/report", viewPath = "/WEB-INF/admin/report")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
//报表模块
public class _ReportController extends JBaseController {
	
	//库存详细
	public void inventoryDetail() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Warehouse> wlist = WarehouseQuery.me().findWarehouseByUserId(user.getId());
		setAttr("wlist", wlist);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("inventoryDetail.html");
	}
	
	//库存详细list
	public void inventoryDetailList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String warehouseId = getPara("warehouse_id");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<InventoryDetail> page = InventoryDetailQuery.me().findByDataArea(getPageNumber(), getPageSize(), keyword, "cid.create_date", startDate,endDate,dataArea,warehouseId);
		List<InventoryDetail> list = page.getList();
		List<InventoryDetail> list1=new ArrayList<>();
		for (InventoryDetail inventoryDetail : list) {
			String warehouseId1 = inventoryDetail.getWarehouseId();
			String sellProductId = inventoryDetail.getSellProductId();
			//得到查询时间段中最新的剩余商品件数
			InventoryDetail inventoryDetail1 = InventoryDetailQuery.me().findByInventoryDetail(sellProductId, warehouseId1,startDate,endDate);
			inventoryDetail.setBalanceCount(inventoryDetail1.getBalanceCount());
			list1.add(inventoryDetail);
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", list1);
		renderJson(map);
	}
	
	//库存详细 产品总计
	public void inventoryDetailListTotal() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String seller_id=getSessionAttr("sellerId").toString();
		Page<InventoryDetail> page = InventoryDetailQuery.me().findByInventoryDetailListTotal(getPageNumber(), getPageSize(), "i.create_date",dataArea,seller_id);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的客户
	public void customerDetails() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("customerDetails.html");
	}
	
	//我的客户list
	public void customerDetailsList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByCustomer(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的客户赠品list
	public void customerDetailsGiftList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByCustomerGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的客户类型报表
	public void clientTypeReport() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("clientTypeReport.html");
	}
	
	//我的客户类型报表list
	public void clientTypeReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByCustomerType(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的客户类型赠品list
	public void clientTypeReportGiftList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByCustomerTypeGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的产品详细
	public void productReport() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("productReport.html");
	}
	
	//我的产品详细list
	public void productReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByProduct(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的产品赠品list
	public void productReportGiftList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByProductGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门下的产品明细
	public void departProduct() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("departmentProductReport.html");
	}
	
	//我部门下的产品明细list
	public void departmentProductReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByDepartmentProduct(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门下的产品明细赠品list
	public void departmentProductGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByDepartmentProductGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的部门业务员
	public void departSalesman() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("departSalesman.html");
	}
	
	//我的部门业务员list
	public void departSalesmanReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByDepartSalesman(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我的部门业务员赠品list
	public void departSalesmanGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByDepartSalesmanGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	

	//我部门的直营商
	public void manageSeller() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("manageSeller.html");
	}
	
	//我部门的直营商list
	public void manageSellerReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByManageSeller(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我管理的直营商赠品list
	public void manageSellerGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByManageSellerGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的直营商详情
	public void mSellerDetail() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		if (startDate==null) {
			String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			startDate=date;
			endDate=date;
		}
		String keyword = getPara("k");
		//编码k 页面刷新是k不会乱码
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		Page<Record> page = SalesOrderQuery.me().findByMSellerDetail(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,sellerId);
		List<Record> list = page.getList();
		List<String>watchHead=new ArrayList<>();
		if (list.size()!=0) {
			String[] watchHead1 = list.get(0).getColumnNames();
			watchHead = Arrays.asList(watchHead1);
		}else {
			watchHead.add("直营商名称");
			List<SellerProduct> findBySellerId = SellerProductQuery.me().findBySellerId(sellerId);
			for (SellerProduct sellerProduct : findBySellerId) {
				String customName=sellerProduct.getCustomName();
				watchHead.add(customName);
			}
		}
		List<Object[]>mSellerDetailReportList=new ArrayList<>();
		for (Record record : list) {
			Object[] columnValues = record.getColumnValues();
			mSellerDetailReportList.add(columnValues);
		}
		setAttr("watchHead", watchHead);
		setAttr("mSellerDetailReportList", mSellerDetailReportList);
		Page<Record> page1 = SalesOrderQuery.me().findByMSellerDetailGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,sellerId);
		List<Record> list1 = page1.getList();
		List<Object[]>mSellerDetailGiftReportList=new ArrayList<>();
		for (Record record : list1) {
			Object[] columnValues = record.getColumnValues();
			mSellerDetailGiftReportList.add(columnValues);
		}
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("k", keyword);
		setAttr("mSellerDetailGiftReportList", mSellerDetailGiftReportList);
		render("mSellerDetail.html");
	}
	
	//我部门的业务员详情
	public void mSalesmanDetail() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		if (startDate==null) {
			String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
			startDate=date;
			endDate=date;
		}
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		Page<Record> page = SalesOrderQuery.me().findByMSalesmanDetail(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,sellerId);
		List<Record> list = page.getList();
		List<String>watchHead=new ArrayList<>();
		if (list.size()!=0) {
			String[] watchHead1 = list.get(0).getColumnNames();
			watchHead = Arrays.asList(watchHead1);
		}else {
			watchHead.add("业务员名称");
			List<SellerProduct> findBySellerId = SellerProductQuery.me().findBySellerId(sellerId);
			for (SellerProduct sellerProduct : findBySellerId) {
				String customName=sellerProduct.getCustomName();
				watchHead.add(customName);
			}
		}
		List<Object[]>mSalesmanDetailReportList=new ArrayList<>();
		for (Record record : list) {
			Object[] columnValues = record.getColumnValues();
			mSalesmanDetailReportList.add(columnValues);
		}
		setAttr("watchHead", watchHead);
		setAttr("mSalesmanDetailReportList", mSalesmanDetailReportList);
		Page<Record> page1 = SalesOrderQuery.me().findByMSalesmanDetailGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,sellerId);
		List<Record> list1 = page1.getList();
		List<Object[]>mSalesmanDetailGiftReportList=new ArrayList<>();
		for (Record record : list1) {
			Object[] columnValues = record.getColumnValues();
			mSalesmanDetailGiftReportList.add(columnValues);
		}
		setAttr("startDate", startDate);
		setAttr("endDate", endDate);
		setAttr("k", keyword);
		setAttr("mSalesmanDetailGiftReportList", mSalesmanDetailGiftReportList);
		render("mSalesmanDetail.html");
	}
	
	//经销商的直营商的采购
	public void purSeller() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("purSeller.html");
	}
	
	//经销商的直营商的采购list
	public void purSellerReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findBypurSeller(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//经销商的直营商的采购赠品list
	public void purSellerGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findBypurSellerGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
}
