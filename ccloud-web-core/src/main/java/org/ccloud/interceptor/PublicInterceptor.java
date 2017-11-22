package org.ccloud.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.render.FreeMarkerRender;

import freemarker.ext.servlet.HttpSessionHashModel;

public class PublicInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation inv) {
        //向freemarker中传jsp的内置对象
        Controller c = inv.getController();
        c.setAttr("request", c.getRequest());
        c.setAttr("response", c.getResponse());
        c.setAttr("session", new HttpSessionHashModel(c.getSession(), FreeMarkerRender.getConfiguration().getObjectWrapper()));
        inv.invoke();
	}

}
