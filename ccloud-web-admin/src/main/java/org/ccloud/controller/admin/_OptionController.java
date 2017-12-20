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

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;

@RouterMapping(url = "/admin/option", viewPath = "/WEB-INF/admin/option")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _OptionController extends JBaseController {
	
	@RequiresPermissions(value={"/admin/option","/admin/all"},logical=Logical.OR)
	public void index() {
		render((getPara() == null ? "web" : getPara()) + ".html");
	}

	@RequiresPermissions(value={"/admin/option","/admin/all"},logical=Logical.OR)
	@Before(UCodeInterceptor.class)
	public void save() {

		HashMap<String, String> filesMap = getUploadFilesMap();

		HashMap<String, String> datasMap = new HashMap<String, String>();

		Map<String, String[]> paraMap = getParaMap();
		if (paraMap != null && !paraMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : paraMap.entrySet()) {
				if (entry.getValue() != null && entry.getValue().length > 0) {
					String value = null;
					for (String v : entry.getValue()) {
						if (StringUtils.isNotEmpty(v)) {
							value = v;
							break;
						}
					}
					datasMap.put(entry.getKey(), value);
				}
			}
		}

		String autosaveString = getPara("autosave");
		if (StringUtils.isNotBlank(autosaveString)) {
			String[] keys = autosaveString.split(",");
			for (String key : keys) {
				if (StringUtils.isNotBlank(key) && !datasMap.containsKey(key)) {
					datasMap.put(key.trim(), getRequest().getParameter(key.trim()));
				}
			}
		}
		
		if(filesMap!=null && !filesMap.isEmpty()){
			datasMap.putAll(filesMap);
		}

		for (Map.Entry<String, String> entry : datasMap.entrySet()) {
			OptionQuery.me().saveOrUpdate(entry.getKey(), entry.getValue());
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, datasMap);
		renderAjaxResultForSuccess();
	}
	
	@RequiresPermissions(value={"/admin/option/seller"})
	@Before(UCodeInterceptor.class)
	public void saveBySeller() {

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		
		HashMap<String, String> datasMap = new HashMap<String, String>();
		Map<String, String[]> paraMap = getParaMap();
		
		if (paraMap != null && !paraMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : paraMap.entrySet()) {
				if (entry.getValue() != null && entry.getValue().length > 0) {
					String value = null;
					for (String v : entry.getValue()) {
						if (StringUtils.isNotEmpty(v)) {
							value = v;
							break;
						}
					}
					datasMap.put(entry.getKey(), value);
				}
			}
		}
		String seller_store_check = getPara("seller_store_check_"+sellerCode);
		String comment_need_procedure = getPara("comment_need_procedure_"+sellerCode);
		if(!StringUtils.isNotBlank(seller_store_check)) {
			datasMap.put("seller_store_check_"+sellerCode, "0");
		}
		if(!StringUtils.isNotBlank(comment_need_procedure)) {
			datasMap.put("comment_need_procedure_"+sellerCode, "0");
		}
		
		String comment_need_goods_number = getPara("comment_need_goods_number_"+sellerCode);
		String comment_need_goods_price = getPara("comment_need_goods_price_"+sellerCode);
		if(!StringUtils.isNotBlank(comment_need_goods_number)) {
			datasMap.put("comment_need_goods_number_"+sellerCode, "0");
		}
		if(!StringUtils.isNotBlank(comment_need_goods_price)) {
			datasMap.put("comment_need_goods_price_"+sellerCode, "0");
		}
		
		
		for (Map.Entry<String, String> entry : datasMap.entrySet()) {
			OptionQuery.me().saveOrUpdateBySellerId(entry.getKey(), entry.getValue(), sellerId);
		}

		MessageKit.sendMessage(Actions.SETTING_CHANGED, datasMap);
		renderAjaxResultForSuccess();
	}	
	
	@RequiresPermissions(value={"/admin/option/seller"})
	public void seller() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String value = OptionQuery.me().findByKeyAndSellerId(Consts.OPTION_SELLER_STORE_CHECK, sellerId);
		setAttr("value", value);
	}
	
}
