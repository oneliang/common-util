package com.oneliang.util.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.oneliang.Constant;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

public class ResourceQueueThread<T extends Object> implements Runnable {

	private static final Logger logger=LoggerManager.getLogger(ResourceQueueThread.class);

	private Queue<T> resourceQueue=new ConcurrentLinkedQueue<T>();
	private Thread thread=null;
	private ResourceProcessor<T> resourceProcessor=null;
	private boolean needToInterrupt = false;

	/**
	 * constructor
	 * @param resourceProcessor
	 */
	public ResourceQueueThread(ResourceProcessor<T> resourceProcessor) {
		this.resourceProcessor=resourceProcessor;
	}

	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try{
				if(!this.resourceQueue.isEmpty()){
					T resource=this.resourceQueue.poll();
					if(this.resourceProcessor!=null){
						this.resourceProcessor.process(resource);
					}
				}else{
					if(this.needToInterrupt){
						this.realInterrupt();
					}
					synchronized (this) {
						//double check,for the scene which notify first
						if(this.needToInterrupt){
							this.realInterrupt();
						}
						this.wait();
					}
				}
			}catch (InterruptedException e) {
				logger.verbose("need to interrupt:"+e.getMessage());
				Thread.currentThread().interrupt();
			}catch (Exception e){
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
	}

	/**
	 * start
	 */
	public synchronized void start(){
		if(this.thread==null){
			this.thread=new Thread(this);
			this.thread.start();
		}
	}

	/**
	 * interrupt
	 */
	public void interrupt(){
		this.needToInterrupt=true;
		synchronized (this) {
			this.notify();
		}
	}

	/**
	 * real interrupt
	 */
	private void realInterrupt(){
		if(this.thread!=null){
			this.thread.interrupt();
			this.thread=null;
			this.needToInterrupt=false;
		}
	}

	/**
	 * @param resource the resource to add
	 */
	public void addResource(T resource) {
		if(resource!=null){
			this.resourceQueue.add(resource);
			synchronized (this) {
				this.notify();
			}
		}
	}

	public abstract static interface ResourceProcessor<T extends Object>{
		/**
		 * process the resource
		 * @param resource
		 */
		public abstract void process(T resource);
	}
}
