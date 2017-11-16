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

import org.ccloud.model.SellerGoods;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerGoodsQuery extends JBaseQuery { 

	protected static final SellerGoods DAO = new SellerGoods();
	private static final SellerGoodsQuery QUERY = new SellerGoodsQuery();

	public static SellerGoodsQuery me() {
		return QUERY;
	}

	public SellerGoods findById(final String id) {
				return DAO.findById(id);
	}
	public SellerGoods findByProductId(String productId){
		String sql = "select * from cc_seller_goods where product_id=?";
		return DAO.findFirst(sql.toString(), productId);
	}

	public Page<SellerGoods> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_goods` ");

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

	public Page<SellerGoods> paginate_sel(int pageNumber, int pageSize,String keyword,String sellerId) {
		String select = "SELECT cs.id,cs.product_id, cs.custom_name,cs.store_count,cs.price,cs.cost,cs.market_price,cs.is_enable, cs.order_list ,GROUP_CONCAT(cgs.`name`) AS cps_name ";
		StringBuilder fromBuilder = new StringBuilder("from cc_seller_goods cs LEFT JOIN cc_product cp ON  cs.product_id = cp.id LEFT JOIN cc_product_goods_specification_value cpg ON  cp.id = cpg.product_set_id LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id");
		LinkedList<Object> params = new LinkedList<Object>();
		if(!keyword.equals("")){
			appendIfNotEmptyWithLike(fromBuilder, "cs.custom_name", keyword, params, true);
			fromBuilder.append(" and cs.seller_id='"+sellerId+"' ");
		}else{
			fromBuilder.append(" where cs.seller_id='"+sellerId+"' ");
		}
		fromBuilder.append(" GROUP BY cp.id ORDER BY cs.is_enable desc,cs.order_list ");
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
}
