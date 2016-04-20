package com.oneliang.exception;

public class MappingNotFoundException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -9053916589861008911L;

	/**
	 * @param message
	 */
	public MappingNotFoundException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public MappingNotFoundException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MappingNotFoundException(String message,Throwable cause){
		super(message,cause);
	}
}
