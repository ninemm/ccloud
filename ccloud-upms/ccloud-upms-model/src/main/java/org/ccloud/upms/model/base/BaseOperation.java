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
package org.ccloud.upms.model.base;

import org.ccloud.message.MessageKit;
import org.ccloud.model.core.JModel;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOperation<M extends BaseOperation<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "operation";
	public static final String METADATA_TYPE = "operation";

	public static final String ACTION_ADD = "operation:add";
	public static final String ACTION_DELETE = "operation:delete";
	public static final String ACTION_UPDATE = "operation:update";

	public void removeCache(Object key){
		if(key == null) return;
		CacheKit.remove(CACHE_NAME, key);
	}

	public void putCache(Object key,Object value){
		CacheKit.put(CACHE_NAME, key, value);
	}

	public M getCache(Object key){
		return CacheKit.get(CACHE_NAME, key);
	}

	public M getCache(Object key,IDataLoader dataloader){
		return CacheKit.get(CACHE_NAME, key, dataloader);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null){ return false; }
		if(!(o instanceof BaseOperation<?>)){return false;}

		BaseOperation<?> m = (BaseOperation<?>) o;
		if(m.getId() == null){return false;}

		return m.getId().compareTo(this.getId()) == 0;
	}

	@Override
	public boolean save() {
		boolean saved = super.save();
		if (saved) { MessageKit.sendMessage(ACTION_ADD, this); }
		return saved;
	}

	@Override
	public boolean delete() {
		boolean deleted = super.delete();
		if (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }
		return deleted;
	}

	@Override
	public boolean deleteById(Object idValue) {
		boolean deleted = super.deleteById(idValue);
		if (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }
		return deleted;
	}

	@Override
	public boolean update() {
		boolean update = super.update();
		if (update) { MessageKit.sendMessage(ACTION_UPDATE, this); }
		return update;
	}

	public void setId(java.lang.String id) {
		set("id", id);
	}

	public java.lang.String getId() {
		return get("id");
	}

	public void setModuleId(java.lang.String moduleId) {
		set("module_id", moduleId);
	}

	public java.lang.String getModuleId() {
		return get("module_id");
	}

	public void setModuleName(java.lang.String moduleName) {
		set("module_name", moduleName);
	}

	public java.lang.String getModuleName() {
		return get("module_name");
	}

	public void setOperationName(java.lang.String operationName) {
		set("operation_name", operationName);
	}

	public java.lang.String getOperationName() {
		return get("operation_name");
	}

	public void setOperationCode(java.lang.String operationCode) {
		set("operation_code", operationCode);
	}

	public java.lang.String getOperationCode() {
		return get("operation_code");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

	public void setIsPrivilege(java.lang.Integer isPrivilege) {
		set("is_privilege", isPrivilege);
	}

	public java.lang.Integer getIsPrivilege() {
		return get("is_privilege");
	}

	public void setIsSplitePage(java.lang.Integer isSplitePage) {
		set("is_splite_page", isSplitePage);
	}

	public java.lang.Integer getIsSplitePage() {
		return get("is_splite_page");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}

	public void setOrderList(java.lang.Integer orderList) {
		set("order_list", orderList);
	}

	public java.lang.Integer getOrderList() {
		return get("order_list");
	}

	public void setCreateDate(java.util.Date createDate) {
		set("create_date", createDate);
	}

	public java.util.Date getCreateDate() {
		return get("create_date");
	}

	public void setModifyDate(java.util.Date modifyDate) {
		set("modify_date", modifyDate);
	}

	public java.util.Date getModifyDate() {
		return get("modify_date");
	}

}