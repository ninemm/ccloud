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
import org.ccloud.model.CustomerVisit;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class CustomerVisitQuery extends JBaseQuery { 

	protected static final CustomerVisit DAO = new CustomerVisit();
	private static final CustomerVisitQuery QUERY = new CustomerVisitQuery();

	public static CustomerVisitQuery me() {
		return QUERY;
	}

	public CustomerVisit findById(final String id) {
				return DAO.findById(id);
	}

	public Page<CustomerVisit> paginate(int pageNumber, int pageSize, String keyword, String dataArea,String customerType,String questionType, String orderby) {
		
		String select = "select cc_v.*,cc.customer_name,(select realname from `user` where id = cc_v.user_id) visit_user,u.realname review_user,cc_s.customer_type_ids customer_type ";
		boolean needWhere = true;
		StringBuilder fromBuilder = new StringBuilder("from cc_customer_visit cc_v left join cc_seller_customer cc_s on cc_v.seller_customer_id = cc_s.id left join cc_customer cc on cc_s.customer_id = cc.id left join `user` u on u.id = cc_v.review_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc_v.data_area", dataArea, params, needWhere);
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cc.customer_name", keyword, params, needWhere);
		if(!customerType.equals("0")) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_s.customer_type_ids", customerType, params, needWhere);
		}
		if(!questionType.equals("0")) {
			needWhere = appendIfNotEmpty(fromBuilder, "cc_v.question_type", questionType, params, needWhere);
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
