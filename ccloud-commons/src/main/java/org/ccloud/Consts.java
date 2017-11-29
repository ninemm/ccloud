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

	public static final String CHARTSET_UTF8 = "UTF-8";

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
}
