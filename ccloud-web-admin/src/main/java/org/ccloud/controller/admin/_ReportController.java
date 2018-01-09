package org.ccloud.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.UserQuery;
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
		
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		List<Warehouse> wlist= new ArrayList<>();
		String user_id = user.getId();
		//判断登录的人是不是经销商管理员
		if (isSuperAdmin) {
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			wlist = WarehouseQuery.me().findWarehouseByDataArea(user_id,dataArea);
		}else {
			wlist = WarehouseQuery.me().findWarehouseByUserId(user.getId());
		}
		
		setAttr("wlist", wlist);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("inventoryDetail.html");
	}
	
	//库存详细list
	public void inventoryDetailList() {
		String startDate = getPara("startDate");
		String warehouseId = getPara("warehouse_id");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<InventoryDetail> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page = InventoryDetailQuery.me().findByDataArea(1, Integer.MAX_VALUE,dataArea,warehouseId,sort,order);
		}else {
			page = InventoryDetailQuery.me().findByDataArea(getPageNumber(), getPageSize(),dataArea,warehouseId,sort,order);
		}
		List<InventoryDetail> list = page.getList();
		List<InventoryDetail> list1=new ArrayList<>();
		for (InventoryDetail inventoryDetail : list) {
			String warehouseId1 = inventoryDetail.getWarehouseId();
			String sellProductId = inventoryDetail.getSellProductId();
			//得到查询时间段中最新的剩余商品件数
			InventoryDetail inventoryDetail1 = InventoryDetailQuery.me().findByInventoryDetail(sellProductId, warehouseId1,endDate);
			inventoryDetail.setBalanceCount(inventoryDetail1.getBalanceCount());
			Record findByInventoryDetail2 = InventoryDetailQuery.me().findByInventoryDetail1(sellProductId, warehouseId1,startDate,endDate);
			BigDecimal out_count = findByInventoryDetail2.getBigDecimal("out_count");
			if (null==out_count) {
				out_count=new BigDecimal("0");
			}
			BigDecimal in_count = findByInventoryDetail2.getBigDecimal("in_count");
			if (null==in_count) {
				in_count=new BigDecimal("0");
			}
			inventoryDetail.setInCount(in_count);
			inventoryDetail.setOutCount(out_count);
			list1.add(inventoryDetail);
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", list1);
		renderJson(map);
	}
	
	//产品总计
	public void productTotal() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Seller> slist = SellerQuery.me().findByDataArea(dataArea);
		setAttr("slist", slist);
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("productTotal.html");
	}
	
	//产品总计
	public void productTotalList() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String seller_id=getSessionAttr("sellerId").toString();
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		String sellerId = getPara("seller_id");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<InventoryDetail> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page = InventoryDetailQuery.me().findByInventoryDetailListTotal(1, Integer.MAX_VALUE,dataArea,seller_id,sort,order,sellerId);
		}else {
			page = InventoryDetailQuery.me().findByInventoryDetailListTotal(getPageNumber(), getPageSize(),dataArea,seller_id,sort,order,sellerId);
		}
		List<InventoryDetail> list = page.getList();
		List<InventoryDetail> list1=new ArrayList<>();
		for (InventoryDetail inventoryDetail : list) {
			String sellProductId = inventoryDetail.getSellProductId();
			//得到查询时间段中最新的剩余商品件数
			InventoryDetail inventoryDetail1 = InventoryDetailQuery.me().findByInventoryDetail2(sellProductId,endDate);
			inventoryDetail.setBalanceCount(inventoryDetail1.getBalanceCount());
			Record findByInventoryDetail2 = InventoryDetailQuery.me().findByInventoryDetail3(sellProductId,startDate,endDate);
			BigDecimal out_count = findByInventoryDetail2.getBigDecimal("out_count");
			if (null==out_count) {
				out_count=new BigDecimal("0");
			}
			BigDecimal in_count = findByInventoryDetail2.getBigDecimal("in_count");
			if (null==in_count) {
				in_count=new BigDecimal("0");
			}
			inventoryDetail.setInCount(in_count);
			inventoryDetail.setOutCount(out_count);
			list1.add(inventoryDetail);
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", list1);
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByCustomerType(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId);
		}else {
			page = SalesOrderQuery.me().findByCustomerType(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		}
		
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
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByCustomerTypeGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId);
		}else {
			page = SalesOrderQuery.me().findByCustomerTypeGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		}
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByProduct(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId);
		}else {
			page = SalesOrderQuery.me().findByProduct(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByProductGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId);
		}else {
			page = SalesOrderQuery.me().findByProductGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartmentProduct(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByDepartmentProduct(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartmentProductGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByDepartmentProductGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartSalesman(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByDepartSalesman(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartSalesmanGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByDepartSalesmanGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByManageSeller(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByManageSeller(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByManageSellerGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findByManageSellerGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
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
		
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findBypurSeller(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findBypurSeller(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
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
		Page<SalesOrder> page=new Page<>();

		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findBypurSellerGift(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea);
		}else {
			page = SalesOrderQuery.me().findBypurSellerGift(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的业务员详情
	public void mSalesmanDetail() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//得到表头
		List<String>watchHead=new ArrayList<>();
		watchHead.add("业务员名称");
		List<SellerProduct> findBySellerId = SellerProductQuery.me().findBySellerId(sellerId);
		for (SellerProduct sellerProduct : findBySellerId) {
			String customName=sellerProduct.getCustomName();
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("mSalesmanDetail.html");
	}
	
	//我部门的业务员详情
	public void mSalesmanDetailReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		List<Record> list = SalesOrderQuery.me().findByMSalesmanDetail(startDate,endDate,keyword, dataArea,sellerId);
		renderJson(list);
	}
	
	//我部门的业务员赠品详情
	public void mSalesmanDetailGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		List<Record> list = SalesOrderQuery.me().findByMSalesmanDetailGift(startDate,endDate,keyword, dataArea,sellerId);
		renderJson(list);
	}
	
	//我部门的直营商详情
	public void mSellerDetail() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//得到表头
		List<String>watchHead=new ArrayList<>();
		watchHead.add("直营商名称");
		List<SellerProduct> findBySellerId = SellerProductQuery.me().findBySellerId(sellerId);
		for (SellerProduct sellerProduct : findBySellerId) {
			String customName=sellerProduct.getCustomName();
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("mSellerDetail.html");
	}
	
	//我部门的直营商详情
	public void mSellerDetailReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		List<Record> list = SalesOrderQuery.me().findByMSellerDetail(startDate,endDate,keyword, dataArea,sellerId);
		renderJson(list);
	}
	
	//我部门的直营商赠品详情
	public void mSellerDetailGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString(); 
		List<Record> list = SalesOrderQuery.me().findByMSellerDetailGift(startDate,endDate,keyword, dataArea,sellerId);
		renderJson(list);
	}
	
	
	//我的客户详情
	public void customerDetails() {
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/manager");
		if (isSuperAdmin) {
			String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
			List<User> ulist = UserQuery.me().findByDataAreaSalesman(dataArea);
			setAttr("ulist", ulist);
		}
		
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//得到表头
		List<String>watchHead=new ArrayList<>();
		watchHead.add("客户名称");
		List<SellerProduct> findBySellerId = SellerProductQuery.me().findBySellerId(sellerId);
		for (SellerProduct sellerProduct : findBySellerId) {
			String customName=sellerProduct.getCustomName();
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("customerDetails.html");
	}
	
	//我的客户详情
	public void customerDetailsReportList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		//判断有没有user_id传过来
		String user_id = getPara("user_id");
		if (StrKit.notBlank(user_id)) {
			userId = StringUtils.urlDecode(user_id);
			setAttr("userId", userId);
		}
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//我的客户卖出商品详情
		List<Record> list = SalesOrderQuery.me().findByCustomerDetail(startDate,endDate,keyword, userId,sellerId);
		renderJson(list);
	}
	
	//我部门的业务员赠品商品详情
	public void customerDetailsGiftReportList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();

		//判断有没有user_id传过来
		String user_id = getPara("user_id");
		if (StrKit.notBlank(user_id)) {
			userId = StringUtils.urlDecode(user_id);
			setAttr("userId", userId);
		}
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		List<Record> list = SalesOrderQuery.me().findByCustomerDetailGift(startDate,endDate,keyword, userId,sellerId);
		renderJson(list);
	}
	
}
