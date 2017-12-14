package org.ccloud.front.controller;

import java.math.BigInteger;
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
import org.ccloud.interceptor.UserInterceptor;
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
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;


import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
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
		String action = getPara();
		if (StringUtils.isBlank(action)) {
			renderError(404);
		}

		keepPara();

		BigInteger userId = StringUtils.toBigInteger(action, null);
		if (userId != null) {
			
			
		} else {
			if ("detail".equalsIgnoreCase(action)) {
				renderError(404);
			}
			render(String.format("user_%s.html", action));
		}
	}
	
	@Clear(UserInterceptor.class)
	@ActionKey(Consts.ROUTER_USER_LOGIN) // 固定登录的url
	public void login() {
		String username = getPara("username");
		String password = getPara("password");

		if (username == null || password == null) {
			render("user_login.html");
			return;
		}
		
		long errorTimes = CookieUtils.getLong(this, "_login_errors", 0);
		
		if (errorTimes >= 3) {
			if (!validateCaptcha("_login_captcha")) { // 验证码没验证成功！
				if (isAjaxRequest()) {
					renderAjaxResultForError("没有该用户");
				} else {
					redirect(Consts.ROUTER_USER_LOGIN);
				}
				return;
			}
		}
		
		User user = UserQuery.me().findUserByUsername(username);
		if (null == user) {
			if (isAjaxRequest()) {
				renderAjaxResultForError("没有该用户");
			} else {
				setAttr("errorMsg", "没有该用户");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
			return;
		}
		
		if (EncryptUtils.verlifyUser(user.getPassword(), user.getSalt(), password)) {
			MessageKit.sendMessage(Actions.USER_LOGINED, user);
			CookieUtils.put(this, Consts.COOKIE_LOGINED_USER, user.getId());
			// 获取用户权限
			init(user.getUsername(), user.getPassword(), true);
			if (this.isAjaxRequest()) {
				renderAjaxResultForSuccess("登录成功");
			} else {
				String gotoUrl = getPara("goto");
				if (StringUtils.isNotEmpty(gotoUrl)) {
					gotoUrl = StringUtils.urlDecode(gotoUrl);
					gotoUrl = StringUtils.urlRedirect(gotoUrl);
					redirect(gotoUrl);
				} else {
					redirect(Consts.ROUTER_USER_CENTER);
				}
			}
		} else {
			if (isAjaxRequest()) {
				renderAjaxResultForError("密码错误");
			} else {
				setAttr("errorMsg", "密码错误");
				render("user_login.html");
			}
			CookieUtils.put(this, "_login_errors", errorTimes + 1);
		}
	}
	
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
		//User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
		//String wareHouseId = getPara("mobile");
		//String productName = getPara("product");
		String sellerId = getPara("sellerId");
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String deptId = "";
		//Page<Record> inventoryList = InventoryQuery.me().findDetailByApp(getPageNumber(), getPageSize(),"","",sellerId,deptDataArea,deptId);
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
		//User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String selDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String goodsType = getPara("goodsType");
		String queryType = getPara("queryType");
		List<Map<String, Object>> regionList = new ArrayList<>();
		if(!queryType.equals("productType")) {
			//String deptDataArea = loginUser!=null?DataAreaUtil.getUserDeptDataArea(loginUser.getDataArea()):"";
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
		//List<GoodsType> productTypeList = GoodsTypeQuery.me().findProductType(deptDataArea+"%");
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
		//User loginUser = getSessionAttr(Consts.SESSION_LOGINED_USER);
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
		//String deptDataArea = loginUser!=null?DataAreaUtil.getUserDeptDataArea(loginUser.getDataArea()):"";
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
		//renderAjaxResultForSuccess("success",JSON.toJSON(inventoryList));
	}	
}
