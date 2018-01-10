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
import org.ccloud.message.MessageKit;
import org.ccloud.model.Dict;
import org.ccloud.model.Message;
import org.ccloud.model.User;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.MessageQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

@RouterMapping(url = "/message")
public class MessageController extends BaseFrontController {

	public void index() {
		
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		Dict dict = DictQuery.me().findByKey("message_type", "order");
		Page<Record> orderPage = MessageQuery.me().paginateObj(getPageNumber(), 15, sellerId, dict.getValue(), null, user.getId(), null);
		setAttr("orderPage", orderPage);
		
		Dict customer = DictQuery.me().findByKey("message_type", "customer");
		Page<Record> customerPage = MessageQuery.me().paginateObj(getPageNumber(), 15, sellerId, customer.getValue(), null, user.getId(), null);
		setAttr("customerPage", customerPage);
		
		Dict customerVisit = DictQuery.me().findByKey("message_type", "customer_visit");
		Page<Record> customerVisitPage = MessageQuery.me().paginateObj(getPageNumber(), 15, sellerId, customerVisit.getValue(), null, user.getId(), null);
		setAttr("customerVisitPage", customerVisitPage);
		
		render("message_list.html");
	}
	
	public void edit() {
		
		String id = getPara("id");
		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}
		
		Message message = MessageQuery.me().findById(id);
		message.setIsRead(Consts.IS_READ);
		message.update();
		setAttr("message", message);
		
		MessageKit.sendMessage(Message.ACTION_EDIT, id);
		
		render("message_edit.html");
	}
	
	public void more() {
		
		String type = getPara("type");
		Integer pageSize = getParaToInt("pageSize");
		Integer pageNumber = getParaToInt("pageNumber");
		
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		Page<Message> page = MessageQuery.me().paginate(pageNumber, pageSize, sellerId, type, null, user.getId(), null);
		List<Message> list = page.getList();
		Ret ret = Ret.create();
		StringBuilder strBuilder = new StringBuilder();
		for (Message message : list) {
			StringBuilder str = new StringBuilder();
			str.append("<div class=\"weui-cell\">");
			str.append("	<div class=\"weui-cell__bd\">");
			str.append("  		<p>" + message.getTitle() + "</p>");
			str.append("	</div>");
			str.append("	<div class=\"check-pass\">");
			str.append(			DictQuery.me().findByKey("message_type", type));
			str.append("	</div>");
			str.append("	<div class=\"weui-cell__ft\">" + message.getCreateDate() + "</div>");
			str.append("</div>");
		}
		
		ret.set("isEnd", list.size() >= pageSize ? false : true);
		ret.set("messageData", strBuilder.toString());
		
		renderAjaxResultForSuccess("success", ret);
	}
	
}
