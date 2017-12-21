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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.compare.BeanCompareUtils;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.CustomerExcel;
import org.ccloud.model.vo.CustomerVO;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.service.WorkFlowService;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.joda.time.DateTime;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/sellerCustomer", viewPath = "/WEB-INF/admin/seller_customer")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _SellerCustomerController extends JBaseCRUDController<SellerCustomer> {

	@RequiresPermissions(value = { "/admin/sellerCustomer", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void index() {
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void list() {

		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Map<String, String[]> paraMap = getParaMap();
		String keyword = StringUtils.getArrayFirst(paraMap.get("k"));
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		Page<Record> page = SellerCustomerQuery.me().paginate(getPageNumber(), getPageSize(), keyword, selectDataArea);
		List<Record> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void edit() {
		String id = getPara("id");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		if (StrKit.notBlank(id)) {
			SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
			setAttr("sellerCustomer", sellerCustomer);

			List<Record> list = UserJoinCustomerQuery.me().findUserListBySellerCustomerId(id, selectDataArea);

			int length = list.size();
			String[] userIds = new String[length];
			String[] realnames = new String[length];

			for (int i = 0; i < length; i++) {
				userIds[i] = list.get(i).getStr("user_id");
				realnames[i] = list.get(i).getStr("realname");
			}

			setAttr("cUserIds", StrKit.join(userIds, ","));
			setAttr("cUserNames", StrKit.join(realnames, ","));

			setAttr("cTypeList", CustomerJoinCustomerTypeQuery.me().findCustomerTypeIdListBySellerCustomerId(id,
					DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea)));
		}

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea));
		setAttr("customerTypeList", customerTypeList);

	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/edit", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void enable() {

		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");

		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		boolean isDealerAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");

		if(isDealerAdmin || isSuperAdmin) {
			if(StrKit.notBlank(id)){
				SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
				sellerCustomer.setIsEnabled(isEnabled);
				if (sellerCustomer.saveOrUpdate()) renderAjaxResultForSuccess("操作成功");
				else renderAjaxResultForError("操作失败");
			}
			return;
		}

		if(StrKit.notBlank(id)) {

			boolean updated = startProcess(id, new HashMap<String, Object>(), 1);

			if (updated) {
				renderAjaxResultForSuccess("操作成功");
			} else {
				renderAjaxResultForError("操作失败");
			}
		}else {
			renderError(500);
		}
	}

	
	@RequiresPermissions(value = { "/admin/sellerCustomer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	@Before(Tx.class)
	public void save() {

		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		boolean isDealerAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Customer customer = getModel(Customer.class);
		SellerCustomer sellerCustomer = getModel(SellerCustomer.class);

		String[] customerTypes = getParaValues("customerTypes");
		List<String> custTypeNameList = new ArrayList<>();
		for(String customerType : customerTypes)
			custTypeNameList.add(CustomerTypeQuery.me().findById(customerType).getStr("name"));

		Map<String, Object> map = Maps.newHashMap();
		boolean updated = true;

		//当是经销商管理员修改时
		if(isSuperAdmin || isDealerAdmin) {
			Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customer.getCustomerName(), customer.getMobile());

			if (persiste != null) {
				customer.setId(persiste.getId());
			}
			updated = customer.saveOrUpdate();

			if (!updated) {
				renderError(500);
				return;
			}

			sellerCustomer.setSellerId(sellerId);
			sellerCustomer.setCustomerId(customer.getId());
			sellerCustomer.setIsEnabled(1);
			sellerCustomer.setIsArchive(1);

			sellerCustomer.setCustomerTypeIds(Joiner.on(",").join(Arrays.asList(customerTypes).iterator()));

			String deptDataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
			Department department = DepartmentQuery.me().findByDataArea(deptDataArea);
			sellerCustomer.setDataArea(deptDataArea);
			sellerCustomer.setDeptId(department.getId());

			updated = sellerCustomer.saveOrUpdate();

			if (!updated) {
				renderError(500);
				return;
			}

			String sellerCustomerId = sellerCustomer.getId();
			CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);

			for (String custType : customerTypes) {
				CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
				ccType.setSellerCustomerId(sellerCustomerId);
				ccType.setCustomerTypeId(custType);
				ccType.save();
			}

			String _userIds = getPara("userIds");

			if (StrKit.isBlank(_userIds)) {// 业务员修改时

				UserJoinCustomerQuery.me().deleteBySelerCustomerIdAndUserId(sellerCustomerId, user.getId());
				_userIds = user.getId();
			} else {
				UserJoinCustomerQuery.me().deleteBySelerCustomerId(sellerCustomerId);
			}

			String[] userIdArray = _userIds.split(",");

			for (String userId : userIdArray) {

				User persist = UserQuery.me().findById(userId);
				UserJoinCustomer uCustomer = new UserJoinCustomer();

				uCustomer.setSellerCustomerId(sellerCustomerId);
				uCustomer.setUserId(userId);
				uCustomer.setDeptId(persist.getDepartmentId());
				uCustomer.setDataArea(persist.getDataArea());

				uCustomer.save();
			}
			renderAjaxResultForSuccess("操作成功");
			return;
		}

		if (sellerCustomer != null && StrKit.notBlank(sellerCustomer.getId())) {

			CustomerVO temp = new CustomerVO();
			temp.setAreaCode(customer.getProvCode() + "," + customer.getCityCode() + "," + customer.getCountryCode());
			temp.setAreaName(customer.getProvName() + "," + customer.getCityName() + "," + customer.getCountryName());
			temp.setCustTypeList(Arrays.asList(customerTypes));

			temp.setCustTypeNameList(custTypeNameList);
			temp.setContact(customer.getContact());

			temp.setSubType(sellerCustomer.getSubType());
			temp.setCustomerKind(sellerCustomer.getCustomerKind());

			temp.setMobile(customer.getMobile());
			temp.setAddress(customer.getAddress());
			temp.setNickname(sellerCustomer.getNickname());
			temp.setCustomerName(customer.getCustomerName());

			map.put("customerVO", temp);

		} else {
			// 检查客户是否存在
			Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customer.getCustomerName(), customer.getMobile());

			if (persiste != null) {
				customer.setId(persiste.getId());
			}
			updated = customer.saveOrUpdate();

			if (!updated) {
				renderError(500);
				return;
			}

			sellerCustomer.setSellerId(sellerId);
			sellerCustomer.setCustomerId(customer.getId());
			sellerCustomer.setIsEnabled(1);
			sellerCustomer.setIsArchive(1);

			sellerCustomer.setCustomerTypeIds(Joiner.on(",").join(Arrays.asList(customerTypes).iterator()));

			String deptDataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
			Department department = DepartmentQuery.me().findByDataArea(deptDataArea);
			sellerCustomer.setDataArea(deptDataArea);
			sellerCustomer.setDeptId(department.getId());

			updated = sellerCustomer.saveOrUpdate();

			if (!updated) {
				renderError(500);
				return;
			}

			for (String custType : customerTypes) {
				CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
				ccType.setSellerCustomerId(sellerCustomer.getId());
				ccType.setCustomerTypeId(custType);
				ccType.save();
			}

			UserJoinCustomer userJoinCustomer = new UserJoinCustomer();

			userJoinCustomer.setSellerCustomerId(sellerCustomer.getId());
			userJoinCustomer.setUserId(user.getId());
			userJoinCustomer.setDeptId(user.getDepartmentId());
			userJoinCustomer.setDataArea(user.getDataArea());

			updated = userJoinCustomer.save();
		}

		if (!updated) {
			renderError(404);
			return ;
		}

		updated = startProcess(sellerCustomer.getId(), map, 0);

		if (updated)
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	public void user_tree() {

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Map<String, Object>> treeData = DepartmentQuery.me().findDeptListAsTree(dataArea, true);
		setAttr("treeData", JSON.toJSON(treeData));

	}

	public void department_tree() {

		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Map<String, Object>> treeData = DepartmentQuery.me().findDeptListAsTree(dataArea, false);
		setAttr("treeData", JSON.toJSON(treeData));
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void download() {

		render("download.html");
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {

		String dataArea = getPara("data_area");

		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\seller_customer\\"
				+ "customerInfo.xlsx";

		Page<Record> page = SellerCustomerQuery.me().paginate(1, Integer.MAX_VALUE, "", dataArea + "%");
		List<Record> customerList = page.getList();

		List<CustomerExcel> excellist = Lists.newArrayList();
		for (Record record : customerList) {

			CustomerExcel excel = new CustomerExcel();
			excel.setCustomerName((String) record.get("customer_name"));
			excel.setNickname((String) record.get("nickname"));
			excel.setContact((String) record.get("contact"));
			excel.setMobile((String) record.get("mobile"));
			excel.setEmail((String) record.get("email"));
			excel.setProvName((String) record.get("prov_name"));
			excel.setCityName((String) record.get("city_name"));
			excel.setCountyName((String) record.get("country_name"));
			excel.setAddress((String) record.get("address"));
			excel.setCustomerTypeName((String) record.get("customerTypeNames"));

			excellist.add(excel);
		}

		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, CustomerExcel.class, excellist);
		File file = new File(filePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ExcelExportUtil.closeExportBigExcel();

		renderFile(new File(filePath));
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void upload() {

		render("upload.html");
	}

	@RequiresPermissions(value = { "/admin/sellerCustomer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void customerTemplate() {
		String realPath = getSession().getServletContext().getRealPath("\\");
		renderFile(new File(realPath + "\\WEB-INF\\admin\\seller_customer\\customerTemplate.xlsx"));
	}

	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/sellerCustomer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void uploading() {
		int inCnt = 0;
		int existCnt = 0;

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dept_dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
		Department dept =  DepartmentQuery.me().findByDataArea(dept_dataArea);
		
		File file = getFile().getFile();
		String userId = getPara("userIds");
		ImportParams params = new ImportParams();

		List<CustomerExcel> list = ExcelImportUtil.importExcel(file, CustomerExcel.class, params);

		for (CustomerExcel excel : list) {
			String customerId = "";
			String sellerCustomerId = "";
			SellerCustomer sellerCustomer = null;

			// 检查客户是否存在
			Customer customer = CustomerQuery.me().findByCustomerNameAndMobile(excel.getCustomerName(),
					excel.getMobile());

			if (customer == null) {
				customer = new Customer();
				customerId = StrKit.getRandomUUID();
				customer.set("id", customerId);
				this.setCustomer(customer, excel);
				customer.set("create_date", new Date());
				customer.save();
			} else {
				customerId = customer.getId();
				// 检查客户是否存在我销售商的客户中
				sellerCustomerId = SellerCustomerQuery.me().findsellerCustomerBycusId(customerId, dataArea);
			}

			if (StrKit.isBlank(sellerCustomerId)) {
				sellerCustomerId = StrKit.getRandomUUID();
				sellerCustomer = new SellerCustomer();
				sellerCustomer.set("id", sellerCustomerId);
				sellerCustomer.set("seller_id", sellerId);
				sellerCustomer.set("customer_id", customerId);
				sellerCustomer.set("is_enabled", 1);
				sellerCustomer.set("is_archive", 1);
				sellerCustomer.set("sub_type", 100301);
				sellerCustomer.set("customer_kind", 100401);
				sellerCustomer.set("nickname", excel.getNickname());
				sellerCustomer.set("data_area", dept_dataArea);
				sellerCustomer.set("dept_id", dept.getId());
				sellerCustomer.set("create_date", new Date());
				sellerCustomer.save();
				inCnt++;
			} else {
				existCnt++;
			}

			UserJoinCustomerQuery.me().deleteBySelerCustomerId(sellerCustomerId);
			this.insertUserJoinCustomer(sellerCustomerId, userId);
			CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);
			this.insertCustomerJoinCustomerType(sellerCustomerId, excel, user);

		}

		renderAjaxResultForSuccess("成功导入客户" + inCnt + "个,已存在客户" + existCnt + "个");
	}

	private void setCustomer(Customer customer, CustomerExcel excel) {
		customer.set("customer_name", excel.getCustomerName());
		customer.set("contact", excel.getContact());
		customer.set("mobile", excel.getMobile());
		customer.set("email", excel.getEmail());
		customer.set("prov_name", excel.getProvName());
		customer.set("city_name", excel.getCityName());
		customer.set("country_name", excel.getCountyName());
		customer.set("address", excel.getAddress());
	}

	private void insertCustomerJoinCustomerType(String sellerCustomerId, CustomerExcel excel, User user) {

		String customerTypeName = excel.getCustomerTypeName();
		String[] customerTypeNames = customerTypeName.split(",");
		for (String typeName : customerTypeNames) {
			String id = CustomerTypeQuery.me().findIdByName(typeName,
					DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
			if (StrKit.isBlank(id)) {
				renderAjaxResultForError("你还没有创建这个客户类型：" + typeName + ", 请确认");
			}
			CustomerJoinCustomerTypeQuery.me().insert(sellerCustomerId, id);
		}
	}

	private void insertUserJoinCustomer(String sellerCustomerId, String userIds) {
		String[] userIdArray = userIds.split(",");
		for (String id : userIdArray) {
			User user = UserQuery.me().findById(id);
			UserJoinCustomerQuery.me().insert(sellerCustomerId, id, user.getDepartmentId(), user.getDataArea());
		}
	}

	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/sellerCustomer/batchSetUser", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void batchSetUser() {
		String[] sellerCustomerIds = getParaValues("dataItem");
		String[] userIds = getParaValues("userIds");
		for (String sellerCustomerId : sellerCustomerIds) {
			UserJoinCustomerQuery.me().deleteBySelerCustomerId(sellerCustomerId);
			for (String userId : userIds)
				this.insertUserJoinCustomer(sellerCustomerId, userId);
		}

		renderAjaxResultForSuccess();
	}
	
	public void searchByCustomerName() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dept_dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
		if(StrKit.isBlank(dept_dataArea)) {
			renderAjaxResultForError("丢失组织数据");
			return ;
		}
		Department dept =  DepartmentQuery.me().findByDataArea(dept_dataArea);
		if(dept==null||StrKit.isBlank(dept.getId())) {
			renderAjaxResultForError("丢失组织结构数据");
			return ;
		}
		String tCustomerName = getPara("name");
		renderAjaxResultForSuccess("success",JSON.toJSON(CustomerQuery.me().findByCustomerName(dept_dataArea, dept.getId(), tCustomerName)));
	}
	
	public void searchTypeById() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String dept_dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
		String sellerCustomerId = getPara("sellerC_Id");
		List<Record> list = UserJoinCustomerQuery.me().findCustomerTypeBySellerCustomerId(sellerCustomerId, dept_dataArea+"%");
		renderAjaxResultForSuccess("success",JSON.toJSON(list));

	}

	@RequiresPermissions(value = { "/admin/customer/audit", "/admin/dealer/all" }, logical = Logical.OR)
	public void audit() {
		
		keepPara();
		
		String id = getPara("id");
		String taskId = getPara("taskId");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		if (StrKit.isBlank(id)) {
			renderError(500);
			return;
		}
		
		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(id);
		//Customer customer = CustomerQuery.me().findById(sellerCustomer.getCustomerId());

		setAttr("sellerCustomer", sellerCustomer);

		List<Record> list = UserJoinCustomerQuery.me().findUserListBySellerCustomerId(id, selectDataArea);

		int length = list.size();
		String[] userIds = new String[length];
		String[] realnames = new String[length];
		
		for (int i = 0; i < length; i++) {
			userIds[i] = list.get(i).getStr("user_id");
			realnames[i] = list.get(i).getStr("realname");
		}

		setAttr("cUserIds", StrKit.join(userIds, ","));
		setAttr("cUserNames", StrKit.join(realnames, ","));
		setAttr("cTypeList", CustomerJoinCustomerTypeQuery.me().findCustomerTypeIdListBySellerCustomerId(id,
				DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea)));

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea));
		setAttr("customerTypeList", customerTypeList);

		List<String> custTypeNameList =  CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(id, DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea));
		String custTypeNames = Joiner.on(",").skipNulls().join(custTypeNameList);
		setAttr("custTypeNames", custTypeNames);

		setAttr("customerType", DictQuery.me().findName(sellerCustomer.getCustomerKind()));
		setAttr("subType", DictQuery.me().findName(sellerCustomer.getSubType()));

		WorkFlowService workflowService = new WorkFlowService();
		Object customerVO = workflowService.getTaskVariableByTaskId(taskId, "customerVO");
		Object applyer = workflowService.getTaskVariableByTaskId(taskId, "applyUsername");
		String isEnable = workflowService.getTaskVariableByTaskId(taskId, "isEnable").toString();

		if (applyer != null) {
			User user = UserQuery.me().findUserByUsername(applyer.toString());
			setAttr("applyer", user);
		}
		
		if (customerVO != null) {
			CustomerVO src = new CustomerVO();
			CustomerVO dest = (CustomerVO) customerVO;
			dest.setSubType(DictQuery.me().findName(dest.getSubType()));
			dest.setCustomerKind(DictQuery.me().findName(dest.getCustomerKind()));

			
			src.setNickname(sellerCustomer.getNickname());
			src.setSellerCustomerId(sellerCustomer.getId());
			src.setCustomerId(sellerCustomer.getCustomerId());
			
			src.setContact(sellerCustomer.getStr("contact"));
			src.setMobile(sellerCustomer.getStr("mobile"));
			src.setAddress(sellerCustomer.getStr("address"));
			src.setCustomerName(sellerCustomer.getStr("customer_name"));

			src.setSubType(DictQuery.me().findName(sellerCustomer.getSubType()));
			src.setCustomerKind(DictQuery.me().findName(sellerCustomer.getCustomerKind()));

			src.setCustTypeNameList(custTypeNameList);
			
			String areaName = Joiner.on(",").skipNulls()
				.join(sellerCustomer.getStr("prov_name")
					, sellerCustomer.getStr("city_name")
					, sellerCustomer.getStr("country_name"));
			src.setAreaName(areaName);
			
			String areaCode = Joiner.on(",").skipNulls()
				.join(sellerCustomer.getStr("prov_code")
					, sellerCustomer.getStr("city_code")
					, sellerCustomer.getStr("country_code"));
			src.setAreaCode(areaCode);
			List<String> diffAttrList = BeanCompareUtils.contrastObj(src, dest);
			setAttr("diffAttrList", diffAttrList);
		} else if(isEnable.equals("0")) {
			List<String> diffAttrList = new ArrayList<>();
			diffAttrList.add("新增客户");
			setAttr("diffAttrList", diffAttrList);
		} else {
			List<String> diffAttrList = new ArrayList<>();
			if(sellerCustomer.getIsEnabled() == 1) diffAttrList.add("申请停用");
			else diffAttrList.add("申请启用");
			setAttr("diffAttrList", diffAttrList);
		}
	}

	@RequiresPermissions(value = { "/admin/customer/audit", "/admin/dealer/all" }, logical = Logical.OR)
	public void complete() {
		
		String taskId = getPara("taskId");
		Integer status = getParaToInt("status");
		String sellerCustomerId = getPara("id");
		String comment;
		if (StrKit.notBlank(getPara("comment"))) comment = getPara("comment");
		else comment = "客户审核批准";

		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		
		boolean updated = true;

		SellerCustomer sellerCustomer =  SellerCustomerQuery.me().findById(sellerCustomerId);
		sellerCustomer.setStatus(status == 1 ? SellerCustomer.CUSTOMER_NORMAL : SellerCustomer.CUSTOMER_REJECT);

		WorkFlowService workFlowService = new WorkFlowService();
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);

		if (status == 1) {
			// 做业务处理

			CustomerVO customerVO = (CustomerVO) workFlowService.getTaskVariableByTaskId(taskId, "customerVO");
			String isEnable = workFlowService.getTaskVariableByTaskId(taskId, "isEnable").toString();

			if (customerVO != null) {

				Customer customer = CustomerQuery.me().findById(sellerCustomer.getCustomerId());
				Customer persiste = CustomerQuery.me().findByCustomerNameAndMobile(customerVO.getCustomerName(), customerVO.getMobile());

				if (StrKit.notBlank(customerVO.getAreaCode())) {

					List<String> areaCodeList = Splitter.on(",")
							.omitEmptyStrings()
							.trimResults()
							.splitToList(customerVO.getAreaCode());

					List<String> areaNameList = Splitter.on(",")
							.omitEmptyStrings()
							.trimResults()
							.splitToList(customerVO.getAreaName());

					if (areaCodeList.size() == 3 && areaNameList.size() == 3) {

						customer.setProvCode(areaCodeList.get(0));
						customer.setProvName(areaNameList.get(0));
						customer.setCityCode(areaCodeList.get(1));
						customer.setCityName(areaNameList.get(1));

						customer.setCountryCode(areaCodeList.get(2));
						customer.setCountryName(areaNameList.get(2));
					}
				}

				customer.setContact(customerVO.getContact());
				customer.setMobile(customerVO.getMobile());
				customer.setAddress(customerVO.getAddress());
				customer.setCustomerName(customerVO.getCustomerName());

				if (persiste != null) {
					customer.setId(persiste.getId());
				} else customer.setId(null);
				updated = updated && customer.saveOrUpdate();

				if (StrKit.notBlank(customerVO.getNickname()))
					sellerCustomer.setNickname(customerVO.getNickname());

				if (customerVO.getCustTypeList() != null || customerVO.getCustTypeList().size() != 0)
					sellerCustomer.setCustomerTypeIds(Joiner.on(",").join(customerVO.getCustTypeList().iterator()));

				if (StrKit.notBlank(customerVO.getImageListStore()))
					sellerCustomer.setImageListStore(customerVO.getImageListStore());

				if (StrKit.notBlank(customerVO.getSubType()))
					sellerCustomer.setSubType(customerVO.getSubType());

				if (StrKit.notBlank(customerVO.getCustomerKind()))
					sellerCustomer.setCustomerKind(customerVO.getCustomerKind());

				sellerCustomer.setSellerId(sellerId);
				sellerCustomer.setCustomerId(customer.getId());
				sellerCustomer.setIsEnabled(1);
				sellerCustomer.setIsArchive(1);
				sellerCustomer.setImageListStore(customerVO.getImageListStore());

				String deptDataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
				Department department = DepartmentQuery.me().findByDataArea(deptDataArea);
				sellerCustomer.setDataArea(deptDataArea);
				sellerCustomer.setDeptId(department.getId());

				updated = updated && sellerCustomer.saveOrUpdate();
				sellerCustomerId = sellerCustomer.getId();

				if (customerVO.getCustTypeList() != null || customerVO.getCustTypeList().size() != 0) {

					CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);
					String[] customerTypes = sellerCustomer.getCustomerTypeIds().split(",");

					for (String custType : customerTypes) {
						CustomerJoinCustomerType ccType = new CustomerJoinCustomerType();
						ccType.setSellerCustomerId(sellerCustomerId);
						ccType.setCustomerTypeId(custType);
						updated = updated && ccType.save();
					}
				}

			} else if(isEnable.equals("1")){
				if(sellerCustomer.getIsEnabled() == 1) sellerCustomer.setIsEnabled(0);
				else sellerCustomer.setIsEnabled(1);
				updated = sellerCustomer.saveOrUpdate();
			}
		}else {
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_audit");

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", sellerCustomer.getCustomer().getCustomerName());
			kv.set("submit", user.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_AUDIT_MESSAGE, kv);
		}
		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", status);
		workFlowService.completeTask(taskId, comment, var);

		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(user.getId());

		message.setToUserId(toUser.getId());
		message.setDeptId(user.getDepartmentId());
		message.setDataArea(user.getDataArea());
		message.setType(Message.CUSTOMER_REVIEW_TYPE_CODE);

		message.setTitle(sellerCustomer.getCustomer().getCustomerName());
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);

		if (updated){
			renderAjaxResultForSuccess("操作成功");
		}
		else
			renderAjaxResultForError("操作失败");
	}

	private boolean startProcess(String customerId, Map<String, Object> param, int isEnable) {

		SellerCustomer sellerCustomer = SellerCustomerQuery.me().findById(customerId);
		boolean isUpdated = true;
//		Boolean isCustomerAudit = OptionQuery.me().findValueAsBool("isCustomerAudit");
		Boolean isCustomerAudit = true;

		if (sellerCustomer == null) {
			renderError(404);
			return false;
		}

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User manager = UserQuery.me().findManagerByDeptId(user.getDepartmentId());

		if (isCustomerAudit != null && isCustomerAudit.booleanValue()) {

			if (manager == null) {
				return false;
			}

			String defKey = "_customer_audit";
			param.put("manager", manager.getUsername());
			param.put("isEnable", isEnable);

			WorkFlowService workflow = new WorkFlowService();
			String procInstId = workflow.startProcess(customerId, defKey, param);

			sellerCustomer.setProcDefKey(defKey);
			sellerCustomer.setProcInstId(procInstId);
			sellerCustomer.setStatus(SellerCustomer.CUSTOMER_AUDIT);
		}

		isUpdated = sellerCustomer.update();

		if (!isUpdated)
			return false;

		Message message = new Message();
		message.setFromUserId(user.getId());
		message.setToUserId(manager.getId());
		message.setDeptId(user.getDepartmentId());
		message.setDataArea(user.getDataArea());
		message.setSellerId(sellerId);
		message.setType(Message.CUSTOMER_REVIEW_TYPE_CODE);
		message.setTitle(sellerCustomer.getCustomer().getCustomerName());

		Object customerVO = param.get("customerVO");
		if (customerVO == null && isEnable == 0) {
			message.setContent("新增待审核");
		} else if(customerVO == null && isEnable == 1) {
			message.setContent("停用待审核");
		}else {
			List<String> list = BeanCompareUtils.contrastObj(sellerCustomer, customerVO);
			if (list != null)
				message.setContent(JsonKit.toJson(list));
		}
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);

		return isUpdated;
	}

}
