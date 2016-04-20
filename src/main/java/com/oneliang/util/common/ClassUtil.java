package com.oneliang.util.common;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassUtil{

	private static final Map<Class<?>,ClassType> classTypeMap=new ConcurrentHashMap<Class<?>, ClassType>();

	private static final Map<Class<?>,Class<?>> baseClassMap=new ConcurrentHashMap<Class<?>,Class<?>>();
	private static final Map<Class<?>,Class<?>> simpleClassMap=new ConcurrentHashMap<Class<?>,Class<?>>();
	private static final Map<Class<?>,Class<?>> baseArrayMap=new ConcurrentHashMap<Class<?>,Class<?>>();
	private static final Map<Class<?>,Class<?>> simpleArrayMap=new ConcurrentHashMap<Class<?>,Class<?>>();
	private static final Map<String,Class<?>> baseClassNameMap=new ConcurrentHashMap<String,Class<?>>();
	private static final Map<String,Class<?>> simpleClassNameMap=new ConcurrentHashMap<String,Class<?>>();
	
	public static final ClassProcessor DEFAULT_CLASS_PROCESSOR=new DefaultClassProcessor();

	private ClassUtil(){}

	public static enum ClassType {
		JAVA_LANG_STRING,JAVA_LANG_CHARACTER,JAVA_LANG_SHORT,JAVA_LANG_INTEGER,JAVA_LANG_LONG,
		JAVA_LANG_FLOAT,JAVA_LANG_DOUBLE,JAVA_LANG_BOOLEAN,JAVA_LANG_BYTE,JAVA_UTIL_DATE,
		CHAR,BYTE,SHORT,INT,LONG,FLOAT,DOUBLE,BOOLEAN,
		JAVA_LANG_STRING_ARRAY,JAVA_LANG_CHARACTER_ARRAY,JAVA_LANG_SHORT_ARRAY,JAVA_LANG_INTEGER_ARRAY,JAVA_LANG_LONG_ARRAY,
		JAVA_LANG_FLOAT_ARRAY,JAVA_LANG_DOUBLE_ARRAY,JAVA_LANG_BOOLEAN_ARRAY,JAVA_LANG_BYTE_ARRAY,
		CHAR_ARRAY,BYTE_ARRAY,SHORT_ARRAY,INT_ARRAY,LONG_ARRAY,FLOAT_ARRAY,DOUBLE_ARRAY,BOOLEAN_ARRAY
	}
	
	static{
		classTypeMap.put(String.class, ClassType.JAVA_LANG_STRING);
		classTypeMap.put(Character.class, ClassType.JAVA_LANG_CHARACTER);
		classTypeMap.put(Short.class, ClassType.JAVA_LANG_SHORT);
		classTypeMap.put(Integer.class, ClassType.JAVA_LANG_INTEGER);
		classTypeMap.put(Long.class, ClassType.JAVA_LANG_LONG);
		classTypeMap.put(Float.class, ClassType.JAVA_LANG_FLOAT);
		classTypeMap.put(Double.class, ClassType.JAVA_LANG_DOUBLE);
		classTypeMap.put(Boolean.class, ClassType.JAVA_LANG_BOOLEAN);
		classTypeMap.put(Byte.class, ClassType.JAVA_LANG_BYTE);
		classTypeMap.put(Date.class, ClassType.JAVA_UTIL_DATE);
		classTypeMap.put(char.class, ClassType.CHAR);
		classTypeMap.put(byte.class, ClassType.BYTE);
		classTypeMap.put(short.class, ClassType.SHORT);
		classTypeMap.put(int.class, ClassType.INT);
		classTypeMap.put(long.class, ClassType.LONG);
		classTypeMap.put(float.class, ClassType.FLOAT);
		classTypeMap.put(double.class, ClassType.DOUBLE);
		classTypeMap.put(boolean.class, ClassType.BOOLEAN);
		
		classTypeMap.put(String[].class, ClassType.JAVA_LANG_STRING_ARRAY);
		classTypeMap.put(Character[].class, ClassType.JAVA_LANG_CHARACTER_ARRAY);
		classTypeMap.put(Short[].class, ClassType.JAVA_LANG_SHORT_ARRAY);
		classTypeMap.put(Integer[].class, ClassType.JAVA_LANG_INTEGER_ARRAY);
		classTypeMap.put(Long[].class, ClassType.JAVA_LANG_LONG_ARRAY);
		classTypeMap.put(Float[].class, ClassType.JAVA_LANG_FLOAT_ARRAY);
		classTypeMap.put(Double[].class, ClassType.JAVA_LANG_DOUBLE_ARRAY);
		classTypeMap.put(Boolean[].class, ClassType.JAVA_LANG_BOOLEAN_ARRAY);
		classTypeMap.put(Byte[].class, ClassType.JAVA_LANG_BYTE_ARRAY);
		classTypeMap.put(char[].class, ClassType.CHAR_ARRAY);
		classTypeMap.put(byte[].class, ClassType.BYTE_ARRAY);
		classTypeMap.put(short[].class, ClassType.SHORT_ARRAY);
		classTypeMap.put(int[].class, ClassType.INT_ARRAY);
		classTypeMap.put(long[].class, ClassType.LONG_ARRAY);
		classTypeMap.put(float[].class, ClassType.FLOAT_ARRAY);
		classTypeMap.put(double[].class, ClassType.DOUBLE_ARRAY);
		classTypeMap.put(boolean[].class, ClassType.BOOLEAN_ARRAY);
		
		baseClassMap.put(char.class, char.class);
		baseClassMap.put(byte.class, byte.class);
		baseClassMap.put(short.class, short.class);
		baseClassMap.put(int.class, int.class);
		baseClassMap.put(long.class, long.class);
		baseClassMap.put(float.class, float.class);
		baseClassMap.put(double.class, double.class);
		baseClassMap.put(boolean.class, boolean.class);
		
		simpleClassMap.put(String.class, String.class);
		simpleClassMap.put(Character.class, Character.class);
		simpleClassMap.put(Byte.class, Byte.class);
		simpleClassMap.put(Short.class, Short.class);
		simpleClassMap.put(Integer.class, Integer.class);
		simpleClassMap.put(Long.class, Long.class);
		simpleClassMap.put(Float.class, Float.class);
		simpleClassMap.put(Double.class, Double.class);
		simpleClassMap.put(Boolean.class, Boolean.class);
		
		baseArrayMap.put(char[].class, char[].class);
		baseArrayMap.put(byte[].class, byte[].class);
		baseArrayMap.put(short[].class, short[].class);
		baseArrayMap.put(int[].class, int[].class);
		baseArrayMap.put(long[].class, long[].class);
		baseArrayMap.put(float[].class, float[].class);
		baseArrayMap.put(double[].class, double[].class);
		baseArrayMap.put(boolean[].class, boolean[].class);
		
		simpleArrayMap.put(String[].class, String[].class);
		simpleArrayMap.put(Character[].class, Character[].class);
		simpleArrayMap.put(Short[].class, Short[].class);
		simpleArrayMap.put(Integer[].class, Integer[].class);
		simpleArrayMap.put(Long[].class, Long[].class);
		simpleArrayMap.put(Float[].class, Float[].class);
		simpleArrayMap.put(Double[].class, Double[].class);
		simpleArrayMap.put(Boolean[].class, Boolean[].class);
		simpleArrayMap.put(Byte[].class, Byte[].class);

		baseClassNameMap.put(char.class.getName(), char.class);
		baseClassNameMap.put(byte.class.getName(), byte.class);
		baseClassNameMap.put(short.class.getName(), short.class);
		baseClassNameMap.put(int.class.getName(), int.class);
		baseClassNameMap.put(long.class.getName(), long.class);
		baseClassNameMap.put(float.class.getName(), float.class);
		baseClassNameMap.put(double.class.getName(), double.class);
		baseClassNameMap.put(boolean.class.getName(), boolean.class);

		simpleClassNameMap.put(String.class.getName(), String.class);
		simpleClassNameMap.put(Character.class.getName(), Character.class);
		simpleClassNameMap.put(Byte.class.getName(), Byte.class);
		simpleClassNameMap.put(Short.class.getName(), Short.class);
		simpleClassNameMap.put(Integer.class.getName(), Integer.class);
		simpleClassNameMap.put(Long.class.getName(), Long.class);
		simpleClassNameMap.put(Float.class.getName(), Float.class);
		simpleClassNameMap.put(Double.class.getName(), Double.class);
		simpleClassNameMap.put(Boolean.class.getName(), Boolean.class);
	}

	/**
	 * get class with class name
	 * @param className
	 * @return Type
	 * @throws Exception class not found
	 */
	public static Class<?> getClass(final ClassLoader classLoader, final String className) throws Exception{
		Class<?> clazz=null;
		if(classLoader!=null&&className!=null){
			if(baseClassNameMap.containsKey(className)){
				clazz=baseClassNameMap.get(className);
			}else{
				clazz=classLoader.loadClass(className);
			}
		}
		return clazz;
	}

	/**
	 * getClassType,for manual judge use
	 * @param clazz
	 * @return ClassType
	 */
	public static ClassType getClassType(Class<?> clazz){
		return classTypeMap.get(clazz);
	}

	/**
	 * is base class or not
	 * include boolean short int long float double byte char
	 * @param clazz
	 * @return boolean
	 */
	public static boolean isBaseClass(Class<?> clazz){
		boolean result=false;
		if(clazz!=null){
			if(baseClassMap.containsKey(clazz)){
				result=true;
			}
		}
		return result;
	}

	/**
	 * is base class or not
	 * include boolean short int long float double byte char
	 * @param className
	 * @return boolean
	 */
	public static boolean isBaseClass(String className){
		boolean result=false;
		if(className!=null){
			if(baseClassNameMap.containsKey(className)){
				result=true;
			}
		}
		return result;
	}

	/**
	 * simple class or not
	 * include Boolean Short Integer Long Float Double Byte String
	 * @param clazz
	 * @return boolean
	 */
	public static boolean isSimpleClass(Class<?> clazz){
		boolean result=false;
		if(clazz!=null){
			if(simpleClassMap.containsKey(clazz)){
				result=true;
			}
		}
		return result;
	}

	/**
	 * simple class or not
	 * include Boolean Short Integer Long Float Double Byte String
	 * @param className
	 * @return boolean
	 */
	public static boolean isSimpleClass(String className){
		boolean result=false;
		if(className!=null){
			if(simpleClassNameMap.containsKey(className)){
				result=true;
			}
		}
		return result;
	}

	/**
	 * basic array or not
	 * @param clazz
	 * @return boolean
	 */
	public static boolean isBaseArray(Class<?> clazz){
		boolean result=false;
		if(clazz!=null){
			if(baseArrayMap.containsKey(clazz)){
				result=true;
			}
		}
		return result;
	}
	
	/**
	 * simple array or not
	 * @param clazz
	 * @return boolean
	 */
	public static boolean isSimpleArray(Class<?> clazz){
		boolean result=false;
		if(clazz!=null){
			if(simpleArrayMap.containsKey(clazz)){
				result=true;
			}
		}
		return result;
	}
	
	/**
	 * change type
	 * @param <T>
	 * @param clazz
	 * @param values
	 * @return Object
	 */
	public static <T extends Object> T changeType(Class<T> clazz,String[] values){
		return changeType(clazz,values,null,DEFAULT_CLASS_PROCESSOR);
	}
	
	/**
	 * change type width class processor
	 * @param <T>
	 * @param clazz
	 * @param values
	 * @param classProcessor
	 * @return Object
	 */
	public static <T extends Object> T changeType(Class<T> clazz,String[] values,ClassProcessor classProcessor){
		return changeType(clazz, values, null, classProcessor);
	}

	/**
	 * change type
	 * @param <T>
	 * @param clazz
	 * @param values
	 * @param fieldName is null if not exist
	 * @return Object
	 */
	public static <T extends Object> T changeType(Class<T> clazz,String[] values,String fieldName){
		return changeType(clazz,values,fieldName,DEFAULT_CLASS_PROCESSOR);
	}
	
	/**
	 * change type width class processor
	 * @param <T>
	 * @param clazz
	 * @param values
	 * @param fieldName is null if not exist
	 * @param classProcessor
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Object> T changeType(Class<T> clazz,String[] values,String fieldName,ClassProcessor classProcessor){
		Object object=null;
		if(classProcessor!=null){
			object=classProcessor.changeClassProcess(clazz, values,fieldName);
		}else{
			throw new NullPointerException("ClassProcessor can not be null.");
		}
		return (T)object;
	}

	public static interface ClassProcessor{

		/**
		 * change class process
		 * @param clazz
		 * @param values
		 * @param fieldName is null if not exist
		 * @return Object
		 */
		public abstract Object changeClassProcess(Class<?> clazz,String[] values,String fieldName);
	}
}
