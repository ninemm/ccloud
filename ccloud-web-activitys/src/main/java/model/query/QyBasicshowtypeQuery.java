/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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
package model.query;

import java.util.LinkedList;

import org.ccloud.model.QyBasicshowtype;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class QyBasicshowtypeQuery extends JBaseQuery { 

	protected static final QyBasicshowtype DAO = new QyBasicshowtype();
	private static final QyBasicshowtypeQuery QUERY = new QyBasicshowtypeQuery();

	public static QyBasicshowtypeQuery me() {
		return QUERY;
	}

	public QyBasicshowtype findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<QyBasicshowtype> paginate(int pageNumber, int pageSize, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `qy_basicshowtype` ");

		LinkedList<Object> params = new LinkedList<Object>();

		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
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

	public void deleteAll() {
		DAO.doDelete("ShowTypeID is not null");
	}

	public String findNameById(String id) {
		QyBasicshowtype showtype = DAO.doFindFirst("ShowTypeID = ?", id);
		if (showtype != null) {
			return showtype.getShowTypeName();
		} else {
			return null;
		}
	}

	public String findIdByDict(String key) {
		StringBuilder fromBuilder = new StringBuilder("SELECT s.ShowTypeID FROM qy_basicshowtype s LEFT JOIN dict d ON d.`name` = s.ShowTypeName ");
		fromBuilder.append("WHERE d.key = ? ");
		Record record = Db.findFirst(fromBuilder.toString(), key);
		if (record != null) {
			return record.getStr("ShowTypeID");
		} else {
			return null;
		}
	}

	
}
