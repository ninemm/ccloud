
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
import org.ccloud.model.Menu;
import org.ccloud.model.query.MenuQuery;

@Listener(action = MenuManager.ACTION_INIT_MENU, async = false)
public class AdminMenuInitListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		Object temp = message.getData();
		if (temp == null && !(temp instanceof MenuManager)) {
			return;
		}

		MenuManager menuManager = (MenuManager) temp;
		
		List<Menu> list = MenuQuery.me().findMenuList();
		this.createMenuList(list, menuManager);
		
	}
	
	private void createMenuList(List<Menu> list, MenuManager menuManager) {
		for (Menu menu : list) {
			if (menu.getIsParent() > 0) {
				MenuGroup group = new MenuGroup(menu.getParam(), menu.getIcon(), menu.getName());
				menuManager.addMenuGroup(group);
				this.findChild(menu, list, group);
			}
		}
	}
	
	private void findChild (Menu parent, List<Menu> list, MenuGroup group) {
		for (Menu menu : list) {
			if (menu.getParentId().equals(parent.getId())) {
				String url = menu.get("url") == null ? "" : menu.get("url").toString();
				MenuItem menuItem = new MenuItem(menu.getParam(), url, menu.getName());
				group.addMenuItem(menuItem);
			}
		}
	}
	
}
