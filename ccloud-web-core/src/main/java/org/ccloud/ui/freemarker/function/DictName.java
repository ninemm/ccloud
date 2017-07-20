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
import org.ccloud.model.query.DictQuery;
import org.ccloud.utils.StringUtils;

public class DictName extends JFunction {

	@Override
	public Object onExec() {
		String key = getToString(0);
		StringBuilder sb = new StringBuilder();
		
		if (StringUtils.isNotBlank(key)) {
			String[] keys = key.split(",");
			for (String k : keys) {
				sb.append(DictQuery.me().findName(k)).append(" / ");
			}
		}
		
		if (sb.length() > 3)
			return sb.toString().substring(0, sb.length() - 3);
		else
			return sb.toString();
	}

}
