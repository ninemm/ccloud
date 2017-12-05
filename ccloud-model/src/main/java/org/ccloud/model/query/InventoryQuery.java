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

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.Inventory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class InventoryQuery extends JBaseQuery { 

	protected static final Inventory DAO = new Inventory();
	private static final InventoryQuery QUERY = new InventoryQuery();

	public static InventoryQuery me() {
		return QUERY;
	}

	public Inventory findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Inventory> paginate(int pageNumber, int pageSize,String product_sn,String product_name,String warehouse_id, String seller_id) {
		String select = "SELECT i.seller_id,i.product_id,p.name as product_name ,p.product_sn,i.in_count, i.in_amount, i.in_price, i.out_count, i.out_amount, i.out_price, i.balance_count, i.balance_amount, i.balance_price, i.afloat_count, i.afloat_amount, i.afloat_price, i.create_date, i.modify_date";
		StringBuilder fromBuilder = new StringBuilder("from `cc_inventory` as i INNER JOIN  `cc_product` as p ON i.product_id = p.id ");
		fromBuilder.append("WHERE i.warehouse_id = '"+ warehouse_id+"'and i.seller_id='"+seller_id+"'");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "p.name", product_name, params, true);
		appendIfNotEmptyWithLike(fromBuilder, "p.product_sn", product_sn, params, true);
		fromBuilder.append("ORDER BY i.create_date DESC");
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
	
	public List<Record> getWareHouseInfo(String userId) {
		StringBuilder fromBuilder = new StringBuilder("select w.id,w.code,w.name from  cc_warehouse w,cc_user_join_warehouse uw where w.id =uw.warehouse_id and uw.user_id=? and w.is_enabled=1");
		List<Record> list = Db.find(fromBuilder.toString(),userId);
		return list;
	}
	
	public Inventory findByWarehouseIdAndProductId(String warehouseId,String productId){
		String sql = "select * from cc_inventory where warehouse_id='"+warehouseId+"' and product_id ='"+productId+"'";
		return DAO.findFirst(sql);
	}
	
	public Inventory findBySellerIdAndProductId(String sellerId,String productId){
		String sql ="select * from cc_inventory where seller_id='"+sellerId+"' and product_id ='"+productId+"'";
		return DAO.findFirst(sql);
	}

	public int updateInventory(String sellerId, String productId, BigDecimal count, BigDecimal productAmount,
			BigDecimal price, int inventoryType) {
		StringBuilder fromBuilder = new StringBuilder("update cc_inventory cc set ");
		if (inventoryType == Consts.INVENTORY_TYPE_IN) {
			fromBuilder.append("cc.in_amount = cc.in_amount+? and cc.in_count = cc.in_count+? AND cc.in_price = ? ");
			fromBuilder.append("AND cc.balance_amount = cc.balance_amount+? AND cc.balance_count = cc.balance_count+? AND cc.balance_price = ? ");
		} else {
			fromBuilder.append("cc.out_amount = cc.out_amount+? and cc.out_count = cc.out_count+? AND cc.out_price = ? ");
			fromBuilder.append("AND cc.balance_amount = cc.balance_amount-? AND cc.balance_count = cc.balance_count-? AND cc.balance_price = ? ");
		}
		fromBuilder.append("WHERE cc.seller_id=? and cc.product_id = ? ");
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(productAmount);
		params.add(count);
		params.add(price);
		params.add(productAmount);
		params.add(count);
		params.add(price);
		params.add(sellerId);
		params.add(productId);
		int i =Db.update(fromBuilder.toString(), params.toArray());
		return i;
	}
}
