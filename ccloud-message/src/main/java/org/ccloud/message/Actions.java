/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
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
package org.ccloud.message;

public class Actions {

	public static final String USER_LOGINED = "user:logined";
//	public static final String USER_CREATED = "user:created";

	public static final String SETTING_CHANGED = "system:setting_changed";

	public static final String CONTENT_COUNT_UPDATE = "content:count_update";
	
	public static final String MENU_ADD = "menu:add";

	public static final String STATION_ADD = "station:add";

	public static final String STATION_DELETE = "station:delete";

	public static final String CCLOUD_STARTED = "ccloud:started";
	
	public static final String DEPT_ADD = "department:add";
	
	public static final String DEPT_UPDATE = "department:update";
	
	public static final String DEPT_DELETE = "department:delete";
	
	public static final String CATEGORY_ADD = "cc_goods_category:add";
	
	public static final String CATEGORY_UPDATE = "cc_goods_category:update";
	
	public static final String CATEGORY_DELETE = "cc_goods_category:delete";
	
	public class System {
		public static final String ACTION_LOG = "action:log";
	}
	public class NotifyWechatMessage {
		public static final String CUSTOMER_AUDIT_MESSAGE = "customer:audit";
		public static final String CUSTOMER_VISIT_AUDIT_MESSAGE = "visit:audit";
	}
	
	public class ProcessMessage {
		public static final String PROCESS_MESSAGE_SAVE = "message:save";
	}
	

}
