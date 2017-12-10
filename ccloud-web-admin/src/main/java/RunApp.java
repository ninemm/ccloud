
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ccloud.model.vo.CustomerVO;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.beust.jcommander.internal.Maps;
import com.jfinal.kit.StrKit;

public class RunApp {

	public static void main(String[] args) {
		//JFinal.start("src/main/webapp", 8899, "/", 5);
		
		CustomerVO src = new CustomerVO();
		src.setNickname("11111");
		
		CustomerVO dest = new CustomerVO();
		dest.setNickname("2222");
		dest.setCustomerName("aaaaa");
		dest.setAddress("bbbb");
		
		contrastObj(src, dest);
	}

	@SuppressWarnings("rawtypes")
	public static void contrastObj(Object src, Object dest) {
		
		if (src instanceof CustomerVO && dest instanceof CustomerVO) {
			CustomerVO scustomer = (CustomerVO) src;
			CustomerVO dcustomer = (CustomerVO) dest;
			List<Map<String, Object>> textList = new ArrayList<Map<String, Object>>();
			
			try {
				Class clazz = scustomer.getClass();
				Field[] fields = scustomer.getClass().getDeclaredFields();
				
				for (Field field : fields) {
					
					if (StrKit.equals(field.getName(), "serialVersionUID")) {
						continue;
					}
					
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
									JSONField jsonField = field.getAnnotation(JSONField.class);
									if (jsonField != null)
									System.out.println(jsonField.name());
									Map<String, Object> temp = Maps.newHashMap();
									temp.put(field.getName(), destObj.toString());
									textList.add(temp);
								}
								
							} else {
								JSONField jsonField = field.getAnnotation(JSONField.class);
								if (jsonField != null)
								System.out.println(jsonField.name());
								Map<String, Object> temp = Maps.newHashMap();
								temp.put(field.getName(), destObj.toString());
								textList.add(temp);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			for (Object object : textList) {
				System.out.println(JSON.toJSONString(object));
			}
		}
	}

}
