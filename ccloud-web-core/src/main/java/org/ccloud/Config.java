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
package org.ccloud;

import org.ccloud.core.CCloud;
import org.ccloud.core.CCloudConfig;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.shiro.tag.ShiroTags;
import org.ccloud.ui.freemarker.function.DictBox;
import org.ccloud.ui.freemarker.function.DictName;
import org.ccloud.ui.freemarker.function.MetadataChecked;
import org.ccloud.ui.freemarker.function.MetadataSelected;
import org.ccloud.ui.freemarker.function.OptionChecked;
import org.ccloud.ui.freemarker.function.OptionSelected;
import org.ccloud.ui.freemarker.function.OptionValue;
import org.ccloud.ui.freemarker.function.TaxonomyBox;
import org.ccloud.ui.freemarker.tag.ContentTag;
import org.ccloud.ui.freemarker.tag.ContentsTag;
import org.ccloud.ui.freemarker.tag.DictTag;
import org.ccloud.wwechat.WorkWechatApi;

import com.jfinal.qyweixin.sdk.api.ApiConfigKit;
import com.jfinal.render.FreeMarkerRender;
import com.jfinal.template.Engine;

public class Config extends CCloudConfig {

	@Override
	public void onCCloudStarted() {

		CCloud.addTag(ContentsTag.TAG_NAME, new ContentsTag());
		CCloud.addTag(ContentTag.TAG_NAME, new ContentTag());
		CCloud.addTag(DictTag.TAG_NAME, new DictTag());
//		CCloud.addTag(PermissionTag.TAG_NAME, new PermissionTag());
		
		CCloud.addFunction("TAXONOMY_BOX", new TaxonomyBox());
		CCloud.addFunction("DICT_BOX", new DictBox());
		CCloud.addFunction("DICT_NAME", new DictName());
		CCloud.addFunction("OPTION", new OptionValue());
		CCloud.addFunction("OPTION_CHECKED", new OptionChecked());
		CCloud.addFunction("OPTION_SELECTED", new OptionSelected());
		CCloud.addFunction("METADATA_CHECKED", new MetadataChecked());
		CCloud.addFunction("METADATA_SELECTED", new MetadataSelected());

		FreeMarkerRender.getConfiguration().setSharedVariable(ShiroTags.TAG_NAME, new ShiroTags());
		
		MessageKit.sendMessage(Actions.CCLOUD_STARTED);
		ApiConfigKit.putApiConfig(WorkWechatApi.getApiConfig());
	}

	@Override
	public void configEngine(Engine me) {
		
	}
	
}
