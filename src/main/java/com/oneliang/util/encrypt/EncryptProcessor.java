package com.oneliang.util.encrypt;

public abstract interface EncryptProcessor {

	/**
	 * encrypt process
	 * @param source
	 * @return String
	 * @throws Exception
	 */
	public abstract String encryptProcess(String source) throws Exception;
}
