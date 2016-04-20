package com.oneliang.util.file;

public class FileCopyException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6670157031514003361L;

	/**
	 * @param message
	 */
	public FileCopyException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public FileCopyException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FileCopyException(String message,Throwable cause){
		super(message,cause);
	}
}
