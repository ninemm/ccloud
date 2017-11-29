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
import org.ccloud.model.InventoryDetail;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class InventoryDetailQuery extends JBaseQuery { 

	protected static final InventoryDetail DAO = new InventoryDetail();
	private static final InventoryDetailQuery QUERY = new InventoryDetailQuery();

	public static InventoryDetailQuery me() {
		return QUERY;
	}

	public InventoryDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<InventoryDetail> paginate(int pageNumber, int pageSize, String warehouse_id,String sell_product_id,String start_date,String end_date) {
		String select = "SELECT p.product_sn, p.name AS product_name, i.in_count, i.in_amount, i.in_price, i.out_count,i.out_amount,i.out_price,i.balance_count,i.balance_amount,i.balance_price,i.biz_type,u.realname,i.biz_bill_sn,i.biz_date ";
		StringBuilder fromBuilder = new StringBuilder("FROM `cc_inventory_detail` AS i INNER JOIN `cc_product` AS p ON (SELECT product_id from cc_seller_product sp WHERE i.sell_product_id=sp.id) = p.id INNER JOIN `user` AS u ON i.biz_user_id = u.id ");
		fromBuilder.append("WHERE i.warehouse_id = '"+ warehouse_id+"' AND i.sell_product_id='"+sell_product_id+"' AND i.biz_date >='"+start_date+" 00:00:00' AND i.biz_date <='"+end_date+" 23:59:59'");
		fromBuilder.append("ORDER BY i.biz_date DESC");
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

	public InventoryDetail findByWarehouseIdAndProductId(String warehouseId, String productId){
		String sql = "select * from cc_inventory_detail where warehouse_id = '"+warehouseId+"' and sell_product_id = '"+productId+"'";
		return DAO.findFirst(sql);
	}
}
