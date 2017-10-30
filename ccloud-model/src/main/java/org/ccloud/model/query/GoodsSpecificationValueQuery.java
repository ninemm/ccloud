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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.GoodsSpecificationValue;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GoodsSpecificationValueQuery extends JBaseQuery { 

	protected static final GoodsSpecificationValue DAO = new GoodsSpecificationValue();
	private static final GoodsSpecificationValueQuery QUERY = new GoodsSpecificationValueQuery();

	public static GoodsSpecificationValueQuery me() {
		return QUERY;
	}

	public GoodsSpecificationValue findById(final String id) {
		return DAO.findById(id);
	}

	public Page<GoodsSpecificationValue> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select cc.*, cs.name as specification_name ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_goods_specification_value` cc ");
		fromBuilder.append("join `cc_goods_specification` cs on cs.id = cc.goods_specification_id ");
		
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

	public List<GoodsSpecificationValue> findByParentId(String id) {
		return DAO.doFind("goods_specification_id = ? order by order_list", id);
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
	
	public int batchDeleteAndFile(List<GoodsSpecificationValue> ids) {
		if (ids != null && ids.size() > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.size(); i++) {
				if (StringUtils.isNotBlank(ids.get(i).getImagePath())) {
					File file1 = new File(PathKit.getWebRootPath()+ids.get(i).getImagePath());
					if (file1.exists() && file1.isFile()) {
						file1.delete();
					}
				}
				if (DAO.deleteById(ids.get(i).getId())) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public List<GoodsSpecificationValue> findByProductId(String productSn) {
 		StringBuilder fromBuilder = new StringBuilder("SELECT c.* ");
		fromBuilder.append("FROM cc_goods_specification_value c ");
		fromBuilder.append("LEFT JOIN cc_product_goods_specification_value s ON s.goods_specification_value_set_id = c.id ");
		fromBuilder.append("LEFT JOIN cc_product p ON p.id = s.product_set_id ");
		fromBuilder.append("where p.id = ?");
		return DAO.find(fromBuilder.toString(), productSn);
	}	
	
}
