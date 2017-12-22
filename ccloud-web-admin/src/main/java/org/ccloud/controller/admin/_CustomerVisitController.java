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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.jfinal.kit.Kv;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
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
import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customerVisit", viewPath = "/WEB-INF/admin/customer_visit")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerVisitController extends JBaseCRUDController<CustomerVisit> {

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void index() {
		render("customer_visit.html");
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void list() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA) + "%";
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        String questionType = getPara("questionType");

        if (StrKit.notBlank(questionType)) {
        	questionType = StringUtils.urlDecode(questionType);
            setAttr("questionType", questionType);
        }

        String customerType = getPara("customerType");
        if (StrKit.notBlank(customerType)) {
        	customerType = StringUtils.urlDecode(customerType);
            setAttr("customerType", customerType);
        }

        String status = getPara("status");
        if(StrKit.notBlank(status)) {
        	status = StringUtils.urlDecode(status);
        	setAttr("status", status);
		}
        Page<CustomerVisit> page = CustomerVisitQuery.me().paginate(getPageNumber(), getPageSize(), keyword, selectDataArea, customerType, questionType, "id", "cc_v.create_date desc", status);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}

	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void audit() {

		keepPara();

		String id = getPara("id");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}

		CustomerVisit customerVisit = CustomerVisitQuery.me().findMoreById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}

		String dataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(selectDataArea) + "%";
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(customerVisit.getSellerCustomerId(), dataArea);

		setAttr("customerVisit", customerVisit);
		setAttr("cTypeName", Joiner.on(",").join(typeList.iterator()));

		render("audit.html");
	}

	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void complete() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String id = getPara("id");
		String taskId = getPara("taskId");

		String commentDesc = getPara("comment");

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		Integer status = getParaToInt("status");
		String comment = (status == 1) ? "批准" : "拒绝";

		if(StrKit.notBlank(commentDesc))
			customerVisit.setComment(commentDesc);

		WorkFlowService workFlowService = new WorkFlowService();
		Map<String,Object> var = new HashMap<>();
		var.put("pass", status);
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);

		if (status == 1) {
			customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
		} else {
			customerVisit.setStatus(Customer.CUSTOMER_REJECT);
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode("_customer_visit_review");

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			kv.set("submit", toUser.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE, kv);
		}

		workFlowService.completeTask(taskId, comment, var);

		sendMessage(sellerId, comment, user.getId(), toUser.getId(), user.getDepartmentId(), user.getDataArea()
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName());

		if (customerVisit.saveOrUpdate())
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	public void queryCustomerType() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        String typeDataArea = DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea());
        List<Record> typeList = CustomerTypeQuery.me().findCustomerTypeList(typeDataArea);
        renderAjaxResultForSuccess("success",JSON.toJSON(typeList));
        //setAttr("customerTypeList", typeList);
	}

	public void queryQuestionType() {
		List<Dict> visitDictList = DictQuery.me().findDictByType("customer_visit");
		renderAjaxResultForSuccess("success", JSON.toJSON(visitDictList));
	}

	private void sendMessage(String sellerId, String comment, String fromUserId, String toUserId, String deptId
			, String dataArea, String type, String title) {
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(fromUserId);

		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);
		message.setType(type);

		message.setTitle(title);
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
	}
}
