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

import org.ccloud.model.SellerJoinTemplate;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerJoinTemplateQuery extends JBaseQuery { 

	protected static final SellerJoinTemplate DAO = new SellerJoinTemplate();
	private static final SellerJoinTemplateQuery QUERY = new SellerJoinTemplateQuery();

	public static SellerJoinTemplateQuery me() {
		return QUERY;
	}

	public SellerJoinTemplate findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<SellerJoinTemplate> paginate(int pageNumber, int pageSize,String keyword, String orderby,String sellerId) {
		String select = "SELECT sj.*,cs.seller_name as seller_name,cp.template_name as template_name ";
		StringBuilder fromBuilder = new StringBuilder("from cc_seller_join_template sj LEFT JOIN cc_seller cs on sj.seller_id=cs.id LEFT JOIN cc_print_template cp on cp.id= sj.print_template_id ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "seller_name", keyword, params, true);
		if(keyword.equals("")){
			fromBuilder.append("where seller_id = '"+sellerId+"'");
		}else {
			fromBuilder.append("and seller_id = '"+sellerId+"'");
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

	public SellerJoinTemplate findByTemplateId(String printTemplateId,String sellerId){
		String sql = "select * from cc_seller_join_template where print_template_id = '"+printTemplateId+"' and seller_id = '"+sellerId+"'";
		return DAO.findFirst(sql);
	}
	
	public SellerJoinTemplate findAllById(String id){
		String sql = "SELECT sj.*,cs.seller_name as seller_name,cp.template_name as template_name from cc_seller_join_template sj "
				+ "LEFT JOIN cc_seller cs on sj.seller_id=cs.id LEFT JOIN cc_print_template cp on cp.id= sj.print_template_id "
				+ "where sj.id = ?";
		return DAO.findFirst(sql, id);
	}
}
