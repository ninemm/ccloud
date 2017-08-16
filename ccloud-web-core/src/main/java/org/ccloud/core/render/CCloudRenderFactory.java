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
package org.ccloud.core.render;

import org.ccloud.core.CCloud;
import org.ccloud.template.Template;
import org.ccloud.template.TemplateManager;
import org.ccloud.utils.StringUtils;

import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;
import com.jfinal.render.TextRender;

public class CCloudRenderFactory extends RenderFactory {

	public CCloudRenderFactory() {
	}

	@Override
	public Render getRender(String view) {
		// front url
		if (view.startsWith("/templates")) {
			String renderType = TemplateManager.me().currentTemplate().getRenderType();

			if (renderType == null) {
				return new JFreemarkerRender(view, true);
			}

			/*			if (renderType.equalsIgnoreCase("freemarker")) {
				return new JFreemarkerRender(view, true);
			}

			else if (renderType.equalsIgnoreCase("thymeleaf")) {
				return new ThymeleafRender(view);
			}*/

			return new JFreemarkerRender(view, true);

		}

		// admin url
		return new JFreemarkerRender(view, false);
	}

	@Override
	public Render getErrorRender(int errorCode, String view) {

		if (!CCloud.isInstalled()) {
			return new TextRender(errorCode + " error in jpress.");
		}

		Template template = TemplateManager.me().currentTemplate();
		if (null == template) {
			return new TextRender(String.format("%s error! you haven't configure your template yet.", errorCode));
		}

		String errorHtml = TemplateManager.me().currentTemplatePath() + "/" + errorCode + ".html";

		String renderType = TemplateManager.me().currentTemplate().getRenderType();

		// the default render type is freemarker
		if (StringUtils.isBlank(renderType)) {
			return new JFreemarkerRender(errorHtml, true);
		}

		if ("freemarker".equalsIgnoreCase(renderType)) {
			return new JFreemarkerRender(errorHtml, true);
		} /*else if ("thymeleaf".equalsIgnoreCase(renderType)) {
			return new ThymeleafRender(errorHtml);
		}*/

		return new TextRender(errorCode + " error in jpress.");
		
	}

}
