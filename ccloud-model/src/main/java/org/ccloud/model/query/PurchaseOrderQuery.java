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

import org.ccloud.model.PurchaseOrder;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PurchaseOrderQuery extends JBaseQuery { 

	protected static final PurchaseOrder DAO = new PurchaseOrder();
	private static final PurchaseOrderQuery QUERY = new PurchaseOrderQuery();

	public static PurchaseOrderQuery me() {
		return QUERY;
	}

	public PurchaseOrder findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<PurchaseOrder> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_purchase_order` ");

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

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate,String id) {
		String select =  "SELECT cpo.id, cpo.porder_sn,cs.`name` as supplier_name,user1.realname as biz_user,user2.realname as confirm_user,user3.realname as input_user,cpo.create_date,cpo.payment_type,cpo.confirm_date,cpo.`status` ";
		StringBuilder fromBuilder = new StringBuilder("FROM cc_purchase_order cpo ");
		fromBuilder.append("LEFT JOIN (SELECT b.realname,b.id FROM `user` b) user1 ON user1.id = cpo.biz_user_id LEFT JOIN (SELECT b.realname,b.id FROM `user` b) user2 ON user2.id = cpo.confirm_user_id LEFT JOIN (SELECT b.realname,b.id FROM `user` b) user3 ON user3.id = cpo.input_user_id LEFT JOIN cc_supplier cs on cs.id=cpo.supplier_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cpo.porder_sn", keyword, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" where 1 = 1");
		}

		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and cpo.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and cpo.create_date <= ?");
			params.add(endDate);
		}
		fromBuilder.append(" and user1.id='"+id+"' ");
		fromBuilder.append(" order by cpo.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public int findByUserId(String userId){
		String sql = "select c.* from cc_purchase_order c LEFT JOIN user u on c.dept_id=u.department_id where u.id=?";
		return DAO.find(sql, userId).size();
	}
	

}
