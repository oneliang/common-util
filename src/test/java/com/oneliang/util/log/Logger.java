package com.oneliang.util.log;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.log.LoggerManager.LoggerMessage;

@Deprecated
public class Logger{

	static final String PATTERN_CLASS_NAME="%c";
	static final String PATTERN_METHOD_NAME="%m";
	static final String PATTERN_LINE_NUMBER="%l";
	static final String PATTERN_FILE_NAME="%f";
	static final String PATTERN_INFOMATION="%i";
	static final String PATTERN_NEW_LINE="%n";
	static final String PATTERN_PRIORITY="%p";
	static final String PATTERN_DATE="%d";
	static final String PATTERN_DATE_REGEX="%d\\{[\\D]*\\}";
	static final String PATTERN_DATE_FIRST_REGEX="%d\\{";

	private DefaultBean defaultBean=null;
	private ErrorBean errorBean=null;
	private LoggerBean loggerBean=null;
	private OutputStream outputStream=null;
	private OutputStream errorOutputStream=null;
	private String outputFile=null;
	private boolean debug=true;

	private static final String CONSOLE_FORMAT_STRING="%s";

	Logger(){}

	/**
	 * get logger by class
	 * @param <T>
	 * @param clazz
	 * @return Logger
	 */
	public static <T extends Object> Logger getLogger(Class<T> clazz){
		return LoggerManager.getLogger(clazz);
	}

	/**
	 * get logger by name
	 * @param name
	 * @return Logger
	 */
	public static Logger getLogger(String name){
		return LoggerManager.getLogger(name);
	}

	/**
	 * log the message
	 * @param message
	 */
	public void log(Object message){
		Throwable throwable=new Throwable();
        StackTraceElement[] stackTraceElementArray=throwable.getStackTrace();
        StackTraceElement stackTraceElement=null;
        if(stackTraceElementArray.length>1){
        	stackTraceElement=stackTraceElementArray[1];
		}
        if(stackTraceElement!=null){
	        if(LoggerManager.ASYNC){
				LoggerMessage loggerMessage=new LoggerMessage(this,LoggerMessage.Type.NORMAL, message, stackTraceElement);
				LoggerManager.addLoggerMessage(loggerMessage);
	        }else{
				logReal(message, stackTraceElement);
	        }
        }
	}

	/**
	 * log the message with Throwable
	 * @param message
	 * @param throwable
	 */
	public void log(Object message,Throwable throwable){
		if(LoggerManager.ASYNC){
			LoggerMessage loggerMessage=new LoggerMessage(this,LoggerMessage.Type.NORMAL, message, throwable);
			LoggerManager.addLoggerMessage(loggerMessage);
		}else{
			logReal(message, throwable);
		}
	}

	/**
	 * log real the message with Throwable
	 * @param message
	 * @param throwable
	 */
	void logReal(Object message,Throwable throwable){
		StackTraceElement[] stackTraceElementArray=throwable.getStackTrace();
		StackTraceElement stackTraceElement=new StackTraceElement(throwable.getClass().getName(),StringUtil.BLANK,StringUtil.nullToBlank(throwable.getMessage()),0);
		if(stackTraceElementArray!=null){
			StackTraceElement[] newStackArray=new StackTraceElement[stackTraceElementArray.length+1];
			newStackArray[0]=stackTraceElement;
			System.arraycopy(stackTraceElementArray, 0, newStackArray, 1, stackTraceElementArray.length);
			for(StackTraceElement stack:stackTraceElementArray){
				logReal(message, stack);
			}
		}
	}

	/**
	 * log the message with one stack
	 * @param message
	 * @param stackTraceElement
	 */
	void logReal(Object message,StackTraceElement stackTraceElement){
		if(stackTraceElement!=null){
			String className=stackTraceElement.getClassName();
			String methodName=stackTraceElement.getMethodName();
			int lineNumber=stackTraceElement.getLineNumber();
			String fileName=stackTraceElement.getFileName();
			Date date=TimeUtil.getTime();
			String pattern=null;
			String priority=null;
//			String outputFile=null;
			String loggerBeanMethodName=null;
			if(this.defaultBean!=null){
				if(this.loggerBean==null){
					pattern=this.defaultBean.getPattern();
					priority=this.defaultBean.getLevel();
//					outputFile=this.defaultBean.getCurrentOutput();
				}else{
					loggerBeanMethodName=this.loggerBean.getMethod();
					LevelBean levelBean=this.loggerBean.getLevel();
					if(levelBean!=null){
						pattern=levelBean.getPattern();
						priority=levelBean.getId();
					}else{
						pattern=this.defaultBean.getPattern();
						priority=this.defaultBean.getLevel();
					}
					this.debug=this.loggerBean.isDebug();
//					OutputBean outputBean=this.loggerBean.getOutput();
//					if(outputBean!=null){
//						outputFile=outputBean.getCurrentFile();
//					}else{
//						outputFile=this.defaultBean.getCurrentOutput();
//					}
				}
			}
			if(message==null){
				message=StringUtil.NULL;
			}
			if(StringUtil.isNotBlank(loggerBeanMethodName)){
				//match method
				if(StringUtil.isMatchPattern(methodName, loggerBeanMethodName)){
					String string=parseString(pattern,date,className,methodName,lineNumber,fileName,message.toString(),priority);
					logString(string,true);
				}
			}else{//all method
				String string=parseString(pattern,date,className,methodName,lineNumber,fileName,message.toString(),priority);
				logString(string,true);
			}
		}
	}

	/**
	 * log the error message
	 * @param message
	 */
	public void error(Object message){
		Throwable throwable=new Throwable();
        StackTraceElement[] stackTraceElementArray=throwable.getStackTrace();
        StackTraceElement stackTraceElement=null;
		if(stackTraceElementArray.length>1){
			stackTraceElement=stackTraceElementArray[1];
		}
		if(stackTraceElement!=null){
			if(LoggerManager.ASYNC){
				LoggerMessage loggerMessage=new LoggerMessage(this,LoggerMessage.Type.ERROR, message, stackTraceElement);
				LoggerManager.addLoggerMessage(loggerMessage);
			}else{
				errorReal(message, stackTraceElement);
			}
		}
	}

	/**
	 * log the error width throwable
	 * @param message
	 * @param throwable
	 */
	public void error(Object message,Throwable throwable){
		if(LoggerManager.ASYNC){
			LoggerMessage loggerMessage=new LoggerMessage(this,LoggerMessage.Type.ERROR, message, throwable);
			LoggerManager.addLoggerMessage(loggerMessage);
		}else{
			errorReal(message, throwable);
		}
	}

	/**
	 * log the error real width throwable
	 * @param message
	 * @param throwable
	 */
	void errorReal(Object message,Throwable throwable){
		while(throwable!=null){
			StackTraceElement[] stackArray=throwable.getStackTrace();
			StackTraceElement stackTraceElement=new StackTraceElement(throwable.getClass().getName(),StringUtil.BLANK,StringUtil.nullToBlank(throwable.getMessage()),0);
			if(stackArray!=null){
				errorReal(message,stackTraceElement);
				for(StackTraceElement stack:stackArray){
					errorReal(message, stack);
				}
			}
			throwable=throwable.getCause();
		}
	}

	/**
	 * log the error width one stack
	 * @param message
	 * @param stackTraceElement
	 */
	void errorReal(Object message,StackTraceElement stackTraceElement){
		if(stackTraceElement!=null){
			String className=stackTraceElement.getClassName();
			String methodName=stackTraceElement.getMethodName();
			int lineNumber=stackTraceElement.getLineNumber();
			String fileName=stackTraceElement.getFileName();
			Date date=TimeUtil.getTime();
			String pattern=null;
			String priority=null;
//			String outputFile=null;
			if(this.errorBean!=null){
				pattern=this.errorBean.getPattern();
				priority=this.errorBean.getLevel();
//				outputFile=this.errorBean.getCurrentOutput();
			}
			if(message==null){
				message=StringUtil.NULL;
			}
			String string=parseString(pattern,date,className,methodName,lineNumber,fileName,message.toString(),priority);
			logString(string,false);
		}
	}

	public void close(){
		if(this.outputStream!=null){
			try {
				this.outputStream.flush();
				this.outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * log the string
	 */
	private void logString(String string,boolean normal){
		boolean needToLog=false;
		if(normal){
			if(LoggerManager.DEBUG){
				if(this.debug){
					needToLog=true;
				}
			}
		}else{
			needToLog=true;
		}
		if(needToLog){
			System.out.print(string);
			if(normal){
				if(this.outputStream!=null){
					try {
						this.outputStream.write(string.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else{
				if(this.errorOutputStream!=null){
					try {
						this.errorOutputStream.write(string.getBytes());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String parseString(String pattern,Date date,String className,String methodName,int lineNumber,String fileName,String message,String priority){
		String string=pattern;
		if(string!=null){
			string=parsePatternDate(date, string);
			string=string.replace(PATTERN_CLASS_NAME, className);
			string=string.replace(PATTERN_METHOD_NAME, methodName);
			string=string.replace(PATTERN_LINE_NUMBER, String.valueOf(lineNumber));
			string=string.replace(PATTERN_FILE_NAME, StringUtil.nullToBlank(fileName));
			string=string.replace(PATTERN_INFOMATION, message);
			string=string.replace(PATTERN_NEW_LINE, StringUtil.CRLF_STRING);
			string=string.replace(PATTERN_PRIORITY, priority);
		}else{
			string=StringUtil.NULL;
		}
		return string;
	}

	/**
	 * parse pattern date,parse %d and %d{yyyy-MM-dd} and so on
	 * @param date
	 * @param string
	 * @return String
	 */
	private static String parsePatternDate(Date date,String string){
		if(string!=null){
			List<String> dateFormatList=StringUtil.parseStringGroup(string,PATTERN_DATE_REGEX,PATTERN_DATE_FIRST_REGEX,StringUtil.BLANK,1);
			if(dateFormatList!=null){
				for(String dateFormat:dateFormatList){
					String currentTime=TimeUtil.dateToString(date, dateFormat);
					string=string.replaceFirst(PATTERN_DATE_REGEX, currentTime);
				}
			}
			string=string.replace(PATTERN_DATE, TimeUtil.dateToString(date));
		}
		return string;
	}

	/**
	 * @return the defaultBean
	 */
	public DefaultBean getDefaultBean() {
		return defaultBean;
	}

	/**
	 * @param defaultBean the defaultBean to set
	 */
	void setDefaultBean(DefaultBean defaultBean) {
		this.defaultBean = defaultBean;
	}

	/**
	 * @return the errorBean
	 */
	public ErrorBean getErrorBean() {
		return errorBean;
	}

	/**
	 * @param errorBean the errorBean to set
	 */
	void setErrorBean(ErrorBean errorBean) {
		this.errorBean = errorBean;
	}

	/**
	 * @return the loggerBean
	 */
	public LoggerBean getLoggerBean() {
		return loggerBean;
	}

	/**
	 * @param loggerBean the loggerBean to set
	 */
	void setLoggerBean(LoggerBean loggerBean) {
		this.loggerBean = loggerBean;
	}

	/**
	 * @return the outputStream
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * @param outputStream the outputStream to set
	 */
	void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	/**
	 * @return the errorOutputStream
	 */
	public OutputStream getErrorOutputStream() {
		return errorOutputStream;
	}

	/**
	 * @param errorOutputStream the errorOutputStream to set
	 */
	void setErrorOutputStream(OutputStream errorOutputStream) {
		this.errorOutputStream = errorOutputStream;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
}
