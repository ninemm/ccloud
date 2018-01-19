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
package org.ccloud.task;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.cron4j.ITask;

public class ActivityTask implements ITask {

	@Override
	public void run() {
		System.out.println("------------1-----------");
	}

	@Override
	public void stop() {
		LogKit.info("stop activity task " + ActivityTask.class.getName());
	}

}