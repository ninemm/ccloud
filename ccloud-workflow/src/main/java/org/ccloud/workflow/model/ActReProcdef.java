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

package org.ccloud.workflow.model;

import org.ccloud.model.core.Table;
import org.ccloud.workflow.model.base.BaseActReProcdef;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName = "act_re_procdef", primaryKey = "id")
public class ActReProcdef extends BaseActReProcdef<ActReProcdef> {
	
	private static final long serialVersionUID = 1L;
	public static final ActReProcdef DAO = new ActReProcdef();
	
	/***
	 * 查询分页
	 * @param curr
	 * @param pagesize
	 * @return
	 */
	public Page<ActReProcdef> getDefPage(Integer curr , Integer pagesize){
		return DAO.paginate(curr, pagesize, "select * ", " from (select def.*,dep.DEPLOY_TIME_ from act_re_procdef def ,act_re_deployment dep where def.DEPLOYMENT_ID_=dep.ID_ ORDER BY VERSION_ DESC) a group by KEY_");
	}
}
