package com.oneliang.util.thread;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.oneliang.Constant;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

/**
 * Timer control the task which is it time to run,and manage the task
 */
public class Timer implements Runnable{

	private static final Logger logger=LoggerManager.getLogger(Timer.class);

	private Thread thread=null;
	private List<TimerTask> timerTaskList=new CopyOnWriteArrayList<TimerTask>();

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
	public synchronized void interrupt(){
		if(this.thread!=null){
			this.thread.interrupt();
			this.thread=null;
		}
	}

	/**
	 * schedule
	 * @param timerTask
	 * @param firstTime
	 * @param period period=0,only execute one time,but first time must be latter than current time
	 */
	public void schedule(TimerTask timerTask,Date firstTime,long period){
		if(timerTask!=null&&firstTime!=null){
			timerTask.setFirstTime(firstTime);
			timerTask.setPeriod(period);
			long nextExecuteTime=firstTime.getTime();
			long currentTimeMillis=System.currentTimeMillis();
			if((nextExecuteTime>=currentTimeMillis)||(nextExecuteTime<currentTimeMillis&&period>0)){
				if(nextExecuteTime<currentTimeMillis){
					nextExecuteTime=nextExecuteTime+period*Math.round(((double)(currentTimeMillis-nextExecuteTime)/period+0.5d));
				}
				timerTask.setNextExecuteTime(nextExecuteTime);
				this.timerTaskList.add(timerTask);
				synchronized(this){
					this.notify();
				}
			}
		}
	}

	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try{
				long currentTimeMillis=System.currentTimeMillis();
				if(!this.timerTaskList.isEmpty()){
					for(TimerTask timerTask:timerTaskList){
						if((timerTask.getNextExecuteTime()>=currentTimeMillis-500)&&(timerTask.getNextExecuteTime()<=currentTimeMillis+500)){
							try{
								timerTask.runTask();
							}catch (Exception e) {
								logger.error(Constant.Base.EXCEPTION, e);								
							}
							if(timerTask.getPeriod()<=0){
								this.timerTaskList.remove(timerTask);
							}else{
								timerTask.updateNextExecuteTime();
							}
						}
					}
					Thread.sleep(1000);
				}else{
					synchronized(this){
						this.wait();
					}
				}
			}catch (InterruptedException e) {
				logger.debug("need to interrupt:"+e.getMessage());
				Thread.currentThread().interrupt();
			}catch (Exception e){
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
	}

	public static abstract class TimerTask {

	    private Date firstTime=null;
		private long nextExecuteTime=0;
		private long period=0;

		/**
		 * run task
		 */
		public abstract void runTask();

		public void updateNextExecuteTime(){
			this.nextExecuteTime=this.nextExecuteTime+period;
		}

		/**
		 * @return the firstTime
		 */
		public Date getFirstTime() {
			return firstTime;
		}

		/**
		 * @param firstTime the firstTime to set
		 */
		void setFirstTime(Date firstTime) {
			this.firstTime = firstTime;
		}

		/**
		 * @return the nextExecuteTime
		 */
		public long getNextExecuteTime() {
			return nextExecuteTime;
		}

		/**
		 * @param nextExecuteTime the nextExecuteTime to set
		 */
		void setNextExecuteTime(long nextExecuteTime) {
			this.nextExecuteTime = nextExecuteTime;
		}

		/**
		 * @return the period
		 */
		public long getPeriod() {
			return period;
		}

		/**
		 * @param period the period to set
		 */
		void setPeriod(long period) {
			this.period = period;
		}
	}

	public static void main(String[] args) {
		Timer timer=new Timer();
		timer.start();
		TimerTask timerTask1=new TimerTask(){
			public void runTask(){
				System.out.println("running timer task 1");
			}
		};
		TimerTask timerTask2=new TimerTask(){
			public void runTask(){
				System.out.println("running  timer task 2");
			}
		};
		timer.schedule(timerTask1, TimeUtil.getTime(), 0);
		timer.schedule(timerTask2, TimeUtil.getTime(), 10000);
	}
}
