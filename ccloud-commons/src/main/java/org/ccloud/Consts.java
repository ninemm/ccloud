/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud;

public class Consts {

	public static final String COOKIE_LOGINED_USER = "user";

	public static final String SESSION_LOGINED_USER = "SESSION_LOGINED_USER";
	public static final String SESSION_SELECT_DATAAREA = "SESSION_SELECT_DATAAREA";//数据查看时的数据域
	
	public static final String WORKFLOW_APPLY_USERNAME = "applyUsername";

	public static final String CHARTSET_UTF8 = "UTF-8";
	
	public static final String INDEX_URL = "/";

	public static final String ROUTER_CONTENT = "/c";
	public static final String ROUTER_TAXONOMY = "/t";
	public static final String ROUTER_USER = "/user";
	public static final String ROUTER_TRIP_ROUTE = "/tr";
	public static final String ROUTER_USER_CENTER = ROUTER_USER + "/center";
	public static final String ROUTER_USER_LOGIN = ROUTER_USER + "/login";

	public static final int ERROR_CODE_NOT_VALIDATE_CAPTHCHE = 1;
	public static final int ERROR_CODE_USERNAME_EMPTY = 2;
	public static final int ERROR_CODE_USERNAME_EXIST = 3;
	public static final int ERROR_CODE_EMAIL_EMPTY = 4;
	public static final int ERROR_CODE_EMAIL_EXIST = 5;
	public static final int ERROR_CODE_PHONE_EMPTY = 6;
	public static final int ERROR_CODE_PHONE_EXIST = 7;
	public static final int ERROR_CODE_PASSWORD_EMPTY = 8;

	public static final String ATTR_PAGE_NUMBER = "_page_number";
	public static final String ATTR_USER = "USER";
	public static final String ATTR_GLOBAL_WEB_NAME = "WEB_NAME";
	public static final String ATTR_GLOBAL_WEB_TITLE = "WEB_TITLE";
	public static final String ATTR_GLOBAL_WEB_SUBTITLE = "WEB_SUBTITLE";
	public static final String ATTR_GLOBAL_META_KEYWORDS = "META_KEYWORDS";
	public static final String ATTR_GLOBAL_META_DESCRIPTION = "META_DESCRIPTION";
	public static final String ATTR_GLOBAL_SYSTEM_LOG = "SYSTEM_LOG";

	public static final String SESSION_WECHAT_USER = "_wechat_user";
	public static final String SESSION_WECHAT_ACCESS_TOKEN = "_wechat_access_token";
	public static final String SESSION_WECHAT_OPEN_ID = "_wechat_open_id";
	public static final String ATTR_USER_OBJECT = "_user_object";
	public static final String SESSION_WECHAT_JSAPI_TICKET = "_jsapi_ticket";

	public static final String MODULE_ARTICLE = "article"; // 文章模型
	public static final String MODULE_PAGE = "page"; // 页面模型
	public static final String MODULE_FOURM = "forum"; // 论坛模型
	public static final String MODULE_MENU = "menu"; // 菜单
	public static final String MODULE_QA = "qa"; // QA问答
	public static final String MODULE_GOODS = "goods"; // 商品
	public static final String MODULE_GOODS_SHOPPING_CART = "goods_shopping_cart"; // 购物车
	public static final String MODULE_GOODS_ORDER = "goods_order"; // 订单
	public static final String MODULE_WECHAT_MENU = "wechat_menu"; // 微信菜单
	public static final String MODULE_WECHAT_REPLY = "wechat_reply"; // 微信自动回复
	public static final String MODULE_USER_COLLECTION = "user_collection"; // 用户搜藏
	public static final String MODULE_USER_RELATIONSHIP = "user_relationship"; // 用户关系（比如：好友，关注等）
	public static final String MODULE_API_APPLICATION = "api_application"; // API应用，可以对应用进行管理
	
	public static final String TAXONOMY_TEMPLATE_PREFIX = "for$";
	
	public static final String QRCODE_PATH = "/qrcode/";// 二维码生成路径
	
	public static final String DICT_UNIT_CODE = "unit";//单位类型字典编码
	
	public static final String DEPT_HQ_ID = "0";//珈研部门ID
	public static final String DEPT_HQ_DATAAREA = "001";//珈研部门数据域
	public static final String DEPT_HQ_DATAAREA_LIKE = "001%";//珈研部门数据域子部门查询条件
	public static final String DEPT_HQ_LEVEL = "0";//珈研部门等级
	
	public static final String SALES_ORDER_SN = "100001";//订单流水每日起始数
	public static final String SALES_OUT_STOCK_SN = "100001";//出库单单流水每日起始数
	
	public static final int SALES_ORDER_AUDIT_STATUS_PASS = 1000;//销售订单审核通过
	
	
	public static final int SALES_ORDER_STATUS_PASS = 1000;//订单已审核
	public static final int SALES_ORDER_STATUS_DEFAULT = 0;//订单待审核
	public static final int SALES_ORDER_STATUS_CANCEL= 1001;//订单取消
	public static final int SALES_ORDER_STATUS_REJECT = 1002;// 订单拒绝
	public static final int SALES_ORDER_STATUS_PART_OUT= 2000;//订单部分出库
	public static final int SALES_ORDER_STATUS_PART_OUT_CLOSE= 2001;//订单部分出库-订单关闭
	public static final int SALES_ORDER_STATUS_ALL_OUT= 3000;//订单全部出库
	public static final int SALES_ORDER_STATUS_ALL_OUT_CLOSE= 30001;//订单全部出库-订单关闭
	
	public static final int SALES_OUT_STOCK_STATUS_OUT = 1000;//出库单出库
	public static final int SALES_OUT_STOCK_STATUS_DEFUALT = 0;//出库单待出库
	
	public static final int SALES_REFUND_INSTOCK_DEFUALT = 0;//退货单待审核
	public static final int SALES_REFUND_INSTOCK_PASS = 1000;//退货单已审核
	public static final int SALES_REFUND_INSTOCK_CANCEL= 1001;//退货单取消
	public static final int SALES_REFUND_INSTOCK_PART_OUT= 2000;//退货单部分入库
	public static final int SALES_REFUND_INSTOCK_ALL_OUT= 3000;//退货单全部入库
	
	public static final String BIZ_TYPE_INIT = "100201";//库存建账
	public static final String BIZ_TYPE_INSTOCK = "100202";//采购入库
	public static final String BIZ_TYPE_P_OUTSTOCK = "100203";//采购退货出库
	public static final String BIZ_TYPE_SALES_OUTSTOCK = "100204";//销售出库
	public static final String BIZ_TYPE_SALES_REFUND_INSTOCK = "100205";//销售退货入库
	public static final String BIZ_TYPE_TRANSFER_INSTOCK = "100206";//调拨单
	public static final String BIZ_TYPE_TRANSFER_OUTSTOCK = "100207";//调拨出库
	public static final String BIZ_TYPE_TRANSFER_PLUS_INSTOCK = "100208";//盘盈入库
	public static final String BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK = "100209";//盘亏出库
	public static final String BIZ_TYPE_SALES_ORDER = "100210";//商品销售
	
	public static final int INVENTORY_TYPE_IN = 0;//库存总账入库
	public static final int INVENTORY_TYPE_OUT = 1;//库存总账出库
	
	public static final String SELLER_OPTION_STORE_SWITCH = "store_switch"; //经销商设置尾缀
	
	public static final String RECEIVABLES_OBJECT_TYPE_CUSTOMER = "customer"; //应收账款客户类型
	public static final String RECEIVABLES_OBJECT_TYPE_SUPPLIER = "supplier"; //应收账款供应商类型
}
