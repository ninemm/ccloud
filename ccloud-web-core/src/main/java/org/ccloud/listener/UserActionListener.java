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

import java.util.Date;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;
import org.ccloud.model.User;

@Listener(action = { Actions.USER_LOGINED, User.ACTION_ADD })
public class UserActionListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		if (message.getAction().equals(Actions.USER_LOGINED)) {
			User user = message.getData();
			user.setLogged(new Date());
			user.update();
		}

		else if (message.getAction().equals(User.ACTION_ADD)) {
			User user = message.getData();
			user.setLogged(new Date());
			user.update();
		}
	}

}
