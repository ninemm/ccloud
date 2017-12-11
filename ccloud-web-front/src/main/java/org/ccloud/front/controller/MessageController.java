/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@126.com).
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

package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.Message;
import org.ccloud.model.User;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/message")
public class MessageController extends BaseFrontController {

	public void index() {
		Page<Message> page = MessageQuery.me().paginate(getPageNumber(), getPageSize(), null);
		setAttr("page", page);
		render("message.html");
	}
	
	public void edit() {
		
		String id = getPara("id");
		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}
		
		Message message = MessageQuery.me().findById(id);
		setAttr("message", message);
		render("message_edit.html");
	}
	
	public void more() {
		
		String type = getPara("type");
		Integer pageSize = getParaToInt("pageSize");
		Integer pageNumber = getParaToInt("pageNumber");
		
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		Page<Message> page = MessageQuery.me().paginate(pageNumber, pageSize, sellerId, type, user.getId(), null);
		List<Message> list = page.getList();
		Ret ret = Ret.create();
		StringBuilder strBuilder = new StringBuilder();
		for (Message message : list) {
			
		}
		
		ret.set("isEnd", list.size() >= pageSize ? false : true);
		ret.set("scoreData", strBuilder.toString());
		
		renderAjaxResultForSuccess("success", ret);
	}
	
}
