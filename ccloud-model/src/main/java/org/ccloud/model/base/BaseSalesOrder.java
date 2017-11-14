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
public abstract class BaseSalesOrder<M extends BaseSalesOrder<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_sales_order";
	public static final String METADATA_TYPE = "cc_sales_order";

	public static final String ACTION_ADD = "cc_sales_order:add";
	public static final String ACTION_DELETE = "cc_sales_order:delete";
	public static final String ACTION_UPDATE = "cc_sales_order:update";

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
		if(!(o instanceof BaseSalesOrder<?>)){return false;}

		BaseSalesOrder<?> m = (BaseSalesOrder<?>) o;
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

	public void setOrderSn(java.lang.String orderSn) {
		set("order_sn", orderSn);
	}

	public java.lang.String getOrderSn() {
		return getStr("order_sn");
	}

	public void setWarehouseId(java.lang.String warehouseId) {
		set("warehouse_id", warehouseId);
	}

	public java.lang.String getWarehouseId() {
		return getStr("warehouse_id");
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

	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	public java.lang.String getAddress() {
		return getStr("address");
	}

	public void setBizUserId(java.lang.String bizUserId) {
		set("biz_user_id", bizUserId);
	}

	public java.lang.String getBizUserId() {
		return getStr("biz_user_id");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public void setIsGift(java.lang.Integer isGift) {
		set("is_gift", isGift);
	}

	public java.lang.Integer getIsGift() {
		return getInt("is_gift");
	}

	public void setTotalAmount(java.math.BigDecimal totalAmount) {
		set("total_amount", totalAmount);
	}

	public java.math.BigDecimal getTotalAmount() {
		return get("total_amount");
	}

	public void setTotalTax(java.math.BigDecimal totalTax) {
		set("total_tax", totalTax);
	}

	public java.math.BigDecimal getTotalTax() {
		return get("total_tax");
	}

	public void setPriceWithTax(java.math.BigDecimal priceWithTax) {
		set("price_with_tax", priceWithTax);
	}

	public java.math.BigDecimal getPriceWithTax() {
		return get("price_with_tax");
	}

	public void setReceiveType(java.lang.Integer receiveType) {
		set("receive_type", receiveType);
	}

	public java.lang.Integer getReceiveType() {
		return getInt("receive_type");
	}

	public void setDeliveryAddress(java.lang.String deliveryAddress) {
		set("delivery_address", deliveryAddress);
	}

	public java.lang.String getDeliveryAddress() {
		return getStr("delivery_address");
	}

	public void setDeliveryDate(java.util.Date deliveryDate) {
		set("delivery_date", deliveryDate);
	}

	public java.util.Date getDeliveryDate() {
		return get("delivery_date");
	}

	public void setConfirmUserId(java.lang.String confirmUserId) {
		set("confirm_user_id", confirmUserId);
	}

	public java.lang.String getConfirmUserId() {
		return getStr("confirm_user_id");
	}

	public void setConfirmDate(java.util.Date confirmDate) {
		set("confirm_date", confirmDate);
	}

	public java.util.Date getConfirmDate() {
		return get("confirm_date");
	}

	public void setCouponCode(java.lang.String couponCode) {
		set("coupon_code", couponCode);
	}

	public java.lang.String getCouponCode() {
		return getStr("coupon_code");
	}

	public void setCouponReducePrice(java.math.BigDecimal couponReducePrice) {
		set("coupon_reduce_price", couponReducePrice);
	}

	public java.math.BigDecimal getCouponReducePrice() {
		return get("coupon_reduce_price");
	}

	public void setPrintTime(java.util.Date printTime) {
		set("print_time", printTime);
	}

	public java.util.Date getPrintTime() {
		return get("print_time");
	}

	public void setProcKey(java.lang.String procKey) {
		set("proc_key", procKey);
	}

	public java.lang.String getProcKey() {
		return getStr("proc_key");
	}

	public void setProcInstId(java.lang.String procInstId) {
		set("proc_inst_id", procInstId);
	}

	public java.lang.String getProcInstId() {
		return getStr("proc_inst_id");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	public java.lang.String getRemark() {
		return getStr("remark");
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

}
