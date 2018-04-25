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

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.ccloud.model.Product;
import org.ccloud.model.SellerProduct;
import org.ccloud.utils.QRCodeUtils;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class SellerProductQuery extends JBaseQuery { 

	protected static final SellerProduct DAO = new SellerProduct();
	private static final SellerProductQuery QUERY = new SellerProductQuery();

	public static SellerProductQuery me() {
		return QUERY;
	}

	public SellerProduct findById(final String id) {
				return DAO.findById(id);
	}
	public SellerProduct findByProductId(String productId){
		String sql = "select * from cc_seller_product where product_id=?";
		return DAO.findFirst(sql.toString(), productId);
	}

	public Page<SellerProduct> paginate(int pageNumber, int pageSize,String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `cc_seller_product` ");

		LinkedList<Object> params = new LinkedList<Object>();
		
		appendIfNotEmptyWithLike(fromBuilder, "name", keyword, params, true);
		
		fromBuilder.append("order by " + orderby);	

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

	public Page<SellerProduct> paginate_sel(int pageNumber, int pageSize,String keyword,String userId,String sta,String sellerProductIds) {
		String select = "SELECT csp.*,cgc.name as cgc_name,cp.name as productName, csp.custom_name,csp.store_count,csp.price,cp.big_unit,cp.small_unit,cp.convert_relate,csp.is_enable, csp.order_list ,GROUP_CONCAT(distinct cgs.`name` order by css.id) AS cps_name";
		StringBuilder fromBuilder = new StringBuilder("from cc_seller_product csp LEFT JOIN cc_product cp ON  csp.product_id = cp.id LEFT JOIN cc_product_goods_specification_value cpg ON  cp.id = cpg.product_set_id "
				+ " LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id "
				+ " LEFT JOIN cc_goods_specification css on css.id = cgs.goods_specification_id "
				+ " LEFT JOIN cc_seller cs on cs.id=csp.seller_id"
				+ " LEFT JOIN user u on u.department_id =cs.dept_id "
				+ " LEFT JOIN cc_goods cg on cg.id = cp.goods_id "
				+ "LEFT JOIN cc_goods_category cgc on cgc.id = cg.goods_category_id");
		LinkedList<Object> params = new LinkedList<Object>();
		if(!keyword.equals("")){
			appendIfNotEmptyWithLike(fromBuilder, "csp.custom_name", keyword, params, true);
			fromBuilder.append(" and  cs.is_enabled=1 and u.id='"+userId+"' ");
		}else{
			fromBuilder.append(" where  cs.is_enabled=1 and u.id='"+userId+"' ");
		}
		if(sta.equals("1") && !sellerProductIds.equals("")) {
			fromBuilder.append(" and csp.id  not in ("+sellerProductIds+")");
		}
		fromBuilder.append(" GROUP BY csp.id ORDER BY csp.is_enable desc,csp.order_list,cgs.name,css.name ");
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	//查询这个销售商的所有商品  如果销售商是仓库创建者  可以看见所有仓库
	public List<SellerProduct> findBySellerId(String sellerId) {
 		StringBuilder fromBuilder = new StringBuilder("select cg.*,t1.valueName,ct.big_unit,ct.small_unit,ct.convert_relate from cc_seller_product cg ");
 		fromBuilder.append(" LEFT JOIN cc_product ct on ct.id = cg.product_id ");
		fromBuilder.append(" LEFT JOIN( SELECT sv.id , cv.product_set_id , GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append(" RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 ON ");
		fromBuilder.append(" t1.product_set_id = cg.product_id WHERE cg.seller_id = ? OR cg.id IN( SELECT id.sell_product_id FROM cc_inventory_detail id ");
		fromBuilder.append(" WHERE id.warehouse_id IN( SELECT w.id FROM cc_warehouse w WHERE w.seller_id = ? ) GROUP BY id.sell_product_id) ");
		return DAO.find(fromBuilder.toString(), sellerId, sellerId);
	}
	
	public List<SellerProduct> findProductBySellerId(String sellerId) {
 		StringBuilder fromBuilder = new StringBuilder("select cg.*,t1.valueName,ct.big_unit,ct.small_unit,ct.convert_relate from cc_seller_product cg ");
 		fromBuilder.append(" LEFT JOIN cc_product ct on ct.id = cg.product_id ");
		fromBuilder.append(" LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append(" RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = cg.product_id ");
		fromBuilder.append(" WHERE cg.seller_id = ? ");
		return DAO.find(fromBuilder.toString(), sellerId);
	}	

	public List<SellerProduct> findByProductIdAndSellerId(String seller_product_id, String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("select * from cc_seller_product where id=? and seller_id=?");
		return DAO.find(fromBuilder.toString(), seller_product_id,sellerId);
	}
	
	public List<Record> findProductTypeBySellerForApp(String sellerId) {
		StringBuilder fromBuilder = new StringBuilder(" SELECT gt.id, gt.`name` ");
		fromBuilder.append(" FROM cc_seller_product sp ");
		fromBuilder.append(" JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id ");
		fromBuilder.append(" JOIN cc_goods_category gc ON g.goods_category_id = gc.id ");
		fromBuilder.append(" JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1  AND sp.is_gift = 0 ");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, false);

		fromBuilder.append(" GROUP BY gt.id ");
		fromBuilder.append(" ORDER BY gt.`name` ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}
	
	public List<Record> findProductListForApp(String sellerId, String keyword, String tag, String categoryId) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sp.id AS sell_product_id, sp.product_id, sp.custom_name, sp.store_count, sp.price, sp.cost, sp.account_price, sp.tags,"
				+ " p.convert_relate, p.product_sn, p.big_unit, p.small_unit, p.description, t1.valueName,"
				+ " g.`name` AS goodsName, g.product_image_list_store, gc.`id` AS categoryId, gc.`name` AS categoryName, gt.`id` as typeId, gt.`name` as typeName ");
		fromBuilder.append(" FROM cc_seller_product sp JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv.`name`) AS valueName FROM cc_goods_specification_value sv RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id ) t1 ON t1.product_set_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id JOIN cc_goods_category gc ON g.goods_category_id = gc.id JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1 AND sp.is_gift = 0");

		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, false);
		appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, false);
		appendIfNotEmpty(fromBuilder, "gc.id", categoryId, params, false);
		if (StrKit.notBlank(tag)) {
			fromBuilder.append(" AND FIND_IN_SET(?, sp.tags)");
			params.add(tag);
		}

		fromBuilder.append(" ORDER BY gc.`parent_id`, gc.`order_list`, gc.`id`, sp.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}
	
	public List<SellerProduct> _findByProductIdAndSellerId(String product_id, String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("select * from cc_seller_product where product_id=? and seller_id=?");
		return DAO.find(fromBuilder.toString(), product_id,sellerId);
	}

	public List<SellerProduct> findByCompositionId(String productId) {
		StringBuilder stringBuilder = new StringBuilder("SELECT cc.*,cp.sub_product_count as productCount, cp.is_gift as isGift, cp.parent_id as parentId, cd.convert_relate FROM cc_seller_product cc ");
		stringBuilder.append("RIGHT JOIN cc_product_composition cp ON cp.sub_seller_product_id = cc.id ");
		stringBuilder.append("LEFT JOIN cc_product cd ON cd.id = cc.product_id ");
		stringBuilder.append("WHERE parent_id = ? ");
		stringBuilder.append("UNION ALL ");
		stringBuilder.append("SELECT cc.*,cp.main_product_count as productCount, 0 as isGift, cp.parent_id as parentId, cd.convert_relate FROM cc_seller_product cc ");
		stringBuilder.append("RIGHT JOIN cc_product_composition cp ON cp.seller_product_id = cc.id ");
		stringBuilder.append("LEFT JOIN cc_product cd ON cd.id = cc.product_id ");
		stringBuilder.append("WHERE parent_id = ? GROUP BY cp.parent_id");
		return DAO.find(stringBuilder.toString(), productId, productId);
	}
	
	public SellerProduct newProduct(String sellerId, Date date, String fomatDate, Product product, HttpServletRequest request) {
		SellerProduct sellerProduct = new SellerProduct();
		String sellerProductId = StrKit.getRandomUUID();
		sellerProduct.set("id", sellerProductId);
		sellerProduct.set("product_id", product.getId());
		sellerProduct.set("seller_id", sellerId);
		sellerProduct.set("custom_name", product.getName());
		sellerProduct.setStoreCount(new BigDecimal(0));
		sellerProduct.set("price", product.getPrice());
		sellerProduct.setCost(product.getPrice());
		sellerProduct.setAccountPrice(product.getPrice());
		sellerProduct.set("cost", product.getCost());
		sellerProduct.set("market_price", product.getMarketPrice());
		sellerProduct.set("weight", product.getWeight());
		sellerProduct.set("weight_unit", product.getWeightUnit());
		sellerProduct.setOrderList(0);
		;
		sellerProduct.set("is_enable", 1);
		sellerProduct.set("is_gift", 0);
		// 生成二维码
		String fileName = fomatDate + ".png";

		String contents = request.getScheme() + "://" + request.getServerName() + "/admin/seller/fu" + "?id="
				+ sellerProductId;
		// 部署之前上传
		// String contents = getRequest().getScheme() + "://" +
		// getRequest().getServerName()+":"+getRequest().getLocalPort()+getRequest().getContextPath()+"/admin/seller/fn"+"?id="+Id;
		String imagePath = request.getSession().getServletContext().getRealPath("\\qrcode\\");
		QRCodeUtils.genQRCode(contents, imagePath, fileName);
		sellerProduct.set("qrcode_url", imagePath + "\\" + fileName);
		sellerProduct.set("create_date", date);
		sellerProduct.save();
		return sellerProduct;
	}
	
	public List<SellerProduct> findBySellerIdAndIsEnable(String sellerId){
		StringBuilder fromBuilder = new StringBuilder("select cg.*,t1.valueName from cc_seller_product cg ");
		fromBuilder.append("LEFT JOIN  (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv. NAME) AS valueName FROM cc_goods_specification_value sv ");
		fromBuilder.append("RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id) t1 on t1.product_set_id = cg.product_id ");
		fromBuilder.append("WHERE cg.seller_id = ? and cg.is_enable = 1");
		return DAO.find(fromBuilder.toString(), sellerId);
	}

	//查询出该经销商的所有商品名称 和 换算关系
	public List<Record> findConvertRelate(String sellerId) {
		StringBuilder stringBuilder = new StringBuilder("SELECT sp.custom_name,p.convert_relate,sp.id ");
		stringBuilder.append(" FROM cc_seller s  ");
		stringBuilder.append(" LEFT JOIN cc_seller_product sp ON sp.seller_id=s.id ");
		stringBuilder.append(" LEFT JOIN cc_product p ON sp.product_id =p.id ");
		stringBuilder.append(" WHERE s.id='"+sellerId+"'");
		return  Db.find(stringBuilder.toString());
		
	}
	
	public SellerProduct findbyCustomerNameAndSellerIdAndProductId(String customName,String sellerId) {
		String sql = "select * from cc_seller_product where custom_name = ? and seller_id = ?";
		return DAO.findFirst(sql, customName,sellerId);
	}

	public SellerProduct findBySellerAndId(String sellerProductId, String sellerId) {
		String sql = "SELECT * FROM cc_seller_product s WHERE s.product_id IN( SELECT p.id FROM cc_seller_product sp LEFT JOIN cc_product p ON p.id = sp.product_id WHERE sp.id = ?) AND s.seller_id = ?";
		return DAO.findFirst(sql, sellerProductId,sellerId);
	}

	public List<Record> findProductListForAppByCar(String sellerId, String keyword, String tag, String wareHouseId, String categoryId) {
		StringBuilder fromBuilder = new StringBuilder(
				" SELECT sp.id AS sell_product_id, sp.product_id, sp.custom_name, sp.price, sp.cost, sp.account_price, sp.tags,"
				+ " p.convert_relate, p.product_sn, p.big_unit, p.small_unit, p.description, t1.valueName,"
				+ " g.`name` AS goodsName, g.product_image_list_store, gc.`id` AS categoryId, gc.`name` AS categoryName, gt.`id` as typeId, gt.`name` as typeName, ");
		fromBuilder.append(" IFNULL((SELECT i.balance_count FROM cc_inventory_detail i where i.sell_product_id = sp.id and i.warehouse_id = ? order by i.create_date desc limit 1),0) as store_count");
		fromBuilder.append(" FROM cc_seller_product sp JOIN cc_product p ON sp.product_id = p.id ");
		fromBuilder.append(" LEFT JOIN (SELECT sv.id, cv.product_set_id, GROUP_CONCAT(sv.`name`) AS valueName FROM cc_goods_specification_value sv RIGHT JOIN cc_product_goods_specification_value cv ON cv.goods_specification_value_set_id = sv.id GROUP BY cv.product_set_id ) t1 ON t1.product_set_id = p.id ");
		fromBuilder.append(" JOIN cc_goods g ON p.goods_id = g.id JOIN cc_goods_category gc ON g.goods_category_id = gc.id JOIN cc_goods_type gt on g.goods_type_id = gt.id ");
		fromBuilder.append(" WHERE sp.is_enable = 1 AND sp.is_gift = 0");

		LinkedList<Object> params = new LinkedList<Object>();
		params.add(wareHouseId);
		appendIfNotEmpty(fromBuilder, "sp.seller_id", sellerId, params, false);
		appendIfNotEmptyWithLike(fromBuilder, "sp.custom_name", keyword, params, false);
		appendIfNotEmpty(fromBuilder, "gc.id", categoryId, params, false);
		if (StrKit.notBlank(tag)) {
			fromBuilder.append(" AND FIND_IN_SET(?, sp.tags)");
			params.add(tag);
		}

		fromBuilder.append(" ORDER BY gc.`parent_id`, gc.`order_list`, gc.`id`, sp.order_list ");

		return Db.find(fromBuilder.toString(), params.toArray());
	}
	
	public List<SellerProduct> checkSellerProduct(String customName,String sellerId,String cpsName) {
		String sql = "SELECT * from (" + 
				"SELECT csp.seller_id, csp.custom_name,GROUP_CONCAT(distinct cgs.`name` order by css.id) AS cps_name " + 
				"from cc_seller_product csp  " + 
				"LEFT JOIN cc_product cp on cp.id = csp.product_id " + 
				"LEFT JOIN cc_product_goods_specification_value cpg ON  cp.id = cpg.product_set_id " + 
				"LEFT JOIN cc_goods_specification_value cgs ON cpg.goods_specification_value_set_id = cgs.id " + 
				"LEFT JOIN cc_goods_specification css on css.id = cgs.goods_specification_id " + 
				"GROUP BY csp.id) t1 where t1.custom_name = '"+customName+"' and t1.seller_id = '"+sellerId+"' and  t1.cps_name ='"+cpsName+"'";
		return DAO.find(sql);
	}

	public Set<String> findTagsBySellerId(String sellerId) {
		Set<String> tagSet = new LinkedHashSet<String>();
		String sql ="SELECT sp.tags tags FROM cc_seller_product sp WHERE sp.seller_id = ? AND sp.tags != '' GROUP BY sp.tags";
		List<Record> recordList = Db.find(sql,sellerId);
		for (Record record : recordList) {
			tagSet.add(record.getStr("tags"));
		}
		return tagSet;
	}
		
	public SellerProduct findByProductSn(String productSn, String sellerId) {
		StringBuilder fromBuilder = new StringBuilder("SELECT * FROM cc_seller_product cs LEFT JOIN cc_product cp ON cp.id = cs.product_id ");
		fromBuilder.append("WHERE product_sn = ? AND seller_id = ?");
		List<SellerProduct> list =  DAO.find(fromBuilder.toString(), productSn, sellerId);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
}
