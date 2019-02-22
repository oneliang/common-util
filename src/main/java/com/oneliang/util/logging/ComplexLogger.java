package com.oneliang.util.logging;

import java.util.List;

public class ComplexLogger extends AbstractLogger {

    private List<AbstractLogger> loggerList = null;

    /**
     * constructor
     * 
     * @param level
     * @param loggerList
     */
    public ComplexLogger(Level level, List<AbstractLogger> loggerList) {
        super(level);
        this.loggerList = loggerList;
    }

    /**
     * log
     */
    protected void log(Level level, String message, Throwable throwable) {
        if (this.loggerList != null) {
            for (AbstractLogger logger : this.loggerList) {
                logger.log(level, message, throwable);
            }
        }
    }

    public void destroy() {
        if (this.loggerList != null) {
            for (Logger logger : this.loggerList) {
                logger.destroy();
            }
        }
    }
}
