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
public abstract class BaseInventory<M extends BaseInventory<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_inventory";
	public static final String METADATA_TYPE = "cc_inventory";

	public static final String ACTION_ADD = "cc_inventory:add";
	public static final String ACTION_DELETE = "cc_inventory:delete";
	public static final String ACTION_UPDATE = "cc_inventory:update";

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
		if(!(o instanceof BaseInventory<?>)){return false;}

		BaseInventory<?> m = (BaseInventory<?>) o;
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

	public void setWarehouseId(java.lang.String warehouseId) {
		set("warehouse_id", warehouseId);
	}

	public java.lang.String getWarehouseId() {
		return get("warehouse_id");
	}

	public void setProductId(java.lang.String productId) {
		set("product_id", productId);
	}

	public java.lang.String getProductId() {
		return get("product_id");
	}

	public void setSellerId(java.lang.String sellerId) {
		set("seller_id", sellerId);
	}

	public java.lang.String getSellerId() {
		return get("seller_id");
	}
	
	public void setInCount(java.math.BigDecimal inCount) {
		set("in_count", inCount);
	}

	public java.math.BigDecimal getInCount() {
		return get("in_count");
	}

	public void setInAmount(java.math.BigDecimal inAmount) {
		set("in_amount", inAmount);
	}

	public java.math.BigDecimal getInAmount() {
		return get("in_amount");
	}

	public void setInPrice(java.math.BigDecimal inPrice) {
		set("in_price", inPrice);
	}

	public java.math.BigDecimal getInPrice() {
		return get("in_price");
	}

	public void setOutCount(java.math.BigDecimal outCount) {
		set("out_count", outCount);
	}

	public java.math.BigDecimal getOutCount() {
		return get("out_count");
	}

	public void setOutAmount(java.math.BigDecimal outAmount) {
		set("out_amount", outAmount);
	}

	public java.math.BigDecimal getOutAmount() {
		return get("out_amount");
	}

	public void setOutPrice(java.math.BigDecimal outPrice) {
		set("out_price", outPrice);
	}

	public java.math.BigDecimal getOutPrice() {
		return get("out_price");
	}

	public void setBalanceCount(java.math.BigDecimal balanceCount) {
		set("balance_count", balanceCount);
	}

	public java.math.BigDecimal getBalanceCount() {
		return get("balance_count");
	}

	public void setBalanceAmount(java.math.BigDecimal balanceAmount) {
		set("balance_amount", balanceAmount);
	}

	public java.math.BigDecimal getBalanceAmount() {
		return get("balance_amount");
	}

	public void setBalancePrice(java.math.BigDecimal balancePrice) {
		set("balance_price", balancePrice);
	}

	public java.math.BigDecimal getBalancePrice() {
		return get("balance_price");
	}

	public void setAfloatCount(java.math.BigDecimal afloatCount) {
		set("afloat_count", afloatCount);
	}

	public java.math.BigDecimal getAfloatCount() {
		return get("afloat_count");
	}

	public void setAfloatAmount(java.math.BigDecimal afloatAmount) {
		set("afloat_amount", afloatAmount);
	}

	public java.math.BigDecimal getAfloatAmount() {
		return get("afloat_amount");
	}

	public void setAfloatPrice(java.math.BigDecimal afloatPrice) {
		set("afloat_price", afloatPrice);
	}

	public java.math.BigDecimal getAfloatPrice() {
		return get("afloat_price");
	}

	public void setDataArea(java.lang.String dataArea) {
		set("data_area", dataArea);
	}

	public java.lang.String getDataArea() {
		return get("data_area");
	}

	public void setDeptId(java.lang.String deptId) {
		set("dept_id", deptId);
	}

	public java.lang.String getDeptId() {
		return get("dept_id");
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
