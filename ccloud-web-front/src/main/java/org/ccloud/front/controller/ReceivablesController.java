package org.ccloud.front.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.Receivables;
import org.ccloud.model.Receiving;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.ReceivablesDetailQuery;
import org.ccloud.model.query.ReceivablesQuery;
import org.ccloud.model.query.ReceivingQuery;
import org.ccloud.model.query.SalesOutstockQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DateUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by zengcheng on 2018/1/16.
 */

@RouterMapping(url = "/receivables")
public class ReceivablesController extends BaseFrontController {
	
	@RequiresPermissions("/admin/receivables")
	public void index() {
		
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);
		
		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString());
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}
		
		String history = getPara("history");
		setAttr("history", history);		
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("out_stock_list.html");		
	}
	
	@RequiresPermissions("/admin/receivables")
	public void stockOrder() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String keyword = getPara("keyword");

		String userId = getPara("status");
		String customerTypeId = getPara("customerTypeId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");

		Page<Record> outStockList = SalesOutstockQuery.me().paginateForReceivables(getPageNumber(), getPageSize(), keyword, userId,
				customerTypeId, startDate, endDate, sellerId, selectDataArea);

		Map<String, Object> map = new HashMap<>();
		map.put("outStockList", outStockList.getList());
		renderJson(map);
	}
	
	public void initUser() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);		
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Integer print = getParaToInt("print");
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		List<Map<String, Object>> userList = new ArrayList<>();
		userList.add(all);
		List<Record> list = SalesOutstockQuery.me().findReceivablesUserList(sellerId, startDate, endDate, print);
		for (Record record : list) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.getStr("realname"));
			item.put("value", record.getStr("id"));
			userList.add(item);
		}		
		Map<String, Object> map = new HashMap<>();
		map.put("userList", userList);		
		renderJson(map);
	}
	
	@RequiresPermissions("/admin/receivables")
	public void detail() {
		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		String outstock_sn = getPara("sn");
		Record receivable = ReceivablesDetailQuery.me().findByBalanceCountBySn(outstock_sn);
		List<Receiving> list = ReceivingQuery.me().findBySn(outstock_sn);
		List<User> userLists = UserQuery.me().findByDeptDataArea(dataArea + "%");
		
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
		
		List<Map<String, Object>> userList = new ArrayList<>();
		userList.add(all);
		
		for (User users : userLists) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", users.getRealname());
			item.put("value", users.getId());
			userList.add(item);
		}		
		
		setAttr("outstock_sn", outstock_sn);
		setAttr("receivable", receivable);
		setAttr("userList", JSON.toJSON(userList));
		setAttr("list", list);
		render("receivable_detail.html");
	}
	
	@RequiresPermissions("/admin/receivables")
	public void save() {
        boolean isSave = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
            	String userId = getPara("userId");
            	String outstock_sn = getPara("outstock_sn");
            	String money = getPara("money");
            	String deliveryDate = getPara("deliveryDate");
            	String remark = getPara("remark");
            	String ref_type = getPara("ref_type");
            	Receivables receivables = ReceivablesQuery.me().findBySn(outstock_sn);
            	
            	Receiving receiving = new Receiving();
				receiving.setId(StrKit.getRandomUUID());
				receiving.setBillId(receivables.getId());
				receiving.setActAmount(new BigDecimal(money));
				receiving.setBizDate(DateUtils.strToDate(deliveryDate, DateUtils.DEFAULT_FORMATTER));
				receiving.setRefSn(outstock_sn);
				receiving.setRefType(ref_type);
				receiving.setInputUserId(user.getId());
				receiving.setReceiveUserId(userId);
				receiving.setRemark(remark);
				receiving.setDataArea(user.getDataArea());
				receiving.setDeptId(user.getDepartmentId());
				receiving.setCreateDate(new Date());
				receiving.setModifyDate(new Date());
				
				if (!receiving.save()) {
					return false;
				}
				int i = ReceivablesQuery.me().updateAmountById(receivables.getId(), money);
				if (i <= 0) {
					return false;
				}
				
            	return true;
            }
        });
		if (isSave) {
			renderAjaxResultForSuccess("收款成功");
		} else {
			renderAjaxResultForError("系统错误保存失败");
		}
	}
	
}
