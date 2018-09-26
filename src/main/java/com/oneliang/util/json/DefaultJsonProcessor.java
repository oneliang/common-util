package com.oneliang.util.json;

import java.util.Date;

import com.oneliang.Constants;
import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.json.JsonUtil.JsonProcessor;

public class DefaultJsonProcessor implements JsonProcessor {

    /**
     * default json processor
     */
    public <T extends Object> String process(final Class<?> clazz, final String fieldName, final Object value, final boolean ignoreFirstLetterCase) {
        String result = null;
        if (value == null) {
            result = StringUtil.NULL;
        } else {
            Class<?> valueClazz = value.getClass();
            if (valueClazz.isArray()) {
                if (ClassUtil.isBaseArray(valueClazz)) {
                    result = JsonUtil.baseArrayToJson(value);
                } else if (ClassUtil.isSimpleArray(valueClazz)) {
                    result = JsonUtil.simpleArrayToJson((Object[]) value, this);
                } else if (value != null) {
                    result = JsonUtil.objectArrayToJson((Object[]) value, new String[] {}, this, ignoreFirstLetterCase);
                }
            } else if (valueClazz.equals(boolean.class) || valueClazz.equals(short.class) || valueClazz.equals(int.class) || valueClazz.equals(long.class) || valueClazz.equals(float.class) || valueClazz.equals(double.class) || valueClazz.equals(byte.class) || valueClazz.equals(Boolean.class) || valueClazz.equals(Short.class) || valueClazz.equals(Integer.class) || valueClazz.equals(Long.class)
                    || valueClazz.equals(Float.class) || valueClazz.equals(Double.class) || valueClazz.equals(Byte.class)) {
                result = value == null ? StringUtil.NULL : value.toString();
            } else if (valueClazz.equals(String.class) || valueClazz.equals(Character.class)) {
                result = value == null ? StringUtil.NULL : Constants.Symbol.DOUBLE_QUOTES + value.toString() + Constants.Symbol.DOUBLE_QUOTES;
            } else if (valueClazz.equals(Date.class)) {
                result = value == null ? StringUtil.NULL : Constants.Symbol.DOUBLE_QUOTES + TimeUtil.dateToString((Date) value) + Constants.Symbol.DOUBLE_QUOTES;
            } else {
                if (value instanceof Iterable) {
                    result = JsonUtil.iterableToJson((Iterable<?>) value, new String[] {}, this, ignoreFirstLetterCase);
                } else {
                    result = JsonUtil.objectToJson(value, new String[] {}, this, ignoreFirstLetterCase);
                }
            }
        }
        result = result == null ? StringUtil.NULL : result;
        return result;
    }
}
