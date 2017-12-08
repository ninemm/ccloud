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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.menu.MenuManager;
import org.ccloud.model.Group;
import org.ccloud.model.GroupRoleRel;
import org.ccloud.model.User;
import org.ccloud.model.UserGroupRel;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.GroupRoleRelQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.UserGroupRelQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/group", viewPath = "/WEB-INF/admin/group")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/group","/admin/all"},logical=Logical.OR)
public class _GroupController extends JBaseCRUDController<Group> {
    @Override
    public void index() {

        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) setAttr("k", keyword);
        
        String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

        Page<Group> page = GroupQuery.me().paginate(getPageNumber(), getPageSize(), keyword, dataArea, "order_list");
        if (page != null) {
            setAttr("page", page);
        }

    }
    
	@Override
	@RequiresPermissions(value={"/admin/group/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Group group = GroupQuery.me().findById(id);
			setAttr("group", group);
		}
	}    

    public void getRole() {
        String id = getPara("groupid");
        String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        List<Record> roles = RoleQuery.me().findBydeptAndGroup(dataArea, id);
        List<Map<String, Object>> list = new ArrayList<>();

        for (Record record : roles) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", record.getStr("id"));
            map.put("name", record.getStr("role_name"));

            if (record.getStr("role_id") == null) map.put("isvalid", 0);
            else map.put("isvalid", 1);

            list.add(map);
        }

        renderJson(list);

    }

    public void saveGroupAndGroupRoleRel() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Group group = getModel(Group.class);
                User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
            	group.setDeptId(user.getDepartmentId());
            	group.setDataArea(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));

                String roleList = getPara("roleList");
                String[] roleId = roleList.split(",");

                if (!group.saveOrUpdate())
                    return false;

                String groupId = group.getId();

                GroupRoleRelQuery.me().deleteByGroupId(groupId);

                List<GroupRoleRel> groupRoleRelList = new ArrayList<>();

                for (int i = 0; i < roleId.length; i++) {
                    GroupRoleRel groupRoleRel = getModel(GroupRoleRel.class);

                    groupRoleRel.setGroupId(groupId);
                    groupRoleRel.setRoleId(roleId[i]);
                    groupRoleRelList.add(groupRoleRel);
                    groupRoleRel.setId(StrKit.getRandomUUID());

                }

                Db.batchSave(groupRoleRelList, groupRoleRelList.size());
                return true;
            }
        });

        if (isSave) {
        	MenuManager.clearAllList();
        	renderAjaxResultForSuccess();
        } else { 
        	renderAjaxResultForError();
        }
    }

    @Override
    @Before(UCodeInterceptor.class)
    @RequiresPermissions(value={"/admin/group/edit","/admin/all"},logical=Logical.OR)
    public void delete() {
        boolean isDelete = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                String id = getPara("id");
                List<User> list = UserQuery.me().findByGroupId(id);
                if (list.size() > 0) {
                	return false;
                } else {
                    GroupRoleRelQuery.me().deleteByGroupId(id);

                    if (!GroupQuery.me().delete(id)) return false;

                    return true;                	
                }
            }
        });

        if(isDelete) renderAjaxResultForSuccess();
        else renderAjaxResultForError("分组下有用户或删除失败");

    }

    @Before(UCodeInterceptor.class)
    @RequiresPermissions(value={"/admin/group/edit","/admin/all"},logical=Logical.OR)
    public void batchDelete() {

        String[] ids = getParaValues("dataItem");
        List<User> list = UserQuery.me().findByGroupIds(ids);
        if (list.size() > 0) {
        	renderAjaxResultForError("分组下有用户");
        	return;
        }
        int count = GroupQuery.me().batchDelete(ids);
        
        for (String groupId : ids)
            GroupRoleRelQuery.me().deleteByGroupId(groupId);

        if (count > 0) {
            renderAjaxResultForSuccess("删除成功");
        } else {
            renderAjaxResultForError("删除失败!");
        }

    }
    
	
	public void getRoleCheck() {
		String id = getPara("groupId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> list = RoleQuery.me().findByRoleCheck(id, dataArea);
		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("id"));
			map.put("name", record.getStr("role_name"));
			if (record.getStr("check_status") == null) {
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
	public void saveRole() {
		String id = getPara("groupId");
		String[] ids = getParaValues("roleIds[]");
		List<GroupRoleRel> saveList = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			GroupRoleRel groupRoleRel = new GroupRoleRel();
			groupRoleRel.setId(StrKit.getRandomUUID());
			groupRoleRel.setGroupId(id);
			groupRoleRel.setRoleId(ids[i]);
			saveList.add(groupRoleRel);
		}
		GroupRoleRelQuery.me().deleteByGroupId(id);
		Db.batchSave(saveList, saveList.size());
		MenuManager.clearListByKey(id);
		renderAjaxResultForSuccess("保存成功");
	}
	
	
    public void saveGroupAndUserGroupRel() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Group group = getModel(Group.class);
                User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
            	group.setDeptId(user.getDepartmentId());
            	group.setDataArea(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));

                String userList = getPara("uList");
                String[] userId = userList.split(",");

                if (!group.saveOrUpdate())
                    return false;

                String groupId = group.getId();

                UserGroupRelQuery.me().deleteByGroupId(groupId);

                List<UserGroupRel> userGroupRelList = new ArrayList<>();

                for (int i = 0; i < userId.length; i++) {
                	UserGroupRel userGroupRel = getModel(UserGroupRel.class);

                	userGroupRel.setGroupId(groupId);
                	userGroupRel.setUserId(userId[i]);
                	userGroupRel.setId(StrKit.getRandomUUID());
                	userGroupRelList.add(userGroupRel);
                }

                Db.batchSave(userGroupRelList, userGroupRelList.size());
                return true;
            }
        });

        if (isSave) {
        	MenuManager.clearAllList();
        	renderAjaxResultForSuccess();
        } else { 
        	renderAjaxResultForError();
        }
    }
	
	
	public void getUser() {
        String id = getPara("groupid");
        String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        List<Record> user = UserQuery.me().findBydeptAndGroup(dataArea, id);
        List<Map<String, Object>> list = new ArrayList<>();

        for (Record record : user) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", record.getStr("id"));
            map.put("name", record.getStr("realname"));

            if (record.getStr("user_id") == null) map.put("isvalid", 0);
            else map.put("isvalid", 1);

            list.add(map);
        }

        renderJson(list);

    }
	
	public void getUserCheck() {
		String id = getPara("groupId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Record> list = UserQuery.me().findByUserCheck(id, dataArea);
		List<Map<String, Object>> checkList = new ArrayList<>();
		List<Map<String, Object>> uncheckList = new ArrayList<>();
		for (Record record : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.getStr("id"));
			map.put("name", record.getStr("realname"));
			if (record.getStr("check_status") == null) {
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
	public void saveUser() {
		String id = getPara("groupId");
		String[] ids = getParaValues("userIds[]");
		List<UserGroupRel> saveList = new ArrayList<>();
		for (int i = 0; i < ids.length; i++) {
			UserGroupRel userGroupRel = new UserGroupRel();
			userGroupRel.setId(StrKit.getRandomUUID());
			userGroupRel.setGroupId(id);
			userGroupRel.setUserId(ids[i]);
			saveList.add(userGroupRel);
		}
		UserGroupRelQuery.me().deleteByGroupId(id);
		Db.batchSave(saveList, saveList.size());
		MenuManager.clearListByKey(id);
		renderAjaxResultForSuccess("保存成功");
	}
	
}

