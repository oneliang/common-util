package com.oneliang.util.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.oneliang.Constant;

public final class TagUtil{

	/**
	 * regex
	 */
	private static final String REGEX = "\\{[\\w]*\\}";
	private static final String FIRST_REGEX="\\{";
	
	/**
	 * field split to object array
	 * @param string
	 * @return Object[]
	 */
	public static Object[] fieldSplitToObjectArray(final String string){
		List<String> list=parseStringGroup(string);
		Object[] objects=new Object[list.size()];
		int i=0;
		for(String str:list){
			if(str.equals("null")){
				objects[i]=null;
			}else{
				objects[i]=str;
			}
			i++;
		}
		return objects;
	}
	
	/**
	 * field split with SystemConstant.PAGE_FIELD_SPLIT;
	 * @param string
	 * @return the field array
	 */
	public static String[] fieldSplit(final String string){
		return string.split(Constant.Symbol.LOGIC_AND);
	}
	
	/**
	 * field style split 
	 * @param string
	 * @return the array which length is always two,array[0] is field,array[1] is style
	 */
	public static String[] fieldStyleSplit(final String string){
		String[] strings=new String[2];
		String[] fieldStyle=string.split(Constant.Symbol.MINUS);
		if(fieldStyle.length==2){
			strings[0]=fieldStyle[0];
			strings[1]=" "+fieldStyle[1];
		}else{
			strings[0]=fieldStyle[0];
			strings[1]="";
		}
		return strings;
	}
	
	/**
	 * @param fieldString like:onclick=show('{id}')
	 * @param nullValue where the field value is null will replace the nullValue
	 * @return string
	 */
	public static String fieldReplace(final String fieldString,final Object object,final String nullValue){
		String tempFieldString=fieldString;
		List<String> tdFields = parseStringGroup(tempFieldString);
		for (String tdField : tdFields) {
			try {
				if(tdField.length()>0){
					Object value = ObjectUtil.getterOrIsMethodInvoke(object, tdField);
					if (value != null) {
						tempFieldString = tempFieldString.replaceFirst(REGEX, value.toString());
					} else {
						tempFieldString = tempFieldString.replaceFirst(REGEX, StringUtil.nullToBlank(nullValue));
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return tempFieldString;
	}
	
	/**
	 * <p>
	 * Method: check the string match the regex or not and return the match
	 * field value
	 * </p>
	 * 
	 * @param string
	 * @return List<String>
	 */
	private static List<String> parseStringGroup(final String string) {
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(string);
		String group = null;
		int start = 0;
		while (m.find(start)) {
			start = m.end();
			group = m.group();
			group = group.replaceFirst(FIRST_REGEX, StringUtil.BLANK);
			group = group.substring(0, group.length() - 1);
			list.add(group);
		}
		return list;
	}
}
