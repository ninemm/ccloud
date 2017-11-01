package org.ccloud.shiro.tag;

import java.io.IOException;
import java.util.Map;

import com.jfinal.kit.LogKit;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

@SuppressWarnings("rawtypes")
public class AuthenticatedTag extends BaseShiroDirectiveTag {

	public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
		
		if (getSubject() != null && getSubject().isAuthenticated()) {
			if (LogKit.isDebugEnabled()) {
				LogKit.debug("Subject exists and is authenticated. Tag body will be evaluated.");
			}
			
			renderBody(env, body);
		} else {
			if (LogKit.isDebugEnabled()) {
				LogKit.debug("Subject does not exist or is not authenticated. Tag body will not be evaluated.");
			}
		}
		
	}

}
