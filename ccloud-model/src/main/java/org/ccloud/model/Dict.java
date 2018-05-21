/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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
package org.ccloud.model;

import org.ccloud.model.base.BaseDict;
import org.ccloud.model.core.Table;

import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="dict", primaryKey="id")
public class Dict extends BaseDict<Dict> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String CACHE_KEY = "dict_list";
	public static final String DICT_TYPE_CUSTOMER_SUBTYPE = "customer_subtype";

	@Override
	public boolean save() {
		
		CacheKit.remove(Dict.CACHE_NAME, CACHE_KEY);
		CacheKit.remove(Dict.CACHE_NAME, getType());
		return super.save();
	}

	@Override
	public boolean saveOrUpdate() {
		
		removeCache(getId());
		CacheKit.remove(Dict.CACHE_NAME, CACHE_KEY);
		CacheKit.remove(Dict.CACHE_NAME, getType());
		if (getId() != null) {
			return super.update();
		} else {
			return super.save();
		}
	}
	
	@Override
	public boolean update() {
		
		removeCache(getId());
		CacheKit.remove(Dict.CACHE_NAME, CACHE_KEY);
		CacheKit.remove(Dict.CACHE_NAME, getType());
		return super.update();
	}

	@Override
	public boolean delete() {
		
		removeCache(getId());
		CacheKit.remove(Dict.CACHE_NAME, CACHE_KEY);
		CacheKit.remove(Dict.CACHE_NAME, getType());
		return super.delete();
	}
	
	@Override
	public boolean deleteById(Object idValue) {
		
		removeCache(idValue);
		CacheKit.remove(Dict.CACHE_NAME, CACHE_KEY);
		CacheKit.remove(Dict.CACHE_NAME, getType());
		return super.deleteById(idValue);
	}
}
