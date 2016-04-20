package com.oneliang.util.classLoader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class JarResourceLoader {

	private ClassLoader classLoader = null;
	private Method innerMainClassMainMethod = null;

	public JarResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
		try {
			ManifestInfo manifestInfo = getManifestInfo();
			try{
				URL.setURLStreamHandlerFactory(new ResourceURLStreamHandlerFactory(this.classLoader));
			}catch (Throwable e) {
				System.err.println(e.getMessage());
			}
			URL[] rsrcUrls = new URL[manifestInfo.rsrcClassPath.length];
			for (int i = 0; i < manifestInfo.rsrcClassPath.length; i++) {
				String rsrcPath = manifestInfo.rsrcClassPath[i];
				if (rsrcPath.endsWith("/"))
					rsrcUrls[i] = new URL("rsrc:" + rsrcPath);
				else
					rsrcUrls[i] = new URL("jar:rsrc:" + rsrcPath + "!/");
			}
			ClassLoader urlClassLoader = new URLClassLoader(rsrcUrls);
			Class<?> innerMainClass = Class.forName(manifestInfo.rsrcMainClass, true, urlClassLoader);
			innerMainClassMainMethod = innerMainClass.getMethod("main", new Class[] { String[].class });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executeInnerMainClass(String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		// ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if(innerMainClassMainMethod!=null){
			innerMainClassMainMethod.invoke(null, new Object[] { args });
		}
	}

	private ManifestInfo getManifestInfo() throws IOException {
		Enumeration<?> resEnum = classLoader.getResources("META-INF/MANIFEST.MF");
		while (resEnum.hasMoreElements()) {
			try {
				URL url = (URL) resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					ManifestInfo result = new ManifestInfo(null);
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs.getValue("Rsrc-Main-Class");
					String rsrcCP = mainAttribs.getValue("Rsrc-Class-Path");
					if (rsrcCP == null)
						rsrcCP = "";
					result.rsrcClassPath = splitSpaces(rsrcCP);
					if ((result.rsrcMainClass != null) && (!result.rsrcMainClass.trim().equals("")))
						return result;
				}
			} catch (Exception localException) {
			}
		}
		System.err.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Rsrc-Class-Path)");
		return null;
	}

	private static String[] splitSpaces(String line) {
		if (line == null)
			return null;
		List<String> result = new ArrayList<String>();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(' ', firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos) {
				result.add(line.substring(firstPos, lastPos));
			}
			firstPos = lastPos + 1;
		}
		return result.toArray(new String[] {});
	}

	public static void main(String[] args){
		JarResourceLoader jarRsrcLoader=new JarResourceLoader(Thread.currentThread().getContextClassLoader());
		try{
			jarRsrcLoader.executeInnerMainClass(args);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static class ManifestInfo {
		String rsrcMainClass;
		String[] rsrcClassPath;

		private ManifestInfo() {
		}

		ManifestInfo(ManifestInfo paramManifestInfo) {
			this();
		}
	}
}