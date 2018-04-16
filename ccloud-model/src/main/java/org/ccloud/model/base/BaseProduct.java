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

import java.util.List;

import org.ccloud.message.MessageKit;
import org.ccloud.model.GoodsSpecificationValue;
import org.ccloud.model.core.JModel;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
@Before(Tx.class)
public abstract class BaseProduct<M extends BaseProduct<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_product";
	public static final String METADATA_TYPE = "cc_product";

	public static final String ACTION_ADD = "cc_product:add";
	public static final String ACTION_DELETE = "cc_product:delete";
	public static final String ACTION_UPDATE = "cc_product:update";
	
	private List<GoodsSpecificationValue> goodsSpecificationValueSet;

	public List<GoodsSpecificationValue> getGoodsSpecificationValueSet() {
		return goodsSpecificationValueSet;
	}

	public void setGoodsSpecificationValueSet(List<GoodsSpecificationValue> goodsSpecificationValueSet) {
		this.goodsSpecificationValueSet = goodsSpecificationValueSet;
	}

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
		if(!(o instanceof BaseProduct<?>)){return false;}

		BaseProduct<?> m = (BaseProduct<?>) o;
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

	public void setCost(java.math.BigDecimal cost) {
		set("cost", cost);
	}

	public java.math.BigDecimal getCost() {
		return get("cost");
	}

	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	public java.lang.String getDescription() {
		return get("description");
	}

	public void setFreezeStore(java.lang.Integer freezeStore) {
		set("freeze_store", freezeStore);
	}

	public java.lang.Integer getFreezeStore() {
		return get("freeze_store");
	}

	public void setIsMarketable(java.lang.Boolean isMarketable) {
		set("is_marketable", isMarketable);
	}

	public java.lang.Boolean getIsMarketable() {
		return get("is_marketable");
	}

	public void setMarketPrice(java.math.BigDecimal marketPrice) {
		set("market_price", marketPrice);
	}

	public java.math.BigDecimal getMarketPrice() {
		return get("market_price");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}

	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	public void setProductSn(java.lang.String productSn) {
		set("product_sn", productSn);
	}

	public java.lang.String getProductSn() {
		return get("product_sn");
	}

	public void setStore(java.lang.Integer store) {
		set("store", store);
	}

	public java.lang.Integer getStore() {
		return get("store");
	}

	public void setStorePlace(java.lang.String storePlace) {
		set("store_place", storePlace);
	}

	public java.lang.String getStorePlace() {
		return get("store_place");
	}

	public void setWeight(java.lang.Double weight) {
		set("weight", weight);
	}

	public java.lang.Double getWeight() {
		return get("weight");
	}

	public void setWeightUnit(java.lang.Integer weightUnit) {
		set("weight_unit", weightUnit);
	}

	public java.lang.Integer getWeightUnit() {
		return get("weight_unit");
	}

	public void setGoodsId(java.lang.String goodsId) {
		set("goods_id", goodsId);
	}

	public java.lang.String getGoodsId() {
		return get("goods_id");
	}
	
	public void setBigUnit(java.lang.String bigUnit) {
		set("big_unit", bigUnit);
	}

	public java.lang.String getBigUnit() {
		return get("big_unit");
	}

	public void setSmallUnit(java.lang.String smallUnit) {
		set("small_unit", smallUnit);
	}

	public java.lang.String getSmallUnit() {
		return get("small_unit");
	}

	public void setConvertRelate(java.lang.Integer convertRelate) {
		set("convert_relate", convertRelate);
	}

	public java.lang.Integer getConvertRelate() {
		return get("convert_relate");
	}	

}