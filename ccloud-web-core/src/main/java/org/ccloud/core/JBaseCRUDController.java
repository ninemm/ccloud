/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
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
package org.ccloud.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.ccloud.model.User;
import org.ccloud.model.core.JModel;
import org.ccloud.utils.DataAreaUtil;

import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;

public class JBaseCRUDController<M extends JModel<? extends JModel<?>>> extends JBaseController {

	private static final Log logger = Log.getLog(JBaseCRUDController.class);;
	private final Class<M> mClazz;
	protected final M mDao;

	@SuppressWarnings("unchecked")
	public JBaseCRUDController() {
		ParameterizedType type = getParameterizedType(getClass());
		mClazz = (Class<M>) type.getActualTypeArguments()[0];
		mDao = getDao();
	}

	private ParameterizedType getParameterizedType(Class<?> clazz) {
		if (clazz == Object.class) {
			logger.error("get ParameterizedType error in _BaseController.class");
			return null;
		}
		Type genericSuperclass = clazz.getGenericSuperclass();
		if (genericSuperclass instanceof ParameterizedType) {
			return (ParameterizedType) genericSuperclass;
		} else {
			return getParameterizedType(clazz.getSuperclass());
		}
	}

	@SuppressWarnings("unchecked")
	public void index() {
		Page<M> page = onIndexDataLoad(getPageNumber(), getPageSize());
		if (null == page) {
			page = (Page<M>) getDao().doPaginate(getPageNumber(), getPageSize());
		}
		setAttr("page", page);
		render("index.html");
	}

	public void edit() {
		String id = getPara("id");
		if (StrKit.notBlank(id)) {
			setAttr(StrKit.firstCharToLowerCase(mClazz.getSimpleName()), getDao().findById(id));
		}
		render("edit.html");
	}

	public void save() {
		if (isMultipartRequest()) {
			getFile();
		}
		
		M m = getModel(mClazz);

		if (!onModelSaveBefore(m)) {
			renderAjaxResultForError();
			return;
		}
		m.saveOrUpdate();

		if (!onModelSaveAfter(m)) {
			renderAjaxResultForError();
			return;
		}

		renderAjaxResultForSuccess("ok");
	}

	public void delete() {
		String id = getPara("id");
		if (StrKit.notBlank(id)) {
			getDao().deleteById(id);
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError();
		}
	}

	private M getDao() {
		M m = null;
		try {
			m = mClazz.newInstance();
		} catch (Exception e) {
			logger.error("get DAO error.", e);
		}
		return m;
	}

	public Page<M> onIndexDataLoad(int pageNumber, int pageSize) {
		return null;
	}

	public boolean onModelSaveBefore(M m) {
		// do nothing
		return true;
	}

	public boolean onModelSaveAfter(M m) {
		// do nothing
		return true;
	}
	
//	public Map<String, String> getDeptIdAndDataArea() {
//
//		User user = getSessionAttr("user");
//		Map<String, String> map = Maps.newHashMap();
//		Subject subject = SecurityUtils.getSubject();
//
//		if (subject.isPermitted("/admin/all")) {// 超级管理员
//			map.put("deptId", "");
//			map.put("dataArea", "");
//
//		} else if (subject.isPermitted("/admin/manager")) {// 经销商管理员
//			map.put("deptId", user.getDepartmentId());
//			map.put("dataArea", DataAreaUtil.getUserDeptDataArea(user.getDataArea()) + "%");
//		} else {// 业务员
//			map.put("deptId", user.getDepartmentId());
//			map.put("dataArea", DataAreaUtil.getUserDeptDataArea(user.getDataArea()));
//		}
//
//		return map;
//	}

}
