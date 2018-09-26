package com.oneliang.util.log;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oneliang.Constants;
import com.oneliang.exception.InitializeException;
import com.oneliang.util.common.JavaXmlUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.concurrent.ResourceQueueThread;
import com.oneliang.util.concurrent.ResourceQueueThread.ResourceProcessor;
import com.oneliang.util.file.FileUtil;

@Deprecated
public class LoggerManager implements Runnable,ResourceProcessor<LoggerManager.LoggerMessage>{

	private static final long SLEEP_TIME=20000;
	private static final String PARAMETER_DEBUG="-D=";
	private static final String PARAMETER_ASYNC="-A=";
	private static final String PARAMETER_LOG_CHECK="-LC=";
	private static final String PARAMETER_FILE="-F=";

	static boolean DEBUG=true;
	static boolean ASYNC=false;
	static boolean LOG_CHECK=false;

	private static final DefaultBean defaultBean=new DefaultBean();
	private static final ErrorBean errorBean=new ErrorBean();
	protected static final Map<String,OutputBean> outputBeanMap=new ConcurrentHashMap<String,OutputBean>();
	protected static final Map<String,LevelBean> levelBeanMap=new ConcurrentHashMap<String,LevelBean>();
	protected static final Map<String,LoggerBean> loggerBeanMap=new ConcurrentHashMap<String,LoggerBean>();
	private static LevelBean[] levelBeanArray=null;
	protected static final Map<String,OutputStream> outputStreamMap=new ConcurrentHashMap<String,OutputStream>();
	protected static final Map<String,Logger> loggerMap=new ConcurrentHashMap<String,Logger>();
	protected String classesRealPath=null;
	protected String projectRealPath=null;

	protected ClassLoader classLoader=null;

	private static Thread thread=null;
	private static final Calendar currentCalendar=Calendar.getInstance();
	private static ResourceQueueThread<LoggerMessage> loggerMessageQueueThread = null;

	/**
	 * when no configure the log use default configuration
	 */
	static{
		defaultBean.setLevel(DefaultBean.DEFAULT_LEVEL);
		defaultBean.setOutput(DefaultBean.DEFAULT_OUTPUT);
		defaultBean.setPattern(DefaultBean.DEFAULT_PATTERN);
		String currentDay=TimeUtil.getCurrentDay();
		defaultBean.setCurrentOutput(DefaultBean.DEFAULT_OUTPUT.replace(Logger.PATTERN_DATE, currentDay));

		errorBean.setLevel(ErrorBean.ERROR_LEVEL);
		errorBean.setOutput(ErrorBean.ERROR_OUTPUT);
		errorBean.setPattern(ErrorBean.ERROR_PATTERN);
		errorBean.setCurrentOutput(ErrorBean.ERROR_OUTPUT.replace(Logger.PATTERN_DATE, currentDay));
	}

	/**
	 * get logger by class
	 * @param <T>
	 * @param clazz
	 * @return Logger
	 */
	static <T extends Object> Logger getLogger(Class<T> clazz){
		String name=clazz.getName();
		return getLogger(name);
	}

	/**
	 * get logger by name
	 * @param name
	 * @return Logger
	 */
	static Logger getLogger(String name){
		Logger logger=null;
		if(name!=null){
			if(loggerMap.containsKey(name)){
				logger=loggerMap.get(name);
			}else{
				logger=new Logger();
				logger.setDefaultBean(defaultBean);
				logger.setErrorBean(errorBean);
				LoggerBean loggerBean=searchLoggerBeanInClass(name);
				if(loggerBean==null){
					loggerBean=searchLoggerBeanInPackage(name);
				}
				logger.setLoggerBean(loggerBean);
				String outputFile=getNormalOutputFile(loggerBean);
				logger.setOutputFile(outputFile);
				logger.setOutputStream(searchOutputStream(outputFile));
				String errorOutputFile=errorBean.getCurrentOutput();
				logger.setErrorOutputStream(searchOutputStream(errorOutputFile));
				loggerMap.put(name,logger);
			}
		}
		return logger;
	}

	/**
	 * search logger bean,if class name is in target,then return logger configuration
	 * @param className
	 * @return LoggerBean
	 */
	private static LoggerBean searchLoggerBeanInClass(String className){
		LoggerBean loggerBean=null;
		if(StringUtil.isNotBlank(className)){
			Iterator<Entry<String,LoggerBean>> iterator=loggerBeanMap.entrySet().iterator();
			while(iterator.hasNext()){
			    Entry<String,LoggerBean> entry=iterator.next();
				LoggerBean bean=entry.getValue();
				String target=bean.getTarget();
				if(className.equals(target)){
					loggerBean=bean;
					break;
				}
			}
		}
		return loggerBean;
	}
	
	/**
	 * search logger bean,if the parent package of class name contain the class,then return logger configuration
	 * @param className
	 * @return LoggerBean
	 */
	private static LoggerBean searchLoggerBeanInPackage(String className){
		LoggerBean loggerBean=null;
		if(StringUtil.isNotBlank(className)){
			int lastIndex=className.lastIndexOf(Constants.Symbol.DOT);
			if(lastIndex>0){
				String packageName=className.substring(0, lastIndex);
				Iterator<Entry<String,LoggerBean>> iterator=loggerBeanMap.entrySet().iterator();
	            while(iterator.hasNext()){
	                Entry<String,LoggerBean> entry=iterator.next();
	                LoggerBean bean=entry.getValue();
					String target=bean.getTarget();
					if(packageName.equals(target)){
						loggerBean=bean;
						break;
					}
				}
			}
		}
		return loggerBean;
	}

	/**
	 * search output stream
	 * @param outputFile
	 * @return OutputStream
	 */
	private static OutputStream searchOutputStream(String outputFile){
		OutputStream outputStream=null;
		if(outputFile!=null){
			if(outputStreamMap.containsKey(outputFile)){
				outputStream=outputStreamMap.get(outputFile);
			}
		}
		return outputStream;
	}

	/**
	 * get output file
	 * @param loggerBean
	 * @param defaultBean
	 * @return String
	 */
	private static String getNormalOutputFile(LoggerBean loggerBean){
		String outputFile=null;
		if(loggerBean==null){//prove no logger bean
			outputFile=defaultBean.getCurrentOutput();
		}else{
			OutputBean outputBean=loggerBean.getOutput();
			if(outputBean!=null){
				outputFile=loggerBean.getOutput().getCurrentFile();
			}else{
				outputFile=defaultBean.getCurrentOutput();
			}
		}
		return outputFile;
	}

	/**
	 * flush all output stream
	 */
	private void flushAllOutputStream(){
		Iterator<Entry<String,OutputStream>> iterator=outputStreamMap.entrySet().iterator();
		try{
			while(iterator.hasNext()){
			    Entry<String,OutputStream> entry=iterator.next();
				OutputStream outputStream=entry.getValue();
				if(outputStream!=null){
					outputStream.flush();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * close all output stream
	 */
	private void closeAllOutputStream(){
		Iterator<Entry<String,OutputStream>> iterator=outputStreamMap.entrySet().iterator();
		try{
			while(iterator.hasNext()){
			    Entry<String,OutputStream> entry=iterator.next();
				OutputStream outputStream=entry.getValue();
				if(outputStream!=null){
					outputStream.flush();
					outputStream.close();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * initialize
	 */
	public void initialize(final String parameters){
		try{
			String[] parameterArray=parameters.split(Constants.Symbol.COMMA);
			String file=null;
			if(parameterArray!=null){
				for(String parameter:parameterArray){
					if(parameter.startsWith(PARAMETER_DEBUG)){
						DEBUG=Boolean.parseBoolean(parameter.replaceFirst(PARAMETER_DEBUG, StringUtil.BLANK));
					}else if(parameter.startsWith(PARAMETER_ASYNC)){
						ASYNC=Boolean.parseBoolean(parameter.replaceFirst(PARAMETER_ASYNC, StringUtil.BLANK));
					}else if(parameter.startsWith(PARAMETER_LOG_CHECK)){
						LOG_CHECK=Boolean.parseBoolean(parameter.replaceFirst(PARAMETER_LOG_CHECK, StringUtil.BLANK));
					}else if(parameter.startsWith(PARAMETER_FILE)){
						file=parameter.replaceFirst(PARAMETER_FILE, StringUtil.BLANK);
					}
				}
			}
			if(StringUtil.isNotBlank(file)){
				String path=file;
				String tempClassesRealPath=this.classesRealPath;
				if(tempClassesRealPath==null){
					tempClassesRealPath=this.classLoader.getResource(StringUtil.BLANK).getPath();
				}
				path=tempClassesRealPath+file;
				Document document=JavaXmlUtil.parse(path);
				if(document!=null){
					Element root=document.getDocumentElement();
					//default
					NodeList defaultElementList=root.getElementsByTagName(DefaultBean.DEFAULT);
					if(defaultElementList!=null&&defaultElementList.getLength()>0){
						NamedNodeMap defaultBeanAttributeList=defaultElementList.item(0).getAttributes();
						JavaXmlUtil.initializeFromAttributeMap(defaultBean,defaultBeanAttributeList);
					}
		
					//error
					NodeList errorElementList=root.getElementsByTagName(ErrorBean.ERROR);
					if(errorElementList!=null&&errorElementList.getLength()>0){
						NamedNodeMap errorBeanAttributeList=errorElementList.item(0).getAttributes();
						JavaXmlUtil.initializeFromAttributeMap(errorBean,errorBeanAttributeList);
					}
		
					//level
					NodeList levelElementList=root.getElementsByTagName(LevelBean.LEVEL);
					if(levelElementList!=null){
						int length=levelElementList.getLength();
						for(int index=0;index<length;index++){
							LevelBean levelBean=new LevelBean();
							Node levelElement=levelElementList.item(index);
							NamedNodeMap levelElementAttributeMap=levelElement.getAttributes();
							JavaXmlUtil.initializeFromAttributeMap(levelBean,levelElementAttributeMap);
							levelBeanMap.put(levelBean.getId(),levelBean);
						}
					}
		
					//output
					NodeList outputElementList=root.getElementsByTagName(OutputBean.OUTPUT);
					if(outputElementList!=null){
						int length=outputElementList.getLength();
						for(int index=0;index<length;index++){
							Node outputElement=outputElementList.item(index);
							OutputBean outputBean=new OutputBean();
							NamedNodeMap outputBeanAttributeMap=outputElement.getAttributes();
							JavaXmlUtil.initializeFromAttributeMap(outputBean,outputBeanAttributeMap);
							outputBeanMap.put(outputBean.getId(),outputBean);
						}
					}
					//logger
					NodeList loggerElementList=root.getElementsByTagName(LoggerBean.TAG_LOGGER);
					if(loggerElementList!=null){
						int length=loggerElementList.getLength();
						for(int index=0;index<length;index++){
							Node loggerElement=loggerElementList.item(index);
							LoggerBean loggerBean=new LoggerBean();
							NamedNodeMap loggerBeanAttributeMap=loggerElement.getAttributes();
							JavaXmlUtil.initializeFromAttributeMap(loggerBean, loggerBeanAttributeMap);
							NodeList childNodeList=loggerElement.getChildNodes();
							if(childNodeList!=null){
								int childNodeLength=childNodeList.getLength();
								for(int childNodeIndex=0;childNodeIndex<childNodeLength;childNodeIndex++){
									Node propertyElement=childNodeList.item(childNodeIndex);
									LoggerPropertyBean loggerPropertyBean=new LoggerPropertyBean();
									NamedNodeMap propertyAttributeMap=propertyElement.getAttributes();
									JavaXmlUtil.initializeFromAttributeMap(loggerPropertyBean, propertyAttributeMap);
									String propertyName=loggerPropertyBean.getName();
									String propertyReference=loggerPropertyBean.getReference();
									if(propertyName!=null){
										if(propertyName.equals(LevelBean.LEVEL)){
											if(levelBeanMap.containsKey(propertyReference)){
												loggerBean.setLevel(levelBeanMap.get(propertyReference));
											}
										}else if(propertyName.equals(OutputBean.OUTPUT)){
											if(outputBeanMap.containsKey(propertyReference)){
												loggerBean.setOutput(outputBeanMap.get(propertyReference));
											}
										}
									}
								}
							}
							loggerBeanMap.put(loggerBean.getId(),loggerBean);
						}
					}
				}
				
				//initial log config
				this.sortLevel();
				List<String> outputFileList=this.initialAllOutputFile();
				this.initialAllOutputStream(outputFileList);
			}
	
			if(thread==null&&LOG_CHECK){
				thread=new Thread(this);
				thread.start();
			}
			if(loggerMessageQueueThread==null&&ASYNC){
				loggerMessageQueueThread=new ResourceQueueThread<LoggerMessage>(this);
				loggerMessageQueueThread.start();
			}
		}catch (Exception e) {
			throw new InitializeException(parameters,e);
		}
	}

	/**
	 * destroy
	 */
	public void destroy(){
		outputBeanMap.clear();
		levelBeanMap.clear();
		loggerBeanMap.clear();
		levelBeanArray=null;
		loggerMap.clear();
		closeAllOutputStream();
		outputStreamMap.clear();
		if(thread!=null){
			thread.interrupt();
			thread=null;
		}
		if(loggerMessageQueueThread!=null){
			loggerMessageQueueThread.interrupt();
			loggerMessageQueueThread=null;
		}
	}

	/**
	 * initial all out put stream
	 * @param outputFileList
	 * @throws Exception
	 */
	private void initialAllOutputStream(List<String> outputFileList) throws Exception{
		for(String outputFile:outputFileList){
			OutputStream outputStream=new FileOutputStream(outputFile,true);
			outputStreamMap.put(outputFile, outputStream);
		}
	}

	/**
	 * initial all out put file,return out put path list
	 * @return List<String>
	 * @throws Exception
	 */
	private List<String> initialAllOutputFile() throws Exception{
		List<String> list=new ArrayList<String>();
		String currentDay=TimeUtil.getCurrentDay();
		Date currentDate=TimeUtil.stringToDate(currentDay,TimeUtil.YEAR_MONTH_DAY);
		currentCalendar.setTime(currentDate);
		if(defaultBean!=null){
			String defaultOutputFile=defaultBean.getOutput();
			if(StringUtil.isNotBlank(defaultOutputFile)){
				String tempDefaultOutputFile=defaultOutputFile;
				tempDefaultOutputFile=projectRealPath+defaultOutputFile;
				tempDefaultOutputFile=tempDefaultOutputFile.replace(Logger.PATTERN_DATE, currentDay);
				defaultBean.setCurrentOutput(tempDefaultOutputFile);
				FileUtil.createFile(tempDefaultOutputFile);
				list.add(tempDefaultOutputFile);
			}
		}
		if(errorBean!=null){
			String errorOutputFile=errorBean.getOutput();
			if(StringUtil.isNotBlank(errorOutputFile)){
				String tempErrorOutputFile=errorOutputFile;
				tempErrorOutputFile=this.projectRealPath+errorOutputFile;
				tempErrorOutputFile=tempErrorOutputFile.replace(Logger.PATTERN_DATE, currentDay);
				errorBean.setCurrentOutput(tempErrorOutputFile);
				FileUtil.createFile(tempErrorOutputFile);
				list.add(tempErrorOutputFile);
			}
		}
		Iterator<Entry<String,OutputBean>> iterator=outputBeanMap.entrySet().iterator();
		while(iterator.hasNext()){
		    Entry<String,OutputBean> entry=iterator.next();
			OutputBean output=entry.getValue();
			String outputFile=output.getFile();
			if(StringUtil.isNotBlank(outputFile)){
				String tempOutputFile=outputFile;
				tempOutputFile=this.projectRealPath+outputFile;
				tempOutputFile=tempOutputFile.replace(Logger.PATTERN_DATE, currentDay);
				output.setCurrentFile(tempOutputFile);
				FileUtil.createFile(tempOutputFile);
				list.add(tempOutputFile);
			}
		}
		return list;
	}

	/**
	 * sort the level
	 */
	private void sortLevel() throws Exception{
		LevelBean[] bean=new LevelBean[levelBeanMap.size()];
		Iterator<Entry<String,LevelBean>> iterator=levelBeanMap.entrySet().iterator();
		int position=0;
		while(iterator.hasNext()){
		    Entry<String,LevelBean> entry=iterator.next();
			bean[position]=entry.getValue();
			position++;
		}
		if(bean.length>0){
			LevelBean temp=null;
			for(int i=0;i<bean.length;i++){
				for(int j=0;j<bean.length-1;j++){
					if(bean[j].getPriority()>bean[j+1].getPriority()){
						temp=bean[j];
						bean[j]=bean[j+1];
						bean[j+1]=temp;
					}
				}
			}
		}
		levelBeanArray=bean;
	}

	public void process(LoggerMessage loggerMessage) {
		Logger logger=loggerMessage.logger;
		Object message=loggerMessage.message;
		Throwable throwable=loggerMessage.throwable;
		StackTraceElement stackTraceElement=loggerMessage.stackTraceElement;
		switch(loggerMessage.type){
		case ERROR:
			if(throwable!=null){
				logger.errorReal(message, throwable);
			}else{
				logger.errorReal(message, stackTraceElement);
			}
			break;
		case NORMAL:
		default:
			if(throwable!=null){
				logger.logReal(message,throwable);
			}else{
				logger.logReal(message, stackTraceElement);
			}
			break;
		}
//		loggerMessage.logger.log(message);
	}

	/**
	 * detect the date
	 */
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try{
				if(currentCalendar!=null){
					Calendar calendar=Calendar.getInstance();
					String currentDay=TimeUtil.getCurrentDay();
					Date currentDate=TimeUtil.stringToDate(currentDay,TimeUtil.YEAR_MONTH_DAY);
					calendar.setTime(currentDate);
					if(currentCalendar.compareTo(calendar)<0){
						closeAllOutputStream();
						outputStreamMap.clear();
						List<String> outputFileList=this.initialAllOutputFile();
						this.initialAllOutputStream(outputFileList);
						Iterator<Entry<String,Logger>> entrySet=loggerMap.entrySet().iterator();
						while(entrySet.hasNext()){
							Entry<String,Logger> entry=entrySet.next();
							Logger logger=entry.getValue();
							LoggerBean loggerBean=logger.getLoggerBean();
							String outputFile=getNormalOutputFile(loggerBean);
							logger.setOutputFile(outputFile);
							logger.setOutputStream(searchOutputStream(outputFile));
							String errorOutputFile=errorBean.getCurrentOutput();
							logger.setErrorOutputStream(searchOutputStream(errorOutputFile));
						}
					}else{
						flushAllOutputStream();
					}
				}
				Thread.sleep(SLEEP_TIME);
			}catch (InterruptedException e) {
//				System.err.println("Need to interrupt:" + e.getMessage());
				Thread.currentThread().interrupt();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the defaultBean
	 */
	public DefaultBean getDefaultBean() {
		return defaultBean;
	}

	/**
	 * @return the errorBean
	 */
	public ErrorBean getErrorBean() {
		return errorBean;
	}

	/**
	 * @return the levelArray
	 */
	public LevelBean[] getLevelBeanArray() {
		return levelBeanArray;
	}

	/**
	 * @return the outputMap
	 */
	public Map<String, OutputBean> getOutputBeanMap() {
		return outputBeanMap;
	}

	/**
	 * @return the levelMap
	 */
	public Map<String, LevelBean> getLevelBeanMap() {
		return levelBeanMap;
	}

	/**
	 * @return the loggerMap
	 */
	public Map<String, LoggerBean> getLoggerBeanMap() {
		return loggerBeanMap;
	}

	/**
	 * @param classesRealPath the classesRealPath to set
	 */
	public void setClassesRealPath(String classesRealPath) {
		this.classesRealPath = classesRealPath;
	}

	/**
	 * @param projectRealPath the projectRealPath to set
	 */
	public void setProjectRealPath(String projectRealPath) {
		this.projectRealPath = projectRealPath;
	}

	/**
	 * @param classLoader the classLoader to set
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
  
	/**
	 * add log content
	 * @param loggerMessage
	 */
	static void addLoggerMessage(LoggerMessage loggerMessage){
		if(loggerMessageQueueThread!=null){
			loggerMessageQueueThread.addResource(loggerMessage);
		}
	}

	static class LoggerMessage {
		enum Type {
			NORMAL, ERROR
		}
		private Type type = Type.NORMAL;
		private Object message = null;
		private Logger logger = null;
		private StackTraceElement stackTraceElement = null;
		private Throwable throwable = null;
		LoggerMessage(Logger logger, Type type, Object message, StackTraceElement stackTraceElement) {
			this(logger, type, message, (Throwable)null);
			this.stackTraceElement=stackTraceElement;
		}
		
		LoggerMessage(Logger logger, Type type, Object message, Throwable throwable) {
			this.logger = logger;
			this.type = type;
			this.message = message;
			this.throwable = throwable;
		}
	}
}
