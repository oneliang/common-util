package com.oneliang.util.logging;

public class BaseLogger extends AbstractLogger {

	/**
	 * constructor
	 * @param level
	 */
	public BaseLogger(Level level) {
		super(level);
	}

	/**
	 * log
	 * @param level
	 * @param message
	 * @param throwable
	 */
	protected void log(Level level, Object message, Throwable throwable){
		System.out.println(message);
		if(throwable!=null){
			throwable.printStackTrace();
		}
	}
}
