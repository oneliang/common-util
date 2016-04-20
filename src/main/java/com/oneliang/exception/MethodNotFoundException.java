package com.oneliang.exception;

public class MethodNotFoundException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4199240817339593012L;

	/**
	 * @param message
	 */
	public MethodNotFoundException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public MethodNotFoundException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotFoundException(String message,Throwable cause){
		super(message,cause);
	}
}
