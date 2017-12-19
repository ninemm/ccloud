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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Comment;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.SalesOrder;
import org.ccloud.model.User;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.service.WorkFlowService;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesOrder", viewPath = "/WEB-INF/admin/sales_order")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesOrder")
public class _SalesOrderController extends JBaseCRUDController<SalesOrder> {

	@Override
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");

		setAttr("startDate", date);
		setAttr("endDate", date);

		render("index.html");
	}

	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}

		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr("sellerId");
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);

		Page<Record> page = SalesOrderQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate, sellerId, dataArea);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	public void detail() {

		String orderId = getPara(0);

		Record order = SalesOrderQuery.me().findMoreById(orderId);
		List<Record> orderDetail = SalesOrderDetailQuery.me().findByOrderId(orderId);

		setAttr("order", order);
		setAttr("orderDetail", orderDetail);

		render("detail.html");

	}

	public void add() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		if (user == null || StrKit.isBlank(sellerId)) {
			// TODO
		}

		List<Record> productlist = SalesOrderQuery.me().findProductListBySeller(sellerId);

		Map<String, Object> productInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();

		for (Record record : productlist) {
			Map<String, String> productOptionMap = new HashMap<String, String>();

			String sellProductId = record.getStr("id");
			String customName = record.getStr("custom_name");
			String speName = record.getStr("valueName");

			productInfoMap.put(sellProductId, record);

			productOptionMap.put("id", sellProductId);
			productOptionMap.put("text", customName + "/" + speName);

			productOptionList.add(productOptionMap);
		}

		List<Record> customerList = SalesOrderQuery.me().findCustomerListByUser(user.getId());

		Map<String, Object> customerInfoMap = new HashMap<String, Object>();
		List<Map<String, String>> customerOptionList = new ArrayList<Map<String, String>>();

		for (Record record : customerList) {
			Map<String, String> customerOptionMap = new HashMap<String, String>();

			String customerId = record.getStr("id");
			String customerName = record.getStr("customer_name");

			customerInfoMap.put(customerId, record);

			customerOptionMap.put("id", customerId);
			customerOptionMap.put("text", customerName);

			customerOptionList.add(customerOptionMap);
		}
		boolean isCheckStore = OptionQuery.me().findStoreCheck(Consts.OPTION_SELLER_STORE_CHECK, sellerId);
		setAttr("isCheckStore", isCheckStore);
		setAttr("productInfoMap", JSON.toJSON(productInfoMap));
		setAttr("productOptionList", JSON.toJSON(productOptionList));

		setAttr("customerInfoMap", JSON.toJSON(customerInfoMap));
		setAttr("customerOptionList", JSON.toJSON(customerOptionList));

		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

		render("add.html");
	}

	public void customerTypeById() {
		String customerId = getPara("customerId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);

		List<Record> customerTypeList = SalesOrderQuery.me().findCustomerTypeListByCustomerId(customerId,
				DataAreaUtil.getDealerDataAreaByCurUserDataArea(user.getDataArea()));
		setAttr("customerTypeList", customerTypeList);
		renderJson(customerTypeList);
	}

	@Override
	public synchronized void save() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");

		if (this.saveOrder(paraMap, user, sellerId, sellerCode)) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("库存不足或仓库中未找到对应商品");
		}
	}
	
	private boolean saveOrder(final Map<String, String[]> paraMap, final User user, 
			final String sellerId, final String sellerCode) {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
        		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
        		Integer productNum = Integer.valueOf(productNumStr);
        		Integer count = 0;
        		Integer index = 0;
        		
        		String orderId = StrKit.getRandomUUID();
        		Date date = new Date();
        		String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);

        		// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
        		String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
        				+ DateUtils.format("yyMMdd", date) + OrderSO;

        		if(!SalesOrderQuery.me().insert(paraMap, orderId, orderSn, sellerId, user.getId(), date, user.getDepartmentId(),
        				user.getDataArea())) {
        			return false;
        		}

        		while (productNum > count) {
        			index++;
        			String productId = StringUtils.getArrayFirst(paraMap.get("productId" + index));
        			if (StrKit.notBlank(productId)) {
        				if(!SalesOrderDetailQuery.me().insert(paraMap, orderId, sellerId, user.getId(), date,
        						user.getDepartmentId(), user.getDataArea(), index)) {
        					return false;
        				}
        				count++;
        			}

        		}
            	return true;
            }
        });
        return isSave;
	}
	
	@RequiresPermissions("/admin/salesOrder/check")
	@Before(Tx.class)
	public void pass() {

		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");

		SalesOutstockQuery.me().pass(orderId, user.getId(), sellerId, sellerCode);

		renderAjaxResultForSuccess();

	}

	@RequiresPermissions("/admin/salesOrder/check")
	@Before(Tx.class)
	public void reject() {

		String orderId = getPara("orderId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		SalesOrderQuery.me().updateConfirm(orderId, 1002, user.getId(), new Date());// 已审核拒绝

		renderAjaxResultForSuccess();

	}
	
	public void start() {
		
		String orderId = getPara("orderId");
		WorkFlowService workflow = new WorkFlowService();
		String defKey = "_order_review";
		
		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Map<String, Object> param = Maps.newHashMap();
		param.put("applyUsername", user.getUsername());
		param.put("account", "zhangwu");
		
		String procInstId = workflow.startProcess(orderId, defKey, param);
		
		salesOrder.setProcKey(defKey);
		salesOrder.setStatus(1);
		salesOrder.setProcInstId(procInstId);
		salesOrder.set("modify_date", new Date());
		salesOrder.update();
		
		renderAjaxResultForSuccess();
	}
	
	public void audit() {

		keepPara();
		
		boolean isCheck = false;
		String id = getPara("id");

		SalesOrder salesOrder = SalesOrderQuery.me().findById(id);
		setAttr("salesOrder", salesOrder);
		
//		HistoricTaskInstanceQuery query = ActivitiPlugin.buildProcessEngine().getHistoryService()  
//                .createHistoricTaskInstanceQuery();  
//        query.orderByProcessInstanceId().asc();  
//        query.orderByHistoricTaskInstanceEndTime().desc();  
//        List<HistoricTaskInstance> list = query.list();  
//        for (HistoricTaskInstance hi : list) {  
//            System.out.println(hi.getAssignee() + " " + hi.getName() + " "  
//                    + hi.getStartTime());  
//        }
        
        String taskId = getPara("taskId");
        List<Comment> comments = WorkFlowService.me().getProcessComments(taskId);
		setAttr("comments", comments);
		
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		if (user != null && StrKit.equals(getPara("assignee"), user.getUsername())) {
			isCheck = true;
		}
		setAttr("isCheck", isCheck);
	}
	
	public void complete() {
		SalesOrder salesOrder = getModel(SalesOrder.class);

		String taskId = getPara("taskId");
		String comment = getPara("comment");
		Integer pass = getParaToInt("pass", 1);
		
		Map<String, Object> var = Maps.newHashMap();
		var.put("pass", pass);
		var.put("orderId", salesOrder.getId());
		
		WorkFlowService workflowService = new WorkFlowService();
		workflowService.completeTask(taskId, comment, var);

		renderAjaxResultForSuccess("订单审核成功");
	}
	
	public void cancel() {
		
		String orderId = getPara("orderId");
		SalesOrder salesOrder = SalesOrderQuery.me().findById(orderId);
		WorkFlowService workflow = new WorkFlowService();
		
		String procInstId = salesOrder.getProcInstId();
		if (StrKit.notBlank(procInstId))
			workflow.deleteProcessInstance(salesOrder.getProcInstId());
		
		salesOrder.setStatus(Consts.SALES_ORDER_STATUS_CANCEL);
		
		if (!salesOrder.saveOrUpdate()) {
			renderAjaxResultForError("取消订单失败");
			return ;
		}
		
		renderAjaxResultForSuccess();
	}
	
	
}
