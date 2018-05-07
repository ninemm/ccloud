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

import org.ccloud.Consts;
import org.ccloud.model.Payables;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PayablesQuery extends JBaseQuery { 

	protected static final Payables DAO = new Payables();
	private static final PayablesQuery QUERY = new PayablesQuery();

	public static PayablesQuery me() {
		return QUERY;
	}

	public Payables findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public Payables findByObjId(String objId, String objType) {
		String select = "select * from cc_payables where obj_id= '"+objId+"' and obj_type= '"+objType+"'";
		return DAO.findFirst(select);
	}
	
	public void updateAmountById(String id,String act_amount) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE `cc_payables` SET act_amount = act_amount +"+act_amount+" , balance_amount = balance_amount-"+act_amount+" WHERE id='"+id+"'");
		Db.update(sqlBuilder.toString());
	}
	
    public Page<Record> paginate(int pageNumber, int pageSize, String id,String userId,String dataArea,String sellerId,String deptId,String keyword) {
		Boolean b = true;
		String select;
		StringBuilder fromBuilder;
		LinkedList<Object> params = new LinkedList<Object>();
		if(id.equals("1")) {
			select = "SELECT CASE WHEN cs.id IS NOT NULL THEN cs.id ELSE s.id END AS id,CASE WHEN cs.`name` IS NOT NULL THEN cs.`name` ELSE s.seller_name END AS name,r.pay_amount,r.act_amount,r.balance_amount";
			fromBuilder = new StringBuilder(" FROM `cc_payables` AS r LEFT JOIN `cc_supplier` AS cs on r.obj_id=cs.id LEFT JOIN cc_seller s ON r.obj_id = s.id ");
			appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, b);if(!keyword.equals("")) {
			fromBuilder.append(" and CASE WHEN cs.`name` IS NOT NULL THEN cs.`name` ELSE s.seller_name END like '%"+keyword+"%' ");
			}
		}else {
			select = " SELECT  r.obj_id AS id  , t1.customerTypeNames,CASE WHEN cs.`name` IS NOT NULL THEN  cs.`name` WHEN c.customer_name IS NOT NULL THEN c.customer_name ELSE s.seller_name END AS name , r.pay_amount, r.act_amount, r.balance_amount  ";
			if(!("0".equals(id)) && id != null){
				fromBuilder = new StringBuilder(" FROM `cc_payables` AS r INNER JOIN (SELECT c1.id, c1.customer_id, ct.id AS customer_type_id, GROUP_CONCAT(ct.NAME) AS customerTypeNames FROM cc_seller_customer c1 INNER JOIN cc_customer_join_customer_type cjct ON c1.id =cjct.seller_customer_id INNER JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
				fromBuilder.append(" WHERE cjct.customer_type_id = '"+ id+"'");
				b = false;
			}else {
				fromBuilder = new StringBuilder(" FROM `cc_payables` AS r LEFT JOIN (SELECT c1.id, c1.customer_id, ct.id AS customer_type_id, GROUP_CONCAT(ct.NAME) AS customerTypeNames FROM cc_seller_customer c1 INNER JOIN cc_customer_join_customer_type cjct ON c1.id =cjct.seller_customer_id INNER JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
			}
			fromBuilder.append(" and c1.seller_id = ? GROUP BY c1.id ) t1 ON r.obj_id = t1.id LEFT JOIN `cc_customer` AS c ON c.id = t1.customer_id LEFT JOIN `cc_supplier` AS cs on r.obj_id=cs.id LEFT JOIN cc_seller s ON r.obj_id = s.id  ");
			params.add(sellerId);
			if(!("0".equals(id))) {
				fromBuilder.append(" where 1=1 ");
			}
			appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, b);
			if(!keyword.equals("")) {
				fromBuilder.append(" and CASE WHEN cs.`name` IS NOT NULL THEN  cs.`name` WHEN c.customer_name IS NOT NULL THEN c.customer_name ELSE s.seller_name END like '%"+keyword+"%' ");
			}
		}
		fromBuilder.append(" and r.dept_id = '"+deptId+"' ORDER BY r.create_date DESC");
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

	public boolean insert(Record record, Date date) {
		Payables payables = this.findByObjId(record.getStr("customer_id"), Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
		if (payables == null) {
			payables = new Payables();
			payables.setObjId(record.getStr("customer_id"));
			payables.setObjType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
			payables.setPayAmount(record.getBigDecimal("total_reject_amount"));
			payables.setActAmount(new BigDecimal(0));
			payables.setBalanceAmount(record.getBigDecimal("total_reject_amount"));
			payables.setDeptId(record.getStr("dept_id"));
			payables.setDataArea(record.getStr("data_area"));
			payables.setCreateDate(new Date());
		} else {
			payables.setPayAmount(payables.getPayAmount()
					.add(record.getBigDecimal("total_reject_amount")));
			payables.setBalanceAmount(payables.getBalanceAmount()
					.add(record.getBigDecimal("total_reject_amount")));
		}
		return payables.saveOrUpdate();
	}
	
	public Payables findByObjId(String objId) {
		String select = "select * from cc_payables where obj_id= '"+objId+"'";
		return DAO.findFirst(select);
	}
	
	public Payables findByObjIdAndDeptId(String objId, String objType) {
		String select = "select * from cc_payables where obj_id= '"+objId+"' and obj_type= '"+objType+"'";
		return DAO.findFirst(select);
	}
	
}
