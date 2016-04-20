package com.oneliang.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil.FileCopyProcessor;

public class GbkToUtf8FileCopyProcessor implements FileCopyProcessor {

	/**
	 * copyFileToFileProcess
	 * @param from,maybe directory
	 * @param to,maybe directory
	 * @param isFile,maybe directory or file
	 * @return boolean,if true keep going copy,only active in directory so far
	 */
	public boolean copyFileToFileProcess(String fromFile, String toFile, boolean isFile){
		try{
			if(isFile){
				FileUtil.createFile(toFile);
				InputStream inputStream=new FileInputStream(fromFile);
				OutputStream outputStream=new FileOutputStream(toFile);
				BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,Constant.Encoding.GBK));
				BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream,Constant.Encoding.UTF8));
				String string=null;
				while((string=bufferedReader.readLine())!=null){
					bufferedWriter.write(string+StringUtil.CRLF_STRING);
				}
				bufferedReader.close();
				bufferedWriter.flush();
				bufferedWriter.close();
			}else{
				FileUtil.createDirectory(toFile);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
