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

import org.ccloud.Consts;
import org.ccloud.model.Receivables;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ReceivablesQuery extends JBaseQuery { 

	protected static final Receivables DAO = new Receivables();
	private static final ReceivablesQuery QUERY = new ReceivablesQuery();

	public static ReceivablesQuery me() {
		return QUERY;
	}

	public Receivables findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String id,String seller_id,String dataArea,String sellerId,String keyword, String startDate, String endDate) {
		
		Boolean b = true;
		String select;
		StringBuilder fromBuilder;
		LinkedList<Object> params = new LinkedList<Object>();
		select = " SELECT r.object_id AS id, t1.customerTypeNames, c.customer_name AS name, sum(rd.receive_amount) receive_amount , sum(rd.act_amount) act_amount , sum(rd.balance_amount) balance_amount ";
		fromBuilder = new StringBuilder(" FROM `cc_receivables` AS r inner JOIN (SELECT c1.id, c1.customer_id,ct.id as customer_type_id, GROUP_CONCAT(ct. NAME) AS customerTypeNames FROM cc_seller_customer c1 inner JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id inner JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		if(!("0".equals(id)) && id != null){
			fromBuilder.append(" WHERE cjct.customer_type_id = '"+ id+"'");
		}
		fromBuilder.append(" WHERE c1.seller_id = ? GROUP BY c1.id ) t1 ON r.object_id = t1.id ");
		params.add(sellerId);
		fromBuilder.append(" inner JOIN `cc_customer` AS c ON c.id = t1.customer_id ");
		fromBuilder.append(" JOIN cc_receivables_detail rd ON rd.object_id=r.object_id ");
		appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, b);
		if(!keyword.equals("")) {
			fromBuilder.append(" and c.customer_name like '%"+keyword+"%' ");
		}
		fromBuilder.append(" and r.create_date >= ?");
		params.add(startDate+" 00:00:00");
		fromBuilder.append(" and r.create_date <= ?");
		params.add(endDate+" 23:59:59");
		fromBuilder.append(" GROUP BY rd.object_id ORDER BY r.create_date DESC");
		
		
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

	public int updateAmountById(String id,String act_amount) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE `cc_receivables` SET act_amount = act_amount +"+act_amount+" , balance_amount = balance_amount-"+act_amount+" WHERE id='"+id+"'");
		return Db.update(sqlBuilder.toString());
	}
	
	public Receivables findByObjId(String objId,String object_type) {
		String select = "select * from `cc_receivables` where object_id= '"+objId+"' and object_type= '"+object_type+"' ";
		return DAO.findFirst(select);
	}

	public Receivables findByCustomerId(String customeId) {
		String receivableType = Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER; 
		return DAO.doFindFirst("object_id = ? and object_type = ?", customeId, receivableType);
	}

	public Receivables findBySn(String outstock_sn) {;
		String select = "SELECT cc.* FROM cc_receivables cc LEFT JOIN cc_receivables_detail cr on cc.object_id = cr.object_id where cr.ref_sn = ? ";
		return DAO.findFirst(select, outstock_sn);
	}

	public List<Record> findByDataArea(String dataArea) {
		StringBuilder fromBuilder = new StringBuilder("SELECT cc.customer_name , cr.receive_amount , cr.act_amount , cr.balance_amount ");
		fromBuilder.append("FROM cc_receivables cr ");
		fromBuilder.append("LEFT JOIN cc_seller_customer csc ON csc.id = cr.object_id ");
		fromBuilder.append("LEFT JOIN cc_customer cc ON cc.id = csc.customer_id ");
		fromBuilder.append("WHERE cr.object_type = 'customer' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "cr.data_area", dataArea, params, false);
		if (params.isEmpty())
			return Db.find(fromBuilder.toString());

		return Db.find(fromBuilder.toString(), params.toArray());
	}
}
