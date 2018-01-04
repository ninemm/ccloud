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
		String select = "SELECT sp.custom_name,i.seller_id,i.product_id,p.name as product_name ,p.product_sn,i.in_count, i.in_amount, i.in_price, i.out_count, i.out_amount, i.out_price, i.balance_count, i.balance_amount, i.balance_price, i.afloat_count, i.afloat_amount, i.afloat_price, i.create_date, i.modify_date,t1.valueName";
		StringBuilder fromBuilder = new StringBuilder("from `cc_inventory` as i INNER JOIN  `cc_product` as p ON i.product_id = p.id INNER JOIN cc_seller_product as sp on sp.seller_id=i.seller_id and sp.product_id=p.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = sp.product_id ");		
		fromBuilder.append("WHERE i.warehouse_id = '"+ warehouse_id+"'and i.seller_id='"+seller_id+"'");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "p.name", product_name, params, false);
		appendIfNotEmptyWithLike(fromBuilder, "p.product_sn", product_sn, params, false);
		fromBuilder.append("GROUP BY p.id ORDER BY i.create_date DESC");
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

	public List<Inventory> _findBySellerIdAndProductId(String sellerId,String productId){
		String sql ="select * from cc_inventory where seller_id='"+sellerId+"' and product_id ='"+productId+"'";
		return DAO.find(sql);
	}	
	
	public Inventory findBySellerIdAndProductIdAndWareHouseId(String sellerId, String productId, String wareHouseId) {
		String sql ="select * from cc_inventory where seller_id=? and product_id =? and warehouse_id = ?";
		return DAO.findFirst(sql, sellerId, productId, wareHouseId);
	}

	public List<Record> findProductStore(String sellerId, String productId) {
		StringBuilder defaultSqlBuilder = new StringBuilder(" select i.warehouse_id, i.balance_count ");
		defaultSqlBuilder.append(" from cc_inventory i ");
		defaultSqlBuilder.append(" LEFT JOIN cc_warehouse w ON i.warehouse_id = w.id ");
		defaultSqlBuilder.append(" WHERE i.seller_id = ? AND i.product_id = ? order by w.is_default desc");
		return Db.find(defaultSqlBuilder.toString(), sellerId, productId);
	}
	
	public List<Record> findProductStoreByUser(String sellerId, String productId, String userId) {
		StringBuilder defaultSqlBuilder = new StringBuilder("SELECT cc.warehouse_id, cc.balance_count FROM cc_inventory cc ");
		defaultSqlBuilder.append("LEFT JOIN cc_warehouse cw on cw.id = cc.warehouse_id ");
		defaultSqlBuilder.append("LEFT JOIN (SELECT cu.warehouse_id,cu.user_id FROM cc_user_join_warehouse cu where cu.user_id=?) t1 ");
		defaultSqlBuilder.append("on t1.warehouse_id = cc.warehouse_id ");
		defaultSqlBuilder.append("where cc.product_id = ? and cc.seller_id = ? ");
		defaultSqlBuilder.append("ORDER BY t1.user_id desc, cw.is_default desc ");
		return Db.find(defaultSqlBuilder.toString(), userId, productId, sellerId);
	}	
	
	public Page<Record> findDetailByApp(int pageNumber, int pageSize,String wareHouseId,String productName, String sellerId, String dataArea, String deptId){
		String select ="select cc_s.id,cc_s.seller_name,cc_s.seller_code,cc_s.seller_type,cc_i.product_id,cc_p.`name`,IFNULL(sum(cc_i.in_count),0) in_count,IFNULL(sum(cc_i.out_count),0) out_count,IFNULL(sum(cc_i.balance_count),0) balance_count,IFNULL(sum(cc_i.afloat_count),0) afloat_count ";
		StringBuilder fromBuilder = new StringBuilder("from cc_inventory cc_i left join cc_seller cc_s on cc_i.seller_id = cc_s.id left join cc_product cc_p on cc_i.product_id = cc_p.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "cc_i.warehouseId", wareHouseId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_p.`name`", productName, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_i.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_i.dept_id", deptId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_i.data_area", dataArea, params, needWhere);
		fromBuilder.append("GROUP BY cc_s.id,cc_i.product_id ");
		//fromBuilder.append("limit 0,10 ");
		Page<Record> list = Db.paginate(pageNumber, pageSize,select,fromBuilder.toString(), params.toArray());	
		return list;
	}
	/**APP端 库存详情 数据**/
	public Page<Record> findDetailByParams(String search,String goodsType, String sellerId, String productType, String deptId, String dataArea,String isOrdered, int page, int pageSize){
		String select = "select * ";
		//StringBuilder fromBuilder = new StringBuilder("select cc_s.id,cc_s.seller_name,cc_s.seller_code,cc_s.seller_type,cc_i.product_id,cc_p.`name`,count(cc_i.in_count) in_count,count(cc_i.out_count) out_count ");
		StringBuilder fromBuilder = new StringBuilder("from (select cc_s.id,cc_s.seller_name,cc_s.seller_code,cc_s.seller_type,cc_i.product_id,cc_p.`name`,IFNULL(sum(cc_i.in_count),0) in_count,IFNULL(sum(cc_i.out_count),0) out_count,IFNULL(sum(cc_i.balance_count),0) balance_count,IFNULL(sum(cc_i.afloat_count),0) afloat_count,querys.counts ");
		fromBuilder.append("from cc_inventory cc_i left join cc_seller cc_s on cc_i.seller_id = cc_s.id left join cc_product cc_p on cc_i.product_id = cc_p.id ");
		fromBuilder.append("left join cc_goods cc_g on cc_p.goods_id = cc_g.id left join cc_goods_type cc_gt on cc_g.goods_type_id = cc_gt.id ");
		fromBuilder.append("left join (select csp.seller_id seller_id,csp.product_id product_id,count(csod.id) counts from cc_seller_product csp left join  cc_sales_order_detail csod on csp.id = csod.sell_product_id group by csp.seller_id,csp.product_id) querys on querys.seller_id = cc_s.id and querys.product_id = cc_i.product_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmpty(fromBuilder, "cc_gt.id", goodsType, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_i.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_p.`name`", productType, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "cc_i.dept_id", deptId, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_i.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_p.`name`", search, params, needWhere);
		fromBuilder.append("GROUP BY cc_s.id,cc_i.product_id ");
		if(isOrdered.equals("0")) {
			fromBuilder.append("having querys.counts<=0 or ISNULL(querys.counts)=1 ");
		}else if(isOrdered.equals("1")){
			fromBuilder.append("having querys.counts>0 ");
		}
		fromBuilder.append(")q ");
		return Db.paginate(page, pageSize,select, fromBuilder.toString(),params.toArray());
	}

	public List<Record> getWareHouse(String dataArea, String userId) {
		StringBuilder fromBuilder = new StringBuilder("select w.id,w.code,w.name from  cc_warehouse w where w.is_enabled=1 and w.data_area like '"+dataArea+"' OR w.id IN (SELECT uw.warehouse_id FROM cc_user_join_warehouse uw WHERE uw.user_id = '"+userId+"')");
		List<Record> list = Db.find(fromBuilder.toString());
		return list;
	}
	
}
