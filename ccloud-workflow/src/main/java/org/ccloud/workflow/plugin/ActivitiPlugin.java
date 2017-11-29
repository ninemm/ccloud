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

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.DbKit;

public class ActivitiPlugin implements IPlugin{

	private static ProcessEngine processEngine = null;
	private static ProcessEngineConfiguration processEngineConfiguration = null;
	private boolean isStarted = false;
	@Override
	public boolean start(){
		try {
			createProcessEngine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean stop() {
		ProcessEngines.destroy(); 
		isStarted = false;
		return true;
	}

	private Boolean createProcessEngine() throws Exception{
		if (isStarted) {
			return true;
		}
		StandaloneProcessEngineConfiguration conf = (StandaloneProcessEngineConfiguration) ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		conf.setDataSource(DbKit.getConfig().getDataSource());
		conf.setEnableDatabaseEventLogging(false);
		conf.setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DATABASE_TYPE_MYSQL);
		conf.setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_TRUE);//更新
//		conf.setDatabaseSchemaUpdate(ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE);//重置数据库!!!调试用!!!请勿打开!!!
		conf.setDbHistoryUsed(true);
		conf.setTransactionsExternallyManaged(true);//使用托管事务工厂
		conf.setTransactionFactory(new ActivitiTransactionFactory());
		UuidGenerator uuidG = new UuidGenerator();
		conf.setIdGenerator(uuidG);
		conf.setActivityFontName("宋体");
		conf.setLabelFontName("宋体");
		conf.setAnnotationFontName("宋体");
		
		ActivitiPlugin.processEngine = conf.buildProcessEngine();
		isStarted = true;
		//开启流程引擎
		System.out.println("启动流程引擎.......");
		return isStarted;
	}

	// 开启流程服务引擎
	public static ProcessEngine buildProcessEngine() {
		if (processEngine == null)
			if (processEngineConfiguration != null) {
				processEngine = processEngineConfiguration.buildProcessEngine();
			}
			return processEngine;
	}
	
}
