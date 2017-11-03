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
import org.ccloud.menu.MenuManager;
import org.ccloud.model.Module;
import org.ccloud.model.Operation;
import org.ccloud.model.Role;
import org.ccloud.model.RoleOperationRel;
import org.ccloud.model.Station;
import org.ccloud.model.StationOperationRel;
import org.ccloud.model.query.ModuleQuery;
import org.ccloud.model.query.OperationQuery;
import org.ccloud.model.query.RoleOperationRelQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.StationOperationRelQuery;
import org.ccloud.model.query.StationQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/operation", viewPath = "/WEB-INF/admin/operation")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/operation","/admin/all"},logical=Logical.OR)
public class _OperationController extends JBaseCRUDController<Operation> {
	
	@Override
	public void index() {
		render("index.html");
	}	

	public void list() {

		String keyword = getPara("k", "");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		Page<Operation> page = OperationQuery.me().paginate(getPageNumber(), getPageSize(), keyword, null);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@Override
	@RequiresPermissions(value={"/admin/operation/edit","/admin/all"},logical=Logical.OR)
	public void edit() {

		List<Module> list = ModuleQuery.me().findAll();
		setAttr("list", list);

		String id = getPara("id");
		Operation operation = OperationQuery.me().findById(id);
		List<Station> slist = StationQuery.me().findByOperationId(id);
		String stationName = "";
		String stationId = "";
		for (int i = 0; i < slist.size(); i++) {
			if (i == 0) {
				stationName = slist.get(i).getStationName();
				stationId = slist.get(i).getId();
			} else {
				stationName = stationName + "," + slist.get(i).getStationName();
				stationId = stationId + "," + slist.get(i).getId();
			}
		}
		setAttr("stationName", stationName);
		setAttr("stationId", stationId);
		setAttr("operation", operation);
	}

	@Override
	@Before(UCodeInterceptor.class)
	public void save() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	Operation operation = getModel(Operation.class);

                String roleList = getPara("roleList");
                String[] roleId = roleList.split(",");
                
                String stationList = getPara("station_id");
                String[] stationId = stationList.split(",");                

                if (!operation.saveOrUpdate())
                    return false;

                String operationId = operation.getId();

                RoleOperationRelQuery.me().deleteByOperationId(operationId);
                StationOperationRelQuery.me().deleteByOperationId(operationId);

                List<RoleOperationRel> rRelList = new ArrayList<>();
                List<StationOperationRel> sRelList = new ArrayList<>();

                for (int i = 0; i < roleId.length; i++) {
                	RoleOperationRel roleOperationRel = getModel(RoleOperationRel.class);

                	roleOperationRel.setOperationId(operationId);;
                	roleOperationRel.setRoleId(roleId[i]);
                	roleOperationRel.setId(StrKit.getRandomUUID());
                	rRelList.add(roleOperationRel);
                }

                for (int i = 0; i < stationId.length; i++) {
                	StationOperationRel stationOperationRel = getModel(StationOperationRel.class);

                	stationOperationRel.setOperationId(operationId);;
                	stationOperationRel.setStationId(stationId[i]);
                	stationOperationRel.setId(StrKit.getRandomUUID());
                	sRelList.add(stationOperationRel);
                }
                
                Db.batchSave(rRelList, rRelList.size());
                Db.batchSave(sRelList, sRelList.size());
                return true;
            }
        });

        if (isSave) {
        	MenuManager.clearAllList();
        	renderAjaxResultForSuccess("保存成功");
        } else {
        	renderAjaxResultForError("保存失败");
        }
	}
	
	@Override
	@RequiresPermissions(value={"/admin/operation/edit","/admin/all"},logical=Logical.OR)
	@Before(Tx.class)
	public void delete() {
		String id = getPara("id");
		final Operation r = OperationQuery.me().findById(id);
		int deleteRole = RoleOperationRelQuery.me().deleteByOperationId(id);
		int deleteStation = StationOperationRelQuery.me().deleteByOperationId(id);
		if (r != null) {
			if (r.delete() && (deleteRole + deleteStation) > 0) {
				MenuManager.clearAllList();
				renderAjaxResultForSuccess("删除成功");
				return;
			}
		}
		renderAjaxResultForError("删除失败");
	}	
	
	public void getRoleAndStation() {
        String id = getPara("id");
        List<Role> roles = RoleQuery.me().findAll();
        List<Map<String, Object>> roleList = new ArrayList<>();

        for (Role role : roles) {
            Map<String, Object> map = new HashMap<>();

            map.put("id", role.getId());
            map.put("name", role.getRoleName());

            if (RoleQuery.me().queryRoleOperation(role.getId(), id).size() == 0) map.put("isvalid", 0);
            else map.put("isvalid", 1);

            roleList.add(map);
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("roles", roleList);
        renderJson(map);
	}
	
    public void station_tree() {
        List<Map<String, Object>> list = StationQuery.me().findStationListAsTree(1);
        setAttr("treeData", JSON.toJSON(list));
    }
    
	@RequiresPermissions(value={"/admin/operation/edit","/admin/all"},logical=Logical.OR)
	public void enable() {
		String id = getPara("id");
		Operation operation = OperationQuery.me().findById(id);
		if (operation.getIsPrivilege() == 0) {
			operation.setIsPrivilege(1);
		} else {
			operation.setIsPrivilege(0);
		}
		operation.update();
		renderAjaxResultForSuccess("更新成功");
	}    
	
}
