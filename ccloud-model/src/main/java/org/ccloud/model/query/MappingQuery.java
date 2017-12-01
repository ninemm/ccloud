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
package org.ccloud.model.query;

import java.sql.SQLException;
import java.util.List;

import org.ccloud.model.Mapping;
import org.ccloud.model.core.Jdb;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

public class MappingQuery extends JBaseQuery {

	protected static final Mapping DAO = new Mapping();
	private static final MappingQuery QUERY = new MappingQuery();

	public static MappingQuery me() {
		return QUERY;
	}

	public int doDelByContentId(String contentId) {
		return DAO.doDelete("content_id = ?", contentId);
	}

	public boolean doBatchUpdate(final String contentId, final String[] taxonomyIds) {
		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				doDelByContentId(contentId);
				for (String taxonomyid : taxonomyIds) {
					Mapping mapping = new Mapping();
					mapping.setContentId(contentId);
					mapping.setTaxonomyId(taxonomyid);
					if (!mapping.save()) {
						return false;
					}
				}
				return true;
			}
		});
	}

	public List<Mapping> findListByContentId(String contentId) {
		return DAO.doFindByCache(Mapping.CACHE_NAME, Mapping.buildKeyByContentId(contentId), " content_id = ?",
				contentId);
	}

	public void deleteByContentId(String id) {
		Jdb.update("DELETE FROM mapping WHERE content_id = ?", id);
	}

	public void deleteByTaxonomyId(String id) {
		Jdb.update("DELETE FROM mapping WHERE taxonomy_id = ? ", id);
	}

	public long findCountByTaxonomyId(String id) {
		return DAO.doFindCount("taxonomy_id = ?", id);
	}

	public long findCountByTaxonomyId(String id, String contentStatus) {
		String sql = "SELECT COUNT(*) FROM mapping m ";
		sql += "left join content c ON m.content_id=c.id ";
		sql += "where m.taxonomy_id = ? and c.status = ?";
		return Jdb.queryLong(sql, id, contentStatus);
	}

	public long findCountByContentId(String id) {
		return DAO.doFindCount("content_id = ?", id);
	}
}
