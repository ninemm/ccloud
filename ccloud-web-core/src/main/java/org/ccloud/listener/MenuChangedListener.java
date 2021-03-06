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

import java.util.Map;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;

@Listener(action = Actions.SETTING_CHANGED)
public class MenuChangedListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		Object temp = message.getData();
		if (temp != null && (temp instanceof Map)) {

			@SuppressWarnings("unchecked")
			Map<String, String> datas = (Map<String, String>) temp;
			// 路由状态发生变化
			if (datas.containsKey("router_content_type") || datas.containsKey("router_fakestatic_enable")) {
				updateMenus();
			}
		}
	}

	private void updateMenus() {
//		List<Content> list = ContentQuery.me().findByModule(Consts.MODULE_MENU, null, "order_number ASC");
//		if (list != null && list.size() > 0) {
//			for (Content content : list) {
//				BigInteger taxonomyId = content.getObjectId();
//				if (taxonomyId != null) {
//					Taxonomy taxonomy = TaxonomyQuery.me().findById(taxonomyId);
//					if (taxonomy != null) {
//						content.setText(taxonomy.getUrl());
//						content.saveOrUpdate();
//					}
//				}
//			}
//		}
	}

}
