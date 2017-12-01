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
import org.ccloud.model.Receiving;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ReceivingQuery extends JBaseQuery { 

	protected static final Receiving DAO = new Receiving();
	private static final ReceivingQuery QUERY = new ReceivingQuery();

	public static ReceivingQuery me() {
		return QUERY;
	}

	public Receiving findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Receiving> paginate(int pageNumber, int pageSize, String ref_sn,String dataArea) {
		String select = "SELECT r.biz_date,r.act_amount,c.customer_name AS receive_user_name,r.create_date,u.realname AS input_user_name,r.remark ";
		StringBuilder fromBuilder = new StringBuilder("FROM `cc_receiving` AS r LEFT JOIN `cc_customer` AS c ON r.receive_user_id = c.id LEFT JOIN `user` AS u ON r.input_user_id = u.id ");
		fromBuilder.append(" WHERE r.ref_sn=\'"+ref_sn+"\' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "r.data_area", dataArea, params, false);
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

	
}
