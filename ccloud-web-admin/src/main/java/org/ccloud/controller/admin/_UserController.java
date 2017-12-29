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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.menu.MenuManager;
import org.ccloud.model.Department;
import org.ccloud.model.Group;
import org.ccloud.model.Station;
import org.ccloud.model.User;
import org.ccloud.model.UserGroupRel;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.StationQuery;
import org.ccloud.model.query.UserGroupRelQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.UserExecel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.EncryptUtils;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/user", viewPath = "/WEB-INF/admin/user")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _UserController extends JBaseCRUDController<User> {

	@Override
	@RequiresPermissions(value = { "/admin/user", "/admin/all" }, logical = Logical.OR)
	public void index() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword))
			setAttr("k", keyword);

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		String userId = null;
		if (!isSuperAdmin) {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			userId = user.getId();
		}

		Page<User> page = UserQuery.me().paginateUser(getPageNumber(), getPageSize(), keyword, dataArea, "u.create_date",
				userId);
		if (page != null) {
			setAttr("page", page);
		}

	}

	@Override
	@RequiresPermissions(value = { "/admin/user", "/admin/all" }, logical = Logical.OR)
	public void save() {

		final User user = getModel(User.class);
		String stationList = getPara("stationList");
		String stationName = getPara("stationName");
		String groupList = getPara("groupList");
		String groupName = getPara("groupName");
		String NewGroupName = groupName.substring(0, groupName.length() -1);
		String deptName = getPara("parent_name");
		user.setDepartmentName(deptName);
		user.setStationId(stationList);
		user.setStationName(stationName);
		// user.setGroupId(groupList);
		 user.setGroupName(NewGroupName);
		if (StringUtils.isBlank(user.getId())) {
			Department dept = DepartmentQuery.me().findById(user.getDepartmentId());
			String dataArea = DataAreaUtil.dataAreaSetByUser(dept.getDataArea());
			user.setDataArea(dataArea);
		} else {
			User userOld = UserQuery.me().findById(user.getId());
			if (!userOld.getDepartmentId().equals(user.getDepartmentId())) {
				Department dept = DepartmentQuery.me().findById(user.getDepartmentId());
				String dataArea = DataAreaUtil.dataAreaSetByUser(dept.getDataArea());
				user.setDataArea(dataArea);
			}
		}
		String [] groupLists = groupList.split(",");
		List<UserGroupRel> userGroupRelList = new ArrayList<>();
		List<String> gList = new ArrayList<>();


		if (user.getId() == null) {
			user.setSalt(EncryptUtils.salt());
			user.setPassword(EncryptUtils.encryptPassword(user.getPassword(), user.getSalt()));
		}else {
			//更新用户组信息
			List<UserGroupRel> userGroupRels = UserGroupRelQuery.me().findByUserId(user.getId());
			  for (UserGroupRel userGroupRel : userGroupRels) {
				  gList.add(userGroupRel.getGroupId());
			}
			  /*for (String groupId : groupLists) {
				if (gList.contains(groupId)) {
					gList.remove(groupId);
				}
			}*/
			  for (String deleteGroupId : gList) {
					Record record = UserGroupRelQuery.me().findByUserIdAndGroupId(user.getId(), deleteGroupId);
					UserGroupRelQuery.me().batchDelete(record.get("id").toString());
			}
		}		
		if (user.saveOrUpdate()) {
			for (String groupId : groupLists) {
	    		UserGroupRel userGroupRel = new UserGroupRel();
	    		userGroupRel.setId(StrKit.getRandomUUID());
				userGroupRel.setUserId(user.getId());
				userGroupRel.setGroupId(groupId);
				userGroupRelList.add(userGroupRel);
			}
	    	Db.batchSave(userGroupRelList, userGroupRelList.size());
			MenuManager.clearListByKey(user.getId());
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("false");
		}
	}

	@Override
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			User user = UserQuery.me().findById(id);
			setAttr("user", user);
		}
	}

	@Override
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
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
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
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
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
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

	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
	public void enable() {
		String id = getPara("id");
		User user = UserQuery.me().findById(id);
		if (user.getStatus() == 0) {
			user.setStatus(1);
		} else {
			user.setStatus(0);
			user.setUnableDate(new Date());
		}
		user.update();
		renderAjaxResultForSuccess("更新成功");
	}

	public void getGroupCheck() {
		String id = getPara("userId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> list = StationQuery.me().findByUserCheck(id, dataArea);
		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("id"));
			map.put("name", record.getStr("station_name"));
			if (record.getStr("realname") == null) {
				uncheckList.add(map);
			} else {
				checkList.add(map);
			}
		}
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("checkList", checkList);
		data.put("uncheckList", uncheckList);
		renderJson(data);
	}

	@RequiresPermissions(value = { "/admin/user", "/admin/all" }, logical = Logical.OR)
	public void saveStation() {
		String id = getPara("userId");
		String[] ids = getParaValues("stationIds[]");
		User user = UserQuery.me().findById(id);
		String stationIds = "";
		for (int i = 0; i < ids.length; i++) {
			if (i == ids.length - 1) {
				stationIds = stationIds + ids[i] + ",";
			} else {
				stationIds = stationIds + ids[i];
			}
		}
		user.setStationId(stationIds);
		if (user.saveOrUpdate()) {
			MenuManager.clearListByKey(id);
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("保存失败");
		}
	}

	public void getGroup() {
		String id = getPara("userid");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Group> groups = GroupQuery.me().findByDept(DataAreaUtil.getDealerDataAreaByCurUserDataArea(dataArea));
		List<Map<String, Object>> list = new ArrayList<>();
		for (Group group : groups) {
			if (group.getId().equals("0")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("id", group.getId());
			map.put("name", group.getGroupName());
			if (!StringUtils.isBlank(id)) {
				List<UserGroupRel> userGroups = UserGroupRelQuery.me().findByUserId(id);
				for (UserGroupRel usergroup : userGroups) {
					if (usergroup.getGroupId().equals(group.getId())) {
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

	public void getUserGroupCheck() {
		String id = getPara("userId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> list = GroupQuery.me().findByUserCheck(id, dataArea);
		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("id"));
			map.put("name", record.getStr("group_name"));
			if (record.getStr("userRelId") == null) {
				uncheckList.add(map);
			} else {
				checkList.add(map);
			}
		}
		Map<String, List<Map<String, Object>>> data = new HashMap<>();
		data.put("checkList", checkList);
		data.put("uncheckList", uncheckList);
		renderJson(data);
	}

	@Before(Tx.class)
	public void saveGroup() {
		String id = getPara("userId");
		String[] ids = getParaValues("groupIds[]");
		List<UserGroupRel> userGroupRels = UserGroupRelQuery.me().findByUserId(id);
		List<UserGroupRel> userGroupRelList = new ArrayList<>();
	    if (userGroupRels.size() == 0) {
	    	for (String groupId : ids) {
	    		UserGroupRel userGroupRel = new UserGroupRel();
	    		userGroupRel.setId(StrKit.getRandomUUID());
				userGroupRel.setUserId(id);
				userGroupRel.setGroupId(groupId);
				userGroupRelList.add(userGroupRel);
			}
	    	Db.batchSave(userGroupRelList, userGroupRelList.size());
			renderAjaxResultForSuccess("保存成功");
		}else {
			  for (UserGroupRel userGroupRel : userGroupRels) {
			   UserGroupRelQuery.me().batchDelete(userGroupRel.getId().toString());
			}
		      for (String s : ids) {
		    	  UserGroupRel userGroupRel = new UserGroupRel();
		    	  userGroupRel.setId(StrKit.getRandomUUID());
				  userGroupRel.setUserId(id);
				  userGroupRel.setGroupId(s);
				  userGroupRelList.add(userGroupRel);
			}
		    Db.batchSave(userGroupRelList, userGroupRelList.size());
			renderAjaxResultForSuccess("保存成功");
		}
	}
	
	@RequiresPermissions(value = { "/admin/user/uploading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)
	public void upload() {
	
		render("upload.html");
	}
	
	@RequiresPermissions(value = { "/admin/user/uploading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)
	public void userTemplate() {
		String realPath = getSession().getServletContext().getRealPath("\\");
		renderFile(new File(realPath + "\\WEB-INF\\admin\\user\\userTemplate.xlsx"));
	}
	
	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/user/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void uploading() {
		int inCnt = 0;
		int existCnt = 0;
		File file = getFile().getFile();
		
		String deptId = getPara("departmentId");
		Department dept = DepartmentQuery.me().findById(deptId);
		
		ImportParams params = new ImportParams();

		List<UserExecel> list = ExcelImportUtil.importExcel(file, UserExecel.class, params);
		
		for (UserExecel excel : list) {
			String userId = "";
			User us = null;
			UserGroupRel userGroupRel = null;

			// 检查用户是否存在
			us = UserQuery.me().findByMobileAndDeptId(excel.getMobile(),deptId);
			List<User> users = UserQuery.me().findAll();
			int count = users.size();
			String nickName = "qdyuser"+(++count);
			Group group = GroupQuery.me().findDataAreaAndGroupName(DataAreaUtil.getDealerDataAreaByCurUserDataArea(dept.getDataArea()), excel.getUserGroup());
			if (us == null) {
				us = new User();
				userId = StrKit.getRandomUUID();
				us.set("id", userId);
				this.setUser(us, excel);
				us.set("create_date", new Date());
				us.set("group_name",excel.getUserGroup());
				us.set("username", nickName);
				String dataArea = DataAreaUtil.dataAreaSetByUser(dept.getDataArea());
				us.set("data_area", dataArea);
				us.set("salt", EncryptUtils.salt());
				us.set("password", EncryptUtils.encryptPassword("123456", us.getSalt()));
				us.set("department_id", deptId);
				us.set("department_name", dept.getDeptName());
				us.save();
				
				userGroupRel = new UserGroupRel();
				userGroupRel.set("id",StrKit.getRandomUUID());
				userGroupRel.set("user_id", userId);
				userGroupRel.set("group_id", group.getId());
				userGroupRel.save();
				inCnt++;
			} else {
				existCnt++;
			}

		}

		renderAjaxResultForSuccess("成功导入客户" + inCnt + "个,已存在客户" + existCnt + "个");
	}
	
	private void setUser(User user, UserExecel excel) {
		user.set("realname", excel.getContact());
		user.set("nickname", excel.getNickname());
		user.set("mobile", excel.getMobile());
		user.set("status", 1);
		user.set("create_date", new Date());
	}
	
	public void updatePassword() {
		render("password.html");
	}
	
	public void changePassword() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String oldPassword = getPara("oldPassword");
		oldPassword = EncryptUtils.encryptPassword(new String(oldPassword), user.getSalt());
		if (!oldPassword.equals(user.getPassword())) {
			renderAjaxResultForError("旧密码错误,请重新输入!");
		} else {
			String newPassword = getPara("newPassword");
			newPassword = EncryptUtils.encryptPassword(new String(newPassword), user.getSalt());
			user.setPassword(newPassword);
			user.update();
			renderAjaxResultForSuccess("修改成功");
		}
	}
	
	@RequiresPermissions(value = { "/admin/user/downloading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {
	
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
		
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\user\\"
				+ "userInfo.xlsx";
		
		Page<Record> page = UserQuery.me().paginateAll(1, Integer.MAX_VALUE, "", dataArea + "%");
		List<Record> userList = page.getList();
		
		List<UserExecel> excellist = Lists.newArrayList();
		for (Record record : userList) {
		
			UserExecel excel = new UserExecel();
			excel.setNickname((String) record.get("nickname"));
			excel.setContact((String) record.get("realname"));
			excel.setMobile((String) record.get("mobile"));
			excel.setUserGroup((String) record.get("groupNames"));
			excel.setDeptName((String) record.get("department_name"));
			excellist.add(excel);
		}
		
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, UserExecel.class, excellist);
		File file = new File(filePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ExcelExportUtil.closeExportBigExcel();
		
		renderFile(new File(filePath));
	}	
	
	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
	public void resetPassword() {
		String[] ids = getParaValues("dataItem");
		int count = UserQuery.me().batchReset(ids);
		if (count > 0) {
			MenuManager.clearListByKeys(ids);
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}		
	}
	
}
