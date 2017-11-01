package org.ccloud.shiro.tag;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

@SuppressWarnings("rawtypes")
public abstract class BasePermissionTag extends BaseShiroDirectiveTag {

	@Override
	public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
		
		String permission = getName(params);
		boolean isShow = showTagBody(permission);
		if (isShow)
			renderBody(env, body);
		
	}
	
	String getName(Map params) {
		return getParam(params, "name");
	}
	
	@Override
	protected void verifyParameters(Map params) throws TemplateModelException {

		String permission = getName(params);
		
		if (permission == null || permission.length() == 0) {
			throw new TemplateModelException("The 'name' tag attribute must be set.");
		}
		
	}
	
	protected boolean isPermitted(String permission) {
		return getSubject() != null && getSubject().isPermitted(permission);
	}

	protected abstract boolean showTagBody(String permission);

}
