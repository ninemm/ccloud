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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.BaseModelGenerator;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;

public class JBaseModelGenerator extends BaseModelGenerator {

	public JBaseModelGenerator(String baseModelPackageName,
			String baseModelOutputDir) {
		super(baseModelPackageName, baseModelOutputDir);
		
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

		this.classDefineTemplate = "/**%n"
				+ " *  Auto generated by 九毫米(http://9mm.tech), do not modify this file.%n"
				+ " */%n"
				+ "@SuppressWarnings(\"serial\")%n"
				+ "public abstract class %s<M extends %s<M>> extends JModel<M> implements IBean {%n%n"
				+ "\tpublic static final String CACHE_NAME = \"%s\";%n"
				+ "\tpublic static final String METADATA_TYPE = \"%s\";%n%n"
				
				+ "\tpublic static final String ACTION_ADD = \"%s:add\";%n"
				+ "\tpublic static final String ACTION_DELETE = \"%s:delete\";%n"
				+ "\tpublic static final String ACTION_UPDATE = \"%s:update\";%n%n"
				
				+ "\tpublic void removeCache(Object key){%n"
				+ "\t\tif(key == null) return;%n"
				+ "\t\tJCacheKit.remove(CACHE_NAME, key);%n"
				+ "\t}%n%n"
				
				
				+ "\tpublic void putCache(Object key,Object value){%n"
				+ "\t\tJCacheKit.put(CACHE_NAME, key, value);%n"
				+ "\t}%n%n"
				
				
				+ "\tpublic M getCache(Object key){%n"
				+ "\t\treturn JCacheKit.get(CACHE_NAME, key);%n"
				+ "\t}%n%n"
				
				
				+ "\tpublic M getCache(Object key,IDataLoader dataloader){%n"
				+ "\t\treturn JCacheKit.get(CACHE_NAME, key, dataloader);%n"
				+ "\t}%n%n"
				
		
//				+ "\tpublic Metadata createMetadata(){%n"
//				+ "\t\tMetadata md = new Metadata();%n"
//				+ "\t\tmd.setObjectId(getId());%n"
//				+ "\t\tmd.setObjectType(METADATA_TYPE);%n"
//				+ "\t\treturn md;%n"
//				+ "\t}%n%n"
//				
//				
//				+ "\tpublic Metadata createMetadata(String key,String value){%n"
//				+ "\t\tMetadata md = new Metadata();%n"
//				+ "\t\tmd.setObjectId(getId());%n"
//				+ "\t\tmd.setObjectType(METADATA_TYPE);%n"
//				+ "\t\tmd.setMetaKey(key);%n"
//				+ "\t\tmd.setMetaValue(value);%n"
//				+ "\t\treturn md;%n"
//				+ "\t}%n%n"
//	
//
//				+ "\tpublic boolean saveOrUpdateMetadta(String key,String value){%n"
//				+ "\t\tMetadata metadata = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);%n"
//				+ "\t\tif (metadata == null) {%n"
//				+ "\t\t\tmetadata = createMetadata(key, value);%n"
//				+ "\t\t\treturn metadata.save();%n"
//				+ "\t\t}%n"
//				+ "\t\tmetadata.setMetaValue(value);%n"
//				+ "\t\treturn metadata.update();%n"
//				+ "\t}%n%n"
//				
//				
//				+ "\tpublic String metadata(String key) {%n"
//				+ "\t\tMetadata m = MetaDataQuery.me().findByTypeAndIdAndKey(METADATA_TYPE, getId(), key);%n"
//				+ "\t\tif (m != null) {%n"
//				+ "\t\t\treturn m.getMetaValue();%n"
//				+ "\t\t}%n"
//				+ "\t\treturn null;%n"
//				+ "\t}%n%n"
		
				
				+ "\t@Override%n"
				+ "\tpublic boolean equals(Object o) {%n"
				+ "\t\tif(o == null){ return false; }%n"
				+ "\t\tif(!(o instanceof %s<?>)){return false;}%n%n"
				+ "\t\t%s<?> m = (%s<?>) o;%n"
				+ "\t\tif(m.getId() == null){return false;}%n%n"
				+ "\t\treturn m.getId().compareTo(this.getId()) == 0;%n"
				+ "\t}%n%n"
		
				
				+ "\t@Override%n"
				+ "\tpublic boolean save() {%n"
				+ "\t\tboolean saved = super.save();%n"
				+ "\t\tif (saved) { MessageKit.sendMessage(ACTION_ADD, this); }%n"
				+ "\t\treturn saved;%n"
				+ "\t}%n%n"
				
				
				+ "\t@Override%n"
				+ "\tpublic boolean delete() {%n"
				+ "\t\tboolean deleted = super.delete();%n"
				+ "\t\tif (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }%n"
				+ "\t\treturn deleted;%n"
				+ "\t}%n%n"
				
				
				+ "\t@Override%n"
				+ "\tpublic boolean deleteById(Object idValue) {%n"
				+ "\t\tboolean deleted = super.deleteById(idValue);%n"
				+ "\t\tif (deleted) { MessageKit.sendMessage(ACTION_DELETE, this); }%n"
				+ "\t\treturn deleted;%n"
				+ "\t}%n%n"
				
				
				+ "\t@Override%n"
				+ "\tpublic boolean update() {%n"
				+ "\t\tboolean update = super.update();%n"
				+ "\t\tif (update) { MessageKit.sendMessage(ACTION_UPDATE, this); }%n"
				+ "\t\treturn update;%n"
				+ "\t}%n%n"
		
				;
		
		this.importTemplate = "import org.ccloud.message.MessageKit;%n"
				//+ "import org.ccloud.model.Metadata;%n"
				+ "import org.ccloud.model.core.JModel;%n"
				//+ "import org.ccloud.model.query.MetaDataQuery;%n"
				//+ "import java.math.BigInteger;%n%n"
				+ "import com.jfinal.plugin.activerecord.IBean;%n"
				+ "import org.ccloud.cache.JCacheKit;%n"
				+ "import com.jfinal.plugin.ehcache.IDataLoader;%n%n";
		

	}

	@Override
	protected void genClassDefine(TableMeta tableMeta, StringBuilder ret) {
		ret.append(String.format(classDefineTemplate, tableMeta.baseModelName,
			tableMeta.baseModelName, tableMeta.name, tableMeta.name, tableMeta.name, tableMeta.name, 
			tableMeta.name, tableMeta.baseModelName, tableMeta.baseModelName, tableMeta.baseModelName));
	}
	
	protected String idGetterTemplate =
			"\tpublic java.math.BigInteger getId() {%n" +
				"\t\tObject id = get(\"id\");%n" +
				"\t\tif (id == null)%n" +
				"\t\t\treturn null;%n%n" +
				"\t\treturn id instanceof BigInteger ? (BigInteger)id : new BigInteger(id.toString());%n" +
			"\t}%n%n";
	
	
	@Override
	protected void genGetMethodName(ColumnMeta columnMeta, StringBuilder ret) {
//		if("id".equals(columnMeta.attrName)){
//			ret.append(String.format(idGetterTemplate));
//		} else {
		
			String getterMethodName = "get" + StrKit.firstCharToUpperCase(columnMeta.attrName);
			String getterOfModel = getGetterOfModel(columnMeta.javaType);
			String getter = String.format(getterTemplate, columnMeta.javaType, getterMethodName,getterOfModel, columnMeta.name);
			ret.append(getter);
//		}
	}

}
