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
package org.ccloud.model.query;

import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.CustomerStore;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerStoreQuery extends JBaseQuery {

	protected static final CustomerStore DAO = new CustomerStore();
	private static final CustomerStoreQuery QUERY = new CustomerStoreQuery();

	public static CustomerStoreQuery me() {
		return QUERY;
	}

	public CustomerStore findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword) {

		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_customer_store` c ");

		LinkedList<Object> params = new LinkedList<Object>();

		fromBuilder.append(" order by c.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public CustomerStore findByUid(String uid) {
		return DAO.findFirst("select * from cc_customer_store where uid = ?", uid);
	}
	
	public boolean enable(String id, int isEnabled) {
		CustomerStore customerStore = DAO.findById(id);
		customerStore.set("is_enabled", isEnabled);

		return customerStore.saveOrUpdate();
	}

	public List<CustomerStore> findCountryIsNull() {
		return DAO.find("select * from cc_customer_store where country_name is null");
	}
}
