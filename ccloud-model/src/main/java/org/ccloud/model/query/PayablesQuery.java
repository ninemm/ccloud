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
import org.ccloud.model.Payables;

import com.jfinal.plugin.activerecord.Page;
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
	
	public Payables findByObjId(String objId,String deptId) {
		String select = "select * from cc_payables where obj_id= '"+objId+"' and dept_id= '"+deptId+"'";
		return DAO.findFirst(select);
	}
	
	public Page<Payables> paginate(int pageNumber, int pageSize,String keyword,String cutomerType,String dataArea, String orderby) {
		String select = "";
		StringBuilder fromBuilder = new StringBuilder("");
		LinkedList<Object> params = new LinkedList<Object>();
		if(cutomerType.equals("supplier")) {
			select = "select cc_p.*,cc_s.`code` customer_no,cc_s.`name` customer_name,'供应商' customer_type ";
			fromBuilder.append(" from cc_payables cc_p inner join cc_supplier cc_s on cc_p.obj_id = cc_s.id where cc_p.obj_type = 'supplier' ");
			 appendIfNotEmptyWithLike(fromBuilder,"cc_p.data_area",dataArea,params,false);
			 appendIfNotEmptyWithLike(fromBuilder, "cc_s.`name`", keyword, params, false);
		}else {
			select = "select cc_p.*,cc_c.customer_code customer_no,cc_c.customer_name customer_name,if(cc_s.customer_kind<>2,'普通客户','直营商') customer_type ";
			fromBuilder.append(" from cc_payables cc_p left join cc_seller_customer cc_s on cc_p.obj_id = cc_s.id left join cc_customer cc_c on cc_s.customer_id = cc_c.id where cc_p.obj_type = 'customer' ");
			 appendIfNotEmptyWithLike(fromBuilder, "cc_p.data_area", dataArea, params, false);
			 appendIfNotEmptyWithLike(fromBuilder, "cc_c.customer_name", keyword, params, false);
		}
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
