package com.oneliang.util.task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.oneliang.util.concurrent.ThreadTask;

public class MultiTaskNodeThreadTask implements ThreadTask {

	private List<TaskNodeThreadTask> taskNodeThreadTaskList=new CopyOnWriteArrayList<TaskNodeThreadTask>();

	public void runTask() {
		if(this.taskNodeThreadTaskList!=null){
			for(TaskNodeThreadTask taskNodeThreadTask:this.taskNodeThreadTaskList){
				taskNodeThreadTask.runTask();
			}
		}
	}

	/**
	 * add task node thread task
	 * @param taskNodeThreadTask
	 * @return boolean
	 */
	public boolean addTaskNodeThreadTask(TaskNodeThreadTask taskNodeThreadTask){
		return this.taskNodeThreadTaskList.add(taskNodeThreadTask);
	}
}
