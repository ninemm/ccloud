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
package org.ccloud.core.addon;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.ccloud.model.query.OptionQuery;
import org.ccloud.utils.FileUtils;
import org.ccloud.utils.StringUtils;

import com.google.common.io.Files;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;

/**
 * 插件管理器，负责加载、启动、停止插件。
 * 
 * @author michael
 */
public class AddonManager {

	public static final String MESSAGE_ON_ADDON_LOAD_START = "addonManager:on_addon_load_start";
	public static final String MESSAGE_ON_ADDON_LOAD_FINISHED = "addonManager:on_addon_load_finished";

	public static final String MESSAGE_ON_ADDON_STARTUP_START = "addonManager:on_addon_startup_start";
	public static final String MESSAGE_ON_ADDON_STARTUP_FINISHED = "addonManager:on_addon_startup_finished";

	private static final Log log = Log.getLog(AddonManager.class);

	private final Map<String, AddonInfo> addonMap = new ConcurrentHashMap<String, AddonInfo>();

	private static AddonManager manager = new AddonManager();

	private AddonManager() {
		autoInstall();
		autoStart();
	}

	public static AddonManager me() {
		return manager;
	}

	public AddonInfo findById(String id) {
		if (id == null) {
			return null;
		}

		return addonMap.get(id);
	}

	/**
	 * 启动插件
	 * 
	 * @param addon
	 * @return
	 */
	public boolean start(AddonInfo addon) {
		OptionQuery.me().saveOrUpdate(buildOptionKey(addon), Boolean.TRUE.toString());
		return addon.start();
	}

	/**
	 * 停止插件
	 * 
	 * @param addon
	 * @return
	 */
	public boolean stop(AddonInfo addon) {
		OptionQuery.me().saveOrUpdate(buildOptionKey(addon), Boolean.FALSE.toString());
		return addon.stop();
	}

	private String buildOptionKey(AddonInfo addon) {
		return "addon_start_" + addon.getId();
	}

	/**
	 * 安装插件
	 * 
	 * @param file
	 */
	public boolean install(File file) {
		
		AddonInfo addon = null;
		String name = file.getName();
		if (name.toLowerCase().endsWith(".jar")) {
			addon = loadAddonByJarFile(file);
		} else if (name.toLowerCase().endsWith(".zip")) {
			addon = loadAddonByWarFile(file);
		}
		
		if (addon == null) {
			return false;
		}

		return registerAddon(addon);
	}

	/**
	 * 卸载插件
	 * 
	 * @param addon
	 * @return
	 */
	public boolean uninstall(AddonInfo addon) {
		
		stop(addon);

		File addonJarFile = new File(PathKit.getWebRootPath(), addon.getJarPath());
		if (addonJarFile.exists()) {
			unRegisterAddon(addon);
			return addonJarFile.delete();
		}
		return false;
	}

	/**
	 * 所有插件
	 * 
	 * @return
	 */
	public List<AddonInfo> getAddons() {
		List<AddonInfo> lis = new ArrayList<AddonInfo>();
		for (Map.Entry<String, AddonInfo> entry : addonMap.entrySet()) {
			lis.add(entry.getValue());
		}
		return lis;
	}

	/**
	 * 所有已经启动的插件
	 * 
	 * @return
	 */
	public List<AddonInfo> getStartedAddons() {
		List<AddonInfo> list = new ArrayList<AddonInfo>();
		for (Map.Entry<String, AddonInfo> entry : addonMap.entrySet()) {
			if (entry.getValue().isStart()) {
				list.add(entry.getValue());
			}
		}
		return list;
	}

	/**
	 * 注册插件
	 * 
	 * @param addon
	 */
	public boolean registerAddon(AddonInfo addon) {
		if (addonMap.containsKey(addon.getId())) {
			return false;
		}

		addonMap.put(addon.getId(), addon);
		return true;
	}

	/**
	 * 取消注册插件
	 * 
	 * @param addon
	 */
	public void unRegisterAddon(AddonInfo addon) {
		addonMap.remove(addon.getId());
	}

	private void autoInstall() {

		File addonsFile = new File(PathKit.getWebRootPath(), "/WEB-INF/addons");
		if (addonsFile.exists()) {
			File[] files = addonsFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".zip");
				}
			});

			if (files != null && files.length > 0) {
				for (File file : files) {
					install(file);
				}
			}
		}

	}

	private void autoStart() {

		for (Map.Entry<String, AddonInfo> entry : addonMap.entrySet()) {
			Boolean start = OptionQuery.me().findValueAsBool(buildOptionKey(entry.getValue()));
			if (start != null && start == true) {
				start(entry.getValue());
			}
		}

	}

	@SuppressWarnings({ "unchecked" })
	private AddonInfo loadAddonByJarFile(File file) {
		AddonInfo addon = null;
		JarFile jarFile = null;
		AddonClassLoader acl = null;
		try {
			jarFile = new JarFile(file);
			acl = new AddonClassLoader(file.getAbsolutePath());
			acl.init();
			acl.autoLoadClass(jarFile);

			Manifest mf = jarFile.getManifest();
			Attributes attr = mf.getMainAttributes();
			if (attr != null) {

				String id = attr.getValue("Addon-Id");
				if (StringUtils.isBlank(id)) {
					log.warn("addon " + file.getParentFile() + " must has id");
					return null;
				}

				String className = attr.getValue("Addon-Class");
				String title = attr.getValue("Addon-Title");
				String description = attr.getValue("Addon-Description");
				String author = attr.getValue("Addon-Author");
				String authorWebsite = attr.getValue("Addon-Author-Website");
				String version = attr.getValue("Addon-Version");
				String versionCode = attr.getValue("Addon-Version-Code");

				addon = new AddonInfo();
				addon.setId(id);
				addon.setTitle(title);
				addon.setAddonClass(className);
				addon.setDescription(description);
				addon.setAuthor(author);
				addon.setAuthorWebsite(authorWebsite);
				addon.setVersion(version);
				addon.setVersionCode(Integer.parseInt(versionCode.trim()));
				addon.setJarPath(FileUtils.removeRootPath(file.getAbsolutePath()));

				Class<? extends Addon> clazz = (Class<? extends Addon>) acl.loadClass(className);
				addon.setAddon(clazz.newInstance());
				return addon;
			}
		} catch (Throwable e) {
			addon.setHasError(true);
			log.error("AddonManager loadAddon error", e);
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			if (acl != null) {
				try {
					acl.close();
				} catch (IOException e) {
				}
			}
		}
		return addon;
	}
	
	@SuppressWarnings("unchecked")
	private AddonInfo loadAddonByWarFile(File file) {
		
		AddonInfo addon = null;
		JarFile jarFile = null;
		AddonClassLoader acl = null;
		
		try {
			String absolutePath = file.getAbsolutePath();
			
			FileUtils.unzip(absolutePath);
			
			File pfile = new File(absolutePath.substring(0, absolutePath.lastIndexOf(".")));
			if (pfile.exists()) {
				File f = pfile.listFiles()[0];
				
				File[] subDirectorys = f.listFiles();
				for (File directory : subDirectorys) {
					
					if(directory.getName().equals("lib")) {
						File jar = directory.listFiles()[0];
						jarFile = new JarFile(jar);
						acl = new AddonClassLoader(jar.getAbsolutePath());
						acl.init();
						acl.autoLoadClass(jarFile);

						Manifest mf = jarFile.getManifest();
						Attributes attr = mf.getMainAttributes();
						if (attr != null) {

							String id = attr.getValue("Addon-Id");
							if (StringUtils.isBlank(id)) {
								log.warn("addon " + file.getParentFile() + " must has id");
								return null;
							}

							String className = attr.getValue("Addon-Class");
							String title = attr.getValue("Addon-Title");
							String description = attr.getValue("Addon-Description");
							String author = attr.getValue("Addon-Author");
							String authorWebsite = attr.getValue("Addon-Author-Website");
							String version = attr.getValue("Addon-Version");
							String versionCode = attr.getValue("Addon-Version-Code");

							addon = new AddonInfo();
							addon.setId(id);
							addon.setTitle(title);
							addon.setAddonClass(className);
							addon.setDescription(description);
							addon.setAuthor(author);
							addon.setAuthorWebsite(authorWebsite);
							addon.setVersion(version);
							addon.setVersionCode(Integer.parseInt(versionCode.trim()));
							addon.setJarPath(FileUtils.removeRootPath(file.getAbsolutePath()));

							Class<? extends Addon> clazz = (Class<? extends Addon>) acl.loadClass(className);
							addon.setAddon(clazz.newInstance());
							
					} else if (directory.getName().equals("web")) {
						File webSrcFile = directory.listFiles()[0];
						
						String destDirectoryPath = new StringBuilder(PathKit.getWebRootPath()).append("/WEB-INF/admin/").append(webSrcFile.getName()).toString();
						File destFile = new File(destDirectoryPath);
						if (!destFile.exists()) {
							destFile.mkdirs();
						}
						Files.copy(webSrcFile, destFile);
					} else if (directory.getName().equals("bin")) {
						
					}
				}
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error("unzip file error");
		} catch (Throwable e) {
			e.printStackTrace();
			log.error("AddonManager loadAddon error", e);
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			if (acl != null) {
				try {
					acl.close();
				} catch (IOException e) {
				}
			}
		}
		
		return addon;
	}

	public Object invokeHook(String hookName, Object... objects) {
		List<AddonInfo> addons = getStartedAddons();
		for (AddonInfo addonInfo : addons) {
			if (addonInfo.getHasError()) {
				continue;
			}

			Addon addon = addonInfo.getAddon();
			Object obj = addon.getHooks().invokeHook(hookName, objects);
			if (!addon.letNextHookInvoke()) {
				return obj;
			}
		}
		return null;
	}

}
