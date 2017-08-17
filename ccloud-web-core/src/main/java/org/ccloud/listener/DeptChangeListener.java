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
package org.ccloud.listener;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;
import org.ccloud.model.Department;
import org.ccloud.model.core.JModel;
import org.ccloud.model.query.DepartmentQuery;

@Listener(action = {Actions.DEPT_ADD, Actions.DEPT_UPDATE, Actions.DEPT_DELETE})
public class DeptChangeListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		Object temp = message.getData();
		
		if (temp != null && temp instanceof JModel) {
			Department dept = (Department) temp;
			Department parentDept = DepartmentQuery.me().findById(dept.getParentId());
			Integer childNum = DepartmentQuery.me().childNumById(dept.getParentId());
			if (childNum > 0) {
				if (parentDept.getIsParent() == 0) {
					parentDept.setIsParent(1);
					parentDept.update();
				}
			} else {
				if (parentDept.getIsParent() > 0) {
					parentDept.setIsParent(0);
					parentDept.update();
				}
			}
		}
	}

}