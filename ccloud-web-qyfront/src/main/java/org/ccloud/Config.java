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
	public void configEngine(Engine me) {

	}
	
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

}
