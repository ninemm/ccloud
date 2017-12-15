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


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Role;
import org.ccloud.model.User;
import org.ccloud.model.query.OperationQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.ModuleInfo;
import org.ccloud.model.vo.OperationInfo;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;

import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/role", viewPath = "/WEB-INF/admin/role")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/role","/admin/all"},logical=Logical.OR)
public class _RoleController extends JBaseCRUDController<Role> { 

	
	@Override
	public void index() {
		
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) setAttr("k", keyword);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
		Page<Role> page = RoleQuery.me().paginate(getPageNumber(), getPageSize(), keyword, DataAreaUtil.getUserDealerDataArea(dataArea), "r.order_list");
		if (page != null) {
			setAttr("page", page);
		}
		
	}
	
	@Override
	@Before(UCodeInterceptor.class)
	public void save() {
		
		Role role = getModel(Role.class);
        User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        role.setDeptId(user.getDepartmentId());
        role.setDataArea(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
		if (role.saveOrUpdate())
			renderAjaxResultForSuccess("新增成功");
		else
			renderAjaxResultForError("修改失败!");
	}	
	
	@Override
	@RequiresPermissions(value={"/admin/role/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			Role role = RoleQuery.me().findById(id);
			setAttr("role", role);
		}
	}
	
	@Override
	@RequiresPermissions(value={"/admin/role/edit","/admin/all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		final Role r = RoleQuery.me().findById(id);
		List<User> list = UserQuery.me().findByRoleId(id);
		if (list.size() > 0) {
			renderAjaxResultForError("已有用户拥有此角色");
			return;
		} else {
			if (r != null) {
				if (r.delete()) {
					renderAjaxResultForSuccess("删除成功");
					return;
				}
			}
			renderAjaxResultForError("删除失败");
		}
	}	

	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"/admin/role/edit","/admin/all"},logical=Logical.OR)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		List<User> list = UserQuery.me().findByRoleIds(ids);
		if (list.size() > 0) {
			renderAjaxResultForError("已有用户拥有此角色");
			return;
		}
		int count = RoleQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}
	
    public void permission() {
        String roleId = getPara("id");
        setAttr("id", getPara("id"));
        try {
			setAttr("role_name", new String(getPara("roleName").getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		List<Role> ownRoleList = null;
		if (!isSuperAdmin) {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			ownRoleList = RoleQuery.me().findByUserId(user.getId());
		}
        List<Record> list = OperationQuery.me().queryRoleOperation(roleId, ownRoleList);
        List<ModuleInfo> moduleList = new ArrayList<>();
        List<String> system = new ArrayList<>();
        List<String> parentModule = new ArrayList<>();
        int loop = 0;
        int rowSpan = 1;
        int sysRowSpan = 1;
        for (Record record : list) {
            String[] operationId = record.get("operation_code").toString().split(",");
            String[] operationName = record.get("operation_name").toString().split(",");
            String[] stationIds = record.get("station_id").toString().split(",");
        	ModuleInfo moduleInfo = new ModuleInfo();
        	moduleInfo.setModuleId(record.getStr("id"));
        	moduleInfo.setModuleName(record.getStr("module_name"));
        	if (!system.contains(record.getStr("sys_name"))) {
        		moduleInfo.setSystemName(record.getStr("sys_name"));
        		if (loop > 0) {
        			this.checkSystemRowSpan(moduleList, system, sysRowSpan);
        			sysRowSpan = 1;
        		}
        		system.add(moduleInfo.getSystemName());
        	} else {
        		sysRowSpan++;
        		if (loop == list.size()-1) {
        			this.checkSystemRowSpan(moduleList, system, sysRowSpan);
        		}        		
        	}
        	if (!parentModule.contains(record.getStr("parent_name"))) {
        		moduleInfo.setParentModuleName(record.getStr("parent_name"));
        		if (loop > 0) {
        			this.checkRowSpan(moduleList, parentModule, rowSpan);
        			rowSpan = 1;
        		}
        		parentModule.add(moduleInfo.getParentModuleName());
        	} else {
        		rowSpan++;
        		if (loop == list.size()-1) {
        			this.checkRowSpan(moduleList, parentModule, rowSpan);
        		}
        	}
        	List<OperationInfo> operationInfos = new ArrayList<>();
            for (int i = 0; i < operationId.length; i++) {
            	OperationInfo info = new OperationInfo();
            	info.setOperationCode(operationId[i]);
            	info.setOperationName(operationName[i]);
            	if (!stationIds[i].equals("0")) {
            		info.setIsChecked(1);
            	} else {
            		info.setIsChecked(0);
            	}
            	operationInfos.add(info);
            }
            moduleInfo.setList(operationInfos);
            moduleList.add(moduleInfo);
            loop++;
		}
        setAttr("moduleList", moduleList);
    }
    
    private void checkRowSpan(List<ModuleInfo> moduleList, List<String> system, int rowSpan) {
    	for (ModuleInfo moduleInfo : moduleList) {
			if (moduleInfo.getParentModuleName() != null && 
					moduleInfo.getParentModuleName().equals(system.get(system.size()-1))) {
				moduleInfo.setParentRowSpan(rowSpan);
				break;
			}
		}
    }
    
    private void checkSystemRowSpan(List<ModuleInfo> moduleList, List<String> parentModule, int rowSpan) {
    	for (ModuleInfo moduleInfo : moduleList) {
			if (moduleInfo.getSystemName() != null && 
					moduleInfo.getSystemName().equals(parentModule.get(parentModule.size()-1))) {
				moduleInfo.setSystemRowSpan(rowSpan);
				break;
			}
		}
    }    
	
}
