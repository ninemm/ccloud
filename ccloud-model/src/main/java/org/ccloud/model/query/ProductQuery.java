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

import com.jfinal.plugin.ehcache.IDataLoader;
import org.ccloud.model.Product;
import org.ccloud.model.vo.ProductInfo;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

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
	
	public List<ProductInfo> getAllProductInfoById(String sellerProductId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName,d.balance_count ");
		fromBuilder.append(" FROM cc_product p ");
		fromBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.product_id = p.id ");
		fromBuilder.append(" LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append(" LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append(" LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append(" LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append(" RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = sp.product_id ");
		fromBuilder.append(" LEFT JOIN cc_inventory c2 ON p.id = c2.product_id  INNER JOIN cc_inventory_detail d on d.sell_product_id = sp.id");
		fromBuilder.append(" and c2.warehouse_id = d.warehouse_id  and d.sell_product_id =? GROUP BY d.sell_product_id");
		List<Record> list = Db.find(fromBuilder.toString(),sellerProductId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			pro.setBalanceCount(record.getBigDecimal("balance_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	public Page<Product> paginate_pro(int pageNumber, int pageSize,String keyword, String orderby,String sellerId,String userId) {
		String select = "SELECT cp.id,cp.big_unit,cp.small_unit,cp.convert_relate,cp.cost,cp.market_price,cp.name,cp.price, GROUP_CONCAT(cgs.`name` order by css.id) as cps_name ";
		StringBuilder fromBuilder = new StringBuilder("FROM cc_product cp LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id=cgs.id "
			 	     									+ " LEFT JOIN cc_goods_specification css on css.id = cgs.goods_specification_id "
			 	     									+ " LEFT JOIN cc_goods cg on cg.id=cp.goods_id "
			 	     									+ "LEFT JOIN cc_brand cb on cb.id=cg.brand_id "
			 	     									+ "LEFT JOIN cc_seller_brand csb on csb.brand_id = cb.id "
			 	     									+ "LEFT JOIN cc_seller cs on cs.id = csb.seller_id  "
			 	     									+ "LEFT JOIN `user` u on u.department_id = cs.dept_id ");
		
		LinkedList<Object> params = new LinkedList<Object>();
		if(!keyword.equals("")){
			appendIfNotEmptyWithLike(fromBuilder, "cp.name", keyword, params, true);
			fromBuilder.append(" and cp.is_marketable=1 and u.id='"+userId+"' ");
	//				+ " and cs.seller_type = 0 and cp.id  not in (select product_id from cc_seller_product where seller_id ='" + sellerId+"')");
		}else{
			fromBuilder.append(" where cp.is_marketable=1 and u.id='"+userId+"' ");
	//				+ " and cs.seller_type = 0 and cp.id  not in (select product_id from cc_seller_product where seller_id ='" + sellerId+"')");
		}
		fromBuilder.append(" GROUP by cp.id ");
		fromBuilder.append(" order by " + orderby);	
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public List<Product> findAllByUserId(String userId,String dataArea){
		String sql = "SELECT cp.id AS id,cp.NAME AS name,csp.store_count,cp.big_unit as big_unit, cp.small_unit as small_unit,cp.convert_relate as convert_relate,cp.price AS price,GROUP_CONCAT(DISTINCT cgs.`name`) AS cps_name "
				+ "FROM	cc_product cp LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id "
				+ "LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ "LEFT JOIN cc_seller_product csp on csp.product_id=cp.id "
				+ "LEFT JOIN cc_seller cs on cs.id=csp.seller_id "
				+ "LEFT JOIN `user` u on u.department_id=cs.dept_id "
				+ "WHERE u.id=? and cs.seller_type=0 and u.data_area=? GROUP BY cp.id";
		return DAO.find(sql, userId, dataArea);
	}
	
	public List<Product> findAllByUser(String userId,String dataArea,String supplierId){
		String sql = "SELECT cp.id AS id,cp.NAME AS name,csp.store_count,cp.cost,cp.big_unit as big_unit, cp.small_unit as small_unit,cp.convert_relate as convert_relate,cp.price AS price,GROUP_CONCAT(DISTINCT cgs.`name`) AS cps_name "
				+ "FROM	cc_product cp LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id "
				+ "LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ "LEFT JOIN cc_seller_product csp on csp.product_id=cp.id "
				+ "LEFT JOIN cc_goods cg on cg.id=cp.goods_id "
				+ "LEFT JOIN cc_brand cb on cb.id=cg.brand_id "
				+ "WHERE cb.supplier_id=? and cp.is_marketable=1 GROUP BY cp.id ORDER BY cp.NAME";
		return DAO.find(sql, supplierId);
	}
	
	public Product findByPId(String id){
		return DAO.findById(id);
	}
	public Product findByUserId(String userId,String productId){
		String sql = "SELECT cp.id AS id,cp.NAME AS name,cp.big_unit as big_unit, cp.small_unit as small_unit,cp.convert_relate as convert_relate,cp.price AS price,GROUP_CONCAT(DISTINCT cgs.`name`) AS cps_name "
				+ "FROM	cc_product cp LEFT JOIN cc_product_goods_specification_value cpg ON cp.id = cpg.product_set_id "
				+ "LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ "WHERE  cp.id='"+productId+"' GROUP BY cp.id";
		return DAO.findFirst(sql);
	}

	public List<ProductInfo> getAllProductInfoBySellerId(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp ON sp.product_id = p.id ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id where sp.is_enable=1 and sp.seller_id=?");
		List<Record> list = Db.find(fromBuilder.toString(),sellerId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	
	/**
	 * 
	* @Title: 盘点单新增时候获取该仓库产品库存
	* @Description: TODO
	* @param @param sellerId
	* @param @param warehouseId
	* @param @return   
	* @return List<ProductInfo>    
	* @throws
	 */
	public List<ProductInfo> getAllProductInforStockTaking(String sellerId,String warehouseId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName, t2.balance_count ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp ON sp.product_id = p.id ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id ");
		fromBuilder.append("LEFT JOIN (SELECT * FROM cc_inventory_detail t WHERE t.create_date = (SELECT max(create_date) FROM cc_inventory_detail WHERE t.sell_product_id = sell_product_id AND warehouse_id =?)  GROUP BY t.sell_product_id ) t2 ON t2.sell_product_id = sp.id ");
		fromBuilder.append("where sp.is_enable=1 and sp.seller_id=? ORDER BY sp.order_list");
		List<Record> list = Db.find(fromBuilder.toString(),warehouseId,sellerId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			pro.setBalanceCount(record.getBigDecimal("balance_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	/**
	 * 
	* @Title: 通过SellerId和仓库Id去库存总账里面查询商品的信息 
	* @Description: TODO
	* @param @param sellerId
	* @param @param warehouseId
	* @param @return   
	* @return List<ProductInfo>    
	* @throws
	 */
	public List<ProductInfo> getProductInfoByInventory(String sellerId,String warehouseId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName,IFNULL(t2.balance_count,0) balance_count ");
		fromBuilder.append("FROM ( SELECT( IFNULL(SUM(c.in_count) , 0) - IFNULL(SUM(c.out_count) , 0)) balance_count , c.sell_product_id FROM cc_inventory_detail c WHERE c.warehouse_id =? GROUP BY c.sell_product_id , c.warehouse_id) t2 ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp ON t2.sell_product_id = sp.id ");
		fromBuilder.append("LEFT JOIN cc_product p  ON sp.product_id = p.id ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = sp.product_id ");
		
		if (null!=sellerId) {
			fromBuilder.append(" WHERE sp.seller_id ='"+sellerId+"'");
		}
		fromBuilder.append(" GROUP BY t2.sell_product_id  ORDER BY sp.order_list");
		List<Record> list = Db.find(fromBuilder.toString(),warehouseId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			pro.setBalanceCount(record.getBigDecimal("balance_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	//库存调拨  编辑时  回显数据
	public List<ProductInfo> getProductBySellerProId(String sellerProId,String warehouseId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName,d.balance_count ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp ON sp.product_id = p.id ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = sp.product_id  INNER JOIN cc_inventory c2 ON p.id = c2.product_id  INNER JOIN cc_inventory_detail d on d.sell_product_id = sp.id ");
		fromBuilder.append("and c2.warehouse_id = d.warehouse_id  and d.sell_product_id =? AND c2.warehouse_id =?  GROUP BY d.sell_product_id");
		List<Record> list = Db.find(fromBuilder.toString(),sellerProId,warehouseId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			pro.setBalanceCount(record.getBigDecimal("balance_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	
	public List<ProductInfo> getProductBySellerProId(String sellerProId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT p.create_date as createDate,sp.id as sellerProductId, p.cost, p.is_marketable as isMarketable, p.market_price as marketPrice, p.`name`, p.price, sp.store_count,p.id as productId, ");
		fromBuilder.append("p.product_sn as productSn,sp.custom_name as customName, p.store, p.store_place,p.big_unit as bigUnit, p.weight, p.weight_unit as weightUnit, g.`code`, b.`name` as brandName, c.`name` as categoryName, t1.valueName ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN cc_seller_product sp ON sp.product_id = p.id ");
		fromBuilder.append("LEFT JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append("LEFT JOIN cc_brand b ON g.brand_id = b.id ");
		fromBuilder.append("LEFT JOIN cc_goods_category c ON g.goods_category_id = c.id ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id where sp.is_enable=1 and sp.store_count is not null and sp.id=?");
		List<Record> list = Db.find(fromBuilder.toString(),sellerProId);	
		List<ProductInfo> plist = new ArrayList<>();
		for (Record record : list) {
			ProductInfo pro = new ProductInfo();
			pro.setCustomName(record.getStr("customName"));
			pro.setBigUnit(record.getStr("bigUnit"));
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
			pro.setSellerProductId(record.getStr("sellerProductId"));
			pro.setSpecificationValue(record.getStr("valueName"));
			pro.setStoreCount(record.getBigDecimal("store_count"));
			plist.add(pro);
		}
		return plist;
	}
	
	public List<Product> findAllProduct(String goodsType){
		//String select ="select distinct name from cc_product ";
		String select ="select distinct cp.`name` from cc_product cp left join cc_goods cg on cp.goods_id = cg.id left join cc_goods_type cgt on cg.goods_type_id = cgt.id where cgt.id = '"+goodsType+"'";
		return DAO.find(select);
	}
	
	public List<Record> findProductByGoodsId(String goodsId){
		StringBuilder fromBuilder = new StringBuilder("SELECT p.*, t1.valueName ");
		fromBuilder.append("FROM cc_product p ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = p.id where p.goods_id=?");
		return Db.find(fromBuilder.toString(), goodsId);
	}	
	
	public Product findbyProductSn(String productSn) {
		return DAO.doFindFirst("product_sn = ?",productSn);
	}
  	
}
