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
     */
    public abstract void verbose(Object message);

    /**
     * debug
     * 
     * @param message
     */
    public abstract void debug(Object message);

    /**
     * info
     * 
     * @param message
     */
    public abstract void info(Object message);

    /**
     * warning
     * 
     * @param message
     */
    public abstract void warning(Object message);

    /**
     * error
     * 
     * @param message
     */
    public abstract void error(Object message);

    /**
     * error
     * 
     * @param message
     * @param throwable
     */
    public abstract void error(Object message, Throwable throwable);

    /**
     * fatal
     * 
     * @param message
     */
    public abstract void fatal(Object message);

    /**
     * destroy
     */
    public void destroy();
}
