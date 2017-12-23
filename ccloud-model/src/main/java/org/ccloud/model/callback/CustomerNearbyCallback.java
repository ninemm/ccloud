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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.plugin.activerecord.ICallback;

public class CustomerNearbyCallback implements ICallback {

	private ResultSet resultSet = null;

	private BigDecimal lon;
	private BigDecimal lat;
	private double dist;
	private String userId;

	@Override
	public Object call(Connection conn) {
		CallableStatement proc = null;

		List<Map<String, Object>> result = Lists.newArrayList();

		try {
			proc = conn.prepareCall("{ call nearby_customer(?, ?, ?, ?) }");

			proc.setBigDecimal(1, getLon());
			proc.setBigDecimal(2, getLat());
			proc.setDouble(3, getDist());
			proc.setString(4, getUserId());

			proc.execute();

			resultSet = proc.getResultSet();

			while (resultSet.next()) {

				Map<String, Object> map = Maps.newHashMap();
				map.put("id", resultSet.getString("id"));
				map.put("customer_code", resultSet.getString("customer_code"));
				map.put("customer_name", resultSet.getString("customer_name"));
				map.put("contact", resultSet.getString("contact"));
				map.put("mobile", resultSet.getString("mobile"));
				map.put("prov_name", resultSet.getString("prov_name"));
				map.put("city_name", resultSet.getString("city_name"));
				map.put("country_name", resultSet.getString("country_name"));
				map.put("prov_code", resultSet.getString("prov_code"));
				map.put("city_code", resultSet.getString("city_code"));
				map.put("country_code", resultSet.getString("country_code"));
				map.put("address", resultSet.getString("address"));
				map.put("dist", resultSet.getLong("dist"));
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
	
	public BigDecimal getLon() {
		return lon;
	}

	public void setLon(BigDecimal lon) {
		this.lon = lon;
	}

	public BigDecimal getLat() {
		return lat;
	}

	public void setLat(BigDecimal lat) {
		this.lat = lat;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


}
