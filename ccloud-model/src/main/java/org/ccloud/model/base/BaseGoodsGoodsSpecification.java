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
public abstract class BaseGoodsGoodsSpecification<M extends BaseGoodsGoodsSpecification<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "cc_goods_goods_specification";
	public static final String METADATA_TYPE = "cc_goods_goods_specification";

	public static final String ACTION_ADD = "cc_goods_goods_specification:add";
	public static final String ACTION_DELETE = "cc_goods_goods_specification:delete";
	public static final String ACTION_UPDATE = "cc_goods_goods_specification:update";

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
		if(!(o instanceof BaseGoodsGoodsSpecification<?>)){return false;}

		BaseGoodsGoodsSpecification<?> m = (BaseGoodsGoodsSpecification<?>) o;
		if(m.getPrimaryKey() == null){return false;}

		return m.getPrimaryKey().compareTo(this.getPrimaryKey()) == 0;
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

	public void setGoodsId(java.lang.String goodsId) {
		set("goods_id", goodsId);
	}

	public java.lang.String getGoodsId() {
		return get("goods_id");
	}

	public void setGoodsSpecificationId(java.lang.String goodsSpecificationId) {
		set("goods_specification_id", goodsSpecificationId);
	}

	public java.lang.String getGoodsSpecificationId() {
		return get("goods_specification_id");
	}

}
