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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.ccloud.model.MemberJoinSeller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class MemberJoinSellerQuery extends JBaseQuery { 

	protected static final MemberJoinSeller DAO = new MemberJoinSeller();
	private static final MemberJoinSellerQuery QUERY = new MemberJoinSellerQuery();

	public static MemberJoinSellerQuery me() {
		return QUERY;
	}

	public MemberJoinSeller findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<MemberJoinSeller> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_member_join_seller` ");

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

	public List<Record> findProductListForApp(String memberId, String keyword, String tag) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sp.seller_id, sp.id AS sell_product_id, sp.product_id, sp.custom_name, sp.store_count, sp.price, sp.cost, sp.account_price, sp.tags,"
						+ " p.convert_relate, p.product_sn, p.big_unit, p.small_unit, p.description, t1.valueName,"
						+ " g.`name` AS goodsName, g.product_image_list_store, gc.`id` AS categoryId, gc.`name` AS categoryName, gt.`id` as typeId, gt.`name` as typeName ");
		fromBuilder.append(" FROM cc_seller_product sp JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv.`name`) AS valueName FROM cc_goods_specification_value sv RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id ) t1 ON t1.product_set_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id JOIN cc_goods_category gc ON g.goods_category_id = gc.id JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1 AND sp.is_gift = 0");

		LinkedList<Object> params = new LinkedList<Object>();

		fromBuilder.append(" AND sp.seller_id IN (SELECT cmjs.seller_id FROM cc_member_join_seller cmjs WHERE cmjs.member_id = ? AND cmjs.status = ? ) ");
		params.add(memberId);
		params.add(1);

		appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, false);

		if (StrKit.notBlank(tag)) {
			fromBuilder.append(" AND FIND_IN_SET(?, sp.tags)");
			params.add(tag);
		}

		fromBuilder.append(" ORDER BY gc.`parent_id`, gc.`order_list`, gc.`id`, sp.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}

	public MemberJoinSeller findUser(String memberId, String sellerId) {
		String sql = "SELECT * FROM cc_member_join_seller WHERE member_id = ? AND seller_id = ? ";
		return DAO.findFirst(sql, memberId, sellerId);
	}

}
