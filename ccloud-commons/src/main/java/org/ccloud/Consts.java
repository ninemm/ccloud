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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Consts {

	public static final String FIRST_URL="/admin";
	
	public static final String COOKIE_LOGINED_USER = "user";
	public static final String COOKIE_SELECTED_SELLER_ID = "_seller_id";
	public static final String COOKIE_SELECTED_USER_ID = "_user_id";

	public static final String SESSION_LOGINED_USER = "_logined_user";
	public static final String SESSION_SELECT_DATAAREA = "_data_area";//数据查看时的数据域
	public static final String SESSION_SELLER_ID = "sellerId";
	public static final String SESSION_SELLER_CODE = "sellerCode";
	public static final String SESSION_SELLER_NAME = "sellerName";
	public static final String SESSION_SELLER_HAS_STORE = "hasStore";
	public static final String SESSION_DEALER_DATA_AREA = "dealer_data_area";//经销商ID

	public static final String WORKFLOW_APPLY_USER = "applyUser";
	public static final String WORKFLOW_APPLY_COMFIRM = "applyComfirm";
	public static final String WORKFLOW_APPLY_USERNAME = "applyUsername";
	public static final String WORKFLOW_APPLY_COMFIRM_USERNAME = "applyComfirmUsername";
	public static final String WORKFLOW_APPLY_SELLER_ID = "sellerId";
	public static final String WORKFLOW_APPLY_SELLER_CODE = "sellerCode";
    public static final List<String> QRDEALERCODE = new ArrayList<>(Arrays.asList("02701015"));

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
	public static final String ATTR_GLOBAL_SELLER_ID = "SELLER_ID";
	public static final String ATTR_GLOBAL_SELLER_NAME = "SELLER_NAME";

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
	public static final String ORDER_QRCODE_PATH = "orderQrcode/";// 二维码生成路径

	public static final String DICT_UNIT_CODE = "unit";//单位类型字典编码

	public static final String DEPT_HQ_ID = "0";//珈研部门ID
	public static final String DEPT_HQ_DATAAREA = "001";//珈研部门数据域
	public static final String DEPT_HQ_DATAAREA_LIKE = "001%";//珈研部门数据域子部门查询条件
	public static final String DEPT_HQ_LEVEL = "0";//珈研部门等级

	public static final String SALES_ORDER_SN = "100001";//订单流水每日起始数
	public static final String SALES_OUT_STOCK_SN = "0001";//出库单单流水每日起始数
	public static final String PURCHASE_IN_STOCK_SN = "100001";//采购入库单单流水每日起始数


	public static final int SALES_ORDER_AUDIT_STATUS_PASS = 1000;//销售订单审核通过


	public static final int SALES_ORDER_STATUS_PASS = 1000;//订单已审核
	public static final int SALES_ORDER_STATUS_DEFAULT = 0;//订单待审核
	public static final int SALES_ORDER_STATUS_CANCEL = 1001;//订单取消
	public static final int SALES_ORDER_STATUS_REJECT = 1002;// 订单拒绝
	public static final int SALES_ORDER_STATUS_PART_OUT = 2000;//订单部分出库
	public static final int SALES_ORDER_STATUS_PART_OUT_CLOSE = 2001;//订单部分出库-订单关闭
	public static final int SALES_ORDER_STATUS_ALL_OUT = 3000;//订单全部出库
	public static final int SALES_ORDER_STATUS_ALL_OUT_CLOSE = 30001;//订单全部出库-订单关闭

	public static final int SALES_ORDER_RECEIVE_TYPE_ACCOUNT = 0;//账期
	public static final int SALES_ORDER_RECEIVE_TYPE_CASH = 1;//现金

	public static final String SALES_ORDER_ACTIVITY_APPLY_ID_OTHER = "1001";//其他活动

	public static final int SALES_OUT_STOCK_STATUS_OUT = 1000;//出库单出库
	public static final int SALES_OUT_STOCK_STATUS_PART_OUT = 2000;//出库单部分出库
	public static final int SALES_OUT_STOCK_STATUS_DEFUALT = 0;//出库单待出库

	public static final int SALES_REFUND_INSTOCK_DEFUALT = 0;//退货单待审核
	public static final int SALES_REFUND_INSTOCK_PASS = 1000;//退货单已审核
	public static final int SALES_REFUND_INSTOCK_CANCEL = 1001;//退货单撤销
	public static final int SALES_REFUND_INSTOCK_REFUSE = 1002;//退货单拒绝
	public static final int SALES_REFUND_INSTOCK_PART_OUT = 2000;//退货单部分入库
	public static final int SALES_REFUND_INSTOCK_ALL_OUT = 3000;//退货单全部入库

	public static final String BIZ_TYPE_INIT = "100201";//库存建账
	public static final String BIZ_TYPE_INSTOCK = "100202";//采购入库
	public static final String BIZ_TYPE_P_OUTSTOCK = "100203";//采购退货出库
	public static final String BIZ_TYPE_SALES_OUTSTOCK = "100204";//销售出库
	public static final String BIZ_TYPE_SALES_REFUND_INSTOCK = "100205";//销售退货入库
	public static final String BIZ_TYPE_TRANSFER_INSTOCK = "100206";//调拨入库
	public static final String BIZ_TYPE_TRANSFER_OUTSTOCK = "100207";//调拨出库
	public static final String BIZ_TYPE_TRANSFER_PLUS_INSTOCK = "100208";//盘盈入库
	public static final String BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK = "100209";//盘亏出库
	public static final String BIZ_TYPE_SALES_ORDER = "100210";//商品销售

	public static final int INVENTORY_TYPE_IN = 0;//库存总账入库
	public static final int INVENTORY_TYPE_OUT = 1;//库存总账出库

	public static final String SELLER_OPTION_STORE_SWITCH = "store_switch"; //经销商设置尾缀

	public static final String RECEIVABLES_OBJECT_TYPE_CUSTOMER = "customer"; //应收账款客户类型
	public static final String RECEIVABLES_OBJECT_TYPE_SUPPLIER = "supplier"; //应收账款供应商类型

	public static final String OPTION_SELLER_STORE_CHECK = "seller_store_check_";//经销商检查库存开关设置
	public static final String OPTION_WEB_ORDER_PRICE_EDIT = "web_order_price_edit_";//购物车修改价格开关设置
	public static final String OPTION_WEB_ORDER_COMFIRM_PRICE_EDIT = "web_order_confirm_price_edit_";//下单修改价格开关设置
	public static final String OPTION_WEB_ORDER_MIX_GIFT = "web_order_mix_gift_";//下单混合赠品开关设置
	public static final String OPTION_WEB_PROCEDURE_REVIEW = "web_procedure_review_";//订单审核流程开关设置
	public static final String OPTION_WEB_PROCEDURE_REVIEW_EDIT = "web_procedure_review_edit_";//订单审核修改开关设置
	public static final String OPTION_WEB_PROC_CUSTOMER_REVIEW = "web_proc_customer_review_";//客户流程审核开关设置
	public static final String OPTION_WEB_PROC_NUM_LIMIT = "web_proc_num_limit_";//商品数量审核开关设置
	public static final String OPTION_WEB_PROC_PRICE_LIMIT = "web_proc_price_limit_";//商品价格审核开关设置
	public static final String OPTION_WEB_PROC_CUSTOMER_VISIT = "web_proc_customer_visit_";//新增拜访审核开关设置
	public static final String OPTION_WEB_PROC_ACTIVITY_APPLY = "web_proc_activity_apply_";//申请活动审核开关设置
	public static final String OPTION_DATA_UPLOAD_CUSTOMER_TYPE = "data_upload_customer_type_";//数据上传类型配置
	public static final String OPTION_WEB_PROC_PLANS_LIMIT = "web_proc_plans_limit_";//计划起止日期配置
	public static final String OPTION_WEB_MEMBER_NUMBER_LIMIT = "web_member_number_limit_";//数据上传类型配置
	public static final String OPTION_FILE_ROOT_PATH = "web_file_root_path";
	public static final String OPTION_WEB_PROC_REFUND = "web_proc_refund_"; //退货审核开关设置

	public static final String OPTION_ON = "true";
	public static final String OPTION_OFF = "false";

	public static final String CUSTOMER_SUB_TYPE_A = "100301";//A类
	public static final String CUSTOMER_SUB_TYPE_B = "100302";//B类
	public static final String CUSTOMER_SUB_TYPE_C = "100303";//c类


	public static final String CUSTOMER_KIND_COMMON = "100401";//普通客户
	public static final String CUSTOMER_KIND_SELLER = "100402";//销售商客户

	public static final String CUSTOMER_TYPE_CODE_SELLER = "G";//销售商类型

	public static final String GROUP_CODE_PREFIX_DATA = "data";//用户组
	public static final String GROUP_CODE_PREFIX_ROLE = "role";//权限组

	public static final String ROLE_CODE_007 = "007";//财务
	public static final String ROLE_CODE_020 = "020";//账务
	public static final String ROLE_CODE_010 = "010";//业务员
	public static final String ROLE_CODE_011 = "011";//直营总监

	public static final String OPERATE_HISTORY_TITLE_ORDER_REVIEW = "订单审核";
	public static final String OPERATE_HISTORY_TITLE_ORDER_PRINT = "订单打印";

	public static final String USER_DEFAULT_PASSWORD = "123456";// 重置初始密码

	public static final Integer SELLER_HAS_STORE_TRUE = 1;// 是否有仓库
	public static final Integer SELLER_HAS_STORE_FALSE = 0;


	public static final String SELLER_TYPE_DEALER = "0";//账套类型：经销商
	public static final String SELLER_TYPE_SELLER = "1";//直营商
	
	public static final Integer SELLER_PRODUCT_SOURCE_DEALER = 0;//产品来源：经销商
	public static final Integer SELLER_PRODUCT_SOURCE_OWN = 1;//自己	
	
	public static final String OBJECT_TYPE_ORDER = "order";//订单
	public static final String OBJECT_TYPE_CUSTOMER = "customer";//客户
	public static final String OBJECT_TYPE_CUSTOMER_VISIT = "visit";//拜访
	public static final String OBJECT_TYPE_ACTIVITY_APPLY = "activity";//活动
	//是否阅读 0:否 1:是
	public static final Integer IS_READ = 1;
	public static final Integer NO_READ = 0;
	
	//活动申请状态 0:待审 1 已审 2 撤回 3 拒绝 4 待核销 5 结束
	public static final Integer ACTIVITY_APPLY_STATUS_WAIT = 0;
	public static final Integer ACTIVITY_APPLY_STATUS_PASS = 1;
	public static final Integer ACTIVITY_APPLY_STATUS_CANCEL = 2;
	public static final Integer ACTIVITY_APPLY_STATUS_REJECT = 3;
	public static final Integer ACTIVITY_APPLY_STATUS_VERIFICATION = 4;
	public static final Integer ACTIVITY_APPLY_STATUS_END = 5;
	
	public static final String WAREHOUSE_TYPE_MY = "0";// 直属仓库
	public static final String WAREHOUSE_TYPE_SELLER = "1";// 销售商仓库
	public static final String WAREHOUSE_TYPE_CAR = "2";// 车销仓库
	
	//活动类型
	public static final String CATEGORY_NORMAL = "101001" ; //商品销售
	public static final String CATEGORY_INVEST = "101002" ; //投入活动
	
	//投入类型
	public static final String INVEST_TYPE = "activity_invest";//投入的类型
	public static final String INVES_PUBLICK ="101101" ; //公关赞助
	public static final String INVEST_CONSUMPTION_CULTIVATION ="101102" ; //消费培育
	public static final String INVEST_TERMINSL_ADVERTISWMENT  ="101103" ; //终端广告
	public static final String INVEST_TERMINSL_DISPLAY = "101104" ; //终端陈列
	public static final String INVEST_CUSTOMER_VISITE = "101105" ; //终端客情
	public static final String INVEST_SUPERMARKET_GIFT = "101106" ; //商超赠品
	public static final String INVEST_SLOTTING_FEE = "101107" ; //进场费
	
	//客户参与活动的时间区间
	public static final String ACTIVE_TIME_INTERVAL = "activity_time_interval";
	public static final String TIME_INTERVAL_ONE = "100801";//1个月
	public static final String TIME_INTERVAL_TWO = "100802";//2个月
	public static final String TIME_INTERVAL_THREE = "100803";//3个月
	public static final String TIME_INTERVAL_FOUR = "100804";//4个月
	public static final String TIME_INTERVAL_FIVE = "100805";//5个月
	public static final String TIME_INTERVAL_SIX = "100806";//6个月
	public static final String TIME_INTERVAL_SEVEN = "100807";//7个月
	public static final String TIME_INTERVAL_EIGHT = "100808";//8个月
	public static final String TIME_INTERVAL_NINE = "100809";//9个月
	public static final String TIME_INTERVAL_TEN = "100810";//10个月
	public static final String TIME_INTERVAL_ELEVEN = "100811";//11个月
	public static final String TIME_INTERVAL_TWELVE = "100812";//12个月
	
	
	public static final String PROC_CUSTOMER_REVIEW = "_customer_review";				// 客户审核
	public static final String PROC_CUSTOMER_VISIT_REVIEW = "_customer_visit_review";	// 拜访审核
	public static final String PROC_ACTIVITY_APPLY_REVIEW = "_activity_apply_review";	// 活动审核

	//业务员下单
	public static final String PROC_ORDER_REVIEW = "_order_review";						// 订单3审(账务→订单审核人→财务)
	public static final String PROC_ORDER_REVIEW_ONE = "_order_review_1";				// 订单1审(订单审核人)
	public static final String PROC_ORDER_REVIEW_TWO = "_order_review_2";				// 订单2审(订单审核人→财务)
	public static final String PROC_ORDER_REVIEW_THREE = "_order_review_3";				//(暂未使用(准备做驳回流程))
	public static final String PROC_ORDER_REVIEW_FOUR = "_order_review_4";				// 订单1审(直营总监)
	public static final String PROC_ORDER_REVIEW_FIVE = "_order_review_5";				// 订单2审(直营总监→财务)

	//终端下单(跟业务员下单对应,加一个业务员审核)
	public static final String PROC_MEMBER = "_member";
	public static final String PROC_MEMBER_ORDER_REVIEW = "_member_order_review";					// 终端订单4审(业务员→账务→订单审核人→财务)
	public static final String PROC_MEMBER_ORDER_REVIEW_ZERO = "_member_order_review_0";			// 终端订单1审(业务员)
	public static final String PROC_MEMBER_ORDER_REVIEW_ONE = "_member_order_review_1";				// 终端订单2审(业务员→订单审核人)
	public static final String PROC_MEMBER_ORDER_REVIEW_TWO = "_member_order_review_2";				// 终端订单3审(业务员→订单审核人→财务)
	public static final String PROC_MEMBER_ORDER_REVIEW_THREE = "_member_order_review_3";			//(暂未使用(准备做驳回流程))
	public static final String PROC_MEMBER_ORDER_REVIEW_FOUR = "_member_order_review_4";		    // 终端订单2审(业务员→直营总监)
	public static final String PROC_MEMBER_ORDER_REVIEW_FIVE = "_member_order_review_5";			// 终端订单3审(业务员→直营总监→财务)


	public static final Integer STATUS_STATE_PUT = 1;// 通用上架
	public static final Integer STATUS_STATE_DOWN = 0;// 通用下架
	
	public static final Integer STATUS_YES = 1;// 通用是
	public static final Integer STATUS_NO = 0;// 通用否

	public static final String TEMPLATE_ALI = "0";//阿里模板
	public static final String TEMPLATE_DANLU = "1";//丹露模板
	public static final String TEMPLATE_NAME_ALI = "阿里";//阿里模板
	public static final String TEMPLATE_NAME_DANLU = "丹露";//丹露模板	
	public static final String TEMPLATE_FILE_NAME_ALI = "aliTemplate.xls";//阿里模板文件名
	public static final String TEMPLATE_FILE_NAME_DANLU = "danluTemplate.xls";//丹露模板文件名
	
	public static final String PLAN = "plan_type";//销售计划
	public static final String WEEK_PLAN = "101201";//周计划
	public static final String MONTH_PLAN = "101202";//月计划
	public static final String YEAR_PLAN = "101203";//年计划
	//计划的显示方式
	public static final String PLAN_SHOW_BIZUSER = "101204"; // 按业务员方式显示
	public static final String PLAN_SHOW_SELLER_PRODUCT = "101205"; // 按产品方式显示
	
	public static final String ACTIVITY_CATEGORY_CODE = "101002";//活动类型(费用投入)
	
	public static final String WAREHOUSE_IDS_IS_NULL = "1";//当用户没有分配仓库时且不是经管时返回空值的仓库ID

	public static final String COOKIE_LOGINED_MEMBER = "member";
	public static final String SESSION_LOGINED_MEMBER = "_logined_member";
	
	public static final String FLOW_DICT_TYPE_NAME_SA = "feeType_name_SA"; //流程字典名: 进场费
	public static final String FLOW_DICT_TYPE_NAME_RAISE = "feeType_name_raise";//消费培育
	public static final String FLOW_DICT_TYPE_NAME_PR = "feeType_name_PR";//公关赞助
	public static final String FLOW_DICT_TYPE_NAME_GIFT = "feeType_name_gift";//商超赠品
	public static final String FLOW_DICT_TYPE_NAME_DISPLAY = "feeType_name_display";//终端陈列
	public static final String FLOW_DICT_TYPE_NAME_AD = "feeType_name_AD";//终端广告
	public static final String FLOW_DICT_TYPE_NAME_CHANNEL = "channel_define";//终端客情
	public static final String CUSTOMER_IDNO="customer_idno";//终端ID
	
	//客户拜访状态
	public static final String CUSTOMER_VISIT_STATUS_PASS = "100101"; //正常
	public static final String CUSTOMER_VISIT_STATUS_DEFAULT = "100102"; //待审核
	public static final String CUSTOMER_VISIT_STATUS_REJECT = "100103"; //拒绝
	
	//活动执行步骤模板类型
	public static final String ACTIVITY_EXECUTE_TEMPLATE = "activity_execute_template"; //类型
	public static final String ACTIVITY_EXECUTE_TEMPLATE_INPUT = "103016"; //输入框
	public static final String ACTIVITY_EXECUTE_TEMPLATE_RADIO = "103017"; //单选
	public static final String ACTIVITY_EXECUTE_TEMPLATE_CHECKBOX= "103018"; //多选
	
	public static final String GET_MID_DATA_TOTALROW = "100";//一次同步数据条数
	
}
