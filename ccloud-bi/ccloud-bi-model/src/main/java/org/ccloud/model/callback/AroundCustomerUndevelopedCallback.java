/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ccloud.model.callback;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.ICallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AroundCustomerUndevelopedCallback implements ICallback {

	private ResultSet resultSet = null;

	private double longitude;
	private double latitude;
	private double dist;
	private String dealerCode;

	@Override
	public Object call(Connection conn) {
		CallableStatement proc = null;

		List<Map<String, Object>> result = Lists.newArrayList();

		try {
			proc = conn.prepareCall("{ call around_customer_undeveloped(?, ?, ?, ?) }");

			proc.setDouble(1, getLongitude());
			proc.setDouble(2, getLatitude());
			proc.setDouble(3, getDist());
			proc.setString(4, getDealerCode());

			proc.execute();

			resultSet = proc.getResultSet();

			while (resultSet.next()) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("customerId", resultSet.getString("uid"));
				map.put("customerName", resultSet.getString("name"));
				map.put("type", resultSet.getString("tag"));
				map.put("phone", resultSet.getString("telephone"));
				map.put("provName", resultSet.getString("prov_name"));
				map.put("cityName", resultSet.getString("city_name"));
				map.put("countyName", resultSet.getString("country_name"));
				map.put("address", resultSet.getString("address"));
				map.put("dist", resultSet.getDouble("dist"));
				map.put("longitude", resultSet.getDouble("lng"));
				map.put("latitude", resultSet.getDouble("lat"));
				result.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

}
