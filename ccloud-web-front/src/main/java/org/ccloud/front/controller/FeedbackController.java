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
import org.ccloud.model.Feedback;
import org.ccloud.model.User;
import org.ccloud.model.query.FeedbackQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

@RouterMapping(url = "/feedback")
public class FeedbackController extends BaseFrontController {
	
	public void index() {
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		Page<Feedback> page = FeedbackQuery.me().paginate(getPageNumber(), getPageSize(), userId, null);
		setAttr("page", page);
		
		render("feedback_list.html");
	}
	
	public void edit() {
		
		String id = getPara("id");
		
		if (StrKit.notBlank(id)) {
			setAttr("feedback", FeedbackQuery.me().findById(id));
		}
		
		render("feedback_edit.html");
	}
	
	public void save() {
		
		String content = getPara("content");
		String picJson = getPara("pic");
		List<ImageJson> list = Lists.newArrayList();
		
		if (StrKit.notBlank(picJson)) {
			
			JSONArray array = JSON.parseArray(picJson);
			for (int i = 0; i < array.size(); i++) {
				JSONObject obj = array.getJSONObject(i);
				String pic = obj.getString("pic");
				String picname = obj.getString("picname");
				
				ImageJson image = new ImageJson();
				image.setImgName(picname);
				String newPath = upload(pic);
				image.setSavePath(newPath.replace("\\", "/"));
				list.add(image);
			}
		}
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		
		String imageListStore = JSON.toJSONString(list);
		
		Feedback feedback = new Feedback();
		feedback.setContent(content);
		feedback.setImageListStore(imageListStore);
		feedback.setUserId(userId);
		
		if (feedback.saveOrUpdate())
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}
	
	public void more() {
		
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 10);
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		
		Page<Feedback> page = FeedbackQuery.me().paginate(pageNumber, pageSize, userId, null);
		List<Feedback> list = page.getList();
		Ret ret = Ret.create();
		StringBuilder strBuilder = new StringBuilder();
		
		for (Feedback feedback : list) {
			strBuilder.append("<a href=\"" + getRequest().getContextPath() + "/feedback/edit?id=" + feedback.getId() + "\" class=\"weui-cell weui-cell_access\">");
			strBuilder.append("		<div class=\"weui-cell__bd\">");
			strBuilder.append("			<p>" + feedback.getContent() + "</p>");
			strBuilder.append("		</div>");
			strBuilder.append("		<div class=\"weui-cell__ft\">");
			strBuilder.append(			DateUtils.dateToStr(feedback.getCreateDate(), DateUtils.DEFAULT_UNSECOND_FORMATTER));
			strBuilder.append("		</div>");
		}
		ret.set("isEnd", list.size() >= pageSize ? false : true);
		ret.set("feedbackData", strBuilder.toString());
		
		renderAjaxResultForSuccess("success", ret);
	}
	
}
