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

import org.ccloud.model.ActivityExecute;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ActivityExecuteQuery extends JBaseQuery { 

	protected static final ActivityExecute DAO = new ActivityExecute();
	private static final ActivityExecuteQuery QUERY = new ActivityExecuteQuery();

	public static ActivityExecuteQuery me() {
		return QUERY;
	}

	public ActivityExecute findById(final String id) {
		return DAO.findById(id);
	}

	public Page<ActivityExecute> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_activity_execute` ");

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

	public List<ActivityExecute> findbyActivityId(String activityId){
		String sql = "select * from cc_activity_execute where activity_id = '"+activityId+"' ORDER By order_list";
		return DAO.find(sql);
	}
	
	public List<ActivityExecute> findByCustomerVisitId(String customerVisitId){
		String sql = "SELECT a.* from cc_activity_execute a " + 
				" LEFT JOIN cc_activity_apply ca on ca.activity_id=a.activity_id " + 
				" LEFT JOIN cc_customer_visit sv on sv.active_apply_id = ca.id " + 
				" where sv.id = '"+customerVisitId+"' ORDER BY a.order_list";
		return DAO.find(sql);
	}
	
	public List<ActivityExecute> findbyActivityIdAndOrderList(String acitiviyId,String orderList){
		String sql = "select * from cc_activity_execute where activity_id = '"+acitiviyId+"' and order_list <= '"+orderList+"' ORDER By order_list";
		return DAO.find(sql);
	}
	
	public ActivityExecute _findbyActivityIdAndOrderList(String acitiviyId,String orderList){
		String sql = "select * from cc_activity_execute where activity_id = '"+acitiviyId+"' and order_list = '"+orderList+"'";
		return DAO.findFirst(sql);
	}

	public List<ActivityExecute> findbyActivityApplyId(String activityApplyId) {
		String sql = "select * from cc_activity_execute cc left join cc_activity_apply ca on cc.activity_id = ca.activity_id where ca.id = ? ";
		return DAO.find(sql, activityApplyId);
	}
	
	public ActivityExecute findActivityApplyIdAndOrderList(String activityApplyId,String orderList){
		String sql = "SELECT	ae.* FROM	cc_activity_execute ae "
				+ "LEFT JOIN cc_activity_apply caa ON caa.activity_id = ae.activity_id "
				+ "WHERE ae.order_list = ? AND caa.id = ?";
		return DAO.findFirst(sql, orderList,activityApplyId);
	}
}
