package com.oneliang.util.task;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskNode {

	private String name=null;
	private int depth=0;
	private boolean finished=false;
	private Runnable runnable=null;
	private long runCostTime=0;
	private List<TaskNode> parentTaskNodeList=new CopyOnWriteArrayList<TaskNode>();
	private List<TaskNode> childTaskNodeList=new CopyOnWriteArrayList<TaskNode>();

	/**
	 * is all parent finished
	 * @return boolean
	 */
	boolean isAllParentFinished(){
		boolean allParentFinished=false;
		boolean result=true;
		for(TaskNode parentTaskNode:parentTaskNodeList){
			if(!parentTaskNode.isFinished()){
				result=false;
				break;
			}
		}
		if(result){
			allParentFinished=true;
		}
		return allParentFinished;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}
	/**
	 * @param finished the finished to set
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	/**
	 * @return the runnable
	 */
	Runnable getRunnable() {
		return runnable;
	}
	/**
	 * @param runnable the runnable to set
	 */
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}
	/**
	 * @param depth the depth to set
	 */
	void setDepth(int depth) {
		this.depth = depth;
	}
	/**
	 * add parent task node
	 * @param parentTaskNode
	 */
	private void addParentTaskNode(TaskNode parentTaskNode) {
		if(!this.parentTaskNodeList.contains(parentTaskNode)){
			this.parentTaskNodeList.add(parentTaskNode);
		}
	}
	/**
	 * @return the parentTaskNodeList
	 */
	public List<TaskNode> getParentTaskNodeList() {
		return parentTaskNodeList;
	}
	/**
	 * @return the childTaskNodeList
	 */
	public List<TaskNode> getChildTaskNodeList() {
		return childTaskNodeList;
	}
	/**
	 * add child task node
	 * @param childTaskNode
	 */
	public void addChildTaskNode(TaskNode childTaskNode) {
		if(childTaskNode!=null&&!this.childTaskNodeList.contains(childTaskNode)){
			this.childTaskNodeList.add(childTaskNode);
			childTaskNode.addParentTaskNode(this);
		}
	}
	/**
	 * remove parent task node
	 * @param parentTaskNode
	 */
	public void removeParentTaskNode(TaskNode parentTaskNode){
		if(parentTaskNode!=null&&this.parentTaskNodeList.contains(parentTaskNode)){
			this.parentTaskNodeList.remove(parentTaskNode);
			parentTaskNode.removeChildTaskNode(this);
		}
	}
	/**
	 * remove child task node
	 * @param childTaskNode
	 */
	public void removeChildTaskNode(TaskNode childTaskNode){
		if(childTaskNode!=null&&this.childTaskNodeList.contains(childTaskNode)){
			this.childTaskNodeList.remove(childTaskNode);
			childTaskNode.removeParentTaskNode(this);
		}
	}
	public String toString() {
		return this.name;
	}
	/**
	 * clear parent task node list
	 */
	public void clearParentTaskNodeList(){
		this.parentTaskNodeList.clear();
	}
	/**
	 * clear child task node list
	 */
	public void clearChildTaskNodeList(){
		this.childTaskNodeList.clear();
	}
	/**
	 * @return the runCostTime
	 */
	public long getRunCostTime() {
		return runCostTime;
	}
	/**
	 * @param runCostTime the runCostTime to set
	 */
	public void setRunCostTime(long runCostTime) {
		this.runCostTime = runCostTime;
	}
}
