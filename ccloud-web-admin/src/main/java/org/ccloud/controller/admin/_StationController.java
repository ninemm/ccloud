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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.model.Operation;
import org.ccloud.model.Role;
import org.ccloud.model.Station;
import org.ccloud.model.User;
import org.ccloud.model.query.OperationQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.StationOperationRelQuery;
import org.ccloud.model.query.StationQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.ModuleInfo;
import org.ccloud.model.vo.OperationInfo;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/station", viewPath = "/WEB-INF/admin/station")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/station","/admin/all"},logical=Logical.OR)
public class _StationController extends JBaseCRUDController<Station> {
	
	@Override
	public void index() {
		render("index.html");
	}	

    public void list() {
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        
        String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

        Page<Station> page = StationQuery.me().paginate(getPageNumber(), getPageSize(),keyword, dataArea, "order_list");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);

    }
    @Override
    @RequiresPermissions(value={"/admin/station/edit","/admin/all"},logical=Logical.OR)
    public void edit() {
        String id = getPara("id");
        Station station = StationQuery.me().findById(id);
        setAttr("station", station);
    }

    @Override
    @Before(UCodeInterceptor.class)
    public void save() {

        Station station = getModel(Station.class);
        User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        station.setDeptId(user.getDepartmentId());
        station.setDataArea(DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
        if (StringUtils.isBlank(station.getId())) {
        	station.setIsParent(0);
        }
        if (station.saveOrUpdate()) {
        	StationQuery.me().updateParent(station);
        	renderAjaxResultForSuccess("新增成功");
        } else {
        	renderAjaxResultForError("修改失败!");
        }
    }

    @Override
    @Before(UCodeInterceptor.class)
    @RequiresPermissions(value={"/admin/station/edit","/admin/all"},logical=Logical.OR)
    public void delete() {
        boolean isDelete = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Station station = getModel(Station.class);
                String id = getPara("id");
                List<User> list = UserQuery.me().findByStation(id);
                if (list.size() > 0) {
                	return false;
                }
                if (StrKit.notBlank(id)) {
                    Station station1 = StationQuery.me().findById(id);
                    //若是叶子节点则直接删除（删除的时候做了监听处理其父节点的is_parent）
                    if(station1.getIsParent() == 0) {

                        StationOperationRelQuery.me().deleteByStationId(station1.getId());
                        if (!station.deleteById(id)) {
                        	return false;
                        } else {
                        	StationQuery.me().updateParent(station1);
                        	return true;
                        }
                    } else {
                        //若非叶子节点，寻找其所有叶子节点id做删除
                        List<String> ids = new ArrayList<>();
                        ids.add(id);
                        int k = 0;
                        //寻找所有叶子节点id
                        while (k < ids.size()) {
                            List<Station> stationList = StationQuery.me().findByParentId(ids.get(k));
                            if (stationList != null) {
                                for(int i = 0; i < stationList.size(); i++) {
                                    ids.add(stationList.get(i).getId());
	                                List<User> ulist = UserQuery.me().findByStation(stationList.get(i).getId());
	                                if (ulist.size() > 0) {
	                                	return false;
	                                }
                                }
                            }
                            k++;
                        }
                        int count = StationQuery.me().batchDelete(ids);

                        StationOperationRelQuery.me().batchDeleteByStationId(ids);
                        StationQuery.me().updateParent(station1);
                        if(count <= 0) return false;
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });

        if (isDelete) renderAjaxResultForSuccess("删除成功");
        else renderAjaxResultForError("已有用户拥有此岗位或删除失败");
    }

    public void station_tree() {
    	String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        List<Map<String, Object>> list = StationQuery.me().findStationListAsTree(1, dataArea);
        setAttr("treeData", JSON.toJSON(list));
    }

    public void assign() {
        setAttr("id", getPara("id"));
        try {
			setAttr("station_name", new String(getPara("stationName").getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    public void initAssign() {
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        String stationId = getPara("id");

        Page<Operation> page = OperationQuery.me().queryModuleAndOperation(1, 10000, keyword, null);

        // List<Object> rowList = new ArrayList<>();

        for (int i = 0; i < page.getTotalRow(); i++) {
            if(page.getList().get(i).get("operation_code")!=null) {
                String[] operationId = page.getList().get(i).get("operation_code").toString().split(",");
                String[] operationName = page.getList().get(i).get("operation_name").toString().split(",");

                List<Object> objectList = new ArrayList<>();
                for (int j = 0; j < operationId.length; j++) {
                    Map<String, Object> m = new HashMap<>();

                    m.put("operationName", operationName[j]);
                    m.put("operationId", operationId[j]);

                    if (StationOperationRelQuery.me().isValid(stationId, operationId[j]).size() == 0) m.put("isValid", 0);
                    else m.put("isValid", 1);
                    objectList.add(m);
                }
                page.getList().get(i).set("operation_code", objectList);
            }
        }

        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());

        renderJson(map);
    }
    
    public void permission() {
        String stationId = getPara("id");
        setAttr("id", getPara("id"));
        try {
			setAttr("station_name", new String(getPara("stationName").getBytes("ISO-8859-1"),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		List<Role> ownRoleList = null;
		if (!isSuperAdmin) {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			ownRoleList = RoleQuery.me().findByUserId(user.getId());
		}
        List<Record> list = OperationQuery.me().queryStationOperation(stationId,ownRoleList);
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
