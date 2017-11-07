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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.ICallback;

public class AroundCustomerSalesCallback implements ICallback {

	private ResultSet resultSet = null;

	private double longitude;
	private double latitude;
	private double dist;
	private String startDate;
	private String endDate;
	private String dealerCode;

	@Override
	public Object call(Connection conn) {
		CallableStatement proc = null;

		List<Map<String, Object>> result = Lists.newArrayList();

		try {
			proc = conn.prepareCall("{ call around_customer_sales(?, ?, ?, ?, ?, ?) }");

			proc.setDouble(1, getLongitude());
			proc.setDouble(2, getLatitude());
			proc.setDouble(3, getDist());
			proc.setString(4, getStartDate());
			proc.setString(5, getEndDate());
			proc.setString(6, getDealerCode());

			proc.execute();

			resultSet = proc.getResultSet();

			while (resultSet.next()) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("customerId", resultSet.getString("customerId"));
				map.put("customerName", resultSet.getString("customerName"));
				map.put("type", resultSet.getInt("type"));
				map.put("contacts", resultSet.getString("contacts"));
				map.put("phone", resultSet.getString("phone"));
				map.put("provName", resultSet.getString("provName"));
				map.put("cityName", resultSet.getString("cityName"));
				map.put("countyName", resultSet.getString("countyName"));
				map.put("address", resultSet.getString("address"));
				map.put("dist", resultSet.getDouble("dist"));
				map.put("longitude", resultSet.getDouble("longitude"));
				map.put("latitude", resultSet.getDouble("latitude"));
				map.put("totalAmount", resultSet.getLong("totalAmount"));
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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getDealerCode() {
		return dealerCode;
	}

	public void setDealerCode(String dealerCode) {
		this.dealerCode = dealerCode;
	}

}
