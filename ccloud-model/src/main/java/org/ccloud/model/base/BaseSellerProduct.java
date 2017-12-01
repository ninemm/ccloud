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
public abstract class BaseSellerProduct<M extends BaseSellerProduct<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_seller_goods";
	public static final String METADATA_TYPE = "cc_seller_goods";

	public static final String ACTION_ADD = "cc_seller_goods:add";
	public static final String ACTION_DELETE = "cc_seller_goods:delete";
	public static final String ACTION_UPDATE = "cc_seller_goods:update";

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
		if(!(o instanceof BaseSellerProduct<?>)){return false;}

		BaseSellerProduct<?> m = (BaseSellerProduct<?>) o;
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

	public void setCustomName(java.lang.String customName) {
		set("custom_name", customName);
	}

	public java.lang.String getCustomName() {
		return get("custom_name");
	}

	public void setStoreCount(java.math.BigDecimal storeCount) {
		set("store_count", storeCount);
	}

	public java.math.BigDecimal getStoreCount() {
		return get("store_count");
	}

	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}

	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public void setCost(java.math.BigDecimal cost) {
		set("cost", cost);
	}

	public java.math.BigDecimal getCost() {
		return get("cost");
	}

	public void setMarketPrice(java.math.BigDecimal marketPrice) {
		set("market_price", marketPrice);
	}

	public java.math.BigDecimal getMarketPrice() {
		return get("market_price");
	}

	public void setWeight(java.math.BigDecimal weight) {
		set("weight", weight);
	}

	public java.math.BigDecimal getWeight() {
		return get("weight");
	}

	public void setWeightUnit(java.lang.String weightUnit) {
		set("weight_unit", weightUnit);
	}

	public java.lang.String getWeightUnit() {
		return get("weight_unit");
	}

	public void setIsEnable(java.lang.Integer isEnable) {
		set("is_enable", isEnable);
	}

	public java.lang.Integer getIsEnable() {
		return get("is_enable");
	}

	public void setFreezeStore(java.math.BigDecimal freezeStore) {
		set("freeze_store", freezeStore);
	}

	public java.math.BigDecimal getFreezeStore() {
		return get("freeze_store");
	}

	public void setOrderList(java.lang.Integer orderList) {
		set("order_list", orderList);
	}

	public java.lang.Integer getOrderList() {
		return get("order_list");
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