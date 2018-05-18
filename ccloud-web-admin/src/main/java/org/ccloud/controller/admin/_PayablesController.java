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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Payables;
import org.ccloud.model.PayablesDetail;
import org.ccloud.model.Payment;
import org.ccloud.model.SellerCustomer;
//import org.ccloud.model.PurchaseInstock;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.PayablesDetailQuery;
import org.ccloud.model.query.PayablesQuery;
import org.ccloud.model.query.PaymentQuery;
import org.ccloud.model.query.SellerCustomerQuery;
//import org.ccloud.model.query.PurchaseInstockQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.payablesExcel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/payables", viewPath = "/WEB-INF/admin/payables")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PayablesController extends JBaseCRUDController<Payables> { 
	
	public void getOptions(){
		String DataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Record> list = new ArrayList();
		list = CustomerTypeQuery.me().getCustomerTypes(DataArea);
		renderJson(list);
	}
	
	public void index() {
		String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
		setAttr("startDate", date);
		setAttr("endDate", date);
	}
	
	public void getPayables() {
		String keyword = getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String customerTypeId = getPara("customerTypeId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		Page<Record> page = PayablesQuery.me().paginate(getPageNumber(),getPageSize(),customerTypeId,user.getId(),deptDataArea,sellerId,user.getDepartmentId(),keyword);
		List<Record> payList = page.getList();
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"rows", payList);
		renderJson(map);
	}
	
	public void getpayablesDetail() {
		String id = getPara("id");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> map;
		if(!id.equals("")) {
			SellerCustomer customer = SellerCustomerQuery.me().findById(id);
			Page<PayablesDetail> page = new Page<>();
			if(customer!=null) {
				page = PayablesDetailQuery.me().paginate(getPageNumber(), getPageSize(), id,deptDataArea,startDate,endDate);
			}else {
				page = PayablesDetailQuery.me().paginateSeller(getPageNumber(), getPageSize(), id,deptDataArea,startDate,endDate);
			}
			map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		}else {
			map = new HashMap<String, Object>();
		}
		
		renderJson(map);
	}
	
	
	public void renderlist() {
		String ref_sn = getPara("ref_sn");
		String ref_type = getPara("ref_type");
		String obj_type = "";
		if(ref_type.equals(Consts.BIZ_TYPE_INSTOCK)){
			obj_type = Consts.RECEIVABLES_OBJECT_TYPE_SUPPLIER;
		}else{
			obj_type = Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER;
		}
		String object_id = getPara("object_id");
		String balance_amount = getPara("balance_amount");
		String pay_amount = getPara("pay_amount");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		//通过客户Id找到应收账款主表ID
		Payables payables = new Payables();
		if(ref_type.equals(Consts.BIZ_TYPE_INSTOCK)) {
			payables = PayablesQuery.me().findByObjIdAndDeptId(object_id, obj_type,user.getDepartmentId());
		}else {
			payables = PayablesQuery.me().findByObjIdAndDeptId(object_id, obj_type);
		}
//		PurchaseInstock purchaseInstock = PurchaseInstockQuery.me().findBySn(ref_sn);
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		Page<Payment> page = PaymentQuery.me().paginate(getPageNumber(), getPageSize(), ref_sn,deptDataArea);
		
		BigDecimal actAmount = new BigDecimal(0);
		for(int i = 0; i < page.getList().size(); i++) {
			actAmount = actAmount.add(page.getList().get(i).getActAmount());
		}
		balance_amount = (new BigDecimal(pay_amount).subtract(actAmount)).toString();
		String userDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<User> list = UserQuery.me().findIdAndNameByDataArea(userDataArea);
	
		setAttr("ref_sn",ref_sn);
		setAttr("balance_amount",balance_amount);
		setAttr("bill_id",payables.getId());
		setAttr("ref_type",ref_type);
		setAttr("type", payables.getObjType());
		setAttr("object_id", object_id);
		setAttr("userInfo",JsonKit.toJson(list));
		render("list.html");
	}
		
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String ref_sn = getPara("ref_sn");
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		Page<Payment> page = PaymentQuery.me().paginate(getPageNumber(), getPageSize(), ref_sn,deptDataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"ref_sn",ref_sn,"rows", page.getList());
		
		renderJson(map);
	}
	
	
	@Override
	public void save() {
		boolean isAdd = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException{
				
				Payment payment = new Payment();
				String payment_id = StrKit.getRandomUUID();
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
				String act_amount = getPara("act_amount");
				String ref_sn = getPara("ref_sn");
				String ref_type = getPara("ref_type");
				String pay_user_id = getPara("pay_user");
				Date date = new Date();
				
				payment.set("id", payment_id);
				payment.set("payables_detail_id", getPara("bill_id"));
				payment.set("act_amount", act_amount);
				payment.set("biz_date", getPara("biz_date"));
				payment.set("ref_sn", ref_sn);
				payment.set("ref_type", ref_type);
				payment.set("input_user_id", user.getId());
				payment.set("pay_user_id",pay_user_id);
				payment.set("remark",getPara("remark"));
				payment.set("data_area",user.get("data_area"));
				payment.set("dept_id",user.get("department_id"));
				payment.set("create_date", date);
				payment.set("modify_date", date);

//				ReceivablesDetailQuery.me().updateAmountByRefSn(ref_sn,act_amount);
				PayablesQuery.me().updateAmountById(getPara("bill_id"),act_amount);
			    return payment.save();
			}
		});
		
		if (isAdd) renderAjaxResultForSuccess("添加付款记录成功");
        else renderAjaxResultForError("添加付款记录失败");
	}
	
	//导出应付记录
	public void downloading() throws UnsupportedEncodingException {
//		String type = getPara("type");
		String keyword=getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = URLDecoder.decode(keyword, "UTF-8");;
		}
		String customerTypeId = getPara("customerTypeId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";	
		
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\payables\\"
				+ "应付账款.xlsx";
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		Page<Record> page = PayablesQuery.me().paginate(1,Integer.MAX_VALUE,customerTypeId,user.getId(),deptDataArea,sellerId,user.getDepartmentId(),keyword);
		List<Record> payablesList = page.getList();
		
		List<payablesExcel> excellist = Lists.newArrayList();
		for (Record record : payablesList) {
		
			payablesExcel excel = new payablesExcel();
			if(StrKit.isBlank(record.getStr("customerTypeNames"))) {
				excel.setCustomerType("供应商");
			}else {
				excel.setCustomerType(record.getStr("customerTypeNames"));
			}
			excel.setCustomerName(record.getStr("name"));
			excel.setPayAmount(record.getBigDecimal("pay_amount"));
			excel.setActAmount(record.getBigDecimal("act_amount"));
			excel.setBalanceAmount(record.getBigDecimal("balance_amount"));
			excellist.add(excel);
		}
		
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, payablesExcel.class, excellist);
		File file = new File(filePath.replace("\\", "/"));
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
		
		renderFile(new File(filePath.replace("\\", "/")));
	} 
	
}
