package com.oneliang.util.resource;

public class ResourceStatus<T extends Object>{
	
	private T resource=null;
	private boolean inUse=false;
	private long lastNotInUseTime=0;
	/**
	 * @return the resource
	 */
	public T getResource() {
		return resource;
	}
	/**
	 * @param resource the resource to set
	 */
	public void setResource(T resource) {
		this.resource = resource;
	}
	/**
	 * @return the inUse
	 */
	public boolean isInUse() {
		return inUse;
	}
	/**
	 * @param inUse the inUse to set
	 */
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	/**
	 * @return the lastNotInUseTime
	 */
	public long getLastNotInUseTime() {
		return lastNotInUseTime;
	}
	/**
	 * @param lastNotInUseTime the lastNotInUseTime to set
	 */
	public void setLastNotInUseTime(long lastNotInUseTime) {
		this.lastNotInUseTime = lastNotInUseTime;
	}
}
