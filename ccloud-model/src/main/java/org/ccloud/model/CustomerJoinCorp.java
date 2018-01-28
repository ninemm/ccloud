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

import org.ccloud.message.MessageKit;
import org.ccloud.model.core.Table;
import org.ccloud.model.base.BaseCustomerJoinCorp;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@Table(tableName="cc_customer_join_corp",primaryKey="customer_id,seller_id")
public class CustomerJoinCorp extends BaseCustomerJoinCorp<CustomerJoinCorp> {

	private static final long serialVersionUID = 1L;

	public boolean delete(String customerId, String sellerId ) {
		boolean deleted = this.delete();
		if (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }
		return deleted;
	}

}
