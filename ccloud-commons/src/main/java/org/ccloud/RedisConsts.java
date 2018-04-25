/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
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

public class RedisConsts {
	public static final String CACHE_NAME_CCLOUD = "CCLOUD";
	
	public static final String REDIS_KEY_USER = "USER:";
	public static final String REDIS_KEY_USER_LIST = "USER_LIST:";
	public static final String REDIS_KEY_DEPT = "DEPT:";
	public static final String REDIS_KEY_ALL_PARENT_DEPTS = "ALL_PARENT_DEPTS:";
	public static final String REDIS_KEY_USER_ROLE_LIST = "USER_ROLE_LIST:";
	public static final String REDIS_KEY_USER_OPERATION_URLS = "USER_OPERATION_URLS:";
	public static final String REDIS_KEY_GROUP_ROLE_RELS_RECORD = "GROUP_ROLE_RELS_RECORD:";
	public static final String REDIS_KEY_AREA_ROLES_RECORD = "AREA_ROLES_RECORD";

	public static final String REDIS_KEY_TODAY= "TODAY";
	public static final String REDIS_KEY_SALES_ORDER_SN= "SALES_ORDER_SN";
	public static final String REDIS_KEY_SELLER_SALES_ORDER_SN = "SELLER_SALES_ORDER_SN:";
	public static final String REDIS_KEY_SALES_OUT_STOCK_SN= "SALES_OUT_STOCK_SN";
	public static final String REDIS_KEY_SELLER_SALES_OUT_STOCK_SN = "SELLER_SALES_OUT_STOCK_SN:";
	public static final String REDIS_KEY_SALES_REFUND_INSTOCK_SN= "SALES_REFUND_INSTOCK_SN";
	public static final String REDIS_KEY_SELLER_SALES_REFUND_INSTOCK_SN = "SELLER_SALES_REFUND_INSTOCK_SN:";
}
