package com.oneliang.util.jar;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.oneliang.Constant;
import com.oneliang.exception.FileLoadException;
import com.oneliang.util.common.StringUtil;

public final class JarUtil {

	/**
	 * extract from jar file
	 * @param jarFileRealPath
	 * @return List<JarEntry>
	 * @throws FileLoadException
	 */
	public static List<JarEntry> extractFromJarFile(String jarFileRealPath) throws FileLoadException{
		List<JarEntry> jarEntryList=new ArrayList<JarEntry>();
		JarInputStream jarInputStream=null;
		try {
			jarInputStream=new JarInputStream(new FileInputStream(jarFileRealPath));
			JarEntry jarEntry=jarInputStream.getNextJarEntry();
			while(jarEntry!=null){
				jarEntryList.add(jarEntry);
				jarEntry=jarInputStream.getNextJarEntry();
			}
		} catch (Exception e) {
			throw new FileLoadException(e);
		} finally {
			if(jarInputStream!=null){
				try {
					jarInputStream.close();
				} catch (IOException e) {
					throw new FileLoadException(e);
				}
			}
		}
		return jarEntryList;
	}

	/**
	 * search class list
	 * @param jarClassLoader
	 * @param jarFileRealPath
	 * @param searchPackageName
	 * @param annotationClass
	 * @return List<Class<?>>
	 * @throws FileLoadException
	 */
	public static List<Class<?>> searchClassList(final JarClassLoader jarClassLoader,final String jarFileRealPath,final String searchPackageName,final Class<? extends Annotation> annotationClass) throws FileLoadException{
		List<Class<?>> classList=new ArrayList<Class<?>>();
		List<Class<?>> allClassList=extractClassFromJarFile(jarClassLoader,jarFileRealPath,searchPackageName);
		if(allClassList!=null){
			for(Class<?> clazz:allClassList){
				if(clazz.isAnnotationPresent(annotationClass)){
					classList.add(clazz);
				}
			}
		}
		return classList;
	}

	/**
	 * extract class from jar file
	 * @param jarClassLoader
	 * @param jarFileRealPath
	 * @return List<Class<?>>
	 * @throws FileLoadException
	 */
	public static List<Class<?>> extractClassFromJarFile(final JarClassLoader jarClassLoader,final String jarFileRealPath) throws FileLoadException{
		return extractClassFromJarFile(jarClassLoader, jarFileRealPath, null);
	}

	/**
	 * extract class from jar file
	 * @param jarClassLoader
	 * @param jarFileRealPath
	 * @param packagePath
	 * @return List<Class<?>>
	 * @throws FileLoadException
	 */
	public static List<Class<?>> extractClassFromJarFile(final JarClassLoader jarClassLoader,final String jarFileRealPath,final String packageName) throws FileLoadException{
		List<Class<?>> classList=new ArrayList<Class<?>>();
		if(jarClassLoader!=null){
			JarInputStream jarInputStream=null;
			try {
				jarClassLoader.addURL(new URL(Constant.Protocol.FILE+jarFileRealPath));
				jarInputStream=new JarInputStream(new FileInputStream(jarFileRealPath));
				JarEntry jarEntry=jarInputStream.getNextJarEntry();
				while(jarEntry!=null){
					String entryName=jarEntry.getName();
					if (entryName.endsWith(Constant.Symbol.DOT+Constant.File.CLASS)) {
						entryName=entryName.substring(0, entryName.length()-(Constant.Symbol.DOT+Constant.File.CLASS).length());
						String className=entryName.replace(Constant.Symbol.SLASH_LEFT, Constant.Symbol.DOT);
						boolean sign=false;
						if(StringUtil.isBlank(packageName)){
							sign=true;
						}else{
							if(className.startsWith(packageName)){
								sign=true;
							}
						}
						if(sign){
							Class<?> clazz=jarClassLoader.loadClass(className);
							classList.add(clazz);
						}
					}
					jarEntry=jarInputStream.getNextJarEntry();
				}
			} catch (Exception e) {
				throw new FileLoadException(e);
			} finally {
				if(jarInputStream!=null){
					try {
						jarInputStream.close();
					} catch (IOException e) {
						throw new FileLoadException(e);
					}
				}
			}
		}
		return classList;
	}
}