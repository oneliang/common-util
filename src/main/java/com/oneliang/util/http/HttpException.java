package com.oneliang.util.http;

public class HttpException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6670157031514003361L;

	/**
	 * @param message
	 */
	public HttpException(String message){
		super(message);
	}

	/**
	 * @param cause
	 */
	public HttpException(Throwable cause){
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public HttpException(String message,Throwable cause){
		super(message,cause);
	}
}
