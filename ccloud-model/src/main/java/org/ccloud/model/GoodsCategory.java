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
package org.ccloud.model;

import org.ccloud.model.core.Table;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ccloud.model.ModelSorter.ISortModel;
import org.ccloud.model.base.BaseGoodsCategory;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="cc_goods_category",primaryKey="id")
public class GoodsCategory extends BaseGoodsCategory<GoodsCategory> implements ISortModel<GoodsCategory>{

	private static final long serialVersionUID = 1L;

    private int layer = 0;
    private GoodsCategory parent;
    private List<GoodsCategory> childList;

    @Override
    public boolean save() {
        removeCache(getId());

        clearList();

        return super.save();
    }

    @Override
    public boolean update() {
        removeCache(getId());

        clearList();

        return super.update();
    }

    @Override
    public boolean delete() {
        removeCache(getId());

        clearList();

        return super.delete();
    }

    @Override
    public boolean deleteById(Object idValue) {
        removeCache(getId());

        clearList();
        return super.deleteById(idValue);

    }

    @Override
    public boolean saveOrUpdate() {

        clearList();

        if (null == get(getPrimaryKey())) {
			set("id", StrKit.getRandomUUID());
			set("create_date", new Date());        	
            return this.save();
        }
        set("modify_date", new Date());
        return this.update();
    }

    public <T> T getFromListCache(Object key, IDataLoader dataloader) {
        Set<String> inCacheKeys = CacheKit.get(CACHE_NAME, "cachecategorykeys");

        Set<String> cacheKeyList = new HashSet<String>();
        if (inCacheKeys != null) {
            cacheKeyList.addAll(inCacheKeys);
        }

        cacheKeyList.add(key.toString());
        CacheKit.put(CACHE_NAME, "cachecategorykeys", cacheKeyList);

        return CacheKit.get("category_list", key, dataloader);
    }

    public void clearList() {
        Set<String> list = CacheKit.get(CACHE_NAME, "cachecategorykeys");
        if (list != null && list.size() > 0) {
            for (String key : list) {

                // 过滤

                CacheKit.remove("category_list", key);
            }
        }
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public String getLayerString() {
        String layerString = "";
        for (int i = 0; i < layer; i++) {
            layerString += "— ";
        }
        return layerString;
    }

    public void setParent(GoodsCategory parent) {
        this.parent = parent;
    }

    public GoodsCategory getParent() {
        return parent;
    }


    public void addChild(GoodsCategory child) {

        if(null == childList)
            childList = new ArrayList<>();

        //如果是从ehcache内存取到的数据，可能该model已经添加过了
        if(!childList.contains(child)){
            childList.add(child);
        }
    }

    public List<GoodsCategory> getChildList() {
        return this.childList;
    }

    public void setChildList(List<GoodsCategory> childList) {
        this.childList = childList;
    }
}
