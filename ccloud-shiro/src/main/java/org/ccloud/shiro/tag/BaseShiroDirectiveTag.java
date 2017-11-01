package org.ccloud.shiro.tag;

import java.io.IOException;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@SuppressWarnings("rawtypes")
public abstract class BaseShiroDirectiveTag implements TemplateDirectiveModel {

	public void execute(Environment arg0, Map arg1, TemplateModel[] arg2, TemplateDirectiveBody arg3)
			throws TemplateException, IOException {

	}

	public abstract void render(Environment env, Map params, TemplateDirectiveBody body)
			throws IOException, TemplateException;

	protected String getParam(Map params, String name) {
		Object value = params.get(name);

		if (value instanceof SimpleScalar) {
			return ((SimpleScalar) value).getAsString();
		}

		return null;
	}

	protected Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	protected void verifyParameters(Map params) throws TemplateModelException {

	}

	protected void renderBody(Environment env, TemplateDirectiveBody body) throws IOException, TemplateException {
		if (body != null) {
			body.render(env.getOut());
		}
	}

}
