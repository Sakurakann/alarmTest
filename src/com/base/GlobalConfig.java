package com.base;

import org.apache.log4j.Logger;

import com.utils.Configure;
import com.utils.StringUtils;

public class GlobalConfig {
	private static Logger log = Logger.getLogger(GlobalConfig.class);

	private static GlobalConfig intance = new GlobalConfig();

	public static String ALARMTEST_CONFIG = "ALARMTEST_CONFIG";
	public static String BILL_CONFIG = "BILL_CONFIG";
	public static String BILL_IP = "billIp";
	public static String BILL_PORT = "billPort";
	public static String POOL_SIZE = "poolSize";
	public static String NEW_TASKS = "newTasks";
	public static String TASK_WAIT_RESULT = "waitTime"; // 等待告警程序出结果的参数
	public static String BILL_CYCLE = "billCycle";

	public static int DEFAULT_POOL_SIZE = 100; // 默认启动线程数
	public static int DEFAULT_BILL_PORT = 13902;// 默认bill端口
	public static String DEFAULT_BILL_IP = "127.0.0.1";
	public static boolean DEFAULT_NEW_TASKS = true;
	public static long DEFAULT_TASK_WAIT_RESULT = 2 * 60 * 1000;// 默认任务等待时间
	public static long DEFAULT_BILL_CYCLE = 1 * 60 * 1000;// 话单发送间隔

	private Configure config = null;

	private GlobalConfig() {
		init();
	}

	private void init() {
		try {
			config = new Configure("/alarmTest.properties");
		} catch (Exception e) {
			log.error("导入配置文件[alarmTest.properties]出错，请先检查...", e);
		}
	}

	public int getPoolSize() {
		String size = StringUtils.trim(getProperty(POOL_SIZE));
		int psize = -1;
		try {
			psize = Integer.valueOf(size);
		} catch (Exception e) {
			psize = -1;
		}
		if (psize < 5) {
			log.warn("设置的线程池大小不正确或太小，启用默认大小[" + DEFAULT_POOL_SIZE + "]...");
			psize = DEFAULT_POOL_SIZE;
		}
		return psize;
	}

	/**
	 * @return 是否需要重新加载任务，新建搜有配合
	 */
	public boolean reBuildTasks() {
		String flag = StringUtils.trim(getProperty(NEW_TASKS));
		if ("".equals(flag)) {
			return DEFAULT_NEW_TASKS;
		} else {
			return !"N".equalsIgnoreCase(flag);
		}
	}

	/**
	 * @return 任务等待时间
	 */
	public long getTaskWaitTime() {
		String time = StringUtils.trim(getProperty(TASK_WAIT_RESULT));
		long timeL = DEFAULT_TASK_WAIT_RESULT;
		try {
			if (!"".equals(time)) {
				timeL = Long.valueOf(time)*1000;
			}
		} catch (Exception e) {
			timeL = DEFAULT_TASK_WAIT_RESULT;
		}
		return timeL;
	}

	public long getBillSendCycle() {
		String time = StringUtils.trim(getProperty(BILL_CYCLE));
		long timeL = DEFAULT_BILL_CYCLE;
		try {
			timeL = Long.valueOf(time)*1000;
		} catch (Exception e) {
			timeL = DEFAULT_BILL_CYCLE;
		}
		return timeL;
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public static GlobalConfig getIntance() {
		return intance;
	}
	
	public static void main(String[] args) {
		GlobalConfig config = GlobalConfig.getIntance();
		System.out.println(config.getTaskWaitTime());
	}
}
