package com.oneliang.util.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class ProxyUtil{

	private ProxyUtil(){}

	/**
	 * <p>Method: return the proxy interface of the interfaces of object</p>
	 * @param <T>
	 * @param classLoader can not be null
	 * @param object can not be null
	 * @param handler can not be null
	 * @return proxy interface
	 */
	public static <T extends Object> Object newProxyInstance(ClassLoader classLoader, T object, InvocationHandler handler){
		if (classLoader == null || object == null || handler == null) {
			throw new NullPointerException("classLoader and object and handler all can not be null");
		}
		Class<?>[] interfaces=ObjectUtil.getClassAllInterfaces(object.getClass());
		return Proxy.newProxyInstance(classLoader, interfaces, handler);
	}
}
