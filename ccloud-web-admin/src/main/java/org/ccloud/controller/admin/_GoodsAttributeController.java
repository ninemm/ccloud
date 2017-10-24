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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.model.GoodsAttribute;
import org.ccloud.model.GoodsType;
import org.ccloud.model.query.GoodsAttributeQuery;
import org.ccloud.model.query.GoodsTypeQuery;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/goodsAttribute", viewPath = "/WEB-INF/admin/goods_attribute")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _GoodsAttributeController extends JBaseCRUDController<GoodsAttribute> { 

	@Override
	public void index() {
		
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) setAttr("k", keyword);
		
		String type = getPara("type");
		String typeName = getPara("typeName");
		try {
			if (StrKit.notBlank(typeName)) {
				typeName = new String(typeName.getBytes("ISO-8859-1"),"UTF-8");
				setAttr("typeName", typeName);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Page<GoodsAttribute> page = GoodsAttributeQuery.me().paginate(getPageNumber(), getPageSize(), keyword, type, "order_list");
		if (page != null) {
			setAttr("page", page);
		}
		
	}
	
	@Override
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			GoodsAttribute goodsAttribute = GoodsAttributeQuery.me().findById(id);
			setAttr("goodsAttribute", goodsAttribute);
		}
		List<GoodsType> typeList = GoodsTypeQuery.me().findAll();
		setAttr("typeList", typeList);
	}	
	
	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		int count = GoodsAttributeQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}
	
	public void getAttributeInput() {
		String typeId = getPara("typeId");
        List<GoodsAttribute> attributes = GoodsAttributeQuery.me().findByTypeId(typeId);
        List<Map<String, Object>> list = new ArrayList<>();

        for (GoodsAttribute attribute : attributes) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", attribute.getId());
            map.put("name", attribute.getName());
            map.put("is_required", attribute.getIsRequired());
            list.add(map);
        }
        renderJson(list);
	}
	
	public void enable() {
		String id = getPara("id");
		GoodsAttribute attribute = GoodsAttributeQuery.me().findById(id);
		if (attribute.getIsEnabled() == 0) {
			attribute.setIsEnabled(1);
		} else {
			attribute.setIsEnabled(0);
		}
		attribute.update();
		renderAjaxResultForSuccess("更新成功");
	}
	
}
