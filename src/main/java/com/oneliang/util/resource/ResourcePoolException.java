package com.oneliang.util.resource;

public class ResourcePoolException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4902981784822638571L;

	/**
	 * @param message
	 */
	public ResourcePoolException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ResourcePoolException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResourcePoolException(String message,Throwable cause) {
		super(message,cause);
	}
}
