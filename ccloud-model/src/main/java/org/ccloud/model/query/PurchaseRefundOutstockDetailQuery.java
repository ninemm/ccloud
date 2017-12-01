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

import org.ccloud.model.PurchaseRefundOutstockDetail;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PurchaseRefundOutstockDetailQuery extends JBaseQuery { 

	protected static final PurchaseRefundOutstockDetail DAO = new PurchaseRefundOutstockDetail();
	private static final PurchaseRefundOutstockDetailQuery QUERY = new PurchaseRefundOutstockDetailQuery();

	public static PurchaseRefundOutstockDetailQuery me() {
		return QUERY;
	}

	public PurchaseRefundOutstockDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<PurchaseRefundOutstockDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_refund_outstock_detail` ");

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

	public List<Record> findByRefundId(String refundId,String dataArea) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT cprod.*,cp.`name` as productName, cp.big_unit, cp.small_unit, cp.convert_relate,GROUP_CONCAT(distinct cgs.`name`) AS cps_name  ");
		sqlBuilder.append(" from cc_purchase_refund_outstock_detail cprod ");
		sqlBuilder.append(" LEFT JOIN cc_product_goods_specification_value cpg ON  cprod.seller_product_id = cpg.product_set_id ");
		sqlBuilder.append(" LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id ");
		sqlBuilder.append(" LEFT JOIN cc_product cp on cp.id= cprod.seller_product_id  ");
		sqlBuilder.append(" where cprod.purchase_refund_outstock_id=? and cprod.data_area="+dataArea+"GROUP BY cprod.id ");

		return Db.find(sqlBuilder.toString(), refundId);
	}
	
	public List<PurchaseRefundOutstockDetail> findAllByPurchaseRefundId(String purchaseRefundOutstockId,String dataArea){
		String sql = "select prod.* ,pro.outstock_sn,pro.warehouse_id from cc_purchase_refund_outstock_detail prod "
				+ " LEFT JOIN cc_purchase_refund_outstock pro on pro.id = prod.purchase_refund_outstock_id where pro.id = ? and pro.data_area="+dataArea;
		return DAO.find(sql, purchaseRefundOutstockId);
	}
	
}
