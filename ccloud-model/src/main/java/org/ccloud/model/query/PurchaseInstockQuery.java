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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.PurchaseInstock;
import org.ccloud.model.vo.printAllNeedInfo;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PurchaseInstockQuery extends JBaseQuery { 

	protected static final PurchaseInstock DAO = new PurchaseInstock();
	private static final PurchaseInstockQuery QUERY = new PurchaseInstockQuery();

	public static PurchaseInstockQuery me() {
		return QUERY;
	}

	public PurchaseInstock findById(final String id) {
				return DAO.findById(id);
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate,String deptId,String dataArea) {
		String select = "select i.*, CASE WHEN cs.`name` IS NOT NULL THEN cs.`name` ELSE s.seller_name END AS supplierName,po.porder_sn ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_instock` i ");
		fromBuilder.append(" LEFT JOIN cc_purchase_order_join_instock poji on poji.purchase_instock_id = i.id ");
		fromBuilder.append(" LEFT JOIN cc_purchase_order po on po.id  = poji.purchase_order_id ");
		fromBuilder.append(" left join cc_supplier cs on i.supplier_id = cs.id ");
		fromBuilder.append(" LEFT JOIN cc_seller s ON i.supplier_id = s.id ");
		fromBuilder.append(" left join user u on i.input_user_id = u.id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		//needWhere = appendIfNotEmptyWithLike(fromBuilder, "i.pwarehouse_sn", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "i.data_area", dataArea, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and i.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and i.create_date <= ?");
			params.add(endDate);
		}

		fromBuilder.append(" and ( i.pwarehouse_sn like '%"+keyword+"%' or cs.name like '%"+keyword+"%' or s.seller_name like '%"+keyword+"%' ) and i.dept_id ='"+deptId+"'  order by i.create_date desc ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
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
	
	public int findByUserId(String userId,String dataArea){
		String sql = "select * from cc_purchase_instock where input_user_id ='"+userId+"' and data_area ='"+dataArea+"' ";
		return DAO.find(sql).size();
	}
	
	public Record findMoreById(final String id,String dataArea) {
		StringBuilder fromBuilder = new StringBuilder(
				" select cpi.*,u.mobile as userMobile,"
				+ "CASE WHEN cs.id IS NOT NULL THEN cs.id ELSE s.id END as supplierId,cs.code,"
				+ "CASE WHEN cs.`name` IS NOT NULL THEN cs.`name` ELSE s.seller_name END AS supplierName,"
				+ "CASE WHEN cs.contact IS NOT NULL THEN cs.contact ELSE s.contact END as contact,"
				+ "CASE WHEN cs.mobile IS NOT NULL THEN cs.mobile ELSE s.phone END as supplierMobile,"
				+ "u.realname as biz_userName  ");
		fromBuilder.append(" from cc_purchase_instock cpi ");
		fromBuilder.append(" LEFT JOIN cc_supplier cs on cs.id= cpi.supplier_id ");
		fromBuilder.append(" LEFT JOIN cc_seller s ON cpi.supplier_id = s.id ");
		fromBuilder.append(" left join user u on cpi.biz_user_id = u.id ");
		fromBuilder.append(" where cpi.id = ? and cpi.data_area like'"+dataArea+"' GROUP BY cpi.id");

		return Db.findFirst(fromBuilder.toString(), id);
	}
	
	public Page<Record> paginateO(int pageNumber, int pageSize, String keyword, String startDate, String endDate,String userId,String dataArea,String purchaseRefundOustockIds,String deptId) {
		String select = "select DISTINCT i.*, CASE WHEN cs.`name` IS NOT NULL THEN cs.`name` ELSE s.seller_name END AS supplierName";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_instock` i ");
		fromBuilder.append(" LEFT JOIN cc_supplier cs on i.supplier_id = cs.id "
				+ " LEFT JOIN cc_seller s on s.id = i.supplier_id "
				+ " LEFT JOIN user u on u.department_id = i.dept_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "i.pwarehouse_sn", keyword, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "i.data_area", dataArea, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and i.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and i.create_date <= ?");
			params.add(endDate);
		}

		fromBuilder.append(" and i.dept_id = '"+deptId+"'  and i.id not in ("+purchaseRefundOustockIds+") and i.status=1000 order by i.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public String getNewSn(String sellerId) {
		String sql = "SELECT p.pwarehouse_sn FROM cc_purchase_instock p LEFT JOIN cc_seller s on s.dept_id = p.dept_id WHERE date(p.create_date) = curdate()"
				+ " and s.id = '"+sellerId+"' ORDER BY p.create_date desc";
		PurchaseInstock purchaseInstock = DAO.findFirst(sql);
		String SN = "";
		if (purchaseInstock == null || StringUtils.isBlank(purchaseInstock.getPwarehouseSn())) {
			SN = Consts.PURCHASE_IN_STOCK_SN;
		} else {
			String endSN = StringUtils.substringSN(Consts.PURCHASE_IN_STOCK_SN, purchaseInstock.getPwarehouseSn());
			SN = new BigDecimal(endSN).add(new BigDecimal(1)).toString();
		}
		return SN;
	}
	
	public boolean insertBySalesOutStock(Map<String, String[]> paraMap, Record seller, String purchaseInstockId,
			String pwarehouseSn, String warehouseId, String userId, Date date,String sellerId) {
		PurchaseInstock purchaseInstock = new PurchaseInstock();
		purchaseInstock.set("id", purchaseInstockId);
		purchaseInstock.set("pwarehouse_sn", pwarehouseSn);
		purchaseInstock.set("warehouse_id", warehouseId);
		purchaseInstock.set("biz_user_id", userId);
		purchaseInstock.setSupplierId(sellerId);
		purchaseInstock.set("input_user_id", StringUtils.getArrayFirst(paraMap.get("input_user_id")));
		purchaseInstock.set("status", 0);// 待入库
		purchaseInstock.set("total_amount", StringUtils.getArrayFirst(paraMap.get("total")));
		purchaseInstock.set("payment_type", StringUtils.getArrayFirst(paraMap.get("paymentType")));
		purchaseInstock.set("remark", StringUtils.getArrayFirst(paraMap.get("remark")));
		purchaseInstock.set("dept_id", seller.get("dept_id"));
		purchaseInstock.set("data_area", seller.get("data_area"));
		purchaseInstock.set("create_date", date);
		return purchaseInstock.save();
	}
	
	
	public boolean insertByBatchSalesOutStock(printAllNeedInfo printAllNeedInfo, Record seller, String purchaseInstockId,
			String pwarehouseSn, String warehouseId, String userId, Date date,String sellerId) {
		PurchaseInstock purchaseInstock = new PurchaseInstock();
		purchaseInstock.set("id", purchaseInstockId);
		purchaseInstock.set("pwarehouse_sn", pwarehouseSn);
		purchaseInstock.set("warehouse_id", warehouseId);
		purchaseInstock.set("biz_user_id", userId);
		purchaseInstock.setSupplierId(sellerId);
		purchaseInstock.set("input_user_id",userId );
		purchaseInstock.set("status", 0);// 待入库
		purchaseInstock.set("total_amount", printAllNeedInfo.getSalesAmount());
		purchaseInstock.set("payment_type",printAllNeedInfo.getReceiveType());
		purchaseInstock.set("remark", printAllNeedInfo.getRemark());
		purchaseInstock.set("dept_id", seller.get("dept_id"));
		purchaseInstock.set("data_area", seller.get("data_area"));
		purchaseInstock.set("create_date", date);
		return purchaseInstock.save();
	}
	
	public PurchaseInstock findBySn(String orderSn){
		String sql = "select * from cc_purchase_instock where pwarehouse_sn = ?";
		return DAO.findFirst(sql, orderSn);
	}
	
	public PurchaseInstock findByPurchaseOrderSn(String orderSn){
		String sql = "SELECT cpi.* from cc_purchase_instock cpi "
				+ "LEFT JOIN cc_purchase_instock_detail cpid on cpi.id = cpid.purchase_instock_id "
				+ "LEFT JOIN cc_purchase_order_detail cpod on cpod.id = cpid.purchase_order_detail_id "
				+ "LEFT JOIN cc_purchase_order cpo on cpo.id = cpod.purchase_order_id "
				+ "where cpo.porder_sn = ? "
				+ "GROUP BY cpi.id";
		return DAO.findFirst(sql, orderSn);
	}
	
	public  Record findPurchaseInstockForPrint(String inStockId) {
	StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append(" SELECT cs.contact as customerContacts, cs.`name` as customerName, p.create_date, cs.mobile as customerPhone, u.realname as placeOrderMan, u.mobile AS placeOrderPhone,p.total_amount, p.remark,d.dept_name,cp.pwarehouse_sn ");
	stringBuilder.append(" FROM cc_purchase_order p INNER JOIN cc_supplier cs ON p.supplier_id = cs.id INNER JOIN `user` u ON u.id = p.biz_user_id ");
	stringBuilder.append(" INNER JOIN department d ON d.id = p.dept_id INNER JOIN cc_purchase_order_join_instock cj on p.id = cj.purchase_order_id INNER JOIN cc_purchase_instock  cp on cp.id = cj.purchase_instock_id ");
	stringBuilder.append(" WHERE cp.id = ? ");
	return Db.findFirst(stringBuilder.toString(), inStockId);
	}
	
	public List<Record> findPrintProductInfo(String inStockId) {
		   StringBuilder stringBuilder = new StringBuilder();
		   stringBuilder.append(" SELECT sod.seller_product_id, sp.custom_name as productName, p.big_unit, p.small_unit, p.convert_relate, sp.seller_id,sp.product_id, sp.bar_code, sod.product_price AS big_Price, ");
		   stringBuilder.append(" CONVERT (sod.product_price / p.convert_relate, DECIMAL (18, 2) ) AS small_price, floor(sod.product_count / p.convert_relate ) AS bigCount, ");
		   stringBuilder.append(" MOD (sod.product_count, p.convert_relate ) AS smallCount, sod.product_amount as productAmout, sod.product_count ");
		   stringBuilder.append(" FROM `cc_purchase_instock_detail` sod LEFT JOIN cc_purchase_instock cso ON cso.id = sod.purchase_instock_id ");
		   stringBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.seller_product_id = sp.id LEFT JOIN cc_product p ON sp.product_id = p.id ");
		   stringBuilder.append(" WHERE sod.purchase_instock_id = ? ");
			
		   return Db.find(stringBuilder.toString(),inStockId);
		}
}
