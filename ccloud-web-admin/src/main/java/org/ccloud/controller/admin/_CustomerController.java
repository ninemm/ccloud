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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.Customer;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.UserJoinCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.CustomerExcel;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.wechat.WechatApiConfigInterceptor;
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
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

		Page<Record> page = CustomerQuery.me().paginate(getPageNumber(), getPageSize(), keyword);
		List<Record> customerList = page.getList();

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", customerList);
		renderJson(map);

	}

	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/all" }, logical = Logical.OR)
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

	@Before({ Tx.class, WechatApiConfigInterceptor.class })
	@RequiresPermissions(value = { "/admin/customer/edit", "/admin/all" }, logical = Logical.OR)
	public void save() {

		Customer customer = getModel(Customer.class);
		String customerId = customer.getId();

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

		if (!this.checkCustomerNameAndMobile(customer)) {
			renderAjaxResultForError("该客户已存在");
		}

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
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			var.set("applyUserId", user.getId());
			var.set("applyer", user.getRealname());
			@SuppressWarnings("unchecked")
			String procInstId = workflow.startProcess(customerId, defKey, var);

			customer.setStatus(Customer.CUSTOMER_AUDIT);
			customer.setProcDefKey(defKey);
			customer.setProcInstId(procInstId);
			customer.set("modify_date", new Date());
			customer.update();

			User tmp = UserQuery.me().findUserByUsername("yly");
			Kv kv = Kv.create();

			kv.set("touser", tmp.getWechatOpenId());
			kv.set("templateId", "Rak2cOiujFAdjxx-80z6y2JL4IFwSbKTxP1rkHUBZrI");
			kv.set("customerName", customer.getCustomerName());
			kv.set("submit", user.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", "待审核");

			MessageKit.sendMessage(Actions.NotifyMessage.CUSTOMER_AUDIT_MESSAGE, kv);

		}

		renderAjaxResultForSuccess();

	}

	private boolean checkCustomerNameAndMobile(Customer customer) {
		Integer cnt = CustomerQuery.me().findByNameAndMobile(customer.getCustomerName(), customer.getMobile());
		if (cnt > 1) {
			return false;
		}
		return true;
	}

	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/all" }, logical = Logical.OR)
	public void upload() {

		render("upload.html");
	}

	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/all" }, logical = Logical.OR)
	public void customerTemplate() {
		String realPath = getSession().getServletContext().getRealPath("\\");
		renderFile(new File(realPath + "\\WEB-INF\\admin\\customer\\customerTemplate.xlsx"));
	}

	@Before(Tx.class)
	@RequiresPermissions(value = { "/admin/customer/uploading", "/admin/all" }, logical = Logical.OR)
	public void uploading() {

		File file = getFile().getFile();
		String userId = getPara("userIds");
		ImportParams params = new ImportParams();

		List<CustomerExcel> list = ExcelImportUtil.importExcel(file, CustomerExcel.class, params);

		for (CustomerExcel excel : list) {
			String customerId = StrKit.getRandomUUID();

			this.insertCustomer(customerId, excel);
			this.insertUserJoinCustomer(customerId, userId);

		}

		renderAjaxResultForSuccess();
	}

	private void insertCustomer(String customerId, CustomerExcel excel) {
		Customer customer = new Customer();
		customer.set("id", customerId);
		// customer.set("customer_code", excel.getCustomerCode());
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


	private void insertUserJoinCustomer(String customerId, String userIds) {
		String[] userIdArray = userIds.split(",");
		for (String id : userIdArray) {
			User user = UserQuery.me().findById(id);
			UserJoinCustomerQuery.me().insert(customerId, id, user.getDepartmentId(), user.getDataArea());
		}
	}

	@RequiresPermissions(value = { "/admin/customer/downloading", "/admin/all" }, logical = Logical.OR)
	public void downloading() throws UnsupportedEncodingException {

		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\customer\\"
				+ "customerInfo.xlsx";

		Page<Record> page = CustomerQuery.me().paginate(1, Integer.MAX_VALUE, "");
		List<Record> customerList = page.getList();

		List<CustomerExcel> excellist = Lists.newArrayList();
		for (Record record : customerList) {

			CustomerExcel excel = new CustomerExcel();
			excel.setCustomerName((String) record.get("customer_name"));
			excel.setContact((String) record.get("contact"));
			excel.setMobile((String) record.get("mobile"));
			excel.setEmail((String) record.get("email"));
			excel.setProvName((String) record.get("prov_name"));
			excel.setCityName((String) record.get("city_name"));
			excel.setCountyName((String) record.get("country_name"));
			excel.setAddress((String) record.get("address"));

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
			// UserJoinCustomerQuery.me().deleteByCustomerId(customerId);
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

		if (customer.saveOrUpdate())
			renderAjaxResultForSuccess("客户修改审核成功");
		else
			renderAjaxResultForError("客户修改审核失败");

	}
}
