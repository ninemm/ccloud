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

import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.GroupRoleRel;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class GroupRoleRelQuery extends JBaseQuery {

    protected static final GroupRoleRel DAO = new GroupRoleRel();
    private static final GroupRoleRelQuery QUERY = new GroupRoleRelQuery();

    public static GroupRoleRelQuery me() {
        return QUERY;
    }

    public GroupRoleRel findById(final String id) {
        return DAO.getCache(id, new IDataLoader() {
            @Override
            public Object load() {
                return DAO.findById(id);
            }
        });
    }

    public List<GroupRoleRel> findIdsByGroupId(final  String groupId) {
        StringBuilder sql = new StringBuilder("select id ");
        sql.append("from `group_role_rel` ");
        sql.append("where group_id = ? ");
        return DAO.find(sql.toString(), groupId);
    }

    public Page<GroupRoleRel> paginate(int pageNumber, int pageSize, String orderby) {
        String select = "select * ";
        StringBuilder fromBuilder = new StringBuilder("from `group_role_rel` ");

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

    public int deleteByGroupId(String groupId) {

        return DAO.doDelete(" group_id = ?", groupId);
    }

}

