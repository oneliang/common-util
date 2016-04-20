package com.oneliang.util.common;

import java.util.List;

public final class Unicode{

	/**
	 * regex
	 */
	public static final String REGEX_ALL="\\\\u[A-Za-z0-9]*";
	public static final String REGEX_CHINESE="\\\\u[A-Za-z0-9]{4}";
	public static final String REGEX_ENGLISH_AND_NUMBER="\\\\u[A-Za-z0-9]{2}";
	public static final String REGEX_SPECIAL="\\\\u[A-Za-z0-9]{1}";
	private static final String FIRST_REGEX="\\\\u";

	/**
	 * to unicode
	 * @param string
	 * @return String
	 */
	public static String toUnicode(String string){
		StringBuilder stringBuilder=new StringBuilder();
		if(string!=null){
			char[] charArray=string.toCharArray();
			for(char c:charArray){
				stringBuilder.append("\\u"+Integer.toHexString(c).toUpperCase());
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * from unicode
	 * @param string
	 * @return String
	 */
	public static String fromUnicode(final String string){
		return fromUnicode(string, REGEX_ALL);
	}

	public static String fromUnicode(final String string,String regex){
		String result=null;
		if(string!=null){
			List<String> groupList=StringUtil.parseStringGroup(string, regex, FIRST_REGEX, StringUtil.BLANK, 0);
			String tempResult=string;
			for(String group:groupList){
				tempResult=tempResult.replaceFirst(regex,String.valueOf((char)Integer.parseInt(group, 16)));
			}
			result=tempResult;
		}
		return result;
	}

	public static void main(String[] args){
		String string="梁asdfasdf文12asfsad翔\r\n您好吗？";
		System.out.println(string);
		String s=Unicode.toUnicode(string);
		System.out.println(s);
		System.out.println(s=Unicode.fromUnicode(s, Unicode.REGEX_CHINESE));
		System.out.println(s=Unicode.fromUnicode(s, Unicode.REGEX_ENGLISH_AND_NUMBER));
		System.out.println(Unicode.fromUnicode(s, Unicode.REGEX_SPECIAL));
	}
}
