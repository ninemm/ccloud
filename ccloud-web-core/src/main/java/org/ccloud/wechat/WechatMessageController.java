/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.wechat;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.jfinal.weixin.sdk.api.ShorturlApi;
import org.ccloud.Consts;
import org.ccloud.cache.JCacheKit;
import org.ccloud.core.JSession;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.model.Content;
import org.ccloud.model.query.ContentQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.template.TemplateManager;
import org.ccloud.template.TplModule;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.weixin.iot.msg.InEquDataMsg;
import com.jfinal.weixin.iot.msg.InEqubindEvent;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.jfinal.MsgController;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InMsg;
import com.jfinal.weixin.sdk.msg.in.InNotDefinedMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.card.InCardPassCheckEvent;
import com.jfinal.weixin.sdk.msg.in.card.InCardPayOrderEvent;
import com.jfinal.weixin.sdk.msg.in.card.InCardSkuRemindEvent;
import com.jfinal.weixin.sdk.msg.in.card.InUserCardEvent;
import com.jfinal.weixin.sdk.msg.in.card.InUserConsumeCardEvent;
import com.jfinal.weixin.sdk.msg.in.card.InUserGetCardEvent;
import com.jfinal.weixin.sdk.msg.in.card.InUserGiftingCardEvent;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InNotDefinedEvent;
import com.jfinal.weixin.sdk.msg.in.event.InPoiCheckNotifyEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent;
import com.jfinal.weixin.sdk.msg.in.event.InWifiEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutCustomMsg;
import com.jfinal.weixin.sdk.msg.out.OutMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;

import javax.servlet.http.HttpServletRequest;

@Clear(SessionInterceptor.class)
@RouterMapping(url = "/wechat")
public class WechatMessageController extends MsgController {

	public ApiConfig getApiConfig() {
		return WechatApi.getApiConfig();
	}
	
	@Before(WechatApiConfigInterceptor.class)
	public void callback() {
		String gotoUrl = getPara("goto") + "?state=" + getPara("state");
		String code = getPara("code");

		String appId = OptionQuery.me().findValue("wechat_appid");
		String appSecret = OptionQuery.me().findValue("wechat_appsecret");

		if (StringUtils.areNotBlank(appId, appSecret)) {
			ApiResult result = WechatApi.getOpenId(appId, appSecret, code);
			if (result != null) {
				getRequest().getSession().setAttribute(Consts.SESSION_WECHAT_USER, result.getJson());
				//setSessionAttr(Consts.SESSION_WECHAT_USER, result.getJson());
				this.setSessionAttr(Consts.SESSION_WECHAT_USER, result.getJson());
			}
		}

		redirect(gotoUrl);
	}
	
	@Override
	public Controller setSessionAttr(String key, Object value) {
		new JSession(this).setAttribute(key, value);
		return this;
	}

	// 处理接收到的文本消息
	protected void processInTextMsg(InTextMsg inTextMsg) {
		String text = inTextMsg.getContent();
		processTextReplay(inTextMsg, text);
	}

	// 处理接收到点击菜单事件
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		if (InMenuEvent.EVENT_INMENU_CLICK.equals(inMenuEvent.getEvent())) {
			String text = inMenuEvent.getEventKey();
			processTextReplay(inMenuEvent, text);
		} else {
			renderNull();
		}
	}

	// 处理接收到的图片消息
	protected void processInImageMsg(InImageMsg inImageMsg) {
		processDefaultReplay("wechat_processInImageMsg", inImageMsg);
	}

	// 处理接收到的语音消息
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		processDefaultReplay("wechat_processInVoiceMsg", inVoiceMsg);
	}

	// 处理接收到的视频消息
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		processDefaultReplay("wechat_processInVideoMsg", inVideoMsg);
	}

	// 处理接收到的视频消息
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		// 同：processInVideoMsg
		processDefaultReplay("wechat_processInVideoMsg", inShortVideoMsg);
	}

	// 处理接收到的地址位置消息
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
		processDefaultReplay("wechat_processInLocationMsg", inLocationMsg);
	}

	// 处理接收到的链接消息
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		processDefaultReplay("wechat_processInLinkMsg", inLinkMsg);
	}

	// 处理接收到的多客服管理事件
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {

		// 关闭多客服
		if (InCustomEvent.EVENT_INCUSTOM_KF_CLOSE_SESSION.equals(inCustomEvent.getEvent())) {

		}

		processDefaultReplay("wechat_processInCustomEvent", inCustomEvent);
	}

	// 处理接收到的关注/取消关注事件
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {

		// 用户关注公众号了
		if (InFollowEvent.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEvent.getEvent())) {
			processDefaultReplay("wechat_processInFollowEvent", inFollowEvent);
		}

		// 如果为取消关注事件，将无法接收到传回的信息
		if (InFollowEvent.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEvent.getEvent())) {
			// 取消关注，无法发送消息给用户了，可以做一些系统处理。
		}

	}

	// 处理接收到的扫描带参数二维码事件
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
//		processDefaultReplay("wechat_processInQrCodeEvent", inQrCodeEvent);
		String replyContent = OptionQuery.me().findValue("wechat_processInQrCodeEvent");
		String scene_str = inQrCodeEvent.getEventKey().replace("qrscene_", "");

		HttpServletRequest request = getRequest();
		String redirectUrl = request.getScheme() + "://" + request.getServerName() + "/member/member/bind?scene_str=" + scene_str;
		String json = ShorturlApi.getShortUrl(redirectUrl).getJson();
		Map<String,Object> parse = JSON.parseObject(json);
		renderOutTextMsg(replyContent + ":" + parse.get("short_url"));
	}

	// 处理接收到的上报地理位置事件
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		processDefaultReplay("wechat_processInLocationEvent", inLocationEvent);
	}

	// 处理接收到的群发任务结束时通知事件
	protected void processInMassEvent(InMassEvent inMassEvent) {
		processDefaultReplay("wechat_processInMassEvent", inMassEvent);
	}

	// 处理接收到的语音识别结果
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		processDefaultReplay("wechat_processInSpeechRecognitionResults", inSpeechRecognitionResults);
	}

	// 处理接收到的模板消息是否送达成功通知事件
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		processDefaultReplay("wechat_processInTemplateMsgEvent", inTemplateMsgEvent);
	}

	// 处理微信摇一摇事件
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		processDefaultReplay("wechat_processInShakearoundUserShakeEvent", inShakearoundUserShakeEvent);
	}

	// 资质认证成功 || 名称认证成功 || 年审通知 || 认证过期失效通知
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
		processDefaultReplay("wechat_processInVerifySuccessEvent", inVerifySuccessEvent);
	}

	// 资质认证失败 || 名称认证失败
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		processDefaultReplay("wechat_processInVerifyFailEvent", inVerifyFailEvent);
	}

	// 门店在审核事件消息
	protected void processInPoiCheckNotifyEvent(InPoiCheckNotifyEvent inPoiCheckNotifyEvent) {
		processDefaultReplay("wechat_processInPoiCheckNotifyEvent", inPoiCheckNotifyEvent);
	}

	private void processTextReplay(InMsg message, String userInput) {

		// 多客服的相关处理
		if (dkfProcess(message, userInput)) {
			return;
		}

		// 自动回复
		Content content = ContentQuery.me().findFirstByModuleAndTitle(Consts.MODULE_WECHAT_REPLY, userInput);
		if (content != null && StringUtils.isNotBlank(content.getText())) {
			textOrSeniorRender(message, content.getText());
			return;
		}

		// 搜索相关
		if (searchProcess(message, userInput)) {
			return;
		}

		// 没有匹配
		processDefaultReplay("wechat_search_no_matching", message);
	}

	/**
	 * 搜索相关处理
	 * 
	 * @param message
	 * @param userInput
	 * @return
	 */
	private boolean searchProcess(InMsg message, String userInput) {
		List<TplModule> modules = TemplateManager.me().currentTemplateModules();
		if (StringUtils.isBlank(userInput) || modules == null || modules.size() == 0) {
			return false;
		}

		TplModule searchModule = null;
		TplModule nonePrefixModule = null;
		for (TplModule module : modules) {
			// 是否启用搜索
			Boolean bool = OptionQuery.me().findValueAsBool(String.format("wechat_search_%s_enable", module.getName()));
			if (bool == null || bool == false) {
				continue;
			}

			// 搜索关键字 前缀
			String prefix = OptionQuery.me().findValue(String.format("wechat_search_%s_prefix", module.getName()));
			if (StringUtils.isBlank(prefix) && nonePrefixModule == null) {
				nonePrefixModule = module;
				continue;
			}

			if (StringUtils.isNotBlank(prefix) && userInput.startsWith(prefix)) {
				searchModule = module;
				userInput = userInput.substring(prefix.length());
				break;
			}
		}

		if (searchModule == null) {
			searchModule = nonePrefixModule;
		}

		if (searchModule == null || StringUtils.isBlank(userInput)) {
			return false;
		}

		// 搜索结果数量
		Integer count = OptionQuery.me()
				.findValueAsInteger(String.format("wechat_search_%s_count", searchModule.getName()));
		if (count == null || count <= 0 || count > 10) {
			count = 10;
		}
		
		List<Content> contents = ContentQuery.me().searchByModuleAndTitle(searchModule.getName(), userInput, count);
		if (contents == null || contents.isEmpty()) {
			// 搜索不到内容时
			processDefaultReplay("wechat_search_none_content", message);
			return true;
		}

		String domain = OptionQuery.me().findValue("web_domain");
		if (StringUtils.isBlank(domain)) {
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent("您还没有配置您的域名，请先在后台的【设置】>【常规】里配置您的网站域名！");
			render(otm);
			return true;
		}

		OutNewsMsg out = new OutNewsMsg(message);
		for (Content content : contents) {
			News news = new News();
			news.setTitle(content.getTitle());
			news.setDescription(content.getSummary());
			news.setPicUrl(domain + content.getImage());
			news.setUrl(domain + content.getUrl());
			out.addNews(news);
		}
		render(out);
		return true;

	}

	/**
	 * 多客服相关处理
	 * 
	 * @param message
	 * @param userInput
	 * @return
	 */
	private boolean dkfProcess(InMsg message, String userInput) {
		String dkf_quit_key = OptionQuery.me().findValue("wechat_dkf_quit_key");
		if (StringUtils.isNotBlank(dkf_quit_key) && dkf_quit_key.equals(userInput)) {
			JCacheKit.remove("wechat_dkf", message.getFromUserName());

			String quit_message = OptionQuery.me().findValue("wechat_dkf_quit_message");
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent(quit_message);
			render(otm);

			return true;
		}

		Boolean isInDkf = JCacheKit.get("wechat_dkf", message.getFromUserName());
		if (isInDkf != null && isInDkf == true) {

			// 重新更新ehcache存储的开始时间，5分钟后失效。
			{
				JCacheKit.remove("wechat_dkf", message.getFromUserName());
				JCacheKit.put("wechat_dkf", message.getFromUserName(), true);
			}

			OutCustomMsg outCustomMsg = new OutCustomMsg(message);
			render(outCustomMsg);
			return true;
		}

		String dkf_enter_key = OptionQuery.me().findValue("wechat_dkf_enter_key");
		if (StringUtils.isNotBlank(dkf_enter_key) && dkf_enter_key.equals(userInput)) {
			// ehcache的过期时间为5分钟，如果用户5分钟未咨询，自动失效。
			JCacheKit.put("wechat_dkf", message.getFromUserName(), true);

			// 进入多客服
			String quit_message = OptionQuery.me().findValue("wechat_dkf_enter_message");
			OutTextMsg otm = new OutTextMsg(message);
			otm.setContent(quit_message);
			render(otm);

			return true;
		}

		return false;
	}

	private void processDefaultReplay(String optionKey, InMsg message) {

		String replyContent = OptionQuery.me().findValue(optionKey);

		if (StringUtils.isBlank(replyContent)) {
			renderNull();
			return;
		}

		textOrSeniorRender(message, replyContent);
	}

	private void textOrSeniorRender(InMsg message, String replyContent) {
		if (isSeniorReplay(replyContent)) {
			OutMsg outMsg = ProcesserManager.me().invoke(replyContent, message);
			if (outMsg != null) {
				render(outMsg);
			} else {
				renderConfigErrorMessage(message, replyContent);
			}
		} else {
			OutTextMsg outTextMsg = new OutTextMsg(message);
			outTextMsg.setContent(replyContent);
			render(outTextMsg);
		}
	}

	private void renderConfigErrorMessage(InMsg message, String replyContent) {
		OutTextMsg outTextMsg = new OutTextMsg(message);
		outTextMsg.setContent("配置错误，没有高级回复 " + replyContent + ",请联系网站管理员。");
		render(outTextMsg);
	}

	private static final boolean isSeniorReplay(String string) {
		return string != null && string.startsWith("[") && string.contains("]");
	}


	protected void processInWifiEvent(InWifiEvent inwifievent) {
		
	}

	protected void processIsNotDefinedEvent(InNotDefinedEvent innotdefinedevent) {
		
	}

	protected void processIsNotDefinedMsg(InNotDefinedMsg innotdefinedmsg) {
		
	}

	@Override
	protected void processInCardPassCheckEvent(InCardPassCheckEvent arg0) {
		
	}

	@Override
	protected void processInCardPayOrderEvent(InCardPayOrderEvent arg0) {
		
	}

	@Override
	protected void processInCardSkuRemindEvent(InCardSkuRemindEvent arg0) {
		
	}

	@Override
	protected void processInEquDataMsg(InEquDataMsg arg0) {
		
	}

	@Override
	protected void processInEqubindEvent(InEqubindEvent arg0) {
		
	}

	@Override
	protected void processInMerChantOrderEvent(com.jfinal.weixin.sdk.msg.in.card.InMerChantOrderEvent arg0) {
		
	}

	@Override
	protected void processInUpdateMemberCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUpdateMemberCardEvent arg0) {
		
	}

	@Override
	protected void processInUserCardEvent(InUserCardEvent arg0) {
		
	}

	@Override
	protected void processInUserConsumeCardEvent(InUserConsumeCardEvent arg0) {
		
	}

	@Override
	protected void processInUserGetCardEvent(InUserGetCardEvent arg0) {
		
	}

	@Override
	protected void processInUserGiftingCardEvent(InUserGiftingCardEvent arg0) {
		
	}

	@Override
	protected void processInUserPayFromCardEvent(com.jfinal.weixin.sdk.msg.in.card.InUserPayFromCardEvent arg0) {
		
	}
}
