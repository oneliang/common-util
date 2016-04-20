package com.oneliang.util.jar;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * JarClassLoader
 */
public class JarClassLoader extends URLClassLoader{
	/**
	 * constructor
	 * @param parentClassLoader
	 */
	public JarClassLoader(ClassLoader parentClassLoader) {
		super(new URL[]{}, parentClassLoader);
	}

	/**
	 * add url,make the protected method to public method
	 */
	public void addURL(URL url) {
		super.addURL(url);
	}
}
