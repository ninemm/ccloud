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
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.Metadata;
import org.ccloud.model.User;
import org.ccloud.template.TemplateManager;
import org.ccloud.template.TplModule;
import org.ccloud.utils.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

public class UserQuery extends JBaseQuery {
	protected static final User DAO = new User();
	private static final UserQuery QUERY = new UserQuery();

	public static UserQuery me() {
		return QUERY;
	}

	public List<User> findList(int page, int pagesize, String gender, String role, String status, String orderBy) {
		StringBuilder sqlBuilder = new StringBuilder("select * from user u ");
		LinkedList<Object> params = new LinkedList<Object>();

		boolean needWhere = true;
		needWhere = appendIfNotEmpty(sqlBuilder, "u.gender", gender, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "u.role", role, params, needWhere);
		needWhere = appendIfNotEmpty(sqlBuilder, "u.status", status, params, needWhere);

		buildOrderBy(orderBy, sqlBuilder);

		sqlBuilder.append(" LIMIT ?, ?");
		params.add(page - 1);
		params.add(pagesize);

		if (params.isEmpty()) {
			return DAO.find(sqlBuilder.toString());
		} else {
			return DAO.find(sqlBuilder.toString(), params.toArray());
		}

	}

	public User findFirstFromMetadata(String key, Object value) {
//		return DAO.findFirstFromMetadata(key, value);
		Metadata md = MetaDataQuery.me().findFirstByTypeAndValue(User.METADATA_TYPE, key, value);
		if (md != null) {
			BigInteger id = md.getObjectId();
			return findById(id);
		}
		return null;
	}

	public Page<User> paginate(int pageNumber, int pageSize , String orderby) {
		String select = "select * ";
		StringBuilder fromBuilder = new StringBuilder(" from user u ");
		buildOrderBy(orderby, fromBuilder);
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
	}
	
	public Page<User> paginate(int pageNumber, int pageSize, String keyword, Integer isSale, String orderby) {
		
		String select = "select *";
		StringBuilder fromBuilder = new StringBuilder(" from user u");
		fromBuilder.append(" where u.role <> ?");
//		fromBuilder.append(" where 1=1");
		
		LinkedList<Object> params = new LinkedList<Object>();
		params.add(User.ROLE_ADMINISTRATOR);
		
		if (isSale != null) {
			fromBuilder.append(" AND u.is_sale = ?");
			params.add(isSale);
		}
		
		if (StringUtils.isNotBlank(keyword)) {
			fromBuilder.append(" AND (u.nickname like ? OR u.mobile = ?)");
			params.add("%" + keyword + "%");
			params.add(keyword);
		}
		
		fromBuilder.append(" group by u.id");
		
		buildOrderBy(orderby, fromBuilder);
		
		if (params.isEmpty()) {
			return DAO.paginate(pageNumber, pageSize, true, select, fromBuilder.toString());
		}
		return DAO.paginate(pageNumber, pageSize, true, select, fromBuilder.toString(), params.toArray());
		
	}
	
	public long findCount() {
		return DAO.doFindCount();
	}

	public long findAdminCount() {
		return DAO.doFindCount(" role = ? ", "administrator");
	}

	public User findById(final BigInteger userId) {
		return DAO.getCache(userId, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(userId);
			}
		});
	}

	public User findUserByEmail(final String email) {
		return DAO.getCache(email, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("email = ?", email);
			}
		});
	}

	public User findUserByUsername(final String username) {
		return DAO.getCache(username, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("username = ?", username);
			}
		});
	}

	public User findUserByMobile(final String mobile) {
		return DAO.getCache(mobile, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.doFindFirst("mobile = ?", mobile);
			}
		});
	}
	
	public List<User> findByUserNameOrNickName(final String name) {
		return DAO.doFind("username like ? OR nickname like ? limit 10", "%" + name + "%", "%" + name + "%");
	}
	
	public boolean updateContentCount(User user) {
		long count = 0;
		List<TplModule> modules = TemplateManager.me().currentTemplateModules();
		if (modules != null && !modules.isEmpty()) {
			/*for (TplModule m : modules) {
				long moduleCount = ContentQuery.me().findCountInNormalByModuleAndUserId(m.getName(), user.getId());
				count += moduleCount;
			}*/
		}

		user.setContentCount(count);
		return user.update();
	}

	public int batchDelete(User loginedUser, BigInteger... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (deleteById(loginedUser, ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}
	
	public boolean deleteById(User loginedUser, BigInteger id) {
		User user = findById(id);
		if (user != null && !"admin".equals(user.getUsername()) 
				&& !user.getUsername().equals(loginedUser.getUsername()))
			return user.delete();
		return false;
	}

	protected static void buildOrderBy(String orderBy, StringBuilder fromBuilder) {
		if ("content_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.content_count DESC");
		}

		else if ("comment_count".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.comment_count DESC");
		}

		else if ("username".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.username DESC");
		}

		else if ("nickname".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.nickname DESC");
		}

		else if ("amount".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.amount DESC");
		}

		else if ("logged".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.logged DESC");
		}

		else if ("activated".equals(orderBy)) {
			fromBuilder.append(" ORDER BY u.activated DESC");
		}

		else {
			fromBuilder.append(" ORDER BY u.created DESC");
		}
	}

}
