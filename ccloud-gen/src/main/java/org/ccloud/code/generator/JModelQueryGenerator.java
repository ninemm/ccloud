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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JModelQueryGenerator extends ModelGenerator {

	public JModelQueryGenerator(String modelPackageName,
			String baseModelPackageName, String modelOutputDir) {
		
		super(modelPackageName, baseModelPackageName, modelOutputDir);
		
		this.packageTemplate = "/**%n"
				+ " * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).%n"
				+ " *%n"
				+ " * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the \"License\");%n"
				+ " * you may not use this file except in compliance with the License.%n"
				+ " * You may obtain a copy of the License at%n"
				+ " *%n"
				+ " *      http://www.gnu.org/licenses/lgpl-3.0.txt%n"
				+ " *%n"
				+ " * Unless required by applicable law or agreed to in writing, software%n"
				+ " * distributed under the License is distributed on an \"AS IS\" BASIS,%n"
				+ " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.%n"
				+ " * See the License for the specific language governing permissions and%n"
				+ " * limitations under the License.%n"
				+ " */%n"
				+"package %s;%n%n";
		
		this.importTemplate = "import java.util.List;%n"
				+ "import java.util.LinkedList%n"
				+ "import org.ccloud.utils.StringUtils;%n%n"
				+ "import org.ccloud.model.%s;%n%n"
				+ "import com.jfinal.plugin.activerecord.Page;%n"
				+ "import com.jfinal.plugin.ehcache.CacheKit;%n"
				+ "import com.jfinal.plugin.ehcache.IDataLoader;%n%n";
		
		this.classDefineTemplate = "/**%n"
				+ " * Generated by 九毫米(http://9mm.tech).%n"
				+ " */%n"
				+ "public class %sQuery extends JBaseQuery { %n%n"
				+ "\tprotected static final %s DAO = new %s();%n"
				+ "\tprivate static final %sQuery QUERY = new %sQuery();%n%n"
				
				+ "\tpublic static %sQuery me() {%n"
				+ "\t\treturn QUERY;%n"
				+ "\t}%n%n"
				
				+ "\tpublic %s findById(final String id) {%n"
				+ "\t\treturn DAO.getCache(id, new IDataLoader() {%n"
				+ "\t\t\t@Override%n"
				+ "\t\t\tpublic Object load() {%n"
				+ "\t\t\t\treturn DAO.findById(id);%n"
				+ "\t\t\t}%n"
				+ "\t\t});%n"
				+ "\t}%n%n"
				
				+ "\tpublic Page<%s> paginate(int pageNumber, int pageSize, String orderby) {%n"
				+ "\t\tString select = \"select * \";%n"
				+ "\t\tStringBuilder fromBuilder = new StringBuilder(\"from `%s` \");%n%n"
				+ "\t\tLinkedList<Object> params = new LinkedList<Object>();%n%n"
				+ "\t\tif (params.isEmpty())%n"
				+ "\t\t\treturn DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());%n%n"
				+ "\t\treturn DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());%n"
				+ "\t}%n%n"
				
				+ "\tpublic int batchDelete(String... ids) {%n"
				+ "\t\tif (ids != null && ids.length > 0) {%n"
				+ "\t\t\tint deleteCount = 0;%n"
				+ "\t\t\tfor (int i = 0; i < ids.length; i++) {%n"
				+ "\t\t\t\tif (DAO.deleteById(ids[i])) {%n"
				+ "\t\t\t\t\t++deleteCount;%n"
				+ "\t\t\t\t}%n"
				+ "\t\t\t}%n"
				+ "\t\t\treturn deleteCount;%n"
				+ "\t\t}%n"
				+ "\t\treturn 0;%n"
				+ "\t}%n%n"
				;
		
		
		this.daoTemplate = "";
		
	}
	
	@Override
	protected void genImport(TableMeta tableMeta, StringBuilder ret) {
		// TODO Auto-generated method stub
//		super.genImport(tableMeta, ret);
		ret.append(String.format(importTemplate, tableMeta.modelName));
	}
	
	@Override
	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(classDefineTemplate, tableMeta.modelName, tableMeta.modelName, tableMeta.modelName, 
			tableMeta.modelName, tableMeta.modelName, tableMeta.modelName, tableMeta.modelName, 
			tableMeta.modelName, tableMeta.name));
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate ModelQuery ...");
		for (TableMeta tableMeta : tableMetas)
			genModelContent(tableMeta);
		
		writeToFile(tableMetas);
	}
	
	protected void writeToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(modelOutputDir);
		if (!dir.exists())
			dir.mkdirs();
		
		String target = modelOutputDir + File.separator + tableMeta.modelName + "Query.java";
		
		File file = new File(target);
		if (file.exists()) {
			return ;	// 若 Model 存在，不覆盖
		}
		
		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		}
		finally {
			fw.close();
		}
	}

}
