package org.ccloud.shiro.tag;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

@SuppressWarnings("rawtypes")
public class PrincipalTag extends BaseShiroDirectiveTag {

	@Override
	public void render(Environment env, Map params, TemplateDirectiveBody body) throws IOException, TemplateException {
		
		String result = null;
		
		if (getSubject() != null) {
			Object principal;
			
			if (getType(params) == null) {
				principal = getSubject().getPrincipal();
			} else {
				principal = getPrincipalFromClassName(params);
			}
			
			if (principal != null) {
				String property = getProperty(params);
				if (property == null) {
					result = principal.toString();
				} else {
					result = getPricipalProperty(principal, property);
				}
			}
		}
		
		if (result != null) {
			env.getOut().write(result);
		}
		
	}
	
	String getType(Map params) {
		return getParam(params, "type");
	}
	
	String getProperty(Map params) {
		return getParam(params, "property");
	}
	
	@SuppressWarnings("unchecked")
	Object getPrincipalFromClassName(Map params) {
		
		String type = getType(params);
		
		try {
			Class cls = Class.forName(type);
			return getSubject().getPrincipals().oneByType(cls);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	String getPricipalProperty(Object principal, String property) throws TemplateModelException {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(principal.getClass());
			
			// loop through the properties to get the string value of the specified property
			for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
				if (propertyDescriptor.getName().equals(property)) {
					Object value = propertyDescriptor.getReadMethod().invoke(principal, (Object[]) null);
					return String.valueOf(value);
				}
			}
			
			// property not found
			throw new TemplateModelException("Property ["+property+"] not found in principal of type ["+principal.getClass().getName()+"]");
		} catch (Exception e) {
			e.printStackTrace();
			throw new TemplateModelException("Property ["+property+"] not found in principal of type ["+principal.getClass().getName()+"]");
		}
	}
}
