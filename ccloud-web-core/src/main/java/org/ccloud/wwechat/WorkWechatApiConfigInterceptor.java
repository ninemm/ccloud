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
package org.ccloud.wwechat;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.qyweixin.sdk.api.ApiConfig;
import com.jfinal.qyweixin.sdk.api.ApiConfigKit;

public class WorkWechatApiConfigInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		try {
			ApiConfig ac = WorkWechatApi.getApiConfig();
			ApiConfigKit.setThreadLocalCorpId(ac.getCorpId());
			inv.invoke();
		} finally {
			ApiConfigKit.removeThreadLocalCorpId();
		}
	}

}
