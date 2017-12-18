/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
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
package org.ccloud.core;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccloud.Consts;
import org.ccloud.install.InstallUtils;
import org.ccloud.log.SystemLogThread;
import org.ccloud.model.SystemLog;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.route.RouterManager;
import org.ccloud.template.TemplateManager;
import org.ccloud.ui.freemarker.tag.MenusTag;
import org.ccloud.utils.FileUtils;
import org.ccloud.utils.RequestUtils;
import org.ccloud.utils.StringUtils;
import org.joda.time.DateTime;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.StrKit;
import com.jfinal.render.FreeMarkerRender;

import freemarker.ext.servlet.HttpSessionHashModel;

public class JHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		
		SystemLog systemLog = initSysLog(request);
		long startTime = DateTime.now().getMillis();
		systemLog.setStartDate(new Date(startTime));
		request.setAttribute(Consts.ATTR_GLOBAL_SYSTEM_LOG, systemLog);
		
		if (target.startsWith("/websocket")) {
			return;
		} else if (target.contains("/processEditor")) { // 流程引擎
			return ;
		}
		
		//HttpSessionHashModel session = new HttpSessionHashModel(request.getSession(), FreeMarkerRender.getConfiguration().getObjectWrapper());
		
		String CPATH = request.getContextPath();
		request.setAttribute("REQUEST", request);
		//request.setAttribute("SESSION", session);
		request.setAttribute("CPATH", CPATH);
		request.setAttribute("SPATH", CPATH + "/static");
		request.setAttribute("JPRESS_VERSION", CCloud.VERSION);

		// 程序还没有安装
		if (!CCloud.isInstalled()) {
			if (target.indexOf('.') != -1) {
				return;
			}

			if (!target.startsWith("/install")) {
				processNotInstall(request, response, isHandled);
				return;
			}
		}

		// 安装完成，但还没有加载完成...
		if (CCloud.isInstalled() && !CCloud.isLoaded()) {
			if (target.indexOf('.') != -1) {
				return;
			}

			InstallUtils.renderInstallFinished(request, response, isHandled);
			return;
		}

		if (CCloud.isInstalled() && CCloud.isLoaded()) {
			setGlobalAttrs(request);
		}

		if (isDisableAccess(target)) {
			HandlerKit.renderError404(request, response, isHandled);
		}

		String originalTarget = target;
		target = RouterManager.converte(target, request, response);

		if (!originalTarget.equals(target)) {
			request.setAttribute("_original_target", originalTarget);
		}

		next.handle(target, request, response, isHandled);
		
		long endTime = DateTime.now().getMillis();
		systemLog.setEndDate(new Date(endTime));
		
		systemLog.setTotalCostTime(endTime - startTime);
		
		// 视图耗时
		long viewCostTime = 0;
		Object renderTime = request.getAttribute("renderTime");
		if (renderTime != null) {
			viewCostTime = (Long) renderTime;
		}
		systemLog.setViewCostTime(viewCostTime);
		systemLog.setActionCostTime(endTime - startTime - viewCostTime);
		SystemLogThread.add(systemLog);
	}

	private void processNotInstall(HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		String CPATH = request.getContextPath();
		HandlerKit.redirect(CPATH + "/install", request, response, isHandled);
	}

	private static boolean isDisableAccess(String target) {
		// 防止直接访问模板文件
		if (target.endsWith(".html") && target.startsWith("/templates")) {
			return true;
		}
		// 防止直接访问jsp文件页面
		if (".jsp".equalsIgnoreCase(FileUtils.getSuffix(target))) {
			return true;
		}

		return false;
	}

	private void setGlobalAttrs(HttpServletRequest request) {

		request.setAttribute(MenusTag.TAG_NAME, new MenusTag(request));

		if (null != TemplateManager.me().currentTemplate()) {
			request.setAttribute("TPATH", TemplateManager.me().currentTemplate().getPath());
			request.setAttribute("CTPATH", request.getContextPath() + TemplateManager.me().currentTemplate().getPath());
		} else {
			request.setAttribute("TPATH", "");
			request.setAttribute("CTPATH", request.getContextPath());
		}
		
		Boolean cdnEnable = OptionQuery.me().findValueAsBool("cdn_enable");
		if (cdnEnable != null && cdnEnable == true) {
			String cdnDomain = OptionQuery.me().findValue("cdn_domain");
			if (cdnDomain != null && !"".equals(cdnDomain.trim())) {
				request.setAttribute("CDN", cdnDomain);
			}
		}
		
		String version = OptionQuery.me().findValue("web_version");
		if(StringUtils.isNotBlank(version)) {
			request.setAttribute("JPRESS_VERSION", version);
		}
		
		request.setAttribute(Consts.ATTR_GLOBAL_WEB_NAME, OptionQuery.me().findValue("web_name"));
		request.setAttribute(Consts.ATTR_GLOBAL_WEB_TITLE, OptionQuery.me().findValue("web_title"));
		request.setAttribute(Consts.ATTR_GLOBAL_WEB_SUBTITLE, OptionQuery.me().findValue("web_subtitle"));
		request.setAttribute(Consts.ATTR_GLOBAL_META_KEYWORDS, OptionQuery.me().findValue("meta_keywords"));
		request.setAttribute(Consts.ATTR_GLOBAL_META_DESCRIPTION, OptionQuery.me().findValue("meta_description"));
	}
	
	private SystemLog initSysLog(HttpServletRequest request) {
		
		String requestPath = RequestUtils.getRequestURIWithParam(request);
		String ip = RequestUtils.getIpAddress(request);
		String referer = request.getHeader("Referer"); 
		String userAgent = request.getHeader("User-Agent");
		String cookie = request.getHeader("Cookie");
		String method = request.getMethod();
		String xRequestedWith = request.getHeader("X-Requested-With");
		String host = request.getHeader("Host");
		String acceptLang = request.getHeader("Accept-Language");
		String acceptEncoding = request.getHeader("Accept-Encoding");
		String accept = request.getHeader("Accept");
		String connection = request.getHeader("Connection");
		
		SystemLog systemLog = new SystemLog();
		systemLog.setId(StrKit.getRandomUUID());
		systemLog.setIp(ip);
		systemLog.setRequestPath(requestPath);
		systemLog.setReferer(referer);
		systemLog.setAccept(accept);
		systemLog.setAcceptEncoding(acceptEncoding);
		systemLog.setAcceptLang(acceptLang);
		systemLog.setUserAgent(userAgent);
		systemLog.setCookie(cookie);
		systemLog.setMethod(method);
		systemLog.setXrequestedwith(xRequestedWith);
		systemLog.setHost(host);
		systemLog.setConnection(connection);
		
		return systemLog;
	}

}
