package org.ccloud.front.controller;

import javax.servlet.http.HttpServletRequest;

import org.ccloud.Consts;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.model.User;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.wechat.WechatUserInterceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.UserApi;

@RouterMapping(url = "/wxoauth")
public class WxOauthController extends Controller {

	@ActionCache
	@Before(WechatUserInterceptor.class)
	public void index() {
		
		String openId = null;
		String accessToken = null;
		String gotoUrl = getPara("goto", "/");
		String wechatUserJson = getSessionAttr(Consts.SESSION_WECHAT_USER);
		
		if (StrKit.notBlank(wechatUserJson)) {
			JSONObject userJson = JSON.parseObject(wechatUserJson);
			accessToken = userJson.getString("access_token");
			openId = userJson.getString("openid");
			
			if (StrKit.notBlank(openId, accessToken)) {
				CookieUtils.put(this, Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken);
				CookieUtils.put(this, Consts.SESSION_WECHAT_OPEN_ID, openId);
				setSessionAttr(Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken);
				setSessionAttr(Consts.SESSION_WECHAT_OPEN_ID, openId);
			} else {
				renderError(500);
				return ;
			}
			
			User user = UserQuery.me().findByWechatOpenid(openId);
			System.err.println("user is not exsit>>>" + (user == null));
			
			if (user == null) {
				gotoUrl = "/user/bind";
			} else {
				
				// 获取用户的相应权限
				
				// 更新用户的信息
			}
		}
		
		redirect(gotoUrl);
	}
	
	public void toOauth() {
		HttpServletRequest request = this.getRequest();
		// 获取用户将要去的路径
		String queryString = request.getQueryString();
		
		// 被拦截前的请求URL
//		String toUrl = request.getRequestURI();
//		if (StringUtils.isNotBlank(queryString)) {
//			toUrl = toUrl.concat("?").concat(queryString);
//		}
		String redirectUrl = request.getScheme() + "://" + request.getServerName() 
//			+ ":" + request.getServerPort() 
			+ "/wxoauth";
		
		if (StrKit.notBlank(queryString))
			redirectUrl = redirectUrl.concat("?").concat(queryString);
		
		System.out.println("redirect = " + redirectUrl);
		redirectUrl = StringUtils.urlEncode(redirectUrl);
		//String appid = OptionQuery.me().findValue("wechat_appid");
		String appid = ApiConfigKit.getAppId();
		
		if (StrKit.isBlank(appid))
			renderText("config is error");
		else {
			String url = SnsAccessTokenApi.getAuthorizeURL(appid.trim(), redirectUrl, false);
			redirect(url);
		}
	}
	
	public boolean saveOrUpdate(ApiResult apiResult, User user, String openId, int subscribe) {
		JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
		String nickname = jsonObject.getString("nickname");
		//nickname = StringUtils.urlEncode(nickname);
		// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
		//int sex = jsonObject.getIntValue("sex");
//			String city = jsonObject.getString("city");
//			String province = jsonObject.getString("province");//省份
//	        String country = jsonObject.getString("country");//国家
		String headimgurl = jsonObject.getString("headimgurl");
		//String unionid = jsonObject.getString("unionid");
		
		ApiResult userInfo = UserApi.getUserInfo(openId);
		if (userInfo.isSucceed()) {
			String userText = userInfo.toString();
			subscribe = JSON.parseObject(userText).getIntValue("subscribe");
		}
		
//		if (sex == 1)
//			user.setGender("male");
//		else if (sex == 2)
//			user.setGender("female");
//		
		user.setAvatar(headimgurl);
		user.setNickname(nickname);
		//user.setUnionid(unionid);
		//user.setOpenid(openId);
		//user.setCreateSource("wechat");
		//user.setSubscribe(subscribe);
		
		return user.saveOrUpdate();
	}
	
}
