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
import org.ccloud.model.Receivables;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
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

	public Page<Receivables> paginate(int pageNumber, int pageSize, String id,String type,String seller_id,String dataArea) {
		
		Boolean b = true;
		String select;
		StringBuilder fromBuilder;
		
		if("1".equals(type)) {
			select = "SELECT r.object_id AS id,c.customer_code AS code,c.customer_name AS name,r.receive_amount,r.act_amount,r.balance_amount";
			fromBuilder = new StringBuilder(" FROM `cc_receivables` AS r INNER JOIN `cc_customer_join_customer_type` AS ct ON r.object_id=ct.seller_customer_id LEFT JOIN `cc_seller_customer` AS sc ON sc.id=ct.seller_customer_id LEFT JOIN `cc_customer` AS c ON c.id=sc.customer_id ");
			if(!("0".equals(id)) && id != null){
				fromBuilder.append(" WHERE ct.customer_type_id = '"+ id+"'");
				b = false;
			}
			
		}else {
			select = "SELECT s.id,s.code,s.name,r.receive_amount,r.act_amount,r.balance_amount";
			fromBuilder = new StringBuilder(" FROM `cc_receivables` AS r INNER JOIN `cc_supplier` AS s on r.object_id=s.id ");
			if(!"0".equals(id)){
				fromBuilder.append("WHERE s.id = '"+ id+"'");
				b = false;
			}
		}
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, b);
		fromBuilder.append(" ORDER BY r.create_date DESC");
		
		
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

	public void updateAmountById(String id,String act_amount) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE `cc_receivables` SET act_amount = act_amount +"+act_amount+" , balance_amount = balance_amount-"+act_amount+" WHERE id='"+id+"'");
		Db.update(sqlBuilder.toString());
	}
	
	public Receivables findByObjId(String objId,String deptId) {
		String select = "select * from `cc_receivables` where object_id= '"+objId+"' and dept_id= '"+deptId+"' ";
		return DAO.findFirst(select);
	}

	public Receivables findByCustomerId(String customeId) {
		String receivableType = Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER; 
		return DAO.doFindFirst("object_id = ? and object_type = ?", customeId, receivableType);
	}
}
