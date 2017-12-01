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
package org.ccloud.controller.admin;

import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.CustomerType;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserJoinCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/sellerCustomer", viewPath = "/WEB-INF/admin/seller_customer")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SellerCustomerController extends JBaseCRUDController<SellerCustomer> {

	public void index() {
	}

	public void list() {
		Map<String, String[]> paraMap = getParaMap();
		String keyword = StringUtils.getArrayFirst(paraMap.get("k"));
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		// Map<String, String> deptIdAndDataArea = this.getDeptIdAndDataArea();
		// String deptId = deptIdAndDataArea.get("deptId");
		// String dataArea = deptIdAndDataArea.get("dataArea");

		// Page<Record> page = CustomerQuery.me().paginate(getPageNumber(),
		// getPageSize(), keyword, paraMap, deptId,
		// dataArea);

		Page<SellerCustomer> page = SellerCustomerQuery.me().paginate(getPageNumber(), getPageSize(), keyword);
		List<SellerCustomer> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);
	}

	public void edit() {
		String id = getPara("id");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		if (StrKit.notBlank(id)) {
			Record sellerCustomer = SellerCustomerQuery.me().findMoreById(id);
			setAttr("sellerCustomer", sellerCustomer);

			List<Record> list = UserJoinCustomerQuery.me().findUserListBySellerCustomerId(id, selectDataArea);

			int length = list.size();
			String[] userIds = new String[length];
			String[] realnames = new String[length];

			for (int i = 0; i < length; i++) {
				userIds[i] = list.get(i).getStr("user_id");
				realnames[i] = list.get(i).getStr("realname");
			}

			setAttr("userIds", StrKit.join(userIds, ","));
			setAttr("realnames", StrKit.join(realnames, ","));

			setAttr("cTypeList", CustomerJoinCustomerTypeQuery.me().findCustomerTypeListBySellerCustomerId(id,
					DataAreaUtil.getUserDealerDataArea(selectDataArea)));
		}

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(selectDataArea));
		setAttr("customerTypeList", customerTypeList);

	}

	@Before(Tx.class)
	public void save() {

		String sellerId = getSessionAttr("sellerId");

		SellerCustomer sellerCustomer = getModel(SellerCustomer.class);
		Customer customer = getModel(Customer.class);

		String areaCodes = getPara("areaCodes");
		String areaNames = getPara("areaNames");
		String[] areaCodeArray = areaCodes.split("/");
		String[] areaNameArray = areaNames.split("/");

		customer.setProvName(areaNameArray[0]);
		customer.setProvCode(areaCodeArray[0]);
		customer.setCityName(areaNameArray[1]);
		customer.setCityCode(areaCodeArray[1]);

		customer.setCountryName(areaNameArray[2]);
		customer.setCountryCode(areaCodeArray[2]);

		String customerId = null;
		// 检查客户是否存在
		Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customer.getCustomerName(),
				customer.getMobile());
		if (persiste == null) {
			customerId = StrKit.getRandomUUID();
			customer.set("id", customerId);
			customer.save();
		} else {
			customerId = persiste.getId();
		}

		String sellerCustomerId = StrKit.getRandomUUID();
		sellerCustomer.set("id", sellerCustomerId);
		sellerCustomer.set("seller_id", sellerId);
		sellerCustomer.set("customer_id", customerId);
		sellerCustomer.set("is_enabled", 1);
		sellerCustomer.set("is_archive", 1);
		sellerCustomer.save();

		CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);

		String[] customerTypes = getParaValues("customerTypes");
		for (String custType : customerTypes) {
			CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
			ccType.setSellerCustomerId(sellerCustomerId);
			;
			ccType.setCustomerTypeId(custType);
			ccType.save();
		}

		UserJoinCustomerQuery.me().deleteBySelerCustomerId(sellerCustomerId);
		String _userIds = getPara("userIds");
		String[] userIdArray = _userIds.split(",");

		for (String userId : userIdArray) {

			User user = UserQuery.me().findById(userId);
			UserJoinCustomer uCustomer = new UserJoinCustomer();

			uCustomer.setSellerCustomerId(sellerCustomerId);
			uCustomer.setUserId(userId);
			uCustomer.setDeptId(user.getDepartmentId());
			uCustomer.setDataArea(user.getDataArea());

			uCustomer.save();
		}
		
		renderAjaxResultForSuccess();
	}

	public void user_tree() {

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Map<String, Object>> treeData = DepartmentQuery.me().findDeptListAsTree(dataArea, true);
		setAttr("treeData", JSON.toJSON(treeData));

	}

	// public void department_tree() {
	//
	// String dataArea = getSessionAttr("DeptDataAreaLike");
	// List<Map<String, Object>> treeData =
	// DepartmentQuery.me().findDeptListAsTree(dataArea, false);
	// setAttr("treeData", JSON.toJSON(treeData));
	// }
}
