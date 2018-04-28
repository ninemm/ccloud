package org.ccloud.log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ccloud.model.SystemLog;

import com.jfinal.kit.LogKit;

public class SystemLogThread {

	private static boolean isRun = true;
	private static int threadNum = 10;
	private static Queue<SystemLog> queue = new ConcurrentLinkedQueue<>();
	
	public static void add(SystemLog systemLog) {
		queue.offer(systemLog);
	}
	
	public static SystemLog getSystemLog() {
		return queue.poll();
	}
	
	public static void setThreadRun(boolean isRun) {
		SystemLogThread.isRun = isRun;
	}
	
	public static void start() {
		for (int i = 0; i < threadNum; i ++) {
			Thread thread = new Thread(new Runnable() {
				
				@SuppressWarnings("unused")
				public void run() {
					
					SystemLog systemLog = null;
					while (isRun) {
						try {
							systemLog = getSystemLog();
							
							if (null == systemLog) {
								Thread.sleep(200);
							} else {
								systemLog.saveOrUpdate();
								systemLog = null;
							}
						} catch (InterruptedException e) {
							if (systemLog != null) {
								systemLog = null;
							}
							LogKit.error("保存操作日志到数据库异常："+ e.getMessage());
							e.printStackTrace();
						}
					}
				}
			});
			
			thread.setName("log-thread-" + (i + 1));
			thread.start();
		}
	}
}
