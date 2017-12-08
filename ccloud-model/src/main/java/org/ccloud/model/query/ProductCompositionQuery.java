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

import org.ccloud.model.ProductComposition;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ProductCompositionQuery extends JBaseQuery { 

	protected static final ProductComposition DAO = new ProductComposition();
	private static final ProductCompositionQuery QUERY = new ProductCompositionQuery();

	public static ProductCompositionQuery me() {
		return QUERY;
	}

	public ProductComposition findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<ProductComposition> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "SELECT cp.name,cp.price,cp.id,cp.seller_product_id,cs.custom_name,t1.valueName,cs.product_id,count(cp.sub_seller_product_id) as type_count,cp.parent_id ";
		StringBuilder fromBuilder = new StringBuilder("FROM cc_product_composition cp ");

		LinkedList<Object> params = new LinkedList<Object>();
		fromBuilder.append("LEFT JOIN cc_seller_product cs ON cs.id = cp.seller_product_id ");
		fromBuilder.append("LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = cs.product_id ");
		appendIfNotEmptyWithLike(fromBuilder, "cs.custom_name", keyword, params, true);
		fromBuilder.append(" GROUP BY parent_id");

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				deleteCount = deleteCount + deleteByParentId(ids[i]);
			}
			return deleteCount;
		}
		return 0;
	}

	public List<ProductComposition> findByProductId(String productId) {
		return DAO.doFind("seller_product_id = ?", productId);
	}
	
	public List<ProductComposition> findByParentId(String parentId) {
		return DAO.doFind("parent_id = ?", parentId);
	}	

	public int batchDelete(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.size(); i++) {
				if (DAO.deleteById(ids.get(i))) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public List<ProductComposition> findDetailByProductId(String id) {
 		StringBuilder fromBuilder = new StringBuilder("SELECT cp.id,cp.name,cp.price,cp.seller_product_id,cp.sub_seller_product_id,cs.product_id as product_id,cg.product_id as sub_product_id, ");
		fromBuilder.append("t1.valueName as product_sp, t2.valueName as sub_product_sp, cs.price as price, cg.price as sub_price, ");
		fromBuilder.append("cs.custom_name as product_name,cg.custom_name as sub_product_name,cp.sub_product_count,cp.parent_id ");
		fromBuilder.append("from `cc_product_composition` cp ");
		fromBuilder.append("LEFT JOIN cc_seller_product cs ON cs.id = cp.seller_product_id ");
		fromBuilder.append("LEFT JOIN cc_seller_product cg ON cg.id = cp.sub_seller_product_id ");
		fromBuilder.append("LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = cs.product_id ");
		fromBuilder.append("LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t2 on t2.product_set_id = cg.product_id ");
		fromBuilder.append("WHERE cp.parent_id = ? ");
		return DAO.find(fromBuilder.toString(), id);
	}

	public int deleteByProId(String id) {
		return DAO.doDelete("seller_product_id = ?", id);
	}

	public int deleteByParentId(String id) {
		return DAO.doDelete("parent_id = ? ", id);
	}

	public List<Record> findProductBySeller(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT sp.parent_id as id, sp.name, sp.price ");
		fromBuilder.append("FROM cc_product_composition sp ");
		fromBuilder.append("LEFT JOIN cc_seller_product cs ON cs.id = sp.seller_product_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "cs.seller_id", sellerId, params, false);

		fromBuilder.append(" GROUP BY sp.parent_id");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	
}
