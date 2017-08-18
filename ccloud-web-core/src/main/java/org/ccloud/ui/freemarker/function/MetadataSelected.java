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
import org.ccloud.model.core.JModel;
import org.ccloud.utils.StringUtils;

public class MetadataSelected extends JFunction {

	@Override
	public Object onExec() {
		Object obj = get(0);
		if (obj == null) {
			return "";
		}

		String key = getToString(1);
		if (StringUtils.isBlank(key)) {
			return "";
		}

		String value = getToString(2);
		if (value == null)
			value = "true";

		if (obj instanceof JModel<?>) {
			JModel<?> model = (JModel<?>) obj;
			String data = model.metadata(key);
			if (data != null && value.equals(data.toLowerCase())) {
				return "selected=\"selected\"";
			}
		}

		return "";
	}

}
