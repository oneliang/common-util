package com.oneliang.util.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.oneliang.Constants;
import com.oneliang.exception.MethodInvokeException;
import com.oneliang.util.common.ClassUtil.ClassProcessor;

/**
 * @author Dandelion
 * @since 2008-09-26
 */
public final class RequestUtil{

	private RequestUtil(){}

	/**
	 *  <p>
	 * Method: for request use,like the struts's actionForm,can convert the
	 * request.getParameterMap to object
	 * </p>
	 * 
	 * @param <T>
	 * @param map
	 * @param object
	 */
	public static <T extends Object> void requestMapToObject(Map<String,String[]> map,T object){
		requestMapToObject(map,object,ClassUtil.DEFAULT_CLASS_PROCESSOR);
	}
	
	/**
	 *  <p>
	 * Method: for request use,like the struts's actionForm,can convert the
	 * request.getParameterMap to object
	 * </p>
	 * 
	 * @param <T>
	 * @param map
	 * @param object
	 * @param classProcessor
	 */
	public static <T extends Object> void requestMapToObject(Map<String, String[]> map, T object,ClassProcessor classProcessor){
		Method[] methods=object.getClass().getMethods();
		if(map!=null&&!map.isEmpty()){
			for(Method method:methods){
				String methodName=method.getName();
				String fieldName=null;
				if(methodName.startsWith(Constants.Method.PREFIX_SET)){
					fieldName=ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName);
				}
				if(fieldName!=null){
					if(map.containsKey(fieldName)){
						String[] values=map.get(fieldName);
						Class<?>[] classes=method.getParameterTypes();
						if(classes.length==1){
							Object value=ClassUtil.changeType(classes[0],values,fieldName, classProcessor);
							try {
								method.invoke(object,value);
							} catch (Exception e) {
								throw new MethodInvokeException(e);
							}
						}
					}
				}
			}
		}
	}

	/**
	 *  <p>
	 * Method: for request use,like the struts's actionForm,can convert the
	 * request map to object list
	 * </p>
	 * 
	 * @param <T>
	 * @param map
	 * @param clazz
	 * @return List<T>
	 */
	public static <T extends Object> List<T> requestMapToObjectList(Map<String, String[]> map, Class<T> clazz){
		return requestMapToObjectList(map, clazz, ClassUtil.DEFAULT_CLASS_PROCESSOR);
	}

	/**
	 *  <p>
	 * Method: for request use,like the struts's actionForm,can convert the
	 * request map to object list
	 * </p>
	 * 
	 * @param <T>
	 * @param map
	 * @param clazz
	 * @param classProcessor
	 * @return List<T>
	 */
	public static <T extends Object> List<T> requestMapToObjectList(Map<String, String[]> map, Class<T> clazz,ClassProcessor classProcessor){
		Method[] methods=clazz.getMethods();
		List<T> list=null;
		if(map!=null&&!map.isEmpty()){
			list=new ArrayList<T>();
			for(Method method:methods){
				String methodName=method.getName();
				String fieldName=null;
				if(methodName.startsWith(Constants.Method.PREFIX_SET)){
					fieldName=ObjectUtil.methodNameToFieldName(Constants.Method.PREFIX_SET, methodName);
				}
				if(fieldName!=null){
					if(map.containsKey(fieldName)){
						String[] values=map.get(fieldName);
						if(values!=null){
							Class<?>[] classes=method.getParameterTypes();
							if(classes.length==1){
								int i=0;
								for(String parameterValue:values){
									T object=null;
									if(i<list.size()){
										object=list.get(i);
									}else{
										try {
											object=clazz.newInstance();
											list.add(object);
										} catch (Exception e) {
											
										}
									}
									Object value=ClassUtil.changeType(classes[0],new String[]{parameterValue},fieldName,classProcessor);
									try {
										method.invoke(object,value);
									} catch (Exception e) {
										throw new MethodInvokeException(e);
									}
									i++;
								}
							}
						}
					}
				}
			}
		}
		return list;
	}

	/**
	 * parse parameter string
	 * string like this:class=com.lwx.test.loader.Test&method=test(java.lang.String=a,java.lang.Integer=10)"
	 * @param parameterString
	 * @return Map<String,String[]>
	 */
	public static Map<String,String[]> parseParameterString(String parameterString){
		String[] stringArray=parameterString.split(Constants.Symbol.AND);
		Map<String,List<String>> map=new HashMap<String,List<String>>();
		Map<String,String[]> parameterMap=new HashMap<String,String[]>();
		for(String string:stringArray){
			int equalIndex=string.indexOf(Constants.Symbol.EQUAL);
			if(equalIndex>0){
				String key=string.substring(0, equalIndex);
				String value=string.substring(equalIndex+1,string.length());
				if(map.containsKey(key)){
					map.get(key).add(value);
				}else{
					List<String> valueList=new ArrayList<String>();
					valueList.add(value);
					map.put(key, valueList);
				}
			}
		}
		Iterator<Entry<String,List<String>>> iterator=map.entrySet().iterator();
		while(iterator.hasNext()){
		    Entry<String,List<String>> entry=iterator.next();
		    String key=entry.getKey();
			List<String> valueList=entry.getValue();
			parameterMap.put(key, valueList.toArray(new String[valueList.size()]));
		}
		return parameterMap;
	}

	/**
	 * map to parameter string
	 * string like a=1&a=2...
	 * @param map
	 * @return String
	 */
	public static String mapToParameterString(Map<String,String[]> map){
		StringBuilder stringBuilder=new StringBuilder();
		if(map!=null){
			Iterator<Entry<String,String[]>> iterator=map.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,String[]> entry=iterator.next();
				String key=entry.getKey();
				String[] valueArray=entry.getValue();
				if(valueArray!=null){
					for(String value:valueArray){
						stringBuilder.append(key);
						stringBuilder.append(Constants.Symbol.EQUAL);
						stringBuilder.append(value);
						stringBuilder.append(Constants.Symbol.AND);
					}
				}
			}
		}
		if(stringBuilder.length()>0){
			stringBuilder=stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
		}
		return stringBuilder.toString();
	}
}
