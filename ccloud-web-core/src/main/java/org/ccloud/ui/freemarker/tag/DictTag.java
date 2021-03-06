/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
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
package org.ccloud.ui.freemarker.tag;

import java.util.List;

import org.ccloud.core.render.freemarker.JTag;
import org.ccloud.model.Dict;
import org.ccloud.model.query.DictQuery;

public class DictTag extends JTag {

	public static final String TAG_NAME = "cc.dict";
	
	@Override
	public void onRender() {

		String type = getParam("type");// 类型
		
		List<Dict> list = DictQuery.me().findDictByType(type);
		
		if (list == null || list.isEmpty()) {
			renderText("");
			return;
		}
		setVariable("dicts", list);
		
		renderBody();
	}

}
