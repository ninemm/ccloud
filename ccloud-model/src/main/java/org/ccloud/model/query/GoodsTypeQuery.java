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

import org.ccloud.model.GoodsType;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GoodsTypeQuery extends JBaseQuery { 

	protected static final GoodsType DAO = new GoodsType();
	private static final GoodsTypeQuery QUERY = new GoodsTypeQuery();

	public static GoodsTypeQuery me() {
		return QUERY;
	}

	public GoodsType findById(final String id) {
		return DAO.findById(id);
	}

	public Page<GoodsType> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_goods_type` ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "name", keyword, params, true);
		
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

	public List<GoodsType> findAll() {
		return DAO.doFind();
	}
	
	public List<GoodsType> findProductType(String dataArea){
		StringBuilder sqlBuilder = new StringBuilder("select distinct cc_gt.id,cc_gt.`name` ");
		sqlBuilder.append("from cc_inventory cc_i left join cc_product cc_p on cc_i.product_id = cc_p.id left join cc_goods cc_g on cc_p.goods_id = cc_g.id left join cc_goods_type cc_gt on cc_g.goods_type_id = cc_gt.id ");
		sqlBuilder.append("where cc_i.data_area like '"+dataArea+"'");
		return DAO.find(sqlBuilder.toString());
	}

	
}
