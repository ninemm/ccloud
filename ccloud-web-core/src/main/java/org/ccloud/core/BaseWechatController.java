package org.ccloud.core;

import org.ccloud.Consts;
import org.ccloud.utils.StringUtils;

import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;

public abstract class BaseWechatController extends BaseFrontController {

	public abstract Object getUserByOpenId(String openid);
	
	public abstract Object doSaveOrUpdateUserByApiResult(String apiResult, String openId, Object user);
	
	public <T> T getCurrentUser() {
        return getAttr(Consts.ATTR_USER_OBJECT);
    }
	
	public void wechatCallback() {

        String gotoUrl = getPara("goto");
        String code = getPara("code");

        //获得不到code？
        if (StringUtils.isBlank(code)) {
            renderText("获取不到正确的code信息");
            return;
        }

        /**
         * 在某些情况下，相同的callback会执行两次，code相同。
         */
        String wechatOpenId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
        String accessToken = getSessionAttr(Consts.SESSION_WECHAT_ACCESS_TOKEN);
        
        if (StringUtils.isNotBlank(wechatOpenId)
                && StringUtils.isNotBlank(accessToken)) {
            doRedirect(gotoUrl, wechatOpenId, accessToken);
            return;
        }

        String appId = ApiConfigKit.getAppId();
		String secret = ApiConfigKit.getApiConfig().getAppSecret();
        
		SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
        if (snsAccessToken == null) {
            renderText("网络错误，获取不到微信信息，请联系管理员");
            return;
        }

        /**
         * 成功获取到 accesstoken 和 openid
         */
        if (snsAccessToken.isAvailable()) {
            wechatOpenId = snsAccessToken.getOpenid();
            accessToken = snsAccessToken.getAccessToken();
            
            setSessionAttr(Consts.SESSION_WECHAT_OPEN_ID, wechatOpenId);
            setSessionAttr(Consts.SESSION_WECHAT_ACCESS_TOKEN, accessToken);
            //setSessionAttr(Consts.SESSION_WECHAT_SCOPE, snsAccessToken.getScope());
        } else {
            //wechatOpenId = getSessionAttr(Consts.SESSION_WECHAT_OPEN_ID);
            //accessToken = getSessionAttr(Consts.SESSION_WECHAT_ACCESS_TOKEN);

            if (StringUtils.isBlank(wechatOpenId) || StringUtils.isBlank(accessToken)) {
                renderText("错误：" + snsAccessToken.getErrorMsg());
                return;
            }
        }

        if ("snsapi_base".equalsIgnoreCase(snsAccessToken.getScope())) {
            redirect(gotoUrl);
            return;
        }

        doRedirect(gotoUrl, wechatOpenId, accessToken);
    }
	
	private void doRedirect(String gotoUrl, String wechatOpenId, String accessToken) {

		System.err.println(this.getClass().getName() + " method: doRedirect");
		if (!gotoUrl.contains("openid")) {
        	gotoUrl += "?openid=" + wechatOpenId;
		}
        /**
         * 由于 wechatOpenId 或者 accessToken 是可能从session读取的，
         * 从而导致失效等问题
         */
        ApiResult apiResult = SnsApi.getUserInfo(accessToken, wechatOpenId);

        if (!apiResult.isSucceed()) {
            redirect(gotoUrl);
            return;
        }

        setSessionAttr(Consts.SESSION_WECHAT_USER, apiResult.getJson());
        redirect(gotoUrl);
    }
}
