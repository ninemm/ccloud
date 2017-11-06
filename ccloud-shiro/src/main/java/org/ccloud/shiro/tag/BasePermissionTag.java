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
		String [] permissions = permission.split(",");
		for (int i = 0; i < permissions.length; i++) {
			boolean status = getSubject().isPermitted(permissions[i]);
			if (status) {
				return getSubject() != null && status;
			}
		}
		return false;
	}

	protected abstract boolean showTagBody(String permission);

}
