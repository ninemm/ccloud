package org.ccloud.model.compare;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.ccloud.model.vo.CustomerVO;

import com.alibaba.fastjson.annotation.JSONField;
import com.jfinal.kit.StrKit;

public class BeanCompareUtils {

	@SuppressWarnings("rawtypes")
	public static List<String> contrastObj(Object src, Object dest) {
		
		if (src instanceof CustomerVO && dest instanceof CustomerVO) {
			CustomerVO scustomer = (CustomerVO) src;
			CustomerVO dcustomer = (CustomerVO) dest;
			List<String> diffAttrList = new ArrayList<String>();
			
			try {
				Class clazz = scustomer.getClass();
				Field[] fields = scustomer.getClass().getDeclaredFields();
				
				for (Field field : fields) {
					
					if (StrKit.equals(field.getName(), "serialVersionUID")) {
						continue;
					}
					
					JSONField jsonField = field.getAnnotation(JSONField.class);
					if (jsonField == null)
						continue;
					
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
					Method getMethod = pd.getReadMethod();
					Object srcObj = getMethod.invoke(scustomer);
					Object destObj = getMethod.invoke(dcustomer);
					
					if (srcObj == null && destObj == null) {
						continue;
					} else {
						if (destObj != null) {
							if (srcObj != null) {
								if(!srcObj.toString().equals(destObj.toString())) {
									diffAttrList.add(jsonField.name() + ": " + destObj.toString());
								}
								
							} else {
								diffAttrList.add(jsonField.name() + ": " + destObj.toString());
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
			return diffAttrList;
		}
		return null;
	}
	
}
