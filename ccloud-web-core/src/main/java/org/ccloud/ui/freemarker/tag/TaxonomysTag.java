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
import org.ccloud.model.ModelSorter;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.query.TaxonomyQuery;

public class TaxonomysTag extends JTag {

	public static final String TAG_NAME = "cc.taxonomys";

	private List<Taxonomy> filterList;

	public TaxonomysTag() {
	}

	public TaxonomysTag(List<Taxonomy> taxonomys) {
		filterList = taxonomys;
	}

	@Override
	public void onRender() {

		String sellerId = getParam("sellerId");
		Integer count = getParamToInt("count");
		String module = getParam("module");
		String activeClass = getParam("activeClass", "active");
		String type = getParam("type");
		String orderby = getParam("orderBy");
		String parentId = getParam("parentId");
		Boolean asTree = getParamToBool("asTree");

		List<Taxonomy> list = TaxonomyQuery.me().findListByModuleAndType(module, type, parentId, count, orderby, sellerId);
		if (filterList != null && list != null && list.size() > 0) {
			for (Taxonomy taxonomy : list) {
				taxonomy.initFilterList(filterList, activeClass);
			}
		}

		if (asTree != null && asTree == true) {
			ModelSorter.tree(list);
		}

		setVariable("taxonomys", list);
		renderBody();
	}

}
