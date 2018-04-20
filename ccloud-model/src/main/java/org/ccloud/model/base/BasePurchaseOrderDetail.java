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
public abstract class BasePurchaseOrderDetail<M extends BasePurchaseOrderDetail<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_purchase_order_detail";
	public static final String METADATA_TYPE = "cc_purchase_order_detail";

	public static final String ACTION_ADD = "cc_purchase_order_detail:add";
	public static final String ACTION_DELETE = "cc_purchase_order_detail:delete";
	public static final String ACTION_UPDATE = "cc_purchase_order_detail:update";

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
		if(!(o instanceof BasePurchaseOrderDetail<?>)){return false;}

		BasePurchaseOrderDetail<?> m = (BasePurchaseOrderDetail<?>) o;
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

	public void setPurchaseOrderId(java.lang.String purchaseOrderId) {
		set("purchase_order_id", purchaseOrderId);
	}

	public java.lang.String getPurchaseOrderId() {
		return getStr("purchase_order_id");
	}

	public void setProductId(java.lang.String productId) {
		set("product_id", productId);
	}

	public java.lang.String getProductId() {
		return getStr("product_id");
	}

	public void setProductCount(java.lang.Integer productCount) {
		set("product_count", productCount);
	}

	public java.lang.Integer getProductCount() {
		return getInt("product_count");
	}

	public void setProductAmount(java.math.BigDecimal productAmount) {
		set("product_amount", productAmount);
	}

	public java.math.BigDecimal getProductAmount() {
		return get("product_amount");
	}

	public void setProductPrice(java.math.BigDecimal productPrice) {
		set("product_price", productPrice);
	}

	public java.math.BigDecimal getProductPrice() {
		return get("product_price");
	}

	public void setTaxRate(java.math.BigDecimal taxRate) {
		set("tax_rate", taxRate);
	}

	public java.math.BigDecimal getTaxRate() {
		return get("tax_rate");
	}

	public void setTax(java.math.BigDecimal tax) {
		set("tax", tax);
	}

	public java.math.BigDecimal getTax() {
		return get("tax");
	}

	public void setMoneyWithTax(java.math.BigDecimal moneyWithTax) {
		set("money_with_tax", moneyWithTax);
	}

	public java.math.BigDecimal getMoneyWithTax() {
		return get("money_with_tax");
	}

	public void setInCount(java.lang.Integer inCount) {
		set("in_count", inCount);
	}

	public java.lang.Integer getInCount() {
		return getInt("in_count");
	}

	public void setLeftCount(java.lang.Integer leftCount) {
		set("left_count", leftCount);
	}

	public java.lang.Integer getLeftCount() {
		return getInt("left_count");
	}

	public void setOrderList(java.lang.Integer orderList) {
		set("order_list", orderList);
	}

	public java.lang.Integer getOrderList() {
		return getInt("order_list");
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
