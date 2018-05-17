package org.ccloud.controller.admin;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.Seller;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.*;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.joda.time.DateTime;


@RouterMapping(url = "/admin/report", viewPath = "/WEB-INF/admin/report")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
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
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<InventoryDetail> page=new Page<>();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		String user_id = user.getId();
		boolean admin;
		//判断登录的人是不是经销商管理员
		if (isSuperAdmin) {
			admin=true;
		}else {
			admin=false;
		}
		if(null==getPara("sortName[offset]")) {
			page = InventoryDetailQuery.me().findByDataArea(1, Integer.MAX_VALUE,dataArea,warehouseId,sort,order,startDate,endDate,user_id,admin);
		}else {
			page = InventoryDetailQuery.me().findByDataArea(getPageNumber(), getPageSize(),dataArea,warehouseId,sort,order,startDate,endDate,user_id,admin);
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		String sellerId = getPara("seller_id");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		String user_id = user.getId();
		boolean admin;
		//判断登录的人是不是经销商管理员
		if (isSuperAdmin) {
			admin=true;
		}else {
			admin=false;
		}
		Page<InventoryDetail> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page = InventoryDetailQuery.me().findByInventoryDetailListTotal(1, Integer.MAX_VALUE,dataArea,sort,order,sellerId,startDate,endDate,user_id,admin);
		}else {
			page = InventoryDetailQuery.me().findByInventoryDetailListTotal(getPageNumber(), getPageSize(),dataArea,sort,order,sellerId,startDate,endDate,user_id,admin);
		}
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByCustomerType1(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findByCustomerType1(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId,false,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByCustomerType1(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findByCustomerType1(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId,true,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByProduct1(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findByProduct1(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId,false,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByProduct1(1, Integer.MAX_VALUE,startDate,endDate,keyword, userId,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findByProduct1(getPageNumber(), getPageSize(),startDate,endDate,keyword, userId,true,sort,order);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的产品详细
	public void departProduct() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("departmentProductReport.html");
	}
	
	//我部门的产品详细list
	public void departmentProductReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartmentProduct1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findByDepartmentProduct1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,false,sort,order);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的产品详细赠品list
	public void departmentProductGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartmentProduct1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findByDepartmentProduct1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,true,sort,order);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的业务员
	public void departSalesman() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		render("departSalesman.html");
	}
	
	//我部门的业务员list
	public void departSalesmanReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartSalesman1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findByDepartSalesman1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,false,sort,order);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的业务员赠品list
	public void departSalesmanGiftReportList() {
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByDepartSalesman1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findByDepartSalesman1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,true,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByManageSeller1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findByManageSeller1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,false,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findByManageSeller1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findByManageSeller1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,true,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		Page<SalesOrder> page=new Page<>();
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findBypurSeller1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,false,sort,order);
		}else {
			page = SalesOrderQuery.me().findBypurSeller1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,false,sort,order);
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
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		if(null==getPara("sortName[offset]")) {
			page =SalesOrderQuery.me().findBypurSeller1(1, Integer.MAX_VALUE,startDate,endDate,keyword, dataArea,true,sort,order);
		}else {
			page = SalesOrderQuery.me().findBypurSeller1(getPageNumber(), getPageSize(),startDate,endDate,keyword, dataArea,true,sort,order);
		}
		
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//我部门的业务员详细
	public void mSalesmanDetail() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//得到表头
		List<String> watchHead = new ArrayList<>();
		watchHead.add("业务员名称");
		watchHead.add("销售额(元)");
		List<Record> findBySellerId = SellerProductQuery.me().findCustomNameBySellerId(sellerId);
		for (int i = 0; i < findBySellerId.size(); i++) {
			String customName = findBySellerId.get(i).getStr("custom_name");
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("mSalesmanDetail.html");
	}
	
	//我部门的业务员详细
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
		List<Record> list = SalesOrderQuery.me().findByMSalesmanDetail1(startDate,endDate,keyword, dataArea,sellerId,false);
		for (Record record : list) {
			String userId=record.getStr("userId");
			Record record1= SalesOrderQuery.me().findTotalAmountByUserId(startDate,endDate,keyword,userId);
			record.set("销售额(元)", record1.get("totalAmount"));
		}
		renderJson(list);
	}
	
	//我部门的业务员赠品详细
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
		List<Record> list = SalesOrderQuery.me().findByMSalesmanDetail1(startDate,endDate,keyword, dataArea,sellerId,true);
		renderJson(list);
	}
	
	//我部门的直营商详细
	public void mSellerDetail() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//得到表头
		List<String>watchHead=new ArrayList<>();
		watchHead.add("直营商名称");
		watchHead.add("销售额(元)");
		List<Record> findBySellerId = SellerProductQuery.me().findCustomNameBySellerId1(sellerId);
		for (int i = 0; i < findBySellerId.size(); i++) {
			String customName = findBySellerId.get(i).getStr("custom_name");
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("mSellerDetail.html");
	}
	
	//我部门的直营商详细
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
		List<Record> list = SalesOrderQuery.me().findByMSellerDetail1(startDate,endDate,keyword, dataArea,sellerId,false);
		List<Record> list1=SalesOrderQuery.me().findSellerMoney(startDate,endDate,keyword, dataArea, sellerId);
		for (Record record : list) {
			String seller_Id=record.getStr("id");
			for (Record record1 : list1) {
				String sellerId1=record1.getStr("seller_id");
				if (seller_Id.equals(sellerId1)) {
					record.set("销售额(元)", record1.get("totalAmount"));
					break;
				}
			}
		}
		renderJson(list);
	}
	
	//我部门的直营商赠品详细
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
		List<Record> list = SalesOrderQuery.me().findByMSellerDetail1(startDate,endDate,keyword, dataArea,sellerId,true);
		renderJson(list);
	}
	
	//我的客户详细
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
		watchHead.add("销售额(元)");
		List<Record> findBySellerId = SellerProductQuery.me().findCustomNameBySellerId(sellerId);
		for (int i = 0; i < findBySellerId.size(); i++) {
			String customName = findBySellerId.get(i).getStr("custom_name");
			watchHead.add(customName);
		}
		setAttr("watchHead", watchHead);
		render("customerDetails.html");
	}
	
	//我的客户详细
	public void customerDetailsReportList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
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
		String customerName = getPara("customerName");
		if (StrKit.notBlank(customerName)) {
			customerName = StringUtils.urlDecode(customerName);
			setAttr("customerName", customerName);
		}
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		//我的客户卖出商品详情
		List<Record> list = SalesOrderQuery.me().findByCustomerDetail1(startDate,endDate,keyword, userId,sellerId,false,dataArea, customerName);
		List<Record> list1=SalesOrderQuery.me().findMoney(startDate,endDate,keyword, userId, sellerId, dataArea, customerName);
		for (Record record : list) {
			String customerId=record.getStr("id");
			for (Record record1 : list1) {
				String customerId1=record1.getStr("customer_id");
				if (customerId.equals(customerId1)) {
					record.set("销售额(元)", record1.get("totalAmount"));
					break;
				}
			}
		}
		renderJson(list);
	}
	
//	我的客户详细赠品list
	public void customerDetailsGiftReportList() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
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
		String customerName = getPara("customerName");
		if (StrKit.notBlank(customerName)) {
			customerName = StringUtils.urlDecode(customerName);
			setAttr("customerName", customerName);
		}		
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID).toString();
		List<Record> list = SalesOrderQuery.me().findByCustomerDetail1(startDate,endDate,keyword, userId,sellerId,true,dataArea, customerName);
		renderJson(list);
	}

	@RequiresPermissions(value = {"/admin/report/productCoverage", "/admin/dealer/all"})
	public void productCoverage() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		DateTime dateTime = new DateTime();
		setAttr("startDate", DateFormatUtils.format(dateTime.withDayOfMonth(1).toDate(), "yyyy-MM-dd"));
		setAttr("endDate", date);

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Seller> sellers = new ArrayList<>();
		if (StrKit.notBlank(sellerId)) {
			Seller seller = SellerQuery.me().findById(sellerId);
			Subject subject = SecurityUtils.getSubject();
			if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
				sellers = SellerQuery.me().findByDataArea(seller.getStr("data_area") + "%");
			} else {
				sellers = SellerQuery.me().findByDataArea(seller.getStr("data_area"));
			}
		}
		setAttr("sellers", sellers);

		render("productCoverage.html");
	}

	public void productCoverageList() {
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sort = getPara("sortName[sort]");
		String order = getPara("sortName[order]");
		String sellerId = StrKit.isBlank(getPara("sellerId")) ? (String) getSessionAttr(Consts.SESSION_SELLER_ID) : getPara("sellerId");

		long customerCount = SellerCustomerQuery.me()._findBySellerId(sellerId);
		Page<Record> page = SalesOutstockQuery.me().cusCntBySellproduct(getPageNumber(), getPageSize(), customerCount, sellerId, startDate, endDate, sort, order);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());

		renderJson(map);
	}
}
