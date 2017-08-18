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
package org.ccloud.controller.admin;

import java.math.BigInteger;
import java.util.Date;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Dict;
import org.ccloud.model.query.DictQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.template.TemplateManager;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/admin/dict", viewPath = "/WEB-INF/admin/dict")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _DictController extends JBaseCRUDController<Dict> {

	public void index() {

		String keyword = getPara("k", "").trim();
		setAttr("k", keyword);
		
		Page<Dict> page = DictQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "value");
		setAttr("page", page);

		String templateHtml = "admin_user_index.html";
		if (TemplateManager.me().existsFile(templateHtml)) {
			setAttr("include", TemplateManager.me().currentTemplatePath() + "/" + templateHtml);
			return;
		}
		setAttr("include", "_index_include.html");
	}

	@Override
	public void edit() {
		BigInteger id = getParaToBigInteger("id");
		if (id != null) {
			setAttr("dict", DictQuery.me().findById(id));
		}

		String templateHtml = "admin_user_edit.html";
		if (TemplateManager.me().existsFile(templateHtml)) {
			setAttr("include", TemplateManager.me().currentTemplatePath() + "/" + templateHtml);
			return;
		}
		setAttr("include", "_edit_include.html");

	}

	@Override
	public void save() {
		
		final Dict dict = getModel(Dict.class);

		if (StringUtils.isBlank(dict.getType())) {
			renderAjaxResultForError("字典类型不能为空");
			return;
		}
		if (StringUtils.isBlank(dict.getKey())) {
			renderAjaxResultForError("字典编码不能为空");
			return;
		}
		if (StringUtils.isBlank(dict.getValue())) {
			renderAjaxResultForError("字典值不能为空");
			return;
		}
		
		if (dict.getId() == null)
			dict.setCreateDate(new Date());

		if (dict.saveOrUpdate()) {
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("false");
		}
	}

	@Override
	public void delete() {
		BigInteger id = getParaToBigInteger("id");
		final Dict r = DictQuery.me().findById(id);
		if (r != null) {
			if (r.delete()) {
				renderAjaxResultForSuccess();
				return;
			}
		}
		renderAjaxResultForError();
	}
	
	public void batchDelete() {
		BigInteger[] ids = getParaValuesToBigInteger("dataItem");
		int count = DictQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("success");
		} else {
			renderAjaxResultForError("batch delete error!");
		}
	}
	
}
