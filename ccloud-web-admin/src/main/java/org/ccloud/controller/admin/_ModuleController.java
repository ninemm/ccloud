/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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

import java.util.List;
import java.util.Map;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Module;
import org.ccloud.model.query.ModuleQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/module", viewPath = "/WEB-INF/admin/module")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ModuleController extends JBaseCRUDController<Module> { 

	
	
public void list() {
		
		String keyword = getPara("k", "");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		
		String parentId = getPara("parentId", "0");
		
		Page<Module> page = ModuleQuery.me().paginate(getPageNumber(), getPageSize(), parentId, keyword, null);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
		
	}
	
		@Override
		@Before(UCodeInterceptor.class)
		public void save() {
			
			Module module = getModel(Module.class);
			module.setIsParent(0);
			if (module.saveOrUpdate())
				renderAjaxResultForSuccess("新增成功");
			else
				renderAjaxResultForError("修改失败!");
		}
	
	public void module_tree() {
		List<Map<String, Object>> list = ModuleQuery.me().findModuleListAsTree(1);
		setAttr("treeData", JSON.toJSON(list));
	}
}
