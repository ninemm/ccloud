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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;
import org.ccloud.Consts;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.callback.CustomerNearbyCallback;
import org.ccloud.model.callback.AroundCustomerBiUndevelopedCallback;
import org.ccloud.utils.DateUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	public Page<Record> paginate(int pageNumber, int pageSize, String keyword, String dataArea, String dealerDataArea, String sort,String sortOrder, String customerType) {

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
		appendIfNotEmptyWithLike(fromBuilder, "c1.data_area", dealerDataArea, params, true);
		fromBuilder.append(" GROUP BY c1.id) t1 ON sc.id = t1.id ");

		fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		fromBuilder.append(" FROM cc_seller_customer c2 ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c2.id = ujc.seller_customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");

		appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, true);
		fromBuilder.append(" GROUP BY c2.id) t2 ON sc.id = t2.id ");

		needWhere = appendIfNotEmptyWithLike(fromBuilder, "t1.customerTypeNames", customerType, params, needWhere);
		if (StrKit.notBlank(keyword))
			if ( needWhere ){
				fromBuilder.append(" WHERE c.customer_name LIKE ? OR t2.realnames LIKE ? ");
				params.add("%" + keyword + "%");
				params.add("%" + keyword + "%");
			} else {
				fromBuilder.append(" AND (c.customer_name LIKE ? OR t2.realnames LIKE ? ) ");
				params.add("%" + keyword + "%");
				params.add("%" + keyword + "%");
			}

		fromBuilder.append("  GROUP BY sc.id ");
		if(StrKit.notBlank(sort)) {
			fromBuilder.append(" order by "+sort);
			if(!sortOrder.equals("")) {
				fromBuilder.append(" "+ sortOrder);
			}
		}

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Page<Record> paginateForApp(int pageNumber, int pageSize, String keyword, String dataArea, String dealerDataArea, String userId,
			String customerTypeId, String isOrdered, String customerKind, String provName, String cityName, String countryName) {

		LinkedList<Object> params = new LinkedList<Object>();

		String select = "select sc.*, c.customer_code, c.customer_name"
				+ ", c.contact, c.mobile, c.prov_name, c.city_name, c.country_name"
				+ ", c.prov_code, c.city_code, c.country_code, c.address" + ", t1.customerTypeNames, t2.realnames";

		StringBuilder fromBuilder = new StringBuilder(" from `cc_seller_customer` sc ");
//		fromBuilder.append(" left join `cc_sales_order` so on sc.id = so.customer_id ");

		if(StrKit.isBlank(customerTypeId)) {
			fromBuilder.append(" LEFT ");
		}

		fromBuilder.append(" JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		fromBuilder.append(" FROM cc_seller_customer c1 ");
		fromBuilder.append(" JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		fromBuilder.append(" JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(fromBuilder, "c1.data_area", dealerDataArea, params, true);
		appendIfNotEmptyWithLike(fromBuilder, "ct.id", customerTypeId, params, false);
		fromBuilder.append(" GROUP BY c1.id) t1 ON sc.id = t1.id ");

		fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		fromBuilder.append(" FROM cc_seller_customer c2 ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c2.id = ujc.seller_customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");

		appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, true);
		appendIfNotEmpty(fromBuilder, "ujc.user_id", userId, params, false);
		fromBuilder.append(" GROUP BY c2.id) t2 ON sc.id = t2.id ");

		fromBuilder.append(" WHERE sc.is_enabled = 1 ");

		appendIfNotEmpty(fromBuilder, "sc.customer_kind", customerKind, params, false);
		appendIfNotEmpty(fromBuilder, "c.prov_name", provName, params, false);
		appendIfNotEmpty(fromBuilder, "c.city_name", cityName, params, false);
		appendIfNotEmpty(fromBuilder, "c.country_name", countryName, params, false);

		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (c.customer_name like '%" + keyword + "%' or c.contact like '%" + keyword + "%')");
		}

		if (StrKit.notBlank(isOrdered)) {

			if ("0".equals(isOrdered)) {
				fromBuilder.append(" AND EXISTS ");
			} else {
				fromBuilder.append(" AND NOT EXISTS ");
			}

			fromBuilder.append(" (SELECT DISTINCT csc.id FROM cc_seller_customer csc join `cc_sales_outstock` cso on csc.id = cso.customer_id where sc.id = csc.id ");
			Date date = null;
			DateTime dateTime = new DateTime(new Date());
			if ("2".equals(isOrdered)) {
				date = dateTime.plusWeeks(-1).toDate();
			} else if ("3".equals(isOrdered)) {
				date = dateTime.plusMonths(-1).toDate();
			} else if ("4".equals(isOrdered)) {
				date = dateTime.plusMonths(-3).toDate();
			} else if ("5".equals(isOrdered)) {
				date = dateTime.plusMonths(-6).toDate();
			}

			if (date != null) {
				fromBuilder.append(" and cso.create_date > ?");
				params.add(DateUtils.format(date));
			}

			fromBuilder.append(" group by csc.id)");

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

	public Page<Record> findByUserTypeForApp(int pageNumber, int pageSize, String selectDataArea, String dealerDataArea, String customerType,
			String isOrdered, String searchKey, String userId, String dealerId) {

		boolean needwhere = false;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "SELECT c.id,c.customer_name,c.contact,c.mobile,c.prov_name,c.city_name,c.country_name,c.address,csc.id as sellerCustomerId,csc.image_list_store,csc.customer_kind, mjs.member_id, mjs.status as memberOrderStatus ";
		StringBuilder sql = new StringBuilder(
				"FROM cc_user_join_customer cujc ");
		sql.append("LEFT JOIN cc_seller_customer csc ON cujc.seller_customer_id = csc.id ");
		sql.append("JOIN cc_customer c ON csc.customer_id = c.id ");
		sql.append("LEFT JOIN cc_member m on m.customer_id = c.id ");
		sql.append("LEFT JOIN cc_member_join_seller mjs on m.id = mjs.member_id AND mjs.user_id = '" + userId + "' AND mjs.seller_id = '" +  dealerId + "'");
		if(StrKit.notBlank(isOrdered)) {
			sql.append("LEFT JOIN cc_sales_outstock cso ON cujc.seller_customer_id = cso.customer_id ");
			DateTime dateTime = new DateTime(new Date());
			if ("2".equals(isOrdered)) {
				sql.append("AND cso.create_date > '" + DateUtils.format(dateTime.plusWeeks(-1).toDate()) + "' ");
			} else if ("3".equals(isOrdered)) {
				sql.append("AND cso.create_date > '" + DateUtils.format(dateTime.plusMonths(-1).toDate()) + "' ");
			} else if ("4".equals(isOrdered)) {
				sql.append("AND cso.create_date > '" + DateUtils.format(dateTime.plusMonths(-3).toDate()) + "' ");
			} else if ("5".equals(isOrdered)) {
				sql.append("AND cso.create_date > '" + DateUtils.format(dateTime.plusMonths(-6).toDate()) + "' ");
			}
		}

		sql.append("LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(sql, "c1.data_area", dealerDataArea, params, true);
		sql.append("GROUP BY c1.id) t1 ON csc.id = t1.id ");

		if (StrKit.notBlank(searchKey)) {
			sql.append(" WHERE ( c.customer_name LIKE ? OR c.contact LIKE ? ) ");
			if (searchKey.contains("%")) {
				params.add(searchKey);
				params.add(searchKey);
			} else {
				params.add("%" + searchKey + "%");
				params.add("%" + searchKey + "%");
			}
		} else {
			sql.append(" WHERE c.customer_name is not null ");
			needwhere = false;
		}

		needwhere = appendIfNotEmptyWithLike(sql, "cujc.data_area", selectDataArea, params, needwhere);
		if (StrKit.notBlank(isOrdered)) {
			if (isOrdered.equals("0")) {
				needwhere = appendIfNotEmptyWithLike(sql, "cso.data_area", selectDataArea, params, needwhere);
			}
		}
		needwhere = appendIfNotEmpty(sql, "csc.is_enabled", 1, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "t1.customerTypeNames", customerType, params, needwhere);

		sql.append("GROUP BY c.id ");

		if (StrKit.notBlank(isOrdered)) {

			if (isOrdered.equals("0")) {
				sql.append("HAVING count(DISTINCT(cso.id)) > 0 ");
			} else {
				sql.append("HAVING count(DISTINCT(cso.id)) = 0 ");
			}
		}
		sql.append("ORDER BY csc.create_date DESC ");
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());

	}

	public Record getOrderNumber( String selectDataArea, String dealerDataArea, String customerType, String isOrdered, String searchKey) {
		boolean needwhere = false;
		LinkedList<Object> params = new LinkedList<Object>();

		StringBuilder sql = new StringBuilder(
				"select count(DISTINCT(c.id)) as orderCount  FROM cc_user_join_customer cujc ");
		sql.append("LEFT JOIN cc_seller_customer csc ON cujc.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer c ON csc.customer_id = c.id ");
		sql.append("LEFT JOIN cc_sales_outstock cso ON cujc.seller_customer_id = cso.customer_id ");

		sql.append("LEFT JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		sql.append("FROM cc_seller_customer c1 ");
		sql.append("LEFT JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(sql, "c1.data_area", dealerDataArea, params, true);
		sql.append("GROUP BY c1.id) t1 ON csc.id = t1.id ");

		if (StrKit.notBlank(searchKey)) {
			sql.append(" WHERE ( c.customer_name LIKE ? OR c.contact LIKE ? ) ");
			if (searchKey.contains("%")) {
				params.add(searchKey);
				params.add(searchKey);
			} else {
				params.add("%" + searchKey + "%");
				params.add("%" + searchKey + "%");
			}
		} else {
			sql.append(" WHERE c.customer_name is not null ");
			needwhere = false;
		}

		needwhere = appendIfNotEmptyWithLike(sql, "cujc.data_area", selectDataArea, params, needwhere);
		if (StrKit.notBlank(isOrdered)) {
			if (isOrdered.equals("0")) {
				needwhere = appendIfNotEmptyWithLike(sql, "cso.data_area", selectDataArea, params, needwhere);
			}
		}
		needwhere = appendIfNotEmpty(sql, "csc.is_enabled", 1, params, needwhere);
		needwhere = appendIfNotEmptyWithLike(sql, "t1.customerTypeNames", customerType, params, needwhere);

		if (StrKit.notBlank(isOrdered)) {

			if (isOrdered.equals("0")) {
				sql.append("HAVING count(DISTINCT(cso.id)) > 0 ");
			} else {
				sql.append("HAVING count(DISTINCT(cso.id)) = 0 ");
			}
		}
		return Db.findFirst( sql.toString(), params.toArray());

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
		sb.append(" where FIND_IN_SET(?, u.USER_ID_) and c.is_enabled = 1");
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
		sql.append("WHERE p.KEY_ = ? and FIND_IN_SET(?, ASSIGNEE_) AND i.DURATION_ is not null ");
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

	public List<Record> findName(String dataArea, String customerType) {
		List<Object> param = new LinkedList<Object>();
		boolean needWhere = true;

		StringBuilder sql = new StringBuilder("SELECT csc.id, cc.customer_name as name ");
		sql.append("FROM cc_seller_customer csc LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
		sql.append("LEFT JOIN cc_user_join_customer cujc ON cujc.seller_customer_id = csc.id ");
		sql.append("LEFT JOIN cc_customer_join_customer_type ccjct ON csc.id = ccjct.seller_customer_id ");
		sql.append("LEFT JOIN cc_customer_type cct ON ccjct.customer_type_id = cct.id ");

		needWhere = appendIfNotEmptyWithLike(sql, "cujc.data_area", dataArea, param, needWhere);
		needWhere = appendIfNotEmpty(sql, "cct.name", customerType, param, needWhere);

		sql.append("GROUP BY csc.id");
		return Db.find(sql.toString(), param.toArray());
	}

	public Page<Record> findImportCustomer(int pageNumber, int pageSize, String userDataArea, String searchKey, String corpSellerId, String dealerDataArea) {
		boolean needwhere = false;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "SELECT c.id,c.customer_name,c.contact,c.mobile,c.prov_name,c.city_name,c.country_name,c.address,csc.id as sellerCustomerId, a.getted ";
		StringBuilder sql = new StringBuilder("FROM cc_customer_join_corp ccjc ");
		sql.append("JOIN cc_customer c ON ccjc.customer_id = c.id ");
		sql.append("JOIN cc_seller_customer csc ON csc.customer_id = c.id ");
		sql.append("JOIN cc_customer_join_customer_type ccjct ON ccjct.seller_customer_id = csc.id ");
		sql.append("JOIN cc_customer_type cct ON ccjct.customer_type_id = cct.id ");
		sql.append("LEFT JOIN( SELECT cujc.seller_customer_id, GROUP_CONCAT(u.realname) AS getted ");
		sql.append("FROM cc_user_join_customer cujc LEFT JOIN `user` u ON cujc.user_id = u.id ");
		sql.append("WHERE u.data_area LIKE ? ");
		params.add(dealerDataArea);
		sql.append("GROUP BY cujc.seller_customer_id ) AS a ON a.seller_customer_id = csc.id ");

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

		needwhere = appendIfNotEmpty(sql, "ccjc.seller_id", corpSellerId, params, needwhere );
		sql.append(" AND cct.code != ? ");
		params.add(Consts.CUSTOMER_TYPE_CODE_SELLER);

//		sql.append(" AND csc.customer_id not in ( ");
//		sql.append("SELECT cc.id ");
//
//		sql.append("FROM cc_user_join_customer cujc ");
//		sql.append("LEFT JOIN cc_seller_customer csc ON cujc.seller_customer_id = csc.id ");
//		sql.append("LEFT JOIN cc_customer cc ON csc.customer_id = cc.id ");
//		sql.append("WHERE cujc.data_area = ?) ");
//
//		params.add(userDataArea);

		sql.append("GROUP BY c.id ");
		return Db.paginate(pageNumber, pageSize, select, sql.toString(), params.toArray());

	}

	public List<Map<String, Object>> findImportAroundCustomer(double dist, BigDecimal lng,BigDecimal lat,String searchKey, String sellerId){

		AroundCustomerBiUndevelopedCallback callback = new AroundCustomerBiUndevelopedCallback();
		callback.setLon(lng);
		callback.setLat(lat);
		callback.setSearchKey(searchKey);
		callback.setDist(dist);
		callback.setSellerId(sellerId);

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) Db.execute(callback);
		return result;
	}

	public Page<Record> _paginateForApp(int pageNumber, int pageSize, String keyword, String dataArea, String dealerDataArea, String userId,
			String customerTypeId, String isOrdered, String customerKind, String provName, String cityName, String countryName) {

		boolean needWhere = true;
		LinkedList<Object> params = new LinkedList<Object>();

		String select = "select sc.*, c.customer_code, c.customer_name"
				+ ", c.contact, c.mobile, c.prov_name, c.city_name, c.country_name"
				+ ", c.prov_code, c.city_code, c.country_code, c.address" + ", t1.customerTypeNames, t2.realnames";

		StringBuilder fromBuilder = new StringBuilder(" from `cc_seller_customer` sc ");
//		fromBuilder.append(" left join `cc_sales_order` so on sc.id = so.customer_id ");

		if(StrKit.isBlank(customerTypeId)) {
			fromBuilder.append(" LEFT ");
		}

		fromBuilder.append(" JOIN (SELECT c1.id,GROUP_CONCAT(ct. NAME) AS customerTypeNames ");
		fromBuilder.append(" FROM cc_seller_customer c1 ");
		fromBuilder.append(" JOIN cc_customer_join_customer_type cjct ON c1.id = cjct.seller_customer_id ");
		fromBuilder.append(" JOIN cc_customer_type ct ON cjct.customer_type_id = ct.id ");
		appendIfNotEmptyWithLike(fromBuilder, "c1.data_area", dealerDataArea, params, true);
		fromBuilder.append(" And ct.id in ("+customerTypeId+") ");
		fromBuilder.append(" GROUP BY c1.id) t1 ON sc.id = t1.id ");

		fromBuilder.append(" JOIN (SELECT c2.id, GROUP_CONCAT(u.realname) AS realnames ");
		fromBuilder.append(" FROM cc_seller_customer c2 ");
		fromBuilder.append(" JOIN cc_user_join_customer ujc ON c2.id = ujc.seller_customer_id ");
		fromBuilder.append(" JOIN USER u ON ujc.user_id = u.id ");

		appendIfNotEmptyWithLike(fromBuilder, "ujc.data_area", dataArea, params, true);
		appendIfNotEmpty(fromBuilder, "ujc.user_id", userId, params, false);
		fromBuilder.append(" GROUP BY c2.id) t2 ON sc.id = t2.id ");

		needWhere = appendIfNotEmpty(fromBuilder, "sc.customer_kind", customerKind, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.prov_name", provName, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.city_name", cityName, params, needWhere);
		needWhere = appendIfNotEmpty(fromBuilder, "c.country_name", countryName, params, needWhere);

		if (needWhere) {
			fromBuilder.append(" WHERE 1 = 1 ");
			needWhere = false;
		}

		if (StrKit.notBlank(keyword)) {
			fromBuilder.append(" and (c.customer_name like '%" + keyword + "%' or c.contact like '%" + keyword + "%')");
		}

		if (StrKit.notBlank(isOrdered)) {

			if ("0".equals(isOrdered)) {
				fromBuilder.append(" AND EXISTS ");
			} else {
				fromBuilder.append(" AND NOT EXISTS ");
			}

			fromBuilder.append(" (SELECT DISTINCT csc.id FROM cc_seller_customer csc join `cc_sales_outstock` cso on csc.id = cso.customer_id where sc.id = csc.id ");
			Date date = null;
			DateTime dateTime = new DateTime(new Date());
			if ("2".equals(isOrdered)) {
				date = dateTime.plusWeeks(-1).toDate();
			} else if ("3".equals(isOrdered)) {
				date = dateTime.plusMonths(-1).toDate();
			} else if ("4".equals(isOrdered)) {
				date = dateTime.plusMonths(-3).toDate();
			} else if ("5".equals(isOrdered)) {
				date = dateTime.plusMonths(-6).toDate();
			}

			if (date != null) {
				fromBuilder.append(" and cso.create_date > ?");
				params.add(DateUtils.format(date));
			}

			fromBuilder.append(" group by csc.id)");

		}

		fromBuilder.append(" GROUP BY sc.id ");
		fromBuilder.append(" order by sc.create_date ");

		if (params.isEmpty())
			return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return Db.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public Long findTotalInstId(String procInstId) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(art.ID_) ");
		sql.append("FROM cc_seller_customer csc ");
		sql.append("JOIN  act_ru_task art on csc.proc_inst_id = art.PROC_INST_ID_ ");
		sql.append("WHERE csc.proc_inst_id = ?");
		return Db.queryLong(sql.toString(), procInstId);
	}

	public List<SellerCustomer> _findBySellerId(String sellerId){
		String sql = "select * from cc_seller_customer where seller_id = '"+sellerId+"' and is_enabled = 1";
		return DAO.find(sql);
	}

	public int getMySellerNum(String selectDataArea) {
		int count = 0;
		LinkedList<Object> params = new LinkedList<Object>();
		StringBuilder sql = new StringBuilder("SELECT count(*) as count FROM cc_user_join_customer cujc ");
		sql.append("LEFT JOIN cc_seller_customer csc ON cujc.seller_customer_id = csc.id ");
		boolean needwhere = true;
		needwhere = appendIfNotEmptyWithLike(sql, "cujc.data_area", selectDataArea, params, needwhere);
		needwhere = appendIfNotEmpty(sql, "csc.is_enabled", 1, params, needwhere);
		Record record = Db.findFirst(sql.toString(), params.toArray());
		if (record != null) {
			count = record.getInt("count");
		}
		return count;
	}

}
