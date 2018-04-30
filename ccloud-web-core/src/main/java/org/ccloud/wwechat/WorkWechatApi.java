package org.ccloud.wwechat;

import com.jfinal.kit.PropKit;
import org.ccloud.model.query.OptionQuery;

import com.jfinal.qyweixin.sdk.api.ApiConfig;


public class WorkWechatApi {
	
	public static ApiConfig getApiConfig() {
		
		ApiConfig config = new ApiConfig();

		PropKit.use("ccloud.properties");
		config.setCorpId(PropKit.get("corpId"));
		config.setAgentId(PropKit.get("agentId"));
		config.setCorpSecret(PropKit.get("contact_secret"));
		config.setEncodingAesKey(PropKit.get("encodingAesKey"));

//		config.setToken(OptionQuery.me().findValue("qywechat_token"));
//		config.setCorpId(OptionQuery.me().findValue("qywechat_corpId"));
//		config.setAgentId(OptionQuery.me().findValue("qywechat_agentId"));
//		config.setCorpSecret(OptionQuery.me().findValue("qywechat_contact_secret"));
		
		// 企业号只支持加密且必须配置
//		config.setEncryptMessage(OptionQuery.me().findValueAsBool("qywechat_encryptMessage"));
		config.setEncryptMessage(true);
//		config.setEncodingAesKey(OptionQuery.me().findValue("qywechat_encodingAesKey"));
		
		return config;
		
	}
	
}
