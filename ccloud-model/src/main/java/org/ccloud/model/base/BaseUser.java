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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.ccloud.cache.JCacheKit;
import org.ccloud.message.MessageKit;
import org.ccloud.model.core.JModel;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;

import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "user";
	public static final String METADATA_TYPE = "user";

	public static final String ACTION_ADD = "user:add";
	public static final String ACTION_DELETE = "user:delete";
	public static final String ACTION_UPDATE = "user:update";

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
		if(!(o instanceof BaseUser<?>)){return false;}

		BaseUser<?> m = (BaseUser<?>) o;
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

	public void setUsername(java.lang.String username) {
		set("username", username);
	}

	public java.lang.String getUsername() {
		return get("username");
	}

	public void setRealname(java.lang.String realname) {
		set("realname", realname);
	}

	public java.lang.String getRealname() {
		return get("realname");
	}

	public void setNickname(java.lang.String nickname) {
		set("nickname", nickname);
	}

	public java.lang.String getNickname() {
		return get("nickname");
	}

	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	public java.lang.String getMobile() {
		return get("mobile");
	}

	public void setPassword(java.lang.String password) {
		set("password", password);
	}

	public java.lang.String getPassword() {
		return get("password");
	}
	
	public void setAvatar(java.lang.String avatar) {
		set("avatar", avatar);
	}

	public java.lang.String getAvatar() {
		return get("avatar");
	}

	public void setSalt(java.lang.String salt) {
		set("salt", salt);
	}

	public java.lang.String getSalt() {
		return get("salt");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setUnableDate(java.util.Date unableDate) {
		set("unable_date", unableDate);
	}

	public java.util.Date getUnableDate() {
		return get("unable_date");
	}

	public void setDepartmentId(java.lang.String departmentId) {
		set("department_id", departmentId);
	}

	public java.lang.String getDepartmentId() {
		return get("department_id");
	}

	public void setDepartmentName(java.lang.String departmentName) {
		set("department_name", departmentName);
	}

	public java.lang.String getDepartmentName() {
		return get("department_name");
	}

	public void setStationId(java.lang.String stationId) {
		set("station_id", stationId);
	}

	public java.lang.String getStationId() {
		return get("station_id");
	}

	public void setStationName(java.lang.String stationName) {
		set("station_name", stationName);
	}

	public java.lang.String getStationName() {
		return get("station_name");
	}

	public void setGroupId(java.lang.String groupId) {
		set("group_id", groupId);
	}

	public java.lang.String getGroupId() {
		return get("group_id");
	}

	public void setGroupName(java.lang.String groupName) {
		set("group_name", groupName);
	}

	public java.lang.String getGroupName() {
		return get("group_name");
	}

	public void setDeptIds(java.lang.String deptIds) {
		set("dept_ids", deptIds);
	}

	public java.lang.String getDeptIds() {
		return get("dept_ids");
	}

	public void setDeptNames(java.lang.String deptNames) {
		set("dept_names", deptNames);
	}

	public java.lang.String getDeptNames() {
		return get("dept_names");
	}

	public void setUserIds(java.lang.String userIds) {
		set("user_ids", userIds);
	}

	public java.lang.String getUserIds() {
		return get("user_ids");
	}

	public void setUserNames(java.lang.String userNames) {
		set("user_names", userNames);
	}

	public java.lang.String getUserNames() {
		return get("user_names");
	}

	public void setWechatOpenId(java.lang.String wechatOpenId) {
		set("wechat_open_id", wechatOpenId);
	}

	public java.lang.String getWechatOpenId() {
		return get("wechat_open_id");
	}
	
	public void setWechatUseriId(java.lang.String wechatUseriId) {
		set("wechat_userid", wechatUseriId);
	}

	public java.lang.String getWechatUseriId() {
		return get("wechat_userid");
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
	
	public void setProvince(java.lang.String province) {
		set("province", province);
	}

	public java.lang.String getProvince() {
		return getStr("province");
	}

	public void setCity(java.lang.String city) {
		set("city", city);
	}

	public java.lang.String getCity() {
		return getStr("city");
	}

	public void setRegion(java.lang.String region) {
		set("region", region);
	}

	public java.lang.String getRegion() {
		return getStr("region");
	}
	
	public void setDataArea(java.lang.String dataArea) {
		set("data_area", dataArea);
	}
	
	public java.lang.String getDataArea() {
		return get("data_area");
	}

}
