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
public abstract class BaseActivity<M extends BaseActivity<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_activity";
	public static final String METADATA_TYPE = "cc_activity";

	public static final String ACTION_ADD = "cc_activity:add";
	public static final String ACTION_DELETE = "cc_activity:delete";
	public static final String ACTION_UPDATE = "cc_activity:update";

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
		if(!(o instanceof BaseActivity<?>)){return false;}

		BaseActivity<?> m = (BaseActivity<?>) o;
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

	public void setId(String id) {
		set("id", id);
	}

	public String getId() {
		return getStr("id");
	}
	public void setSellerId(String sellerId) {
		set("seller_id", sellerId);
	}

	public String getSellerId() {
		return getStr("seller_id");
	}

	public void setCode(String code) {
		set("code", code);
	}

	public String getCode() {
		return getStr("code");
	}

	public void setTitle(String title) {
		set("title", title);
	}

	public String getTitle() {
		return getStr("title");
	}

	public void setStartTime(java.util.Date startTime) {
		set("start_time", startTime);
	}

	public java.util.Date getStartTime() {
		return get("start_time");
	}

	public void setEndTime(java.util.Date endTime) {
		set("end_time", endTime);
	}

	public java.util.Date getEndTime() {
		return get("end_time");
	}

	public void setCategory(String category) {
		set("category", category);
	}

	public String getCategory() {
		return getStr("category");
	}

	public void setCustomerType(String customerType) {
		set("customer_type", customerType);
	}

	public String getCustomerType() {
		return getStr("customer_type");
	}

	public void setAreaType(String areaType) {
		set("area_type", areaType);
	}

	public String getAreaType() {
		return getStr("area_type");
	}

	public void setInvestType(String investType) {
		set("invest_type", investType);
	}

	public String getInvestType() {
		return getStr("invest_type");
	}

	public void setInvestAmount(java.math.BigDecimal investAmount) {
		set("invest_amount", investAmount);
	}

	public java.math.BigDecimal getInvestAmount() {
		return get("invest_amount");
	}

	public void setInvestNum(Integer investNum) {
		set("invest_num", investNum);
	}

	public Integer getInvestNum() {
		return getInt("invest_num");
	}

	public void setUnit(String unit) {
		set("unit", unit);
	}

	public String getUnit() {
		return getStr("unit");
	}

	public void setTags(String tags) {
		set("tags", tags);
	}

	public String getTags() {
		return getStr("tags");
	}

	public void setProcCode(String procCode) {
		set("proc_code", procCode);
	}

	public String getProcCode() {
		return getStr("proc_code");
	}

	public void setPlanCode(String planCode) {
		set("plan_code", planCode);
	}

	public String getPlanCode() {
		return getStr("plan_code");
	}

	public void setContent(String content) {
		set("content", content);
	}

	public String getContent() {
		return getStr("content");
	}

	public void setJoinNum(Integer joinNum) {
		set("join_num", joinNum);
	}

	public Integer getJoinNum() {
		return getInt("join_num");
	}

	public void setTimeInterval(String timeInterval) {
		set("time_interval", timeInterval);
	}

	public String getTimeInterval() {
		return getStr("time_interval");
	}
	
	public void setTotalCustomerNum(Integer totalCustomerNum) {
		set("total_customer_num", totalCustomerNum);
	}
	
	public Integer getTotalCustomerNum() {
		return getInt("total_customer_num");
	}

	
	public void setVisitNum(Integer visitNum) {
		set("visit_num", visitNum);
	}

	public Integer getVisitNum() {
		return getInt("visit_num");
	}

	public void setImageListStore(String imageListStore) {
		set("image_list_store", imageListStore);
	}

	public String getImageListStore() {
		return getStr("image_list_store");
	}

	public void setIsPublish(Integer isPublish) {
		set("is_publish", isPublish);
	}

	public Integer getIsPublish() {
		return getInt("is_publish");
	}
	
	public void setIsLimit(Integer isLimit) {
		set("is_limit", isLimit);
	}

	public Integer getIsLimit() {
		return getInt("is_limit");
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