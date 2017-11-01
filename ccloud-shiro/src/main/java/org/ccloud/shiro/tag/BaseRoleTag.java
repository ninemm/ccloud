package org.ccloud.shiro.tag;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

@SuppressWarnings("rawtypes")
public abstract class BaseRoleTag extends BaseShiroDirectiveTag {

	public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
		
		boolean isShow = showTagBody(getName(params));
		
		if (isShow)
			renderBody(env, body);
		
	}
	
	String getName(Map params) {
		return getParam(params, "name");
	}
	
	protected abstract boolean showTagBody(String roleNames);
}
