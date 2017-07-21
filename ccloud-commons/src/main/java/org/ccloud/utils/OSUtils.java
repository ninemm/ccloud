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

package org.ccloud.utils;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;

@SuppressWarnings("restriction")
public class OSUtils {

	// 系统bean
	private static final OperatingSystemMXBean operatingSystemMXBean;
	private static final List<GarbageCollectorMXBean> list;

	// K转换M
	private static final long K2M = 1024l * 1024l;

	static {
		operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		list = ManagementFactory.getGarbageCollectorMXBeans();
	}
	
	/**
	 * 获取java系统环境变量
	 * 
	 * @param key
	 * @return
	 */
	public static String getOsSystemProperty(String key) {
		return System.getProperty(key);
	}

	/**
	 * 获取本机IP
	 * 
	 * @return
	 */
	public static String getOsLocalHostIp() {
		InetAddress addr;
		String ip = null;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			ip = "未知";
		}
		return ip;
	}

	/**
	 * 获取本机名称
	 * 
	 * @return
	 */
	public static String getOsLocalHostName() {
		InetAddress addr;
		String name = null;
		try {
			addr = InetAddress.getLocalHost();
			name = addr.getHostName();// 获得本机名称
		} catch (UnknownHostException e) {
			name = "未知";
		}
		return name;
	}

	/**
	 * 获取操作系统路径类型
	 * 
	 * @return
	 */
	public static String getOsPathType() {
		String osPathType = System.getProperty("file.separator");
		if (osPathType.equals("\\")) {
			return "\\\\";
		}
		if (osPathType.equals("/")) {
			return "/";
		}
		return null;
	}

	/**
	 * 获取操作系统类型名称
	 * 
	 * @return
	 */
	public static String getOsName() {
		return operatingSystemMXBean.getName();
	}

	/**
	 * 操作系统的体系结构 如:x86
	 * 
	 * @return
	 */
	public static String getOsArch() {
		return operatingSystemMXBean.getArch();
	}

	/**
	 * 获取CPU数量
	 * 
	 * @return
	 */
	public static int getOsCpuNumber() {
		return operatingSystemMXBean.getAvailableProcessors();// Runtime.getRuntime().availableProcessors();// 获取当前电脑CPU数量
	}
	
	/**
	 * CPU使用率
	 * 
	 * @return
	 */
	public static double getOscpuRatio(){
		return operatingSystemMXBean.getSystemCpuLoad();
	}

	/**
	 * 物理内存，总的可使用的，单位：M
	 * 
	 * @return
	 */
	public static long getOsPhysicalMemory() {
		long totalMemorySize = operatingSystemMXBean.getTotalPhysicalMemorySize() / K2M; // M
		return totalMemorySize;
	}

	/**
	 * 物理内存，剩余，单位：M
	 * 
	 * @return
	 */
	public static long getOsPhysicalFreeMemory() {
		long freePhysicalMemorySize = operatingSystemMXBean.getFreePhysicalMemorySize() / K2M; // M
		return freePhysicalMemorySize;
	}

	/**
	 * JVM内存，内存总量，单位：M
	 * 
	 * @return
	 */
	public static long getJvmTotalMemory() {
		return Runtime.getRuntime().totalMemory() / K2M;
	}

	/**
	 * JVM内存，空闲内存量，单位：M
	 * 
	 * @return
	 */
	public static long getJvmFreeMemory() {
		return Runtime.getRuntime().freeMemory() / K2M;
	}

	/**
	 * JVM内存，最大内存量，单位：M
	 * 
	 * @return
	 */
	public static long getJvmMaxMemory() {
		return Runtime.getRuntime().maxMemory() / K2M;
	}

	/**
	 * 获取JVM GC次数
	 * 
	 * @return
	 */
	public static long getJvmGcCount() {
		long count = 0;
		for (final GarbageCollectorMXBean garbageCollectorMXBean : list) {
			count += garbageCollectorMXBean.getCollectionCount();
		}
		return count;
	}

	/**
	 * 系统线程列表
	 * 
	 * @return
	 */
	public static List<Thread> getJvmThreads() {
		int activeCount = Thread.activeCount();
		Thread[] threads = new Thread[activeCount];
		Thread.enumerate(threads);
		return java.util.Arrays.asList(threads);
	}

	public static void main(String[] args) {
		System.out.println(getOsLocalHostIp());
	}
	
}
