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
package org.ccloud.model.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.Product;
import org.ccloud.model.vo.ProductInfo;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class ProductQuery extends JBaseQuery { 

	protected static final Product DAO = new Product();
	private static final ProductQuery QUERY = new ProductQuery();

	public static ProductQuery me() {
		return QUERY;
	}

	public Product findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public Page<Product> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_product` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}


	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public List<Product> findByGoodId(String id) {
		StringBuilder fromBuilder = new StringBuilder("SELECT a.*, (SELECT COUNT(*) FROM cc_seller_product b WHERE b.product_id = a.id) as count ");
		fromBuilder.append("FROM cc_product a where a.goods_id = ? ");
		return DAO.find(fromBuilder.toString(), id);
	}

	public int batchDelete(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.size(); i++) {
				if (DAO.deleteById(ids.get(i))) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}
	
	public List<ProductInfo> getAllProductInfo() {
 		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,p.id as productId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, ");
		fromBuilder.append("p.product_sn as productSn, p.store, p.store_place, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		List<Record> list = Db.find(fromBuilder.toString());	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setBrandName(record.getStr("brandName"));
			pro.setCategoryName(record.getStr("categoryName"));
			pro.setCode(record.getStr("code"));
			pro.setCost(record.getBigDecimal("cost"));
			pro.setCreateDate(record.getDate("createDate"));
			pro.setIsMarketable(record.getBoolean("isMarketable"));
			pro.setMarketPrice(record.getBigDecimal("marketPrice"));
			pro.setName(record.getStr("name"));
			pro.setPrice(record.getBigDecimal("price"));
			pro.setProductSn(record.getStr("productSn"));
			pro.setStore(record.getStr("store"));
			pro.setStorePlace(record.getStr("storePlace"));
			pro.setWeight(record.getStr("weight"));
			pro.setWeightUnit(record.getStr("weightUnit"));
			pro.setProductId(record.getStr("productId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			plist.add(pro);
		}
		return plist;
	}
	
	
	
	public List<ProductInfo> getAllProductInfoById(String id) {
 		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,p.id as productId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, ");
		fromBuilder.append("p.product_sn as productSn, p.store, p.store_place, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");		
	 	fromBuilder.append("WHERE p.id = ?");
		List<Record> list = Db.find(fromBuilder.toString(), id);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setBrandName(record.getStr("brandName"));
			pro.setCategoryName(record.getStr("categoryName"));
			pro.setCode(record.getStr("code"));
			pro.setCost(record.getBigDecimal("cost"));
			pro.setCreateDate(record.getDate("createDate"));
			pro.setIsMarketable(record.getBoolean("isMarketable"));
			pro.setMarketPrice(record.getBigDecimal("marketPrice"));
			pro.setName(record.getStr("name"));
			pro.setPrice(record.getBigDecimal("price"));
			pro.setProductSn(record.getStr("productSn"));
			pro.setStore(record.getStr("store"));
			pro.setStorePlace(record.getStr("storePlace"));
			pro.setWeight(record.getStr("weight"));
			pro.setWeightUnit(record.getStr("weightUnit"));
			pro.setProductId(record.getStr("productId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			plist.add(pro);
		}
		return plist;
	}
	
	public Page<Product> paginate_pro(int pageNumber, int pageSize,String keyword, String orderby,String sellerId) {
		String select = "SELECT cp.id,cp.cost,cp.market_price,cp.name,cp.price, GROUP_CONCAT(cgs.`name`) as cps_name ";
		StringBuilder fromBuilder = new StringBuilder("FROM cc_product cp LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id=cgs.id ");
		
		LinkedList<Object> params = new LinkedList<Object>();
		if(!keyword.equals("")){
			appendIfNotEmptyWithLike(fromBuilder, "cp.name", keyword, params, true);
			fromBuilder.append(" and cp.id  not in (select product_id from cc_seller_product where seller_id ='" + sellerId+"')");
		}else{
			fromBuilder.append(" where cp.id  not in (select product_id from cc_seller_product where seller_id ='" + sellerId+"')");
		}
		fromBuilder.append(" GROUP by " + orderby);
		fromBuilder.append(" order by " + orderby);	
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
}
