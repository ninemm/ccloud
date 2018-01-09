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
import org.ccloud.model.Message;

import com.jfinal.core.Const;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class MessageQuery extends JBaseQuery { 

	protected static final Message DAO = new Message();
	private static final MessageQuery QUERY = new MessageQuery();

	public static MessageQuery me() {
		return QUERY;
	}

	public Message findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				StringBuilder sql = new StringBuilder("SELECT m.*, u.realname, d.`name`");
				sql.append(" FROM `cc_message` m");
				sql.append(" LEFT JOIN user u on m.from_user_id = u.id");
				sql.append(" LEFT JOIN dict d on m.type = d.`value`");
				sql.append(" WHERE m.id = ? limit 1");
				return DAO.findFirst(sql.toString(), id);
			}
		});
	}
	
	public Page<Message> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_message` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<Message> paginate(int pageNumber, int pageSize, String sellerId, String type, String fromUserId, 
			String toUserId, String orderby) {
		
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_message` ");
		
		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(fromBuilder, "seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "type", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "from_user_id", fromUserId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "to_user_id", toUserId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "is_read", Consts.NO_READ, params, needWhere);
		
		orderby = StrKit.isBlank(orderby) == true ? "create_date" : orderby;
		fromBuilder.append("order by " + orderby +" desc");
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<Record> paginate1(int pageNumber, int pageSize, String sellerId, String type, String fromUserId, 
			String toUserId, String orderby) {
		String select = "select m.*,t1.ID_ taskId,t1.ASSIGNEE_ assignee  ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_message` m");
		fromBuilder.append(" LEFT JOIN cc_sales_order o ON m.object_id=o.id ");
		fromBuilder.append(" LEFT JOIN (SELECT a.ID_ ,a.ASSIGNEE_, a.PROC_INST_ID_ FROM act_ru_task a) t1 on o.proc_inst_id = t1.PROC_INST_ID_ ");
		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();
		needWhere = appendIfNotEmpty(fromBuilder, "m.seller_id", sellerId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "m.type", type, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "m.from_user_id", fromUserId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "m.to_user_id", toUserId, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "m.is_read", Consts.NO_READ, params, needWhere);
		
		fromBuilder.append("order by m.create_date desc");
		
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

	public Message findByObjectIdAndToUserId(String orderId, String id) {
		String sql="SELECT * FROM cc_message WHERE object_id='"+orderId+"' And to_user_id='"+id+"' And is_read=0";
		return DAO.findFirst(sql.toString());
	}


	
}
