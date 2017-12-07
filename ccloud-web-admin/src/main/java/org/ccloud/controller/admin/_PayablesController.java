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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Payables;
import org.ccloud.model.PayablesDetail;
import org.ccloud.model.Payment;
import org.ccloud.model.User;
import org.ccloud.model.query.PayablesDetailQuery;
import org.ccloud.model.query.PayablesQuery;
import org.ccloud.model.query.PaymentQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/payables", viewPath = "/WEB-INF/admin/account")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PayablesController extends JBaseCRUDController<Payables> { 
	
	@Override
	public void index() {
		render("payables.html");
	}
	
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        String customerType = getPara("customerType");
        if (StrKit.notBlank(customerType)) {
        	customerType = StringUtils.urlDecode(customerType);
            setAttr("customerType", customerType);
        }
        Page<Payables> page = PayablesQuery.me().paginate(getPageNumber(), getPageSize(),keyword,customerType,user.getDataArea()+"%",  "create_date");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void payableInfo() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String objId = getPara("objId");
        if (StrKit.notBlank(objId)) {
        	objId = StringUtils.urlDecode(objId);
            setAttr("objId", objId);
        }
        Page<PayablesDetail> page = PayablesDetailQuery.me().findByObjId(getPageNumber(), getPageSize(),objId,user.getDataArea()+"%",  "create_date");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void payment() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String detailId = getPara("detail_id");
        if (StrKit.notBlank(detailId)) {
        	detailId = StringUtils.urlDecode(detailId);
            setAttr("detailId", detailId);
        }
        Page<Payment> page = PaymentQuery.me().findByDetailId(getPageNumber(), getPageSize(),detailId,user.getDataArea()+"%", "id");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	@Override
	public void save() {
		String objId = getPara("objId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Payables qPayables = PayablesQuery.me().findByObjId(objId, user.getDepartmentId());
		if(qPayables!=null) {
			renderAjaxResultForError("创建失败，汇总记录重复.");
			return;
		}
		boolean saveStatus = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException{
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
				Date date = new Date();
				Payables payables = getModel(Payables.class);
				String payablesId = StrKit.getRandomUUID();
				payables.set("id", payablesId);
				payables.set("obj_id", getPara("objId"));
				payables.set("obj_type", getPara("objType"));
				payables.set("pay_amount", new BigDecimal("0.00"));
				payables.set("act_amount", new BigDecimal("0.00"));
				payables.set("balance_amount", new BigDecimal("0.00"));
				payables.set("dept_id", user.getDepartmentId());
				payables.set("data_area", user.getDataArea());
				payables.set("create_date", date);
				payables.set("modify_date", date);
				return payables.save();
			}
		});
		if (saveStatus) renderAjaxResultForSuccess("添加应付汇总记录成功");
        else renderAjaxResultForError("添加应付汇总记录失败");
	}
	
	public void savePayablesDetail() throws ParseException {
		String objId = getPara("objId");
		String refSn = getPara("refSn");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		PayablesDetail qPayablesDetail = PayablesDetailQuery.me().findByRefSn(objId, user.getDepartmentId(),refSn);
		if(qPayablesDetail!=null) {
			renderAjaxResultForError("创建失败，单据明细记录重复.");
			return;
		}
		Date date = new Date();
		PayablesDetail payablesDetail = getModel(PayablesDetail.class);
		String deptId = user.getDepartmentId();
		Payables payables = PayablesQuery.me().findByObjId(objId, deptId);
		if(payables!=null) {
			String detailId = StrKit.getRandomUUID();
			payablesDetail.set("id", detailId);
			payablesDetail.set("object_id", getPara("objId"));
			payablesDetail.set("object_type", getPara("objType"));
			payablesDetail.set("pay_amount", new BigDecimal(getPara("payAmount")));
			payablesDetail.set("act_amount", new BigDecimal(getPara("actAmount")));
			payablesDetail.set("balance_amount", new BigDecimal(getPara("balanceAmount")));
			payablesDetail.set("ref_sn", getPara("refSn"));
			payablesDetail.set("ref_type", getPara("refType"));
			payablesDetail.set("dept_id", deptId);
			payablesDetail.set("data_area", user.getDataArea());
			payablesDetail.set("create_date", date);
			payablesDetail.set("modify_date", date);
			payablesDetail.save();
			payables.set("pay_amount", payables.getPayAmount().add(new BigDecimal(getPara("payAmount"))));
			payables.set("act_amount", payables.getActAmount().add(new BigDecimal(getPara("actAmount"))));
			payables.set("balance_amount", payables.getBalanceAmount().add(new BigDecimal(getPara("balanceAmount"))));
			payables.setModifyDate(date);
			payables.update();
			renderAjaxResultForSuccess();
		}else {
			renderAjaxResultForError("未找到本次交易单位应付账款汇总数据.");
		}

	}
	
	public void savePayment() throws ParseException {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Date date = new Date();
		String objId = getPara("objId");
		String refSn = getPara("refSn");
		PayablesDetail payablesDetail = PayablesDetailQuery.me().findByObjId(objId, user.getDepartmentId(),refSn);
		Payables payables = PayablesQuery.me().findByObjId(objId, user.getDepartmentId());
		final Payment payment = getModel(Payment.class);
		if(payables==null) renderAjaxResultForError("未找到来往单位应付汇总数据记录.");
		if(payablesDetail!=null) {
			String paymentId = StrKit.getRandomUUID();
			payment.set("id", paymentId);
			payment.set("payables_detail_id", payablesDetail.getId());
			payment.set("act_amount", new BigDecimal(getPara("actAmount")));
			payment.set("biz_date", date);
			payment.set("ref_sn", refSn);
			payment.set("ref_type", getPara("refType"));
			payment.set("input_user_id", user.getId());
			payment.set("pay_user_id", getPara("payUserId"));
			payment.set("remark", getPara("remark"));
			payment.set("data_area", user.getDataArea());
			payment.set("dept_id", user.getDepartmentId());
			payment.set("create_date", date);
			payment.set("modify_date", date);
			payment.save();
			payablesDetail.set("act_amount", payablesDetail.getActAmount().add(new BigDecimal(getPara("actAmount"))));
			payablesDetail.set("balance_amount", payablesDetail.getBalanceAmount().subtract(new BigDecimal(getPara("actAmount"))));
			payablesDetail.set("modify_date", date);
			payablesDetail.update();
			payables.set("act_amount", payables.getActAmount().add(new BigDecimal(getPara("actAmount"))));
			payables.set("balance_amount", payables.getBalanceAmount().subtract(new BigDecimal(getPara("actAmount"))));
			payables.set("modify_date", date);
			payables.update();
			renderAjaxResultForSuccess();
		}else {
			renderAjaxResultForError("未找到本次单号的记录.");
		}
	}
}
