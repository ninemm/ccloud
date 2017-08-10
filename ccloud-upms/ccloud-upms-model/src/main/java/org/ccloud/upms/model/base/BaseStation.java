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
package org.ccloud.upms.model.base;

import com.jfinal.kit.StrKit;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Station;
import org.ccloud.model.core.JModel;
import java.util.Date;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import org.ccloud.model.query.StationQuery;

/**
 *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStation<M extends BaseStation<M>> extends JModel<M> implements IBean {

	public static final String CACHE_NAME = "station";
	public static final String METADATA_TYPE = "station";

	public static final String ACTION_ADD = "station:add";
	public static final String ACTION_DELETE = "station:delete";
	public static final String ACTION_UPDATE = "station:update";

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
		if(!(o instanceof BaseStation<?>)){return false;}

		BaseStation<?> m = (BaseStation<?>) o;
		if(m.getId() == null){return false;}

		return m.getId().compareTo(this.getId()) == 0;
	}

	@Override
	public boolean save() {
		setId(StrKit.getRandomUUID());
		setCreateDate(new Date());
		boolean saved = super.save();
		if (saved) { MessageKit.sendMessage(ACTION_ADD, this); }
		return saved;
	}

	@Override
	public boolean delete() {
		setId(StrKit.getRandomUUID());
		boolean deleted = super.delete();
		if (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }
		return deleted;
	}

	@Override
	public boolean deleteById(Object idValue) {
		Station station = StationQuery.me().findById(idValue.toString());
		boolean deleted = super.deleteById(idValue);
		if (deleted) { MessageKit.sendMessage(ACTION_DELETE, station); }
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

	public void setStationName(String stationName) {
		set("station_name", stationName);
	}

	public String getStationName() {
		return get("station_name");
	}

	public void setParentId(String parentId) {
		set("parent_id", parentId);
	}

	public String getParentId() {
		return get("parent_id");
	}

	public void setIsParent(Integer isParent) {
		set("is_parent", isParent);
	}

	public Integer getIsParent() {
		return get("is_parent");
	}

	public void setOrderList(Integer orderList) {
		set("order_list", orderList);
	}

	public Integer getOrderList() {
		return get("order_list");
	}

	public void setDescription(String description) {
		set("description", description);
	}

	public String getDescription() {
		return get("description");
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
