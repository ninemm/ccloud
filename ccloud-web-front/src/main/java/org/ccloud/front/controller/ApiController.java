/**
 * Copyright (c) 2015-2016, Eric Huang (ninemm@qq.com).
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
package org.ccloud.front.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.model.Content;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.User;
import org.ccloud.model.query.ContentQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.TaxonomyQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.template.TemplateManager;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/api")
public class ApiController extends JBaseController {

	/**
	 * http://www.xxx.com/api?method=queryTest
	 * 
	 * 如果查询的方法不是query开头的方法，需要在后台添加appkey和appsecret
	 */

	public void index() {

		Boolean apiCorsEnable = OptionQuery.me().findValueAsBool("api_cors_enable");
		if (apiCorsEnable != null && apiCorsEnable == true) {
			getResponse().setHeader("Access-Control-Allow-Origin", "*");
			getResponse().setHeader("Access-Control-Allow-Methods", "GET,POST");
		}

		Boolean apiEnable = OptionQuery.me().findValueAsBool("api_enable");
		if (apiEnable == null || apiEnable == false) {
			renderAjaxResult("api is not open", 1);
			return;
		}

		String method = getPara("method");
		if (StringUtils.isBlank(method)) {
			renderAjaxResultForError("method must not empty!");
			return;
		}

		if (method.startsWith("query")) {
			doInvoke(method);
			return;
		}

		String appkey = getPara("appkey");
		if (StringUtils.isBlank(appkey)) {
			renderAjaxResultForError("appkey must not empty!");
			return;
		}

		Content content = ContentQuery.me().findFirstByModuleAndText(Consts.MODULE_API_APPLICATION, appkey);
		if (content == null) {
			renderAjaxResultForError("appkey is error!");
			return;
		}

		String appSecret = content.getFlag();

		String sign = getPara("sign");
		if (!StringUtils.isNotBlank(sign)) {
			renderAjaxResultForError("sign must not empty!");
			return;
		}

		String sign_method = getPara("sign_method");
		if (!StringUtils.isNotBlank(sign_method)) {
			renderAjaxResultForError("sign_method must not empty!");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> oParams = getParaMap();
		if (oParams != null) {
			for (Map.Entry<String, String[]> entry : oParams.entrySet()) {
				String value = entry.getValue() == null ? "" : (entry.getValue()[0] == null ? "" : entry.getValue()[0]);
				params.put(entry.getKey(), value);
			}
		}
		params.remove("sign");

		String mySign = EncryptUtils.signForRequest(params, appSecret);
		if (!sign.equals(mySign)) {
			renderAjaxResultForError("sign is error!");
			return;
		}

		doInvoke(method);
	}

	private void doInvoke(String method) {
		try {
			invoke(method);
		} catch (NoSuchMethodException e) {
			renderAjaxResultForError("hava no this method : " + method);
			return;
		} catch (Throwable e) {
			renderAjaxResultForError("system error!");
			return;
		}
	}

	private void invoke(String methodName) throws NoSuchMethodException, Throwable {
		Method method = ApiController.class.getDeclaredMethod(methodName);
		if (method == null) {
			throw new NoSuchMethodException();
		}
		method.setAccessible(true);
		method.invoke(this);
	}

	/////////////////////// api methods////////////////////////////

//	/**
//	 * test api
//	 */
//	private void queryTest() {
//		renderAjaxResultForSuccess("test ok!");
//	}

	/**
	 * 查询content的api
	 */
	public void queryContent() {
		String id = getPara("id");
		if (id == null) {
			renderAjaxResultForError("id is null");
			return;
		}

		Content c = ContentQuery.me().findById(id);
		if (c == null) {
			renderAjaxResultForError("can't find by id:" + id);
			return;
		}
		renderAjaxResult("success", 0, c);
	}

	/**
	 * 分页查询content信息
	 */
	public void queryContentPage() {
		int page = getParaToInt("page", 1);
		if (page < 1) {
			page = 1;
		}

		int pagesize = getParaToInt("pagesize", 10);
		if (pagesize < 1 || pagesize > 100) {
			pagesize = 10;
		}

		String[] modules = null;
		String modulesString = getPara("module");
		if (modulesString != null) {
			modules = modulesString.split(",");
			List<String> moduleList = new ArrayList<String>();
			for (int i = 0; i < modules.length; i++) {
				String module = modules[i];
				if (TemplateManager.me().currentTemplateModule(modules[i]) != null) {
					moduleList.add(module);
				}
			}
			if (!moduleList.isEmpty()) {
				modules = moduleList.toArray(new String[] {});
			}
		}

		if (modules == null) {
			modules = TemplateManager.me().currentTemplateModulesAsArray();
		}

		String keyword = getPara("keyword");
		String status = getPara("status");

		String[] taxonomyIds = null;
		String taxonomyIdString = getPara("taxonomyid");
		if (taxonomyIdString != null) {
			String[] taxonomyIdStrings = taxonomyIdString.split(",");
			List<String> ids = new ArrayList<String>();
			for (String idString : taxonomyIdStrings) {
				ids.add(idString);
			}
			taxonomyIds = ids.toArray(new String[] {});
		}

		String userId = getPara("userid");
		String month = getPara("month");
		String orderBy = getPara("orderBy");

		Page<Content> contentPage = ContentQuery.me().paginate(page, pagesize, modules, keyword, status, taxonomyIds,
				userId, month, orderBy);

		renderAjaxResultForSuccess("success", contentPage);
	}

	/**
	 * 查询分类
	 */
	public void queryTaxonomy() {
		String id = getPara("id");
		if (id == null) {
			renderAjaxResultForError();
			return;
		}
		Taxonomy t = TaxonomyQuery.me().findById(id);
		if (t == null) {
			renderAjaxResultForError();
			return;
		}
		renderAjaxResultForSuccess("success", t);
	}

	/**
	 * 查询分类列表
	 */
	public void queryTaxonomys() {
		String id = getPara("id");
		String type = getPara("type");
		if (id == null) {
			renderAjaxResultForError();
			return;
		}
		List<Taxonomy> taxonomys = null;
		if (StringUtils.isBlank(type)) {
			taxonomys = TaxonomyQuery.me().findListByContentId(id);
		} else {
			taxonomys = TaxonomyQuery.me().findListByTypeAndContentId(type, id);
		}
		
		renderAjaxResultForSuccess("success", taxonomys);
	}

	/**
	 * 查询用户信息
	 */
	public void queryUser() {
		String id = getPara("id");
		if (id == null) {
			renderAjaxResultForError();
			return;
		}

		User user = UserQuery.me().findById(id);
		if (user == null) {
			renderAjaxResultForError();
		}

		user.remove("password", "salt", "username", "email", "email_status", "mobile", "mobile_status", "role");
		renderAjaxResultForSuccess("success", user);
	}
	
	
	
	public void queryScore() {
		/*Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		BigInteger userId = new BigInteger(CookieUtils.get(this, Consts.COOKIE_LOGINED_USER));
		
		Page<TourScoreItem> page = TourScoreItemQuery.me().paginate(pageNumber, pageSize, userId, null, null);
		List<TourScoreItem> list = page.getList();
		Ret ret = Ret.create();
		StringBuilder strBuilder = new StringBuilder();
		
		for (TourScoreItem score : list) {
			strBuilder.append("<div class=\"weui-panel__bd\">");
			strBuilder.append("		<div class=\"weui-media-box weui-media-box_text\">");
			strBuilder.append("			<h4 class=\"weui-media-box__title txt-color-green\">+<em class=\"num\">" + score.getScore() + "</em> 积分</h4>");
			strBuilder.append("			<p class=\"weui-media-box__desc\">" + score.getScoreItem() + "</p>");
			strBuilder.append("			<ul class=\"weui-media-box__info\">");
			strBuilder.append("				<li class=\"weui-media-box__info__meta\">交易时间：<em class=\"num\">" + score.getCreateDate() + "</em></li>");
			strBuilder.append("			</ul>");
			strBuilder.append("		</div>");
			strBuilder.append("</div>");
		}
		ret.set("isEnd", list.size() >= pageSize ? false : true);
		ret.set("scoreData", strBuilder.toString());
		
		renderAjaxResultForSuccess("success", ret);*/
	}
	
}
