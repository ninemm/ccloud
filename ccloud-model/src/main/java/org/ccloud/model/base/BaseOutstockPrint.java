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
package org.ccloud.model.base;

import org.ccloud.message.MessageKit;
import org.ccloud.model.core.JModel;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOutstockPrint<M extends BaseOutstockPrint<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_outstock_print";
	public static final String METADATA_TYPE = "cc_outstock_print";

	public static final String ACTION_ADD = "cc_outstock_print:add";
	public static final String ACTION_DELETE = "cc_outstock_print:delete";
	public static final String ACTION_UPDATE = "cc_outstock_print:update";

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
		if(!(o instanceof BaseOutstockPrint<?>)){return false;}

		BaseOutstockPrint<?> m = (BaseOutstockPrint<?>) o;
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
		return getStr("id");
	}

	public void setOrderId(java.lang.String orderId) {
		set("order_id", orderId);
	}

	public java.lang.String getOrderId() {
		return getStr("order_id");
	}

	public void setBizUserId(java.lang.String bizUserId) {
		set("biz_user_id", bizUserId);
	}

	public java.lang.String getBizUserId() {
		return getStr("biz_user_id");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public void setDeptId(java.lang.String deptId) {
		set("dept_id", deptId);
	}

	public java.lang.String getDeptId() {
		return getStr("dept_id");
	}

	public void setDataArea(java.lang.String dataArea) {
		set("data_area", dataArea);
	}

	public java.lang.String getDataArea() {
		return getStr("data_area");
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