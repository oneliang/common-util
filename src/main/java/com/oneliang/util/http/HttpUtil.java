package com.oneliang.util.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public final class HttpUtil {

	private static final Logger logger=LoggerManager.getLogger(HttpUtil.class);

	private static final int DEFAULT_TIMEOUT = 20000;

	private HttpUtil() {
	}

	/**
	 * send request by get method,default return encoding utf8
	 * @param httpUrl
	 */
	public static String sendRequestGet(String httpUrl){
		return sendRequestGet(httpUrl, null, Constant.Encoding.UTF8);
	}

	/**
	 * send request by get method
	 * @param httpUrl
	 * @param returnEncoding
	 * @return String
	 */
	public static String sendRequestGet(String httpUrl,String returnEncoding){
		return sendRequestGet(httpUrl, null, returnEncoding);
	}

	/**
	 * send request by get method,most for download
	 * @param httpUrl
	 * @param timeout
	 * @param callback
	 */
	public static void sendRequestGet(String httpUrl, int timeout, Callback callback){
		sendRequestGet(httpUrl, null, timeout, callback);
	}

	/**
	 * send request by get method,default return encoding utf8
	 * @param httpUrl
	 * @param httpHeaderList
	 * @return String
	 */
	public static String sendRequestGet(String httpUrl, List<HttpNameValue> httpHeaderList){
		return sendRequestGet(httpUrl, httpHeaderList, Constant.Encoding.UTF8);
	}

	/**
	 * send request by get method
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param returnEncoding
	 * @return String
	 */
	public static String sendRequestGet(String httpUrl, List<HttpNameValue> httpHeaderList,String returnEncoding){
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		sendRequestGet(httpUrl, httpHeaderList,new Callback(){
			public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength) throws Exception{
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					byteArrayOutputStream.write(buffer, 0, dataLength);
				}
				byteArrayOutputStream.close();
			}
			public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception{
				logger.debug("Response code:"+responseCode);
			}
			public void exceptionCallback(Exception exception) {
				logger.error(Constant.Base.EXCEPTION, exception);
			}
		});
		byte[] byteArray=byteArrayOutputStream.toByteArray();
		String result=null;
		if(byteArray.length>0){
			try {
				result=new String(byteArray, StringUtil.isBlank(returnEncoding)?Constant.Encoding.UTF8:returnEncoding);
			} catch (UnsupportedEncodingException e) {
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
		return result;
	}

	/**
	 * send request by get method,most for download
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param callback
	 */
	public static void sendRequestGet(String httpUrl, List<HttpNameValue> httpHeaderList,Callback callback){
		sendRequestGet(httpUrl, httpHeaderList, DEFAULT_TIMEOUT, callback);
	}

	/**
	 * send request by get method
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param timeout
	 * @param callback
	 */
	public static void sendRequestGet(String httpUrl, List<HttpNameValue> httpHeaderList,int timeout,Callback callback){
		sendRequest(httpUrl, Constant.Http.RequestMethod.GET, httpHeaderList, null, null, null, timeout, null, callback);
	}

	/**
	 * send request by post method
	 * 
	 * @param httpUrl
	 * @return String
	 */
	public static String sendRequestPost(String httpUrl){
		return sendRequestPost(httpUrl, null, null);
	}

	/**
	 * send request by post method
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @return String
	 */
	public static String sendRequestPost(String httpUrl, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList){
		return sendRequestPost(httpUrl, httpHeaderList, httpParameterList, DEFAULT_TIMEOUT);
	}

	/**
	 * send request by post method,default return encoding utf8
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param timeout
	 * @return String
	 */
	public static String sendRequestPost(String httpUrl, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList, int timeout){
		return sendRequestPost(httpUrl, httpHeaderList, httpParameterList, timeout, Constant.Encoding.UTF8);
	}

	/**
	 * send request by post method
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param timeout
	 * @param returnEncoding
	 * @return String
	 */
	public static String sendRequestPost(String httpUrl, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList, int timeout, String returnEncoding){
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		sendRequestPost(httpUrl, httpHeaderList, httpParameterList, timeout, new Callback() {
			public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int length) throws Exception {
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					byteArrayOutputStream.write(buffer, 0, dataLength);
				}
				byteArrayOutputStream.close();
			};
			public void exceptionCallback(Exception exception) {
				logger.error(Constant.Base.EXCEPTION, exception);
			}
			public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception {
				logger.debug("Response code:"+responseCode);
			}
		});
		byte[] byteArray=byteArrayOutputStream.toByteArray();
		String result=null;
		if(byteArray.length>0){
			try {
				result=new String(byteArray, StringUtil.isBlank(returnEncoding)?Constant.Encoding.UTF8:returnEncoding);
			} catch (UnsupportedEncodingException e) {
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
		return result;
	}

	/**
	 * send request by post method,most for download
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param timeout
	 * @param callback
	 */
	public static void sendRequestPost(String httpUrl, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList, int timeout, Callback callback){
		sendRequestPost(httpUrl, httpHeaderList, httpParameterList, null, null, timeout, null, callback);
	}

	/**
	 * send request with bytes by post method,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param byteArray
	 * @param timeout
	 * @return String
	 */
	public static String sendRequestPostWithBytes(String httpUrl, List<HttpNameValue> httpHeaderList, byte[] byteArray, int timeout){
		byte[] tempByteArray=sendRequestPostWithWholeBytes(httpUrl, httpHeaderList, byteArray, timeout);
		String result=null;
		if(tempByteArray.length>0){
			try {
				result=new String(tempByteArray,Constant.Encoding.UTF8);
			} catch (UnsupportedEncodingException e) {
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
		return result;
	}

	/**
	 * send request with whole bytes by post method,most for communication,whole bytes means request and response are bytes
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param byteArray
	 * @param timeout
	 * @return byte[]
	 */
	public static byte[] sendRequestPostWithWholeBytes(String httpUrl, List<HttpNameValue> httpHeaderList, byte[] byteArray, int timeout){
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		sendRequestPostWithBytes(httpUrl, httpHeaderList, byteArray, timeout, new Callback() {
			public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int length) throws Exception {
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					byteArrayOutputStream.write(buffer, 0, dataLength);
				}
				byteArrayOutputStream.close();
			};
			public void exceptionCallback(Exception exception) {
				logger.error(Constant.Base.EXCEPTION, exception);
			}
			public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception {
				logger.debug("Response code:"+responseCode);
			}
		});
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * send request with bytes by post method,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param byteArray
	 * @param timeout
	 * @param callback
	 */
	public static void sendRequestPostWithBytes(String httpUrl, List<HttpNameValue> httpHeaderList, byte[] byteArray, int timeout, Callback callback){
		sendRequestPost(httpUrl, httpHeaderList, null, byteArray, null, timeout, null, callback);
	}

	/**
	 * send request with input stream,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param inputStream
	 * @param timeout
	 * @return String
	 */
	public static String sendRequestWithInputStream(String httpUrl, List<HttpNameValue> httpHeaderList, InputStream inputStream, int timeout){
		String result = sendRequestWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, new InputStreamProcessor() {
			public void process(InputStream inputStream, OutputStream outputStream) throws Exception {
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					outputStream.write(buffer, 0, dataLength);
					outputStream.flush();
				}
			}
		});
		return result;
	}

	/**
	 * send request with input stream,most for upload,return String default return encoding utf8
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param inputStream
	 * @param timeout
	 * @param inputStreamProcessor
	 * @return String
	 */
	public static String sendRequestWithInputStream(String httpUrl, List<HttpNameValue> httpHeaderList, InputStream inputStream, int timeout, InputStreamProcessor inputStreamProcessor){
		return sendRequestWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, inputStreamProcessor, Constant.Encoding.UTF8);
	}

	/**
	 * send request with input stream,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param inputStream
	 * @param timeout
	 * @param inputStreamProcessor
	 * @param returnEncoding
	 * @return String
	 */
	public static String sendRequestWithInputStream(String httpUrl, List<HttpNameValue> httpHeaderList, InputStream inputStream, int timeout, InputStreamProcessor inputStreamProcessor,String returnEncoding){
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		sendRequestWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, inputStreamProcessor, new Callback() {
			public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int length) throws Exception {
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					byteArrayOutputStream.write(buffer, 0, dataLength);
				}
				byteArrayOutputStream.close();
			};
			public void exceptionCallback(Exception exception) {
				logger.error(Constant.Base.EXCEPTION, exception);
			}
			public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception {
				logger.debug("Response code:"+responseCode);
			}
		});
		byte[] byteArray=byteArrayOutputStream.toByteArray();
		String result=null;
		if(byteArray.length>0){
			try {
				result=new String(byteArray, StringUtil.isBlank(returnEncoding)?Constant.Encoding.UTF8:returnEncoding);
			} catch (UnsupportedEncodingException e) {
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
		return result;
	}

	/**
	 * send request with input stream,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param inputStream
	 * @param timeout
	 * @param callback
	 */
	public static void sendRequestWithInputStream(String httpUrl, List<HttpNameValue> httpHeaderList, InputStream inputStream, int timeout, Callback callback){
		sendRequestWithInputStream(httpUrl, httpHeaderList, inputStream, timeout, new InputStreamProcessor() {
			public void process(InputStream inputStream, OutputStream outputStream) throws Exception {
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					outputStream.write(buffer, 0, dataLength);
					outputStream.flush();
				}
			}
		}, callback);
	}

	/**
	 * send request with input stream,most for upload
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param inputStream
	 * @param timeout
	 * @param inputStreamProcessor
	 * @param callback
	 */
	public static void sendRequestWithInputStream(String httpUrl, List<HttpNameValue> httpHeaderList, InputStream inputStream, int timeout, InputStreamProcessor inputStreamProcessor, Callback callback){
		sendRequestPost(httpUrl, httpHeaderList, null, null, inputStream, timeout, inputStreamProcessor, callback);
	}

	/**
	 * send request
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param streamByteArray
	 * @param stream
	 * @param timeout
	 * @param inputStreamProcessor
	 * @param callback
	 */
	private static void sendRequestPost(String httpUrl, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList, byte[] streamByteArray, InputStream stream, int timeout, InputStreamProcessor inputStreamProcessor, Callback callback){
		sendRequest(httpUrl, Constant.Http.RequestMethod.POST, httpHeaderList, httpParameterList, streamByteArray, stream, timeout, inputStreamProcessor, callback);
	}

	/**
	 * send request
	 * 
	 * @param httpUrl
	 * @param httpHeaderList
	 * @param httpParameterList
	 * @param streamByteArray
	 * @param stream
	 * @param timeout
	 * @param inputStreamProcessor
	 * @param callback
	 */
	private static void sendRequest(String httpUrl, String method, List<HttpNameValue> httpHeaderList, List<HttpNameValue> httpParameterList, byte[] streamByteArray, InputStream stream, int timeout, InputStreamProcessor inputStreamProcessor, Callback callback){
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setRequestMethod(method);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setInstanceFollowRedirects(true);
			httpUrlConnection.setConnectTimeout(timeout);
			httpUrlConnection.setReadTimeout(timeout);
			if (httpHeaderList != null && !httpHeaderList.isEmpty()) {
				for (HttpNameValue httpParameter : httpHeaderList) {
					httpUrlConnection.setRequestProperty(httpParameter.getName(), httpParameter.getValue());
				}
			}
			StringBuilder content = new StringBuilder();
			if (httpParameterList != null && !httpParameterList.isEmpty()) {
				int length = httpParameterList.size();
				int index = 0;
				for (HttpNameValue httpParameter : httpParameterList) {
					content.append(httpParameter.getName());
					content.append(Constant.Symbol.EQUAL);
					content.append(URLEncoder.encode(httpParameter.getValue(), Constant.Encoding.UTF8));
					if (index < length - 1) {
						content.append(Constant.Symbol.AND);
					}
					index++;
				}
			}
			httpUrlConnection.connect();
			if(StringUtil.isNotBlank(method)&&method.equalsIgnoreCase(Constant.Http.RequestMethod.POST)){
				OutputStream outputStream = httpUrlConnection.getOutputStream();
				outputStream.write(content.toString().getBytes(Constant.Encoding.UTF8));
				if (streamByteArray != null) {
					outputStream.write(streamByteArray);
					outputStream.flush();
				} else {
					if (inputStreamProcessor != null) {
						inputStreamProcessor.process(stream, outputStream);
					}
				}
				outputStream.close();
			}
			int responseCode = httpUrlConnection.getResponseCode();
			Map<String, List<String>> headerFieldMap = httpUrlConnection.getHeaderFields();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				if (callback != null) {
					int contentLength = httpUrlConnection.getContentLength();
					InputStream inputStream = httpUrlConnection.getInputStream();
					try{
						callback.httpOkCallback(headerFieldMap, inputStream, contentLength);
					}catch (Exception e) {
						callback.exceptionCallback(e);
					}
					inputStream.close();
				}
			} else {
				if (callback != null) {
					try{
						callback.httpNotOkCallback(responseCode, headerFieldMap);
					}catch (Exception e) {
						callback.exceptionCallback(e);
					}
				}
			}
			httpUrlConnection.disconnect();
		} catch (Exception e) {
			if (callback != null) {
				callback.exceptionCallback(e);
			}
		}
	}

	public static abstract interface InputStreamProcessor {
		/**
		 * process
		 * 
		 * @param inputStream
		 * @param outputStream
		 * @throws Exception
		 */
		public abstract void process(InputStream inputStream, OutputStream outputStream) throws Exception;
	}

	public static abstract interface Callback {
		/**
		 * http ok callback only for http status 200
		 * 
		 * @param headerFieldMap
		 * @param inputStream
		 * @param contentLength
		 * @throws Exception
		 */
		public abstract void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength) throws Exception;

		/**
		 * request process throw exception callback like timeout unknownhost and
		 * so on
		 * 
		 * @param exception
		 */
		public abstract void exceptionCallback(Exception exception);

		/**
		 * http not ok callback
		 * @param responseCode
		 * @param headerFieldMap
		 * @throws Exception
		 */
		public abstract void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception;
	}

	public static class HttpNameValue{

		private String name = null;
		private String value = null;

		/**
		 * constructor
		 * 
		 * @param name
		 * @param value
		 */
		public HttpNameValue(String name, String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}
	}

	public static void main(String[] args) throws Exception {
//		Accept	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
//		Accept-Encoding	gzip, deflate
//		Accept-Language	zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3
//		Cache-Control	max-age=0
//		Connection	keep-alive
//		Cookie	BAIDUID=BEC8C0EF2B3544C969CFFCF12AEC163A:FG=1; BDSVRTM=2; H_PS_PSSID=1450_2447_2450_1788_2249_2253
//		Host	www.baidu.com
//		User-Agent	Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20100101 Firefox/12.0
		List<HttpNameValue> httpHeaderList=new ArrayList<HttpNameValue>();
//		httpHeaderList.add(new HttpNameValue("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
//		httpHeaderList.add(new HttpNameValue("Accept-Encoding","gzip, deflate"));
//		httpHeaderList.add(new HttpNameValue("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3"));
//		httpHeaderList.add(new HttpNameValue("Cache-Control","max-age=0"));
//		httpHeaderList.add(new HttpNameValue("Connection","keep-alive"));
//		httpHeaderList.add(new HttpNameValue("Cookie","BAIDUID=BEC8C0EF2B3544C969CFFCF12AEC163A:FG=1; BDSVRTM=2; H_PS_PSSID=1450_2447_2450_1788_2249_2253"));
//		httpHeaderList.add(new HttpNameValue("Host","www.baidu.com"));
//		httpHeaderList.add(new HttpNameValue("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:12.0) Gecko/20100101 Firefox/12.0"));
		final FileOutputStream byteArrayOutputStream = new FileOutputStream(new File("D:\\baidu.html"));
		HttpUtil.sendRequestGet("http://www.baidu.com", httpHeaderList, new Callback(){
			public void httpOkCallback(Map<String, List<String>> headerFieldMap, InputStream inputStream, int contentLength) throws Exception {
				System.out.println(contentLength);
				byte[] buffer = new byte[Constant.Capacity.BYTES_PER_KB];
				int dataLength = -1;
				while ((dataLength = inputStream.read(buffer, 0, buffer.length)) != -1) {
					byteArrayOutputStream.write(buffer, 0, dataLength);
				}
				byteArrayOutputStream.close();
			}
			public void exceptionCallback(Exception exception) {
			}
			public void httpNotOkCallback(int responseCode, Map<String, List<String>> headerFieldMap) throws Exception {
			}
		});
	}
}