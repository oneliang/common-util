package com.oneliang.util.encrypt;
/**
 * Encrypt
 * @author Dandelion
 */
public final class Encrypt {

	/**
	 * encrypt with EncryptProcessor
	 * @param source
	 * @param encryptProcessor
	 * @return String
	 * @throws Exception
	 */
	public static String encrypt(final String source,EncryptProcessor encryptProcessor) throws Exception{
		String result=source;
		if(encryptProcessor!=null){
			result=encryptProcessor.encryptProcess(source);
		}
		return result;
	}
	
	/**
	 * decrypt with DecryptProcessor
	 * @param source
	 * @param decryptProcessor
	 * @return String
	 * @throws Exception
	 */
	public static String decrypt(final String source,DecryptProcessor decryptProcessor) throws Exception{
		String result=source;
		if(decryptProcessor!=null){
			result=decryptProcessor.decryptProcess(source);
		}
		return result;
	}
}
