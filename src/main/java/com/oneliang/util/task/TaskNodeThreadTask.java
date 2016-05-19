package com.oneliang.util.task;

import com.oneliang.Constant;
import com.oneliang.util.concurrent.ThreadTask;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class TaskNodeThreadTask implements ThreadTask {

	private static final Logger logger=LoggerManager.getLogger(TaskNodeThreadTask.class);

	private TaskEngine taskEngine=null;
	private TaskNode taskNode=null;
	public TaskNodeThreadTask(TaskEngine taskEngine,TaskNode taskNode) {
		this.taskEngine=taskEngine;
		this.taskNode=taskNode;
	}
	public void runTask() {
		long begin=System.currentTimeMillis();
		logger.info(taskNode.getName()+" ready");
		while(!taskNode.isAllParentFinished()){
			synchronized(taskNode){
				try {
					taskNode.wait();
				} catch (InterruptedException e) {
					logger.error(Constant.Base.EXCEPTION, e);
				}
			}
		}
		long runBegin=System.currentTimeMillis();
		try{
			if(taskNode.getRunnable()!=null){
				logger.info(taskNode.getName()+" start");
				taskNode.getRunnable().run();
			}
		}catch(Exception e){
			logger.error(Constant.Base.EXCEPTION, e);
			taskEngine.setSuccessful(false);
		}finally{
			taskNode.setFinished(true);
		}
		long runCost=System.currentTimeMillis()-runBegin;
		if(taskNode.getChildTaskNodeList()!=null&&!taskNode.getChildTaskNodeList().isEmpty()){
			for(TaskNode childTaskNode:taskNode.getChildTaskNodeList()){
//				taskEngine.executeTaskNode(childTaskNode);
				synchronized(childTaskNode){
					childTaskNode.notify();
				}
			}
		}
		long taskCost=System.currentTimeMillis()-begin;
		logger.info(taskNode.getName()+" end,task cost:"+taskCost+",run cost:"+runCost+",waiting:"+(taskCost-runCost));
		this.taskNode.setRunCostTime(runCost);
		if(this.taskEngine.isDefaultMode()){
			if(this.taskEngine.isAllTaskNodeFinished()){
				synchronized (this.taskEngine) {
					this.taskEngine.notify();
				}
			}
		}
	}
}
