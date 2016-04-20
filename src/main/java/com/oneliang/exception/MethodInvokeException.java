package com.oneliang.exception;

public class MethodInvokeException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 691132443052509352L;

	/**
	 * @param message
	 */
	public MethodInvokeException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public MethodInvokeException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodInvokeException(String message,Throwable cause){
		super(message,cause);
	}
}
