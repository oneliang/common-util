package com.oneliang.util.task;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.exception.MethodNotSupportedException;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.concurrent.ThreadPool;
import com.oneliang.util.concurrent.ThreadPool.Processor;
import com.oneliang.util.concurrent.ThreadTask;
import com.oneliang.util.file.FileUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TaskEngine {

	private static final Logger logger=LoggerManager.getLogger(TaskNodeThreadTask.class);

	public static enum Mode{
		DEFAULT,SERVER
	}

	private boolean successful=true;
	private ThreadPool threadPool=null;
	private String taskNodeTimeFile=null;
	private Properties taskNodeTimeProperties=null;//default mode
	private boolean autoSort=false;
	private Mode mode=null;
	private Map<String,TaskNode> allTaskNodeMap=new ConcurrentHashMap<String, TaskNode>();//default mode
	private ThreadLocal<Map<String,TaskNode>> taskNodeMapThreadLocal=new ThreadLocal<Map<String,TaskNode>>(){
		protected Map<String, TaskNode> initialValue() {
			return new HashMap<String, TaskNode>();
		}
	};
	private Processor processor=new Processor(){
		public void beforeRunTaskProcess(Queue<ThreadTask> threadTaskQueue) {
			ThreadTask threadTask=threadTaskQueue.peek();
//			ConcurrentLinkedQueue<ThreadTask> queue=(ConcurrentLinkedQueue<ThreadTask>)threadTaskQueue;
			if(threadTask instanceof TaskNode){
				TaskNode taskNode=(TaskNode)threadTask;
			}
		};
	};

	/**
	 * constructor
	 * @param minThreads
	 * @param maxThreads
	 */
	public TaskEngine(int minThreads,int maxThreads){
		this(Mode.DEFAULT, minThreads, maxThreads);
	}

	/**
	 * constructor
	 * @param mode
	 * @param minThreads
	 * @param maxThreads
	 */
	public TaskEngine(Mode mode,int minThreads,int maxThreads) {
		this.mode=mode;
		this.threadPool=new ThreadPool();
		this.threadPool.setMinThreads(minThreads);
		this.threadPool.setMaxThreads(maxThreads);
		this.threadPool.setProcessor(this.processor);
	}

	/**
	 * prepare
	 * @param rootTaskNodeList
	 */
	public void prepare(List<TaskNode> rootTaskNodeList){
		this.prepare(rootTaskNodeList, null);
	}

	/**
	 * prepare
	 * @param rootTaskNodeList
	 * @param excludeTaskNodeNameList
	 */
	public void prepare(List<TaskNode> rootTaskNodeList, List<String> excludeTaskNodeNameList){
		this.travelAllTaskNode(rootTaskNodeList, excludeTaskNodeNameList);
	}

	/**
	 * commit to thread pool
	 * @param taskNodeMap
	 */
	public void commit(){
		Map<String,TaskNode> taskNodeMap=this.taskNodeMapThreadLocal.get();
		Map<Integer,List<TaskNode>> taskNodeDepthMap=new HashMap<Integer, List<TaskNode>>();
		Iterator<Entry<String,TaskNode>> iterator=taskNodeMap.entrySet().iterator();
		List<Integer> depthList=new ArrayList<Integer>();
		while(iterator.hasNext()){
			Entry<String,TaskNode> entry=iterator.next();
			TaskNode taskNode=entry.getValue();
			int depth=calcuateTaskNodeDepth(taskNode);
			List<TaskNode> taskNodeList=null;
			if(taskNodeDepthMap.containsKey(depth)){
				taskNodeList=taskNodeDepthMap.get(depth);
			}else{
				taskNodeList=new ArrayList<TaskNode>();
				taskNodeDepthMap.put(depth, taskNodeList);
				depthList.add(depth);
			}
			taskNodeList.add(taskNode);
		}
		Integer[] depthArray=depthList.toArray(new Integer[]{});
		Arrays.sort(depthArray);
		Map<String,TaskNode> hasAddThreadTaskMap=new HashMap<String,TaskNode>();
		for(Integer depth:depthArray){
			List<TaskNode> taskNodeList=taskNodeDepthMap.get(depth);
			if(autoSort){
				taskNodeList=sort(taskNodeList);
			}
			for(TaskNode taskNode:taskNodeList){
				taskNode.setDepth(depth);
				logger.info("task node depth:"+depth+",name:"+taskNode.getName()+(this.taskNodeTimeProperties!=null?(",cost time:"+this.taskNodeTimeProperties.getProperty(taskNode.getName())):StringUtil.BLANK));
				if(!hasAddThreadTaskMap.containsKey(taskNode.getName())){
					MultiTaskNodeThreadTask multiTaskNodeThreadTask=null;
					List<TaskNode> singleChildTaskNodeList=this.findSingleChildTaskNodeList(taskNode);
					if(singleChildTaskNodeList!=null&&!singleChildTaskNodeList.isEmpty()){
						multiTaskNodeThreadTask=new MultiTaskNodeThreadTask();
						multiTaskNodeThreadTask.addTaskNodeThreadTask(new TaskNodeThreadTask(this,taskNode));
						hasAddThreadTaskMap.put(taskNode.getName(), taskNode);
						for(TaskNode singleChildTaskNode:singleChildTaskNodeList){
							multiTaskNodeThreadTask.addTaskNodeThreadTask(new TaskNodeThreadTask(this,singleChildTaskNode));
							hasAddThreadTaskMap.put(singleChildTaskNode.getName(), singleChildTaskNode);
						}
						this.threadPool.addThreadTask(multiTaskNodeThreadTask);
					}
					if(multiTaskNodeThreadTask==null){
						hasAddThreadTaskMap.put(taskNode.getName(), taskNode);
						this.threadPool.addThreadTask(new TaskNodeThreadTask(this,taskNode));
					}
				}
			}
		}
	}

	/**
	 * find single child task node list
	 * @param rootTaskNode
	 * @return List<TaskNode>
	 */
	private List<TaskNode> findSingleChildTaskNodeList(TaskNode rootTaskNode){
		List<TaskNode> singleChildTaskNodeList=new ArrayList<TaskNode>();
		Queue<TaskNode> queue=new ConcurrentLinkedQueue<TaskNode>();
		queue.add(rootTaskNode);
		while(!queue.isEmpty()){
			TaskNode taskNode=queue.poll();
			if(!taskNode.getChildTaskNodeList().isEmpty()&&taskNode.getChildTaskNodeList().size()==1){
				TaskNode childTaskNode=taskNode.getChildTaskNodeList().get(0);
				if(!childTaskNode.getParentTaskNodeList().isEmpty()&&childTaskNode.getParentTaskNodeList().size()==1){
					singleChildTaskNodeList.add(childTaskNode);
					queue.add(childTaskNode);
				}
			}
		}
		return singleChildTaskNodeList;
	}

	/**
	 * execute
	 */
	public void execute(){
		if(this.isDefaultMode()){
			if(!this.allTaskNodeMap.isEmpty()){
				this.threadPool.start();
			}
		}else{
			this.threadPool.start();
		}
	}

	/**
	 * waiting
	 */
	public void waiting(){
		if(this.isDefaultMode()){
			if(!this.allTaskNodeMap.isEmpty()){
				synchronized(this){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			throw new MethodNotSupportedException("TaskEngine.waiting() is invalid for server mode.");
		}
	}

	/**
	 * clean,clean all list and map cache,include thread pool interrupt
	 */
	public void clean(){
		this.saveTaskNodeTime();
		this.threadPool.interrupt();
	}
	/**
	 * travel all task node
	 * @param rootTaskNodeList
	 * @param excludeTaskNodeNameList
	 * @return Map<String,TaskNode>
	 */
	private Map<String,TaskNode> travelAllTaskNode(List<TaskNode> rootTaskNodeList,List<String> excludeTaskNodeNameList){
        List<TaskNode> newRootTaskNodeList = new ArrayList<TaskNode>();
		Map<String,TaskNode> taskNodeMap=new HashMap<String, TaskNode>();
		Map<String,String> excludeTaskNodeNameMap=new HashMap<String,String>();
		if(excludeTaskNodeNameList!=null){
			for(String excludeTaskNodeName:excludeTaskNodeNameList){
				excludeTaskNodeNameMap.put(excludeTaskNodeName,excludeTaskNodeName);
			}
		}
		//bug fix,first check root task
        for (TaskNode rootTaskNode : rootTaskNodeList) {
            if(!excludeTaskNodeNameMap.containsKey(rootTaskNode.getName())){
                newRootTaskNodeList.add(rootTaskNode);
                continue;
            }
            List<TaskNode> childChildTaskNodeList=rootTaskNode.getChildTaskNodeList();
            for(TaskNode childChildTaskNode:childChildTaskNodeList){
                childChildTaskNode.removeParentTaskNode(rootTaskNode);
                newRootTaskNodeList.add(childChildTaskNode);
            }
        }
		
		Queue<TaskNode> queue=new ConcurrentLinkedQueue<TaskNode>();
		queue.addAll(newRootTaskNodeList);
		Map<String,String> hasAddQueueMap=new HashMap<String, String>();
		while(!queue.isEmpty()){
			TaskNode taskNode=queue.poll();
			String taskNodeName=taskNode.getName();
			if(!hasAddQueueMap.containsKey(taskNodeName)){
				hasAddQueueMap.put(taskNodeName,taskNodeName);
			}
			if(!taskNodeMap.containsKey(taskNodeName)&&!excludeTaskNodeNameMap.containsKey(taskNodeName)){
				taskNodeMap.put(taskNodeName, taskNode);
				this.taskNodeMapThreadLocal.get().put(taskNodeName, taskNode);
				if(isDefaultMode()){
					this.allTaskNodeMap.put(taskNodeName, taskNode);
				}
			}
			List<TaskNode> childTaskNodeList=taskNode.getChildTaskNodeList();
			if(childTaskNodeList!=null&&!childTaskNodeList.isEmpty()){
				boolean result=false;
				for(TaskNode childTaskNode:childTaskNodeList){
					if(excludeTaskNodeNameMap.containsKey(childTaskNode.getName())){
						List<TaskNode> childParentTaskNodeList=childTaskNode.getParentTaskNodeList();
						List<TaskNode> childParentTempTaskNodeList=new ArrayList<TaskNode>();
						//first delete the parent child relation,and keep the parent reference for last iterator
						for(TaskNode childParentTaskNode:childParentTaskNodeList){
							childParentTaskNode.removeChildTaskNode(childTaskNode);
							childParentTempTaskNodeList.add(childParentTaskNode);
						}
						//second delete the child child relation,and keep the child child reference for last iterator
						List<TaskNode> childChildTaskNodeList=childTaskNode.getChildTaskNodeList();
						List<TaskNode> childChildTempTaskNodeList=new ArrayList<TaskNode>();
						for(TaskNode childChildTaskNode:childChildTaskNodeList){
							childChildTaskNode.removeParentTaskNode(childTaskNode);
							childChildTempTaskNodeList.add(childChildTaskNode);
						}
						//then add the child child to parent
						for(TaskNode childParentTaskNode:childParentTempTaskNodeList){
							for(TaskNode childChildTaskNode:childChildTempTaskNodeList){
								childParentTaskNode.addChildTaskNode(childChildTaskNode);
							}
						}
						result=true;
					}
				}
				if(result){
					queue.add(taskNode);//recheck this task node
				}
			}
			for(TaskNode childTaskNode:childTaskNodeList){
				String childTaskNodeName=childTaskNode.getName();
				if(!taskNodeMap.containsKey(childTaskNodeName)&&!excludeTaskNodeNameMap.containsKey(childTaskNodeName)){
					taskNodeMap.put(childTaskNodeName, childTaskNode);
					this.taskNodeMapThreadLocal.get().put(childTaskNodeName, childTaskNode);
					if(isDefaultMode()){
						this.allTaskNodeMap.put(childTaskNodeName, childTaskNode);
					}
				}
				if(!hasAddQueueMap.containsKey(childTaskNodeName)){
					hasAddQueueMap.put(childTaskNodeName,childTaskNodeName);
					queue.add(childTaskNode);
				}
			}
		}
		return taskNodeMap;
	}

	private List<TaskNode> sort(List<TaskNode> taskNodeList){
		TaskNode[] taskNodeArray=taskNodeList.toArray(new TaskNode[0]);
		for(int i=0;i<taskNodeArray.length;i++){
			int currentCostTime=Integer.parseInt(this.taskNodeTimeProperties.getProperty(taskNodeArray[i].getName(),String.valueOf(0)));
			int maxCost=currentCostTime;
			for(int j=i;j<taskNodeArray.length;j++){
				int nextCostTime=Integer.parseInt(this.taskNodeTimeProperties.getProperty(taskNodeArray[j].getName(),String.valueOf(0)));
				if(nextCostTime>maxCost){
					TaskNode temp=taskNodeArray[i];
					taskNodeArray[i]=taskNodeArray[j];
					taskNodeArray[j]=temp;
					maxCost=nextCostTime;
				}
			}
		}
		return Arrays.asList(taskNodeArray);
	}

	/**
	 * calcuate task node depth
	 * @param taskNode
	 * @return int
	 */
	private int calcuateTaskNodeDepth(TaskNode taskNode){
		List<String> depthList=new ArrayList<String>();
		Queue<TaskNode> parentTaskNodeQueue=new ConcurrentLinkedQueue<TaskNode>();
		parentTaskNodeQueue.add(taskNode);
		while(!parentTaskNodeQueue.isEmpty()){
			TaskNode parentTaskNode=parentTaskNodeQueue.poll();
			if(!depthList.contains(parentTaskNode.getName())){
				depthList.add(parentTaskNode.getName());
			}
			for(TaskNode parentParent:parentTaskNode.getParentTaskNodeList()){
				if(!depthList.contains(parentParent.getName())){
					parentTaskNodeQueue.add(parentParent);
				}
			}
		}
		return depthList.size();
	}

	/**
	 * is all task node finished
	 * @return boolean
	 */
	boolean isAllTaskNodeFinished(){
		boolean result=true;
		if(this.isDefaultMode()){
			Iterator<Entry<String,TaskNode>> iterator=this.allTaskNodeMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,TaskNode> entry=iterator.next();
				if(!entry.getValue().isFinished()){
					result=false;
				}
			}
		}else{
			throw new RuntimeException("TaskEngine.isAllTaskNodeFinished() is invalid for server mode.");
		}
		return result;
	}

	/**
	 * save task node time
	 */
	private void saveTaskNodeTime(){
		if(this.taskNodeTimeProperties!=null){
			if(!FileUtil.isExist(this.taskNodeTimeFile)){
				FileUtil.createFile(this.taskNodeTimeFile);
			}
			Iterator<Entry<String,TaskNode>> iterator=this.allTaskNodeMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String,TaskNode> entry=iterator.next();
				TaskNode taskNode=entry.getValue();
				this.taskNodeTimeProperties.setProperty(taskNode.getName(), String.valueOf(taskNode.getRunCostTime()));
			}
			try{
				this.taskNodeTimeProperties.store(new FileOutputStream(this.taskNodeTimeFile), null);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * is default mode
	 * @return boolean
	 */
	public boolean isDefaultMode(){
		return this.mode==Mode.DEFAULT;
	}

	/**
	 * is server mode
	 * @return boolean
	 */
	public boolean isServerMode(){
		return this.mode==Mode.SERVER;
	}

	/**
	 * @return the successful
	 */
	public boolean isSuccessful() {
		return successful;
	}

	/**
	 * @param successful the successful to set
	 */
	void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	/**
	 * @param taskNodeTimeFile the taskNodeTimeFile to set
	 */
	public void setTaskNodeTimeFile(String taskNodeTimeFile) {
		this.taskNodeTimeFile = taskNodeTimeFile;
		if(StringUtil.isNotBlank(this.taskNodeTimeFile)){
			try {
				this.taskNodeTimeProperties=FileUtil.getPropertiesAutoCreate(this.taskNodeTimeFile);
				if(!this.taskNodeTimeProperties.isEmpty()){
					this.autoSort=true;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
