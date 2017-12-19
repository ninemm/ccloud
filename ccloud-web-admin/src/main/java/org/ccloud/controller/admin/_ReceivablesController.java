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

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.model.Receivables;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.GoodsCategoryQuery;
import org.ccloud.model.query.ReceivablesQuery;
import org.ccloud.model.ReceivablesDetail;
import org.ccloud.model.query.ReceivablesDetailQuery;
import org.ccloud.model.Receiving;
import org.ccloud.model.query.ReceivingQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.User;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
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
		String type = getPara("type");
		String DataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Record> list = new ArrayList();
		if(type != null) {
			if("1".equals(type)) {
				list = CustomerTypeQuery.me().getCustomerTypes(DataArea);
			}else if("2".equals(type)) {
				list = GoodsCategoryQuery.me().getLeafTypes();
			}
		}
		renderJson(list);
	}
	
	public void getReceivables() {
		String type = getPara("type");
		String customerTypeId = getPara("customerTypeId");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Page<Receivables> page = ReceivablesQuery.me().paginate(getPageNumber(),getPageSize(),customerTypeId,type,user.getId(),deptDataArea);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(),"rows", page.getList());
		
		renderJson(map);
	}
	
	public void getReceivablesDetail() {
		String type = getPara("type");
		String id = getPara("id");
		String deptDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> map;
		if(type != null && id!=null) {
			Page<ReceivablesDetail> page = ReceivablesDetailQuery.me().paginate(getPageNumber(), getPageSize(), id,type,deptDataArea);
			map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		}else {
			map = new HashMap<String, Object>();
		}
		
		renderJson(map);
	}
	
	public void renderlist() {
		String ref_sn = getPara("ref_sn");
		String ref_type = getPara("ref_type");
		String object_id = getPara("object_id");
		//通过客户Id找到应收账款主表ID
		Receivables receivables = ReceivablesQuery.me().findByObjId(object_id, Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
		
		String userDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<User> list = UserQuery.me().findIdAndNameByDataArea(userDataArea);
	
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
}
