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

import java.util.List;

import javax.sql.DataSource;

import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;

public class JGenerator {

	private final String outputPath;
	private final String basePackage;
	private final String dbHost;
	private final String dbName;
	private final String dbUser;
	private final String dbPassword;
	
	
	public JGenerator(String outputPath, String basePackage, String dbHost, String dbName,
			String dbUser, String dbPassword) {
		this.outputPath = outputPath;
		this.basePackage = basePackage;
		this.dbHost = dbHost;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	public void doGenerate(){
		
		String modelPackage = basePackage + ".model";
		String baseModelPackage = basePackage + ".model.base";
		String adminControllerPackage = basePackage + ".controller.admin";
		String modelQueryPackage = basePackage + ".model.query";
//		String viewPackage = basePackage + ".view.admin";
		
		String modelDir = outputPath + modelPackage.replace(".", "/");
		String baseModelDir = outputPath + baseModelPackage.replace(".", "/");
		String adminControllerDir = outputPath + adminControllerPackage.replace(".", "/");
		String modelQueryDir = outputPath + modelQueryPackage.replace(".", "/");
		
		System.out.println("start generate...");
		System.out.println("Generate dir:" + modelDir);
		
		MetaBuilder mb = new MetaBuilder(getDataSource());
		mb.setRemovedTableNamePrefixes("sys_");
		//mb.addExcludedTable("user");
		List<TableMeta> tableMetaList = mb.build();
		
		new JBaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
		new JModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);
		new JModelQueryGenerator(modelQueryPackage, baseModelPackage, modelQueryDir).generate(tableMetaList);
		new JControllerGenerator(adminControllerPackage, baseModelPackage, adminControllerDir).generate(tableMetaList);
		
		System.out.println("Generate finished !!!");
		
	}
	
	public DataSource getDataSource() {

		String jdbc_url = "jdbc:mysql://" + dbHost + "/" + dbName;
		DruidPlugin druidPlugin = new DruidPlugin(jdbc_url, dbUser, dbPassword);
		druidPlugin.start();
		return druidPlugin.getDataSource();
	}

}
