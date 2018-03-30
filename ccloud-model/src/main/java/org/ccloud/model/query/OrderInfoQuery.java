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
import org.ccloud.model.OrderInfo;
import org.ccloud.utils.DateUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class OrderInfoQuery extends JBaseQuery { 

	protected static final OrderInfo DAO = new OrderInfo();
	private static final OrderInfoQuery QUERY = new OrderInfoQuery();

	public static OrderInfoQuery me() {
		return QUERY;
	}

	public OrderInfo findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String startDate, String endDate, 
			String order, String sort, String platformName, String status, String receiveType, String sellerId) {
		String select = "SELECT o.order_sn, o.id, cc.customer_name, cs.seller_name, o.create_date, o.pay_date, o.delivery_date, o.receive_type, o.`status`, o.send_user_name, o.pay_amount,o.total_amount, o.platform_name ";
		StringBuilder fromBuilder = new StringBuilder("from cc_order_info o ");
		fromBuilder.append("LEFT JOIN cc_customer_info cc ON cc.id = o.customer_info_id ");
		fromBuilder.append("LEFT JOIN cc_seller_info cs on cs.id = o.seller_info_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.receive_type", receiveType, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.platform_name", platformName, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append("WHERE 1 = 1 ");
		}
		
		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (o.order_sn like '%" + keyword + "%' or cc.customer_name like '%" + keyword + "%')");
		}
		
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}
		
		if (StrKit.isBlank(sort)) {
			fromBuilder.append("order by o.create_date desc ");
		}else {
			fromBuilder.append("order by " + sort + " " + order);
		}

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

	public Record findMoreById(String orderId) {
		StringBuilder fromBuilder = new StringBuilder("select o.*, cc.customer_name, cs.seller_name, CONCAT(IFNULL(cc.prov_name,''),IFNULL(cc.city_name,''),IFNULL(cc.country_name,''),IFNULL(cc.address,'')) as address from cc_order_info o ");
		fromBuilder.append("LEFT JOIN cc_customer_info cc ON cc.id = o.customer_info_id ");
		fromBuilder.append("LEFT JOIN cc_seller_info cs on cs.id = o.seller_info_id ");
		fromBuilder.append("WHERE o.id = ? ");
		return Db.findFirst(fromBuilder.toString(), orderId);
	}

	public Record getCountInfo(String keyword, String startDate, String endDate, String platformName,
			String status, String receiveType, String dayTag, String sellerId) {
		if (StrKit.notBlank(dayTag)) {
			String[] date = DateUtils.getStartDateAndEndDateByType(dayTag);
			startDate = date[0];
			endDate = date[1];
		}
		StringBuilder fromBuilder = new StringBuilder("SELECT IFNULL(SUM(o.total_amount),0) as total_amount, IFNULL(SUM(o.pay_amount),0) as pay_amount, COUNT(*) as orderCount FROM cc_order_info o ");
		fromBuilder.append("LEFT JOIN cc_customer_info cc ON cc.id = o.customer_info_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		boolean needWhere = true;
		
		needWhere = appendIfNotEmpty(fromBuilder, "o.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.status", status, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.receive_type", receiveType, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "o.platform_name", platformName, params, needWhere);
		
		if (needWhere) {
			fromBuilder.append("WHERE 1 = 1 ");
		}
		
		if (StrKit.notBlank(keyword)) {
			needWhere = appendIfNotEmpty(fromBuilder, "o.order_sn", keyword, params, needWhere);
		}
		
		if (StrKit.notBlank(startDate)) {
			fromBuilder.append(" and o.create_date >= ?");
			params.add(startDate);
		}

		if (StrKit.notBlank(endDate)) {
			fromBuilder.append(" and o.create_date <= ?");
			params.add(endDate);
		}
		
		if (params.isEmpty())
			return Db.findFirst(fromBuilder.toString());

		return Db.findFirst(fromBuilder.toString(), params.toArray());
	}

	public boolean isExist(String orderSn) {
		OrderInfo info = DAO.doFindFirst("order_sn = ? ", orderSn);
		if (info != null) {
			return true;
		} else {
			return false;
		}
	}

	
}
