package com.oneliang.exception;

public class MethodNotSupportedException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6664815302540021031L;

	/**
	 * @param message
	 */
	public MethodNotSupportedException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public MethodNotSupportedException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotSupportedException(String message,Throwable cause){
		super(message,cause);
	}
}
