package org.ccloud.sms;

import java.util.Date;

import org.ccloud.core.BaseFrontController;
import org.ccloud.interceptor.SessionInterceptor;
import org.ccloud.model.SmsCode;
import org.ccloud.notify.sms.AlidayuSmsSender;
import org.ccloud.notify.sms.SmsMessage;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Clear;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

@RouterMapping(url = "/smsCode")
@Clear({SessionInterceptor.class})
public class SmsCodeController extends BaseFrontController{

	public void index() {
		//render("user.html");
		renderNull();
	}
	
	public void send() {
		
		final String mobile = getPara("mobile");
		String code = StringUtils.getRandomCode(6, 0);
		
		Prop prop = PropKit.use("sms.properties");
		String content = prop.get("sms_content_template_bind", "验证码${code}，您正在进行${product}身份验证，打死不要告诉别人哦！");
		
		SmsCode smsCode = new SmsCode();
		smsCode.setAuthCode(code);
		smsCode.setMobile(mobile);
		smsCode.setContent(content.replace("${code}", code).replace("${product}", "协同云平台"));
		smsCode.setSendTime(new Date());
		
		smsCode.saveOrUpdate();
		
		SmsMessage sms = new SmsMessage();

		sms.setContent(content);
		sms.setRec_num(mobile);
		sms.setTemplate(prop.get("sms_template_id", "SMS_69840314"));
		sms.setParam("{\"code\":\"" + code + "\",\"product\":\"协同云平台\"}");
		sms.setSign_name(prop.get("sms_sign_name_jy"));
		
		boolean sendOk = new AlidayuSmsSender().send(sms);
		
		if (sendOk) {
			renderAjaxResultForSuccess();
			return ;
		}
		
		renderAjaxResultForError("短信发送失败!");
		
	}
	
}
