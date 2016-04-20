package com.oneliang.util.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.oneliang.util.common.StringUtil;


/**
 * http util
 * @author Dandelion
 * @since 2011-10-23
 */
public final class HttpParseUtil implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2298468246213062743L;

	private static final String HEADER_SEPARATE=": ";
	public static final String CONTENT_LENGTH="Content-Length";
	public static final String TRANSFER_ENCODING="Transfer-Encoding";
	public static final String TRANSFER_ENCODING_VALUE="chunked";
	
	/**
	 * read and write content body
	 * @param inputStream
	 * @param contentLength
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readAndWriteContentBody(InputStream inputStream,int contentLength,OutputStream outputStream) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		int count=0;
		while(count<contentLength){
			int data=inputStream.read();
			byteArrayOutputStream.write(data);
			if(outputStream!=null){
				outputStream.write(data);
			}
			count++;
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * read and write chunked body
	 * @param inputStream
	 * @param outputStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readAndWriteChunkedBody(InputStream inputStream,OutputStream outputStream) throws Exception{
		ByteArrayOutputStream resultByteArrayOutputStream=new ByteArrayOutputStream();
		String line=readAndWriteLine(inputStream,outputStream);
		resultByteArrayOutputStream.write(line.getBytes());
		resultByteArrayOutputStream.write(StringUtil.CRLF);
		int chunkedSize=Integer.parseInt(line, 16);
		while(chunkedSize>0) {
			int count=0;
			while(count<chunkedSize+2){//include CR LF
				int data=inputStream.read();
				resultByteArrayOutputStream.write(data);
				if(outputStream!=null){
					outputStream.write(data);
				}
				count++;
			}
			line=readAndWriteLine(inputStream,outputStream);
			resultByteArrayOutputStream.write(line.getBytes());
			resultByteArrayOutputStream.write(StringUtil.CRLF);
			chunkedSize=Integer.parseInt(line, 16);
		}
		if(chunkedSize==0){
			resultByteArrayOutputStream.write(StringUtil.CRLF);
			if(outputStream!=null){
				outputStream.write(StringUtil.CRLF);
			}
		}
		return resultByteArrayOutputStream.toByteArray();
	}

	/**
	 * read and write header
	 * @param inputStream
	 * @param outputStream can be null mean not write
	 * @return Map<String,String>
	 * @throws Exception
	 */
	public static Map<String,String> readAndWriteHeader(InputStream inputStream,OutputStream outputStream) throws Exception {
		Map<String, String> headerMap=new HashMap<String, String>();
		String line=null;
		while (StringUtil.isNotBlank(line=readAndWriteLine(inputStream,outputStream))){
			String[] stringArray=line.split(HEADER_SEPARATE);
			headerMap.put(stringArray[0], stringArray[1]);
		}
		return headerMap;
	}

	/**
	 * read and write status line
	 * @param inputStream
	 * @param outputStream can be null mean not write
	 * @return String
	 * @throws Exception
	 */
	public static String readAndWriteFirstLine(InputStream inputStream,OutputStream outputStream) throws Exception {
		return readAndWriteLine(inputStream,outputStream);
	}

	/**
	 * read and write line
	 * @param inputStream
	 * @param outputStream can be null mean not write
	 * @return String
	 * @throws Exception
	 */
	private static String readAndWriteLine(InputStream inputStream,OutputStream outputStream) throws Exception {
		int data=-1;
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		while((data=inputStream.read())!=StringUtil.CR) {
			byteArrayOutputStream.write(data);
			if(outputStream!=null){
				outputStream.write(data);
			}
		}
		inputStream.read();
		if(outputStream!=null){
			outputStream.write(StringUtil.CRLF);
		}
		String line=byteArrayOutputStream.toString();
		return line;
	}
}
