package org.ccloud.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/admin/report", viewPath = "/WEB-INF/admin/report")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
//报表模块
public class _reportController extends JBaseController {
	
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
			InventoryDetail inventoryDetail1 = InventoryDetailQuery.me().findBySellerProductId(sellProductId, warehouseId1);
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
	
	//我管理的业务员
	public void salesman() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("salesman.html");
	}
	
	//我管理的业务员list
	public void reportlist() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<SalesOrder> page = SalesOrderQuery.me().findByDataArea(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
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
	
	
}
