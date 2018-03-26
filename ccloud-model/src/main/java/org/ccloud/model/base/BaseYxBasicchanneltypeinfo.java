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
public abstract class BaseYxBasicchanneltypeinfo<M extends BaseYxBasicchanneltypeinfo<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "yx_basicchanneltypeinfo";
	public static final String METADATA_TYPE = "yx_basicchanneltypeinfo";

	public static final String ACTION_ADD = "yx_basicchanneltypeinfo:add";
	public static final String ACTION_DELETE = "yx_basicchanneltypeinfo:delete";
	public static final String ACTION_UPDATE = "yx_basicchanneltypeinfo:update";

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
		if(!(o instanceof BaseYxBasicchanneltypeinfo<?>)){return false;}

		BaseYxBasicchanneltypeinfo<?> m = (BaseYxBasicchanneltypeinfo<?>) o;
		if(m.getPrimaryKey() == null){return false;}

		return m.getPrimaryKey().compareTo(this.getPrimaryKey()) == 0;
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

	public void setChannelTypeID(java.lang.Integer ChannelTypeID) {
		set("ChannelTypeID", ChannelTypeID);
	}

	public java.lang.Integer getChannelTypeID() {
		return getInt("ChannelTypeID");
	}

	public void setChannelTypeName(java.lang.String ChannelTypeName) {
		set("ChannelTypeName", ChannelTypeName);
	}

	public java.lang.String getChannelTypeName() {
		return getStr("ChannelTypeName");
	}

	public void setCodeName(java.lang.String CodeName) {
		set("CodeName", CodeName);
	}

	public java.lang.String getCodeName() {
		return getStr("CodeName");
	}

	public void setParentID(java.lang.Integer ParentID) {
		set("ParentID", ParentID);
	}

	public java.lang.Integer getParentID() {
		return getInt("ParentID");
	}

	public void setClassCode(java.lang.String ClassCode) {
		set("ClassCode", ClassCode);
	}

	public java.lang.String getClassCode() {
		return getStr("ClassCode");
	}

	public void setCreateTime(java.util.Date CreateTime) {
		set("CreateTime", CreateTime);
	}

	public java.util.Date getCreateTime() {
		return get("CreateTime");
	}

	public void setModifyTime(java.util.Date ModifyTime) {
		set("ModifyTime", ModifyTime);
	}

	public java.util.Date getModifyTime() {
		return get("ModifyTime");
	}

	public void setFlag(java.lang.Integer Flag) {
		set("Flag", Flag);
	}

	public java.lang.Integer getFlag() {
		return getInt("Flag");
	}

	public void setSysCreateTime(java.util.Date SysCreateTime) {
		set("SysCreateTime", SysCreateTime);
	}

	public java.util.Date getSysCreateTime() {
		return get("SysCreateTime");
	}

	public void setSysModifyTime(java.util.Date SysModifyTime) {
		set("SysModifyTime", SysModifyTime);
	}

	public java.util.Date getSysModifyTime() {
		return get("SysModifyTime");
	}

}
