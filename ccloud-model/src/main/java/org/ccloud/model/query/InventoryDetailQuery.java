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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.ccloud.Consts;
import org.ccloud.model.InventoryDetail;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.IDataLoader;
import com.jfinal.plugin.activerecord.Page;

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

	public Page<InventoryDetail> paginate(int pageNumber, int pageSize, String warehouse_id,String product_id,String seller_id,String start_date,String end_date) {
		String select = "SELECT sp.custom_name, p.product_sn, p.name AS product_name,i.in_count, i.in_amount, i.in_price, i.out_count,i.out_amount,i.out_price,i.balance_count,i.balance_amount,i.balance_price,i.biz_type,u.realname,i.biz_bill_sn,i.biz_date ";
		StringBuilder fromBuilder = new StringBuilder(" FROM `cc_inventory_detail` AS i ");
		fromBuilder.append(" INNER JOIN `cc_product` AS p ON  i.sell_product_id in (select sp.id from cc_seller_product sp where sp.product_id ='"+product_id+"'");
		if (null!=seller_id) {
			fromBuilder.append("AND sp.seller_id='"+seller_id+"'");
		}
		fromBuilder.append(" )INNER JOIN `user` AS u ON i.biz_user_id = u.id");
		fromBuilder.append(" INNER JOIN cc_seller_product AS sp ON sp.id=i.sell_product_id ");
		fromBuilder.append(" WHERE i.warehouse_id = '"+ warehouse_id+"'and p.id='"+product_id+"' AND i.biz_date >='"+start_date+" 00:00:00' AND i.biz_date <='"+end_date+" 23:59:59'");
		fromBuilder.append(" ORDER BY sp.custom_name, i.create_date DESC");
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
	
//	public InventoryDetail findByWarehouseIdAndProductId(String warehouseId, String productId){
//		String sql = "select * from cc_inventory_detail where warehouse_id = '"+warehouseId+"' and sell_product_id = '"+productId+"'";
//		return DAO.findFirst(sql);
//	}
//	
//	public InventoryDetail findBalanceCountByWarehouseIdId(String warehouseId, String productId){
//		String sql = "select MAX(create_date),balance_count from cc_inventory_detail where warehouse_id = '"+warehouseId+"' and sell_product_id = '"+productId+"'";
//		return DAO.findFirst(sql);
//	}

	
	public Page<InventoryDetail> _in_paginate(int pageNumber, int pageSize,String keyword,String sellerId, String startDate, String endDate,String sellerProductId,String sort,String sortOrder) {
		String select = "SELECT cid.*,cw.`name` as warehouse,csp.custom_name as sellerName ";
		StringBuilder fromBuilder = new StringBuilder(" from cc_inventory_detail cid ");
		fromBuilder.append(" LEFT JOIN cc_warehouse cw on cw.id = cid.warehouse_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_product csp on csp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_user_join_warehouse cujw on cujw.warehouse_id=cid.warehouse_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		if (needWhere) {
			fromBuilder.append(" where ( 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cid.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cid.create_date <= ?");
			params.add(endDate);
		}
		if(!sellerProductId.equals("")){
			fromBuilder.append(" and csp.id = '"+sellerProductId+"' ");
		}
		fromBuilder.append(" and cid.biz_type in ('"+Consts.BIZ_TYPE_INSTOCK+"','"+Consts.BIZ_TYPE_SALES_REFUND_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_PLUS_INSTOCK+"') ");
		fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"' ");
		fromBuilder.append(" ) OR (csp.id IN( SELECT id.sell_product_id FROM cc_inventory_detail id WHERE id.warehouse_id IN( SELECT w.id FROM cc_warehouse w WHERE w.seller_id ='"+sellerId+"' ) GROUP BY id.sell_product_id)");
		fromBuilder.append(" and cid.biz_type in ('"+Consts.BIZ_TYPE_INSTOCK+"','"+Consts.BIZ_TYPE_SALES_REFUND_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_PLUS_INSTOCK+"') )");
		fromBuilder.append(" GROUP BY cid.id");
		if(sort!=null){
			fromBuilder.append(" order by "+sort+" "+ sortOrder);	
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<InventoryDetail> _out_paginate(int pageNumber, int pageSize,String keyword,String sellerId, String startDate, String endDate,String sellerProductId,String sort,String sortOrder) {
		String select = "SELECT cid.*,cw.`name` as warehouse,csp.custom_name as sellerName ";
		StringBuilder fromBuilder = new StringBuilder(" from cc_inventory_detail cid ");
		fromBuilder.append(" LEFT JOIN cc_warehouse cw on cw.id = cid.warehouse_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_product csp on csp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_user_join_warehouse cujw on cujw.warehouse_id=cid.warehouse_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		if (needWhere) {
			fromBuilder.append(" where ( 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cid.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cid.create_date <= ?");
			params.add(endDate);
		}
		if(!sellerProductId.equals("")){
			fromBuilder.append(" and csp.id = '"+sellerProductId+"' ");
		}
		fromBuilder.append(" and cid.biz_type in ('"+Consts.BIZ_TYPE_P_OUTSTOCK+"','"+Consts.BIZ_TYPE_SALES_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK+"') ");
		fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"'");	
		fromBuilder.append(" ) OR (csp.id IN( SELECT id.sell_product_id FROM cc_inventory_detail id WHERE id.warehouse_id IN( SELECT w.id FROM cc_warehouse w WHERE w.seller_id ='"+sellerId+"' ) GROUP BY id.sell_product_id)");
		fromBuilder.append(" and cid.biz_type in ('"+Consts.BIZ_TYPE_P_OUTSTOCK+"','"+Consts.BIZ_TYPE_SALES_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK+"')) ");
		fromBuilder.append(" GROUP BY cid.id ");
		if(sort!=null){
			fromBuilder.append(" order by "+sort+" "+ sortOrder);	
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	//库存明细报表
	public Page<InventoryDetail> findByDataArea(int pageNumber, int pageSize, String dataArea, String warehouseId, String sort, String order, String startDate, String endDate, String user_id, boolean admin) {
		String select = "SELECT cid.warehouse_id , cid.sell_product_id , cs.seller_name , w.`name` , sp.custom_name , IFNULL(t1.out_count,0) out_count, IFNULL(t1.in_count,0) in_count, IFNULL(t2.balance_count,0) balance_count ";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_inventory_detail cid");
		fromBuilder.append(" LEFT JOIN cc_warehouse w ON w.id = cid.warehouse_id  ");
		fromBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_seller cs ON cs.id = sp.seller_id ");
		
		fromBuilder.append(" LEFT JOIN( SELECT IFNULL(SUM(c.out_count) , 0) out_count , IFNULL(SUM(c.in_count) , 0) in_count , c.sell_product_id ,c.warehouse_id FROM cc_inventory_detail c WHERE");
		fromBuilder.append(" c.create_date >= '"+startDate+"' AND c.create_date <= '"+endDate+"' GROUP BY c.sell_product_id,c.warehouse_id) t1 ON t1.sell_product_id = sp.id AND t1.warehouse_id=cid.warehouse_id");
		
		fromBuilder.append(" LEFT JOIN( SELECT( IFNULL(SUM(c.in_count) , 0) - IFNULL(SUM(c.out_count) , 0)) balance_count , c.sell_product_id ,c.warehouse_id FROM cc_inventory_detail c WHERE ");
		fromBuilder.append(" c.create_date <= '"+endDate+"' GROUP BY c.sell_product_id,c.warehouse_id) t2 ON t2.sell_product_id = sp.id AND t2.warehouse_id=cid.warehouse_id");
		
		if (admin) {
			fromBuilder.append(" where cid.warehouse_id IN(SELECT c.id FROM `cc_warehouse` c LEFT JOIN department d ON c.dept_id = d.id WHERE c.id IN");
			fromBuilder.append("( SELECT uw.warehouse_id FROM cc_user_join_warehouse uw WHERE uw.user_id ='"+user_id+"') OR d.data_area LIKE '"+dataArea+"')");
		}else {
			fromBuilder.append(" where cid.warehouse_id IN(select w.id from  cc_warehouse w,cc_user_join_warehouse uw where w.id =uw.warehouse_id and uw.user_id='"+user_id+"' and w.is_enabled=1)");
		}
		
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = false;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "w.id", warehouseId, params, needWhere);
		fromBuilder.append("GROUP BY cid.warehouse_id , cid.sell_product_id ");
		if (sort==""||null==sort) {
			fromBuilder.append("order by "+"cid.create_date");
		}else {
			fromBuilder.append("order by "+sort+" "+order);
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	//库存详细报表 产品总计
	public Page<InventoryDetail> findByInventoryDetailListTotal(int pageNumber, int pageSize,
			String dataArea, String sort, String order, String sellerId, String startDate, String endDate, String user_id, boolean admin) {
		String select = "SELECT cid.warehouse_id , cid.sell_product_id , cs.seller_name ,  sp.custom_name , IFNULL(t1.out_count,0) out_count, IFNULL(t1.in_count,0) in_count, IFNULL(t2.balance_count,0) balance_count ";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_inventory_detail cid");
		fromBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_seller cs ON cs.id = sp.seller_id ");
		
		fromBuilder.append(" LEFT JOIN( SELECT IFNULL(SUM(c.out_count) , 0) out_count , IFNULL(SUM(c.in_count) , 0) in_count , c.sell_product_id FROM cc_inventory_detail c WHERE");
		fromBuilder.append(" c.create_date >= '"+startDate+"' AND c.create_date <= '"+endDate+"' GROUP BY c.sell_product_id) t1 ON t1.sell_product_id = cid.sell_product_id ");
		
		fromBuilder.append(" LEFT JOIN( SELECT( IFNULL(SUM(c.in_count) , 0) - IFNULL(SUM(c.out_count) , 0)) balance_count , c.sell_product_id FROM cc_inventory_detail c WHERE ");
		fromBuilder.append(" c.create_date <= '"+endDate+"' GROUP BY c.sell_product_id) t2 ON t2.sell_product_id = cid.sell_product_id ");
		
		if (admin) {
			fromBuilder.append(" where cid.warehouse_id IN(SELECT c.id FROM `cc_warehouse` c LEFT JOIN department d ON c.dept_id = d.id WHERE c.id IN");
			fromBuilder.append("( SELECT uw.warehouse_id FROM cc_user_join_warehouse uw WHERE uw.user_id ='"+user_id+"') OR d.data_area LIKE '"+dataArea+"')");
		}else {
			fromBuilder.append(" where cid.warehouse_id IN(select w.id from  cc_warehouse w,cc_user_join_warehouse uw where w.id =uw.warehouse_id and uw.user_id='"+user_id+"' and w.is_enabled=1)");
		}
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = false;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, needWhere);
		fromBuilder.append("GROUP BY cid.sell_product_id ");
		if (sort==""||null==sort) {
			fromBuilder.append("order by "+"cid.create_date");
		}else {
			fromBuilder.append("order by "+sort+" "+order);
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	//查询当前商品在库存中的数量
	public InventoryDetail findBySellerProductId(String seller_product_id, String warehouse_id) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat ("yyyy-MM-dd"); 
		Date date = new Date(); 
		StringBuilder fromBuilder = new StringBuilder("SELECT (IFNULL(SUM(c.in_count) , 0) - IFNULL(SUM(c.out_count) , 0)) balance_count,");
		fromBuilder.append("(IFNULL(SUM(c.in_amount) , 0) - IFNULL(SUM(c.out_amount) , 0)) balance_amount,c.balance_price ");
		fromBuilder.append("FROM cc_inventory_detail c WHERE c.create_date <= '"+bartDateFormat.format(date)+" 23:59:59'");
		fromBuilder.append(" AND c.sell_product_id = '"+seller_product_id+"' AND c.warehouse_id = '"+warehouse_id+"' GROUP BY c.sell_product_id");
		return DAO.findFirst(fromBuilder.toString());
	}

}
