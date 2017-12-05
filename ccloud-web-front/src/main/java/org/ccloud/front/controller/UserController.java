package org.ccloud.front.controller;

import java.sql.SQLException;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.SmsCode;
import org.ccloud.model.User;
import org.ccloud.model.query.SmsCodeQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
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
			//System.err.println(wxUserResult.getJson());
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
						user.setAvatar(wxUserResult.getStr("headimgurl"));
						user.setNickname(wxUserResult.getStr("nickname"));
						user.setWechatOpenId(openId);
						if (user.saveOrUpdate()) {
							
							// 获取用户权限
							
						} else {
							return false;
						}
						
					}
				}
				
				return true;
			}
		});
		
		if (updated) {
			renderAjaxResultForSuccess("绑定手机号成功");
			return ;
		}
		renderAjaxResultForError("绑定手机号失败");
	}

}
