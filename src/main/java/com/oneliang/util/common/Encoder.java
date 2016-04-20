package com.oneliang.util.common;


public final class Encoder{

	private Encoder(){}

	/**
	 * escape
	 * @param string
	 * @param excludeCharArray
	 * @return String
	 */
	public static String escape(final String string,final char[] excludeCharArray){
		StringBuilder stringBuilder=new StringBuilder();
		if(string!=null){
			stringBuilder.ensureCapacity(string.length() * 6);
			for (int i = 0; i < string.length(); i++) {
				char character = string.charAt(i);
				boolean excludeSign=false;
				if(excludeCharArray!=null&&excludeCharArray.length>0){
					for(char excludeChar:excludeCharArray){
						if(character==excludeChar){
							stringBuilder.append(character);
							excludeSign=true;
							break;
						}
					}
				}
				if(!excludeSign){
					if (Character.isDigit(character) || Character.isLowerCase(character) || Character.isUpperCase(character)){
						stringBuilder.append(character);
					}else if (character < 0x100) {
						stringBuilder.append("%");
						if (character < 0x10){
							stringBuilder.append("0");
						}
						stringBuilder.append(Integer.toString(character, 16).toUpperCase());
					} else {
						stringBuilder.append("%u");
						stringBuilder.append(Integer.toString(character, 16).toUpperCase());
					}
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * escape string
	 * @param string
	 * @return String
	 */
	public static String escape(final String string) {
		return escape(string, null);
	}

	/**
	 * unescape string
	 * @param string
	 * @return String
	 */
	public static String unescape(final String string) {
		StringBuilder stringBuilder = new StringBuilder();
		if(string!=null){
			stringBuilder.ensureCapacity(string.length());
			int lastPos = 0, pos = 0;
			while (lastPos < string.length()) {
				pos = string.indexOf("%", lastPos);
				if (pos == lastPos) {
					if (string.charAt(pos + 1) == 'u') {
						char character = (char) Integer.parseInt(string.substring(pos + 2, pos + 6), 16);
						stringBuilder.append(character);
						lastPos = pos + 6;
					} else {
						char character = (char) Integer.parseInt(string.substring(pos + 1, pos + 3), 16);
						stringBuilder.append(character);
						lastPos = pos + 3;
					}
				} else {
					if (pos == -1) {
						stringBuilder.append(string.substring(lastPos));
						lastPos = string.length();
					} else {
						stringBuilder.append(string.substring(lastPos, pos));
						lastPos = pos;
					}
				}
			}
		}
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		String tmp = "~!@#$%^&*()_+|\\=-,erg./?><;'][{}\"";
		System.out.println(tmp);
		tmp = escape(tmp,new char[]{'.'});
		System.out.println(tmp);
		System.out.println(tmp);
		System.out.println(unescape(tmp));
		System.out.println(unescape(escape("您好吗？....",new char[]{'.'})));
	}
}
