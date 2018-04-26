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
public abstract class BaseCustomerVisit<M extends BaseCustomerVisit<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "customer_visit";
	public static final String METADATA_TYPE = "customer_visit";

	public static final String ACTION_ADD = "cc_customer_visit:add";
	public static final String ACTION_DELETE = "cc_customer_visit:delete";
	public static final String ACTION_UPDATE = "cc_customer_visit:update";

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
		if(!(o instanceof BaseCustomerVisit<?>)){return false;}

		BaseCustomerVisit<?> m = (BaseCustomerVisit<?>) o;
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

	public void setUserId(java.lang.String userId) {
		set("user_id", userId);
	}

	public java.lang.String getUserId() {
		return getStr("user_id");
	}

	public void setSellerCustomerId(java.lang.String sellerCustomerId) {
		set("seller_customer_id", sellerCustomerId);
	}

	public java.lang.String getSellerCustomerId() {
		return getStr("seller_customer_id");
	}

	public void setQuestionType(java.lang.String questionType) {
		set("question_type", questionType);
	}

	public java.lang.String getQuestionType() {
		return getStr("question_type");
	}

	public void setQuestionDesc(java.lang.String questionDesc) {
		set("question_desc", questionDesc);
	}

	public java.lang.String getQuestionDesc() {
		return getStr("question_desc");
	}

	public void setAdvice(java.lang.String advice) {
		set("advice", advice);
	}

	public java.lang.String getAdvice() {
		return getStr("advice");
	}

	public void setPhoto(java.lang.String photo) {
		set("photo", photo);
	}

	public java.lang.String getPhoto() {
		return getStr("photo");
	}

	public void setVedio(java.lang.String vedio) {
		set("vedio", vedio);
	}

	public java.lang.String getVedio() {
		return getStr("vedio");
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
	
	public void setReviewLng(java.math.BigDecimal lng) {
		set("review_lng", lng);
	}

	public java.math.BigDecimal getReviewLng() {
		return get("review_lng");
	}

	public void setReviewLat(java.math.BigDecimal lat) {
		set("review_lat", lat);
	}

	public java.math.BigDecimal getReviewLat() {
		return get("review_lat");
	}

	public void setLocation(java.lang.String location) {
		set("location", location);
	}

	public java.lang.String getLocation() {
		return getStr("location");
	}

	public void setReviewId(java.lang.String reviewId) {
		set("review_id", reviewId);
	}

	public java.lang.String getReviewId() {
		return getStr("review_id");
	}

	public void setSolution(java.lang.String solution) {
		set("solution", solution);
	}

	public java.lang.String getSolution() {
		return getStr("solution");
	}

	public void setComment(java.lang.String comment) {
		set("comment", comment);
	}

	public java.lang.String getComment() {
		return getStr("comment");
	}

	public void setReviewAddress(java.lang.String reviewAddress) {
		set("review_address", reviewAddress);
	}

	public java.lang.String getReviewAddress() {
		return getStr("review_address");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return getStr("status");
	}

	public void setReviewDate(java.util.Date reviewDate) {
		set("review_date", reviewDate);
	}

	public java.util.Date getReviewDate() {
		return get("review_date");
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

	public void setImageListStore(java.lang.String imageListStore) {
		set("image_list_store", imageListStore);
	}

	public java.lang.String getImageListStore() {
		return getStr("image_list_store");
	}
	
	public void setActiveApplyId(java.lang.String activityApplyId) {
		set("active_apply_id", activityApplyId);
	}

	public java.lang.String getActiveApplyId() {
		return getStr("active_apply_id");
	}
	
	public void setActivityExecuteId(java.lang.String activityExecuteId) {
		set("activity_execute_id", activityExecuteId);
	}

	public java.lang.String getActivityExecuteId() {
		return getStr("activity_execute_id");
	}
	
	public void setVisitUser(java.lang.String visitUser) {
		set("visit_user", visitUser);
	}

	public java.lang.String getVisitUser() {
		return getStr("visit_user");
	}
	
	public void setReviewUser(java.lang.String reviewUser) {
		set("review_user", reviewUser);
	}

	public java.lang.String getReviewUser() {
		return getStr("review_user");
	}
}
