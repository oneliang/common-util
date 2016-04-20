package com.oneliang.util.common;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MathUtil{
	
	private static final char[] characters={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	private static final Map<Character,Integer> characterMap=new ConcurrentHashMap<Character, Integer>();

	private MathUtil(){}

	static{
		characterMap.put(characters[0], 0);
		characterMap.put(characters[1], 1);
		characterMap.put(characters[2], 2);
		characterMap.put(characters[3], 3);
		characterMap.put(characters[4], 4);
		
		characterMap.put(characters[5], 5);
		characterMap.put(characters[6], 6);
		characterMap.put(characters[7], 7);
		characterMap.put(characters[8], 8);
		characterMap.put(characters[9], 9);
		
		characterMap.put(characters[10], 10);
		characterMap.put(characters[11], 11);
		characterMap.put(characters[12], 12);
		characterMap.put(characters[13], 13);
		characterMap.put(characters[14], 14);
		
		characterMap.put(characters[15], 15);
		characterMap.put(characters[16], 16);
		characterMap.put(characters[17], 17);
		characterMap.put(characters[18], 18);
		characterMap.put(characters[19], 19);
		
		characterMap.put(characters[20], 20);
		characterMap.put(characters[21], 21);
		characterMap.put(characters[22], 22);
		characterMap.put(characters[23], 23);
		characterMap.put(characters[24], 24);
		
		characterMap.put(characters[25], 25);
		characterMap.put(characters[26], 26);
		characterMap.put(characters[27], 27);
		characterMap.put(characters[28], 28);
		characterMap.put(characters[29], 29);
		
		characterMap.put(characters[30], 30);
		characterMap.put(characters[31], 31);
		characterMap.put(characters[32], 32);
		characterMap.put(characters[33], 33);
		characterMap.put(characters[34], 34);
		characterMap.put(characters[35], 35);
	}
	/**
	 * round half up
	 * @param num
	 * @param scale
	 * @return double
	 */
	public static double round(final double num, final int scale){
		if (scale < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal b = new BigDecimal(Double.toString(num));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * integer to scale string
	 * @param number
	 * @param scale
	 * @return String
	 */
	public static String integerToScaleString(final int number,final int scale){
		int length=scale<=characters.length?scale:characters.length;
		StringBuilder builder=new StringBuilder();
		int num=number;
		do{
			int mod=num%length;
			builder.append(characters[mod]);
			num=(num-mod)/(length);
		}while(num>0);
		return builder.reverse().toString();
	}

	/**
	 * scale string to integer
	 * @param string
	 * @param scale
	 * @return int
	 */
	public static int scaleStringToInteger(final String string,final int scale){
		int length=string.length();
		int total=0;
		for(int i=0,j=length-1;i<length;i++,j--){
			char c=string.charAt(i);
			int index=characterMap.get(c);
			total+=index*(int)Math.pow(scale, j);
		}
		return total;
	}

	/**
	 * long to byte array,byte[8]
	 * @param longNum
	 * @return byte[]
	 */
	public static byte[] longToByteArray(long number) {
		byte[] byteArray = new byte[8];
		byteArray[7] = (byte) (number & 0xff);
		byteArray[6] = (byte) (number >> 8 & 0xff);
		byteArray[5] = (byte) (number >> 16 & 0xff);
		byteArray[4] = (byte) (number >> 24 & 0xff);
		byteArray[3] = (byte) (number >> 32 & 0xff);
		byteArray[2] = (byte) (number >> 40 & 0xff);
		byteArray[1] = (byte) (number >> 48 & 0xff);
		byteArray[0] = (byte) (number >> 56 & 0xff);
		return byteArray;
	}

	/**
	 * byte array to long
	 * @param byteArray byte[8]
	 * @return long
	 */
	public static long byteArrayToLong(byte[] byteArray) {
		long result=0;
		if(byteArray!=null&&byteArray.length==8){
			result=((((long) byteArray[0] & 0xff) << 56) | (((long) byteArray[1] & 0xff) << 48) | (((long) byteArray[2] & 0xff) << 40) | (((long) byteArray[3] & 0xff) << 32) | (((long) byteArray[4] & 0xff) << 24) | (((long) byteArray[5] & 0xff) << 16) | (((long) byteArray[6] & 0xff) << 8) | (((long) byteArray[7] & 0xff) << 0));
		}
		return result;
	}

	/**
	 * short to byte array,byte[2]
	 * @param number
	 * @return byte[]
	 */
	public static byte[] shortToByteArray(short number){
		byte[] byteArray = new byte[2];
		byteArray[1] = (byte) (number & 0xff);
		byteArray[0] = (byte) (number >> 8 & 0xff);
		return byteArray;
	}

	/**
	 * byte array to short
	 * @param byteArray byte[2]
	 * @return short
	 */
	public static short byteArrayToShort(byte[] byteArray) {
		short result=0;
		if(byteArray!=null&&byteArray.length==2){
			result=(short)(byteArray[1] & 0xff | (byteArray[0] & 0xff) << 8);
		}
		return result;
	}

	/**
	 * int to byte array,byte[4]
	 * @param number
	 * @return byte[]
	 */
	public static byte[] intToByteArray(int number) {
		byte[] byteArray = new byte[4];
		byteArray[3] = (byte) (number & 0xff);
		byteArray[2] = (byte) (number >> 8 & 0xff);
		byteArray[1] = (byte) (number >> 16 & 0xff);
		byteArray[0] = (byte) (number >> 24 & 0xff);
		return byteArray;
	}

	/**
	 * byte array to int
	 * @param byteArray byte[4]
	 * @return int
	 */
	public static int byteArrayToInt(byte[] byteArray) {
		int result=0;
		if(byteArray!=null&&byteArray.length==4){
			result=byteArray[3] & 0xff | (byteArray[2] & 0xff) << 8 | (byteArray[1] & 0xff) << 16 | (byteArray[0] & 0xff) << 24;
		}
		return result;
	}

	/**
	 * float to byte array
	 * @param number
	 * @return byte[]
	 */
	public static byte[] floatToByteArray(float number) {
		byte[] byteArray = new byte[4];
		Integer intBits = Float.floatToIntBits(number);
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = new Integer(intBits).byteValue();
			intBits = intBits >> 8;
		}
		return byteArray;
	}

	/**
	 * byte array to float
	 * @param byteArray
	 * @return float
	 */
	public static float byteArrayToFloat(byte[] byteArray) {
		float number=0f;
		if(byteArray!=null&&byteArray.length==4){
			int intBits=0;
			intBits = byteArray[0];
			intBits &= 0xff;
			intBits |= ((int) byteArray[1] << 8);
			intBits &= 0xffff;
			intBits |= ((int) byteArray[2] << 16);
			intBits &= 0xffffff;
			intBits |= ((int) byteArray[3] << 24);
			number = Float.intBitsToFloat(intBits);
		}
		return number;
	}

	/**
	 * double to byte array
	 * @param number
	 * @return byte[]
	 */
	public static byte[] doubleToByteArray(double number) {
		byte[] byteArray = new byte[8];
		long longBits = Double.doubleToLongBits(number);
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = new Long(longBits).byteValue();
			longBits = longBits >> 8;
		}
		return byteArray;
	}

	/**
	 * byte array to double
	 * @param byteArray
	 * @return double
	 */
	public static double byteArrayToDouble(byte[] byteArray) {
		double number=0d;
		if(byteArray!=null&&byteArray.length==8){
			long longBits=0l;
			longBits = byteArray[0];
			longBits &= 0xff;
			longBits |= ((long) byteArray[1] << 8);
			longBits &= 0xffff;
			longBits |= ((long) byteArray[2] << 16);
			longBits &= 0xffffff;
			longBits |= ((long) byteArray[3] << 24);
			longBits &= 0xffffffffl;
			longBits |= ((long) byteArray[4] << 32);
			longBits &= 0xffffffffffl;
			longBits |= ((long) byteArray[5] << 40);
			longBits &= 0xffffffffffffl;
			longBits |= ((long) byteArray[6] << 48);
			longBits &= 0xffffffffffffffl;
			longBits |= ((long) byteArray[7] << 56);
			number = Double.longBitsToDouble(longBits);
		}
		return number;
	}

	/**
	 * byte array reverse
	 * @param byteArray
	 * @return byte[]
	 */
	public static byte[] byteArrayReverse(byte[] byteArray) {
		byte[] reverseByteArray = new byte[byteArray.length];
		for (int i=0,j=byteArray.length;i<byteArray.length;i++,j--) {
			reverseByteArray[i]=byteArray[j-1];
		}
		return reverseByteArray;
	}
}
