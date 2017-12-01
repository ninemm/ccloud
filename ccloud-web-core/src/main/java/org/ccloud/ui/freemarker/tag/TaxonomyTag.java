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

import org.ccloud.core.render.freemarker.JTag;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.query.TaxonomyQuery;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;

public class TaxonomyTag extends JTag {

	public static final String TAG_NAME = "cc.taxonomy";

	@Override
	public void onRender() {

		Taxonomy taxonomy = null;
		String id = getParam("id");
		if (StrKit.notBlank(id)) {
			taxonomy = TaxonomyQuery.me().findById(id);
		}

		if (taxonomy == null) {
			String slug = getParam("slug");
			String module = getParam("module");
			if (StringUtils.areNotBlank(slug, module)) {
				taxonomy = TaxonomyQuery.me().findBySlugAndModule(slug, module);
			}
		}

		if (taxonomy == null) {
			renderText("");
			return;
		}

		setVariable("taxonomy", taxonomy);
		renderBody();
	}

}
