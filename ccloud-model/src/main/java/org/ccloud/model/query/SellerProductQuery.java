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

import org.ccloud.model.SellerProduct;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerProductQuery extends JBaseQuery { 

	protected static final SellerProduct DAO = new SellerProduct();
	private static final SellerProductQuery QUERY = new SellerProductQuery();

	public static SellerProductQuery me() {
		return QUERY;
	}

	public SellerProduct findById(final String id) {
				return DAO.findById(id);
	}
	public SellerProduct findByProductId(String productId){
		String sql = "select * from cc_seller_product where product_id=?";
		return DAO.findFirst(sql.toString(), productId);
	}

	public Page<SellerProduct> paginate(int pageNumber, int pageSize,String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_product` ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "name", keyword, params, true);
		
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

	public Page<SellerProduct> paginate_sel(int pageNumber, int pageSize,String keyword,String userId) {
		String select = "SELECT csp.id,csp.product_id,csp.bar_code,csp.qrcode_url,cgc.name as cgc_name,cp.name as productName, csp.custom_name,csp.store_count,csp.price,cp.big_unit,cp.small_unit,cp.convert_relate,csp.is_enable, csp.order_list ,GROUP_CONCAT(distinct cgs.`name`) AS cps_name";
		StringBuilder fromBuilder = new StringBuilder("from cc_seller_product csp LEFT JOIN cc_product cp ON  csp.product_id = cp.id LEFT JOIN cc_product_goods_specification_value cpg ON  cp.id = cpg.product_set_id "
				+ " LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ " LEFT JOIN cc_seller cs on cs.id=csp.seller_id"
				+ " LEFT JOIN user u on u.department_id =cs.dept_id "
				+ " LEFT JOIN cc_goods cg on cg.id = cp.goods_id "
				+ "LEFT JOIN cc_goods_category cgc on cgc.id = cg.goods_category_id");
		LinkedList<Object> params = new LinkedList<Object>();
		if(!keyword.equals("")){
			appendIfNotEmptyWithLike(fromBuilder, "csp.custom_name", keyword, params, true);
			fromBuilder.append(" and cs.seller_type=0 and cs.is_enabled=1 and u.id='"+userId+"' ");
		}else{
			fromBuilder.append(" where cs.seller_type=0 and cs.is_enabled=1 and u.id='"+userId+"' ");
		}
		fromBuilder.append(" GROUP BY csp.id ORDER BY csp.is_enable desc,csp.order_list ");
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public List<SellerProduct> findBySellerId(String sellId) {
 		StringBuilder fromBuilder = new StringBuilder("select cg.*,t1.valueName from cc_seller_product cg ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = cg.product_id ");
		fromBuilder.append("WHERE cg.seller_id = ? ");
		return DAO.find(fromBuilder.toString(), sellId);
	}

	public List<SellerProduct> findByProductIdAndSellerId(String seller_product_id, String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("select * from cc_seller_product where id=? and seller_id=?");
		return DAO.find(fromBuilder.toString(), seller_product_id,sellerId);
	}
	
	public List<Record> findProductTypeBySellerForApp(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder(" SELECT gt.id, gt.`name` ");
		fromBuilder.append(" FROM cc_seller_product sp ");
		fromBuilder.append(" JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append(" JOIN cc_goods_category gc ON g.goods_category_id = gc.id ");
		fromBuilder.append(" JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1  AND sp.is_gift = 0 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, false);

		fromBuilder.append(" GROUP BY gt.id ");
		fromBuilder.append(" ORDER BY gt.`name` ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}
	
	public List<Record> findProductListForApp(String sellerId, String keyword) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sp.id AS sell_product_id, sp.product_id, sp.custom_name, sp.store_count, sp.price, p.convert_relate, p.big_unit, p.small_unit, t1.valueName, g.`name` AS goodsName, gc.`name` AS categoryName, gt.`id` as typeId, gt.`name` as typeName ");
		fromBuilder.append(" FROM cc_seller_product sp JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv.`name`) AS valueName FROM cc_goods_specification_value sv RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id ) t1 ON t1.product_set_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id JOIN cc_goods_category gc ON g.goods_category_id = gc.id JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1 AND sp.is_gift = 0");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, false);
		appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, false);

		fromBuilder.append(" ORDER BY gt.`name`, sp.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}
	
	public List<SellerProduct> _findByProductIdAndSellerId(String product_id, String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("select * from cc_seller_product where product_id=? and seller_id=?");
		return DAO.find(fromBuilder.toString(), product_id,sellerId);
	}

	public List<SellerProduct> findByCompositionId(String productId) {
		StringBuilder stringBuilder = new StringBuilder("SELECT cc.*,cp.sub_product_count as productCount, cd.convert_relate FROM cc_seller_product cc ");
		stringBuilder.append("RIGHT JOIN cc_product_composition cp ON cp.sub_seller_product_id = cc.id ");
		stringBuilder.append("LEFT JOIN cc_product cd ON cd.id = cc.product_id ");
		stringBuilder.append("WHERE parent_id = ? ");
		stringBuilder.append("UNION ALL ");
		stringBuilder.append("SELECT cc.*,1 as productCount, cd.convert_relate FROM cc_seller_product cc ");
		stringBuilder.append("RIGHT JOIN cc_product_composition cp ON cp.seller_product_id = cc.id ");
		stringBuilder.append("LEFT JOIN cc_product cd ON cd.id = cc.product_id ");
		stringBuilder.append("WHERE parent_id = ? GROUP BY cp.parent_id");
		return DAO.find(stringBuilder.toString(), productId, productId);
	}
}
