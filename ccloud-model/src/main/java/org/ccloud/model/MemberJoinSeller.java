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

import org.ccloud.model.core.Table;
import org.ccloud.model.base.BaseMemberJoinSeller;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="cc_member_join_seller",primaryKey="member_id,seller_id,user_id")
public class MemberJoinSeller extends BaseMemberJoinSeller<MemberJoinSeller> {

	private static final long serialVersionUID = 1L;

}
