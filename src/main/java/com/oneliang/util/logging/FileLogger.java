package com.oneliang.util.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.oneliang.Constant;
import com.oneliang.util.common.StringUtil;

public class FileLogger extends AbstractLogger {

	private FileOutputStream fileOutputStream=null;

	public FileLogger(Level level, File outputFile) {
		super(level);
		if(outputFile==null){
			throw new NullPointerException("outputFile can not be null.");
		}else{
			try {
				this.fileOutputStream=new FileOutputStream(outputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	protected void log(Level level, Object message, Throwable throwable) {
		String messageString=(message==null?StringUtil.NULL:message.toString())+StringUtil.CRLF_STRING;
		try {
			this.fileOutputStream.write(messageString.getBytes(Constant.Encoding.UTF8));
			if(throwable!=null){
				throwable.printStackTrace(new PrintStream(this.fileOutputStream));
			}
			this.fileOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		try{
			if(this.fileOutputStream!=null){
				this.fileOutputStream.flush();
				this.fileOutputStream.close();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
}
