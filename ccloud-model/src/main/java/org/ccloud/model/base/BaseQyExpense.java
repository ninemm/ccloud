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

import org.ccloud.cache.JCacheKit;
import org.ccloud.message.MessageKit;
import org.ccloud.model.core.JModel;
import com.jfinal.plugin.activerecord.IBean;

import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseQyExpense<M extends BaseQyExpense<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "qy_expense";
	public static final String METADATA_TYPE = "qy_expense";

	public static final String ACTION_ADD = "qy_expense:add";
	public static final String ACTION_DELETE = "qy_expense:delete";
	public static final String ACTION_UPDATE = "qy_expense:update";

	public void removeCache(Object key){
		if(key == null) return;
		JCacheKit.remove(CACHE_NAME, key);
	}

	public void putCache(Object key,Object value){
		JCacheKit.put(CACHE_NAME, key, value);
	}

	public M getCache(Object key){
		return JCacheKit.get(CACHE_NAME, key);
	}

	public M getCache(Object key,IDataLoader dataloader){
		return JCacheKit.get(CACHE_NAME, key, dataloader);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null){ return false; }
		if(!(o instanceof BaseQyExpense<?>)){return false;}

		BaseQyExpense<?> m = (BaseQyExpense<?>) o;
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

	public void setActivityNo(java.lang.String ActivityNo) {
		set("ActivityNo", ActivityNo);
	}

	public java.lang.String getActivityNo() {
		return getStr("ActivityNo");
	}

	public void setExpenseName(java.lang.String ExpenseName) {
		set("ExpenseName", ExpenseName);
	}

	public java.lang.String getExpenseName() {
		return getStr("ExpenseName");
	}

	public void setFranchiserCode(java.lang.String FranchiserCode) {
		set("FranchiserCode", FranchiserCode);
	}

	public java.lang.String getFranchiserCode() {
		return getStr("FranchiserCode");
	}

	public void setBrandCode(java.lang.String BrandCode) {
		set("BrandCode", BrandCode);
	}

	public java.lang.String getBrandCode() {
		return getStr("BrandCode");
	}

	public void setProvinceCode(java.lang.String ProvinceCode) {
		set("ProvinceCode", ProvinceCode);
	}

	public java.lang.String getProvinceCode() {
		return getStr("ProvinceCode");
	}

	public void setCityCode(java.lang.String CityCode) {
		set("CityCode", CityCode);
	}

	public java.lang.String getCityCode() {
		return getStr("CityCode");
	}

	public void setExpenseBeginDate(java.lang.String ExpenseBeginDate) {
		set("ExpenseBeginDate", ExpenseBeginDate);
	}

	public java.lang.String getExpenseBeginDate() {
		return get("ExpenseBeginDate");
	}

	public void setExpenseEndDate(java.lang.String ExpenseEndDate) {
		set("ExpenseEndDate", ExpenseEndDate);
	}

	public java.lang.String getExpenseEndDate() {
		return get("ExpenseEndDate");
	}

	public void setInputDay(java.lang.Integer InputDay) {
		set("InputDay", InputDay);
	}

	public java.lang.Integer getInputDay() {
		return getInt("InputDay");
	}

	public void setMemo(java.lang.String Memo) {
		set("Memo", Memo);
	}

	public java.lang.String getMemo() {
		return getStr("Memo");
	}

	public void setInputAccountType(java.lang.String InputAccountType) {
		set("InputAccountType", InputAccountType);
	}

	public java.lang.String getInputAccountType() {
		return getStr("InputAccountType");
	}

	public void setInputPayType(java.lang.String InputPayType) {
		set("InputPayType", InputPayType);
	}

	public java.lang.String getInputPayType() {
		return getStr("InputPayType");
	}

	public void setApplyAmount(java.lang.Double ApplyAmount) {
		set("ApplyAmount", ApplyAmount);
	}

	public java.lang.Double getApplyAmount() {
		return getDouble("ApplyAmount");
	}

	public void setOperateYear(java.lang.String OperateYear) {
		set("OperateYear", OperateYear);
	}

	public java.lang.String getOperateYear() {
		return getStr("OperateYear");
	}

	public void setApplyPersonNumber(java.lang.String ApplyPersonNumber) {
		set("ApplyPersonNumber", ApplyPersonNumber);
	}

	public java.lang.String getApplyPersonNumber() {
		return getStr("ApplyPersonNumber");
	}

	public void setApplyPersonName(java.lang.String ApplyPersonName) {
		set("ApplyPersonName", ApplyPersonName);
	}

	public java.lang.String getApplyPersonName() {
		return getStr("ApplyPersonName");
	}

	public void setApplyTime(java.lang.String ApplyTime) {
		set("ApplyTime", ApplyTime);
	}

	public java.lang.String getApplyTime() {
		return get("ApplyTime");
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

	public void setYxFlowidno(java.lang.Long yxFlowidno) {
		set("YX_FlowIDNO", yxFlowidno);
	}

	public java.lang.Long getYxFlowidno() {
		return getLong("YX_FlowIDNO");
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
