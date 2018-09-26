package com.oneliang.util.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

import com.oneliang.Constants;

public final class Generator{

	private static final char[] characters = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private static final String FONT_FAMILY_TIMES_NEW_ROMAN = "Times New Roman";
	private static final int COUNT_MAX_LENGTH = 3;
	private static final int COUNT_MAX_VALUE = 999;
	private static final ThreadLocal<Integer> countThreadLocal = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return 0;
		}
	};

	private Generator() {}

	/**
	 * the union id generator
	 * 
	 * @return String
	 */
	public static String ID() {
		int count = countThreadLocal.get();
		long threadId = Thread.currentThread().getId();
		long timeMillis = System.currentTimeMillis();
		String result = String.valueOf(timeMillis) + StringUtil.fillZero(COUNT_MAX_LENGTH + 1 - String.valueOf(threadId).length()) + String.valueOf(threadId) + StringUtil.fillZero(COUNT_MAX_LENGTH - String.valueOf(count).length()) + String.valueOf(count);
		count++;
		if (count > COUNT_MAX_VALUE) {
			count = 0;
		}
		countThreadLocal.set(count);
		return result;
	}

	/**
	 * the uuid generator
	 * @return String
	 */
	public static String UUID(){
		return UUID.randomUUID().toString();
	}

	/**
	 * MD5 generator
	 * 
	 * @param source
	 * @return MD5 string
	 */
	public static String MD5(final String source) {
		String string = null;
		if (source != null) {
			try {
				string = StringUtil.byteArrayToHexString(MD5ByteArray(source.getBytes(Constants.Encoding.UTF8)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return string;
	}

	/**
	 * random string
	 * @param size
	 * @return String
	 */
	public static final String randomString(int size) {
		StringBuilder stringBuilder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			String string = Character.toString(characters[random.nextInt(characters.length)]);
			stringBuilder.append(string);
		}
		return stringBuilder.toString();
	}

	/**
	 * create random image
	 * 
	 * @param string
	 * @param width
	 * @param height
	 * @return BufferedImage
	 */
	public static BufferedImage createRandomImage(final String string, final int width, final int height){
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics graphics = bufferedImage.getGraphics();
		Random random = new Random();
		graphics.setColor(getRandomColor(200, 250));
		graphics.fillRect(0, 0, width, height);
		graphics.setFont(new Font(FONT_FAMILY_TIMES_NEW_ROMAN, Font.PLAIN, 18));
		graphics.setColor(getRandomColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			graphics.drawLine(x, y, x + xl, y + yl);
		}
		if (string != null) {
			for (int i = 0; i < string.length(); i++) {
				graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
				graphics.drawString(Character.toString(string.charAt(i)), 14 * i + 6, 16);
			}
		}
		graphics.dispose();
		return bufferedImage;
	}

	/**
	 * get random color
	 * @param frontColor
	 * @param backColor
	 * @return Color
	 */
	private static Color getRandomColor(int frontColor, int backColor) {
		Random random = new Random();
		if (frontColor > 0xFF) {
			frontColor = 0xFF;
		}
		if (backColor > 0xFF) {
			backColor = 0xFF;
		}
		int red = frontColor + random.nextInt(backColor - frontColor);
		int green = frontColor + random.nextInt(backColor - frontColor);
		int blue = frontColor + random.nextInt(backColor - frontColor);
		return new Color(red, green, blue);
	}

	/**
	 * @param byteArray
	 * @return byte[]
	 */
	public static byte[] MD5ByteArray(byte[] byteArray){
		byte[] result=null;
		try {
			MessageDigest messageDigest=MessageDigest.getInstance("MD5");
			messageDigest.update(byteArray);
			result=messageDigest.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * refer to rfc2104 HMAC
	 * @param key
	 * @param data
	 * @return byte[]
	 */
	public static byte[] getHmacMd5Bytes(byte[] key,byte[] data){
		/*
		 * HmacMd5 calculation formula: H(K XOR opad, H(K XOR ipad, text))
		 * 
		 * HmacMd5 计算公式：H(K XOR opad, H(K XOR ipad, text))
		 * 
		 * H代表hash算法，本类中使用MD5算法，K代表密钥，text代表要加密的数据
		 * 
		 * ipad为0x36，opad为0x5C。
		 */
		int length = 64;
		byte[] ipad = new byte[length];
		byte[] opad = new byte[length];
		for (int i = 0; i < 64; i++) {
			ipad[i] = 0x36;
			opad[i] = 0x5C;
		}
		byte[] actualKey = key; // Actual key.
		byte[] keyArr = new byte[length]; // Key bytes of 64 bytes length
		/*
		 * If key's length is longer than 64,then use hash to digest it and use
		 * the result as actual key.
		 * 
		 * 如果密钥长度，大于64字节，就使用哈希算法，计算其摘要，作为真正的密钥。
		 */
		if (key.length > length){
			actualKey = MD5ByteArray(key);
		}
		for (int i = 0; i < actualKey.length; i++){
			keyArr[i] = actualKey[i];
		}
		/*
		 * append zeros to K
		 * 
		 * 如果密钥长度不足64字节，就使用0x00补齐到64字节。
		 */
		if (actualKey.length < length){
			for (int i = actualKey.length; i < keyArr.length; i++){
				keyArr[i] = 0x00;
			}
		}
		/*
		 * calc K XOR ipad
		 * 
		 * 使用密钥和ipad进行异或运算。
		 */
		byte[] kIpadXorResult = new byte[length];
		for (int i = 0; i < length; i++){
			kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
		}
		/*
		 * append "text" to the end of "K XOR ipad"
		 * 
		 * 将待加密数据追加到K XOR ipad计算结果后面。
		 */
		byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
		for (int i = 0; i < kIpadXorResult.length; i++){
			firstAppendResult[i] = kIpadXorResult[i];
		}
		for (int i = 0; i < data.length; i++){
			firstAppendResult[i + keyArr.length] = data[i];
		}
		/*
		 * calc H(K XOR ipad, text)
		 * 
		 * 使用哈希算法计算上面结果的摘要。
		 */
		byte[] firstHashResult = MD5ByteArray(firstAppendResult);
		/*
		 * calc K XOR opad
		 * 
		 * 使用密钥和opad进行异或运算。
		 */
		byte[] kOpadXorResult = new byte[length];
		for (int i = 0; i < length; i++){
			kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
		}
		/*
		 * append "H(K XOR ipad, text)" to the end of "K XOR opad"
		 * 
		 * 将H(K XOR ipad, text)结果追加到K XOR opad结果后面
		 */
		byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];
		for (int i = 0; i < kOpadXorResult.length; i++){
			secondAppendResult[i] = kOpadXorResult[i];
		}
		for (int i = 0; i < firstHashResult.length; i++) {
			secondAppendResult[i + keyArr.length] = firstHashResult[i];
		}
		/*
		 * H(K XOR opad, H(K XOR ipad, text))
		 * 
		 * 对上面的数据进行哈希运算。
		 */
		byte[] hmacMd5Bytes = MD5ByteArray(secondAppendResult);
		return hmacMd5Bytes;
	}

	/**
	 * md5 file
	 * @param fullFilename
	 * @return String
	 */
	public static String MD5File(String fullFilename){
		String result=null;
		if(fullFilename!=null){
			try{
				result=MD5(new FileInputStream(fullFilename));
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * md5
	 * @param inputStream
	 * @return String
	 */
	public static String MD5(final InputStream inputStream) {
		String result=null;
		if(inputStream!=null){
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] buffer = new byte[Constants.Capacity.BYTES_PER_KB];
				int readCount = 0;
				while ((readCount = inputStream.read(buffer,0,buffer.length)) != -1) {
					md.update(buffer, 0, readCount);
				}
				result=StringUtil.byteArrayToHexString(md.digest());
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * SHA1 byte array
	 * @param byteArray
	 * @return byte[]
	 */
	public static byte[] SHA1ByteArray(byte[] byteArray){
		byte result[] = null;
		try{
			MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
			digest.update(byteArray);
			result = digest.digest();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * SHA1
	 * @param source
	 * @return String
	 */
	public static String SHA1(final String source){
		String string = null;
		if (source != null) {
			try {
				string = StringUtil.byteArrayToHexString(SHA1ByteArray(source.getBytes(Constants.Encoding.UTF8)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return string;
	}
}
