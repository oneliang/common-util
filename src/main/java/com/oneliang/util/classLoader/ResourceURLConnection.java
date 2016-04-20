package com.oneliang.util.classLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class ResourceURLConnection extends URLConnection {
	private ClassLoader classLoader;

	public ResourceURLConnection(URL url, ClassLoader classLoader) {
		super(url);
		this.classLoader = classLoader;
	}

	public void connect() throws IOException {
	}

	public InputStream getInputStream() throws IOException {
		String file = URLDecoder.decode(this.url.getFile(), "UTF-8");
		InputStream result = this.classLoader.getResourceAsStream(file);
		if (result == null) {
			throw new MalformedURLException("Could not open InputStream for URL '" + this.url + "'");
		}
		return result;
	}
}