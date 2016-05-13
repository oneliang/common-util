package com.oneliang.util.logging;

public abstract class AbstractLogger implements Logger {

	private Level level = null;

	/**
	 * constructor
	 * @param level
	 */
	public AbstractLogger(Level level) {
		if(level==null){
			throw new NullPointerException("level can not be null.");
		}
		this.level = level;
	}

	/**
	 * verbose
	 * @param message
	 */
	public void verbose(Object message) {
		logByLevel(Level.VERBOSE, message, null);
	}

	/**
	 * debug
	 * @param message
	 */
	public void debug(Object message) {
		logByLevel(Level.DEBUG, message, null);
	}

	/**
	 * info
	 * @param message
	 */
	public void info(Object message) {
		logByLevel(Level.INFO, message, null);
	}

	/**
	 * warning
	 * @param message
	 */
	public void warning(Object message) {
		logByLevel(Level.WARNING, message, null);
	}

	/**
	 * error
	 * @param message
	 * @param throwable
	 */
	public void error(Object message, Throwable throwable) {
		logByLevel(Level.ERROR, message, throwable);
	}

	/**
	 * log by level
	 * @param level
	 * @param message
	 * @param throwable
	 */
	private void logByLevel(Level level, Object message, Throwable throwable){
		if(level.ordinal()>=this.level.ordinal()){
			log(level, message, throwable);
		}
	}

	/**
	 * log
	 * @param level
	 * @param message
	 * @param throwable
	 */
	protected abstract void log(Level level, Object message, Throwable throwable);
}
