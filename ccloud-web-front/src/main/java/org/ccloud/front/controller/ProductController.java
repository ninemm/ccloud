package org.ccloud.front.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.core.BaseFrontController;
import org.ccloud.model.CustomerType;
import org.ccloud.model.User;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.SalesOrderDetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.route.RouterMapping;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/product")
public class ProductController extends BaseFrontController {

	String sellerId = "05a9ad0a516c4c459cb482f83bfbbf33";
	String sellerCode = "QG";
	User user = UserQuery.me().findById("1f797c5b2137426093100f082e234c14");
	String dataArea = DataAreaUtil.getUserDealerDataArea(user.getDataArea());

	public void index() {

		List<Record> productTypeList = SellerProductQuery.me().findProductTypeBySellerForApp(sellerId);
		setAttr("productTypeList", productTypeList);
		render("product.html");
	}

	public void productList() {

		String keyword = getPara("keyword");
		List<Record> productList = SellerProductQuery.me().findProductListForApp(sellerId, keyword);
		renderJson(productList);
	}

	public void shoppingCart() {

		render("shopping_cart.html");
	}

	public void order() {
		setAttr("deliveryDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		render("order.html");
	}

	public void customerChoose() {

		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");

		List<Map<String, Object>> userIds = new ArrayList<>();
		userIds.add(all);

		List<Record> userList = UserQuery.me().findNextLevelsUserList(user.getDataArea());
		for (Record record : userList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", record.get("realname"));
			item.put("value", record.get("id"));
			userIds.add(item);
		}

		List<Map<String, Object>> customerTypes = new ArrayList<>();
		customerTypes.add(all);

		List<CustomerType> customerTypeList = CustomerTypeQuery.me()
				.findByDataArea(DataAreaUtil.getUserDealerDataArea(user.getDataArea()));
		for (CustomerType customerType : customerTypeList) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", customerType.getName());
			item.put("value", customerType.getId());
			customerTypes.add(item);
		}

		setAttr("userIds", JSON.toJSON(userIds));
		setAttr("customerTypes", JSON.toJSON(customerTypes));
		render("customer_choose.html");
	}

	public void customerList() {

		String keyword = getPara("keyword");
		String userId = getPara("userId");
		String customerTypeId = getPara("customerTypeId");
		String isOrdered = getPara("isOrdered");

		Page<Record> customerList = SellerCustomerQuery.me().paginateForApp(getPageNumber(), getPageSize(), keyword,
				dataArea, userId, customerTypeId, isOrdered);

		Map<String, Object> map = new HashMap<>();
		map.put("customerList", customerList.getList());
		map.put("totalRow", customerList.getTotalRow());
		map.put("totalPage", customerList.getTotalPage());
		renderJson(map);
	}

	public void customerTypeById() {
		String customerId = getPara("customerId");

		List<Record> customerTypeList = SalesOrderQuery.me().findCustomerTypeListByCustomerId(customerId,
				DataAreaUtil.getUserDealerDataArea(user.getDataArea()));

		renderJson(customerTypeList);
	}

	public synchronized void salesOrder() {

		Map<String, String[]> paraMap = getParaMap();

		if (this.saveOrder(paraMap, user, sellerId, sellerCode)) {
			renderAjaxResultForSuccess("保存成功");
		} else {
			renderAjaxResultForError("库存不足或提交失败");
		}
	}

	private boolean saveOrder(final Map<String, String[]> paraMap, final User user, final String sellerId,
			final String sellerCode) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				String[] sellProductIds = paraMap.get("sellProductId");

				String orderId = StrKit.getRandomUUID();
				Date date = new Date();
				String OrderSO = SalesOrderQuery.me().getNewSn(sellerId);

				// 销售订单：SO + 100000(机构编号或企业编号6位) + A(客户类型) + 171108(时间) + 100001(流水号)
				String orderSn = "SO" + sellerCode + StringUtils.getArrayFirst(paraMap.get("customerTypeCode"))
						+ DateUtils.format("yyMMdd", date) + OrderSO;

				if (!SalesOrderQuery.me().insertForApp(paraMap, orderId, orderSn, sellerId, user.getId(), date,
						user.getDepartmentId(), user.getDataArea())) {
					return false;
				}

				for (int index = 0; index < sellProductIds.length; index++) {
					if (!SalesOrderDetailQuery.me().insertForApp(paraMap, orderId, sellerId, user.getId(), date,
							user.getDepartmentId(), user.getDataArea(), index)) {
						return false;
					}

				}
				return true;
			}
		});
		return isSave;
	}
	
	public void myOrder() {
		render("myOrder.html");
	}

	public void orderDetial() {
		render("myOrder.html");
	}

}
