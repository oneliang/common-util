package com.oneliang.util.logging;

public abstract class AbstractLogger implements Logger {

    private Level level = null;

    /**
     * constructor
     * 
     * @param level
     */
    public AbstractLogger(Level level) {
        if (level == null) {
            throw new NullPointerException("level can not be null.");
        }
        this.level = level;
    }

    /**
     * verbose
     * 
     * @param message
     * @param args
     */
    public void verbose(String message, Object... args) {
        logByLevel(Level.VERBOSE, message, null, args);
    }

    /**
     * debug
     * 
     * @param message
     * @param args
     */
    public void debug(String message, Object... args) {
        logByLevel(Level.DEBUG, message, null, args);
    }

    /**
     * info
     * 
     * @param message
     * @param args
     */
    public void info(String message, Object... args) {
        logByLevel(Level.INFO, message, null, args);
    }

    /**
     * warning
     * 
     * @param message
     * @param args
     */
    public void warning(String message, Object... args) {
        logByLevel(Level.WARNING, message, null, args);
    }

    /**
     * error
     * 
     * @param message
     * @param args
     */
    public void error(String message, Object... args) {
        this.error(message, null, args);
    }

    /**
     * error
     * 
     * @param message
     * @param throwable
     * @param args
     */
    public void error(String message, Throwable throwable, Object... args) {
        logByLevel(Level.ERROR, message, throwable, args);
    }

    /**
     * fatal
     * 
     * @param message
     * @param args
     */
    public void fatal(String message, Object... args) {
        logByLevel(Level.FATAL, message, null, args);
    }

    /**
     * log by level
     * 
     * @param level
     * @param message
     * @param throwable
     * @param args
     */
    private void logByLevel(Level level, String message, Throwable throwable, Object... args) {
        if (level.ordinal() >= this.level.ordinal()) {
            log(level, String.format(message, args), throwable);
        }
    }

    /**
     * log
     * 
     * @param level
     * @param message
     * @param throwable
     */
    protected abstract void log(Level level, String message, Throwable throwable);
}
