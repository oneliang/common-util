package com.oneliang.exception;

public class FieldReflectException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2332376911113378804L;

	/**
	 * @param message
	 */
	public FieldReflectException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public FieldReflectException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FieldReflectException(String message,Throwable cause){
		super(message,cause);
	}
}
