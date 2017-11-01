package org.ccloud.shiro.tag;

import java.io.IOException;
import java.util.Map;

import com.jfinal.kit.LogKit;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

@SuppressWarnings("rawtypes")
public class UserTag extends BaseShiroDirectiveTag {

	@Override
	public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
		// TODO Auto-generated method stub
		if (getSubject() != null && getSubject().getPrincipal() != null) {
			if (LogKit.isDebugEnabled()) {
				LogKit.debug("Subject has known identity (aka 'principal'). Tag body will be evaluated.");
			}
			
			renderBody(env, body);
		} else {
			if (LogKit.isDebugEnabled()) {
				LogKit.debug("Subject does not exist or have a known identity (aka 'principal'). Tag body will not be evaluated.");
			}
		}
	}

}
