/**
 * Copyright (c) 2015-2016, Eric Huang (ninemm@qq.com).
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

import java.util.ArrayList;
import java.util.List;

import org.ccloud.core.render.freemarker.JTag;
import org.ccloud.model.Content;
import org.ccloud.model.ModelSorter;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.query.ContentQuery;
import org.ccloud.model.query.TaxonomyQuery;
import org.ccloud.utils.StringUtils;

public class ContentsTag extends JTag {

	public static final String TAG_NAME = "cc.contents";

	@Override
	public void onRender() {

		String sellerId = getParam("sellerId");
		String orderBy = getParam("orderBy");
		String keyword = getParam("keyword");

		int pageNumber = getParamToInt("page", 1);
		int pageSize = getParamToInt("pageSize", 10);

		Integer count = getParamToInt("count");
		if (count != null && count > 0) {
			pageSize = count;
		}

		String[] typeIds = getParamToStringArray("typeId");
		String[] typeSlugs = getParamToStringArray("typeSlug");
		String[] tags = getParamToStringArray("tag");
		String[] modules = getParamToStringArray("module");
		String[] styles = getParamToStringArray("style");
		String[] flags = getParamToStringArray("flag");
		String[] slugs = getParamToStringArray("slug");
		String[] userIds = getParamToStringArray("userId");
		String[] parentIds = getParamToStringArray("parentId");
		Boolean hasThumbnail = getParamToBool("hasThumbnail");

		Taxonomy upperTaxonomy = null;
		if (modules != null && modules.length == 1) {
			
			String upperId = getParam("upperId");
			
			if (upperId != null) {
				upperTaxonomy = TaxonomyQuery.me().findById(upperId);
			}

			if (upperTaxonomy == null) {
				String upperSlug = getParam("upperSlug");
				if (StringUtils.isNotBlank(upperSlug)) {
					upperTaxonomy = TaxonomyQuery.me().findBySlugAndModule(upperSlug, modules[0]);
				}
			}
		}

		if (upperTaxonomy != null) {
			List<Taxonomy> list = TaxonomyQuery.me().findListByModuleAndType(modules[0], null, sellerId);
			// 找到taxonomy id的所有孩子或孙子
			List<Taxonomy> newlist = new ArrayList<Taxonomy>();
			ModelSorter.sort(list, newlist, upperTaxonomy.getId(), 0);
			if (newlist != null && newlist.size() > 0) {
				slugs = null; // 设置 slugs无效
				typeIds = new String[newlist.size() + 1];
				typeIds[0] = upperTaxonomy.getId();
				for (int i = 1; i < typeIds.length; i++) {
					typeIds[i] = newlist.get(i - 1).getId();
				}
			}
		}

		List<Content> data = ContentQuery.me().findListInNormal(pageNumber, pageSize, orderBy, keyword, typeIds,
				typeSlugs, modules, styles, flags, slugs, userIds, parentIds, tags, hasThumbnail, null, sellerId);

		if (data == null || data.isEmpty()) {
			renderText("");
			return;
		}

		setVariable("contents", data);
		renderBody();
	}

}
