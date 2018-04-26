/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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
package midData;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.model.QyBasicfeetype;
import org.ccloud.model.QyBasicflowtype;
import org.ccloud.model.QyBasicshowtype;
import org.ccloud.model.QyExpensedetail;
import org.ccloud.model.YxBasicchannelinfo;
import org.ccloud.model.YxBasicchanneltypeinfo;
import org.ccloud.model.vo.Expense;
import org.ccloud.model.vo.ExpensesDetail;
import org.ccloud.model.vo.Mid_ActivityInfo;
import org.ccloud.utils.HttpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;

public class MidDataUtil {
	
//	private static String userName = PropKit.use("midData.properties").get("middata_soap_username");
//	private static String passWord = PropKit.use("midData.properties").get("middata_soap_password");
    private static final String GET_JINGPAI_ACTIVITYS = PropKit.use("midData.properties").get("GET_JINGPAI_ACTIVITYS");
    private static final String GET_JINGPAI_FLOWTYPE = PropKit.use("midData.properties").get("GET_JINGPAI_FLOWTYPE");
    private static final String GET_JINGPAI_FEETYPE = PropKit.use("midData.properties").get("GET_JINGPAI_FEETYPE");
    private static final String GET_JINGPAI_SHOWTYPE = PropKit.use("midData.properties").get("GET_JINGPAI_SHOWTYPE");
    private static final String GET_JINGPAI_EXPENSES = PropKit.use("midData.properties").get("GET_JINGPAI_EXPENSES");
    private static final String GET_JINGPAI_EXPENSEDETAILS = PropKit.use("midData.properties").get("GET_JINGPAI_EXPENSEDETAILS");
    private static final String GET_JINGPAI_BASICCHANNELTYPE = PropKit.use("midData.properties").get("GET_JINGPAI_BASICCHANNELTYPE");
    private static final String GET_JINGPAI_BASICCHANNEL= PropKit.use("midData.properties").get("GET_JINGPAI_BASICCHANNEL");

	public static void main(String[] args) throws RemoteException {
//		List<Expense> list = getExpensesInfo("2018-02-01", "2018-03-01", "1", "100");
//		System.out.println(list.get(0).getExpenseName());
//		System.out.println(list.get(1).getExpenseName());
//		List<ExpensesDetail> list = getExpenseDetail("3f2881e9612ce2fc016130f1c1401414", "INVEST_CUSTOMER_VISITE");
	}

	public static List<Mid_ActivityInfo> getActivityInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<Mid_ActivityInfo> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_ACTIVITYS, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("Activitys");
			list = jsonArray.toJavaList(Mid_ActivityInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<QyBasicflowtype> getFlowTypeInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<QyBasicflowtype > list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_FLOWTYPE, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("BasicFlowTypes");
			list = jsonArray.toJavaList(QyBasicflowtype.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<QyBasicfeetype> getFeeTypeInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<QyBasicfeetype> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_FEETYPE, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("BasicFeeTypes");
			list = jsonArray.toJavaList(QyBasicfeetype.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<QyBasicshowtype> getShowTypeInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<QyBasicshowtype> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_SHOWTYPE, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("BasicShowTypes");
			list = jsonArray.toJavaList(QyBasicshowtype.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<Expense> getExpensesInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<Expense> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_EXPENSES, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("Expenses");
			list = jsonArray.toJavaList(Expense.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<QyExpensedetail> getExpenseDetailsInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<QyExpensedetail> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_EXPENSEDETAILS, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("Expenses");
			list = jsonArray.toJavaList(QyExpensedetail.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<YxBasicchannelinfo> getBasicChannel() {
        List<YxBasicchannelinfo> list = new ArrayList<>();
		try {
			String result = HttpUtils.get(GET_JINGPAI_BASICCHANNEL);
			JSONArray jsonArray = JSONArray.parseArray(result);
			list = jsonArray.toJavaList(YxBasicchannelinfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<YxBasicchanneltypeinfo> getBasicChannelType() {
        List<YxBasicchanneltypeinfo> list = new ArrayList<>();
		try {
			String result = HttpUtils.get(GET_JINGPAI_BASICCHANNELTYPE);
			JSONArray jsonArray = JSONArray.parseArray(result);
			list = jsonArray.toJavaList(YxBasicchanneltypeinfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<ExpensesDetail> getExpenseDetail(String expenseID, String type) {
        List<ExpensesDetail> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("strExpenseID", expenseID);
		try {
			String result = HttpUtils.get(GET_JINGPAI_EXPENSEDETAILS, map);
			System.out.println(result);
			String response = getResponseType(type);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray(response);
			list = jsonArray.toJavaList(ExpensesDetail.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private static String getResponseType(String type) {
		if (type.equals(Consts.INVES_PUBLICK)) {
			return "ExpenseDetail_BrandActivity";
		} else if (type.equals(Consts.INVEST_CONSUMPTION_CULTIVATION)) { 
			return "ExpenseDetail_ProductTasting";
		} else if (type.equals(Consts.INVEST_TERMINSL_ADVERTISWMENT)) {
			return "ExpenseDetail_Ad";
		} else if (type.equals(Consts.INVEST_TERMINSL_DISPLAY)) {
			return "ExpenseDetail_ShopShow";
		} else if (type.equals(Consts.INVEST_CUSTOMER_VISITE)) {
			return "ExpenseDetail_NewStores";
		} else if (type.equals(Consts.INVEST_SUPERMARKET_GIFT)) {
			return "ExpenseDetail_MarketGift";
		} else {
			return "ExpenseDetail_Entry";
		}
	}

}
