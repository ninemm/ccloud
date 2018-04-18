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

import org.ccloud.model.Goods;
import org.ccloud.model.Product;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GoodsQuery extends JBaseQuery { 

	protected static final Goods DAO = new Goods();
	private static final GoodsQuery QUERY = new GoodsQuery();

	public static GoodsQuery me() {
		return QUERY;
	}

	public Goods findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				StringBuilder sqlBuilder = new StringBuilder("select cc.*, ca.name as parent_name ");
				sqlBuilder.append("from `cc_goods` cc ");
				sqlBuilder.append("join `cc_goods_category` ca on cc.goods_category_id = ca.id ");
				sqlBuilder.append("where cc.id = ?");
				return DAO.findFirst(sqlBuilder.toString(), id);
			}
		});		
	}

	public Page<Goods> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select cc.*, cb.name as brand_name, cgc.name as category_name, cgt.name as type_name ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_goods` cc ");
		fromBuilder.append("join `cc_brand` cb on cc.brand_id = cb.id ");
		fromBuilder.append("join `cc_goods_category` cgc on cc.goods_category_id = cgc.id ");
		fromBuilder.append("join `cc_goods_type` cgt on cc.goods_type_id = cgt.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "cc.name", keyword, params, true);
		
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

	public Goods findDetail(String id) {
		StringBuilder sqlBuilder = new StringBuilder("select cc.*, cb.name as brand_name, cgc.name as category_name, cgt.name as type_name");
		sqlBuilder.append(" from `cc_goods` cc join `cc_brand` cb on cc.brand_id = cb.id");
		sqlBuilder.append(" join `cc_goods_category` cgc on cc.goods_category_id = cgc.id");
		sqlBuilder.append(" join `cc_goods_type` cgt on cc.goods_type_id = cgt.id");
		sqlBuilder.append(" where cc.id = ?");		
		return DAO.findFirst(sqlBuilder.toString(), id);
	}

	public int deleteAbout(Goods goods) {
		int deleteCount = 0; 
		if (goods.delete()) {
			deleteCount++;
		}
		deleteCount = deleteCount + GoodsGoodsSpecificationQuery.me().deleteByGoodsId(goods.getId());
		List<Product> proList = ProductQuery.me().findByGoodId(goods.getId());
		for (Product ccProduct : proList) {
			deleteCount = deleteCount + ProductGoodsSpecificationValueQuery.me().deleteByPId(ccProduct.getId());
			if (ccProduct.delete()) {
				deleteCount++;
			}
		}
		deleteCount = deleteCount + GoodsGoodsAttributeMapStoreQuery.me().deleteAllByGoodsId(goods.getId());
		return deleteCount;
	}

	public List<Goods> findByType(String id) {
		return DAO.doFind("goods_type_id = ?", id);
	}

	public List<Goods> findByCategory(String id) {
		return DAO.doFind("goods_category_id = ?", id);
	}

	public List<Goods> findByBrand(String id) {
		return DAO.doFind("brand_id = ?", id);
	}

	
}