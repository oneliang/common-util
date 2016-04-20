package com.oneliang.exception;

/**
 * initialize
 */
public class InitializeException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4552732773580998316L;

	/**
	 * @param message
	 */
	public InitializeException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public InitializeException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializeException(String message,Throwable cause){
		super(message,cause);
	}
}
