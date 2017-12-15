package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Product;
import org.ccloud.model.Seller;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.route.RouterMapping;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/inventory")
public class InventoryController extends BaseFrontController {
	
	public void index() {
		render("inventory.html");
	}
	
	public void inventory() {
		String sellerId = getPara("sellerId");
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = "";
		Page<Record> inventoryList = new Page<Record>();
		List<GoodsType> goodsTypeList = new ArrayList<GoodsType>();
		if(StrKit.notBlank(selDataArea)) {
			inventoryList = InventoryQuery.me().findDetailByParams("","", sellerId, "", deptId, selDataArea,"",getPageNumber(), getPageSize());
			goodsTypeList = GoodsTypeQuery.me().findGoodsType(selDataArea);
		}
		setAttr("inventoryList", inventoryList);
		setAttr("goodsTypeList", goodsTypeList);
		render("inventory.html");
	}
	
	public void appLoadRegionAndProductType() {
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String goodsType = getPara("goodsType");
		String queryType = getPara("queryType");
		List<Map<String, Object>> regionList = new ArrayList<>();
		if(!queryType.equals("productType")) {
			List<Seller> sellerList = new ArrayList<Seller>();
			if(StrKit.notBlank(selDataArea)) {
				sellerList = SellerQuery.me().findSellerRegion(selDataArea);
			}
			Map<String, Object> region = new HashMap<>();
			region.put("title", "全部");
			region.put("value", "");
			regionList.add(region);
			for(Seller seller : sellerList) {
				Map<String, Object> item = new HashMap<>();
				item.put("title", seller.getSellerName());
				item.put("value", seller.getId());
				regionList.add(item);
			}
		}
		goodsType = (StrKit.notBlank(goodsType))?goodsType:"";
		List<Product> productList = ProductQuery.me().findAllProduct(goodsType);
		List<Map<String, Object>> typeList = new ArrayList<>();
		Map<String, Object> type = new HashMap<>();
		type.put("title", "全部");
		type.put("value", "");
		typeList.add(type);
		for(Product product : productList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", product.getName());
			item.put("value", product.getId());
			typeList.add(item);
		}
		Map<String, List<Map<String, Object>>> data = ImmutableMap.of("region", regionList, "productType", typeList);
		renderJson(data);
	}
	
	public void appLoadFollowUpData() {
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String search = getPara("search");
		int pageNumber = Integer.parseInt(getPara("pageNumber"));
		int pageSize = Integer.parseInt(getPara("pageSize"));
		String sellerId = getPara("region");
		String productType = getPara("productType");
		productType = productType.equals("全部")?"":productType;
		String isOrdered = getPara("isOrdered");
		if(isOrdered==null)isOrdered="00";
		String goodsType = getPara("goodsType");
		goodsType = goodsType.equals("00")||goodsType==null?"":goodsType;
		String deptId = "";
		Page<Record> inventoryList = new Page<Record>();
		StringBuilder inventoryHtml = new StringBuilder("<div class=\"weui-loadmore weui-loadmore_line\"><span class=\"weui-loadmore__tips\"  style=\"float: inherit;\">暂无数据</span></div>");
		if(StrKit.notBlank(selDataArea)) {
			inventoryList = InventoryQuery.me().findDetailByParams(search,goodsType, sellerId, productType, deptId, selDataArea,isOrdered,pageNumber,pageSize);
			if(inventoryList.getList().size()>0||pageNumber>1) {
				inventoryHtml.delete(0, inventoryHtml.length());	
			}
			for (Record inventory : inventoryList.getList()) {
				inventoryHtml.append("<div class=\"product_detail\">");
				inventoryHtml.append("<div class=\"inventory_name\" style=\"font-size: 0.7rem;\">"+inventory.getStr("name")+"</div>");
				//期初期末结存 未定,暂时不做统计。
				//inventoryHtml.append("<div class=\"weui-flex\"><div class=\"weui-flex__item\">期初结存：<span>"+inventory.getStr("in_count")+"</span></div><div class=\"weui-flex__item\">期末结存：<span>"+inventory.getStr("out_count")+"</span></div></div>");
				inventoryHtml.append("<div class=\"weui-flex\" style=\"margin:0.1rem;\"><div class=\"weui-flex__item\">出库：<span class=\"green-button\">"+inventory.getStr("in_count")+"</span></div><div class=\"weui-flex__item\">入库：<span class=\"yellow-button\">"+inventory.getStr("out_count")+"</span></div></div>");
				//暂时隐藏在途量 <div class=\"weui-flex__item\">在途：<span>"+inventory.getStr("afloat_count")+"</span></div>
				inventoryHtml.append("<div class=\"weui-flex\" style=\"margin:0.1rem;\"><div class=\"weui-flex__item\">库存：<span class=\"blue-button\">"+inventory.getStr("balance_count")+"</span></div><div class=\"weui-flex__item\"></div></div>");
				inventoryHtml.append("<div><i class=\"icon-map-pin blue ft16\"></i>&nbsp;&nbsp;"+inventory.getStr("seller_name")+"</div>");
				inventoryHtml.append("</div>\n");
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("inventoryHtml", inventoryHtml.toString());
		map.put("totalRow", inventoryList.getTotalRow());
		map.put("totalPage", inventoryList.getTotalPage());
		renderJson(map);
	}	
}
