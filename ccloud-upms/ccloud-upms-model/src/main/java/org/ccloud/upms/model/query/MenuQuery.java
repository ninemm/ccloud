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
package org.ccloud.upms.model.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.model.Menu;
import org.ccloud.model.ModelSorter;
import org.ccloud.model.query.JBaseQuery;

import com.google.common.collect.Lists;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class MenuQuery extends JBaseQuery { 

	protected static final Menu DAO = new Menu();
	private static final MenuQuery QUERY = new MenuQuery();

	public static MenuQuery me() {
		return QUERY;
	}

	public Menu findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				
				StringBuilder sqlBuilder = new StringBuilder("select m.*, p.name as parent_name ");
				sqlBuilder.append("from `menu` m ");
				sqlBuilder.append("join `menu` p on p.id = m.parent_id ");
				sqlBuilder.append("where m.id = ?");
				return DAO.findFirst(sqlBuilder.toString(), id);
			}
		});
	}
	
	public List<Menu> findMenuList(String parentId, String orderby) {
		final StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM menu m ");
		sqlBuilder.append("where m.id <> '0' ");
		
		final List<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(sqlBuilder, "parent_id", parentId, params, false);
		buildOrderBy(orderby, sqlBuilder);
		
		String key = buildKey(null, null, null, null, orderby);
		
		List<Menu> data = DAO.getFromListCache(key, new IDataLoader() {
			@Override
			public Object load() {
				if (params.isEmpty()) {
					return DAO.find(sqlBuilder.toString());
				}
				return DAO.find(sqlBuilder.toString(), params.toArray());
			}
		});
		
		if (data == null)
			return null;
		return new ArrayList<Menu>(data);
	}
	
	private String buildKey(String module, Object... params) {
		StringBuffer keyBuffer = new StringBuffer(module == null ? "" : "module:" + module);
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				keyBuffer.append("-p").append(i).append(":").append(params[i]);
			}
		}
		return keyBuffer.toString().replace(" ", "");
	}

	public Page<Menu> paginate(int pageNumber, int pageSize, String parentId, String keyword, String orderby) {
		
		String select = "select m.*, s.name as sys_name, me.name as parent_name ";
		
		StringBuilder fromBuilder = new StringBuilder("from `menu` m ");
		fromBuilder.append("join `systems` s on s.id = m.system_id ");
		fromBuilder.append("join `menu` me on me.id = m.parent_id ");

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(fromBuilder, "m.parent_id", parentId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "m.name", keyword, params, needWhere);
		
		buildOrderBy(orderby, fromBuilder);

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public List<Map<String, Object>> findMenuListAsTree(Integer enable) {
		
		List<Menu> list = findMenuList(null, "order_list asc");
		ModelSorter.tree(list);
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("text", "菜单根节点");
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuild(list)); 
		resTreeList.add(map);
		return resTreeList;
		
	}
	
	private List<Map<String, Object>> doBuild(List<Menu> list) {
		List<Map<String, Object>> resTreeList = new ArrayList<>();
		for(Menu menu : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("text", menu.getName());
			map.put("tags", Lists.newArrayList(menu.getId()));
//			if(menu.getLong("selected") == 1) {
//				Map<String, Object> stateMap = new HashMap<>();
//				stateMap.put("checked", true);
//				stateMap.put("selected", true);
//				stateMap.put("disabled", false);
//				stateMap.put("expanded", true);
//				map.put("state", stateMap);
//			}
//			if(!resTreeList.contains(map))
				resTreeList.add(map);
			
			if(menu.getChildList() != null && menu.getChildList().size() > 0) {
				map.put("nodes", doBuild(menu.getChildList()));
			}
		}
		return resTreeList;
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	protected void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		
		fromBuilder.append(" order by ");
		
		if (StrKit.isBlank(orderBy)) {
			fromBuilder.append("m.order_list asc ");
			return ;
		}
		
		String orderbyInfo[] = orderBy.trim().split("\\s+");
		orderBy = orderbyInfo[0];
		
		fromBuilder.append("m.order_list ");
		
		if (orderbyInfo.length == 1) {
			fromBuilder.append("desc");
		} else {
			fromBuilder.append(orderbyInfo[1]);
		}
	}
}
