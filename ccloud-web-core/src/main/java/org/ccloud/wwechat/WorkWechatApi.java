package org.ccloud.wwechat;

import org.ccloud.model.query.OptionQuery;

import com.jfinal.qyweixin.sdk.api.ApiConfig;


public class WorkWechatApi {
	
	public static ApiConfig getApiConfig() {
		
		ApiConfig config = new ApiConfig();
		
//		config.setToken(OptionQuery.me().findValue("qywechat_token"));
		config.setCorpId(OptionQuery.me().findValue("qywechat_corpId"));
		config.setCorpSecret(OptionQuery.me().findValue("qywechat_secret"));
		config.setAgentId(OptionQuery.me().findValue("qywechat_agentId"));
		
		// 企业号只支持加密且必须配置
		config.setEncryptMessage(true);
		config.setEncodingAesKey(OptionQuery.me().findValue("qywechat_encodingAesKey"));
		
		return config;
		
	}
	
}
