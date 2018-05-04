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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.mgt.RealmSecurityManager;
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
import org.ccloud.model.UserHistory;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.UserExecel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.shiro.ShiroDbRealm;
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
		List<User> list = page.getList();
		for (User user : list) {
			try {
				if (StrKit.notBlank(user.getNickname())) {
					String nickname = URLDecoder.decode(user.getNickname(), "utf-8");
					user.setNickname(nickname);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (page != null) {
			setAttr("page", page);
		}
//		String nickname = get("nickname");
//		try {
//			if (StrKit.notBlank(nickname)) {
//				nickname = URLDecoder.decode((String) get("nickname"), "utf-8");
//			}
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return nickname;

	}

	@Override
	@RequiresPermissions(value = { "/admin/user", "/admin/all" }, logical = Logical.OR)
	public void save() {
		final User user = getModel(User.class);
	/*	try {
		if (StrKit.notBlank(user.getNickname())) {
			String nickname = URLEncoder.encode(user.getNickname(), "utf-8");
			user.setNickname(nickname);
		}
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
		String stationList = getPara("stationList");
		String stationName = getPara("stationName");
		String groupList = getPara("groupList");
		String groupName = getPara("groupName");
		//用于判断操作人员点击的是编辑按钮还是转用按钮
		String t = getPara("t");
		String NewGroupName = groupName.substring(0, groupName.length() -1);
		String deptName = getPara("parent_name");
		user.setDepartmentName(deptName);
		user.setStationId(stationList);
		user.setStationName(stationName);
		// user.setGroupId(groupList);
		 user.setGroupName(NewGroupName);
		 if(StringUtils.isBlank(user.getId())) {
			 User u = UserQuery.me()._findUserByUsername(user.getUsername());
			 if(u!=null) {
				 renderAjaxResultForError("用户名已存在！");
				 return;
			 }
		 }else{
			 User u = UserQuery.me()._findUserByUsername(user.getUsername());
			 if(u!=null && u.getUsername().equals(user.getUsername()) && !user.getId().equals(u.getId())) {
				renderAjaxResultForError("用户名已存在！");
				return;
			 }
		 }
		if (StringUtils.isBlank(user.getId())) {
			List<User> users = UserQuery.me().findByMobile(user.getMobile());
			for(User us : users) {
				if(StringUtils.isBlank(us.getWechatOpenId())) {
					continue;
				}else {
					user.setWechatOpenId(us.getWechatOpenId());
					break;
				}
			}
			for(User us : users) {
				if(StringUtils.isBlank(us.getAvatar())) {
					continue;
				}else {
					user.setAvatar(us.getAvatar());;
					break;
				}
			}
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
				if(t.equals("1")) {
					user.setWechatOpenId("");
				}
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
	    	clearUserCache(user);
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("false");
		}
	}

	private void clearUserCache(User user) {
		MenuManager.clearListByKey(user.getId());
		RealmSecurityManager rsm = (RealmSecurityManager)SecurityUtils.getSecurityManager();    
        ShiroDbRealm realm = (ShiroDbRealm)rsm.getRealms().iterator().next();   
        realm.clearCachedAuthorizationInfo(user);
	}

	@Override
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
	public void edit() {
		String id = getPara("id");
		String t = getPara("t");
		if (id != null) {
			User user = UserQuery.me().findById(id);
			setAttr("user", user);
			setAttr("t",t);
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
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
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
		UserHistory userHistory = UserHistoryQuery.me().findById(user.getId());
		if (user.getStatus() == 0) {
			user.setStatus(1);
		} else {
			user.setStatus(0);
			user.setUnableDate(new Date());
			if(userHistory == null) {
				userHistory = new UserHistory();
				userHistory.setId(user.getId());
				userHistory.setUsername(user.getUsername());
				userHistory.setRealname(user.getRealname());
				userHistory.setNickname(user.getNickname());
				userHistory.setMobile(user.getMobile());
				userHistory.setPassword(user.getPassword());
				userHistory.setAvatar(user.getAvatar());
				userHistory.setSalt(user.getSalt());
				userHistory.setStatus(user.getStatus());
				userHistory.setUnableDate(new Date());
				userHistory.setDepartmentId(user.getDepartmentId());
				userHistory.setDepartmentName(user.getDepartmentName());
				userHistory.setStationId(user.getStationId());
				userHistory.setStationName(user.getStationName());
				userHistory.setGroupId(user.getGroupId());
				userHistory.setGroupName(user.getGroupName());
				userHistory.setDeptIds(user.getDeptIds());
				userHistory.setDeptNames(user.getDeptNames());
				userHistory.setUserIds(user.getUserIds());
				userHistory.setUserNames(user.getUserNames());
				userHistory.setDataArea(user.getDataArea());
				userHistory.setWechatOpenId(user.getWechatOpenId());
				userHistory.setWechatUserid(user.getWechatUseriId());
				userHistory.setCreateDate(new Date());
				userHistory.save();
			}
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
		List<Group> groups = GroupQuery.me().findByDept(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString(), id);
		List<Map<String, Object>> list = new ArrayList<>();
		for (Group group : groups) {
			if (group.getId().equals("0")) {
				continue;
			}
			Map<String, Object> map = new HashMap<>();
			map.put("id", group.getId());
			map.put("name", group.getGroupName());
			if (StringUtils.isNotBlank(id)) {
				if (StringUtils.isNotBlank(group.getStr("user_id"))) {
					map.put("isvalid", 1);
				} else {
					map.put("isvalid", 0);
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
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";
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
	
	public void userTemplate() {
		String realPath = getSession().getServletContext().getRealPath("\\") +"\\WEB-INF\\admin\\user\\userTemplate.xls";
		renderFile(new File(realPath.replace("\\", "/")));
	}
	
	@Before(Tx.class)
	public void uploading() {
		int inCnt = 0;
		int existCnt = 0;
		int errorCnt = 0;
		File file = getFile().getFile();
		
		String deptId = getPara("departmentId");
		Department dept = DepartmentQuery.me().findById(deptId);
		
		ImportParams params = new ImportParams();
		params.setReadRows(99);//一次读100条
		List<UserExecel> list = ExcelImportUtil.importExcel(file, UserExecel.class, params);
		String  username = "";
		while(list.size()>0) {
			for (UserExecel excel : list) {
				String userId = "";
				User us = null;
				UserGroupRel userGroupRel = null;
				User user = UserQuery.me()._findUserByUsername(excel.getUsername());
				if(user !=null) {
					username +=excel.getUsername()+"、";
					errorCnt++;
					continue;
				}
				
				if(excel.getMobile()==null) {
					break;
				}
				User user00 = new User();
				List<User> uss = UserQuery.me().findByMobile(excel.getMobile());
				for(User user01:uss) {
					if(!user01.getWechatOpenId().equals("")) {
						user00 = user01;
						break;
					}
				}
				// 检查用户是否存在
				us = UserQuery.me().findByMobileAndDeptId(excel.getMobile(),deptId);
				Group group = GroupQuery.me().findDataAreaAndGroupName(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString(), excel.getUserGroup());
				if (us == null) {
					us = new User();
					userId = StrKit.getRandomUUID();
					us.setId(userId);
					this.setUser(us, excel);
					us.setCreateDate(new Date());
					String uGdest = "";
					if (excel.getUserGroup()!=null) {
						Pattern p = Pattern.compile("\\s*|\t|\r|\n");
						Matcher m = p.matcher(excel.getUserGroup());
						uGdest = m.replaceAll("");
					}
					us.setGroupName(uGdest);
					String udest = "";
					if (excel.getUsername()!=null) {
						Pattern p = Pattern.compile("\\s*|\t|\r|\n");
						Matcher m = p.matcher(excel.getUsername());
						udest = m.replaceAll("");
					}
					us.setUsername(udest);
					String dataArea = DataAreaUtil.dataAreaSetByUser(dept.getDataArea());
					us.setDataArea(dataArea);
					if(user00 != null) {
						us.setWechatOpenId(user00.getWechatOpenId());
						us.setAvatar(user00.getAvatar());
						us.setNickname(user00.getNickname());
					}
					us.setSalt(EncryptUtils.salt());
					us.setPassword(EncryptUtils.encryptPassword("123456", us.getSalt()));
					us.setDepartmentId(deptId);
					us.setDepartmentName(dept.getDeptName());
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
			params.setStartRows(params.getStartRows() + list.size());
			list = ExcelImportUtil.importExcel(file, UserExecel.class, params);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("inCnt", inCnt);
		map.put("existCnt", existCnt);
		map.put("errorCnt", errorCnt);
		if(!username.equals("")) {
			map.put("usName", username.substring(0, username.length()-1));
		}
		renderJson(map);
//		renderAjaxResultForSuccess("成功导入用户" + inCnt + "个,已存在用户" + existCnt + "个,导入失败"+errorCnt+"个");
	}
	
	private void setUser(User user, UserExecel excel) {
		String dest = "";
		if (excel.getContact()!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(excel.getContact());
			dest = m.replaceAll("");
		}
		user.set("realname", dest);
		String destt = "";
		if (excel.getMobile()!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(excel.getMobile());
			destt = m.replaceAll("");
		}
		user.set("mobile", destt);
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
			List<User> list = UserQuery.me().findByMobile(user.getMobile());
			for (User users : list) {
				users.setPassword(EncryptUtils.encryptPassword(new String(newPassword), users.getSalt()));
				users.update();
			}
			renderAjaxResultForSuccess("修改成功");
		}
	}
	
	@RequiresPermissions(value = { "/admin/user/downloading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {
		String keyword = getPara("k");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		//多选部门ID集合
		//String deptIds = getPara("deptId");
		
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\user\\"
				+ "用户信息.xlsx";
		String userId = "";
		Page<User> page = UserQuery.me().paginateUser(1, Integer.MAX_VALUE,  keyword, dataArea, "u.create_date",userId);
		List<User> userList = page.getList();
		
		List<UserExecel> excellist = Lists.newArrayList();
		for (User record : userList) {
		
			UserExecel excel = new UserExecel();
			excel.setUsername((String) record.get("username"));
			excel.setContact((String) record.get("realname"));
			excel.setMobile((String) record.get("mobile"));
			excel.setUserGroup((String) record.get("group_name"));
			excel.setDeptName((String) record.get("department_name"));
			excellist.add(excel);
		}
		
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, UserExecel.class, excellist);
		File file = new File(filePath.replace("\\", "/"));
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
		
		renderFile(new File(filePath.replace("\\", "/")));
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
	
	/*public void station_tree() {
    	String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        List<Map<String, Object>> list = DepartmentQuery.me().findDepartmentListAsTree(1, dataArea);
        setAttr("treeData", JSON.toJSON(list));
    }*/
	@RequiresPermissions(value = { "/admin/user/edit", "/admin/all" }, logical = Logical.OR)
	public void send() {
		
	}
	
	public void sendSave(String id) {
		User user = UserQuery.me().findById(id);
		UserHistory userHistory = new UserHistory();
		userHistory.setId(user.getId());
		userHistory.setUsername(user.getUsername());
		userHistory.setRealname(user.getRealname());
		userHistory.setNickname(user.getNickname());
		userHistory.setMobile(user.getMobile());
		userHistory.setPassword(user.getPassword());
		userHistory.setAvatar(user.getAvatar());
		userHistory.setSalt(user.getSalt());
		userHistory.setStatus(user.getStatus());
		userHistory.setUnableDate(new Date());
		userHistory.setDepartmentId(user.getDepartmentId());
		userHistory.setDepartmentName(user.getDepartmentName());
		userHistory.setStationId(user.getStationId());
		userHistory.setStationName(user.getStationName());
		userHistory.setGroupId(user.getGroupId());
		userHistory.setGroupName(user.getGroupName());
		userHistory.setDeptIds(user.getDeptIds());
		userHistory.setDeptNames(user.getDeptNames());
		userHistory.setUserIds(user.getUserIds());
		userHistory.setUserNames(user.getUserNames());
		userHistory.setDataArea(user.getDataArea());
		userHistory.setWechatOpenId(user.getWechatOpenId());
		userHistory.setWechatUserid(user.getWechatUseriId());
		userHistory.setCreateDate(new Date());
		userHistory.save();
//		renderAjaxResultForSuccess("更新成功");
	}
	
	//验证用户名不能重复
	public void checkUserName(){
		//用户名唯一
		String username = getPara("user.username");
		String userId = getPara("userId");
		Map<String, Boolean> map = new HashMap<>();
		boolean result = true;
		if(StringUtils.isBlank(userId)) {
			User user = UserQuery.me()._findUserByUsername(username);
			if(user!=null) {
				result = false;
			}
		}else {
			User us = UserQuery.me().findById(userId);
			if(us.getUsername().equals(username)) {
				result = true;
			}else {
				User user = UserQuery.me()._findUserByUsername(username);
				if(user!=null) {
					result = false;
				}
			}
		}
		map.put("valid", result);
		renderJson(map);
	}

	public void dealerUser(){
		List<Record> department  = SellerQuery.me().findDeptByLevel();

		List<Map<String, Object>> departmentList = new ArrayList<>();

		for(Record name : department) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", name.getStr("id"));
			item.put("text", name.getStr("text"));
			departmentList.add(item);
		}

		setAttr("departmentType", JSON.toJSON(departmentList));
		render("dealer_user.html");
	}

	@RequiresPermissions(value = { "/admin/all"}, logical = Logical.OR)
	public void dealerUserList(){

		String sort = getPara("sort");
		String sortOrder = getPara("sortOrder");
		Map<String, String[]> paraMap = getParaMap();
		String keyword = StringUtils.getArrayFirst(paraMap.get("k"));
		String departmentType = getPara("departmentType");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		Page<Record> page = UserQuery.me().paginateByDeptAndKey(getPageNumber(), getPageSize(), keyword, departmentType, sort, sortOrder);
		List<Record> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);
	}
}
