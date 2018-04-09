package org.ccloud.model.activity;

import java.util.HashMap;
import java.util.Map;

import org.ccloud.Consts;

import com.jfinal.plugin.activerecord.Record;

public class ActivityMaps {
	
	public static Map<String, Object> chooseMap(Record YxActivity) {
		if (YxActivity.getStr("invest_type").equals(Consts.INVES_PUBLICK)) {
			return INVES_PUBLICK_MAP(YxActivity);
		}else if(YxActivity.getStr("invest_type").equals(Consts.INVEST_CONSUMPTION_CULTIVATION)) {
			return INVEST_CONSUMPTION_CULTIVATION_MAP(YxActivity);
		}else if(YxActivity.getStr("invest_type").equals(Consts.INVEST_TERMINSL_ADVERTISWMENT)) {
			return INVEST_TERMINSL_ADVERTISWMENT_MAP(YxActivity);
		}else if(YxActivity.getStr("invest_type").equals(Consts.INVEST_TERMINSL_DISPLAY)) {
			return INVEST_TERMINSL_DISPLAY_MAP(YxActivity);
		}else if(YxActivity.getStr("invest_type").equals(Consts.INVEST_CUSTOMER_VISITE)) {
			return INVEST_CUSTOMER_VISITE_MAP(YxActivity);
		}else if(YxActivity.getStr("invest_type").equals(Consts.INVEST_SUPERMARKET_GIFT)) {
			return INVEST_SUPERMARKET_GIFT_MAP(YxActivity);
		}else {
			return INVEST_SLOTTING_FEE_MAP(YxActivity);
		}		
	}
	
	//公关赞助
	public static Map<String, Object> INVES_PUBLICK_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
		map.put("ActivityTime",YxActivity.getStr("ActivityTime"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ActivityAddress",YxActivity.getStr("ActivityAddress"));
		map.put("Telephone",YxActivity.getStr("Telephone"));
		map.put("Position",YxActivity.getStr("Position"));
		map.put("ResourceFlag",1);
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
//		map.put("SignPhoto","");		
		map.put("Telephone",YxActivity.getStr("Telephone"));
		map.put("CreateManName",YxActivity.getStr("CreateManName"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
		map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));
		map.put("Flag",1);
//		map.put("ShopOrderID",);
//		map.put("GiftPhoto","");		
		return map;
	}
	
	//消费培育
	public static Map<String, Object> INVEST_CONSUMPTION_CULTIVATION_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
		map.put("ActivityTime",YxActivity.getStr("ActivityTime"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ActivityAddress",YxActivity.getStr("ActivityAddress"));
//		map.put("TableNum",);
//		map.put("DinnerType",);
		map.put("Telephone",YxActivity.getStr("Telephone"));
		map.put("Position",YxActivity.getStr("Position"));
		map.put("ResourceFlag",1);
		map.put("InvestState",2);
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
//		map.put("ShopOrderID",);
//		map.put("IntroducerOrderID",);
//		map.put("IntroducerName","");
//		map.put("IntroducerTel","");
//		map.put("SignPhotos","");
		map.put("CreateManName",YxActivity.getStr("CreateManName"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
		map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));		
		map.put("Flag",1);
//		map.put("GiftPhoto","");		
		return map;
	}
	
	//终端广告
	public static Map<String, Object> INVEST_TERMINSL_ADVERTISWMENT_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("ProvinceName",YxActivity.getStr("ProvinceName"));
		map.put("CityName",YxActivity.getStr("CityName"));
		map.put("CountyName",YxActivity.getStr("CountyName"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime",YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan",YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone",YxActivity.getStr("ShopPhone"));
//		map.put("ChannelID",);
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
		map.put("Num",Integer.parseInt(YxActivity.getStr("Num")));
		map.put("InvestAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState",2);
		map.put("ResourceFlag",1);
		map.put("ExecuteManName",YxActivity.getStr("ExecuteManName"));
		map.put("ExecuteTime",YxActivity.getStr("ExecuteTime"));		
		map.put("ExecuteState",1);
//		map.put("ExecuteNum",);
//		map.put("ExecuteSize","");
//		map.put("ExecuteRemark","");
		map.put("WriteOffNum",Integer.parseInt(YxActivity.getStr("Num")));
//		map.put("WriteOffSize","");
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName",YxActivity.getStr("CreateManName"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));		
		return map;
	}
	
	//终端陈列 
	public static Map<String, Object> INVEST_TERMINSL_DISPLAY_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("ProvinceName",YxActivity.getStr("ProvinceName"));
		map.put("CityName",YxActivity.getStr("CityName"));
		map.put("CountyName",YxActivity.getStr("CountyName"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
//		map.put("ChannelID",);
		map.put("ShopVisitCount",Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ShopXCJHCount",Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ResourceFlag",1);
//		map.put("SignPhotoId","");
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
		map.put("ShowType",Integer.parseInt(YxActivity.getStr("ShowType")));
		map.put("BeginTime",YxActivity.getStr("BeginTime"));
		map.put("EndTime",YxActivity.getStr("EndTime"));
		map.put("InvestDay",Integer.parseInt(YxActivity.getStr("InvestDay")));
		map.put("InvestType",1);
//		map.put("Remark","");
		map.put("GrantAmount",YxActivity.getBigDecimal("GrantAmount"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));		
		map.put("Flag",1);
		map.put("InvestState",2);
		map.put("ShopCreateTime",YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan",YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone",YxActivity.getStr("ShopPhone"));
		map.put("GrantTime",YxActivity.getStr("CreateTime"));
		map.put("AuditResult",1);
		map.put("CreateManName",YxActivity.getStr("CreateManName"));		
		return map;
	}
	
	//终端客情
	public static Map<String, Object> INVEST_CUSTOMER_VISITE_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ChannelID",Integer.parseInt(YxActivity.getStr("ChannelID")));
		map.put("InvestState",2);
//		map.put("CancleReason","");
		map.put("ResourceFlag",1);
//		map.put("CreateManID",);
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
//		map.put("ShopOrderID",111);
//		map.put("SignPhotos","");
//		map.put("ModifyManID",);
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));
		map.put("Flag",1);
		map.put("ChannelTypeID",1);
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("ShopCreateTime",YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan",YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone",YxActivity.getStr("ShopPhone"));
		map.put("ShopVisitCount",Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
		map.put("ShopXCJHCount",Integer.parseInt(YxActivity.getStr("ShopVisitCount")));
//		map.put("OrderMan",);
		map.put("ProvinceName",YxActivity.getStr("ProvinceName"));
		map.put("CityName",YxActivity.getStr("CityName"));
		map.put("CountyName",YxActivity.getStr("CountyName"));		
		return map;
	}
	
	//商超赠品
	public static Map<String, Object> INVEST_SUPERMARKET_GIFT_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("ProvinceName",YxActivity.getStr("ProvinceName"));
		map.put("CityName",YxActivity.getStr("CityName"));
		map.put("CountyName",YxActivity.getStr("CountyName"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime",YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan",YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone",YxActivity.getStr("ShopPhone"));
//		map.put("ChannelID",);
		map.put("InvestAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState",2);
		map.put("ResourceFlag",1);
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName",YxActivity.getStr("CreateManName"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
		map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));		
		map.put("Flag",1);		
		return map;
	}
	
	//进场费
	public static Map<String, Object> INVEST_SLOTTING_FEE_MAP(Record YxActivity) {
		Map<String, Object> map = new HashMap<>();
		map.put("ProvinceName",YxActivity.getStr("ProvinceName"));
		map.put("CityName",YxActivity.getStr("CityName"));
		map.put("CountyName",YxActivity.getStr("CountyName"));
		map.put("CustomerName",YxActivity.getStr("CustomerName"));
		map.put("ShopCreateTime",YxActivity.getStr("ShopCreateTime"));
		map.put("ShopLinkMan",YxActivity.getStr("ShopLinkMan"));
		map.put("ShopPhone",YxActivity.getStr("ShopPhone"));
//		map.put("ChannelID",);
//		map.put("InvestType",);
		map.put("InvestAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("InvestState",2);
		map.put("ResourceFlag",1);
//		map.put("CommodityID",);
		map.put("CostType",Integer.parseInt(YxActivity.getStr("CostType")));
//		map.put("ExecuteRemark","");
		map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
		map.put("CreateManName",YxActivity.getStr("CreateManName"));
		map.put("CreateTime",YxActivity.getStr("CreateTime"));
		map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
		map.put("ModifyTime",YxActivity.getStr("ModifyTime"));		
		map.put("Flag",1);
		map.put("ExecuteManName",YxActivity.getStr("ExecuteManName"));
		map.put("ExecuteTime",YxActivity.getStr("ExecuteTime"));		
		map.put("ExecuteState",1);		
		return map;
	}
	
}
