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

import org.ccloud.model.GoodsSpecification;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GoodsSpecificationQuery extends JBaseQuery { 

	protected static final GoodsSpecification DAO = new GoodsSpecification();
	private static final GoodsSpecificationQuery QUERY = new GoodsSpecificationQuery();

	public static GoodsSpecificationQuery me() {
		return QUERY;
	}

	public GoodsSpecification findById(final String id) {
		return DAO.findById(id);
	}

	public Page<GoodsSpecification> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_goods_specification` ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "name", keyword, params, true);
		
		fromBuilder.append("order by " + orderby);		

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<Record> paginateRecords(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "SELECT c.*, t1.valueName ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_goods_specification` c ");
		fromBuilder.append("LEFT JOIN (SELECT sv.goods_specification_id, GROUP_CONCAT(sv. NAME ORDER BY sv.order_list ) AS valueName ");
		fromBuilder.append("FROM cc_goods_specification_value sv GROUP BY sv.goods_specification_id ) t1 ");
		fromBuilder.append("ON c.id = t1.goods_specification_id ");
		
		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "c.name", keyword, params, true);
		
		fromBuilder.append("GROUP BY c.id ");
		
		fromBuilder.append("order by c." + orderby);		

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

	public List<GoodsSpecification> findAll() {
		return DAO.doFind();
	}

	
}