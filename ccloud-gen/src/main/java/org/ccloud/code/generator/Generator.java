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
package org.ccloud.code.generator;

public class Generator {

	public static void main(String[] args) {
		
		String modelPackage = "org.ccloud";
		String outputPath = "D:/ccloud/";
		String dbHost = "127.0.0.1";//外网开发数据库IP
		String dbName = "local-v2-test";
		String dbUser = "root";
		String dbPassword = "ejuster@jiayan";
		
		new JGenerator(outputPath, modelPackage, dbHost, dbName, dbUser, dbPassword).doGenerate();

	}

}
