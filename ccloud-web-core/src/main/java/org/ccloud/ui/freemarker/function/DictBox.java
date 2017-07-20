/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.ui.freemarker.function;

import java.util.List;

import org.ccloud.core.render.freemarker.JFunction;
import org.ccloud.model.Dict;
import org.ccloud.model.query.DictQuery;
import org.ccloud.utils.StringUtils;

public class DictBox extends JFunction{

	@Override
	public Object onExec() {
		
		String dictType = getToString(0);	// 字典类型
		String boxType = getToString(1);	// HTML类型
		String dictValue = getToString(2);	// 字典值
		String propName = getToString(3);	// 属性名称 
		String clickEvent = getToString(4);	// 事件名称
		if(StringUtils.isBlank(dictType))
			return "";
		List<Dict> list = DictQuery.me().findDictByType(dictType);
		StringBuilder htmlBuilder = new StringBuilder();
		for(Dict dict : list) {
			if ("select".equals(boxType)) {
				
				if(StringUtils.isNotEmpty(dictValue) && dictValue.equals(dict.getValue()))
					htmlBuilder.append("<option value = \"" + dict.getValue() + "\" selected = \"selected\" >");
				else
					htmlBuilder.append("<option value = \"" + dict.getValue() + "\" >");
				htmlBuilder.append(dict.getName());
				htmlBuilder.append("</option>");
				
			} else if ("checkbox".equals(boxType)) {
				
				boolean isChecked = false;
				String[] dv = dictValue.split(",");
				int len = dv.length;
				for (int i = 0; i < len; i++) {
					if (StringUtils.isNotBlank(dv[i]) && dv[i].equals(dict.getValue()))
						isChecked = true;
				}
				
				htmlBuilder.append("<label class=\"checkbox-inline\">");
//				if (isChecked) 
//					htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" checked >");
//				else
//					htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" >");
				
				if (StringUtils.isNotBlank(clickEvent)) {
					if (isChecked) 
						htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" checked onclick=\"" + clickEvent + "('" + propName +  "')\" >");
					else
						htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" onclick=\"" + clickEvent + "('" + propName +  "')\" >");
				} else {
					if (isChecked) 
						htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" checked >");
					else
						htmlBuilder.append("<input type=\"checkbox\" name=\"" + propName + "\" value = \"" + dict.getValue() + "\" >");
				}
				
				htmlBuilder.append(dict.getName());
				htmlBuilder.append("</label>");
				
			}
		}
		
		return htmlBuilder.toString();
	}

}
