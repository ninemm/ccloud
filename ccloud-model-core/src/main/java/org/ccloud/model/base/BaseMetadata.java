/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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

import org.ccloud.cache.CacheManager;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Metadata;
import org.ccloud.model.core.JModel;
import org.ccloud.model.query.MetaDataQuery;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 *  Auto generated by ccloud, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseMetadata<M extends BaseMetadata<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "metadata";
	public static final String METADATA_TYPE = "metadata";

	public static final String ACTION_ADD = "metadata:add";
	public static final String ACTION_DELETE = "metadata:delete";
	public static final String ACTION_UPDATE = "metadata:update";

	public void removeCache(Object key){
		if(key == null) return;
		CacheManager.me().getCache().remove(CACHE_NAME, key);
	}

	public void putCache(Object key,Object value){
		CacheManager.me().getCache().put(CACHE_NAME, key, value);
	}

	public M getCache(Object key){
		return CacheManager.me().getCache().get(CACHE_NAME, key);
	}

	public M getCache(Object key,IDataLoader dataloader){
		return CacheManager.me().getCache().get(CACHE_NAME, key, dataloader);
	}

	public Metadata createMetadata(){
		Metadata md = new Metadata();
		md.setObjectId(getId());
		md.setObjectType(METADATA_TYPE);
		return md;
	}

	public Metadata createMetadata(String key,String value){
		Metadata md = new Metadata();
		md.setObjectId(getId());
		md.setObjectType(METADATA_TYPE);
		md.setMetaKey(key);
		md.setMetaValue(value);
		return md;
	}

	public boolean saveOrUpdateMetadta(String key,String value){
		Metadata metadata = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);
		if (metadata == null) {
			metadata = createMetadata(key, value);
			return metadata.save();
		}
		metadata.setMetaValue(value);
		return metadata.update();
	}

	public String metadata(String key) {
		Metadata m = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);
		if (m != null) {
			return m.getMetaValue();
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if(o == null){ return false; }
		if(!(o instanceof BaseMetadata<?>)){return false;}

		BaseMetadata<?> m = (BaseMetadata<?>) o;
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
		return get("id");
	}

	public void setMetaKey(java.lang.String metaKey) {
		set("meta_key", metaKey);
	}

	public java.lang.String getMetaKey() {
		return get("meta_key");
	}

	public void setMetaValue(java.lang.String metaValue) {
		set("meta_value", metaValue);
	}

	public java.lang.String getMetaValue() {
		return get("meta_value");
	}

	public void setObjectType(java.lang.String objectType) {
		set("object_type", objectType);
	}

	public java.lang.String getObjectType() {
		return get("object_type");
	}

	public void setObjectId(String objectId) {
		set("object_id", objectId);
	}

	public String getObjectId() {
		return get("object_id");
	}

}
