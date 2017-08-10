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
package org.ccloud.upms.model;

import org.ccloud.model.core.Table;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ccloud.model.ModelSorter.ISortModel;
import org.ccloud.model.base.BaseDepartment;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="department",primaryKey="id")
public class Department extends BaseDepartment<Department> implements ISortModel<Department>{

	private static final long serialVersionUID = 1L;
	public static final String CACHE_KEY = "dept_list";
	
	private int layer = 0;
	private Department parent;
	private List<Department> childList;

	@Override
	public boolean save() {
		
		clearList();
		
		CacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		return super.save();
	}

	@Override
	public boolean saveOrUpdate() {
		
		clearList();
		
		removeCache(getId());
		CacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		
		return super.saveOrUpdate();
	}
	
	@Override
	public boolean update() {
		
		clearList();
		
		removeCache(getId());
		CacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		
		return super.update();
	}

	@Override
	public boolean delete() {
		
		clearList();
		
		removeCache(getId());
		CacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		
		return super.delete();
	}
	
	@Override
	public boolean deleteById(Object idValue) {
		
		clearList();
		
		removeCache(idValue);
		CacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		
		return super.deleteById(idValue);
	}

	public <T> T getFromListCache(Object key, IDataLoader dataloader) {
		Set<String> inCacheKeys = CacheKit.get(CACHE_NAME, "cachekeys");

		Set<String> cacheKeyList = new HashSet<String>();
		if (inCacheKeys != null) {
			cacheKeyList.addAll(inCacheKeys);
		}

		cacheKeyList.add(key.toString());
		CacheKit.put(CACHE_NAME, "cachekeys", cacheKeyList);

		return CacheKit.get("dept_list", key, dataloader);
	}
	
	public Department getParent() {
		return parent;
	}

	public void setParent(Department parent) {
		this.parent = parent;
	}
	
	public void addChild(Department child) {
        if (null == childList) {
        	childList = new ArrayList<>();
        }

        if (!childList.contains(child)) {
            childList.add(child);
        }
	}	

	public List<Department> getChildList() {
		return this.childList;
	}

	public void setChildList(List<Department> childList) {
		this.childList = childList;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public String getLayerString() {
		String layerString = "";
		for (int i = 0; i < layer; i++) {
			layerString += "— ";
		}
		return layerString;
	}
	
    public void clearList() {
        Set<String> list = CacheKit.get(CACHE_NAME, "cacheDeptkeys");
        if (list != null && list.size() > 0) {
            for (String key : list) {

                // 过滤

                CacheKit.remove("dept_list", key);
            }
        }
    }
	
	
}
