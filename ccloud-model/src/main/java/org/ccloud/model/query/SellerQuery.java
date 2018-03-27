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

import org.ccloud.model.Seller;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerQuery extends JBaseQuery { 

	protected static final Seller DAO = new Seller();
	private static final SellerQuery QUERY = new SellerQuery();

	public static SellerQuery me() {
		return QUERY;
	}

	public Seller findById(final String id) {
		
		StringBuilder sqlBuilder = new StringBuilder("select cc.*, d.dept_name as parent_name ");
		sqlBuilder.append("from `cc_seller` cc ");
		sqlBuilder.append("join `department` d on d.id = cc.dept_id ");
		sqlBuilder.append("where cc.id = ?");
		return DAO.findFirst(sqlBuilder.toString(), id);
		//return DAO.findById(id);
	}
	
	public int deleteByCcSellerId(String sellerId) {
		return DAO.doDelete("id = ?", sellerId);
	}
	
	public Page<Seller> paginate(int pageNumber, int pageSize,String keyword, String orderby,String username,String child,String sellerId) {
		String select = "select DISTINCT cs.* ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller` cs LEFT JOIN user u on u.department_id =cs.dept_id ");
		fromBuilder.append(" LEFT JOIN department d on d.id=cs.dept_id ");
		LinkedList<Object> params = new LinkedList<Object>();
		
		boolean needWhere = true;
		needWhere = appendIfNotEmptyWithLike(fromBuilder, "cs.seller_name", keyword, params, true);

		
		if(needWhere) {
			fromBuilder.append("where 1=1 ");
		}
/*		if(!username.equals("admin")){
			fromBuilder.append("and cs.seller_type =1 and cs.dept_id in ("+child+")  ");
		}else {
			fromBuilder.append("and cs.seller_type =0 ");
		}
*/		if(StrKit.notBlank(sellerId)) {
			fromBuilder.append(" and cs.dept_id in ("+child+") and cs.id not in ('"+sellerId+"')  ");
		}
		fromBuilder.append("  GROUP BY cs.id order by " + orderby);	
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

	public List<Seller> querySellIdByDept(String departmentId) {
		return DAO.doFind("dept_id = ? and is_enabled = 1", departmentId);
	}
	
	public List<Record> querySellerIdByDept(String departmentId) {
		String sql = "SELECT id as sellerId, seller_code as sellerCode, seller_name as sellerName FROM cc_seller WHERE dept_id = ? and is_enabled = 1";
		return Db.find(sql, departmentId);
	}
	
	public List<Record> queryParentSellerIdByDept(String departmentId) {
		String sql = "SELECT d.parent_id, s.id AS sellerId, seller_code as sellerCode, seller_name as sellerName FROM department d LEFT JOIN cc_seller s ON d.parent_id = s.dept_id and s.is_enabled = 1 WHERE d.id = ?";
		return Db.find(sql, departmentId);
	}
	
	public List<Seller> findSellerListByUser(String userId) {
		String sql = "select cs.* from cc_seller cs LEFT JOIN `user` u on u.department_id = cs.dept_id "
				+ "WHERE u.id =? and cs.seller_type=1";

		return DAO.find(sql, userId);
	}
	
	public Seller findByUserId(String userId){
		String sql = "select cs.* from cc_seller cs LEFT JOIN user u on u.department_id=cs.dept_id where u.id =? and cs.seller_type=0";
		return DAO.findFirst(sql, userId);
	}
	
	public Seller findByDeptAndSellerType(String deptId,int sellerType){
		String sql = "select * from cc_seller where dept_id = '"+deptId+"' and seller_type ="+sellerType;
		return DAO.findFirst(sql);
	}
	
	public List<Seller> findAllByUserId(String userId){
		String sql = "select cs.* from cc_seller cs LEFT JOIN user u on u.department_id=cs.dept_id where u.id =?";
		return DAO.find(sql, userId);
	}
	

	public List<Seller> findAll(){
		String sql = "select * from cc_seller";
		return DAO.find(sql);
	}
	
	public List<Seller> findSellerRegion(String dataArea){
		StringBuilder sqlBuilder = new StringBuilder("select c_s.id,c_s.seller_name from department d inner join cc_seller c_s on d.id = c_s.dept_id where d.data_area like '"+dataArea+"'");
		sqlBuilder.append("order by order_list,dept_level ");
		return DAO.find(sqlBuilder.toString());

	}
	
	public Seller findByDeptId(String deptId){
		String sql="SELECT * from cc_seller where dept_id= ? and seller_type = 0";
		return DAO.findFirst(sql,deptId);
	}
	
	public Record findByCustomerId(final String customerId) {
		
		return Db.findFirst("select s.* , d.data_area from cc_seller s join department d on s.dept_id = d.id where s.customer_id = ?" , customerId);
	}

	public List<Seller> findByDataArea(String dataArea) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT cs.* FROM `cc_seller` cs LEFT JOIN department d ON d.id = cs.dept_id WHERE d.data_area LIKE '"+dataArea+"'");
		sqlBuilder.append(" GROUP BY cs.id ORDER BY cs.seller_type");
		return DAO.find(sqlBuilder.toString());
	}

	public Seller findBySellerCustomerId(String customerId) {
		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM cc_seller cs  LEFT JOIN cc_seller_customer csc ON csc.seller_id=cs.id WHERE csc.id='"+customerId+"'");
		return DAO.findFirst(sqlBuilder.toString());
	}
	
	public Seller findbyCode(String sellerCode) {
		String sql = "select * from cc_seller where seller_code = ?";
		return DAO.findFirst(sql, sellerCode);
	}
	
	public Seller _findByDeptId(String deptId){
		String sql="SELECT * from cc_seller where dept_id= ?";
		return DAO.findFirst(sql,deptId);
	}

	public Seller _findByDataArea(String dataArea){
		String sql="SELECT s.* from cc_seller s join department d on s.dept_id = d.id where data_area = ?";
		return DAO.findFirst(sql,dataArea);
	}
}
