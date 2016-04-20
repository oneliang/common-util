package com.oneliang.util.jvm;

import com.oneliang.Constant;
import com.oneliang.util.log.Logger;

public class JvmMonitor implements Runnable {

	private static final Logger logger=Logger.getLogger(JvmMonitor.class);
	
	private static final short DEFAULT_PERCENT=85;
	
	private static Thread thread=null;
	private short percent=85;
	private long checkTime=5000;

	public JvmMonitor() {}
	
	public JvmMonitor(short percent,long checkTime){
		if(percent>=100){
			percent=DEFAULT_PERCENT;
		}
		this.percent=percent;
		this.checkTime=checkTime;
	}

	public synchronized void start(){
		if(thread==null){
			thread=new Thread(this);
			thread.start();
		}
	}

	/**
	 * interrupt
	 */
	public synchronized void interrupt(){
		if(thread!=null){
			thread.interrupt();
			thread=null;
		}
	}

	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				long heapSize=Runtime.getRuntime().totalMemory();
			    long heapMaxSize=Runtime.getRuntime().maxMemory();
//			    long heapFreeSize=Runtime.getRuntime().freeMemory();

			    long heapSizeM=heapSize/Constant.Capacity.BYTES_PER_MB;
			    long heapMaxSizeM=heapMaxSize/Constant.Capacity.BYTES_PER_MB;
//			    long heapFreeSizeM=heapFreeSize/Constant.Capacity.BYTES_PER_MB;
			    short currentPercent=(short)((double)heapSizeM/heapMaxSizeM*100);
			    if(currentPercent>=this.percent){
			    	Runtime.getRuntime().gc();
			    }
			    logger.log("jvm used percent:"+currentPercent+"%"+",heap size:"+heapSizeM+"M,max:"+heapMaxSizeM+"M");
				Thread.sleep(this.checkTime);
			}catch (InterruptedException e) {
				logger.log("need to interrupt:"+e.getMessage());
				Thread.currentThread().interrupt();
			}catch (Exception e){
				logger.error(Constant.Base.EXCEPTION, e);
			}
		}
	}

	/**
	 * @param checkTime the checkTime to set
	 */
	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(short percent) {
		if(percent>=100){
			percent=DEFAULT_PERCENT;
		}
		this.percent = percent;
	}
}
