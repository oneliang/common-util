package com.oneliang.util.logging;

/**
 * @author oneliang
 */
public abstract interface Logger {

    public enum Level {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL;
    }

    /**
     * verbose
     * 
     * @param message
     * @param args
     */
    public abstract void verbose(String message, Object... args);

    /**
     * debug
     * 
     * @param message
     * @param args
     */
    public abstract void debug(String message, Object... args);

    /**
     * info
     * 
     * @param message
     * @param args
     */
    public abstract void info(String message, Object... args);

    /**
     * warning
     * 
     * @param message
     * @param args
     */
    public abstract void warning(String message, Object... args);

    /**
     * error
     * 
     * @param message
     * @param args
     */
    public abstract void error(String message, Object... args);

    /**
     * error
     * 
     * @param message
     * @param throwable
     * @param args
     */
    public abstract void error(String message, Throwable throwable, Object... args);

    /**
     * fatal
     * 
     * @param message
     * @param args
     */
    public abstract void fatal(String message, Object... args);

    /**
     * destroy
     */
    public void destroy();
}
