package com.oneliang.util.common;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.Constant;
import com.oneliang.exception.MethodInvokeException;
import com.oneliang.exception.MethodNotFoundException;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

/**
 * reflect the object property and invoke the method
 * 
 * @author Dandelion
 * @since 2008-04-??
 */
public final class ObjectUtil {

    private static final Logger logger = LoggerManager.getLogger(ObjectUtil.class);

    private ObjectUtil() {
    }

    /**
     * when object is null return blank,when the object is not null it return
     * object;
     * 
     * @param object
     * @return Object
     */
    public static Object nullToBlank(Object object) {
        if (object == null) {
            return StringUtil.BLANK;
        }
        return object;
    }

    /**
     * equal
     * 
     * @param a
     * @param b
     * @return boolean
     */
    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * check not null
     * 
     * @param object
     * @param message
     */
    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * field name to method name
     * 
     * @param methodPrefix
     * @param fieldName
     * @return methodName
     */
    public static String fieldNameToMethodName(String methodPrefix, String fieldName) {
        return fieldNameToMethodName(methodPrefix, fieldName, false);
    }

    /**
     * field name to method name
     * 
     * @param methodPrefix
     * @param fieldName
     * @param ignoreFirstLetterCase
     * @return methodName
     */
    public static String fieldNameToMethodName(String methodPrefix, String fieldName, boolean ignoreFirstLetterCase) {
        String methodName = null;
        if (fieldName != null && fieldName.length() > 0) {
            if (ignoreFirstLetterCase) {
                methodName = methodPrefix + fieldName;
            } else {
                methodName = methodPrefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            }
        } else {
            methodName = methodPrefix;
        }
        return methodName;
    }

    /**
     * method name to field name
     * 
     * @param methodPrefix
     * @param methodName
     * @return fieldName
     */
    public static String methodNameToFieldName(String methodPrefix, String methodName) {
        return methodNameToFieldName(methodPrefix, methodName, false);
    }

    /**
     * method name to field name
     * 
     * @param methodPrefix
     * @param methodName
     * @param ignoreFirstLetterCase
     * @return fieldName
     */
    public static String methodNameToFieldName(String methodPrefix, String methodName, boolean ignoreFirstLetterCase) {
        String fieldName = null;
        if (methodName != null && methodName.length() > methodPrefix.length()) {
            int front = methodPrefix.length();
            if (ignoreFirstLetterCase) {
                fieldName = methodName.substring(front, front + 1) + methodName.substring(front + 1);
            } else {
                fieldName = methodName.substring(front, front + 1).toLowerCase() + methodName.substring(front + 1);
            }
        }
        return fieldName;
    }

    /**
     * get field with method name,which start with method prefix get or is,not
     * include method getClass()
     * 
     * @param methodName
     *            get or is method
     * @return String
     */
    public static String methodNameToFieldName(String methodName) {
        return methodNameToFieldName(methodName, false);
    }

    /**
     * get field with method name,which start with method prefix get or is,not
     * include method getClass()
     * 
     * @param methodName
     *            get or is method
     * @param ignoreFirstLetterCase
     * @return String
     */
    public static String methodNameToFieldName(String methodName, boolean ignoreFirstLetterCase) {
        String fieldName = null;
        if (methodName.startsWith(Constant.Method.PREFIX_GET) && !methodName.equals(Constant.Method.GET_CLASS)) {
            fieldName = ObjectUtil.methodNameToFieldName(Constant.Method.PREFIX_GET, methodName, ignoreFirstLetterCase);
        } else if (methodName.startsWith(Constant.Method.PREFIX_IS)) {
            fieldName = ObjectUtil.methodNameToFieldName(Constant.Method.PREFIX_IS, methodName, ignoreFirstLetterCase);
        }
        return fieldName;
    }

    /**
     * get class all interface list
     * 
     * @param <T>
     * @param clazz
     * @return List<Class<?>>
     */
    private static <T extends Object> List<Class<?>> getClassAllInterfaceList(final Class<T> clazz) {
        return getClassAllSuperclassAndAllInterfaceList(false, false, true, clazz);
    }

    /**
     * get class all superclass and all interface list include self class.
     * 
     * @param <T>
     * @param isIncludeSelfClass
     * @param isAllSuperclass
     * @param isAllInterface
     * @param clazz
     * @return List<Class<?>>
     */
    private static <T extends Object> List<Class<?>> getClassAllSuperclassAndAllInterfaceList(boolean isIncludeSelfClass, boolean isAllSuperclass, boolean isAllInterface, final Class<T> clazz) {
        List<Class<?>> list = new ArrayList<Class<?>>();
        Queue<Class<?>> queue = new ConcurrentLinkedQueue<Class<?>>();
        queue.add(clazz);
        if (isIncludeSelfClass) {
            list.add(clazz);
        }
        while (!queue.isEmpty()) {
            Class<?> currentClass = queue.poll();
            Class<?> superclass = currentClass.getSuperclass();
            if (superclass != null) {
                queue.add(superclass);
                if (isAllSuperclass) {
                    if (!list.contains(superclass)) {
                        list.add(superclass);
                    }
                }
            }
            if (isAllInterface) {
                Class<?>[] interfaces = currentClass.getInterfaces();
                if (interfaces != null && interfaces.length > 0) {
                    for (Class<?> interfaceClass : interfaces) {
                        queue.add(interfaceClass);
                        if (!list.contains(interfaceClass)) {
                            list.add(interfaceClass);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * getClassAllInterfaces
     * 
     * @param <T>
     * @param clazz
     * @return Class<?>[]
     */
    public static <T extends Object> Class<?>[] getClassAllInterfaces(final Class<T> clazz) {
        List<Class<?>> list = getClassAllInterfaceList(clazz);
        return list.toArray(new Class<?>[list.size()]);
    }

    /**
     * is interface implement
     * 
     * @param implement
     * @param interfaceClass
     * @return boolean
     */
    public static boolean isInterfaceImplement(final Class<?> implement, final Class<?> interfaceClass) {
        boolean result = false;
        List<Class<?>> list = getClassAllInterfaceList(implement);
        if (list.contains(interfaceClass)) {
            result = true;
        }
        return result;
    }

    /**
     * just objectClass is it the inheritance or interface implement of clazz
     * 
     * @param objectClass
     * @param clazz
     * @return boolean
     */
    public static boolean isInheritanceOrInterfaceImplement(final Class<?> objectClass, final Class<?> clazz) {
        boolean result = false;
        if (objectClass != null) {
            List<Class<?>> list = getClassAllSuperclassAndAllInterfaceList(true, true, true, objectClass);
            if (list.contains(clazz)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * judge the object is it the entity of class or interface
     * 
     * @param object
     * @param clazz
     * @return boolean
     */
    public static boolean isEntity(final Object object, final Class<?> clazz) {
        return isInheritanceOrInterfaceImplement(object.getClass(), clazz);
    }

    /**
     * invoke getter or is method for field
     * 
     * @param object
     * @param fieldName
     * @return Object
     */
    public static Object getterOrIsMethodInvoke(final Object object, final String fieldName) {
        return getterOrIsMethodInvoke(object, fieldName, false);
    }

    /**
     * invoke getter or is method for field
     * 
     * @param object
     * @param fieldName
     * @param ignoreFirstLetterCase
     * @return Object
     */
    public static Object getterOrIsMethodInvoke(final Object object, final String fieldName, final boolean ignoreFirstLetterCase) {
        Object value = null;
        String methodName = ObjectUtil.fieldNameToMethodName(Constant.Method.PREFIX_GET, fieldName, ignoreFirstLetterCase);
        Method method = null;
        try {
            method = object.getClass().getMethod(methodName, new Class[] {});
        } catch (Exception e) {
            methodName = ObjectUtil.fieldNameToMethodName(Constant.Method.PREFIX_IS, fieldName, ignoreFirstLetterCase);
            try {
                method = object.getClass().getMethod(methodName, new Class[] {});
            } catch (Exception ex) {
                throw new MethodNotFoundException("No getter or is method for field:" + fieldName, ex);
            }
        }
        try {
            value = method.invoke(object, new Object[] {});
        } catch (Exception e) {
            throw new MethodInvokeException(e);
        }
        return value;
    }

    /**
     * read object
     * 
     * @param inputStream
     * @return Object
     */
    public static Object readObject(InputStream inputStream) {
        Object object = null;
        if (inputStream != null) {
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(inputStream);
                object = objectInputStream.readObject();
            } catch (Exception e) {
                logger.warning("Read exception:" + e.getMessage());
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (Exception e) {
                        logger.warning("Read close exception:" + e.getMessage());
                    }
                }
            }
        }
        return object;
    }

    /**
     * write object
     * 
     * @param serializable
     * @param outputStream
     */
    public static void writeObject(Serializable serializable, OutputStream outputStream) {
        if (serializable != null && outputStream != null) {
            ObjectOutputStream objectOutputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(serializable);
                objectOutputStream.flush();
            } catch (Exception e) {
                logger.warning("Write exception:" + e.getMessage());
            } finally {
                if (objectOutputStream != null) {
                    try {
                        objectOutputStream.close();
                    } catch (Exception e) {
                        logger.warning("Write close exception:" + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * new instance
     * 
     * @param clazz
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] parameterValues) {
        if (clazz == null) {
            return null;
        }
        T object = null;
        try {
            object = (T) clazz.getConstructor(parameterTypes).newInstance(parameterValues);
        } catch (Exception e) {
            throw new ObjectUtilException(e);
        }
        return object;
    }

    /**
     * method invoke
     * 
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T methodInvoke(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
        if (object == null) {
            return null;
        }
        return methodInvoke(object.getClass(), object, methodName, parameterTypes, parameterValues);
    }

    /**
     * method invoke
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T methodInvoke(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
        if (clazz == null) {
            return null;
        }
        return methodInvoke(clazz, null, methodName, parameterTypes, parameterValues);
    }

    /**
     * method invoke
     * @param clazz
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param parameterValues
     * @return T
     */
    @SuppressWarnings("unchecked")
    private static <T extends Object> T methodInvoke(Class<?> clazz, Object object, String methodName, Class<?>[] parameterTypes, Object[] parameterValues) {
        if (clazz == null) {
            return null;
        }
        T value = null;
        try {
            value = (T) clazz.getMethod(methodName, parameterTypes).invoke(object, parameterValues);
        } catch (Exception e) {
            throw new ObjectUtilException(e);
        }
        return value;
    }

    public static class ObjectUtilException extends RuntimeException {

        private static final long serialVersionUID = 8556349776219190514L;

        public ObjectUtilException(Throwable cause) {
            super(cause);
        }
    }
}