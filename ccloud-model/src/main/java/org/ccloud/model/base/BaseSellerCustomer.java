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
public abstract class BaseSellerCustomer<M extends BaseSellerCustomer<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_seller_customer";
	public static final String METADATA_TYPE = "cc_seller_customer";

	public static final String ACTION_ADD = "cc_seller_customer:add";
	public static final String ACTION_DELETE = "cc_seller_customer:delete";
	public static final String ACTION_UPDATE = "cc_seller_customer:update";

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
		if(!(o instanceof BaseSellerCustomer<?>)){return false;}

		BaseSellerCustomer<?> m = (BaseSellerCustomer<?>) o;
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

	public void setSellerId(java.lang.String sellerId) {
		set("seller_id", sellerId);
	}

	public java.lang.String getSellerId() {
		return getStr("seller_id");
	}

	public void setCustomerId(java.lang.String customerId) {
		set("customer_id", customerId);
	}

	public java.lang.String getCustomerId() {
		return getStr("customer_id");
	}

	public void setNickname(java.lang.String nickname) {
		set("nickname", nickname);
	}

	public java.lang.String getNickname() {
		return getStr("nickname");
	}

	public void setCreditAmount(java.lang.Integer creditAmount) {
		set("credit_amount", creditAmount);
	}

	public java.lang.Integer getCreditAmount() {
		return getInt("credit_amount");
	}

	public void setBalanceAmount(java.lang.Integer balanceAmount) {
		set("balance_amount", balanceAmount);
	}

	public java.lang.Integer getBalanceAmount() {
		return getInt("balance_amount");
	}

	public void setIsChecked(java.lang.Integer isChecked) {
		set("is_checked", isChecked);
	}

	public java.lang.Integer getIsChecked() {
		return getInt("is_checked");
	}

	public void setIsEnabled(java.lang.Integer isEnabled) {
		set("is_enabled", isEnabled);
	}

	public java.lang.Integer getIsEnabled() {
		return getInt("is_enabled");
	}

	public void setIsArchive(java.lang.Integer isArchive) {
		set("is_archive", isArchive);
	}

	public java.lang.Integer getIsArchive() {
		return getInt("is_archive");
	}

	public void setCustomerTypeIds(java.lang.String customerTypeIds) {
		set("customer_type_ids", customerTypeIds);
	}

	public java.lang.String getCustomerTypeIds() {
		return getStr("customer_type_ids");
	}

	public void setSubType(java.lang.String subType) {
		set("sub_type", subType);
	}

	public java.lang.String getSubType() {
		return getStr("sub_type");
	}

	public void setCustomerKind(java.lang.String customerKind) {
		set("customer_kind", customerKind);
	}

	public java.lang.String getCustomerKind() {
		return getStr("customer_kind");
	}
	
	public void setImageListStore(java.lang.String imageListStore) {
		set("image_list_store", imageListStore);
	}

	public java.lang.String getImageListStore() {
		return getStr("image_list_store");
	}
	
	public void setLng(java.math.BigDecimal lng) {
		set("lng", lng);
	}

	public java.math.BigDecimal getLng() {
		return get("lng");
	}

	public void setLat(java.math.BigDecimal lat) {
		set("lat", lat);
	}

	public java.math.BigDecimal getLat() {
		return get("lat");
	}

	public void setLocation(java.lang.String location) {
		set("location", location);
	}

	public java.lang.String getLocation() {
		return getStr("location");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setProcDefKey(java.lang.String procDefKey) {
		set("proc_def_key", procDefKey);
	}

	public java.lang.String getProcDefKey() {
		return getStr("proc_def_key");
	}

	public void setProcInstId(java.lang.String procInstId) {
		set("proc_inst_id", procInstId);
	}

	public java.lang.String getProcInstId() {
		return getStr("proc_inst_id");
	}

	public void setDataArea(java.lang.String dataArea) {
		set("data_area", dataArea);
	}

	public java.lang.String getDataArea() {
		return getStr("data_area");
	}

	public void setDeptId(java.lang.String deptId) {
		set("dept_id", deptId);
	}

	public java.lang.String getDeptId() {
		return getStr("dept_id");
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
