package com.oneliang.util.resource;

import com.oneliang.Constants;
import com.oneliang.util.logging.Logger;
import com.oneliang.util.logging.LoggerManager;

/**
 * class Pool,resource pool
 * 
 * @author Dandelion
 * @since 2011-09-19
 */
public abstract class ResourcePool<T extends Object> implements Runnable {

	protected static final Logger logger=LoggerManager.getLogger(ResourcePool.class);

	protected String resourcePoolName=null;
	protected ResourceSource<T> resourceSource=null;
	protected int minResources=1;
	protected int maxResources=1;
	protected long resourceAliveTime=0;
	protected long threadSleepTime=300000l;
	protected ResourceStatus<T>[] resourcesStatus=null;
	protected int currentSize=0;
	protected Thread thread=null;

	protected final Object lock=new Object();

	/**
	 * initial
	 */
	@SuppressWarnings("unchecked")
	public void initialize() {
		this.resourcesStatus=(ResourceStatus<T>[])new ResourceStatus<?>[this.maxResources];
		for(int i=0;i<this.minResources;i++){
			T resource=this.resourceSource.getResource();
			if(resource!=null){
				ResourceStatus<T> resourceStatus=new ResourceStatus<T>();
				resourceStatus.setResource(resource);
				this.resourcesStatus[i]=resourceStatus;
				this.currentSize++;
			}
		}
		this.thread=new Thread(this);
		this.thread.start();
	}

	/**
	 * get resource from resource pool
	 * @return T
	 * @throws Exception
	 */
	public T getResource() throws ResourcePoolException{
		T resource = null;
		synchronized(this.lock){
			int index=0;
			//true prove if have the resource which have not in use
			//current size > 0 prove the resource pool have the resource
			if(this.currentSize>0){
				for(ResourceStatus<T> resourceStatus:this.resourcesStatus){
					if(resourceStatus!=null){
						if(!resourceStatus.isInUse()){
							resource=resourceStatus.getResource();
							resourceStatus.setInUse(true);
							break;
						}
					}
				}
			}
			if(resource==null){
				if(this.currentSize<this.maxResources){
					for(ResourceStatus<T> resourceStatus:this.resourcesStatus){
						if(resourceStatus==null){
							resource=this.resourceSource.getResource();
							if(resource!=null){
								ResourceStatus<T> oneStatus=new ResourceStatus<T>();
								oneStatus.setResource(resource);
								oneStatus.setInUse(true);
								
								this.resourcesStatus[index]=oneStatus;
								
								this.currentSize++;
							}
							break;
						}
						index++;
					}
				}else{
					throw new ResourcePoolException("The resource pool is max,current:"+this.currentSize);
				}
			}
		}
		return resource;
	}

	/**
	 * release resource to pool
	 * @param resource
	 */
	public void releaseResource(T resource){
		if(resource!=null){
			for(ResourceStatus<T> resourceStatus:this.resourcesStatus){
				if(resourceStatus!=null){
					//find the resource and set in use false
					if(resource.equals(resourceStatus.getResource())){
						resourceStatus.setInUse(false);
						resourceStatus.setLastNotInUseTime(System.currentTimeMillis());
						break;
					}
				}
			}
		}
	}

	/**
	 * destroy resource
	 * @param resource
	 * @throws ResourcePoolException
	 */
	protected abstract void destroyResource(T resource) throws ResourcePoolException;

	/**
	 * clean the timeout resource
	 * @throws Exception
	 */
	public void clean(){
		synchronized(this.lock){
			int index=0;
			for(ResourceStatus<T> resourceStatus:this.resourcesStatus){
				if(resourceStatus!=null){
					if(!resourceStatus.isInUse()){
						long lastTime=resourceStatus.getLastNotInUseTime();
						long currentTime=System.currentTimeMillis();
						T resource=resourceStatus.getResource();
						if((currentTime-lastTime)>=this.resourceAliveTime){
							try {
								destroyResource(resource);
							} catch (Exception e) {
								logger.error(Constants.Base.EXCEPTION, e);
							}
							this.resourcesStatus[index]=null;
							this.currentSize--;
						}
					}
				}
				index++;
			}
		}
	}

	/**
	 * thread run
	 */
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(threadSleepTime);
				logger.debug("--"+thread.getName()+"--The resource pool is:'"+this.resourcePoolName+"',before clean resources number:"+String.valueOf(this.currentSize));
				this.clean();
				logger.debug("--"+thread.getName()+"--The resource pool is:'"+this.resourcePoolName+"',after clean resources number:"+String.valueOf(this.currentSize));
			} catch (InterruptedException e) {
				logger.debug("need to interrupt:"+e.getMessage());
				Thread.currentThread().interrupt();
			} catch (Exception e){
				logger.error(Constants.Base.EXCEPTION, e);
			}
		}
	}

	/**
	 * @return the resourcePoolName
	 */
	public String getResourcePoolName() {
		return resourcePoolName;
	}

	/**
	 * @param resourcePoolName the resourcePoolName to set
	 */
	public void setResourcePoolName(String resourcePoolName) {
		this.resourcePoolName = resourcePoolName;
	}

	/**
	 * @return the resourceSource
	 */
	public ResourceSource<T> getResourceSource() {
		return resourceSource;
	}

	/**
	 * @param resourceSource the resourceSource to set
	 */
	public void setResourceSource(ResourceSource<T> resourceSource) {
		this.resourceSource = resourceSource;
	}

	/**
	 * @return the minResources
	 */
	public int getMinResources() {
		return minResources;
	}

	/**
	 * @param minResources the minResources to set
	 */
	public void setMinResources(int minResources) {
		this.minResources = minResources;
	}

	/**
	 * @return the maxResources
	 */
	public int getMaxResources() {
		return maxResources;
	}

	/**
	 * @param maxResources the maxResources to set
	 */
	public void setMaxResources(int maxResources) {
		this.maxResources = maxResources;
	}

	/**
	 * @return the resourceAliveTime
	 */
	public long getResourceAliveTime() {
		return resourceAliveTime;
	}

	/**
	 * @param resourceAliveTime the resourceAliveTime to set
	 */
	public void setResourceAliveTime(long resourceAliveTime) {
		this.resourceAliveTime = resourceAliveTime;
	}

	/**
	 * @return the threadSleepTime
	 */
	public long getThreadSleepTime() {
		return threadSleepTime;
	}

	/**
	 * @param threadSleepTime the threadSleepTime to set
	 */
	public void setThreadSleepTime(long threadSleepTime) {
		this.threadSleepTime = threadSleepTime;
	}

	/**
	 * @return the resourcesStatus
	 */
	public ResourceStatus<T>[] getResourcesStatus() {
		return resourcesStatus;
	}

	/**
	 * @param resourcesStatus the resourcesStatus to set
	 */
	public void setResourcesStatus(ResourceStatus<T>[] resourcesStatus) {
		this.resourcesStatus = resourcesStatus;
	}

	/**
	 * @return the currentSize
	 */
	public int getCurrentSize() {
		return currentSize;
	}

	/**
	 * @param currentSize the currentSize to set
	 */
	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
}