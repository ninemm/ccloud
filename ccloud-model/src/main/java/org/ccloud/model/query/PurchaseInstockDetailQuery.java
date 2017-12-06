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

import org.ccloud.model.PurchaseInstockDetail;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PurchaseInstockDetailQuery extends JBaseQuery { 

	protected static final PurchaseInstockDetail DAO = new PurchaseInstockDetail();
	private static final PurchaseInstockDetailQuery QUERY = new PurchaseInstockDetailQuery();

	public static PurchaseInstockDetailQuery me() {
		return QUERY;
	}

	public PurchaseInstockDetail findById(final String id) {
				return DAO.findById(id);
	}

	public Page<PurchaseInstockDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_instock_detail` ");

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

	public List<Record> findByOutstockId(String outstockId,String dataArea) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT cpid.*,cp.`name` as productName, cp.big_unit, cp.small_unit, cp.convert_relate,GROUP_CONCAT(distinct cgs.`name`) AS cps_name ");
		sqlBuilder.append(" FROM cc_purchase_instock_detail cpid  ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product csp on csp.product_id=cpid.seller_product_id ");
		sqlBuilder.append(" LEFT JOIN cc_product_goods_specification_value cpg ON  cpid.seller_product_id = cpg.product_set_id ");
		sqlBuilder.append(" LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id ");
		sqlBuilder.append(" LEFT JOIN cc_product cp on cp.id= cpid.seller_product_id ");
		sqlBuilder.append(" where cpid.purchase_instock_id=? and cpid.data_area='"+dataArea+"' GROUP BY cpid.id ");

		return Db.find(sqlBuilder.toString(), outstockId);
	}

	public List<PurchaseInstockDetail> findAllByPurchaseInstockId(String purchaseInstockId){
		String sql = "select cpid.* ,cp.convert_relate,cpi.warehouse_id,cpi.pwarehouse_sn from cc_purchase_instock_detail cpid "
				+ " LEFT JOIN cc_purchase_instock cpi on cpid.purchase_instock_id=cpi.id "
				+ " LEFT JOIN cc_product cp ON  cp.id = cpid.seller_product_id "
				+ "where cpi.id=?";
		return DAO.find(sql, purchaseInstockId);
	}
}
