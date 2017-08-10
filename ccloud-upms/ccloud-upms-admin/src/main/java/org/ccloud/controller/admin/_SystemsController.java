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

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Systems;
import org.ccloud.model.query.SystemsQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/systems", viewPath = "/WEB-INF/admin/systems")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SystemsController extends JBaseCRUDController<Systems> { 

	@Override
	public void index() {
		
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) setAttr("k", keyword);
		
		
		Page<Systems> page = SystemsQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "order_list");
		if (page != null) {
			setAttr("page", page);
		}
		
	}
	
	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		int count = SystemsQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}
	
}
