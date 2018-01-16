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
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.PrintTemplate;
import org.ccloud.model.SellerJoinTemplate;
import org.ccloud.model.query.PrintTemplateQuery;
import org.ccloud.model.query.SellerJoinTemplateQuery;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/sellerJoinTemplate", viewPath = "/WEB-INF/admin/seller_join_tempate")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SellerJoinTemplateController extends JBaseCRUDController<SellerJoinTemplate> { 

	@Override
	public void index() {
		render("index.html");
	}
	
	public void list() {
		String sellerId = getSessionAttr("sellerId");
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<SellerJoinTemplate> page = SellerJoinTemplateQuery.me().paginate(getPageNumber(), getPageSize(),keyword,  "id",sellerId);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
		
	}
	
	public void saveTemplate(){
		final SellerJoinTemplate template = getModel(SellerJoinTemplate.class);
		String ds = getPara("orderItems");
		String sellerId = getPara("sellerId");
		boolean result = false;
		JSONArray jsonArray = JSONArray.parseArray(ds);
		List<SellerJoinTemplate> imageList = jsonArray.toJavaList(SellerJoinTemplate.class);
		for (SellerJoinTemplate sellerJoinTemplate : imageList) {
			String sellerJoinTemplateId = StrKit.getRandomUUID();
			SellerJoinTemplate sellerJoinTemplate2 = SellerJoinTemplateQuery.me().findByTemplateId(sellerJoinTemplate.getPrintTemplateId(),sellerId);
			
			if(sellerJoinTemplate2 !=null){
				continue;
			}
			template.set("id", sellerJoinTemplateId);
			template.set("seller_id", sellerId);
			template.set("print_template_id", sellerJoinTemplate.getPrintTemplateId());
			template.set("name", sellerJoinTemplate.getName());
			result=template.save();
			if(result == false){
				break;
			}
		}
		renderJson(result);
	}
	
	public void edit(){
		String id = getPara("id");
		if(!id.equals("")){
			SellerJoinTemplate sellerJoinTemplate = SellerJoinTemplateQuery.me().findAllById(id);
			setAttr("sellerJoinTemplate", sellerJoinTemplate);
		}
	}
	
	public void save(){
		String id = getPara("id");
		String name = getPara("name");
		boolean flang = false;
		if(!id.equals("")){
			SellerJoinTemplate sellerJoinTemplate = SellerJoinTemplateQuery.me().findAllById(id);
			sellerJoinTemplate.set("name", name);
			flang=sellerJoinTemplate.update();
		}
		renderJson(flang);
	}
	
	public void saveAll(){
		String ds = getPara("orderItems");
		boolean result = false;
		JSONArray jsonArray = JSONArray.parseArray(ds);
		List<SellerJoinTemplate> imageList = jsonArray.toJavaList(SellerJoinTemplate.class);
		for (SellerJoinTemplate sellerJoinTemplate : imageList) {
			SellerJoinTemplate sellerJoinTemplate1 = SellerJoinTemplateQuery.me().findAllById(sellerJoinTemplate.getId());
			sellerJoinTemplate1.set("name", sellerJoinTemplate.getName());
			result=sellerJoinTemplate1.update();
			if(result == false){
				break;
			}
		}
		renderJson(result);
	}
	
	public void listTemplate(){
		String keyword = getPara("k");
		String sellerId = getPara("sellerId");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		Page<PrintTemplate> page = PrintTemplateQuery.me().paginateSellerJoinTemplate(getPageNumber(), getPageSize(), keyword,sellerId, "cp.create_date");

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
}
