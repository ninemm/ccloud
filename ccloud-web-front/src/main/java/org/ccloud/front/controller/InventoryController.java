package org.ccloud.front.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.GoodsCategory;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.GoodsCategoryQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.route.RouterMapping;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/inventory")
@RequiresPermissions(value = { "/front/inventory", "/admin/dealer/all" }, logical = Logical.OR)
public class InventoryController extends BaseFrontController {
	
	public void index() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Warehouse> wareHouseList = new ArrayList<>();
		String wareHouseIds = "";
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");
		if (isSuperAdmin) {
			wareHouseList = WarehouseQuery.me().findByDataArea(dataArea);
		} else {
			wareHouseList = WarehouseQuery.me().findWarehouseByUserId(user.getId());
		}
		for (int i = 0; i < wareHouseList.size(); i++) {
			if (i == wareHouseList.size() - 1) {
				wareHouseIds = wareHouseIds + wareHouseList.get(i).getId();
			} else {
				wareHouseIds = wareHouseIds + wareHouseList.get(i).getId() + ",";
			}
		}
		List<GoodsCategory> categoryList = GoodsCategoryQuery.me().findProductCategory(sellerId);
		List<Map<String, Object>> regionList = new ArrayList<>();
		Map<String, Object> region = new HashMap<>();
		region.put("title", "全部");
		region.put("value", "");
		regionList.add(region);
		for(Warehouse wareHouse : wareHouseList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", wareHouse.getName());
			item.put("value", wareHouse.getId());
			regionList.add(item);
		}	
		setAttr("wareHouseIds", wareHouseIds);
		setAttr("categoryList", categoryList);
		setAttr("wareHouseList", JsonKit.toJson(regionList));
		render("inventory.html");
	}
	
	//库存详情
	@RequiresPermissions(value = { "/front/inventory", "/admin/dealer/all" }, logical = Logical.OR)
	public void inventory() {
		String[] warehouseIds = getWareHouseIdsList(getPara("warehouseIds"));
		String warehouseId = getPara("warehouseId");
		String categoryId = getPara("categoryId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String search = getPara("search");
		Page<Record> inventoryList = InventoryQuery.me().
				findInventoryDetailByParams(getPageNumber(), getPageSize(), startDate, endDate, warehouseId, warehouseIds, categoryId, search);
		Map<String, Object> map = ImmutableMap.of("total", inventoryList.getTotalRow(), "rows", inventoryList.getList());
		renderJson(map);
	}

	private String[] getWareHouseIdsList(String para) {
		if (StrKit.notBlank(para)) {
			String[] ids = para.split(",");
			return ids;
		}
		return null;
	}
	
}
