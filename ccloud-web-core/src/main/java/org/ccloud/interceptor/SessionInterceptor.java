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
package org.ccloud.interceptor;

import org.ccloud.Consts;
import org.ccloud.model.User;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class SessionInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		//判断session里面有没有user
		User user =  inv.getController().getSessionAttr(Consts.SESSION_LOGINED_USER);
		//获取访问地址
		String controllerKey = inv.getControllerKey();
		//筛选掉后台
		if (user != null || controllerKey.startsWith(Consts.FIRST_URL)) {
			inv.invoke();
		} else {
			inv.getController().redirect("/user/timeout");
		}
	}

}