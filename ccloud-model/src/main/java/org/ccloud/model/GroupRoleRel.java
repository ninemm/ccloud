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
package org.ccloud.model;

import com.jfinal.kit.StrKit;
import org.ccloud.model.core.Table;
import org.ccloud.model.base.BaseGroupRoleRel;


/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="group_role_rel",primaryKey="id")
public class GroupRoleRel extends BaseGroupRoleRel<GroupRoleRel> {
    @Override
    public boolean saveOrUpdate() {

        if (null == get(getPrimaryKey())) {
            set("id", StrKit.getRandomUUID());
            return super.save();
        }
        return super.update();
    }

}

