package org.ccloud.front.controller;

import java.util.List;

import org.ccloud.core.BaseFrontController;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.route.RouterMapping;

import com.jfinal.plugin.activerecord.Record;

/**
 * Created by WT on 2017/11/30.
 */
@RouterMapping(url = "/product")
public class ProductController extends BaseFrontController {

	public void index() {

		String sellerId = "739cbc22c4484a9bb84622ed4ccc0541";
		List<Record> productTypeList = SellerProductQuery.me().findProductTypeBySellerForApp(sellerId);
		setAttr("productTypeList", productTypeList);
		render("product.html");
	}

	public void productList() {

		String sellerId = "739cbc22c4484a9bb84622ed4ccc0541";
		String typeId = getPara("typeId");
		String keyword = getPara("keyword");
		List<Record> productList = SellerProductQuery.me().findProductListForApp(sellerId, typeId, keyword);
		renderJson(productList);
	}

	public void shoppingCart() {


		render("shopping_cart.html");
	}

}
