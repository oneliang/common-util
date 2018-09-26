package com.oneliang.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.oneliang.Constants;
import com.oneliang.util.common.StringUtil;

public final class DES {

	private static final String DES="DES";
	private static final String DES_CBC_PKC5PADDING="DES/CBC/PKCS5Padding";
	private static final byte[] IV={1,2,3,4,5,6,7,8};

	private DES() {}

	/**
	 * des encrypt
	 * @param source
	 * @param keyString
	 * @return String
	 * @throws Exception
	 */
	public static String encrypt(String source,String keyString) throws Exception{
		IvParameterSpec ivParameterSpec=new IvParameterSpec(IV);
		SecretKeySpec key=new SecretKeySpec(keyString.getBytes(), DES);
		Cipher cipher=Cipher.getInstance(DES_CBC_PKC5PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
		byte[] bytes=cipher.doFinal(source.getBytes());  
		return StringUtil.byteArrayToHexString(bytes);
	}

	/**
	 * des decrypt
	 * @param hexString
	 * @param keyString
	 * @return String
	 * @throws Exception
	 */
	public static String decrypt(String hexString,String keyString) throws Exception{
		byte[] bytes=StringUtil.hexStringToByteArray(hexString);
		IvParameterSpec ivParameterSpec=new IvParameterSpec(IV);  
		SecretKeySpec key=new SecretKeySpec(keyString.getBytes(), DES);
		Cipher cipher = Cipher.getInstance(DES_CBC_PKC5PADDING);
		cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
		byte[] array=cipher.doFinal(bytes);
		return new String(array, Constants.Encoding.UTF8);
	}
}
