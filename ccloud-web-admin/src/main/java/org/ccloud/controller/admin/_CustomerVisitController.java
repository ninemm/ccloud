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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.CustomerVisitQuery;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customerVisit", viewPath = "/WEB-INF/admin/customer_visit")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerVisitController extends JBaseCRUDController<CustomerVisit> { 
	
	@Override
	public void index() {
		render("customer_visit.html");
	}
	
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        String questionType = getPara("questionType");
        switch (questionType){
    	case "type":
    		questionType = "0";
    	break;
    	case "normal":
    		questionType = "10";
    	break;
    	case "unusual":
    		questionType = "11";
    	break;
    	case "other":
    		questionType = "19";
    	break;
    	default :
    		questionType = "0";
    	break;	
    }
        if (StrKit.notBlank(questionType)) {
        	questionType = StringUtils.urlDecode(questionType);
            setAttr("questionType", questionType);
        }
        String customerType = getPara("customerType");
        if (StrKit.notBlank(customerType)) {
        	customerType = StringUtils.urlDecode(customerType);
            setAttr("customerType", customerType);
        }
        Page<CustomerVisit> page = CustomerVisitQuery.me().paginate(getPageNumber(), getPageSize(), keyword, user.getDataArea()+"%", customerType, questionType, "id", "create_date");
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	public void audit_visit() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		Date date = new Date();
		 String visitId = getPara("visitId");
		 String review_solution = getPara("review_solution");
		 CustomerVisit customerVisit = CustomerVisitQuery.me().findById(visitId);
		 customerVisit.setStatus(1);
		 customerVisit.setSolution(review_solution);
		 customerVisit.setReviewId(user.getId());
		 customerVisit.setReviewDate(date);
		 customerVisit.setComment("");
		 customerVisit.setRLat(new BigDecimal("0.00"));
		 customerVisit.setRLng(new BigDecimal("0.00"));
		 customerVisit.setReviewAddress("");
		 boolean result = customerVisit.update();
		 if(result==true) {
			 renderAjaxResultForSuccess();
		 }else {
			 renderAjaxResultForError("审核更新失败");
		 }
	}
	
	public void queryCustomerType() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
        String typeDataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());
        List<Record> typeList = CustomerTypeQuery.me().findCustomerTypeList(typeDataArea);
        renderAjaxResultForSuccess("success",JSON.toJSON(typeList));
        //setAttr("customerTypeList", typeList);
	}
}
