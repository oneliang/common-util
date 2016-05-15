package com.oneliang.util.logging;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import com.oneliang.util.common.StringUtil;

/**
 * @author oneliang
 */
public final class LoggerManager {

	private static final Map<Class<?>, Logger> loggerMap=new ConcurrentHashMap<Class<?>, Logger>();
	private static final Map<String, Logger> loggerPatternMap=new ConcurrentHashMap<String, Logger>();
	private static final Set<String> loggerPatternSet=new ConcurrentSkipListSet<String>();

	private LoggerManager(){}

	/**
	 * get logger
	 * @param clazz
	 * @return Logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		if(clazz==null){
			throw new NullPointerException("parameter[Class<?> class] can no be null");
		}
		Logger logger = loggerMap.get(clazz);
		if(logger==null){
			String className=clazz.getName();
			for(String patternKey:loggerPatternSet){
				if(StringUtil.isMatchPattern(className, patternKey)){
					logger=loggerPatternMap.get(patternKey);
					break;
				}
			}
		}
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
	 * register logger
	 * @param pattern
	 * @param logger
	 */
	public static void registerLogger(String pattern, Logger logger){
		loggerPatternMap.put(pattern, logger);
		loggerPatternSet.add(pattern);
	}

	/**
	 * unregister logger
	 * @param clazz
	 */
	public static void unregisterLogger(Class<?> clazz){
		loggerMap.remove(clazz);
	}

	/**
	 * unregister logger
	 * @param pattern
	 */
	public static void unregisterLogger(String pattern){
		loggerPatternMap.remove(pattern);
		loggerPatternSet.remove(pattern);
	}
}
