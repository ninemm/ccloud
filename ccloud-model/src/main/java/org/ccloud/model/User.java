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
package org.ccloud.model;

import org.ccloud.RedisConsts;
import org.ccloud.cache.JCacheKit;
import org.ccloud.model.core.Table;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.OperationQuery;



import java.util.List;

import org.ccloud.model.base.BaseUser;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="user",primaryKey="id")
public class User extends BaseUser<User> {

	public static final String ROLE_ADMINISTRATOR = "admin";
	private static final long serialVersionUID = 1L;
	public static final String CACHE_KEY = "user_list";
	
	public boolean isAdministrator() {
		return ROLE_ADMINISTRATOR.equals(getUsername());
	}
	
	@Override
	public boolean save() {
		removeCache(getMobile());
		removeCache(getWechatOpenId());
		JCacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		JCacheKit.remove(User.CACHE_NAME, RedisConsts.REDIS_KEY_USER_LIST + getMobile());
		return super.save();
	}

	@Override
	public boolean saveOrUpdate() {
		
		removeCache(getId());
		removeCache(getUsername());
		removeCache(getMobile());
		removeCache(getWechatOpenId());
		JCacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		JCacheKit.remove(User.CACHE_NAME, RedisConsts.REDIS_KEY_USER_LIST + getMobile());
		
		return super.saveOrUpdate();
	}
	
	@Override
	public boolean update() {
		
		removeCache(getId());
		removeCache(getUsername());
		removeCache(getMobile());
		removeCache(getWechatOpenId());
		JCacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		JCacheKit.remove(User.CACHE_NAME, RedisConsts.REDIS_KEY_USER_LIST + getMobile());
		
		return super.update();
	}

	@Override
	public boolean delete() {
		
		removeCache(getId());
		removeCache(getMobile());
		removeCache(getWechatOpenId());
		JCacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		JCacheKit.remove(User.CACHE_NAME, RedisConsts.REDIS_KEY_USER_LIST + getMobile());
		
		return super.delete();
	}
	
	@Override
	public boolean deleteById(Object idValue) {
		
		removeCache(idValue);
		JCacheKit.remove(Department.CACHE_NAME, CACHE_KEY);
		JCacheKit.remove(User.CACHE_NAME, RedisConsts.REDIS_KEY_USER_LIST + getMobile());
		
		return super.deleteById(idValue);
	}
	
	public Department findDepartmentById() {
		return DepartmentQuery.me().findById(getDepartmentId());
	}
	
	public List<String> getUserPermission(User user) {
		return OperationQuery.me().getPermissionsByUser(user);
	}
	
}
