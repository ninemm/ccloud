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
package org.ccloud.model.query;

import org.ccloud.Consts;
import org.ccloud.model.Option;
import org.ccloud.utils.StringUtils;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class OptionQuery extends JBaseQuery {

	protected static final Option DAO = new Option();
	private static final OptionQuery QUERY = new OptionQuery();

	public static OptionQuery me() {
		return QUERY;
	}

	public String findValue(final String key) {
		String value = CacheKit.get(Option.CACHE_NAME, key, new IDataLoader() {
			@Override
			public Object load() {
				Option option = DAO.doFindFirst("option_key =  ?", key);
				if (null != option && option.getOptionValue() != null) {
					return option.getOptionValue();
				}
				return "";
			}
		});

		return "".equals(value) ? null : value;
	}

	public boolean saveOrUpdate(String key, String value) {
		CacheKit.remove(Option.CACHE_NAME, key);
		boolean save = false;
		Option option = DAO.doFindFirst("option_key =  ?", key);
		if (null == option) {
			option = new Option();
			save = true;
		}

		option.setOptionKey(key);
		option.setOptionValue(value);

		if(save) {
			return option.save();
		}else {
			return option.update();
		}
		
		//return option.saveOrUpdateWithoutDate();
	}
	
	public boolean saveOrUpdateBySellerId(String key, String value, String sellerId) {
		CacheKit.remove(Option.CACHE_NAME, sellerId + key);
		Option option = DAO.doFindFirst("option_key = ? AND seller_id = ?", key, sellerId);
		boolean save = false;
		if (null == option) {
			option = new Option();
			save = true;
		}

		option.setOptionKey(key);
		option.setOptionValue(value);
		option.setSellerId(sellerId);
		
		if(save) {
			return option.save();
		}else {
			return option.update();
		}
		

		//return option.saveOrUpdateWithoutDate();
	}	

	public Option findByKey(String key) {
		return DAO.doFindFirst("option_key =  ?", key);
	}

	public Boolean findValueAsBool(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
			}
		}
		return false;
	}

	public Integer findValueAsInteger(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public Float findValueAsFloat(String key) {
		String value = findValue(key);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public String findByKeyAndSellerId(final String key, final String sellerId) {
		String sessionKey = sellerId + key;
		String value = CacheKit.get(Option.CACHE_NAME, sessionKey, new IDataLoader() {
			@Override
			public Object load() {
				Option option = DAO.doFindFirst("option_key = ? AND seller_id = ?", key, sellerId);
				if (null != option && option.getOptionValue() != null) {
					return option.getOptionValue();
				}
				return "";
			}
		});

		return "".equals(value) ? null : value;
	}
	
	public boolean findOptionValueToBoolean(String key, String sellerId) {
		String result = this.findByKeyAndSellerId(key, sellerId);
		if (result == null || result.equals(Consts.OPTION_ON)) {
			return true;
		}
		return false;
	}
}
