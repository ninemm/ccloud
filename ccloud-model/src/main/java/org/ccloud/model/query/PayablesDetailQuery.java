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

import org.ccloud.model.PayablesDetail;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PayablesDetailQuery extends JBaseQuery { 

	protected static final PayablesDetail DAO = new PayablesDetail();
	private static final PayablesDetailQuery QUERY = new PayablesDetailQuery();

	public static PayablesDetailQuery me() {
		return QUERY;
	}

	public PayablesDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public PayablesDetail findByObjId(String objId,String deptId,String refSn) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"' and ref_sn='"+refSn+"'";
		return DAO.findFirst(select);
	}
	
	public List<PayablesDetail> findByObjId(String objId,String deptId) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"'";
		return DAO.find(select);
	}
	
	public PayablesDetail findByRefSn(String objId,String deptId,String refSn) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"' and ref_sn ='"+refSn+"' ";
		return DAO.findFirst(select);
	}

	public Page<PayablesDetail> paginate(int pageNumber, int pageSize,String keyword,String deptId, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_payables_detail` where dept_id = '"+deptId+"'");

		LinkedList<Object> params = new LinkedList<Object>();
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<PayablesDetail> findByObjId(int pageNumber, int pageSize,String objId,String dataArea, String orderby) {
		String select = "select cc_d.*,if(cc_s.`status`>0,'已退货','待退货') ref_status ";
		StringBuilder fromBuilder = new StringBuilder("from cc_payables_detail cc_d inner join `cc_sales_refund_instock` cc_s on cc_d.ref_sn = cc_s.instock_sn and cc_s.payment_type = 0 ");
		fromBuilder.append("where cc_d.object_id = '"+objId+"' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "cc_d.data_area", dataArea, params, false);
		fromBuilder.append("order by " + orderby);
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
