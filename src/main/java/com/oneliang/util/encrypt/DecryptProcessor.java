package com.oneliang.util.encrypt;

public abstract interface DecryptProcessor {

	/**
	 * decrypt process
	 * @param source
	 * @return String
	 * @throws Exception
	 */
	public abstract String decryptProcess(String source) throws Exception;
}
