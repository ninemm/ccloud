package org.ccloud.controller.bi;

import com.jfinal.plugin.activerecord.Record;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.model.User;
import org.ccloud.model.query.BiManagerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.CookieUtils;
import org.ccloud.wwechat.WorkWechatApiConfigInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.qyweixin.sdk.api.ApiResult;
import com.jfinal.qyweixin.sdk.api.OAuthApi;

import java.util.List;

@RouterMapping(url = "/woauth")
public class WWxOauthController extends BaseFrontController {

	@ActionCache
	@Clear(SessionInterceptor.class)
	@Before(WorkWechatApiConfigInterceptor.class)
	public void index() {
		String gotoUrl = getPara("goto", Consts.INDEX_URL);
		String wechatUserJson = getSessionAttr(Consts.SESSION_WECHAT_USER);
		String code = getPara("code");
		
		if (StrKit.isBlank(wechatUserJson)) {
			String userJsonText = getUserInfoByCode(code);
			if (StrKit.notBlank(userJsonText)) {
				setSessionAttr(Consts.SESSION_WECHAT_USER, userJsonText);
				wechatUserJson = userJsonText;
			}
		}
		
		JSONObject userJson = JSON.parseObject(wechatUserJson);
		String errorCode = userJson.getString("errcode");
		if (!StrKit.equals(errorCode, "0")) {
			String userJsonText = getUserInfoByCode(code);
			if (StrKit.notBlank(userJsonText)) {
				setSessionAttr(Consts.SESSION_WECHAT_USER, userJsonText);
				userJson = JSON.parseObject(userJsonText);
			}
		}
		
		String wechatUserId = userJson.getString("UserId");
		if (StrKit.isBlank(wechatUserId)) {
			renderText("配置错误，请联系管理员！");
			return ;
		} else { 
			CookieUtils.put(this, Consts.SESSION_WECHAT_OPEN_ID, wechatUserId);
			setSessionAttr(Consts.SESSION_WECHAT_OPEN_ID, wechatUserId);
		}
		
		User user = UserQuery.me().findByWechatUserId(wechatUserId);
		if (user == null) {
			renderError(500);
			return ;
		}
		
		setSessionAttr(Consts.SESSION_LOGINED_USER, user);

		List<Record> sellerByUser = BiManagerQuery.me().findSellerByUser(user.getId());
		String sellerArray[] = new String[sellerByUser.size()];
		String sellerNameArray[] = new String[sellerByUser.size()];
		for (int i = 0; i < sellerByUser.size(); i++) {
			sellerArray[i] = sellerByUser.get(i).getStr("dealer_data_area");
			sellerNameArray[i] = sellerByUser.get(i).getStr("seller_name");
		}

		List<Record> brandByUser = BiManagerQuery.me().findBrandByUser(user.getId());
		String brandArray[] = new String[brandByUser.size()];
		for (int i = 0; i < brandByUser.size(); i++) {
			brandArray[i] = brandByUser.get(i).getStr("brand_id");
		}

		List<Record> productByUser = BiManagerQuery.me().findProductByUser(user.getId());
		String productArray[] = new String[productByUser.size()];
		for (int i = 0; i < productByUser.size(); i++) {
			productArray[i] = productByUser.get(i).getStr("product_id");
		}

		setSessionAttr(Consts.SESSION_DEALER_DATA_AREA_ARRAY, sellerArray);
		setSessionAttr(Consts.SESSION_SELLER_NAME, sellerNameArray);
		setSessionAttr(Consts.SESSION_BRAND_ID_ARRAY, brandArray);
		setSessionAttr(Consts.SESSION_PRODUCT_ID_ARRAY, productArray);
		
		// 更新用户的信息
		/*ApiResult wxUserResult = ConUserApi.getUser(wechatUserId);
		if (wxUserResult.isSucceed()) {
			user.setAvatar(wxUserResult.getStr("avatar"));
			user.setNickname(wxUserResult.getStr("name"));
			user.setWechatUseriId(wechatUserId);
			
			if (!user.saveOrUpdate()) {
				renderError(500);
				return ;
			}
			setSessionAttr(Consts.SESSION_LOGINED_USER, user);
		} else {
			LogKit.warn("user info get failure");
		}*/
		
		forwardAction(gotoUrl);
	}
	
	private String getUserInfoByCode(String code) {
		ApiResult result = OAuthApi.getUserInfoByCode(code);
		if (result != null) {
			return result.getJson();
		}
		return null;
	}
}
