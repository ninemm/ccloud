/**
 * Copyright (c) 2011-2013, dafei 李飞 (myaniu AT gmail DOT com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.shiro.core;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.ccloud.model.SystemLog;
import org.ccloud.model.User;
import org.ccloud.model.query.OperationQuery;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

public class ShiroInterceptor implements Interceptor {

	public void intercept(Invocation ai) {
		String actionKey = ai.getActionKey();
		AuthzHandler ah = ShiroKit.getAuthzHandler(actionKey);

		Controller controller = ai.getController();
		User user = controller.getSessionAttr("user");
		String operationId = OperationQuery.me().findIdByUrl(actionKey);

		SystemLog systemlog = controller.getAttr("systemLog");
		if (user != null) {
			systemlog.setUserId(user.getId());
		}
		systemlog.setOperationId(operationId);
		// 存在访问控制处理器。
		if (ah != null) {
			try {
				// 执行权限检查。
				ah.assertAuthorized();
			} catch (UnauthenticatedException lae) {
				// RequiresGuest，RequiresAuthentication，RequiresUser，未满足时，抛出未经授权的异常。
				// 如果没有进行身份验证，返回HTTP401状态码,或者跳转到默认登录页面
				if (StrKit.notBlank(ShiroKit.getLoginUrl())) {
					// 保存登录前的页面信息,只保存GET请求。其他请求不处理。
					if (controller.getRequest().getMethod().equalsIgnoreCase("GET")) {
						controller.setSessionAttr(ShiroKit.getSavedRequestKey(), actionKey);
					}
					controller.redirect(ShiroKit.getLoginUrl());
				} else {
					controller.renderError(401);
				}
				systemlog.setStatus(0);
				systemlog.setDescription("没有登录");
				return;
			} catch (AuthorizationException ae) {
				// RequiresRoles，RequiresPermissions授权异常
				// 如果没有权限访问对应的资源，返回HTTP状态码403，或者调转到为授权页面
				if (StrKit.notBlank(ShiroKit.getUnauthorizedUrl())) {
					controller.redirect(ShiroKit.getUnauthorizedUrl());
				} else {
					controller.renderError(403);
				}
				systemlog.setStatus(0);
				systemlog.setDescription("权限认证失败");
				return;
			}
		}
		// 执行正常逻辑
		systemlog.setStatus(1);
		systemlog.setDescription("成功");
		ai.invoke();
	}
}
