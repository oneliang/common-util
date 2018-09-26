package com.oneliang.util.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.oneliang.Constants;

public final class MixUtil {

	private static final char[] hexCharacters=new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	private static final char[][] mixMap=new char[][]{
		new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'},
		new char[]{'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v'},
		new char[]{'w','x','y','z','~','!','@','#','$','%','^','*','(',')','+','='}
	};
	private static final Map<Character,Character> characterMap=new ConcurrentHashMap<Character, Character>();

	static{
		characterMap.put(mixMap[0][0], hexCharacters[0]);
		characterMap.put(mixMap[0][1], hexCharacters[1]);
		characterMap.put(mixMap[0][2], hexCharacters[2]);
		characterMap.put(mixMap[0][3], hexCharacters[3]);
		characterMap.put(mixMap[0][4], hexCharacters[4]);
		characterMap.put(mixMap[0][5], hexCharacters[5]);
		characterMap.put(mixMap[0][6], hexCharacters[6]);
		characterMap.put(mixMap[0][7], hexCharacters[7]);
		characterMap.put(mixMap[0][8], hexCharacters[8]);
		characterMap.put(mixMap[0][9], hexCharacters[9]);
		characterMap.put(mixMap[0][10], hexCharacters[10]);
		characterMap.put(mixMap[0][11], hexCharacters[11]);
		characterMap.put(mixMap[0][12], hexCharacters[12]);
		characterMap.put(mixMap[0][13], hexCharacters[13]);
		characterMap.put(mixMap[0][14], hexCharacters[14]);
		characterMap.put(mixMap[0][15], hexCharacters[15]);
		
		characterMap.put(mixMap[1][0], hexCharacters[0]);
		characterMap.put(mixMap[1][1], hexCharacters[1]);
		characterMap.put(mixMap[1][2], hexCharacters[2]);
		characterMap.put(mixMap[1][3], hexCharacters[3]);
		characterMap.put(mixMap[1][4], hexCharacters[4]);
		characterMap.put(mixMap[1][5], hexCharacters[5]);
		characterMap.put(mixMap[1][6], hexCharacters[6]);
		characterMap.put(mixMap[1][7], hexCharacters[7]);
		characterMap.put(mixMap[1][8], hexCharacters[8]);
		characterMap.put(mixMap[1][9], hexCharacters[9]);
		characterMap.put(mixMap[1][10], hexCharacters[10]);
		characterMap.put(mixMap[1][11], hexCharacters[11]);
		characterMap.put(mixMap[1][12], hexCharacters[12]);
		characterMap.put(mixMap[1][13], hexCharacters[13]);
		characterMap.put(mixMap[1][14], hexCharacters[14]);
		characterMap.put(mixMap[1][15], hexCharacters[15]);
		
		characterMap.put(mixMap[2][0], hexCharacters[0]);
		characterMap.put(mixMap[2][1], hexCharacters[1]);
		characterMap.put(mixMap[2][2], hexCharacters[2]);
		characterMap.put(mixMap[2][3], hexCharacters[3]);
		characterMap.put(mixMap[2][4], hexCharacters[4]);
		characterMap.put(mixMap[2][5], hexCharacters[5]);
		characterMap.put(mixMap[2][6], hexCharacters[6]);
		characterMap.put(mixMap[2][7], hexCharacters[7]);
		characterMap.put(mixMap[2][8], hexCharacters[8]);
		characterMap.put(mixMap[2][9], hexCharacters[9]);
		characterMap.put(mixMap[2][10], hexCharacters[10]);
		characterMap.put(mixMap[2][11], hexCharacters[11]);
		characterMap.put(mixMap[2][12], hexCharacters[12]);
		characterMap.put(mixMap[2][13], hexCharacters[13]);
		characterMap.put(mixMap[2][14], hexCharacters[14]);
		characterMap.put(mixMap[2][15], hexCharacters[15]);
	}

	/**
	 * mix by encoding utf8
	 * @param string
	 * @param offset
	 * @return String
	 * @throws Exception
	 */
	public static String mix(String string,int offset) throws Exception{
		return mix(string,Constants.Encoding.UTF8,offset);
	}

	/**
	 * mix
	 * @param string
	 * @param encoding
	 * @param offset
	 * @return String
	 * @throws Exception
	 */
	public static String mix(String string,String encoding,int offset) throws Exception{
		StringBuilder stringBuilder=new StringBuilder();
		if(string!=null){
			byte[] byteArray=string.getBytes(encoding);
			byte[] newByteArray=new byte[byteArray.length];
			int i=0;
			for(byte b:byteArray){
				newByteArray[i++]=(byte)(b+offset);
			}
			String hexString=StringUtil.byteArrayToHexString(newByteArray);
			for(i=0;i<hexString.length();i++){
				char c=hexString.charAt(i);
				int index=(int)(Math.random()*3);
				int position=0;
				int ascii=(int)c;
				if(ascii>=48&&ascii<=57){
					position=ascii-48;
				}else if(ascii>=65&&ascii<=90){
					position=ascii-65+10;
				}else if(ascii>=97&&ascii<=122){
					position=ascii-97+10;
				}
				stringBuilder.append(mixMap[index][position]);
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * de mix by encoding utf8
	 * @param string
	 * @param offset
	 * @return String
	 * @throws Exception
	 */
	public static String deMix(String string,int offset) throws Exception{
		return deMix(string,Constants.Encoding.UTF8,offset);
	}

	/**
	 * de mix
	 * @param string
	 * @param encoding
	 * @param offset
	 * @return String
	 * @throws Exception
	 */
	public static String deMix(String string,String encoding,int offset) throws Exception{
		StringBuilder stringBuilder=new StringBuilder();
		for(int i=0;i<string.length();i++){
			char c=string.charAt(i);
			stringBuilder.append(characterMap.get(c));
		}
		byte[] byteArray=StringUtil.hexStringToByteArray(stringBuilder.toString());
		int i=0;
		for(byte b:byteArray){
			byteArray[i++]=(byte)(b-offset);
		}
		return new String(byteArray,encoding);
	}
}
