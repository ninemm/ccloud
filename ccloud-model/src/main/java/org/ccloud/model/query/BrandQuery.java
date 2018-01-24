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

import org.ccloud.model.Brand;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class BrandQuery extends JBaseQuery { 

	protected static final Brand DAO = new Brand();
	private static final BrandQuery QUERY = new BrandQuery();

	public static BrandQuery me() {
		return QUERY;
	}

	public Brand findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});		
	}

	public Page<Brand> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select cb.*, cs.name as supplier_name";
		StringBuilder fromBuilder = new StringBuilder("from `cc_brand` cb ");
		fromBuilder.append("join `cc_supplier` cs on cb.supplier_id = cs.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "cb.name", keyword, params, true);
		
		fromBuilder.append("order by " + orderby);

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

	public List<Brand> findBySupplierId(String id) {
		return DAO.doFind("supplier_id = ?", id);
	}

	public List<Brand> findAll() {
		return DAO.doFind();
	}
	
	public List<Brand> findBySellerId(String sellerId){
		String sql = "select b.* from cc_brand b LEFT JOIN cc_seller_brand s on s.brand_id=b.id where s.seller_id = ?";
		return DAO.find(sql, sellerId);
	}

}
