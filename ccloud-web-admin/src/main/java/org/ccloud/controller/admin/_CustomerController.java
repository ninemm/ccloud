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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Customer;
import org.ccloud.model.Department;
import org.ccloud.model.ModelSorter;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.UserJoinCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.CustomerExcel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.shiro.core.ShiroKit;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.service.WorkFlowService;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customer", viewPath = "/WEB-INF/admin/customer")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerController extends JBaseCRUDController<Customer> {

	@Override
	@RequiresPermissions(value = { "/admin/customer", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void index() {
		render("index.html");
	}

	@RequiresPermissions(value = { "/admin/customer", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void list() {

		Map<String, String[]> paraMap = getParaMap();
		String keyword = StringUtils.getArrayFirst(paraMap.get("k"));
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		Map<String, String> deptIdAndDataArea = this.getDeptIdAndDataArea();
		String deptId = deptIdAndDataArea.get("deptId");
		String dataArea = deptIdAndDataArea.get("dataArea");

		Page<Record> page = CustomerQuery.me().paginate(getPageNumber(), getPageSize(), keyword, paraMap, deptId,
				dataArea);
		List<Record> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);

	}

	private Map<String, String> getDeptIdAndDataArea() {

		Map<String, String> map = Maps.newHashMap();
		User user = getSessionAttr("user");
		Subject subject = SecurityUtils.getSubject();

		if (subject.isPermitted("/admin/all")) {
			map.put("deptId", "");
			map.put("dataArea", "");

		} else if (subject.isPermitted("/admin/dealer/all")) {
			map.put("deptId", user.getDepartmentId());
			map.put("dataArea", DataAreaUtil.getUserDeptDataArea(user.getDataArea()) + "%");

		} else {
			map.put("deptId", user.getDepartmentId());
			map.put("dataArea", DataAreaUtil.getUserDeptDataArea(user.getDataArea()));

		}

		return map;
	}

	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void enable() {

		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");

		if (CustomerQuery.me().enable(id, isEnabled)) {
			renderAjaxResultForSuccess();
		} else {
			renderAjaxResultForError();
		}

	}

	@Override
	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void edit() {
		String id = getPara("id");

		boolean notBlank = StrKit.notBlank(id);
		boolean isSuperAdmin = SecurityUtils.getSubject().isPermitted("/admin/all");
		boolean isDealerAdmin = SecurityUtils.getSubject().isPermitted("/admin/dealer/all");

		Map<String, String> deptIdAndDataArea = this.getDeptIdAndDataArea();
		String deptId = deptIdAndDataArea.get("deptId");
		String dataArea = deptIdAndDataArea.get("dataArea");

		StringBuilder cUserIds = new StringBuilder();
		StringBuilder cUserNames = new StringBuilder();

		if (notBlank) {// 超级管理员修改
			setAttr("customer", CustomerQuery.me().findById(id));
			setAttr("cTypeList", CustomerJoinCustomerTypeQuery.me().findCustomerTypeListByCustomerId(id,
					DataAreaUtil.getUserDealerDataArea(dataArea)));

			if (isSuperAdmin || isDealerAdmin) {
				List<Record> list = UserJoinCustomerQuery.me().findUserListByCustomerId(id, deptId, dataArea);
				for (Record record : list) {
					if (cUserIds.length() != 0 || cUserIds.length() != 0) {
						cUserIds.append(",");
						cUserNames.append(",");
					}
					cUserIds.append(record.get("user_id"));
					cUserNames.append(record.get("realname"));

				}
				setAttr("cUserIds", cUserIds);
				setAttr("cUserNames", cUserNames);
			}

		}

		if (!isSuperAdmin) {
			setAttr("customerTypeList",
					CustomerTypeQuery.me().findCustomerTypeList(DataAreaUtil.getUserDealerDataArea(dataArea)));
		}

		render("edit.html");
	}

	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void user_tree() {

		String dataArea = getSessionAttr("DeptDataAreaLike");
		List<Department> list = DepartmentQuery.me().findDeptList(dataArea, "order_list asc");
		List<Map<String, Object>> resTreeList = new ArrayList<Map<String, Object>>();
		ModelSorter.tree(list);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "总部");// 父子表第一级名称,以后可以存储在字典表或字典类
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuild(list, true));
		resTreeList.add(map);

		setAttr("treeData", JSON.toJSON(resTreeList));
	}

	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void department_tree() {

		String dataArea = getSessionAttr("DeptDataAreaLike");
		List<Department> list = DepartmentQuery.me().findDeptList(dataArea, "order_list asc");
		List<Map<String, Object>> resTreeList = new ArrayList<Map<String, Object>>();
		ModelSorter.tree(list);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("text", "总部");// 父子表第一级名称,以后可以存储在字典表或字典类
		map.put("tags", Lists.newArrayList(0));
		map.put("nodes", doBuild(list, false));
		resTreeList.add(map);

		setAttr("treeData", JSON.toJSON(resTreeList));
	}

	private List<Map<String, Object>> doBuild(List<Department> list, boolean addUserFlg) {
		List<Map<String, Object>> resTreeList = new ArrayList<Map<String, Object>>();
		for (Department dept : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", dept.getDeptName());
			map.put("tags", Lists.newArrayList(dept.getId(), dept.getDataArea()));

			List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();

			if (dept.getChildList() != null && dept.getChildList().size() > 0) {
				childList = doBuild(dept.getChildList(), addUserFlg);
			}

			if (addUserFlg) {
				childList = addUser(dept.getId(), childList);
			}

			map.put("nodes", childList);

			resTreeList.add(map);
		}
		return resTreeList;
	}

	private List<Map<String, Object>> addUser(String deptId, List<Map<String, Object>> childList) {
		List<User> list = UserQuery.me().findByDeptId(deptId);
		for (User user : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", user.getRealname());
			map.put("tags", Lists.newArrayList(user.getId(), "user"));
			childList.add(map);

		}
		return childList;
	}

	@Override
	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void save() {

		Customer customer = getModel(Customer.class);
		String customerId = customer.getId();
		customer.setProvCode(getPara("userProvinceId"));
		customer.setProvName(getPara("userProvinceText"));
		customer.setCityCode(getPara("userCityId"));
		customer.setCityName(getPara("userCityText"));
		customer.setCountryCode(getPara("userDistrictId"));
		customer.setCountryName(getPara("userDistrictText"));
		customer.setIsArchive(1);
		
		if(!this.checkCustomerNameAndMobile(customer)) {
			renderAjaxResultForError("该客户已存在");
		}
		
		String[] customerTypes = getParaValues("customerTypes");
		String userIds = getPara("userIds");

		if (StrKit.isBlank(userIds)) {
			User user = getSessionAttr("user");
			userIds = user.getId();
		}

		CustomerJoinCustomerTypeQuery.me().deleteByCustomerId(customerId);
		UserJoinCustomerQuery.me().deleteByCustomerId(customerId);

		if (StrKit.isBlank(customerId)) {
			customerId = StrKit.getRandomUUID();
			customer.set("id", customerId);
			customer.set("create_date", new Date());
			customer.save();
		} else {
			
			WorkFlowService workflow = new WorkFlowService();
			String defKey = getPara("defKey");
			defKey = "customer_edit";
			Ret var = Ret.create();
			// 找到对应的上级
			var.set("manager", "hx");
			var.set("apply", "qgadmin");
			User user = getSessionAttr("user");
			var.set("applyUserId", user.getId());
			var.set("applyer", user.getRealname());
			String procInstId = workflow.startProcess(customerId, defKey, var);
			
			customer.setStatus(Customer.CUSTOMER_AUDIT);
			customer.setProcDefKey(defKey);
			customer.setProcInstId(procInstId);
			customer.set("modify_date", new Date());
			customer.update();
		}

		for (String customerType : customerTypes) {
			CustomerJoinCustomerTypeQuery.me().insert(customerId, customerType);
		}

		this.insertUserJoinCustomer(customerId, userIds);

		renderAjaxResultForSuccess();

	}
	
	
	private boolean checkCustomerNameAndMobile(Customer customer) {
		Integer cnt = CustomerQuery.me().findByNameAndMobile(customer.getCustomerName(), customer.getMobile());
		if (cnt > 1) {
			return false;
		}
		return true;
	}

	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void upload() {

		render("upload.html");
	}

	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void customerTemplate() {
		String realPath = getSession().getServletContext().getRealPath("\\");
		renderFile(new File(realPath + "\\WEB-INF\\admin\\customer\\customerTemplate.xlsx"));
	}

	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void uploading() {

		User user = getSessionAttr("user");

		File file = getFile().getFile();
		String userId = getPara("userIds");
		ImportParams params = new ImportParams();

		List<CustomerExcel> list = ExcelImportUtil.importExcel(file, CustomerExcel.class, params);

		for (CustomerExcel excel : list) {
			String customerId = StrKit.getRandomUUID();

			this.insertCustomer(customerId, excel);
			this.insertCustomerJoinCustomerType(customerId, excel, user);
			this.insertUserJoinCustomer(customerId, userId);

		}

		renderAjaxResultForSuccess();
	}

	private void insertCustomer(String customerId, CustomerExcel excel) {
		Customer customer = new Customer();
		customer.set("id", customerId);
		customer.set("customer_code", excel.getCustomerCode());
		customer.set("customer_name", excel.getCustomerName());
		customer.set("contact", excel.getContact());
		customer.set("mobile", excel.getMobile());
		customer.set("email", excel.getEmail());
		customer.set("prov_name", excel.getProvName());
		customer.set("city_name", excel.getCityName());
		customer.set("country_name", excel.getCountyName());
		customer.set("address", excel.getAddress());
		customer.set("create_date", new Date());
		customer.save();
	}

	private void insertCustomerJoinCustomerType(String customerId, CustomerExcel excel, User user) {

		String customerTypeName = excel.getCustomerTypeName();
		String[] customerTypeNames = customerTypeName.split(",");
		for (String typeName : customerTypeNames) {
			String id = CustomerTypeQuery.me().findIdByName(typeName,
					DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
			CustomerJoinCustomerTypeQuery.me().insert(customerId, id);
		}
	}

	private void insertUserJoinCustomer(String customerId, String userIds) {
		String[] userIdArray = userIds.split(",");
		for (String id : userIdArray) {
			User user = UserQuery.me().findById(id);
			UserJoinCustomerQuery.me().insert(customerId, id, user.getDepartmentId(), user.getDataArea());
		}
	}

	@RequiresPermissions(value = { "/admin/customer/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void download() {

		render("download.html");
	}

	@RequiresPermissions(value = { "/admin/customer/downloading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {
		Map<String, String[]> paraMap = getParaMap();
		String depatName = getPara("parent_name");
		if (StrKit.notBlank(depatName)) {
			depatName = StringUtils.urlRedirectToUTF8(depatName);
		}
		String depatId = getPara("parent_id");

		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\customer\\"
				+ depatName + "客户资料.xlsx";

		Page<Record> page = CustomerQuery.me().paginate(1, Integer.MAX_VALUE, null, paraMap, depatId, "");
		List<Record> customerList = page.getList();

		List<CustomerExcel> excellist = Lists.newArrayList();
		for (Record record : customerList) {

			CustomerExcel excel = new CustomerExcel();
			excel.setCustomerName((String) record.get("customer_name"));
			excel.setCustomerCode((String) record.get("customer_code"));
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
		}

		renderFile(new File(filePath));
	}

	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/customer/batchSetUser", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)
	public void batchSetUser() {
		String[] customerIds = getParaValues("dataItem");
		String[] userIds = getParaValues("userIds");
		for (String customerId : customerIds) {
			UserJoinCustomerQuery.me().deleteByCustomerId(customerId);
			for (String userId : userIds)
				this.insertUserJoinCustomer(customerId, userId);
		}

		renderAjaxResultForSuccess();
	}
	
	public void audit() {
		
		keepPara("taskId");
		String id = getPara("id");
		
		Customer customer = CustomerQuery.me().findById(id);
		setAttr("customer", customer);
		
		WorkFlowService workflowService = new WorkFlowService();
		Object _applyer = workflowService.getTaskVariableByTaskId(getPara("taskId"), "applyer");
		
		String applier = null;
		if (_applyer != null) {
			applier = _applyer.toString();
			setAttr("applier", applier);
		}
	}
	
	public void auditSave() {
		
		Customer customer = getModel(Customer.class);
		
		String taskId = getPara("taskId");
		String comment = getPara("comment");
		
		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, null);
		
		if(customer.saveOrUpdate())
			renderAjaxResultForSuccess("客户修改审核成功");
		else
			renderAjaxResultForError("客户修改审核失败");
		
		
	}
}
