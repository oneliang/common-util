package com.oneliang.util.json;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.oneliang.Constants;
import com.oneliang.exception.MethodInvokeException;
import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.ClassUtil.ClassProcessor;

public final class JsonUtil {

    public static final JsonProcessor DEFAULT_JSON_PROCESSOR = new DefaultJsonProcessor();
    private static final ClassProcessor DEFAULT_CLASS_PROCESSOR = ClassUtil.DEFAULT_CLASS_PROCESSOR;

    private JsonUtil() {
    }

    /**
     * <p>
     * Method:basic array to json
     * </p>
     * 
     * @param object
     * @return String
     */
    public static String baseArrayToJson(final Object object) {
        String result = null;
        if (object != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            if (object instanceof Object[]) {
                Object[] fields = (Object[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(Constants.Symbol.DOUBLE_QUOTES);
                    string.append(fields[i].toString());
                    string.append(Constants.Symbol.DOUBLE_QUOTES);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof boolean[]) {
                boolean[] fields = (boolean[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof byte[]) {
                byte[] fields = (byte[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof short[]) {
                short[] fields = (short[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof int[]) {
                int[] fields = (int[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof long[]) {
                long[] fields = (long[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof float[]) {
                float[] fields = (float[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof double[]) {
                double[] fields = (double[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            } else if (object instanceof char[]) {
                char[] fields = (char[]) object;
                for (int i = 0; i < fields.length; i++) {
                    string.append(fields[i]);
                    if (i < fields.length - 1) {
                        string.append(Constants.Symbol.COMMA);
                    }
                }
            }
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            result = string.toString();
        }
        return result;
    }

    /**
     * <p>
     * Method:simple array to json,just only for simple object array not include
     * base type array
     * </p>
     * 
     * @param <T>
     * @param array
     * @return String
     */
    public static <T extends Object> String simpleArrayToJson(final T[] array) {
        return simpleArrayToJson(array, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:simple array to json
     * </p>
     * 
     * @param <T>
     * @param array
     * @param jsonProcessor
     * @return String
     */
    public static <T extends Object> String simpleArrayToJson(final T[] array, JsonProcessor jsonProcessor) {
        String result = null;
        if (array != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            StringBuilder subString = new StringBuilder();
            int length = array.length;
            for (int i = 0; i < length; i++) {
                T object = array[i];
                if (jsonProcessor != null) {
                    subString.append(jsonProcessor.process(null, null, object, false));
                } else {
                    subString.append(object.toString());
                }
                if (i < length - 1) {
                    subString.append(Constants.Symbol.COMMA);
                }
            }
            string.append(subString.toString());
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            result = string.toString();
        }
        return result;
    }

    /**
     * <p>
     * Method:object array to json
     * </p>
     * 
     * @param <T>
     * @param array
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array) {
        return objectArrayToJson(array, new String[] {}, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:object array to json
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fields
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final String[] fields) {
        return objectArrayToJson(array, fields, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:object array to json
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fields
     * @param jsonProcessor
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final String[] fields, final JsonProcessor jsonProcessor) {
        return objectArrayToJson(array, fields, jsonProcessor, false);
    }

    /**
     * <p>
     * Method:object array to json
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fields
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final String[] fields, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String result = null;
        if (array != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            int length = array.length;
            for (int i = 0; i < length; i++) {
                T object = array[i];
                string.append(objectToJson(object, fields, jsonProcessor, ignoreFirstLetterCase));
                if (i < length - 1) {
                    string.append(Constants.Symbol.COMMA);
                }
            }
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            result = string.toString();
        }
        return result;
    }

    /**
     * <p>
     * Method:object array to json array,key means json's properties,value means
     * object field
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fieldMap
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final Map<String, String> fieldMap) {
        return objectArrayToJson(array, fieldMap, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:object array to json array,key means json's properties,value means
     * object field
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fieldMap
     * @param jsonProcessor
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor) {
        return objectArrayToJson(array, fieldMap, jsonProcessor, false);
    }

    /**
     * <p>
     * Method:object array to json array,key means json's properties,value means
     * object field
     * </p>
     * 
     * @param <T>
     * @param array
     * @param fieldMap
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
     */
    public static <T extends Object> String objectArrayToJson(final T[] array, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String result = null;
        if (array != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            int length = array.length;
            for (int i = 0; i < length; i++) {
                T object = array[i];
                string.append(objectToJson(object, fieldMap, jsonProcessor, ignoreFirstLetterCase));
                if (i < length - 1) {
                    string.append(Constants.Symbol.COMMA);
                }
            }
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            result = string.toString();
        }
        return result;
    }

    /**
     * <p>
     * Method: iterable to json
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @return String
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable) {
        return iterableToJson(iterable, new String[] {}, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method: iterable to json
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fields
     * @return String
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final String[] fields) {
        return iterableToJson(iterable, fields, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method: iterable to json
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fields
     * @param jsonProcessor
     * @return String
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final String[] fields, final JsonProcessor jsonProcessor) {
        return iterableToJson(iterable, fields, jsonProcessor, false);
    }

    /**
     * <p>
     * Method: iterable to json
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fields
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return String
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final String[] fields, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String json = null;
        if (iterable != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            Iterator<T> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                T object = iterator.next();
                string.append(objectToJson(object, fields, jsonProcessor, ignoreFirstLetterCase));
                if (iterator.hasNext()) {
                    string.append(Constants.Symbol.COMMA);
                }
            }
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            json = string.toString();
        }
        return json;
    }

    /**
     * <p>
     * Method:iterable to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fieldMap
     * @return json
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final Map<String, String> fieldMap) {
        return iterableToJson(iterable, fieldMap, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:iterable to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fieldMap
     * @param jsonProcessor
     * @return json
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor) {
        return iterableToJson(iterable, fieldMap, jsonProcessor, false);
    }

    /**
     * <p>
     * Method:iterable to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param iterable
     * @param fieldMap
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json
     */
    public static <T extends Object> String iterableToJson(final Iterable<T> iterable, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String json = null;
        if (iterable != null) {
            StringBuilder string = new StringBuilder();
            string.append(Constants.Symbol.MIDDLE_BRACKET_LEFT);
            Iterator<T> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                T object = iterator.next();
                string.append(objectToJson(object, fieldMap, jsonProcessor, ignoreFirstLetterCase));
                if (iterator.hasNext()) {
                    string.append(Constants.Symbol.COMMA);
                }
            }
            string.append(Constants.Symbol.MIDDLE_BRACKET_RIGHT);
            json = string.toString();
        }
        return json;
    }

    /**
     * <p>
     * Method: object to json string
     * </p>
     * 
     * @param object
     * @return json string
     */
    public static <T extends Object> String objectToJson(final T object) {
        return objectToJson(object, new String[] {});
    }

    /**
     * <p>
     * Method: object to json string
     * </p>
     * 
     * @param object
     * @param fields
     * @return json string
     */
    public static <T extends Object> String objectToJson(final T object, final String[] fields) {
        return objectToJson(object, fields, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method: object to json string
     * </p>
     * 
     * @param object
     * @param fields
     * @param jsonProcessor
     * @return json string
     */
    public static <T extends Object> String objectToJson(final T object, final String[] fields, final JsonProcessor jsonProcessor) {
        return objectToJson(object, fields, jsonProcessor, false);
    }

    /**
     * <p>
     * Method: object to json string
     * </p>
     * 
     * @param object
     * @param fields
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json string
     */
    public static <T extends Object> String objectToJson(final T object, final String[] fields, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String json = null;
        if (object != null) {
            StringBuilder objectJson = new StringBuilder();
            Class<?> clazz = object.getClass();
            objectJson.append(Constants.Symbol.BIG_BRACKET_LEFT);
            if (fields != null && fields.length > 0) {
                int length = fields.length;
                for (int i = 0; i < length; i++) {
                    String fieldName = fields[i];
                    Object methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(object, fieldName, ignoreFirstLetterCase);
                    if (jsonProcessor != null) {
                        methodReturnValue = jsonProcessor.process(clazz, fieldName, methodReturnValue, ignoreFirstLetterCase);
                    }
                    objectJson.append(Constants.Symbol.DOUBLE_QUOTES + fieldName + Constants.Symbol.DOUBLE_QUOTES + Constants.Symbol.COLON + (methodReturnValue == null ? StringUtil.NULL : methodReturnValue.toString()));
                    if (i < length - 1) {
                        objectJson.append(Constants.Symbol.COMMA);
                    }
                }
            } else {
                StringBuilder subString = new StringBuilder();
                Method[] methods = object.getClass().getMethods();
                for (Method method : methods) {
                    String methodName = method.getName();
                    String fieldName = ObjectUtil.methodNameToFieldName(methodName, ignoreFirstLetterCase);
                    if (StringUtil.isNotBlank(fieldName)) {
                        Object value = null;
                        try {
                            value = method.invoke(object, new Object[] {});
                        } catch (Exception e) {
                            throw new MethodInvokeException(e);
                        }
                        if (jsonProcessor != null) {
                            value = jsonProcessor.process(clazz, fieldName, value, ignoreFirstLetterCase);
                        }
                        subString.append(Constants.Symbol.DOUBLE_QUOTES + fieldName + Constants.Symbol.DOUBLE_QUOTES + Constants.Symbol.COLON + (value == null ? StringUtil.NULL : value.toString()) + Constants.Symbol.COMMA);
                    }
                }
                if (subString.length() > 0) {
                    subString.delete(subString.length() - 1, subString.length());
                    objectJson.append(subString.toString());
                }
            }
            objectJson.append(Constants.Symbol.BIG_BRACKET_RIGHT);
            json = objectJson.toString();
        }
        return json;
    }

    /**
     * <p>
     * Method:object to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param object
     * @param fieldMap
     * @return json
     */
    public static <T extends Object> String objectToJson(final T object, final Map<String, String> fieldMap) {
        return objectToJson(object, fieldMap, DEFAULT_JSON_PROCESSOR);
    }

    /**
     * <p>
     * Method:object to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param object
     * @param fieldMap
     * @param jsonProcessor
     * @return json
     */
    public static <T extends Object> String objectToJson(final T object, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor) {
        return objectToJson(object, fieldMap, jsonProcessor, false);
    }

    /**
     * <p>
     * Method:object to json with field map,key means json's properties,value
     * means object field
     * </p>
     * 
     * @param <T>
     * @param object
     * @param fieldMap
     * @param jsonProcessor
     * @param ignoreFirstLetterCase
     * @return json
     */
    public static <T extends Object> String objectToJson(final T object, final Map<String, String> fieldMap, final JsonProcessor jsonProcessor, final boolean ignoreFirstLetterCase) {
        String json = null;
        if (object != null) {
            StringBuilder objectJson = new StringBuilder();
            Class<?> clazz = object.getClass();
            Iterator<Entry<String, String>> iterator = fieldMap.entrySet().iterator();
            objectJson.append(Constants.Symbol.BIG_BRACKET_LEFT);
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String fieldName = entry.getValue();
                Object methodReturnValue = ObjectUtil.getterOrIsMethodInvoke(object, fieldName, ignoreFirstLetterCase);
                if (jsonProcessor != null) {
                    methodReturnValue = jsonProcessor.process(clazz, fieldName, methodReturnValue, ignoreFirstLetterCase);
                }
                objectJson.append(key + Constants.Symbol.COLON + (methodReturnValue == null ? StringUtil.NULL : methodReturnValue.toString()));
                if (iterator.hasNext()) {
                    objectJson.append(Constants.Symbol.COMMA);
                }
            }
            objectJson.append(Constants.Symbol.BIG_BRACKET_RIGHT);
            json = objectJson.toString();
        }
        return json;
    }

    /**
     * jsonObject to object
     * 
     * @param jsonObject
     * @param clazz
     * @return T
     */
    public static <T extends Object> T jsonObjectToObject(JsonObject jsonObject, Class<T> clazz) {
        return jsonObjectToObject(jsonObject, clazz, DEFAULT_CLASS_PROCESSOR);
    }

    /**
     * jsonObject to object
     * 
     * @param jsonObject
     * @param clazz
     * @param classProcessor
     * @return T
     */
    public static <T extends Object> T jsonObjectToObject(JsonObject jsonObject, Class<T> clazz, ClassProcessor classProcessor) {
        return jsonObjectToObject(jsonObject, clazz, classProcessor, false);
    }

    /**
     * jsonObject to object
     * 
     * @param jsonObject
     * @param clazz
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @return T
     */
    public static <T extends Object> T jsonObjectToObject(JsonObject jsonObject, Class<T> clazz, ClassProcessor classProcessor, final boolean ignoreFirstLetterCase) {
        T object = null;
        if (jsonObject != null) {
            Method[] methods = clazz.getMethods();
            try {
                object = clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (Method method : methods) {
                String methodName = method.getName();
                String fieldName = null;
                if (methodName.startsWith(Constants.Method.PREFIX_SET)) {
                    fieldName = ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName, ignoreFirstLetterCase);
                }
                if (fieldName != null) {
                    Class<?>[] classes = method.getParameterTypes();
                    Object value = null;
                    if (classes.length == 1) {
                        Class<?> objectClass = classes[0];
                        if (jsonObject.has(fieldName)) {
                            try {
                                if (ClassUtil.isBaseClass(objectClass)) {
                                    if (!jsonObject.isNull(fieldName)) {
                                        value = ClassUtil.changeType(objectClass, new String[] { jsonObject.get(fieldName).toString() }, fieldName, classProcessor);
                                    } else {
                                        value = ClassUtil.changeType(objectClass, null, fieldName, classProcessor);
                                    }
                                } else if (ClassUtil.isBaseArray(objectClass) || ClassUtil.isSimpleArray(objectClass)) {
                                    if (!jsonObject.isNull(fieldName)) {
                                        value = jsonArrayToArray(jsonObject.getJsonArray(fieldName), objectClass, fieldName, classProcessor);
                                    }
                                } else {
                                    if (!jsonObject.isNull(fieldName)) {
                                        value = ClassUtil.changeType(objectClass, new String[] { jsonObject.get(fieldName).toString() }, fieldName, classProcessor);
                                    }
                                }
                            } catch (Exception e) {
                                if (ClassUtil.isBaseClass(objectClass)) {
                                    value = ClassUtil.changeType(objectClass, null, fieldName, classProcessor);
                                } else {
                                    value = null;
                                }
                            }
                            try {
                                method.invoke(object, value);
                            } catch (Exception e) {
                                throw new MethodInvokeException(clazz.getSimpleName() + Constants.Symbol.DOT + fieldName, e);
                            }
                        }
                    }
                }
            }
        }
        return object;
    }

    /**
     * jsonArray to array,just include base array and simple array
     * 
     * @param jsonArray
     * @param clazz
     * @param fieldName
     * @param classProcessor
     * @return Object
     */
    private static Object jsonArrayToArray(JsonArray jsonArray, Class<?> clazz, String fieldName, ClassProcessor classProcessor) {
        Object object = null;
        int length = jsonArray.length();
        String[] values = new String[length];
        for (int i = 0; i < length; i++) {
            values[i] = jsonArray.get(i).toString();
        }
        object = ClassUtil.changeType(clazz, values, fieldName, classProcessor);
        return object;
    }

    /**
     * jsonArray to list
     * 
     * @param <T>
     * @param jsonArray
     * @param clazz
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonArrayToList(JsonArray jsonArray, Class<T> clazz) {
        return jsonArrayToList(jsonArray, clazz, DEFAULT_CLASS_PROCESSOR);
    }

    /**
     * jsonArray to list
     * 
     * @param <T>
     * @param jsonArray
     * @param clazz
     * @param classProcessor
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonArrayToList(JsonArray jsonArray, Class<T> clazz, ClassProcessor classProcessor) {
        return jsonArrayToList(jsonArray, clazz, classProcessor, false);
    }

    /**
     * jsonArray to list
     * 
     * @param <T>
     * @param jsonArray
     * @param clazz
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonArrayToList(JsonArray jsonArray, Class<T> clazz, ClassProcessor classProcessor, final boolean ignoreFirstLetterCase) {
        List<T> list = null;
        int length = jsonArray.length();
        list = new ArrayList<T>(length);
        for (int i = 0; i < length; i++) {
            Object object = jsonArray.get(i);
            if (object instanceof JsonObject) {
                list.add(jsonObjectToObject((JsonObject) object, clazz, classProcessor, ignoreFirstLetterCase));
            }
        }
        return list;
    }

    /**
     * json to object
     * 
     * @param json
     * @param clazz
     * @return T
     */
    public static <T extends Object> T jsonToObject(String json, Class<T> clazz) {
        return jsonToObject(json, clazz, DEFAULT_CLASS_PROCESSOR);
    }

    /**
     * json to object
     * 
     * @param json
     * @param clazz
     * @param classProcessor
     * @return T
     */
    public static <T extends Object> T jsonToObject(String json, Class<T> clazz, ClassProcessor classProcessor) {
        return jsonToObject(json, clazz, classProcessor, false);
    }

    /**
     * json to object
     * 
     * @param json
     * @param clazz
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @return T
     */
    public static <T extends Object> T jsonToObject(String json, Class<T> clazz, ClassProcessor classProcessor, final boolean ignoreFirstLetterCase) {
        T object = null;
        if (json != null) {
            JsonObject jsonObject = new JsonObject(json);
            object = jsonObjectToObject(jsonObject, clazz, classProcessor, ignoreFirstLetterCase);
        }
        return object;
    }

    /**
     * json to object list
     * 
     * @param json
     * @param clazz
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonToObjectList(String json, Class<T> clazz) {
        return jsonToObjectList(json, clazz, DEFAULT_CLASS_PROCESSOR);
    }

    /**
     * json to object list
     * 
     * @param json
     * @param clazz
     * @param classProcessor
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonToObjectList(String json, Class<T> clazz, ClassProcessor classProcessor) {
        return jsonToObjectList(json, clazz, classProcessor, false);
    }

    /**
     * json to object list
     * 
     * @param json
     * @param clazz
     * @param classProcessor
     * @param ignoreFirstLetterCase
     * @return List<T>
     */
    public static <T extends Object> List<T> jsonToObjectList(String json, Class<T> clazz, ClassProcessor classProcessor, final boolean ignoreFirstLetterCase) {
        List<T> list = null;
        if (json != null) {
            JsonArray jsonArray = new JsonArray(json);
            list = jsonArrayToList(jsonArray, clazz, classProcessor, ignoreFirstLetterCase);
        }
        return list;
    }

    public static interface JsonProcessor {

        /**
         * process
         * 
         * @param <T>
         * @param clazz
         * @param fieldName
         * @param value
         * @param ignoreFirstLetterCase
         * @return String
         */
        public abstract <T extends Object> String process(Class<?> clazz, String fieldName, Object value, boolean ignoreFirstLetterCase);
    }
}
