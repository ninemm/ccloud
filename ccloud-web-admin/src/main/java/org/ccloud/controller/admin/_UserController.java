/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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
package org.ccloud.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.menu.MenuManager;
import org.ccloud.model.Department;
import org.ccloud.model.Group;
import org.ccloud.model.Station;
import org.ccloud.model.User;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.StationQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/user", viewPath = "/WEB-INF/admin/user")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/user","/admin/all"},logical=Logical.OR)
public class _UserController extends JBaseCRUDController<User> {

	@Override
	public void index() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword))
			setAttr("k", keyword);
		
		String dataArea = getSessionAttr("DeptDataAreaLike");
		
		Page<User> page = UserQuery.me().paginate(getPageNumber(), getPageSize(), keyword, dataArea, "create_date");
		if (page != null) {
			setAttr("page", page);
		}

	}

	@Override
	public void save() {

		final User user = getModel(User.class);
		String stationList = getPara("stationList");
		String stationName = getPara("stationName");
		String deptName = getPara("parent_name");
		user.setDepartmentName(deptName);
		user.setStationId(stationList);
		user.setStationName(stationName);
		Department dept = DepartmentQuery.me().findById(user.getDepartmentId());
		String dataArea = DataAreaUtil.dataAreaSetByUser(dept.getDataArea());
		user.setDataArea(dataArea);

		if (user.getId() == null) {
			user.setSalt(EncryptUtils.salt());
			user.setPassword(EncryptUtils.encryptPassword(user.getPassword(), user.getSalt()));
		}

		if (user.saveOrUpdate()) {
			MenuManager.clearListByKey(user.getId());
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("false");
		}
	}

	@Override
	@RequiresPermissions(value={"/admin/user/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			User user = UserQuery.me().findById(id);
			setAttr("user", user);
		}
		String dataArea = getSessionAttr("DeptDataAreaLike");
		List<Group> list = GroupQuery.me().findByDept(dataArea);
		setAttr("list", list);
	}

	@Override
	@RequiresPermissions(value={"/admin/user/edit","/admin/all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		final User r = UserQuery.me().findById(id);
		if (r != null) {
			if (r.getUsername().equals("admin")) {
				renderAjaxResultForError("无法删除管理员!");
			} else {
				boolean success = r.delete();
				if (success) {
					MenuManager.clearListByKey(id);
					renderAjaxResultForSuccess("删除成功");
				} else {
					renderAjaxResultForError("删除失败");
				}
			}
		}
	}

	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"/admin/user/edit","/admin/all"},logical=Logical.OR)
	public void batchDelete() {
		String[] ids = getParaValues("dataItem");
		int count = UserQuery.me().batchDelete(ids);
		if (count > 0) {
			MenuManager.clearListByKeys(ids);				
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}

	}

	public void getStation() {
		String id = getPara("userid");
		String dataArea = getSessionAttr("DeptDataAreaLike");
		List<Station> stations = StationQuery.me().findByDept(dataArea);
		List<Map<String, Object>> list = new ArrayList<>();
		for (Station station : stations) {
			if (station.getId().equals("0")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("id", station.getId());
			map.put("name", station.getStationName());
			if (!StringUtils.isBlank(id)) {
				User user = UserQuery.me().findById(id);
				String stationIds = user.getStationId();
				String[] stationId = stationIds.split(",");
				for (int i = 0, len = stationId.length; i < len; i++) {
					if (stationId[i].toString().equals(station.getId())) {
						map.put("isvalid", 1);
						break;
					} else {
						map.put("isvalid", 0);
					}
				}
			} else {
				map.put("isvalid", 0);
			}
			list.add(map);
		}
		renderJson(list);
	}

	@RequiresPermissions(value={"/admin/user/edit","/admin/all"},logical=Logical.OR)
	public void enable() {
		String id = getPara("id");
		User user = UserQuery.me().findById(id);
		if (user.getStatus() == 0) {
			user.setStatus(1);
		} else {
			user.setStatus(0);
		}
		user.update();
		renderAjaxResultForSuccess("更新成功");
	}
	
	public void synchroWechat() {
		
	}

}
