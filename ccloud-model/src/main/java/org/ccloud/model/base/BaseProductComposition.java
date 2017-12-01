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
public abstract class BaseProductComposition<M extends BaseProductComposition<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_product_composition";
	public static final String METADATA_TYPE = "cc_product_composition";

	public static final String ACTION_ADD = "cc_product_composition:add";
	public static final String ACTION_DELETE = "cc_product_composition:delete";
	public static final String ACTION_UPDATE = "cc_product_composition:update";

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
		if(!(o instanceof BaseProductComposition<?>)){return false;}

		BaseProductComposition<?> m = (BaseProductComposition<?>) o;
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

	public void setSellerProductId(java.lang.String sellerProductId) {
		set("seller_product_id", sellerProductId);
	}

	public java.lang.String getSellerProductId() {
		return getStr("seller_product_id");
	}

	public void setSubSellerProductId(java.lang.String subSellerProductId) {
		set("sub_seller_product_id", subSellerProductId);
	}

	public java.lang.String getSubSellerProductId() {
		return getStr("sub_seller_product_id");
	}

	public void setSubProductCount(java.lang.String subProductCount) {
		set("sub_product_count", subProductCount);
	}

	public java.lang.String getSubProductCount() {
		return getStr("sub_product_count");
	}

	public void setParentId(java.lang.String parentId) {
		set("parent_id", parentId);
	}

	public java.lang.String getParentId() {
		return getStr("parent_id");
	}
	
	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return getStr("name");
	}	
	
	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}

	public java.math.BigDecimal getPrice() {
		return get("price");
	}
}