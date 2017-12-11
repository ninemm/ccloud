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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.SalesRefundInstock;
import org.ccloud.model.User;
import org.ccloud.model.query.SalesOutstockDetailQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.SalesRefundInstockDetailQuery;
import org.ccloud.model.query.SalesRefundInstockQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/salesRefund", viewPath = "/WEB-INF/admin/sales_refund")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions("/admin/salesRefund")
public class _SalesRefundController extends JBaseCRUDController<SalesRefundInstock> {

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

		Page<Record> page = SalesRefundInstockQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate,
				endDate, null);

		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	public void detail() {

		String refundId = getPara(0);

		Record refund = SalesRefundInstockQuery.me().findMoreById(refundId);
		List<Record> refundDetail = SalesRefundInstockDetailQuery.me().findByRefundId(refundId);

		setAttr("refund", refund);
		setAttr("refundDetail", refundDetail);

		render("detail.html");

	}

	public void add() {

		render("add.html");

	}

	public void outstockIndex() {

		render("outstock_index.html");

	}
	
	public void outstockDetail() {

		String outstockId = getPara(0);

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);

		setAttr("outstock", outstock);
		setAttr("outstockDetail", outstockDetail);

		render("outstock_detail.html");

	}

	public void refund() {
		String outstockId = getPara("outstockId");

		Record outstock = SalesOutstockQuery.me().findMoreById(outstockId);
		List<Record> outstockDetail = SalesOutstockDetailQuery.me().findByOutstockId(outstockId);

		HashMap<String, Object> result = Maps.newHashMap();
		result.put("outstock", outstock);
		result.put("outstockDetail", outstockDetail);

		renderJson(result);
	}

	@Override
	@Before(Tx.class)
	public void save() {

		Map<String, String[]> paraMap = getParaMap();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr("sellerId");
		String sellerCode = getSessionAttr("sellerCode");
		String deptId = StringUtils.getArrayFirst(paraMap.get("deptId"));
		String dataArea = StringUtils.getArrayFirst(paraMap.get("dataArea"));

		String instockId = StrKit.getRandomUUID();
		Date date = new Date();
		String OrderSO = SalesRefundInstockQuery.me().getNewSn(sellerId);

		// SR + 100000(机构编号或企业编号6位) + A(客户类型) + W(仓库编号) + 171108(时间) + 100001(流水号)
		String instockSn = "SR" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
				+ StringUtils.getArrayFirst(paraMap.get("warehouseCode")) + DateUtils.format("yyMMdd", date) + OrderSO;

		SalesRefundInstockQuery.me().insert(paraMap, instockId, instockSn, sellerId, user.getId(), date, deptId,
				dataArea);

		String productNumStr = StringUtils.getArrayFirst(paraMap.get("productNum"));
		Integer productNum = Integer.valueOf(productNumStr);
		Integer count = 0;
		Integer index = 0;

		while (productNum > count) {
			index++;
			String sellProductId = StringUtils.getArrayFirst(paraMap.get("sellProductId" + index));
			if (StrKit.notBlank(sellProductId)) {
				SalesRefundInstockDetailQuery.me().insert(paraMap, instockId, sellerId, date, deptId, dataArea, index);

				count++;
			}

		}

		renderAjaxResultForSuccess();

	}
	
	@RequiresPermissions("/admin/salesRefund/check")
	@Before(Tx.class)
	public void pass() {

		String inStockId = getPara("inStockId");
		int status = getParaToInt("status");

		SalesRefundInstockQuery.me().updateConfirm(inStockId, status, new Date());

		renderAjaxResultForSuccess();

	}
	
}
