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

import org.ccloud.Consts;
import org.ccloud.core.render.freemarker.JTag;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.query.TaxonomyQuery;

public class TagsTag extends JTag {
	public static final String TAG_NAME = "cc.tags";

	@Override
	public void onRender() {

		String sellerId = getParam("sellerId");
		int count = getParamToInt("count", 0);
		count = count <= 0 ? 10 : count;

		String orderby = getParam("orderBy");

		String module = getParam("module", Consts.MODULE_ARTICLE);
		List<Taxonomy> list = TaxonomyQuery.me().findListByModuleAndType(module, "tag", null, count, orderby, sellerId);
		setVariable("tags", list);

		renderBody();
	}

}
