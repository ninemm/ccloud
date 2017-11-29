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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.model.SalesOrderDetail;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SalesOrderDetailQuery extends JBaseQuery {

	protected static final SalesOrderDetail DAO = new SalesOrderDetail();
	private static final SalesOrderDetailQuery QUERY = new SalesOrderDetailQuery();

	public static SalesOrderDetailQuery me() {
		return QUERY;
	}

	public List<Record> findByOrderId(String orderId) {

		StringBuilder sqlBuilder = new StringBuilder(
				" SELECT sod.*, sp.custom_name, p.big_unit, p.small_unit, p.convert_relate, w.code as warehouseCode ");
		sqlBuilder.append(" from `cc_sales_order_detail` sod ");
		sqlBuilder.append(" LEFT JOIN cc_seller_product sp ON sod.sell_product_id = sp.id ");
		sqlBuilder.append(" LEFT JOIN cc_product p ON sp.product_id = p.id ");
		sqlBuilder.append(" LEFT JOIN cc_warehouse w ON sod.warehouse_id = w.id ");
		sqlBuilder.append(" WHERE order_id = ? ");
		sqlBuilder.append(" ORDER BY sod.warehouse_id ");

		return Db.find(sqlBuilder.toString(), orderId);
	}

	public boolean insert(Map<String, String[]> paraMap, String orderId, String sellerId, String userId, Date date,
			String deptId, String dataArea, int index) {

		DAO.set("id", StrKit.getRandomUUID());
		DAO.set("order_id", orderId);
		DAO.set("sell_product_id", StringUtils.getArrayFirst(paraMap.get("sellProductId" + index)));

		String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
		// String warehouseId = this.getWarehouseId(productId, sellerId);TODO
		// 库存盘点写入库存总账未完成
		String warehouseId = "1";
		DAO.set("warehouse_id", warehouseId);

		String convert = StringUtils.getArrayFirst(paraMap.get("convert" + index));
		String bigNum = StringUtils.getArrayFirst(paraMap.get("bigNum" + index));
		String smallNum = StringUtils.getArrayFirst(paraMap.get("smallNum" + index));

		Integer productCount = Integer.valueOf(bigNum) * Integer.valueOf(convert) + Integer.valueOf(smallNum);

		DAO.set("product_count", productCount);
		DAO.set("product_price", StringUtils.getArrayFirst(paraMap.get("smallPrice" + index)));

		DAO.set("product_amount", StringUtils.getArrayFirst(paraMap.get("rowTotal" + index)));
		DAO.set("is_gift", StringUtils.getArrayFirst(paraMap.get("isGift" + index)));
		DAO.set("create_date", date);
		DAO.set("dept_id", deptId);
		DAO.set("data_area", dataArea);
		return DAO.save();
	}
	
	private String getWarehouseId(String sellProductId) {

		StringBuilder defaultSqlBuilder = new StringBuilder(" select i.warehouse_id, i.balance_count ");
		defaultSqlBuilder.append(" from cc_inventory i ");
		defaultSqlBuilder.append(" JOIN cc_warehouse w ON i.warehouse_id = w.id ");
		defaultSqlBuilder.append(" WHERE w.is_default = 1 ");
		defaultSqlBuilder.append(" AND i.sell_product_id = ? ");

		Record defaultRecord = Db.findFirst(defaultSqlBuilder.toString(), sellProductId);
		Integer defaultCount = defaultRecord.getInt("balance_count");
		if (defaultCount > 0) {
			return defaultRecord.getStr("warehouse_id");
		}

		StringBuilder sqlBuilder = new StringBuilder(" select i.warehouse_id, i.balance_count ");
		sqlBuilder.append(" from cc_inventory i ");
		sqlBuilder.append(" WHERE i.sell_product_id = ? ");
		Record record = Db.findFirst(sqlBuilder.toString(), sellProductId);

		return record.getStr("warehouse_id");
	}

	public SalesOrderDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<SalesOrderDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_sales_order_detail` ");

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

}
