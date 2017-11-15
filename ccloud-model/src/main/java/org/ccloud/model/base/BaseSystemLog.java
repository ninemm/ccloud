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
public abstract class BaseSystemLog<M extends BaseSystemLog<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "system_log";
	public static final String METADATA_TYPE = "system_log";

	public static final String ACTION_ADD = "system_log:add";
	public static final String ACTION_DELETE = "system_log:delete";
	public static final String ACTION_UPDATE = "system_log:update";

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
		if(!(o instanceof BaseSystemLog<?>)){return false;}

		BaseSystemLog<?> m = (BaseSystemLog<?>) o;
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

	public void setStartDate(java.util.Date startDate) {
		set("start_date", startDate);
	}

	public java.util.Date getStartDate() {
		return get("start_date");
	}

	public void setEndDate(java.util.Date endDate) {
		set("end_date", endDate);
	}

	public java.util.Date getEndDate() {
		return get("end_date");
	}

	public void setActionStartDate(java.util.Date actionStartDate) {
		set("action_start_date", actionStartDate);
	}

	public java.util.Date getActionStartDate() {
		return get("action_start_date");
	}

	public void setActionEndDate(java.util.Date actionEndDate) {
		set("action_end_date", actionEndDate);
	}

	public java.util.Date getActionEndDate() {
		return get("action_end_date");
	}

	public void setActionCostTime(java.lang.Long actionCostTime) {
		set("action_cost_time", actionCostTime);
	}

	public java.lang.Long getActionCostTime() {
		return get("action_cost_time");
	}

	public void setViewCostTime(java.lang.Long viewCostTime) {
		set("view_cost_time", viewCostTime);
	}

	public java.lang.Long getViewCostTime() {
		return get("view_cost_time");
	}

	public void setTotalCostTime(java.lang.Long totalCostTime) {
		set("total_cost_time", totalCostTime);
	}

	public java.lang.Long getTotalCostTime() {
		return get("total_cost_time");
	}

	public void setCause(java.lang.String cause) {
		set("cause", cause);
	}

	public java.lang.String getCause() {
		return get("cause");
	}

	public void setCookie(java.lang.String cookie) {
		set("cookie", cookie);
	}

	public java.lang.String getCookie() {
		return get("cookie");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}

	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}

	public java.lang.String getIp() {
		return get("ip");
	}

	public void setMethod(java.lang.String method) {
		set("method", method);
	}

	public java.lang.String getMethod() {
		return get("method");
	}

	public void setReferer(java.lang.String referer) {
		set("referer", referer);
	}

	public java.lang.String getReferer() {
		return get("referer");
	}

	public void setRequestPath(java.lang.String requestPath) {
		set("request_path", requestPath);
	}

	public java.lang.String getRequestPath() {
		return get("request_path");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setUserAgent(java.lang.String userAgent) {
		set("user_agent", userAgent);
	}

	public java.lang.String getUserAgent() {
		return get("user_agent");
	}

	public void setOperationId(java.lang.String operationId) {
		set("operation_id", operationId);
	}

	public java.lang.String getOperationId() {
		return get("operation_id");
	}

	public void setAccept(java.lang.String accept) {
		set("accept", accept);
	}

	public java.lang.String getAccept() {
		return get("accept");
	}

	public void setAcceptEncoding(java.lang.String acceptEncoding) {
		set("accept_encoding", acceptEncoding);
	}

	public java.lang.String getAcceptEncoding() {
		return get("accept_encoding");
	}

	public void setAcceptLang(java.lang.String acceptLang) {
		set("accept_lang", acceptLang);
	}

	public java.lang.String getAcceptLang() {
		return get("accept_lang");
	}

	public void setConnection(java.lang.String connection) {
		set("connection", connection);
	}

	public java.lang.String getConnection() {
		return get("connection");
	}

	public void setHost(java.lang.String host) {
		set("host", host);
	}

	public java.lang.String getHost() {
		return get("host");
	}

	public void setXrequestedwith(java.lang.String xrequestedwith) {
		set("xrequestedwith", xrequestedwith);
	}

	public java.lang.String getXrequestedwith() {
		return get("xrequestedwith");
	}

	public void setPvids(java.lang.String pvids) {
		set("pvids", pvids);
	}

	public java.lang.String getPvids() {
		return get("pvids");
	}

	public void setUserId(java.lang.String userId) {
		set("user_id", userId);
	}

	public java.lang.String getUserId() {
		return get("user_id");
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