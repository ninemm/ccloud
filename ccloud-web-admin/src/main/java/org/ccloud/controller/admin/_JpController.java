/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.controller.admin;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.shiro.util.CollectionUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Brand;
import org.ccloud.model.Department;
import org.ccloud.model.Goods;
import org.ccloud.model.GoodsCategory;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Product;
import org.ccloud.model.PurchaseOrder;
import org.ccloud.model.PurchaseOrderDetail;
import org.ccloud.model.Seller;
import org.ccloud.model.SellerSynchronize;
import org.ccloud.model.Supplier;
import org.ccloud.model.User;
import org.ccloud.model.query.BrandQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GoodsCategoryQuery;
import org.ccloud.model.query.GoodsQuery;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.PurchaseOrderQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.SellerSynchronizeQuery;
import org.ccloud.model.query.SupplierQuery;
import org.ccloud.model.vo.remote.jp.pull.JpGoodsCategoryResponseEntity;
import org.ccloud.model.vo.remote.jp.pull.JpProductResponseEntity;
import org.ccloud.model.vo.remote.jp.pull.JpPurchaseStockInDetailResponse;
import org.ccloud.model.vo.remote.jp.pull.JpPurchaseStockInMainResponse;
import org.ccloud.model.vo.remote.jp.pull.JpPurchaseStockInRequestBody;
import org.ccloud.model.vo.remote.jp.pull.JpPurchaseStockInResponseBody;
import org.ccloud.model.vo.remote.jp.pull.JpSellerAccountResponseEntity;
import org.ccloud.model.vo.remote.jp.pull.JpSellerResponseEntity;
import org.ccloud.model.vo.remote.jp.pull.TypeConst;
import org.ccloud.remote.jp.pull.http.JpHttpClientExecute;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.GsonUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import comparator.SellerDeptComparator;

/**
 * 劲牌
 * @author wally
 *
 */
@RouterMapping(url = "/admin/jp/info/synchronize", viewPath = "/WEB-INF/admin/jp")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _JpController extends JBaseCRUDController<Goods> {
	private String httpUrl;
	private String wsUrl;
	private Map<String, String> headers;
	private Map<String, String> params;
	private static final String BRAND_CODE = "B001";
	private Brand brand;
	private Supplier supplier;
	public _JpController() {
		PropKit.use("ccloud.properties");
		httpUrl = PropKit.get("jp.api.httpclient.url");
		wsUrl = PropKit.get("jp.api.ws.url");
		if(headers == null) {
			headers = new HashMap<String, String>();
			headers.put("imClientCode", PropKit.get("jp.api.imClientCode"));
			headers.put("imSecretKey", PropKit.get("jp.api.imSecretKey"));
			headers.put("ContentType", PropKit.get("jp.api.ContentType"));
		}
		if(params == null) {
			params = new HashMap<String, String>();
		}
		if(brand == null) {
			brand = BrandQuery.me().findByCode(BRAND_CODE);
		}
		
		if (supplier == null) {
			supplier = SupplierQuery.me().findById(brand.getSupplierId());
		}
	}
	
	@Override
	public void index() {
		render("index.html");
	}
	
	/**
	 * 拉取经销商信息
	 */
	public void pullSellers() {
		Calendar calendar = Calendar.getInstance();
		String apiName = PropKit.get("jp.api.httpclient.sellers");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		params.put("clientCode", PropKit.get("jp.api.httpclient.sellers.clientCode"));
		params.put("DealerCode", "");
		params.put("type", "GetFile");
		String result = null;
		try {
			result = JpHttpClientExecute.executeGet(requestUrl, params, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StrKit.notBlank(result) &&  result.contains("\"code\"")) {
			renderAjaxResultForError("劲牌接口异常");
			return;
		}
		
		List<JpSellerResponseEntity> sellers = GsonUtils.deserializeList(result, TypeConst.JP_SELLER_RESPONSE_ENTITY_LIST_TYPE);
		
		if(!CollectionUtils.isEmpty(sellers)) {
			List<SellerSynchronize> sellerSynchronizes = new ArrayList<SellerSynchronize>();
			SellerSynchronize sellerSynchronize = null;
			JpSellerResponseEntity sellerResponseEntity = null;
			SellerSynchronize storedSeller = null;
			for (Iterator<JpSellerResponseEntity> iterator = sellers.iterator(); iterator.hasNext();) {
				sellerResponseEntity = iterator.next();
				storedSeller = SellerSynchronizeQuery.me().findByCode(sellerResponseEntity.getDealerCode());
				if(storedSeller != null)
					continue;
				sellerSynchronize = new SellerSynchronize();
				sellerSynchronize.setId(StrKit.getRandomUUID());
				sellerSynchronize.setBrandCode(BRAND_CODE);
				sellerSynchronize.setSellerCode(sellerResponseEntity.getDealerCode());
				sellerSynchronize.setSellerName(sellerResponseEntity.getDealerName().trim());
				sellerSynchronize.setProvName(sellerResponseEntity.getProvinceName().trim());
				sellerSynchronize.setCityName(sellerResponseEntity.getCityName().trim());
				sellerSynchronize.setCountryName(sellerResponseEntity.getCountyName());
				sellerSynchronize.setSellerType(0);
				sellerSynchronize.setHasStore(0);
				sellerSynchronize.setCreateDate(calendar.getTime());
				sellerSynchronize.setModifyDate(calendar.getTime());
				sellerSynchronizes.add(sellerSynchronize);
			}
			Db.batchSave(sellerSynchronizes, sellerSynchronizes.size());
		}
		renderAjaxResultForSuccess();
	}
	
	/**
	 * 拉取经销商账号信息
	 */
	public void pullSellerAccounts() {
		// 查询出劲牌所有父级经销商信息
		List<SellerSynchronize> brandSellers = SellerSynchronizeQuery.me().findParentSellersByBrandCode(BRAND_CODE);
		if(CollectionUtils.isEmpty(brandSellers)) {
			renderAjaxResultForError("未同步经销商信息");
			return;
		}
		
		String apiName = PropKit.get("jp.api.httpclient.sellerAccounts");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		
		params.put("clientCode", PropKit.get("jp.api.httpclient.sellerAccounts.clientCode"));
		SellerSynchronize parentSeller = null;
		List<SellerSynchronize> needAddList = new ArrayList<SellerSynchronize>();
		// 循环所有父级经销商信息
		for (Iterator<SellerSynchronize> iterator = brandSellers.iterator(); iterator.hasNext();) {
			parentSeller = iterator.next();
			params.remove("DealerCode");
			params.put("DealerCode", parentSeller.getSellerCode());
			
			String result = null;
			try {
				result = JpHttpClientExecute.executeGet(requestUrl, params, headers);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StrKit.isBlank(result) || result.contains("\"code\"")) {
				renderAjaxResultForError("劲牌接口异常");
				return;
			}
			List<JpSellerAccountResponseEntity> sellerAccounts = GsonUtils.deserializeList(result, TypeConst.JP_SELLER_ACCOUNT_RESPONSE_ENTITY);
			
			if(!CollectionUtils.isEmpty(sellerAccounts)) {
				JpSellerAccountResponseEntity responseEntity = null;
				List<SellerSynchronize> storedSubSellers = SellerSynchronizeQuery.me().findByParentCode(parentSeller.getSellerCode());
				// 将拉取到的子经销商信息与系统中的子经销商进行比对， 如果不存在则添加
				for (Iterator<JpSellerAccountResponseEntity> sellerAccountIterator = sellerAccounts.iterator(); sellerAccountIterator.hasNext();) {
					responseEntity = sellerAccountIterator.next();
					// 如果系统中没有子经销商， 则直接进行添加
					if (CollectionUtils.isEmpty(storedSubSellers)) {
						needAddList.add(getSellerSynchronizeEntity(responseEntity, parentSeller));
					} else {	// 如果系统中有子经销商， 且与系统中的子经销商进行比对， 如果不存在则添加
						SellerSynchronize subSeller = null;
						boolean contains = false;
						for (Iterator<SellerSynchronize> subSellerIterator = storedSubSellers.iterator(); subSellerIterator.hasNext();) {
							subSeller = subSellerIterator.next();
							if(responseEntity.getDealerMarketCode().equals(subSeller.getSellerCode())) {
								contains = true;
								break;
							}
						}
						if(!contains) {
							needAddList.add(getSellerSynchronizeEntity(responseEntity, parentSeller));
						}
					}
				}
			}
			parentSeller = null;
		}
		if(needAddList.size() > 0)
			Db.batchSave(needAddList, needAddList.size());
		renderAjaxResultForSuccess();
	}
	
	/**
	 * 拉取商品
	 */
	public void pullGoodsCategories() {
		String goodsTypeCode = getPara("goodsType");
		Calendar calendar = Calendar.getInstance();
		String apiName = PropKit.get("jp.api.httpclient.goodsCategories");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		params.put("clientCode", PropKit.get("jp.api.httpclient.goodsCategories.clientCode"));
		params.put("cInvCCode", goodsTypeCode);
		String result = null;
		try {
			result = JpHttpClientExecute.executeGet(requestUrl, params, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StrKit.notBlank(result) &&  result.contains("\"code\"")) {
			renderAjaxResultForError("劲牌接口异常");
			return;
		}
		List<JpGoodsCategoryResponseEntity> responseGoodsCategories = GsonUtils.deserializeList(result, TypeConst.JP_GOODS_CATEGORY_RESPONSE_ENTITY_LIST_TYPE);
		if(!CollectionUtils.isEmpty(responseGoodsCategories)) {
			JpGoodsCategoryResponseEntity goodsCategory = null;
			GoodsCategory storedGoodsCategory = null;
			String parentCode = null;
			GoodsCategory parentCategory = null;
			String parentId = null;
			final List<Goods> goodsList = new ArrayList<Goods>();
			Goods goods = null;
			GoodsType goodsType = null;
			boolean needAddGoodType = false;
			goodsType = GoodsTypeQuery.me().findByName("劲牌同步类型");
			if(goodsType == null) {
				goodsType = new GoodsType();
				goodsType.setId(StrKit.getRandomUUID());
				goodsType.setName("劲牌同步类型");
				goodsType.setCreateDate(calendar.getTime());
				needAddGoodType = true;
			}
			for (Iterator<JpGoodsCategoryResponseEntity> iterator = responseGoodsCategories.iterator(); iterator.hasNext();) {
				goodsCategory = iterator.next();
				storedGoodsCategory = GoodsCategoryQuery.me().findByCode(goodsCategory.getcInvCCode(), brand.getId());
				if(storedGoodsCategory == null) {
					storedGoodsCategory = new GoodsCategory();
					storedGoodsCategory.setId(StrKit.getRandomUUID());
					storedGoodsCategory.setBrandId(brand.getId());
					storedGoodsCategory.setCode(goodsCategory.getcInvCCode());
					storedGoodsCategory.setCreateDate(calendar.getTime());
					storedGoodsCategory.setGrade(goodsCategory.getiInvCGrade());
					storedGoodsCategory.setIsParent(2 == goodsCategory.getiInvCGrade() ? 0 : 1);
					storedGoodsCategory.setName(goodsCategory.getcInvCName());
					if (storedGoodsCategory.getGrade() == 3) {
						goods = GoodsQuery.me().findByCode(goodsCategory.getcInvCCode());
						if(goods == null) {
							goods = new Goods();
							goods.setId(StrKit.getRandomUUID());
							goods.setBrandId(brand.getId());
							goods.setGoodsCategoryId(storedGoodsCategory.getId());
							goods.setCode(goodsCategory.getcInvCCode());
							goods.setCreateDate(calendar.getTime());
							goods.setName(goodsCategory.getcInvCName());
							goods.setGoodsTypeId(goodsType.getId());
							goods.setState(1);
							goods.setProductImageListStore("[]");
							goodsList.add(goods);
						}
					}
					parentCode = goodsCategory.getParent();
					parentCategory = GoodsCategoryQuery.me().findByCode(parentCode, brand.getId());
					if(parentCategory != null)
						parentId = parentCategory.getId();
					else {
						parentId = "0";
					}
					
					storedGoodsCategory.setParentId(parentId);
					storedGoodsCategory.setState(1);
					storedGoodsCategory.setSupplierId(brand.getSupplierId());
					storedGoodsCategory.save();
				}
				storedGoodsCategory = null;
				parentCode = null;
				parentCategory = null;
				parentId = null;
				goods = null;
			}
			final boolean needAdd = needAddGoodType;
			final GoodsType toSaveType = goodsType;
			Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					try {
						if(goodsList.size() > 0)
							Db.batchSave(goodsList, goodsList.size());
						if(needAdd)
							toSaveType.save();
							
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			});
		}
		renderAjaxResultForSuccess();
	}
	
	/**
	 * 拉取产品信息(根据已存在商品拉取)
	 */
	public void pullProductsByGoods() {
		Calendar calendar = Calendar.getInstance();
		// 查询出劲牌所有商品
		List<Goods> goodsList = GoodsQuery.me().findByBrand(brand.getId());
		if(CollectionUtils.isEmpty(goodsList)) {
			renderAjaxResultForError("没有找到商品");
			return;
		}
		
		String apiName = PropKit.get("jp.api.httpclient.products");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		
		params.put("clientCode", PropKit.get("jp.api.httpclient.products.clientCode"));
		
		Goods goodsCode = null;
		String result = null;
		List<JpProductResponseEntity> responseProducts = null;
		// 循环所有商
		for (Iterator<Goods> iterator = goodsList.iterator(); iterator.hasNext();) {
			goodsCode = iterator.next();
			params.remove("cInvCCode");
			params.put("cInvCCode", goodsCode.getCode());
			
			try {
				result = JpHttpClientExecute.executeGet(requestUrl, params, headers);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StrKit.notBlank(result) && result.contains("\"code\"")) {
				renderAjaxResultForError("劲牌接口异常");
				return;
			}
			responseProducts = GsonUtils.deserializeList(result, TypeConst.JP_PRODUCT_RESPONSE_ENTITY_LIST_TYPE);
			
			if(!CollectionUtils.isEmpty(responseProducts)) {
				JpProductResponseEntity responseEntity = null;
				Product storedProduct = null;
				List<Product> products = new ArrayList<Product>();
				// 将拉取到的产品信息与系统中的产品比对， 如果不存在则添加
				for (Iterator<JpProductResponseEntity> productIterator = responseProducts.iterator(); productIterator.hasNext();) {
					responseEntity = productIterator.next();
					storedProduct = ProductQuery.me().findbyProductSn(responseEntity.getcInvCode());
					if(storedProduct == null) {
						storedProduct = new Product();
						storedProduct.setId(StrKit.getRandomUUID());
						storedProduct.setProductSn(responseEntity.getcInvCode());
						storedProduct.setName(responseEntity.getcInvName());
						storedProduct.setGoodsId(goodsCode.getId());
						if (StrKit.notBlank(responseEntity.getmComUnitName())) {
							storedProduct.setSmallUnit(responseEntity.getmComUnitName());
						} else {
							storedProduct.setSmallUnit("瓶");
						}
						storedProduct.setBigUnit(responseEntity.getcComUnitName());
						storedProduct.setConvertRelate(responseEntity.getcInvMNum());
						storedProduct.setCost(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setPrice(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setFreezeStore(0);
						storedProduct.setIsMarketable(responseEntity.getIstate() != null && responseEntity.getIstate() == 1 ? Boolean.TRUE.booleanValue() : Boolean.FALSE.booleanValue());
						storedProduct.setMarketPrice(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setWeight(responseEntity.getiInvWeight() == null || responseEntity.getiInvWeight() == 0 ? null : responseEntity.getiInvWeight());
						storedProduct.setCreateDate(calendar.getTime());
						products.add(storedProduct);
					}
					responseEntity = null;
					storedProduct = null;
				}
				if(products.size() > 0)
					Db.batchSave(products, products.size());
				
			}
			goodsCode = null;
		}
		renderAjaxResultForSuccess();
	}	
	
	/**
	 * 拉取产品信息(根据已存在分类)
	 */
	public void pullProducts() {
		Calendar calendar = Calendar.getInstance();
		// 查询出劲牌所有商品分类
		List<GoodsCategory> brandCategories = GoodsCategoryQuery.me().findCategoryByBrandId(brand.getId());
		if(CollectionUtils.isEmpty(brandCategories)) {
			renderAjaxResultForError("未同步商品分类");
			return;
		}
		
		String apiName = PropKit.get("jp.api.httpclient.products");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		
		params.put("clientCode", PropKit.get("jp.api.httpclient.products.clientCode"));
		
		GoodsCategory goodsCategory = null;
		String result = null;
		List<JpProductResponseEntity> responseProducts = null;
		// 循环所有商
		for (Iterator<GoodsCategory> iterator = brandCategories.iterator(); iterator.hasNext();) {
			goodsCategory = iterator.next();
			params.remove("cInvCCode");
			params.put("cInvCCode", goodsCategory.getCode());
			
			try {
				result = JpHttpClientExecute.executeGet(requestUrl, params, headers);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(StrKit.notBlank(result) && result.contains("\"code\"")) {
				renderAjaxResultForError("劲牌接口异常");
				return;
			}
			responseProducts = GsonUtils.deserializeList(result, TypeConst.JP_PRODUCT_RESPONSE_ENTITY_LIST_TYPE);
			
			if(!CollectionUtils.isEmpty(responseProducts)) {
				JpProductResponseEntity responseEntity = null;
				Product storedProduct = null;
				Goods goods = null;
				List<Product> products = new ArrayList<Product>();
				List<Product> updateProducts = new ArrayList<>();
				// 将拉取到的产品信息与系统中的产品比对， 如果不存在则添加
				for (Iterator<JpProductResponseEntity> productIterator = responseProducts.iterator(); productIterator.hasNext();) {
					responseEntity = productIterator.next();
					storedProduct = ProductQuery.me().findbyProductSn(responseEntity.getcInvCode());
					if(storedProduct == null) {
						storedProduct = new Product();
						storedProduct.setId(StrKit.getRandomUUID());
						storedProduct.setProductSn(responseEntity.getcInvCode());
						storedProduct.setName(responseEntity.getcInvName());
						goods = GoodsQuery.me().findByCode(goodsCategory.getCode());
						if(goods == null)
							continue;
						storedProduct.setGoodsId(goods.getId());
						if (StrKit.notBlank(responseEntity.getmComUnitName())) {
							storedProduct.setSmallUnit(responseEntity.getmComUnitName());
						} else {
							storedProduct.setSmallUnit("瓶");
						}
						storedProduct.setBigUnit(responseEntity.getcComUnitName());
						storedProduct.setConvertRelate(responseEntity.getcInvMNum());
						storedProduct.setCost(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setPrice(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setFreezeStore(0);
						storedProduct.setIsMarketable(responseEntity.getIstate() != null && responseEntity.getIstate() == 1 ? Boolean.TRUE.booleanValue() : Boolean.FALSE.booleanValue());
						storedProduct.setMarketPrice(responseEntity.getCurrentPrice() == null ? new BigDecimal(0) : responseEntity.getCurrentPrice());
						storedProduct.setWeight(responseEntity.getiInvWeight() == null || responseEntity.getiInvWeight() == 0 ? null : responseEntity.getiInvWeight());
						storedProduct.setCreateDate(calendar.getTime());
						products.add(storedProduct);
					} else {
						storedProduct.setName(responseEntity.getcInvName());
						updateProducts.add(storedProduct);
					}
					goods = null;
					responseEntity = null;
					storedProduct = null;
				}
				if(products.size() > 0)
					Db.batchSave(products, products.size());
				if(updateProducts.size() > 0)
					Db.batchUpdate(updateProducts, updateProducts.size());				
			}
			goodsCategory = null;
		}
		renderAjaxResultForSuccess();
	}
	
	/**
	 * 拉取采购单入库信息
	 */
	public void pullPurchaseStockIns() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		boolean result = false;
		Calendar calendar = Calendar.getInstance();
		FastDateFormat fdf = FastDateFormat.getInstance(DateUtils.DEFAULT_FILE_NAME_FORMATTER);
		String nowDateTimeStr = fdf.format(calendar);
		String apiName = PropKit.get("jp.api.httpclient.purchaseStockIn");
		String requestUrl = JpHttpClientExecute.getRequestUrl(apiName);
		JpPurchaseStockInRequestBody requestBody = new JpPurchaseStockInRequestBody();
		
		params.put("clientCode", PropKit.get("jp.api.httpclient.purchaseStockIn.clientCode"));
		params.put("DealerMarketCode", "");
		String purchaseTime = getPara("purchaseTime");
		if(StrKit.isBlank(purchaseTime)) {
			FastDateFormat shortFdf = FastDateFormat.getInstance(DateUtils.DEFAULT_NORMAL_FORMATTER);
			purchaseTime = shortFdf.format(calendar);
		}
		params.put("BusinessDate", purchaseTime);
		
		JpPurchaseStockInResponseBody responseBody = JpHttpClientExecute.executeGet(requestUrl, requestBody, params, headers);
		if(responseBody != null && responseBody.getFlsg() != null && responseBody.getFlsg().equals("success") &&
			!CollectionUtils.isEmpty(responseBody.getData())) {
			final List<PurchaseOrder> purchaseOrders = new ArrayList<PurchaseOrder>();
			final List<PurchaseOrderDetail> purchaseOrderDetails = new ArrayList<PurchaseOrderDetail>();
			JpPurchaseStockInMainResponse mainStockIn = null;
			List<JpPurchaseStockInDetailResponse> purchaseStockInDetails = null;
			JpPurchaseStockInDetailResponse purchaseStockInDetail = null;
			PurchaseOrder purchaseOrder = null;
			PurchaseOrderDetail purchaseOrderDetail = null;
			Department department = null;
			String porderSn = null;
			Seller seller = null;
			Product product = null;
			String sellerCode = null;
			BigDecimal mainPurchaseTotalAmount = null;
			String stockOutSn = null;
			Integer mainIndex = 0;
			for (Iterator<JpPurchaseStockInMainResponse> iterator = responseBody.getData().iterator(); iterator.hasNext();) {
				/**************添加采购单主单信息********************/
				mainStockIn = iterator.next();
				stockOutSn = mainStockIn.getOrderNum();
				PurchaseOrder order = PurchaseOrderQuery.me().findByStockSn(stockOutSn);
				if (order != null) {
					continue;
				}
				purchaseStockInDetails = mainStockIn.getOrderDetails();
				sellerCode = purchaseStockInDetails.get(0).getDealerMarketCode();
				purchaseOrder = new PurchaseOrder();
				purchaseOrder.setId(StrKit.getRandomUUID());
				List<Seller> sellers = SellerQuery.me().findListByCode(sellerCode);
				if(CollectionUtils.isEmpty(sellers))
					continue;
				if(sellers.size() > 1)
					Collections.sort(sellers, new SellerDeptComparator());
				seller = sellers.get(0);
				mainIndex++;
				porderSn = "PO" + sellerCode + nowDateTimeStr.substring(0,8)+(Integer.valueOf(PurchaseOrderQuery.me().getNewSn(seller.getId()) + mainIndex));
				purchaseOrder.setPorderSn(porderSn);
				purchaseOrder.setStockOutSn(stockOutSn);
				purchaseOrder.setSupplierId(brand.getSupplierId());
				purchaseOrder.setBizDate(calendar.getTime());
				purchaseOrder.setStatus(0);
				purchaseOrder.setDeptId(seller.getDeptId());
				department = DepartmentQuery.me().findById(seller.getDeptId());
				purchaseOrder.setDataArea(department.getDataArea());
				purchaseOrder.setSupplierId(brand.getSupplierId());
				purchaseOrder.setContact(supplier.getContact());
				purchaseOrder.setBizUserId(user.getId());
				purchaseOrder.setMobile(supplier.getMobile());
				purchaseOrder.setCreateDate(calendar.getTime());
				mainPurchaseTotalAmount = new BigDecimal(0);
				
				/********************添加采购单明细**************************/
				purchaseStockInDetails = mainStockIn.getOrderDetails();
				int index = 0;
				for (Iterator<JpPurchaseStockInDetailResponse> detailIterator = purchaseStockInDetails.iterator(); detailIterator.hasNext();) {
					purchaseStockInDetail = detailIterator.next();
					purchaseOrderDetail = new PurchaseOrderDetail();
					purchaseOrderDetail.setId(StrKit.getRandomUUID());
					purchaseOrderDetail.setDeptId(purchaseOrder.getDeptId());
					purchaseOrderDetail.setDataArea(purchaseOrder.getDataArea());
					purchaseOrderDetail.setCreateDate(purchaseStockInDetail.getBusinessDate());
					purchaseOrderDetail.setOrderList(index);
					purchaseOrderDetail.setPurchaseOrderId(purchaseOrder.getId());
					product = ProductQuery.me().findbyProductSn(purchaseStockInDetail.getcInvCode());
					
					if(product == null)
						continue;
					purchaseOrderDetail.setProductId(product.getId());
					// 小数量
					purchaseOrderDetail.setProductCount(Integer.valueOf(purchaseStockInDetail.getSaleNum()) * product.getConvertRelate());
					purchaseOrderDetail.setProductAmount(product.getPrice().multiply(new BigDecimal(purchaseOrderDetail.getProductCount())));
					purchaseOrderDetail.setProductPrice(product.getPrice());
					mainPurchaseTotalAmount.add(purchaseOrderDetail.getProductAmount());
					purchaseOrderDetails.add(purchaseOrderDetail);
					index++;
				}
				purchaseOrder.setTotalAmount(mainPurchaseTotalAmount);
				purchaseOrders.add(purchaseOrder);
			}
			result = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					try {
						if(purchaseOrders.size() > 0)
							Db.batchSave(purchaseOrders, purchaseOrders.size());
						if(purchaseOrderDetails.size() > 0)
							Db.batchSave(purchaseOrderDetails, purchaseOrderDetails.size());
						return Boolean.TRUE.booleanValue();
					} catch (Exception e) {
						e.printStackTrace();
						return Boolean.FALSE.booleanValue();
					}
				}
			});
		} else {
			renderAjaxResultForSuccess("没有数据");
			return;
		}
		if(result) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}
	}
	
	/**
	 * 获得经销商账号实体(即系统中的经销商账套)
	 * @param responseEntity
	 * @param parentSeller
	 */
	private SellerSynchronize getSellerSynchronizeEntity(JpSellerAccountResponseEntity responseEntity, SellerSynchronize parentSeller) {
		Calendar calendar = Calendar.getInstance();
		SellerSynchronize subSellerSynchronize = new SellerSynchronize();
		subSellerSynchronize.setId(StrKit.getRandomUUID());
		subSellerSynchronize.setBrandCode(BRAND_CODE);
		subSellerSynchronize.setSellerCode(responseEntity.getDealerMarketCode());
		subSellerSynchronize.setSellerName(responseEntity.getDealerMarketName().trim());
		subSellerSynchronize.setParentCode(parentSeller.getSellerCode());
		subSellerSynchronize.setParentId(parentSeller.getId());
		subSellerSynchronize.setProvName(StrKit.isBlank(parentSeller.getProvName()) ? null : parentSeller.getProvName().trim());
		subSellerSynchronize.setCityName(StrKit.isBlank(parentSeller.getCityName()) ? null : parentSeller.getCityName().trim());
		subSellerSynchronize.setSellerType(0);
		subSellerSynchronize.setHasStore(0);
		subSellerSynchronize.setCreateDate(calendar.getTime());
		subSellerSynchronize.setModifyDate(calendar.getTime());
		return subSellerSynchronize;
	}
}
