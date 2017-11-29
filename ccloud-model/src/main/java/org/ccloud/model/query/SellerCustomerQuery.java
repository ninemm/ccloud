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
import org.ccloud.model.SellerCustomer;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerCustomerQuery extends JBaseQuery { 

	protected static final SellerCustomer DAO = new SellerCustomer();
	private static final SellerCustomerQuery QUERY = new SellerCustomerQuery();

	public static SellerCustomerQuery me() {
		return QUERY;
	}

	public SellerCustomer findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public Page<SellerCustomer> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select sc.*, c.customer_code, c.customer_name"
				+ ", c.contact, c.mobile, c.prov_name, c.city_name, c.county_name"
				+ ", c.prov_code, c.city_code, c.country_code, c.address";
		
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_customer` sc ");
		fromBuilder.append("join `cc_customer` c on c.id = sc.customer_id");
		
		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
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
	
	public void findUserListAsTree() {
		
	}

	
}
