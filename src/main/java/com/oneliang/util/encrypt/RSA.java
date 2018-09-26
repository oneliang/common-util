package com.oneliang.util.encrypt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.oneliang.Constants;
import com.oneliang.util.common.Base64;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.file.FileUtil;

public final class RSA {

	public static final String ALGORITHM_RSA = "RSA";
	public static final String RSA_MODE_1 = "RSA/ECB/PKCS1Padding";
	public static final int ENCRYPT_MAX_LENGTH=117;
	public static final int DECRYPT_MAX_LENGTH=128;

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
		keyPairGenerator.initialize(Constants.Capacity.BYTES_PER_KB, secureRandom);
		return keyPairGenerator.generateKeyPair();
	}

	/**
	 * @param byteArray
	 * @param key
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encryptByKey(byte[] byteArray, Key key) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		if(byteArray!=null){
			int leftLength=byteArray.length;
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
			while(leftLength>0){
				int bufferLength=ENCRYPT_MAX_LENGTH;
				if(leftLength<ENCRYPT_MAX_LENGTH){
					bufferLength=leftLength;
				}
				byte[] buffer=new byte[bufferLength];
				int length=byteArrayInputStream.read(buffer, 0, buffer.length);
				leftLength=leftLength-length;
				byteArrayOutputStream.write(cipher.doFinal(buffer));
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * @param byteArray
	 * @param key
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decryptByKey(byte[] byteArray, Key key) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
		Cipher cipher = Cipher.getInstance(RSA_MODE_1);
		cipher.init(Cipher.DECRYPT_MODE, key);
		if(byteArray!=null){
			int leftLength=byteArray.length;
			ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
			while(leftLength>0){
				int bufferLength=DECRYPT_MAX_LENGTH;
				if(leftLength<DECRYPT_MAX_LENGTH){
					bufferLength=leftLength;
				}
				byte[] buffer=new byte[bufferLength];
				int length=byteArrayInputStream.read(buffer, 0, buffer.length);
				leftLength=leftLength-length;
				byteArrayOutputStream.write(cipher.doFinal(buffer));
			}
		}
		return byteArrayOutputStream.toByteArray();
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
		byte[] buffer = Base64.decode(privateKeyBase64ByteArray, Base64.DEFAULT);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(buffer);
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
		privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		buffer = Base64.decode(publicKeyBase64ByteArray, Base64.DEFAULT);
		keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(buffer);
		publicKey = keyFactory.generatePublic(x509KeySpec);
		//need to encrypt data max length:117
		//need to decrypt data max length:128
		String string = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		System.out.println(string.getBytes(Constants.Encoding.UTF8).length);
		byte[] encryptByteArray = encryptByKey(string.getBytes(Constants.Encoding.UTF8), privateKey);
		System.out.println(StringUtil.byteArrayToHexString(encryptByteArray));
		System.out.println(new String(decryptByKey(encryptByteArray, publicKey), Constants.Encoding.UTF8));
	}
}
