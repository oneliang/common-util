package com.oneliang.util.logging;

import com.oneliang.Constants;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;

public class BaseLogger extends AbstractLogger {

    /**
     * constructor
     * 
     * @param level
     */
    public BaseLogger(Level level) {
        super(level);
    }

    /**
     * log
     * 
     * @param level
     * @param message
     * @param throwable
     */
    protected void log(Level level, String message, Throwable throwable) {
        System.out.println(processMessage(level, message, throwable));
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    /**
     * process message
     * 
     * @param level
     * @param message
     * @param throwable
     * @return String
     */
    protected String processMessage(Level level, String message, Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(TimeUtil.dateToString(TimeUtil.getTime(), TimeUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND));
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(level.name());
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(Thread.currentThread().getName());
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(message);
        stringBuilder.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
        return stringBuilder.toString();
    }

    public void destroy() {
    }
}
