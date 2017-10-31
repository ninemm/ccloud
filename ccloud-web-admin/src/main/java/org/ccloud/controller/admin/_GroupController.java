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
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Group;
import org.ccloud.model.GroupRoleRel;
import org.ccloud.model.Role;
import org.ccloud.model.query.GroupQuery;
import org.ccloud.model.query.GroupRoleRelQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/group", viewPath = "/WEB-INF/admin/group")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"group:view","admin:all"},logical=Logical.OR)
public class _GroupController extends JBaseCRUDController<Group> {
    @Override
    public void index() {

        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) setAttr("k", keyword);

        Page<Group> page = GroupQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "order_list");
        if (page != null) {
            setAttr("page", page);
        }

    }
    
	@Override
	@RequiresPermissions(value={"group:edit","admin:all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Group group = GroupQuery.me().findById(id);
			setAttr("group", group);
		}
	}    

    public  void getRole() {

        String id = getPara("groupid");
        List<Role> roles = RoleQuery.me().findAll();
        List<Map<String, Object>> list = new ArrayList<>();

        for (Role role : roles) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", role.getId());
            map.put("name", role.getRoleName());

            if (RoleQuery.me().queryRoleGroupRelation(role.getId(), id).size() == 0) map.put("isvalid", 0);
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

        if (isSave) renderAjaxResultForSuccess();
        else renderAjaxResultForError();
    }

    @Override
    @Before(UCodeInterceptor.class)
    @RequiresPermissions(value={"group:edit","admin:all"},logical=Logical.OR)
    public void delete() {
        boolean isDelete = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                String id = getPara("id");
                GroupRoleRelQuery.me().deleteByGroupId(id);

                if (!GroupQuery.me().delete(id)) return false;

                return true;
            }
        });

        if(isDelete) renderAjaxResultForSuccess();
        else renderAjaxResultForError();

    }

    @Before(UCodeInterceptor.class)
    @RequiresPermissions(value={"group:edit","admin:all"},logical=Logical.OR)
    public void batchDelete() {

        String[] ids = getParaValues("dataItem");
        int count = GroupQuery.me().batchDelete(ids);

        for (String groupId : ids)
            GroupRoleRelQuery.me().deleteByGroupId(groupId);

        if (count > 0) {
            renderAjaxResultForSuccess("删除成功");
        } else {
            renderAjaxResultForError("删除失败!");
        }

    }
}

