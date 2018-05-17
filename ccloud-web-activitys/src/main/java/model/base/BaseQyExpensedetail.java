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
public abstract class BaseQyExpensedetail<M extends BaseQyExpensedetail<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "qy_expensedetail";
	public static final String METADATA_TYPE = "qy_expensedetail";

	public static final String ACTION_ADD = "qy_expensedetail:add";
	public static final String ACTION_DELETE = "qy_expensedetail:delete";
	public static final String ACTION_UPDATE = "qy_expensedetail:update";

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
		if(!(o instanceof BaseQyExpensedetail<?>)){return false;}

		BaseQyExpensedetail<?> m = (BaseQyExpensedetail<?>) o;
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

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return getLong("id");
	}

	public void setExpenseDetailID(java.lang.String ExpenseDetailID) {
		set("ExpenseDetailID", ExpenseDetailID);
	}

	public java.lang.String getExpenseDetailID() {
		return getStr("ExpenseDetailID");
	}

	public void setExpenseID(java.lang.String ExpenseID) {
		set("ExpenseID", ExpenseID);
	}

	public java.lang.String getExpenseID() {
		return getStr("ExpenseID");
	}

	public void setFlowID(java.lang.String FlowID) {
		set("FlowID", FlowID);
	}

	public java.lang.String getFlowID() {
		return getStr("FlowID");
	}

	public void setFlowNo(java.lang.String FlowNo) {
		set("FlowNo", FlowNo);
	}

	public java.lang.String getFlowNo() {
		return getStr("FlowNo");
	}

	public void setFlowTypeID(java.lang.String FlowTypeID) {
		set("FlowTypeID", FlowTypeID);
	}

	public java.lang.String getFlowTypeID() {
		return getStr("FlowTypeID");
	}

	public void setItem1(java.lang.String Item1) {
		set("Item1", Item1);
	}

	public java.lang.String getItem1() {
		return getStr("Item1");
	}

	public void setItem2(java.lang.String Item2) {
		set("Item2", Item2);
	}

	public java.lang.String getItem2() {
		return getStr("Item2");
	}

	public void setItem3(java.lang.String Item3) {
		set("Item3", Item3);
	}

	public java.lang.String getItem3() {
		return getStr("Item3");
	}

	public void setItem4(java.lang.String Item4) {
		set("Item4", Item4);
	}

	public java.lang.String getItem4() {
		return getStr("Item4");
	}

	public void setItem5(java.lang.String Item5) {
		set("Item5", Item5);
	}

	public java.lang.String getItem5() {
		return getStr("Item5");
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