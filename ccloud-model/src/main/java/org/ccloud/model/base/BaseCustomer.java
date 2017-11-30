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
public abstract class BaseCustomer<M extends BaseCustomer<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_customer";
	public static final String METADATA_TYPE = "cc_customer";

	public static final String ACTION_ADD = "cc_customer:add";
	public static final String ACTION_DELETE = "cc_customer:delete";
	public static final String ACTION_UPDATE = "cc_customer:update";

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
		if(!(o instanceof BaseCustomer<?>)){return false;}

		BaseCustomer<?> m = (BaseCustomer<?>) o;
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

	public void setCustomerCode(java.lang.String customerCode) {
		set("customer_code", customerCode);
	}

	public java.lang.String getCustomerCode() {
		return getStr("customer_code");
	}

	public void setCustomerName(java.lang.String customerName) {
		set("customer_name", customerName);
	}

	public java.lang.String getCustomerName() {
		return getStr("customer_name");
	}

	public void setContact(java.lang.String contact) {
		set("contact", contact);
	}

	public java.lang.String getContact() {
		return getStr("contact");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	public java.lang.String getMobile() {
		return getStr("mobile");
	}

	public void setEmail(java.lang.String email) {
		set("email", email);
	}

	public java.lang.String getEmail() {
		return getStr("email");
	}

	public void setImgPath(java.lang.String imgPath) {
		set("img_path", imgPath);
	}

	public java.lang.String getImgPath() {
		return getStr("img_path");
	}

	public void setIsEnabled(java.lang.Integer isEnabled) {
		set("is_enabled", isEnabled);
	}

	public java.lang.Integer getIsEnabled() {
		return getInt("is_enabled");
	}

	public void setProvName(java.lang.String provName) {
		set("prov_name", provName);
	}

	public java.lang.String getProvName() {
		return getStr("prov_name");
	}

	public void setCityName(java.lang.String cityName) {
		set("city_name", cityName);
	}

	public java.lang.String getCityName() {
		return getStr("city_name");
	}

	public void setCountryName(java.lang.String countryName) {
		set("country_name", countryName);
	}

	public java.lang.String getCountryName() {
		return getStr("country_name");
	}

	public void setProvCode(java.lang.String provCode) {
		set("prov_code", provCode);
	}

	public java.lang.String getProvCode() {
		return getStr("prov_code");
	}

	public void setCityCode(java.lang.String cityCode) {
		set("city_code", cityCode);
	}

	public java.lang.String getCityCode() {
		return getStr("city_code");
	}

	public void setCountryCode(java.lang.String countryCode) {
		set("country_code", countryCode);
	}

	public java.lang.String getCountryCode() {
		return getStr("country_code");
	}

	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	public java.lang.String getAddress() {
		return getStr("address");
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

}
