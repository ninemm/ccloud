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

import org.ccloud.model.PurchaseOrderDetail;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PurchaseOrderDetailQuery extends JBaseQuery { 

	protected static final PurchaseOrderDetail DAO = new PurchaseOrderDetail();
	private static final PurchaseOrderDetailQuery QUERY = new PurchaseOrderDetailQuery();

	public static PurchaseOrderDetailQuery me() {
		return QUERY;
	}

	public PurchaseOrderDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<PurchaseOrderDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_order_detail` ");

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
	
	public List<PurchaseOrderDetail> findByPurchaseId(String purchaseOrderId){
		String sql = "select cpod.* from cc_purchase_order_detail cpod LEFT JOIN cc_purchase_order cpo on cpod.purchase_order_id=cpo.id where cpo.id=?";
		return DAO.find(sql, purchaseOrderId);
	}
	
	public List<PurchaseOrderDetail> findByAll(String id){
		String sql = "select cpod.product_count as product,cpod.product_amount as productAmount,cpod.product_price as price,cp.NAME AS name,cp.big_unit as big_unit, cp.small_unit as small_unit,cp.convert_relate as convert_relate,cp.price AS price,GROUP_CONCAT(DISTINCT cgs.`name`) AS cps_name "
				+" from cc_purchase_order_detail cpod "
				+ "LEFT JOIN cc_purchase_order cpo on cpo.id = cpod.purchase_order_id "
				+ "LEFT JOIN cc_product cp on cp.id= cpod.product_id "
				+ "LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id "
				+ "LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ "WHERE cpo.id=? GROUP BY cpo.id";
		return DAO.find(sql, id);
	}

	
}
