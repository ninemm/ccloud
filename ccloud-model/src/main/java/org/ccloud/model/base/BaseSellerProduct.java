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
public abstract class BaseSellerProduct<M extends BaseSellerProduct<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_seller_product";
	public static final String METADATA_TYPE = "cc_seller_product";

	public static final String ACTION_ADD = "cc_seller_product:add";
	public static final String ACTION_DELETE = "cc_seller_product:delete";
	public static final String ACTION_UPDATE = "cc_seller_product:update";

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
		return getStr("id");
	}

	public void setProductId(java.lang.String productId) {
		set("product_id", productId);
	}

	public java.lang.String getProductId() {
		return getStr("product_id");
	}

	public void setSellerId(java.lang.String sellerId) {
		set("seller_id", sellerId);
	}

	public java.lang.String getSellerId() {
		return getStr("seller_id");
	}

	public void setCustomName(java.lang.String customName) {
		set("custom_name", customName);
	}

	public java.lang.String getCustomName() {
		return getStr("custom_name");
	}

	public void setStoreCount(java.math.BigDecimal storeCount) {
		set("store_count", storeCount);
	}

	public java.math.BigDecimal getStoreCount() {
		return get("store_count");
	}

	public void setTaxPrice(java.math.BigDecimal taxPrice) {
		set("tax_price", taxPrice);
	}

	public java.math.BigDecimal getTaxPrice() {
		return get("tax_price");
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

	public void setWeightUnit(java.lang.String weightUnit) {
		set("weight_unit", weightUnit);
	}

	public java.lang.String getWeightUnit() {
		return getStr("weight_unit");
	}
	
	public void setWeight(java.math.BigDecimal weight) {
		set("weight", weight);
	}

	public java.math.BigDecimal getWeight() {
		return get("weight");
	}

	public void setTags(java.lang.String tags) {
		set("tags", tags);
	}

	public java.lang.String getTags() {
		return getStr("tags");
	}

	public void setWarehouseId(java.lang.String warehouseId) {
		set("warehouse_id", warehouseId);
	}

	public java.lang.String getWarehouseId() {
		return getStr("warehouse_id");
	}

	public void setIsSource(java.lang.Integer isSource) {
		set("is_source", isSource);
	}

	public java.lang.Integer getIsSource() {
		return getInt("is_source");
	}

	public void setIsEnable(java.lang.Integer isEnable) {
		set("is_enable", isEnable);
	}

	public java.lang.Integer getIsEnable() {
		return getInt("is_enable");
	}

	public void setIsGift(java.lang.Integer isGift) {
		set("is_gift", isGift);
	}

	public java.lang.Integer getIsGift() {
		return getInt("is_gift");
	}

	public void setFreezeStore(java.math.BigDecimal freezeStore) {
		set("freeze_store", freezeStore);
	}

	public java.math.BigDecimal getFreezeStore() {
		return get("freeze_store");
	}

	public void setBarCode(java.lang.String barCode) {
		set("bar_code", barCode);
	}

	public java.lang.String getBarCode() {
		return getStr("bar_code");
	}

	public void setQrcodeUrl(java.lang.String qrcodeUrl) {
		set("qrcode_url", qrcodeUrl);
	}

	public java.lang.String getQrcodeUrl() {
		return getStr("qrcode_url");
	}

	public void setOrderList(java.lang.Integer orderList) {
		set("order_list", orderList);
	}

	public java.lang.Integer getOrderList() {
		return getInt("order_list");
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
	
	public void setAccountPrice(java.math.BigDecimal accountPrice) {
		set("account_price", accountPrice);
	}

	public java.math.BigDecimal getAccountPrice() {
		return get("account_price");
	}
	
	public void setIisHot(java.lang.Integer isHot) {
		set("is_hot", isHot);
	}

	public java.lang.Integer getIisHot() {
		return getInt("is_hot");
	}
}
