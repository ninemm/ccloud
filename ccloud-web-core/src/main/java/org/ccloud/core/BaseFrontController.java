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
package org.ccloud.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.ccloud.Consts;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.template.TemplateManager;
import org.ccloud.utils.FileUtils;
import org.ccloud.utils.StringUtils;
import org.joda.time.DateTime;

import com.jfinal.core.Controller;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;

public class BaseFrontController extends JBaseController {

	private static final String FILE_SEPARATOR = "_";
	private String renderFile = null;

	public void render(String name) {

		renderFile = null;

		initRenderFile(name);

		if (renderFile != null) {
			super.render(renderFile);
		} else {
			renderError(404);
		}

	}

	private void initRenderFile(String name) {
		if (isWechatBrowser()) {
			initWechatFile(name);

			if (renderFile == null) {
				initMobileFile(name);
			}
		}

		if (isMoblieBrowser()) {
			initMobileFile(name);
		}

		if (renderFile == null) {
			initNormalFile(name);
		}
	}

	public boolean templateExists(String file) {
		boolean isExists = false;
		if (isWechatBrowser()) {
			isExists = TemplateManager.me().existsFileInWechat(file);

			if (!isExists) {
				isExists = TemplateManager.me().existsFileInMobile(file);
			}
		}

		if (isMoblieBrowser()) {
			isExists = TemplateManager.me().existsFileInMobile(file);
		}

		if (!isExists) {
			isExists = TemplateManager.me().existsFile(file);
		}

		return isExists;
	}

	private void initWechatFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFileInWechat(name)) {
					renderFile = buildWechatPath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFileInWechat(name)) {
			renderFile = buildWechatPath(name);
		}
	}

	private void initMobileFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFileInMobile(name)) {
					renderFile = buildMobilePath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFileInMobile(name)) {
			renderFile = buildMobilePath(name);
		}
	}

	private void initNormalFile(String name) {
		if (name.contains(FILE_SEPARATOR)) {
			do {
				if (TemplateManager.me().existsFile(name)) {
					renderFile = buildPath(name);
					return;
				}
				name = clearProp(name);
			} while (name.contains(FILE_SEPARATOR));
		}

		if (TemplateManager.me().existsFile(name)) {
			renderFile = buildPath(name);
			return;
		}

	}

	private String buildPath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/" + name;
	}

	private String buildWechatPath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/tpl_wechat/" + name;
	}

	private String buildMobilePath(String name) {
		return TemplateManager.me().currentTemplate().getPath() + "/tpl_mobile/" + name;
	}

	public String clearProp(String fname) {
		return fname.substring(0, fname.lastIndexOf(FILE_SEPARATOR)) + ".html";
	}

	@Override
	public Controller keepPara() {
		super.keepPara();
		String gotoUrl = getPara("goto");
		if (StringUtils.isNotBlank(gotoUrl)) {
			setAttr("goto", StringUtils.urlEncode(gotoUrl));
		}
		return this;
	}
	
	public String upload(String blobImage) {
		if (StrKit.notBlank(blobImage)) {
			
			String webRoot = PathKit.getWebRootPath();
			
			String rootFilePath = OptionQuery.me().findValue(Consts.OPTION_FILE_ROOT_PATH);
			if (StrKit.notBlank(rootFilePath))
				webRoot = rootFilePath;
			
			String uuid = StrKit.getRandomUUID();
			StringBuilder newFileName = new StringBuilder(webRoot).append(File.separator).append("attachment")
					.append(File.separator).append(DateTime.now().toString("yyyyMMdd")).append(File.separator).append(uuid)
					.append(".jpeg");

			File newfile = new File(newFileName.toString());

			if (!newfile.getParentFile().exists()) {
				newfile.getParentFile().mkdirs();
			}
			
			try (OutputStream os = new FileOutputStream(newfile)) {
				blobImage = blobImage.substring(blobImage.indexOf(",") + 1);
				byte[] b = Base64Kit.decode(blobImage);
				for (int i = 0; i < b.length; ++i) {
		            if (b[i] < 0) {//调整异常数据
		                b[i] += 256;
		            }
		        }
				
				os.write(b);
				os.close();
			} catch (Exception e) {
				LogKit.error("上传反馈图片失败!");
				return null;
			}
			return FileUtils.removePrefix(newfile.getAbsolutePath(), webRoot);
		}
		return null;
	}

}
