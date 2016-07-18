package com.oneliang.util.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.oneliang.util.http.HttpUtil.Callback;
import com.oneliang.util.http.HttpUtil.HttpNameValue;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class HttpDownloader{

	private static final Logger logger=LoggerManager.getLogger(HttpDownloader.class);

	private static final DownloadListener DEFAULT_DOWNLOAD_LISTENER = new DefaultDownloadListener();

	/**
	 * download
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param timeout
	 * @param saveFile
	 *            full file
	 * @param downloadListener
	 */
	public void download(final String httpUrl, final List<HttpNameValue> httpHeaderList, final List<HttpNameValue> httpParameterList, final int timeout, final String saveFile, DownloadListener downloadListener) {
		if (downloadListener == null) {
			downloadListener = DEFAULT_DOWNLOAD_LISTENER;
		}
		final DownloadListener listener = downloadListener;
		try {
			listener.onStart();
			HttpUtil.sendRequestPost(httpUrl, httpHeaderList, httpParameterList, timeout, new Callback() {
				public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength) throws Exception {
					listener.onProcess(headerFieldMap, inputStream, contentLength, saveFile);
					listener.onFinish();
				}

				public void exceptionCallback(Exception exception) {
					listener.onFailure(exception);
				}

				public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception {
					logger.debug("Response code:"+responseCode);
				}
			});
		} catch (Exception e) {
			listener.onFailure(e);
		}
	}

	public static interface DownloadListener {
		public abstract void onStart();

		public abstract void onProcess(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength, String saveFile);

		public abstract void onFinish();

		public abstract void onFailure(Exception exception);
	}
}
