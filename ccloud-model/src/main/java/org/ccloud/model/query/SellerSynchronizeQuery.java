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

import org.ccloud.model.SellerSynchronize;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerSynchronizeQuery extends JBaseQuery { 

	protected static final SellerSynchronize DAO = new SellerSynchronize();
	private static final SellerSynchronizeQuery QUERY = new SellerSynchronizeQuery();

	public static SellerSynchronizeQuery me() {
		return QUERY;
	}

	public SellerSynchronize findById(final String id) {
		return DAO.findById(id);
	}

	public Page<SellerSynchronize> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_synchronize` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<SellerSynchronize> paginateSynchronize(int pageNumber, int pageSize,String keyword) {
		String select = "select cs.* ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_synchronize` cs  ");
		LinkedList<Object> params = new LinkedList<Object>();
		
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cs.seller_name", keyword, params, true);

		
		if(needWhere) {
			fromBuilder.append("where cs.dept_id is null ");
		} else {
			fromBuilder.append(" and cs.dept_id is null ");
		}
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Page<SellerSynchronize> paginateSynchronize(int pageNumber, int pageSize,String keyword, String parentId, String deptFlag) {
		String select = "select cs.* ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_synchronize` cs  ");
		LinkedList<Object> params = new LinkedList<Object>();
		
		boolean needWhere = true;
		if(StrKit.isBlank(parentId)) {
			fromBuilder.append("where cs.parent_id is null ");
		} else {
			fromBuilder.append("where cs.parent_id = ? ");
			params.add(parentId);
		}
		needWhere = false;
		if(StrKit.notBlank(deptFlag)) {
			if("0".equals(deptFlag)) {
				fromBuilder.append("and cs.dept_id is null ");
			} else if("1".equals(deptFlag)) {
				fromBuilder.append("and cs.dept_id is not null ");
			}
		}
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cs.seller_name", keyword, params, needWhere);

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public SellerSynchronize findByCode(String sellerCode) {
		String sql = "select * from cc_seller_synchronize where seller_code = ? and parent_code is null";
		return DAO.findFirst(sql, sellerCode);
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
	
	public List<SellerSynchronize> findParentSellersByBrandCode(String brandCode) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select s.id, s.seller_code, s.seller_name, s.brand_code ");
		sqlBuilder.append("from cc_seller_synchronize s ");
		sqlBuilder.append("where s.brand_code = ? and s.parent_code is null");
		return DAO.find(sqlBuilder.toString(), brandCode);
	}

	public List<SellerSynchronize> findByParentCode(String parentCode) {
		return DAO.doFind("parent_code = ?", parentCode);
	}
}
