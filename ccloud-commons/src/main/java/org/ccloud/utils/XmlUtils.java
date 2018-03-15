package org.ccloud.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;

public class XmlUtils {

	 public static Map<String, Object> Dom2Map(Document doc){  
         Map<String, Object> map = new HashMap<String, Object>();  
         if(doc == null)  
             return map;  
         Element root = doc.getRootElement();  
         for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {  
             Element e = (Element) iterator.next();  
             //System.out.println(e.getName());  
             List list = e.elements();  
             if(list.size() > 0){  
                 map.put(e.getName(), Dom2Map(e));  
             }else  
                 map.put(e.getName(), e.getText());  
         }  
         return map;  
     }  
       
   
     public static Map Dom2Map(Element e){  
         Map map = new HashMap();  
         List list = e.elements();  
         if(list.size() > 0){  
             for (int i = 0;i < list.size(); i++) {  
                 Element iter = (Element) list.get(i);  
                 List mapList = new ArrayList();  
                   
                 if(iter.elements().size() > 0){  
                     Map m = Dom2Map(iter);  
                     if(map.get(iter.getName()) != null){  
                         Object obj = map.get(iter.getName());  
                         if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                             mapList = new ArrayList();  
                             mapList.add(obj);  
                             mapList.add(m);  
                         }  
                         if(obj.getClass().getName().equals("java.util.ArrayList")){  
                             mapList = (List) obj;  
                             mapList.add(m);  
                         }  
                         map.put(iter.getName(), mapList);  
                     }else  
                         map.put(iter.getName(), m);  
                 }  
                 else{  
                     if(map.get(iter.getName()) != null){  
                         Object obj = map.get(iter.getName());  
                         if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                             mapList = new ArrayList();  
                             mapList.add(obj);  
                             mapList.add(iter.getText());  
                         }  
                         if(obj.getClass().getName().equals("java.util.ArrayList")){  
                             mapList = (List) obj;  
                             mapList.add(iter.getText());  
                         }  
                         map.put(iter.getName(), mapList);  
                     }else  
                         map.put(iter.getName(), iter.getText());  
                 }  
             }  
         }else  
             map.put(e.getName(), e.getText());  
         return map;  
     }  
   
     public static String map2Xmlstring(Map<String,Object> map){  
         StringBuffer sb = new StringBuffer("");  
         sb.append("<xml>");  
           
         Set<String> set = map.keySet();  
         for(Iterator<String> it=set.iterator(); it.hasNext();){  
             String key = it.next();  
             Object value = map.get(key);  
             sb.append("<").append(key).append(">");  
             sb.append(value);  
             sb.append("</").append(key).append(">");  
         }  
         sb.append("</xml>");  
         return sb.toString();  
     }  
     
 }  