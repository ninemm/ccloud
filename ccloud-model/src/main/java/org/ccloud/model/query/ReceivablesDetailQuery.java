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
import org.ccloud.model.ReceivablesDetail;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ReceivablesDetailQuery extends JBaseQuery { 

	protected static final ReceivablesDetail DAO = new ReceivablesDetail();
	private static final ReceivablesDetailQuery QUERY = new ReceivablesDetailQuery();

	public static ReceivablesDetailQuery me() {
		return QUERY;
	}

	public ReceivablesDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<ReceivablesDetail> paginate(int pageNumber, int pageSize, String id,String type,String dataArea) {
		
		String select = "SELECT COALESCE(SUM(t3.act_amount),0) as act_amount, t3.ref_sn, t3.receive_amount,(t3.receive_amount) - COALESCE(SUM(t3.act_amount),0) as balance_amount, t3.object_id, t3.ref_type, t3.create_date, t3.biz_date ";
		StringBuilder fromBuilder = new StringBuilder(" FROM (SELECT r.act_amount AS act_amount, t2.ref_sn, t2.receive_amount AS receive_amount, t2.receive_amount - r.act_amount AS ");
		fromBuilder.append("balance_amount, t2.object_id, t2.ref_type, t2.create_date, t2.biz_date FROM cc_receiving r RIGHT JOIN (SELECT SUM(receive_amount) AS receive_amount, object_id, ref_type, create_date, biz_date, ref_sn FROM `cc_receivables_detail` c ");
		fromBuilder.append(" WHERE c.object_id ='"+id+"'");
		if("1".equals(type)) {
			fromBuilder.append(" AND c.object_type = 'customer' ");
		}else {
			fromBuilder.append(" AND c.object_type = 'supplier' ");
		}
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "data_area", dataArea, params, false);
		fromBuilder.append(" GROUP BY c.ref_sn  ORDER BY c.create_date DESC ) t2 ON r.ref_sn = t2.ref_sn ) t3 GROUP BY t3.ref_sn");
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
	
	public void updateAmountByRefSn(String ref_sn,String act_amount) {
		StringBuilder sqlBuilder = new StringBuilder("UPDATE `cc_receivables_detail` SET act_amount = act_amount + "+act_amount+",balance_amount = balance_amount -"+act_amount+" WHERE ref_sn='"+ref_sn+"'");
		Db.update(sqlBuilder.toString());
	}
	
	public ReceivablesDetail findByRefSn(String objId,String deptId,String refSn) {
		String select = "select * from `cc_receivables_detail` where object_id= '"+objId+"' and dept_id='"+deptId+"' and ref_sn ='"+refSn+"' ";
		return DAO.findFirst(select);
	}
}
