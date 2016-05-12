package com.oneliang.util.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author oneliang
 */
public final class LoggerManager {

	private static final Map<Class<?>, Logger> loggerMap=new ConcurrentHashMap<Class<?>, Logger>();

	/**
	 * get logger
	 * @param clazz
	 * @return Logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = loggerMap.get(clazz);
		return logger == null ? new BaseLogger(Logger.Level.ERROR) : logger;
	}

	/**
	 * register logger
	 * @param clazz
	 * @param logger
	 */
	public static void registerLogger(Class<?> clazz, Logger logger){
		loggerMap.put(clazz, logger);
	}

	/**
	 * unregister logger
	 * @param clazz
	 */
	public static void unregisterLogger(Class<?> clazz){
		loggerMap.remove(clazz);
	}
}
