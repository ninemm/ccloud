/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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
package org.ccloud.core;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.cache.JCachePlugin;
import org.ccloud.core.cache.ActionCacheHandler;
import org.ccloud.core.interceptor.HookInterceptor;
import org.ccloud.core.interceptor.JI18nInterceptor;
import org.ccloud.core.render.CCloudRenderFactory;
import org.ccloud.interceptor.AdminInterceptor;
import org.ccloud.interceptor.GlobelInterceptor;
import org.ccloud.interceptor.PublicInterceptor;
import org.ccloud.log.SystemLogThread;
import org.ccloud.message.plugin.MessagePlugin;
import org.ccloud.model.core.JModelMapping;
import org.ccloud.model.core.Table;
import org.ccloud.route.RouterMapping;
import org.ccloud.shiro.core.ShiroInterceptor;
import org.ccloud.shiro.core.ShiroPlugin;
import org.ccloud.utils.ClassUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.workflow.plugin.ActivitiPlugin;

import com.alibaba.druid.filter.stat.StatFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.qyweixin.sdk.api.ApiConfigKit;
import com.jfinal.render.ViewType;
import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.DiskStoreConfiguration;

public abstract class CCloudConfig extends JFinalConfig {

	static Log log = Log.getLog(CCloudConfig.class);
	
	Routes routes;

	public void configConstant(Constants constants) {

		log.info("CCloud is starting ...");

		PropKit.use("ccloud.properties");

		constants.setDevMode(PropKit.getBoolean("dev_mode", false));
		constants.setViewType(ViewType.FREE_MARKER);
		constants.setI18nDefaultBaseName("language");
//		constants.setErrorRenderFactory(new JErrorRenderFactory());
		constants.setBaseUploadPath("attachment");
		constants.setEncoding(Consts.CHARTSET_UTF8);
		constants.setMaxPostSize(1024 * 1024 * 200);
		constants.setRenderFactory(new CCloudRenderFactory());
		
		// constants.setTokenCache(new JTokenCache());
		ApiConfigKit.setDevMode(PropKit.getBoolean("dev_mode", false));
	}

	@SuppressWarnings("unchecked")
	public void configRoute(Routes routes) {
		List<Class<Controller>> controllerClassList = ClassUtils.scanSubClass(Controller.class);
		if (controllerClassList != null) {
			for (Class<?> clazz : controllerClassList) {
				RouterMapping urlMapping = clazz.getAnnotation(RouterMapping.class);
				if (null != urlMapping && StringUtils.isNotBlank(urlMapping.url())) {
					if (StrKit.notBlank(urlMapping.viewPath())) {
						routes.add(urlMapping.url(), (Class<? extends Controller>) clazz, urlMapping.viewPath());
					} else {
						routes.add(urlMapping.url(), (Class<? extends Controller>) clazz);
					}
				}
			}
		}
		
		this.routes = routes;
	}

	public void configPlugin(Plugins plugins) {
		
		plugins.add(createEhCachePlugin());

		if (CCloud.isInstalled()) {

			JCachePlugin leCachePlugin = new JCachePlugin();
			plugins.add(leCachePlugin);

			DruidPlugin druidPlugin = createDruidPlugin();
			plugins.add(druidPlugin);

			ActiveRecordPlugin activeRecordPlugin = createRecordPlugin(druidPlugin);
			activeRecordPlugin.setCache(leCachePlugin.getCache());
			activeRecordPlugin.setShowSql(JFinal.me().getConstants().getDevMode());

			plugins.add(activeRecordPlugin);

			//plugins.add(new SearcherPlugin());
			
			// plugins.add(createCron4jPlugin());

			plugins.add(new MessagePlugin());
			
			plugins.add(new ActivitiPlugin());
			
			ShiroPlugin shiroPlugin = createShiroPlugin();
			plugins.add(shiroPlugin);
		}
	}

	public EhCachePlugin createEhCachePlugin() {
		String ehcacheDiskStorePath = PathKit.getRootClassPath();
		File pathFile = new File(ehcacheDiskStorePath, ".ehcache");

		Configuration cfg = ConfigurationFactory.parseConfiguration();
		cfg.addDiskStore(new DiskStoreConfiguration().path(pathFile.getAbsolutePath()));
		return new EhCachePlugin(cfg);
	}

	public DruidPlugin createDruidPlugin() {

		Prop dbProp = PropKit.use("db.properties");
		String db_host = dbProp.get("db_host").trim();

		String db_host_port = dbProp.get("db_host_port");
		db_host_port = StringUtils.isNotBlank(db_host_port) ? db_host_port.trim() : "3306";

		String db_name = dbProp.get("db_name").trim();
		String db_user = dbProp.get("db_user").trim();
		String db_password = dbProp.get("db_password").trim();

		String jdbc_url = "jdbc:mysql://" + db_host + ":" + db_host_port + "/" + db_name + "?" + "useUnicode=true&"
				+ "characterEncoding=utf8&" + "zeroDateTimeBehavior=convertToNull&useSSL=false"; 

		DruidPlugin druidPlugin = new DruidPlugin(jdbc_url, db_user, db_password);
		druidPlugin.addFilter(new StatFilter());

		return druidPlugin;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ActiveRecordPlugin createRecordPlugin(IDataSourceProvider dsp) {
		ActiveRecordPlugin arPlugin = new ActiveRecordPlugin(dsp);
		List<Class<Model>> modelClassList = ClassUtils.scanSubClass(Model.class);
		if (modelClassList != null) {
			String tablePrefix = PropKit.use("db.properties").get("db_tablePrefix");
			tablePrefix = (StrKit.isBlank(tablePrefix)) ? "" : (tablePrefix.trim());
			for (Class<?> clazz : modelClassList) {
				Table tb = clazz.getAnnotation(Table.class);
				if (tb == null)
					continue;
				String tname = tablePrefix + tb.tableName();
				if (StringUtils.isNotBlank(tb.primaryKey())) {
					arPlugin.addMapping(tname, tb.primaryKey(), (Class<? extends Model<?>>) clazz);
				} else {
					arPlugin.addMapping(tname, (Class<? extends Model<?>>) clazz);
				}

				JModelMapping.me().mapping(clazz.getSimpleName().toLowerCase(), tname);
			}
		}
		return arPlugin;
	}
	
	public Cron4jPlugin createCron4jPlugin() {
		Cron4jPlugin cron4jPlugin = new Cron4jPlugin(PropKit.use("ccloud.properties"));
		return cron4jPlugin;
	}
	
	public ShiroPlugin createShiroPlugin() {
		ShiroPlugin shiroPlugin = new ShiroPlugin(this.routes);
	    shiroPlugin.setLoginUrl("/admin/login");//登陆url：未验证成功跳转
	    shiroPlugin.setSuccessUrl("/admin");//登陆成功url：验证成功自动跳转
	    shiroPlugin.setUnauthorizedUrl("/admin/checkRole");//授权url：未授权成功自动跳转
	    
	    return shiroPlugin;
	}

	public void configInterceptor(Interceptors interceptors) {
		interceptors.add(new JI18nInterceptor());
		interceptors.add(new GlobelInterceptor());
		interceptors.add(new AdminInterceptor());
		interceptors.add(new HookInterceptor());
		interceptors.add(new ShiroInterceptor());
		interceptors.add(new PublicInterceptor());
	}

	public void configHandler(Handlers handlers) {
		handlers.add(new ActionCacheHandler());
//		handlers.add(new UrlSkipHandler("/ca/.*|/se/.*|.*.jsp|.*.htm|.*.html|.*.js|.*.css|.*.json|.*.png"
//				+ "|.*.gif|.*.jpg|.*.jpeg|.*.bmp|.*.ico|.*.exe|.*.txt|.*.zip|.*.rar|.*.7z", false));
		handlers.add(new JHandler());
		MyDruidStatViewHandler druidViewHandler = new MyDruidStatViewHandler();
		handlers.add(druidViewHandler);
	}

	@Override
	public void afterJFinalStart() {
		if (CCloud.isInstalled()) {
			CCloud.loadFinished();
		}

		CCloud.renderImmediately();
		onCCloudStarted();
		SystemLogThread.start();
		
		log.info("CCloud is started!");
	}

	@Override
	public void beforeJFinalStop() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				try {
					Driver driver = drivers.nextElement();
					DriverManager.deregisterDriver(driver);
				} catch (Exception e) {
					log.error("deregisterDriver error in beforeJFinalStop() method.", e);
				}
			}
		}
		
		try {
			// Deal With Tomcat Server Memory Leak
			AbandonedConnectionCleanupThread.shutdown();
		} catch (InterruptedException e) {
			log.warn("Tomcat Server problem cleaning up:" + e.getMessage());
			e.printStackTrace();
		}
		
		 CacheManager.getInstance().shutdown();
	}

	public void onCCloudStarted() {
	}

}
