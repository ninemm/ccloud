package org.ccloud.model.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.model.CustomerVisit;
import org.ccloud.model.SalesOrderDetail;
import org.ccloud.model.YxBasicchannelinfo;
import org.ccloud.model.query.ProductQuery;
import org.ccloud.model.query.QyBasicshowtypeQuery;
import org.ccloud.model.query.YxBasicchannelinfoQuery;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class ActivityMaps {

	// 公关赞助
	public static Map<String, Object> INVES_PUBLICK_MAP(Record YxActivity, String SYN_ID,
			Map<String, List<String>> ScenePhoto, String orderId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("OrderID", orderId);
		map.put("IDNO", SYN_ID);
		map.put("ScenePhoto", photo.get("ScenePhoto"));
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		map.put("ActivityTime", YxActivity.getStr("ActivityTime"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ActivityAddress", YxActivity.getStr("ActivityAddress"));
		map.put("Telephone", YxActivity.getStr("Telephone"));
		map.put("Position", YxActivity.getStr("Position"));
//		map.put("Position", YxActivity.getStr("ActivityAddress"));
		map.put("ResourceFlag", 1);
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("SignPhoto", photo.get("SignPhoto"));
		map.put("Telephone", YxActivity.getStr("Telephone"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyManName", YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		map.put("GiftPhoto", photo.get("GiftPhoto"));
		return map;
	}

	// 消费培育
	public static Map<String, Object> INVEST_CONSUMPTION_CULTIVATION_MAP(Record YxActivity,
			String SYN_ID, Map<String, List<String>> ScenePhoto, String orderId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("IDNO", SYN_ID);
		map.put("ScenePhoto", photo.get("ScenePhoto"));
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		map.put("ActivityTime", YxActivity.getStr("ActivityTime"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ActivityAddress", YxActivity.getStr("ActivityAddress"));
		map.put("Telephone", YxActivity.getStr("Telephone"));
		map.put("Position", YxActivity.getStr("Position"));
//		map.put("Position", YxActivity.getStr("ActivityAddress"));
		map.put("ResourceFlag", 1);
		map.put("InvestState", 2);
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("OrderID", orderId);
		map.put("SignPhoto", photo.get("SignPhoto"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyManName", YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		map.put("GiftPhoto", photo.get("GiftPhoto"));
		return map;
	}

	// 终端广告
	public static Map<String, Object> INVEST_TERMINSL_ADVERTISWMENT_MAP(Record YxActivity,
			String SYN_ID, Map<String, List<String>> ScenePhoto, Long shopId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("IDNO", SYN_ID);
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("ShopID", shopId);
		map.put("ExecutePhotoIds", photo.get("AllPhoto"));
		map.put("ProvinceName", YxActivity.getStr("ProvinceName"));
		map.put("CityName", YxActivity.getStr("CityName"));
		map.put("CountyName", YxActivity.getStr("CountyName"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime", YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan", YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone", YxActivity.getStr("ShopPhone"));
		 map.put("ChannelID",1);
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		map.put("Num", Integer.parseInt(YxActivity.getStr("Num")));
		map.put("InvestAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState", 2);
		map.put("ResourceFlag", 1);
		map.put("ExecuteManName", YxActivity.getStr("ExecuteManName"));
		map.put("ExecuteTime", YxActivity.getStr("ExecuteTime"));
		map.put("ExecuteState", 1);
		map.put("ExecuteNum", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("ExecuteSize", YxActivity.getBigDecimal("ExecuteSize"));
		map.put("WriteOffNum", Integer.parseInt(YxActivity.getStr("Num")));
		map.put("WriteOffSize", YxActivity.getBigDecimal("WriteOffSize"));
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		return map;
	}

	// 终端陈列
	public static Map<String, Object> INVEST_TERMINSL_DISPLAY_MAP(Record YxActivity,
			String SYN_ID, Map<String, List<String>> ScenePhoto, Long shopId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("IDNO", SYN_ID);
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("ShopID", shopId);
		map.put("GiftPhotoId", photo.get("GiftPhoto"));
		map.put("ProvinceName", YxActivity.getStr("ProvinceName"));
		map.put("CityName", YxActivity.getStr("CityName"));
		map.put("CountyName", YxActivity.getStr("CountyName"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		 map.put("ChannelID", 1);
		map.put("ShopVisitCount", Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ShopXCJHCount", Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ResourceFlag", 1);
		map.put("SignPhotoId", photo.get("SignPhoto"));
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		String shopTypeID = QyBasicshowtypeQuery.me().findIdByDict(YxActivity.getStr("ShowType"));
		map.put("ShowTypeID", shopTypeID);
		map.put("BeginTime", YxActivity.getStr("BeginTime"));
		map.put("EndTime", YxActivity.getStr("EndTime"));
		map.put("InvestDay", Integer.parseInt(YxActivity.getStr("InvestDay")));
		map.put("InvestType", 1);
		// map.put("Remark","");
		map.put("GrantAmount", YxActivity.getBigDecimal("GrantAmount"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		map.put("InvestState", 2);
		map.put("ShopCreateTime", YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan", YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone", YxActivity.getStr("ShopPhone"));
		map.put("GrantTime", YxActivity.getStr("CreateTime"));
		map.put("AuditResult", 1);
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		return map;
	}
	
	//终端陈列明细
	public static Map<String, Object> INVEST_TERMINSL_ADVERTISWMENT_DETAIL_MAP(CustomerVisit customerVisit,
			String SYN_ID, String scenePhoto, Long shopId, String orderID) {
		Map<String, Object> map = new HashMap<>();
		map.put("IDNO", StrKit.getRandomUUID());
		map.put("ShopShowID", SYN_ID);
		map.put("PhotoId", shopId.toString());
		map.put("Remark", customerVisit.getQuestionDesc());
		map.put("OrderID", orderID);
		map.put("CreateManName", customerVisit.getStr("realname"));
		map.put("CreateTime", customerVisit.getCreateDate());
		map.put("Flag", 1);
		map.put("ModifyManName", customerVisit.getStr("realname"));
		map.put("ModifyTime", customerVisit.getModifyDate());
		map.put("CancleFlag", 0);
		map.put("ResourceFlag", 1);
		String image = scenePhoto.substring(1, scenePhoto.length() - 1);
		map.put("SignPhoto", image);
		return map;
	}	

	// 终端客情
	public static Map<String, Object> INVEST_CUSTOMER_VISITE_MAP(Record YxActivity,
			String SYN_ID, Map<String, List<String>> ScenePhoto, Long shopId, String orderId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("OrderID", orderId);
		map.put("IDNO", SYN_ID);
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("ShopID", shopId);
		map.put("ActivityPhotos", photo.get("ScenePhoto"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ChannelID", Integer.parseInt(YxActivity.getStr("ChannelID")));
		map.put("InvestState", 2);
		map.put("ResourceFlag", 0);
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("SignPhotos", photo.get("SignPhoto"));
		map.put("ModifyManName", YxActivity.getStr("CreateManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		YxBasicchannelinfo basicchannelinfo = YxBasicchannelinfoQuery.me().findById(YxActivity.getStr("ChannelID"));
		map.put("ChannelTypeID", basicchannelinfo.getChannelTypeID());
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("ShopCreateTime", YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan", YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone", YxActivity.getStr("ShopPhone"));
		map.put("ShopVisitCount", Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ShopXCJHCount", Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("OrderMan",YxActivity.getStr("CreateManName"));
		map.put("ProvinceName", YxActivity.getStr("ProvinceName"));
		map.put("CityName", YxActivity.getStr("CityName"));
		map.put("CountyName", YxActivity.getStr("CountyName"));
		return map;
	}

	// 商超赠品
	public static Map<String, Object> INVEST_SUPERMARKET_GIFT_MAP(Record YxActivity,
			String SYN_ID, Long shopId, String orderID) {
		Map<String, Object> map = new HashMap<>();
		map.put("OrderID", orderID);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("IDNO", SYN_ID);
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("ShopID", shopId);
		map.put("ProvinceName", YxActivity.getStr("ProvinceName"));
		map.put("CityName", YxActivity.getStr("CityName"));
		map.put("CountyName", YxActivity.getStr("CountyName"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime", YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan", YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone", YxActivity.getStr("ShopPhone"));
		 map.put("ChannelID",1);
		map.put("InvestAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState", 2);
		map.put("ResourceFlag", 1);
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyManName", YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		return map;
	}
	
	// 商超赠品执行详情
	public static Map<String, Object> INVEST_SUPERMARKET_GIFT_EXECUTE_MAP(Record YxActivity, String SYN_ID, String orderID) {
		Map<String, Object> map = new HashMap<>();
		map.put("IDNO", StrKit.getRandomUUID());
		map.put("MarketGiftID", SYN_ID);
		map.put("OrderID", orderID);
		map.put("ResourceFlag", 1);
		map.put("ExecuteState", 1);
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyManName", YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		return map;
	}	

	// 进场费
	public static Map<String, Object> INVEST_SLOTTING_FEE_MAP(Record YxActivity,
			String SYN_ID, Map<String, List<String>> ScenePhoto, Long shopId, String orderId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("IDNO", SYN_ID);
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("ShopID", shopId);
		map.put("ExecutePhotoIds", photo.get("AllPhoto"));
		map.put("ProvinceName", YxActivity.getStr("ProvinceName"));
		map.put("CityName", YxActivity.getStr("CityName"));
		map.put("CountyName", YxActivity.getStr("CountyName"));
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime", YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan", YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone", YxActivity.getStr("ShopPhone"));
		 map.put("ChannelID","1");
		 if (StrKit.isBlank(orderId)) {
			 map.put("InvestType", 1);
		 } else {
			 map.put("InvestType", 2);
		 }
		map.put("InvestAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState", 2);
		map.put("ResourceFlag", 1);
		if (StrKit.notBlank(YxActivity.getStr("ShowType"))) {
			String productCode = ProductQuery.me().findCodeBySellerProductId(YxActivity.getStr("ShowType"));
			map.put("CommodityCode", productCode);
		}
		map.put("FeeTypeID", YxActivity.getStr("CostType"));
		map.put("WriteOffAmount", YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("ModifyManName", YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("Flag", 1);
		map.put("ExecuteManName", YxActivity.getStr("ExecuteManName"));
		map.put("ExecuteTime", YxActivity.getStr("ExecuteTime"));
		map.put("ExecuteState", 1);
		return map;
	}
	
	// 进场费赠品信息
	public static Map<String, Object> INVEST_SLOTTING_FEE_GIFT_MAP(Record YxActivity,
			Map<String, List<String>> ScenePhoto, Long shopId, String orderId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, String> photo = getPhotoResult(ScenePhoto);
		map.put("IDNO", StrKit.getRandomUUID());
		map.put("FlowID", YxActivity.getStr("FlowID"));
		map.put("CustomerCode", shopId.toString());
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("CustomerAddress", YxActivity.getStr("ActivityAddress"));
		map.put("OrderID", orderId);
		map.put("CreateTime", YxActivity.getStr("CreateTime"));
		map.put("CreateManName", YxActivity.getStr("CreateManName"));
		map.put("ModifyTime", YxActivity.getStr("ModifyTime"));
		map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
		map.put("Flag", 1);
		map.put("SignPhotoId", photo.get("SignPhoto"));
		return map;
	}	

	// 订单
	public static Map<String, Object> orderMap(String orderId, Record YxActivity,
			String shopId, String createDate) {
		Map<String, Object> map = new HashMap<>();
		map.put("OrderID", orderId);
		map.put("FlowNo", YxActivity.getStr("FlowIDNO"));
		map.put("CustomerCode", shopId);
		map.put("CustomerName", YxActivity.getStr("CustomerName"));
		map.put("OrderTime", createDate);		
		return map;
	} 	

	// 订单明细
	public static Map<String, Object> orderDetailMap(String orderId, SalesOrderDetail salesOrderDetail) {
		Map<String, Object> map = new HashMap<>();
		map.put("OrderDetailID", salesOrderDetail.getId());
		map.put("OrderID", orderId);
		map.put("CommodityCode", salesOrderDetail.get("product_sn"));
		map.put("Quantity", salesOrderDetail.get("bigCount"));
		map.put("Small_Quantity", salesOrderDetail.get("smallCount"));
		map.put("IsGift", salesOrderDetail.getIsGift());
		return map;
	}

	// 客户信息
	public static Map<String, Object> customerMap(String idno, Record Customer) {
		Map<String, Object> map = new HashMap<>();
		map.put("ShopID", idno);
		map.put("CustomerName", Customer.getStr("CustomerName"));
		map.put("CustomerCode", idno);
		map.put("LinkMan", Customer.getStr("LinkMan"));
		map.put("LinkMobile", Customer.getStr("LinkMobile"));
		map.put("ResponsableMan", Customer.getStr("ResponsableMan"));
		map.put("CustomerAddress", Customer.getStr("CustomerAddress"));
		// map.put("ChannelName", Customer.getStr("ChannelName"));
		map.put("ProvinceName", Customer.getStr("ProvinceName"));
		map.put("CityName", Customer.getStr("CityName"));
		map.put("CountyName", Customer.getStr("CountyName"));
		map.put("PersonName", Customer.getStr("PersonName"));
		// map.put("ChannelID", Customer.getInt("PersonName"));
		// map.put("ProvinceID",);
		// map.put("CityID",);
		// map.put("CountyID",);
		// map.put("PersonID", Customer.getInt("PersonID"));
		map.put("CreateTime", Customer.getStr("CreateTime"));
		map.put("ModifyTime", Customer.getStr("ModifyTime"));
		map.put("Flag", 1);
		return map;
	}

	// 照片信息
	public static Map<String, Object> photoMap(String savePath, String domain, String createDate) {
		Map<String, Object> map = new HashMap<>();
		map.put("PhotoID", savePath);
		map.put("PhotoSource", "七牛云");
		// map.put("PhotoSize", "");
		map.put("PhotoUrl", domain + "/" + savePath);
		// map.put("Remark", "");
		map.put("PhotoTime", createDate);
		map.put("ApplyTime", new Date());
		map.put("ModifyTime", new Date());
		map.put("PhotoType", 1);
		return map;
	}
	
	private static Map<String, String> getPhotoResult(Map<String, List<String>> map) {
		Map<String, String> result = new HashMap<>();
		List<String> ScenePhotoList = map.get("ScenePhoto");
		List<String> SignPhotoList = map.get("SignPhoto");
		List<String> GiftPhotoList = map.get("GiftPhoto");
		List<String> ExecutePhotoList = map.get("ExecutePhoto");
		List<String> AllPhotoList = map.get("AllPhoto");
		if (ScenePhotoList.size() > 0) {
			String scenePhoto = ScenePhotoList.toString();
			result.put("ScenePhoto", scenePhoto.substring(1, scenePhoto.length() - 1));
		}
		if (SignPhotoList.size() > 0) {
			String signPhoto = SignPhotoList.toString();
			result.put("SignPhoto", signPhoto.substring(1, signPhoto.length() - 1));
		}
		if (GiftPhotoList.size() > 0) {
			String giftPhoto = GiftPhotoList.toString();
			result.put("GiftPhoto", giftPhoto.substring(1, giftPhoto.length() - 1));
		}
		if (ExecutePhotoList.size() > 0) {
			String executePhoto = ExecutePhotoList.toString();
			result.put("ExecutePhoto", executePhoto.substring(1, executePhoto.length() - 1));
		}
		if (AllPhotoList.size() > 0) {
			String allPhoto = AllPhotoList.toString();
			result.put("AllPhoto", allPhoto.substring(1, allPhoto.length() - 1));
		}		
		return result;
	}
	
}
