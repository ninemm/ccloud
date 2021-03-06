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
package org.ccloud.listener;

import java.util.List;

import org.ccloud.menu.MenuGroup;
import org.ccloud.menu.MenuItem;
import org.ccloud.menu.MenuManager;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;
import org.ccloud.template.Template;
import org.ccloud.template.TemplateManager;
import org.ccloud.template.TplModule;
import org.ccloud.template.TplTaxonomyType;
import org.ccloud.utils.StringUtils;

@Listener(action = MenuManager.ACTION_INIT_MENU, async = false)
public class AdminMenuInitListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		Object temp = message.getData();
		if (temp == null && !(temp instanceof MenuManager)) {
			return;
		}

		MenuManager menuManager = (MenuManager) temp;
		//initModuleMenuGroup(menuManager);

		menuManager.addMenuGroup(0, createOrderMenuGroup());
		menuManager.addMenuGroup(1, createStoreMenuGroup());
		menuManager.addMenuGroup(2, createCustomerMenuGroup());
		menuManager.addMenuGroup(3, createAttachmentMenuGroup());
		menuManager.addMenuGroup(4, createWechatMenuGroup());
		menuManager.addMenuGroup(5, createStatMenuGroup());
		
		menuManager.addMenuGroup(MenuGroup.createBlockGroup());
		
		/*menuManager.addMenuGroup(createTemplateMenuGroup());*/
		/*menuManager.addMenuGroup(createAddonMenuGroup());*/
		menuManager.addMenuGroup(createRoleResMenuGroup());
		menuManager.addMenuGroup(createDataMenuGroup());
		menuManager.addMenuGroup(createSettingMenuGroup());
		menuManager.addMenuGroup(createToolsMenuGroup()); 
	}

	public void initModuleMenuGroup(MenuManager menuMnager) {
		Template t = TemplateManager.me().currentTemplate();
		if (t == null || t.getModules() == null) {
			return;
		}

		List<TplModule> modules = t.getModules();
		for (TplModule module : modules) {
			String iconClass = module.getIconClass();
			if (StringUtils.isBlank(iconClass)) {
				iconClass = "fa fa-file-text-o";
			}
			MenuGroup group = new MenuGroup(module.getName(), iconClass, module.getTitle());

			String moduleName = module.getModuleName();
			if(StringUtils.isBlank(moduleName))
				moduleName = "content";
			
			group.addMenuItem(new MenuItem("list", "/admin/" + moduleName + "?m=" + module.getName(), module.getListTitle()));
			group.addMenuItem(new MenuItem("edit", "/admin/" + moduleName + "/edit?m=" + module.getName(), module.getAddTitle()));

			List<TplTaxonomyType> types = module.getTaxonomyTypes();
			if (types != null && !types.isEmpty()) {
				for (TplTaxonomyType type : types) {
					group.addMenuItem(new MenuItem(type.getName(),
						"/admin/taxonomy?m=" + module.getName() + "&t=" + type.getName(), type.getTitle()));
				}
			}

			if (StringUtils.isNotBlank(module.getCommentTitle())) {
				group.addMenuItem(new MenuItem("comment", "/admin/comment?t=comment&m=" + module.getName(),
					module.getCommentTitle()));
			}

			menuMnager.addMenuGroup(group);
		}
	}
	
	private MenuGroup createOrderMenuGroup() {
		MenuGroup group = new MenuGroup("order", "fa fa-cart-arrow-down", "订单管理");
		
		{
			group.addMenuItem(new MenuItem("unpaid", "/admin/order?s=101501", "订货单"));
			group.addMenuItem(new MenuItem("paid", "/admin/order?s=101502", "退货单"));
			group.addMenuItem(new MenuItem("confirmed", "/admin/order?s=101503", "订单指派"));
		}
		
		return group;
	}
	
	private MenuGroup createStoreMenuGroup() {
		MenuGroup group = new MenuGroup("store", "fa fa-barcode", "库存管理");
		
		{
			group.addMenuItem(new MenuItem("init", "/admin/promoter", "初始化"));
			group.addMenuItem(new MenuItem("setting", "/admin/promoter/setting", "出库管理"));
			group.addMenuItem(new MenuItem("commission", "/admin/promoter/commission", "入库管理"));
			
		}
		
		return group;
	}
	
	private MenuGroup createStatMenuGroup() {
		MenuGroup group = new MenuGroup("stat", "fa fa-bar-chart", "统计分析");
		
		{
			group.addMenuItem(new MenuItem("outline", "/admin/stat/outline", "统计概要"));
			group.addMenuItem(new MenuItem("in", "/admin/stat", "入库明细"));
			group.addMenuItem(new MenuItem("out", "/admin/stat", "出库明细"));
			
		}
		
		return group;
	}
	
	private MenuGroup createWechatMenuGroup() {
		MenuGroup group = new MenuGroup("wechat", "fa fa-weixin", "微信");

		{
			group.addMenuItem(new MenuItem("r", "/admin/wechat", "自动回复"));
			group.addMenuItem(new MenuItem("rd", "/admin/wechat/reply_default", "默认回复"));
			group.addMenuItem(new MenuItem("menu", "/admin/wechat/menu", "菜单设置"));
			group.addMenuItem(new MenuItem("option", "/admin/wechat/option", "微信设置"));
		}
		return group;
	}

	private MenuGroup createAttachmentMenuGroup() {
		MenuGroup group = new MenuGroup("attachment", "fa fa-file-image-o", "附件");

		{
			group.addMenuItem(new MenuItem("list", "/admin/attachment", "所有附件"));
			group.addMenuItem(new MenuItem("upload", "/admin/attachment/upload", "上传"));
		}
		return group;
	}

	private MenuGroup createCustomerMenuGroup() {
		MenuGroup group = new MenuGroup("customer", "fa fa-user", "客户管理");

		{
			group.addMenuItem(new MenuItem("list", "/admin/customer", "客户列表"));
		}
		return group;
	}

	public MenuGroup createTemplateMenuGroup() {
		MenuGroup group = new MenuGroup("template", "fa fa-magic", "模板");
		{
			group.addMenuItem(new MenuItem("list", "/admin/template", "所有模板"));
			group.addMenuItem(new MenuItem("install", "/admin/template/install", "模板安装"));
			group.addMenuItem(new MenuItem("menu", "/admin/template/menu", "菜单"));
			group.addMenuItem(new MenuItem("setting", "/admin/template/setting", "设置"));
			group.addMenuItem(new MenuItem("edit", "/admin/template/edit", "编辑"));
		}
		return group;
	}

//	private MenuGroup createAddonMenuGroup() {
//		MenuGroup group = new MenuGroup("addon", "fa fa-plug", "插件");
//		{
//			group.addMenuItem(new MenuItem("list", "/admin/addon", "所有插件"));
//			group.addMenuItem(new MenuItem("install", "/admin/addon/install", "安装"));
//			group.addMenuItem(new MenuItem("store", "/admin/store", "门店数据插件"));
//		}
//		return group;
//	}

	private MenuGroup createSettingMenuGroup() {
		MenuGroup group = new MenuGroup("option", "fa fa-cogs", "系统设置");
		{
			group.addMenuItem(new MenuItem("list", "/admin/option/web", "常规"));
			group.addMenuItem(new MenuItem("n", "/admin/option/notification", "通知"));
			group.addMenuItem(new MenuItem("cdn", "/admin/option/cdn", "CDN加速"));
			group.addMenuItem(new MenuItem("api", "/admin/api", "API应用"));
		}

		return group;
	}
	
	private MenuGroup createRoleResMenuGroup() {
		MenuGroup group = new MenuGroup("permission", "fa fa-cog", "权限管理");
		{
			group.addMenuItem(new MenuItem("user", "/admin/user", "用户"));
			group.addMenuItem(new MenuItem("group", "/admin/group", "分组"));
			group.addMenuItem(new MenuItem("role", "/admin/role", "角色"));
			group.addMenuItem(new MenuItem("station", "/admin/station", "岗位"));
			group.addMenuItem(new MenuItem("system", "/admin/system", "系统"));
			group.addMenuItem(new MenuItem("operation", "/admin/operation", "功能"));
			group.addMenuItem(new MenuItem("department", "/admin/department", "组织机构"));
		}
		
		return group;
	}
	
	private MenuGroup createDataMenuGroup() {
		MenuGroup group = new MenuGroup("data", "fa fa-database", "基础数据");
		{
			group.addMenuItem(new MenuItem("dict", "/admin/dict", "数据字典"));
			group.addMenuItem(new MenuItem("area", "/admin/area", "地区"));
		}
		
		return group;
	}

	private MenuGroup createToolsMenuGroup() {
		MenuGroup group = new MenuGroup("tools", "fa fa-wrench", "工具");

		{
			group.addMenuItem(new MenuItem("import", "/admin/tools/_import", "导入"));
			group.addMenuItem(new MenuItem("export", "/admin/tools/export", "导出"));
			group.addMenuItem(new MenuItem("sync", "/admin/tools/sync", "同步"));
			
		}
		return group;
	}

}
