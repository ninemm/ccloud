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
package model.base;

import org.ccloud.message.MessageKit;
import org.ccloud.model.core.JModel;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseQyBasicflowtype<M extends BaseQyBasicflowtype<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "qy_basicflowtype";
	public static final String METADATA_TYPE = "qy_basicflowtype";

	public static final String ACTION_ADD = "qy_basicflowtype:add";
	public static final String ACTION_DELETE = "qy_basicflowtype:delete";
	public static final String ACTION_UPDATE = "qy_basicflowtype:update";

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
		if(!(o instanceof BaseQyBasicflowtype<?>)){return false;}

		BaseQyBasicflowtype<?> m = (BaseQyBasicflowtype<?>) o;
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

	public void setFlowTypeID(java.lang.String FlowTypeID) {
		set("FlowTypeID", FlowTypeID);
	}

	public java.lang.String getFlowTypeID() {
		return getStr("FlowTypeID");
	}

	public void setFlowTypeName(java.lang.String FlowTypeName) {
		set("FlowTypeName", FlowTypeName);
	}

	public java.lang.String getFlowTypeName() {
		return getStr("FlowTypeName");
	}

	public void setParentID(java.lang.String ParentID) {
		set("ParentID", ParentID);
	}

	public java.lang.String getParentID() {
		return getStr("ParentID");
	}

	public void setMemo(java.lang.String Memo) {
		set("Memo", Memo);
	}

	public java.lang.String getMemo() {
		return getStr("Memo");
	}

	public void setCreateTime(java.lang.String CreateTime) {
		set("CreateTime", CreateTime);
	}

	public java.lang.String getCreateTime() {
		return get("CreateTime");
	}

	public void setModifyTime(java.lang.String ModifyTime) {
		set("ModifyTime", ModifyTime);
	}

	public java.lang.String getModifyTime() {
		return get("ModifyTime");
	}

	public void setFlag(java.lang.Integer Flag) {
		set("Flag", Flag);
	}

	public java.lang.Integer getFlag() {
		return getInt("Flag");
	}

	public void setYxFlowtypeid(java.lang.Integer yxFlowtypeid) {
		set("YX_FlowTypeID", yxFlowtypeid);
	}

	public java.lang.Integer getYxFlowtypeid() {
		return getInt("YX_FlowTypeID");
	}

	public void setYxParentid(java.lang.Integer yxParentid) {
		set("YX_ParentID", yxParentid);
	}

	public java.lang.Integer getYxParentid() {
		return getInt("YX_ParentID");
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
