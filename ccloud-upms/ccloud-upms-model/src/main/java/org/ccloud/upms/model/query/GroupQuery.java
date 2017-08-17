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
package org.ccloud.upms.model.query;

import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.query.JBaseQuery;
import org.ccloud.upms.model.Group;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GroupQuery extends JBaseQuery { 

	protected static final Group DAO = new Group();
	private static final GroupQuery QUERY = new GroupQuery();

	public static GroupQuery me() {
		return QUERY;
	}

	public Group findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<Group> paginate(int pageNumber, int pageSize,String keyword, String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder("from `group` ");

		LinkedList<Object> params = new LinkedList<Object>();

		appendIfNotEmptyWithLike(fromBuilder, "`group_name`", keyword, params, true);

		fromBuilder.append("order by " + orderby);

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

    public List<Group> findAll() {
        return DAO.doFind();
    }

	public boolean delete(String id) {
		return DAO.deleteById(id);
	}
}
