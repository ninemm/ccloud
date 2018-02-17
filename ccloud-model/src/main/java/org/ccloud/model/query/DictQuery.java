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

import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.Dict;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ComparisonChain;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

public class DictQuery extends JBaseQuery {

	protected static final Dict DAO = new Dict();
	private static final DictQuery QUERY = new DictQuery();

	public static DictQuery me() {
		return QUERY;
	}
	
	public Page<Dict> paginate(int pageNumber, int pageSize, String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `dict` ");
		
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "`name`", keyword, params, true);
		
		buildOrderBy(orderby, fromBuilder);
		
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}
	
	public Dict findById(final BigInteger id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public Dict findByKey(final String type, final String key) {
		return DAO.getCache(type + key, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("`type` = ? and `key` = ?", type, key);
			}
		});
	}
	
	public List<Dict> findDictByType(String type) {
		List<Dict> list = DAO.doFindByCache(Dict.CACHE_NAME, type, "`type` = ? order by ?", type, "`value`");
		Collections.sort(list, new Comparator<Dict>() {
			public int compare(Dict src, Dict dest) {
				return ComparisonChain.start()
					.compare(src.getValue(), dest.getValue())
					.result();
			}
		});
		return list;
	}
	
	public String findName(final String key) {
		String value = CacheKit.get(Dict.CACHE_NAME, key, new IDataLoader() {
			@Override
			public Object load() {
				Dict dict = DAO.doFindFirst("`value` =  ?", key);
				
				StringBuilder sb = new StringBuilder();
				if (null != dict) {
					if (StringUtils.isNotBlank(dict.getIcon())) {
						sb.append("<i class=\"" + dict.getIcon() + "\"></i> ");
					}
					
					if (StringUtils.isNotBlank(dict.getName())) {
						sb.append(dict.getName());
					}
				}
				
				return sb.toString();
			}
		});

		return "".equals(value) ? null : value;
	}
	
	public int batchDelete(BigInteger... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}
	
	protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		if ("key".equals(orderBy)) {
			fromBuilder.append(" ORDER BY `key` desc");
		}

		else if ("value".equals(orderBy)) {
			fromBuilder.append(" ORDER BY `value` desc");
		}

		else {
			fromBuilder.append(" ORDER BY `create_date` desc");
		}
	}

	public List<Dict> findByCode(String dictUnitCode) {
		return DAO.doFind("dict.key = ?", dictUnitCode);
	}
	
	public Dict findbyName(String name) {
		return DAO.doFindFirst("dict.name=?", name);
	}
	
	public Dict findByValue(String value) {
		return DAO.doFindFirst("dict.value=?",value);
	}
}
