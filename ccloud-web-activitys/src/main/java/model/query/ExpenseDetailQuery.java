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
package model.query;

import java.util.LinkedList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.ExpenseDetail;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ExpenseDetailQuery extends JBaseQuery { 

	protected static final ExpenseDetail DAO = new ExpenseDetail();
	private static final ExpenseDetailQuery QUERY = new ExpenseDetailQuery();

	public static ExpenseDetailQuery me() {
		return QUERY;
	}

	public ExpenseDetail findById(final String id) {
		String sql = "select c.*,d.`name` as expenseDetailName from cc_expense_detail c LEFT JOIN dict d on d.`value` = c.item1 where c.id = '"+id+"'";
				return DAO.findFirst(sql);
	}

	public Page<ExpenseDetail> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_expense_detail` ");

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

	public List<ExpenseDetail> findByActivityId(String id) {
		return DAO.doFind("activity_id = ? and state = 1", id);
	}

	public int batchDelete(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.size(); i++) {
				if (DAO.deleteById(ids.get(i))) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public ExpenseDetail findSurplusById(String id) {
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder builder = new StringBuilder("SELECT ce.*, IFNULL(t1.num,0) as num, IFNULL(t1.amount,0) as amount FROM cc_expense_detail ce ");
		builder.append("LEFT JOIN (SELECT ca.expense_detail_id,SUM(ca.apply_amount) as amount, SUM(ca.apply_num) as num FROM cc_activity_apply ca ");
		builder.append("WHERE ca.status != ? ");
		params.add(Consts.ACTIVITY_APPLY_STATUS_REJECT);
		builder.append("GROUP BY ca.expense_detail_id) t1 ON t1.expense_detail_id = ce.id ");
		builder.append("where id = ? ");
		params.add(id);
		return DAO.findFirst(builder.toString(), params.toArray());
	}

	public ExpenseDetail _findById(String id) {
		String sql = "select c.*,d.`name` as expenseDetailName,cs.custom_name,CASE WHEN cs.custom_name IS NOT NULL THEN cs.custom_name ELSE t1.name END AS expenseDetailName1 from cc_expense_detail c "
				+ "LEFT JOIN dict d on d.`value` = c.item1 "
				+ "LEFT JOIN cc_seller_product cs on cs.id = c.item2 "
				+ "LEFT JOIN (select ce.id,di.`name` from cc_expense_detail ce LEFT JOIN dict di on di.`value` = ce.item2) t1 on t1.id = c.id "
				+ " where c.id = '"+id+"'";
		return DAO.findFirst(sql);
	}

	public ExpenseDetail findByActivityApplyId(String activityApplyId) {
		StringBuilder builder = new StringBuilder("SELECT * FROM cc_expense_detail cc LEFT JOIN cc_activity_apply ca ON ca.expense_detail_id = cc.id ");
		builder.append(" WHERE ca.id = ?");
		return DAO.findFirst(builder.toString(), activityApplyId);
	}
	
	public ExpenseDetail findActivityApplyId(String activityApplyId) {
		String sql = "select c.* from cc_expense_detail c LEFT JOIN cc_activity_apply a on a.expense_detail_id = c.id where a.id = ?";
		return DAO.findFirst(sql, activityApplyId);
	}
	
}
