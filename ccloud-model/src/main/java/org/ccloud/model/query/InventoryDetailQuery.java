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

import org.ccloud.Consts;
import org.ccloud.model.InventoryDetail;

import com.jfinal.kit.StrKit;
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

	public Page<InventoryDetail> paginate(int pageNumber, int pageSize, String warehouse_id,String product_id,String seller_id,String start_date,String end_date) {
		String select = "SELECT sp.custom_name, p.product_sn, p.name AS product_name,i.in_count, i.in_amount, i.in_price, i.out_count,i.out_amount,i.out_price,i.balance_count,i.balance_amount,i.balance_price,i.biz_type,u.realname,i.biz_bill_sn,i.biz_date ";
		StringBuilder fromBuilder = new StringBuilder(" FROM `cc_inventory_detail` AS i ");
		fromBuilder.append(" INNER JOIN `cc_product` AS p ON  i.sell_product_id in (select sp.id from cc_seller_product sp where sp.product_id ='");
		fromBuilder.append(product_id+"'and sp.seller_id='"+seller_id+"' )INNER JOIN `user` AS u ON i.biz_user_id = u.id ");
		fromBuilder.append("INNER JOIN cc_seller_product AS sp ON sp.id=i.sell_product_id ");
		fromBuilder.append("WHERE i.warehouse_id = '"+ warehouse_id+"'and p.id='"+product_id+"' AND i.biz_date >='"+start_date+" 00:00:00' AND i.biz_date <='"+end_date+" 23:59:59'");
		fromBuilder.append("ORDER BY sp.custom_name, i.create_date DESC");
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
	
	public InventoryDetail findBalanceCountByWarehouseIdId(String warehouseId, String productId){
		String sql = "select MAX(create_date),balance_count from cc_inventory_detail where warehouse_id = '"+warehouseId+"' and sell_product_id = '"+productId+"'";
		return DAO.findFirst(sql);
	}

	public InventoryDetail findBySellerProductId(String seller_product_id,String warehouse_id) {
		String sql = "select * from cc_inventory_detail c where c.sell_product_id = '"+seller_product_id+"' and c.warehouse_id ='"+warehouse_id+"' ORDER BY  c.create_date DESC ";
		return DAO.findFirst(sql);
	}
	
	
	
	public Page<InventoryDetail> _in_paginate(int pageNumber, int pageSize,String keyword,String sellerId,String dataArea, String startDate, String endDate,String sellerProductId,String sort,String sortOrder) {
		String select = "SELECT cid.*,cw.`name` as warehouse,csp.custom_name as sellerName ";
		StringBuilder fromBuilder = new StringBuilder(" from cc_inventory_detail cid ");
		fromBuilder.append(" LEFT JOIN cc_warehouse cw on cw.id = cid.warehouse_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_product csp on csp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_user_join_warehouse cujw on cujw.warehouse_id=cid.warehouse_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
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
		fromBuilder.append("and cid.biz_type in ('"+Consts.BIZ_TYPE_INSTOCK+"','"+Consts.BIZ_TYPE_SALES_REFUND_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_INSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_PLUS_INSTOCK+"') ");
		if(sort==null){
			fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"' GROUP BY cid.id ");	
		}else{
			fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"' GROUP BY cid.id order by "+sort+" "+ sortOrder);	
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<InventoryDetail> _out_paginate(int pageNumber, int pageSize,String keyword,String sellerId,String dataArea, String startDate, String endDate,String sellerProductId,String sort,String sortOrder) {
		String select = "SELECT cid.*,cw.`name` as warehouse,csp.custom_name as sellerName ";
		StringBuilder fromBuilder = new StringBuilder(" from cc_inventory_detail cid ");
		fromBuilder.append(" LEFT JOIN cc_warehouse cw on cw.id = cid.warehouse_id ");
		fromBuilder.append(" LEFT JOIN cc_seller_product csp on csp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_user_join_warehouse cujw on cujw.warehouse_id=cid.warehouse_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
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
		fromBuilder.append("and cid.biz_type in ('"+Consts.BIZ_TYPE_P_OUTSTOCK+"','"+Consts.BIZ_TYPE_SALES_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_OUTSTOCK+"','"+Consts.BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK+"') ");
		if(sort==null){
			fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"' GROUP BY cid.id ");	
		}else{
			fromBuilder.append(" and ( cid.biz_bill_sn like '%"+keyword+"%' or csp.custom_name like '%"+keyword+"%' ) and csp.seller_id = '"+sellerId+"' GROUP BY cid.id order by "+sort+" "+ sortOrder);	
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	//库存明细报表
	public Page<InventoryDetail> findByDataArea(int pageNumber, int pageSize, String startDate, String endDate,String dataArea, String warehouseId, String sort, String order) {
		String select = "select cid.warehouse_id , cid.sell_product_id , cs.seller_name , w.`name` , sp.custom_name , IFNULL(SUM(cid.out_count) , 0) out_count , IFNULL(SUM(cid.in_count) , 0) in_count";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_inventory_detail cid");
		fromBuilder.append(" LEFT JOIN cc_warehouse w ON w.id = cid.warehouse_id  ");
		fromBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_seller cs ON cs.id = sp.seller_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "w.id", warehouseId, params, needWhere);
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cid.create_date >= ?");
			params.add(startDate);
		}
		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cid.create_date <= ?");
			params.add(endDate);
		}
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
			String dataArea, String seller_id, String sort, String order) {
		String select = "SELECT cs.seller_name , '总计' AS `name` , sp.custom_name , IFNULL(SUM(cid.out_count) , 0) out_count ,IFNULL(SUM(cid.in_count) , 0)  in_count , SUM(cid.in_count) - IFNULL(SUM(cid.out_count) , 0) AS  balance_count";
		StringBuilder fromBuilder = new StringBuilder(" FROM cc_inventory_detail cid ");
		fromBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.id = cid.sell_product_id ");
		fromBuilder.append(" LEFT JOIN cc_seller cs ON cs.id = sp.seller_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cid.data_area", dataArea, params, needWhere);
		if (sort==""||null==sort) {
			fromBuilder.append("GROUP BY cid.sell_product_id order by cs.seller_type,cs.id ");
		}else {
			fromBuilder.append("GROUP BY cid.sell_product_id order by "+sort+" "+order);
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	//查询当前的剩余商品
	public InventoryDetail findByInventoryDetail(String sellProductId, String warehouseId1, String startDate,
			String endDate) {
		String sql = "select * from cc_inventory_detail c where c.sell_product_id = '"+sellProductId+"' and c.warehouse_id ='";
		sql=sql+warehouseId1+"'and c.create_date >= '"+startDate+"' and c.create_date <= '"+endDate+"' ORDER BY c.create_date DESC ";
		return DAO.findFirst(sql);
	}
}
