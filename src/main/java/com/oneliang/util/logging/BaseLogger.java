package com.oneliang.util.logging;

import com.oneliang.Constant;
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
    protected void log(Level level, Object message, Throwable throwable) {
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
    protected String processMessage(Level level, Object message, Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(TimeUtil.dateToString(TimeUtil.getTime(), TimeUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND));
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(level.name());
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(Thread.currentThread().getName());
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_RIGHT);
        stringBuilder.append(StringUtil.SPACE);
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_LEFT);
        stringBuilder.append(message);
        stringBuilder.append(Constant.Symbol.MIDDLE_BRACKET_RIGHT);
        return stringBuilder.toString();
    }

    public void destroy() {
    }
}
