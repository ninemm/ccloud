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

import org.ccloud.core.render.freemarker.JFunction;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.utils.StringUtils;

public class OptionSelected extends JFunction {

	@Override
	public Object onExec() {
		String key = getToString(0);
		if (key == null)
			return "";
		
		String value = getToString(1);
		if (StringUtils.isNotBlank(value)) {
			String setting = OptionQuery.me().findValue(key);
			if (value.equals(setting)) {
				return "selected=\"selected\"";
			} else {
				return "";
			}
		}

		if (key.startsWith("!")) {
			Boolean bool = OptionQuery.me().findValueAsBool(key.substring(1));
			if (bool != null && !bool) {
				return "selected=\"selected\"";
			}
		} else {
			Boolean bool = OptionQuery.me().findValueAsBool(key);
			if (bool != null && bool) {
				return "selected=\"selected\"";
			}
		}

		return "";
	}


}
