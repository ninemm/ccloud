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
package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.core.addon.HookInvoker;
import org.ccloud.core.cache.ActionCache;
import org.ccloud.model.Content;
import org.ccloud.model.Taxonomy;
import org.ccloud.model.query.ContentQuery;
import org.ccloud.model.query.TaxonomyQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.template.TemplateManager;
import org.ccloud.template.TplModule;
import org.ccloud.ui.freemarker.tag.MenusTag;
import org.ccloud.utils.StringUtils;

import com.jfinal.render.Render;

@RouterMapping(url = Consts.ROUTER_CONTENT)
public class ContentController extends BaseFrontController {

	private String slug;
	private String id;
	private int page;

	@ActionCache
	public void index() {
		try {
			Render render = onRenderBefore();
			if (render != null) {
				render(render);
			} else {
				doRender();
			}
		} finally {
			onRenderAfter();
		}

	}

	private void doRender() {
		initRequest();

		Content content = queryContent();
		if (null == content) {
			renderError(404);
			return;
		}

		TplModule module = TemplateManager.me().currentTemplateModule(content.getModule());

		if (module == null) {
			renderError(404);
			return;
		}

		updateContentViewCount(content);
		setGlobleAttrs(content);

		setAttr("p", page);
		setAttr("content", content);

		//setAttr(NextContentTag.TAG_NAME, new NextContentTag(content));
		//setAttr(PreviousContentTag.TAG_NAME, new PreviousContentTag(content));

		//setAttr(CommentPageTag.TAG_NAME, new CommentPageTag(getRequest(), content, page));

		List<Taxonomy> taxonomys = TaxonomyQuery.me().findListByContentId(content.getId());
		setAttr("taxonomys", taxonomys);
		setAttr(MenusTag.TAG_NAME, new MenusTag(getRequest(), taxonomys, content));

		String style = content.getStyle();
		if (StringUtils.isNotBlank(style)) {
			render(String.format("content_%s_%s.html", module.getName(), style.trim()));
			return;
		}

		style = tryGetTaxonomyTemplate(module, taxonomys);
		if (style != null) {
			render(String.format("content_%s_%s.html", module.getName(), style));
			return;
		}

		render(String.format("content_%s.html", module.getName()));

	}

	private String tryGetTaxonomyTemplate(TplModule module, List<Taxonomy> taxonomys) {
		if (taxonomys == null || taxonomys.isEmpty())
			return null;
		String forSlug = null;
		for (Taxonomy taxonomy : taxonomys) {
			String tFile = String.format("content_%s_%s%s.html", module.getName(), Consts.TAXONOMY_TEMPLATE_PREFIX,taxonomy.getSlug());
			if (templateExists(tFile)) {
				if (forSlug == null) {
					forSlug = Consts.TAXONOMY_TEMPLATE_PREFIX + taxonomy.getSlug();
				} else {
					forSlug = null;
					break;
				}
			}
		}
		return forSlug;
	}

	private void updateContentViewCount(Content content) {
		long visitorCount = VisitorCounter.getVisitorCount(content.getId(), "content");
		Long viewCount = content.getViewCount() == null ? visitorCount : content.getViewCount() + visitorCount;
		content.setViewCount(viewCount);
		if (content.update()) {
			VisitorCounter.clearVisitorCount(content.getId(), "content");
		}
	}

	private void setGlobleAttrs(Content content) {

		setAttr(Consts.ATTR_GLOBAL_WEB_TITLE, content.getTitle());

		if (StringUtils.isNotBlank(content.getMetaKeywords())) {
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, content.getMetaKeywords());
		} else {
			setAttr(Consts.ATTR_GLOBAL_META_KEYWORDS, content.getTaxonomyAsString(null));
		}

		if (StringUtils.isNotBlank(content.getMetaDescription())) {
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, content.getMetaDescription());
		} else {
			setAttr(Consts.ATTR_GLOBAL_META_DESCRIPTION, content.getSummary());
		}

	}

	private Content queryContent() {
		if (id != null) {
			return ContentQuery.me().findById(id);
		} else {
			return ContentQuery.me().findBySlug(StringUtils.urlDecode(slug));
		}
	}

	private void initRequest() {
		String para = getPara(0);
		if (StringUtils.isBlank(para)) {
			id = getPara("id");
			slug = getPara("slug");
			page = getParaToInt("p", 1);
			if (id == null && slug == null) {
				renderError(404);
			}
			return;
		}

		if (StringUtils.isNumeric(para)) {
			id = para;
		} else {
			slug = para;
		}
		page = getParaToInt(1, 1);

	}

	private Render onRenderBefore() {
		return HookInvoker.contentRenderBefore(this);
	}

	private void onRenderAfter() {
		HookInvoker.contentRenderAfter(this);
	}

}
