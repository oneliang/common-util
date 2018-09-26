package com.oneliang.util.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oneliang.Constants;

/**
 * String util class
 * @author Dandelion
 * @since 2008-08-21
 */
public final class StringUtil{
	
	private static final String METCH_PATTERN_REGEX="[\\*]+";
	private static final String METCH_PATTERN=Constants.Symbol.WILDCARD;
	private static final String METCH_PATTERN_REPLACEMENT="[\\\\S|\\\\s]*";
	public static final String BLANK="";
	public static final String SPACE=" ";
	public static final String NULL="null";
	public static final String CRLF_STRING="\r\n";
	public static final String CR_STRING="\r";
	public static final String LF_STRING="\n";
	public static final byte CR='\r';
	public static final byte LF='\n';
	public static final byte[] CRLF={CR,LF};
	private static final String ZERO="0";

	private StringUtil(){}

	/**
	 * when string is null return blank,where the string is not null it return string.trim
	 * @param string
	 * @return String
	 */
	public static String trim(final String string){
		String result=null;
		if(string==null){
			result=BLANK;
		}else{
			result=string.trim();
		}
		return result;
	}
	
	/**
	 * when string is null return blank string
	 * @param string
	 * @return String
	 */
	public static String nullToBlank(final String string){
		return string==null?BLANK:string;
	}
	
	/**
	 * when string[] is null return blank array
	 * @param stringArray
	 * @return String[]{} length==0
	 */
	public static String[] nullToBlank(final String[] stringArray){
		String[] result=stringArray;
		if(stringArray==null){
			result=new String[]{};
		}
		return result;
	}
	
	/**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param string  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
	public static boolean isBlank(final String string) {
		boolean result = false;
		int strLen;
		if (string == null || (strLen = string.length()) == 0) {
			result = true;
		} else {
			for (int i = 0; i < strLen; i++) {
				if (!Character.isWhitespace(string.charAt(i))) {
					result = false;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * <p>
	 * Checks if a String is not empty (""), not null and not whitespace only.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isNotBlank(null)      = false
	 * StringUtils.isNotBlank(&quot;&quot;)        = false
	 * StringUtils.isNotBlank(&quot; &quot;)       = false
	 * StringUtils.isNotBlank(&quot;bob&quot;)     = true
	 * StringUtils.isNotBlank(&quot;  bob  &quot;) = true
	 * </pre>
	 * 
	 * @param string
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null and
	 *         not whitespace
	 */
    public static boolean isNotBlank(final String string) {
        return !isBlank(string);
    }
	
	/**
	 * compare stringArray1 and stringArray2 return the different in str1
	 * @param stringArray1
	 * @param stringArray2
	 * @return String[]
	 */
	public static String[] compareString(final String[] stringArray1,final String[] stringArray2){
		String[] differentString=null;
		if(stringArray1!=null&&stringArray2!=null){
			List<String> list=new ArrayList<String>();
			for(int i=0;i<stringArray1.length;i++){
				boolean sign=false;
				for(int j=0;j<stringArray2.length;j++){
					if(stringArray1[i].equals(stringArray2[j])){
						sign=true;
						break;
					}
				}
				if(!sign){
					list.add(stringArray1[i]);
				}
			}
			differentString=new String[list.size()];
			differentString=list.toArray(differentString);
		}
		return differentString;
	}
	
	/**
	 * <p>Method:only for '*' match pattern,return true of false</p>
	 * @param string
	 * @param patternString
	 * @return boolean
	 */
	public static boolean isMatchPattern(final String string, final String patternString) {
		boolean result=false;
		if(string!=null&&patternString!=null){
			if(patternString.indexOf(METCH_PATTERN)>=0){
				String matchPattern=Constants.Symbol.XOR+patternString.replaceAll(METCH_PATTERN_REGEX, METCH_PATTERN_REPLACEMENT)+Constants.Symbol.DOLLAR;
				result=isMatchRegex(string, matchPattern);
			}else{
				if(string.equals(patternString)){
					result=true;
				}
			}
		}
		return result;
	}

	/**
	 * <p>Method:only for regex</p>
	 * @param string
	 * @param regex
	 * @return boolean
	 */
	public static boolean isMatchRegex(final String string,final String regex){
		boolean result=false;
		if(string!=null&&regex!=null){
			Pattern pattern=Pattern.compile(regex);
			Matcher matcher=pattern.matcher(string);
			result=matcher.find();
		}
		return result;
	}

	/**
	 * <p>Method:only for regex,parse regex group when regex include group</p>
	 * @param string
	 * @param regex
	 * @return List<String>
	 */
	public static List<String> parseRegexGroup(final String string,final String regex){
		List<String> groupList=null;
		if(string!=null&&regex!=null){
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(string);
			int groupCount=matcher.groupCount();
			int count=1;
			groupList=new ArrayList<String>();
			if(matcher.find()){
				while (count<=groupCount) {
					groupList.add(matcher.group(count));
					count++;
				}
			}
		}
		return groupList;
	}

	/**
	 * <p>
	 * Method: check the string match the regex or not and return all the match
	 * </p>
	 * @param string
	 * @param regex
	 * @return List<String>
	 */
	public static List<String> parseStringGroup(final String string,final String regex) {
		return parseStringGroup(string, regex, BLANK, BLANK, 0);
	}

	/**
	 * <p>
	 * Method: check the string match the regex or not and return the match
	 * field value
	 * like {xxxx} can find xxxx
	 * </p>
	 * @param string
	 * @param regex
	 * @param firstRegex
	 * @param firstRegexReplace
	 * @param lastRegexStringLength like {xxxx},last regex string is "}" so last regex string length equals 1
	 * @return List<String>
	 */
	public static List<String> parseStringGroup(final String string,final String regex,final String firstRegex,final String firstRegexReplace,final int lastRegexStringLength) {
		List<String> list = null;
		if(string!=null){
			list = new ArrayList<String>();
			int lastRegexLength=lastRegexStringLength<0?0:lastRegexStringLength;
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(string);
			String group = null;
			int start = 0;
			while (matcher.find(start)) {
				start = matcher.end();
				group = matcher.group();
				group = group.replaceFirst(firstRegex, firstRegexReplace);
				group = group.substring(0, group.length() - lastRegexLength);
				list.add(group);
			}
		}
		return list;
	}

	/**
	 * byte array to hex string
	 * @param byteArray
	 * @return String
	 */
	public static String byteArrayToHexString(byte[] byteArray){
		StringBuilder builder=new StringBuilder();
		for(int i=0;i<byteArray.length;i++){
			int byteCode=byteArray[i]&0xFF;
			if(byteCode<0x10){
				builder.append(0);
			}
			builder.append(Integer.toHexString(byteCode));
		}
		return builder.toString();
	}

	/**
	 * hex string to byte array
	 * @param string
	 * @return byte
	 */
	public static byte[] hexStringToByteArray(final String string){
		byte[] bytes=null;
		if(string!=null){
			bytes=new byte[string.length()/2];
			int i=0;
			while(i<bytes.length){
				bytes[i]=(byte)(Integer.parseInt(string.substring(i*2, (i+1)*2),16));
				i++;
			}
		}
		return bytes;
	}

	/**
	 * fill zero
	 * @param length
	 * @return String
	 */
	public static String fillZero(int length){
		StringBuilder stringBuilder=new StringBuilder();
		for(int i=0;i<length;i++){
			stringBuilder.append(ZERO);
		}
		return stringBuilder.toString(); 
	}

	/**
	 * <p>Method: string mod operator,return 0~(mod-1)</p>
	 * @param string
	 * @param mod
	 * @return int
	 */
	public static int stringMod(String string,int mod){
		int hashCode=0;
		if(string!=null){
			hashCode=string.hashCode();
			if (hashCode<0) {
				hashCode=Math.abs(hashCode);
				hashCode=hashCode<0?0:hashCode;
			}
		}
		return hashCode%(mod>0?mod:1);
	}
}
