package org.ccloud.wechat;

import javax.servlet.http.HttpServletRequest;

import org.ccloud.Consts;
import org.ccloud.core.BaseWechatController;
import org.ccloud.model.User;
import org.ccloud.utils.CookieUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;

public class WechatOauthInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {

		BaseWechatController controller = (BaseWechatController) inv.getController();
		
		User user = null;
		String openid = controller.getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
		System.err.println(openid);
		if (StrKit.notBlank(openid)) {
			user = (User) controller.getUserByOpenId(openid);
			if (user != null) {
	            controller.setAttr(Consts.ATTR_USER_OBJECT, user);
	            CookieUtils.put(controller, Consts.COOKIE_LOGINED_USER, user.getId());
	            inv.invoke();
	            return;
	        }
		}
		 
		String wechatUserJson = controller.getSessionAttr(Consts.SESSION_WECHAT_USER);
		if (validateUserJson(wechatUserJson)) {
			user = (User) controller.doSaveOrUpdateUserByApiResult(wechatUserJson, openid, user);
			if (user == null) {
                controller.renderText("can not save or update user when get user from wechat");
                return;
            }
            controller.setAttr(Consts.ATTR_USER_OBJECT, user);
			inv.invoke();
            return;
		}
		
		HttpServletRequest request = controller.getRequest();
		String baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
		
		// 获取用户将要去的路径
        String queryString = request.getQueryString();
		// 被拦截前的请求URL
        String toUrl = request.getRequestURI();
        if (StringUtils.isNotBlank(queryString)) {
            toUrl = toUrl.concat("?").concat(queryString);
        }
		
		String appid = ApiConfigKit.getAppId();
		
		if (StrKit.isBlank(appid)) {
			//renderText("config is error");
			inv.invoke();
			return ;
		} else {
			String controllerKey = inv.getControllerKey();
	        String callbackControllerKey = controllerKey + "/wechatCallback";

	        if (!JFinal.me().getAllActionKeys().contains(callbackControllerKey)) {
	            callbackControllerKey = controllerKey.substring(0, controllerKey.lastIndexOf("/")) + "/wechatCallback";
	        }

	        String redirectUrl = baseUrl + callbackControllerKey + "?goto=" + StringUtils.urlEncode(toUrl);

	        redirectUrl = StringUtils.urlEncode(redirectUrl);
	        //String authUrl = isFromBaseScope ? AUTHORIZE_URL : BASE_AUTHORIZE_URL;
			
			String url = SnsAccessTokenApi.getAuthorizeURL(appid.trim(), redirectUrl, false);
			controller.redirect(StringUtils.urlDecode(url));
		}
	}
	
	/**
     * 验证微信用户的json信息是否正确
     *
     * @param wechatUserJson
     * @return
     */
    protected boolean validateUserJson(String wechatUserJson) {
        return StringUtils.isNotBlank(wechatUserJson)
                && wechatUserJson.contains("openid")
                && wechatUserJson.contains("nickname") //包含昵称
                && wechatUserJson.contains("headimgurl"); //包含头像
    }

//    protected boolean isFromBaseScope(Invocation inv) {
//        String scope = inv.getController().getSessionAttr(JbootWechatController.SESSION_WECHAT_SCOPE);
//        return scope != null && "snsapi_base".equalsIgnoreCase(scope);
//    }

}
