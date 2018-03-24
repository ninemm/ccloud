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
package org.ccloud.mid;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.model.QyBasicfeetype;
import org.ccloud.model.QyBasicflowtype;
import org.ccloud.model.QyBasicshowtype;
import org.ccloud.model.QyExpense;
import org.ccloud.model.QyExpensedetail;
import org.ccloud.model.YxBasicchannelinfo;
import org.ccloud.model.YxBasicchanneltypeinfo;
import org.ccloud.model.vo.Mid_ActivityInfo;
import org.ccloud.utils.HttpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;

public class MidDataUtil {
	
	private static String userName = PropKit.use("midData.properties").get("middata_soap_username");
	private static String passWord = PropKit.use("midData.properties").get("middata_soap_password");
    private static final String GET_JINGPAI_ACTIVITYS = "http://yxmiddb.jingpai.com/WebAPI/api/Activitys";
    private static final String GET_JINGPAI_FLOWTYPE = "http://yxmiddb.jingpai.com/WebAPI/api/BasicFlowTypes";
    private static final String GET_JINGPAI_FEETYPE = "http://yxmiddb.jingpai.com/WebAPI/api/BasicFeeTypes";
    private static final String GET_JINGPAI_SHOWTYPE = "http://yxmiddb.jingpai.com/WebAPI/api/BasicShowTypes";
    private static final String GET_JINGPAI_EXPENSES = "http://yxmiddb.jingpai.com/WebAPI/api/Expenses";
    private static final String GET_JINGPAI_EXPENSEDETAILS = "http://yxmiddb.jingpai.com/WebAPI/api/ExpensesDetail";
    private static final String GET_JINGPAI_BASICCHANNELTYPE = "http://yxmiddb.jingpai.com/WebAPI/api/BasicChannelType";
    private static final String GET_JINGPAI_BASICCHANNEL= "http://yxmiddb.jingpai.com/WebAPI/api/BasicChannel";
    

	public static void main(String[] args) throws RemoteException {
		List<YxBasicchannelinfo> list = getBasicChannel();
		System.out.println(list.size());
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
	
	public static List<QyExpense> getExpensesInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<QyExpense> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_EXPENSES, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("Expenses");
			list = jsonArray.toJavaList(QyExpense.class);
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

}
