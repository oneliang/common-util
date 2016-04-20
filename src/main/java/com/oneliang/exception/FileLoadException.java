package com.oneliang.exception;

public class FileLoadException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8541494498804859430L;

	/**
	 * @param message
	 */
	public FileLoadException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public FileLoadException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FileLoadException(String message,Throwable cause){
		super(message,cause);
	}
}
