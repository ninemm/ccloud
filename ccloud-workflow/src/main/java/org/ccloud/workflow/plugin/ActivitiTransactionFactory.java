/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@126.com).
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
package org.ccloud.workflow.plugin;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

public class ActivitiTransactionFactory implements TransactionFactory {

	@Override
	public void setProperties(Properties props) {
	}

	@Override
	public Transaction newTransaction(Connection conn) {
		return new ActivitiTransaction(conn);
	}

	@Override
	public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
		return new ActivitiTransaction(ds, level, autoCommit);
	}
}
