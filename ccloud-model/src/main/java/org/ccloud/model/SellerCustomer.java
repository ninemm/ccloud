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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ccloud.model.base.BaseSellerCustomer;
import org.ccloud.model.core.Table;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.vo.ImageJson;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="cc_seller_customer",primaryKey="id")
public class SellerCustomer extends BaseSellerCustomer<SellerCustomer> {

	private static final long serialVersionUID = 1L;
	
	public static final String CUSTOMER_NORMAL = "100101";
	public static final String CUSTOMER_AUDIT = "100102";
	public static final String CUSTOMER_REJECT = "100103";

	public Customer getCustomer() {
		return CustomerQuery.me().findById(getCustomerId());
	}
	
	public List<ImageJson> getImageList() {
		
		String imageListStore = getImageListStore();

		if(StrKit.notBlank(imageListStore)) {
			//Boolean isEnable = OptionQuery.me().findValueAsBool("cdn_enable");

			//if (isEnable != null && isEnable) {
			String domain = OptionQuery.me().findValue("cdn_domain");
			List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
			for (ImageJson image : list) {
				image.setSavePath(domain + "/" + image.getSavePath());
				image.setOriginalPath(domain + "/" +image.getOriginalPath());
			}
			return list;
		} else return new ArrayList<>();
//		} else {
//			if (StrKit.notBlank(imageListStore)) {
//				List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
//				return list;
//			}
//		}
		//return null;
	}

	@Override
	public boolean saveOrUpdate() {
		if (null == get(getPrimaryKey())) {
			set("id", StrKit.getRandomUUID());
			set("create_date", new Date());
			removeCache(get("id"));
			return this.save();
		}
		removeCache(get(getPrimaryKey()));
		set("modify_date", new Date());
		return this.update();
	}

	@Override
	public boolean update() {

		removeCache(getId());

		return super.update();
	}

	@Override
	public boolean delete() {

		removeCache(getId());

		return super.delete();
	}

	@Override
	public boolean deleteById(Object idValue) {


		removeCache(idValue);

		return super.deleteById(idValue);
	}


}
