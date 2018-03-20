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

import org.ccloud.model.vo.Mid_ActivityInfo;
import org.ccloud.model.vo.Mid_ExpensesInfo;
import org.ccloud.model.vo.Mid_FlowTypeInfo;
import org.ccloud.utils.HttpUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;

public class MidDataUtil {
	
	private static String userName = PropKit.use("midData.properties").get("middata_soap_username");
	private static String passWord = PropKit.use("midData.properties").get("middata_soap_password");
    private static final String GET_JINGPAI_ACTIVITYS = "http://yxmiddb.jingpai.com/WebAPI/api/Activitys";

	public static void main(String[] args) throws RemoteException {
		
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
	
	public static List<Mid_FlowTypeInfo> getFlowTypeInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<Mid_FlowTypeInfo> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_ACTIVITYS, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("BasicFlowTypes");
			list = jsonArray.toJavaList(Mid_FlowTypeInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<Mid_ExpensesInfo> getExpensesInfo(String startTime, String endTime, String pageIndex, String pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("BegTime", startTime);
        map.put("EndTime", endTime);
        map.put("PageIndex", pageIndex);
        map.put("PageSize", pageSize);
        List<Mid_ExpensesInfo> list = new ArrayList<>();
		try {
			String result = HttpUtils.post(GET_JINGPAI_ACTIVITYS, map);
			JSONArray jsonArray = JSONObject.parseObject(result).getJSONArray("Expenses");
			list = jsonArray.toJavaList(Mid_ExpensesInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
