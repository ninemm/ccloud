package org.ccloud.front.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.GoodsType;
import org.ccloud.model.Product;
import org.ccloud.model.Seller;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.GoodsTypeQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.SellerQuery;
import org.ccloud.model.query.SmsCodeQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.CaptchaUsernamePasswordToken;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.DataAreaUtil;

import com.google.common.collect.ImmutableMap;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.UserApi;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = Consts.ROUTER_USER)
public class UserController extends BaseFrontController{

	public void index() {
		render("user.html");
	}
	
	public void login() {}
	
	public void center() {
		keepPara();
		String action = getPara(0, "index");
		render(String.format("user_center_%s.html", action));
	}
	
	public void bind() {
		
		String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
		ApiResult wxUserResult = UserApi.getUserInfo(openId);
		if (wxUserResult != null) {
			setAttr("avatar", wxUserResult.getStr("headimgurl"));
			setAttr("nickname", wxUserResult.getStr("nickname"));
		}
		
		render("user_bind.html");
	}
	
	public void checkMobile() {
		
		String mobile = getPara("mobile");
		User user = UserQuery.me().findByMobile(mobile);
		if (user != null)
			renderAjaxResultForSuccess();
		else
			renderAjaxResultForError("手机号不存在");
	}
	
	
	public void update() {
		
		final String mobile = getPara("mobile");
		final String code = getPara("code");
		final Ret ret = Ret.create();
		
		boolean updated = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				
				boolean isSend = false;
				// 验证短信验证码是否正确
				SmsCode smsCode = SmsCodeQuery.me().findByMobileAndCode(mobile, code);
				if (smsCode != null) {
					smsCode.setStatus(1);
					if (!smsCode.update()) {
						return false;
					}
					isSend = true;
				} else {
					return false;
				}
				
				if (isSend) {
					String openId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
					
					ApiResult wxUserResult = UserApi.getUserInfo(openId);
					if (wxUserResult != null) {
						User user = UserQuery.me().findByMobile(mobile);
						
						if (user == null) {
							ret.set("message", "手机号不存在，请联系管理员");
							return false;
						}
						
						user.setAvatar(wxUserResult.getStr("headimgurl"));
						user.setNickname(wxUserResult.getStr("nickname"));
						user.setWechatOpenId(openId);
						if (!user.saveOrUpdate()) {
							ret.set("message", "手机号绑定失败，请联系管理员");
							return false;
						}
						
						// 获取用户权限
						init(user.getUsername(), user.getPassword(), true);
					}
				}
				
				return true;
			}
		});
		
		if (updated) {
			renderAjaxResultForSuccess("绑定手机号成功");
			return ;
		}
		renderAjaxResultForError(ret.getStr("message"));
	}

	private void init(String username, String password, Boolean rememberMe) {
		
		Subject subject = SecurityUtils.getSubject();
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(username, password, rememberMe, "", "");
		try {
			subject.login(token);
			User user = (User) subject.getPrincipal();
			if (user != null) {
				// 数据查看时的数据域
				if (subject.isPermitted("/admin/all") || subject.isPermitted("/admin/manager")) {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA,
							DataAreaUtil.getUserDeptDataArea(user.getDataArea()) + "%");
				} else {
					setSessionAttr(Consts.SESSION_SELECT_DATAAREA, user.getDataArea());
				}

				// sellerId
				if (!subject.isPermitted("/admin/all")) {
					List<Record> sellerList = SellerQuery.me().querySellerIdByDept(user.getDepartmentId());

					if(sellerList.size() == 0) {
						sellerList = SellerQuery.me().queryParentSellerIdByDept(user.getDepartmentId());

						while(StrKit.isBlank(sellerList.get(0).getStr("sellerId"))) {
							sellerList = SellerQuery.me().queryParentSellerIdByDept(sellerList.get(0).getStr("parent_id"));
						}
					}

					setSessionAttr("sellerList", sellerList);
					setSessionAttr("sellerId", sellerList.get(0).get("sellerId"));
					setSessionAttr("sellerCode", sellerList.get(0).get("sellerCode"));
					setSessionAttr("sellerName", sellerList.get(0).get("sellerName"));
				}
			}
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId().toString());
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
	}
	
	public void inventory() {
		User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
		//String wareHouseId = getPara("mobile");
		//String productName = getPara("product");
		String sellerId = getPara("sellerId");
		String deptDataArea = loginUser!=null?DataAreaUtil.getUserDeptDataArea(loginUser.getDataArea()):"";
		String deptId = "";
		//Page<Record> inventoryList = InventoryQuery.me().findDetailByApp(getPageNumber(), getPageSize(),"","",sellerId,deptDataArea,deptId);
		Page<Record> inventoryList = InventoryQuery.me().findDetailByParams("","", sellerId, "", deptId, deptDataArea,"",getPageNumber(), getPageSize());

		List<GoodsType> goodsTypeList = GoodsTypeQuery.me().findGoodsType(deptDataArea+"%");
		setAttr("inventoryList", inventoryList);
		setAttr("goodsTypeList", goodsTypeList);
		render("inventory.html");
	}
	
	public void appLoadRegionAndProductType() {
		User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String goodsType = getPara("goodsType");
		String queryType = getPara("queryType");
		List<Map<String, Object>> regionList = new ArrayList<>();
		if(!queryType.equals("productType")) {
			String deptDataArea = loginUser!=null?DataAreaUtil.getUserDeptDataArea(loginUser.getDataArea()):"";
			List<Seller> sellerList = SellerQuery.me().findSellerRegion(deptDataArea+"%");
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
		//List<GoodsType> productTypeList = GoodsTypeQuery.me().findProductType(deptDataArea+"%");
		goodsType = (!StrKit.notBlank(goodsType))?"":goodsType;
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
		User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
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
		String deptDataArea = loginUser!=null?DataAreaUtil.getUserDeptDataArea(loginUser.getDataArea()):"";
		String deptId = "";
		Page<Record> inventoryList = InventoryQuery.me().findDetailByParams(search,goodsType, sellerId, productType, deptId, deptDataArea,isOrdered,pageNumber,pageSize);
		StringBuilder inventoryHtml = new StringBuilder();
		for (Record inventory : inventoryList.getList()) {
			inventoryHtml.append("<div class=\"product_detail\">");
			inventoryHtml.append("<div class=\"inventory_name\">"+inventory.getStr("name")+"</div>");
			//期初期末结存 未定,暂时不做统计。
			//inventoryHtml.append("<div class=\"weui-flex\"><div class=\"weui-flex__item\">期初结存：<span>"+inventory.getStr("in_count")+"</span></div><div class=\"weui-flex__item\">期末结存：<span>"+inventory.getStr("out_count")+"</span></div></div>");
			inventoryHtml.append("<div class=\"weui-flex\"><div class=\"weui-flex__item\">出库：<span class=\"green-button\">"+inventory.getStr("in_count")+"</span></div><div class=\"weui-flex__item\">入库：<span class=\"yellow-button\">"+inventory.getStr("out_count")+"</span></div></div>");
			inventoryHtml.append("<div class=\"weui-flex\"><div class=\"weui-flex__item\">库存：<span class=\"blue-button\">"+inventory.getStr("balance_count")+"</span></div><div class=\"weui-flex__item\">在途：<span>"+inventory.getStr("afloat_count")+"</span></div></div>");
			inventoryHtml.append("<div><i class=\"icon-map-pin blue ft16\"></i>&nbsp;&nbsp;"+inventory.getStr("seller_name")+"</div>");
			inventoryHtml.append("</div>\n");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("inventoryHtml", inventoryHtml.toString());
		map.put("totalRow", inventoryList.getTotalRow());
		map.put("totalPage", inventoryList.getTotalPage());
		renderJson(map);
		//renderAjaxResultForSuccess("success",JSON.toJSON(inventoryList));
	}
}
