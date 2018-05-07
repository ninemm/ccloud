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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.model.Receivables;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.ReceivablesQuery;
import org.ccloud.model.ReceivablesDetail;
import org.ccloud.model.query.ReceivablesDetailQuery;
import org.ccloud.model.Receiving;
import org.ccloud.model.query.ReceivingQuery;
//import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.receivablesExcel;
import org.ccloud.model.User;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/receivables", viewPath = "/WEB-INF/admin/receivables")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ReceivablesController extends JBaseCRUDController<Receivables> { 
	
	public void getOptions(){
//		String type = getPara("type");
		String DataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Record> list = new ArrayList();
//		if(type != null) {
//			if("1".equals(type)) {
//			}else if("2".equals(type)) {
//				list = GoodsCategoryQuery.me().getLeafTypes();
//			}
//		}
		list = CustomerTypeQuery.me().getCustomerTypes(DataArea);
		renderJson(list);
	}
	
	public void getReceivables() {
//		String type = getPara("type");
		String keyword=getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String customerTypeId = getPara("customerTypeId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<Record> page = ReceivablesQuery.me().paginate(getPageNumber(),getPageSize(),customerTypeId,user.getId(),deptDataArea,sellerId,keyword);
		List<Record> receivablesList = page.getList();
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"rows", receivablesList);
		
		renderJson(map);
	}
	
	public void getReceivablesDetail() {
//		String type = getPara("type");
		String id = getPara("id");
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> map;
		if(id!=null) {
			Page<ReceivablesDetail> page = ReceivablesDetailQuery.me().paginate(getPageNumber(), getPageSize(), id,deptDataArea);
			map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		}else {
			map = new HashMap<String, Object>();
		}
		
		renderJson(map);
	}
	
	public void renderlist() {
//		String type = getPara("type");
		String ref_sn = getPara("ref_sn");
		String ref_type = getPara("ref_type");
		String object_id = getPara("object_id");
		String balance_amount = getPara("balance_amount");
		String receive_amount = getPara("receive_amount");
//		Record salesOutstock = SalesOutstockQuery.me().findMoreBySn(ref_sn);
		Receivables receivables = new Receivables();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		Page<Receiving> page = ReceivingQuery.me().paginate(1, Integer.MAX_VALUE, ref_sn,deptDataArea);
		BigDecimal actAmount = new BigDecimal(0);
		for(int i = 0; i < page.getList().size(); i++) {
			actAmount = actAmount.add(page.getList().get(i).getActAmount());
		}
		balance_amount = (new BigDecimal(receive_amount).subtract(actAmount)).toString();
		//通过客户Id找到应收账款主表ID
//		if("1".equals(type)) {
		receivables = ReceivablesQuery.me().findByObjId(object_id, Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
//		}else {
//			receivables = ReceivablesQuery.me().findByObjId(object_id, Consts.RECEIVABLES_OBJECT_TYPE_SUPPLIER);
//		}
		String userDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<User> list = UserQuery.me().findIdAndNameByDataArea(userDataArea);
	
		setAttr("balance_amount",balance_amount);
		setAttr("ref_sn",ref_sn);
		setAttr("bill_id",receivables.getId());
		setAttr("ref_type",ref_type);
		setAttr("object_id", object_id);
		setAttr("type", receivables.getObjectType());
		setAttr("userInfo",JsonKit.toJson(list));
		render("list.html");
	}
	
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String ref_sn = getPara("ref_sn");
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		Page<Receiving> page = ReceivingQuery.me().paginate(getPageNumber(), getPageSize(), ref_sn,deptDataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"ref_sn",ref_sn,"rows", page.getList());
		renderJson(map);
	}
		
	@Override
	public void save() {
		boolean isAdd = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException{
				
				Receiving receiving = new Receiving();
				String receiving_id = StrKit.getRandomUUID();
				User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
				String act_amount = getPara("act_amount");
				String ref_sn = getPara("ref_sn");
				String ref_type = getPara("ref_type");
				String receive_user_id = getPara("receive_user");
				Date date = new Date();
				
				receiving.set("id", receiving_id);
				receiving.set("bill_id", getPara("bill_id"));
				receiving.set("act_amount", act_amount);
				receiving.set("biz_date", getPara("biz_date"));
				receiving.set("ref_sn", ref_sn);
				receiving.set("ref_type", ref_type);
				receiving.set("input_user_id", user.getId());
				receiving.set("receive_user_id",receive_user_id);
				receiving.set("remark",getPara("remark"));
				receiving.set("data_area",user.get("data_area"));
				receiving.set("dept_id",user.get("department_id"));
				receiving.set("create_date", date);
				receiving.set("modify_date", date);

//				ReceivablesDetailQuery.me().updateAmountByRefSn(ref_sn,act_amount);
				ReceivablesQuery.me().updateAmountById(getPara("bill_id"),act_amount);
			    return receiving.save();
			}
		});
		
		if (isAdd) renderAjaxResultForSuccess("添加收款记录成功");
        else renderAjaxResultForError("添加收款记录失败");
	}
	
	//导出应收记录
	public void downloading() throws UnsupportedEncodingException {
//		String type = getPara("type");
		String keyword=getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String customerTypeId = getPara("customerTypeId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String deptDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";	
		
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\receivables\\"
				+ "应收账款.xlsx";
		Page<Record> page = ReceivablesQuery.me().paginate(1,Integer.MAX_VALUE,customerTypeId,user.getId(),deptDataArea,sellerId,keyword);
		List<Record> receivablesList = page.getList();
		
		List<receivablesExcel> excellist = Lists.newArrayList();
		for (Record record : receivablesList) {
		
			receivablesExcel excel = new receivablesExcel();
			excel.setCustomerType(record.getStr("customerTypeNames"));
			excel.setCustomerName(record.getStr("name"));
			excel.setReceiveAmount(record.getBigDecimal("receive_amount"));
			excel.setActAmount(record.getBigDecimal("act_amount"));
			excel.setBalanceAmount(record.getBigDecimal("balance_amount"));
			excellist.add(excel);
		}
		
		ExportParams params = new ExportParams();
		Workbook wb = ExcelExportUtil.exportBigExcel(params, receivablesExcel.class, excellist);
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
	
	public void  totalAmount() {
		render("total_amount.html");
	}
	
	public void getTotalAmount() {
		String keyword=getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
		}
		String sort = getPara("sort");
		String sortOrder = getPara("sortOrder");
		String customerTypeId = getPara("customerTypeId");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<Record> page = ReceivablesQuery.me().paginateTotalAmount(getPageNumber(),getPageSize(),customerTypeId,deptDataArea,sellerId,keyword,sort,sortOrder);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"rows", page.getList());
		
		renderJson(map);
	}
}
