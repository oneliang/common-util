package com.oneliang.util.encrypt;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

import com.oneliang.Constant;
import com.oneliang.util.common.Base64;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public final class RSA {

	private static final String ALGORITHM_RSA = "RSA";
	private static final String RSA_MODE_1 = "RSA/ECB/PKCS1Padding";

	private RSA() {}

	/**
	 * get key pair
	 * 
	 * @return KeyPair
	 * @throws Exception
	 */
	public static KeyPair getKeyPair() throws Exception {
		SecureRandom secureRandom = new SecureRandom();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
		keyPairGenerator.initialize(Constant.Capacity.BYTES_PER_KB, secureRandom);
		return keyPairGenerator.generateKeyPair();
	}

	/**
	 * @param byteArray
	 * @param privateKey
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] byteArray, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(byteArray);
	}

	/**
	 * @param byteArray
	 * @param publicKey
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] byteArray, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance(RSA_MODE_1);
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(byteArray);
	}

	public static void main(String[] args) throws Exception {
		KeyPair keyPair = getKeyPair();
		String privateFile = "/D:/RSAPrivateKey.txt";
		String publicFile = "/D:/RSAPublicKey.txt";
		FileUtil.createFile(privateFile);
		FileUtil.createFile(publicFile);
		OutputStream privateOutputStream = new FileOutputStream(privateFile);
		OutputStream publicOutputStream = new FileOutputStream(publicFile);
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		byte[] privateKeyBase64ByteArray = Base64.encode(privateKey.getEncoded(),Base64.DEFAULT);
		byte[] publicKeyBase64ByteArray = Base64.encode(publicKey.getEncoded(),Base64.DEFAULT);
		privateOutputStream.write(privateKeyBase64ByteArray);
		publicOutputStream.write(publicKeyBase64ByteArray);
		privateOutputStream.close();
		publicOutputStream.close();
//		byte[] buffer = Base64.decode(privateKeyBase64);
//		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(buffer);
//		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//
//		buffer = Base64.decode(publicKeyBase64);
//		keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
//		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(buffer);
//		publicKey = keyFactory.generatePublic(x509KeySpec);

		String string = "a";
		byte[] encryptByteArray = encryptByPrivateKey(string.getBytes(Constant.Encoding.UTF8), privateKey);
		System.out.println(StringUtil.byteArrayToHexString(encryptByteArray));
		System.out.println(new String(decryptByPublicKey(encryptByteArray, publicKey), Constant.Encoding.UTF8));
	}
}
