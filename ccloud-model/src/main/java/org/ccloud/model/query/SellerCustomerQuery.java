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

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ccloud.model.SellerCustomer;
import org.ccloud.model.callback.CustomerNearbyCallback;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerCustomerQuery extends JBaseQuery {

	protected static final SellerCustomer DAO = new SellerCustomer();
	private static final SellerCustomerQuery QUERY = new SellerCustomerQuery();

	public static SellerCustomerQuery me() {
		return QUERY;
	}

	public SellerCustomer findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				StringBuilder sql = new StringBuilder(
						" select sc.*, c.customer_code, c.customer_name, c.contact, c.mobile");
				sql.append(" , c.prov_name, c.city_name, c.country_name");
				sql.append(" , c.prov_code, c.city_code, c.country_code, c.address");
				sql.append(" , GROUP_CONCAT(ct.id) as cTypeList, GROUP_CONCAT(ct.name) as cTypeName");

				sql.append(" from `cc_seller_customer` sc");
				sql.append(" join `cc_customer` c on c.id = sc.customer_id");
				sql.append(" LEFT JOIN cc_customer_join_customer_type cjct ON cjct.seller_customer_id = sc.id");
				sql.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id");

				sql.append(" WHERE sc.id = ? ");
				sql.append(" GROUP BY sc.id limit 1");
				return DAO.findFirst(sql.toString(), id);
			}
		});
	}

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String dataArea) {

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "select sc.*, c.customer_code, c.customer_name"
				+ ", c.contact, c.mobile, c.prov_name, c.city_name, c.country_name"
				+ ", c.prov_code, c.city_code, c.country_code, c.address" + ", t1.customerTypeNames, t2.realnames";

		StringBuilder fromBuilder = new StringBuilder(" from `cc_seller_customer` sc ");
		fromBuilder.append(" join `cc_customer` c on c.id = sc.customer_id ");

		fromBuilder.append(" LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		fromBuilder.append(" FROM cc_seller_customer c1 ");
		fromBuilder.append(" LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		fromBuilder.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		fromBuilder.append(" GROUP BY c1.id) t1 ON sc.id = t1.id ");

		fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		fromBuilder.append(" FROM cc_seller_customer c2 ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c2.id = ujc.seller_customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");

		appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, true);
		fromBuilder.append(" GROUP BY c2.id) t2 ON sc.id = t2.id ");

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", keyword, params, needWhere);

		fromBuilder.append("  GROUP BY sc.id ");
		fromBuilder.append(" order by sc.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String dataArea, String userId,
			String customerTypeId, String isOrdered, String customerKind) {

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "select sc.*, c.customer_code, c.customer_name"
				+ ", c.contact, c.mobile, c.prov_name, c.city_name, c.country_name"
				+ ", c.prov_code, c.city_code, c.country_code, c.address" + ", t1.customerTypeNames, t2.realnames";

		StringBuilder fromBuilder = new StringBuilder(" from `cc_seller_customer` sc ");
		fromBuilder.append(" join `cc_customer` c on c.id = sc.customer_id ");
		fromBuilder.append(" left join `cc_sales_order` so on sc.id = so.customer_id ");

		if(StrKit.isBlank(customerTypeId)) {
			fromBuilder.append(" LEFT ");
		}

		fromBuilder.append(" JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		fromBuilder.append(" FROM cc_seller_customer c1 ");
		fromBuilder.append(" JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		fromBuilder.append(" JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(fromBuilder, "ct.id", customerTypeId, params, true);
		fromBuilder.append(" GROUP BY c1.id) t1 ON sc.id = t1.id ");

		fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		fromBuilder.append(" FROM cc_seller_customer c2 ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c2.id = ujc.seller_customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");

		appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, true);
		appendIfNotEmpty(fromBuilder, "ujc.user_id", userId, params, false);
		fromBuilder.append(" GROUP BY c2.id) t2 ON sc.id = t2.id ");

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "c.customer_name", keyword, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "sc.customer_kind", customerKind, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" WHERE 1 = 1 ");
			needWhere = false;
		}
		if (StrKit.notBlank(isOrdered)) {
			if ("1".equals(isOrdered)) {

				fromBuilder.append(" AND EXISTS ");
			}

			if ("0".equals(isOrdered)) {

				fromBuilder.append(" AND NOT EXISTS ");
			}

			fromBuilder.append(" (SELECT 1 FROM cc_seller_customer csc  join `cc_sales_order` cso on csc.id = cso.customer_id where sc.id = csc.id group by csc.id)" );

		}

		fromBuilder.append(" GROUP BY sc.id ");
		fromBuilder.append(" order by sc.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public boolean enable(String id, int isEnabled) {
		SellerCustomer sellerCustomer = DAO.findById(id);
		sellerCustomer.set("is_enabled", isEnabled);

		return sellerCustomer.saveOrUpdate();
	}

	public String findsellerCustomerBycusId(String customerId, String dataArea) {

		StringBuilder fromBuilder = new StringBuilder(" select sc.id ");
		fromBuilder.append(" from cc_seller_customer sc ");
		fromBuilder.append(" join cc_user_join_customer ujc on sc.id = ujc.seller_customer_id ");
		fromBuilder.append(" where sc.customer_id = ? ");
		fromBuilder.append(" AND ujc.data_area like ? ");

		return Db.queryStr(fromBuilder.toString(), customerId, dataArea);
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

	public void findUserListAsTree() {

	}

	public Page<Record> findByUserTypeForApp(int pageNumber, int pageSize, String selectDataArea, String customerType,
			String isOrdered, String searchKey) {
		boolean needwhere = false;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "SELECT c.id,c.customer_name,c.contact,c.mobile,c.prov_name,c.city_name,c.country_name,c.address,c.sellerCustomerId,c.image_list_store ";
		StringBuilder sql = new StringBuilder(
				"FROM (SELECT c.id,c.customer_name,c.contact,c.mobile,c.prov_name,c.city_name,c.country_name,c.address,csc.id as sellerCustomerId,csc.image_list_store  FROM cc_user_join_customer cujc ");
		sql.append(
				"LEFT JOIN cc_customer_join_customer_type ccjct ON cujc.seller_customer_id = ccjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_seller_customer csc ON cujc.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer c ON csc.customer_id = c.id ");
		sql.append("LEFT JOIN cc_sales_order cso ON cujc.seller_customer_id = cso.customer_id AND cujc.data_area = cso.data_area ");

		if (StrKit.notBlank(searchKey)) {
			sql.append("WHERE ( c.customer_name LIKE ? OR c.contact LIKE ? ) ");
			if (searchKey.contains("%")) {
				params.add(searchKey);
				params.add(searchKey);
			} else {
				params.add("%" + searchKey + "%");
				params.add("%" + searchKey + "%");
			}
		} else {
			sql.append("WHERE c.customer_name is not null ");
			needwhere = false;
		}

		needwhere = appendIfNotEmptyWithLike(sql, "cujc.data_area", selectDataArea, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "csc.is_enabled", 1, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "ccjct.customer_type_id", customerType, params, needwhere);

		sql.append("GROUP BY c.id ");

		if (StrKit.notBlank(isOrdered)) {

			if (isOrdered.equals("1"))
				sql.append("HAVING count(DISTINCT(cso.order_sn)) > 0 ");
			if (isOrdered.equals("0"))
				sql.append("HAVING count(DISTINCT(cso.order_sn)) = 0 ");
		}

		sql.append(") AS c");
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());

	}
	
	public List<SellerCustomer> getToDo(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT sc.*, c.customer_name, c.customer_code, c.contact, c.mobile, c.address, c.prov_name, c.city_name, c.country_name,");
		sb.append(" a.ID_ taskId, a.NAME_ taskName, a.ASSIGNEE_ assignee, a.CREATE_TIME_ createTime, group_concat(ct.name) as customerType");
		sb.append(" FROM cc_seller_customer sc");
		sb.append(" JOIN cc_customer c on sc.customer_id = c.id");

		sb.append(" LEFT JOIN cc_customer_join_customer_type cjct ON cjct.seller_customer_id = sc.id ");
		sb.append(" LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");

		sb.append(" JOIN act_ru_task a on sc.proc_inst_id = a.PROC_INST_ID_");
		sb.append(" JOIN act_ru_identitylink u on sc.proc_inst_id = u.PROC_INST_ID_");
		sb.append(" where locate(?, u.USER_ID_) > 0 and c.is_enabled = 1");
		sb.append(" GROUP BY sc.id");
		return DAO.find(sb.toString(), username);
	}
	
	public Page<Record> getHisProcessList(int pageNumber, int pageSize, String procKey, String username) {
		
		String select = "SELECT c.*,cc.customer_name, cc.mobile, cc.contact, cc.prov_name, cc.city_name, cc.country_name, cc.address, group_concat(ct.name) as customerType  ";
		LinkedList<Object> params = new LinkedList<>();
		params.add(procKey);
		params.add(username);

		StringBuilder sql = new StringBuilder("FROM cc_seller_customer c ");
		sql.append("LEFT JOIN cc_customer cc ON c.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON cjct.seller_customer_id = c.id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");

		sql.append("LEFT JOIN act_hi_actinst i on c.proc_inst_id = i.PROC_INST_ID_ ");
		sql.append("LEFT JOIN act_re_procdef p on p.ID_ = i.PROC_DEF_ID_ ");
		sql.append("WHERE p.KEY_ = ? and locate(?, ASSIGNEE_) > 0 AND i.DURATION_ is not null ");
		sql.append("group by c.id ");


		return Db.paginate(pageNumber, pageSize, true, select, sql.toString(), params.toArray());
	}
	
	public SellerCustomer findBySellerId(String sellerId ,String customerId){
		return DAO.doFindFirst("seller_id = ? and customer_id = ?", sellerId,customerId);
	}

	public List<Record> findSubTypeByUserID(String dataArea) {
		List<Object> param = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("select DISTINCT(sub_type) ");
		sql.append(" FROM cc_seller_customer ");
		appendIfNotEmptyWithLike(sql, "data_area", dataArea, param, true );
		return Db.find(sql.toString(), param);
	}
	
	public Long findTotalCountByDataArea(String dataArea) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT(c.id)) ");
		sql.append("FROM `cc_user_join_customer` uc ");
		sql.append("JOIN cc_seller_customer c on c.id = uc.seller_customer_id ");
		sql.append("WHERE c.is_enabled = 1 AND uc.data_area like ?");
		return Db.queryLong(sql.toString(), dataArea);
	}
	
	public List<Map<String, Object>> queryCustomerNearby(double nearby,BigDecimal lng,BigDecimal lat,String userId){

		CustomerNearbyCallback callback = new CustomerNearbyCallback();
		callback.setLon(lng);
		callback.setLat(lat);
		callback.setUserId(userId);
		callback.setDist(nearby);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) Db.execute(callback);
		return result;
	}

	public List<Record> findName(String dataArea) {
		List<Object> param = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("SELECT DISTINCT(csc.id), cc.customer_name as name ");
		sql.append("FROM cc_seller_customer csc LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_user_join_customer cujc ON cujc.seller_customer_id = csc.id ");
		appendIfNotEmptyWithLike(sql, "cujc.data_area", dataArea, param, true);
		return Db.find(sql.toString(), param.toArray());
	}
}
