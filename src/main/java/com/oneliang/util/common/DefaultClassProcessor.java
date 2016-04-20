package com.oneliang.util.common;

import com.oneliang.util.common.ClassUtil.ClassProcessor;
import com.oneliang.util.common.ClassUtil.ClassType;

public class DefaultClassProcessor implements ClassProcessor {

//	private static final String ZERO=String.valueOf(0);
//	private static final String FALSE=String.valueOf(false);
	private static final int DATE_LENGTH=TimeUtil.YEAR_MONTH_DAY.length();
	private static final int DATE_TIME_LENGTH=TimeUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND.length();

	/**
	 * simple class type process
	 * @param clazz
	 * @param values
	 * @param fieldName is null if not exist
	 * @return Object
	 */
	public Object changeClassProcess(Class<?> clazz,String[] values,String fieldName){
		Object value=null;
		ClassType classType=ClassUtil.getClassType(clazz);
		if(classType!=null){
			switch(classType){
			case JAVA_LANG_CHARACTER:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value=Character.valueOf(values[0].toCharArray()[0]);
				}
				break;
			case JAVA_LANG_STRING:
				if(values!=null&&values.length>0&&values[0]!=null){
					value = values[0];
				}
				break;
			case JAVA_LANG_BYTE:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Byte.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_SHORT:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Short.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_INTEGER:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Integer.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_LONG:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Long.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_FLOAT:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Float.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_DOUBLE:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Double.valueOf(values[0]);
				}
				break;
			case JAVA_LANG_BOOLEAN:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Boolean.valueOf(values[0]);
				}
				break;
			case CHAR:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = values[0].toCharArray()[0];
				}else{
					value = Character.MIN_VALUE;
				}
				break;
			case BYTE:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Byte.parseByte(values[0]);
				}else{
					value = Byte.MIN_VALUE;
				}
				break;
			case SHORT:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Short.parseShort(values[0]);
				}else{
					value = Short.MIN_VALUE;
				}
				break;
			case INT:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Integer.parseInt(values[0]);
				}else{
					value = Integer.MIN_VALUE;
				}
				break;
			case LONG:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Long.parseLong(values[0]);
				}else{
					value = Long.MIN_VALUE;
				}
				break;
			case FLOAT:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Float.parseFloat(values[0]);
				}else{
					value = Float.MIN_VALUE;
				}
				break;
			case DOUBLE:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Double.parseDouble(values[0]);
				}else{
					value = Double.MIN_VALUE;
				}
				break;
			case BOOLEAN:
				if(values!=null&&values.length>0&&StringUtil.isNotBlank(values[0])){
					value = Boolean.parseBoolean(values[0]);
				}else{
					value = false;
				}
				break;
			case JAVA_UTIL_DATE:
				if(values==null||values.length==0||StringUtil.isBlank(values[0])){
					value=null;
				}else{
					int valueLength=values[0].length();
					if(valueLength==DATE_LENGTH){
						value=TimeUtil.stringToDate(values[0],TimeUtil.YEAR_MONTH_DAY);
					}else if(valueLength==DATE_TIME_LENGTH){
						value=TimeUtil.stringToDate(values[0]);
					}else{
						value=null;
					}
				}
				break;
			case JAVA_LANG_BYTE_ARRAY:
				if(values!=null){
					Byte[] byteArray = new Byte[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							byteArray[j] = Byte.valueOf(values[j]);
						}else{
							byteArray[j] = null;
						}
					}
					value = byteArray;
				}
				break;
			case JAVA_LANG_CHARACTER_ARRAY:
				if(values!=null){
					Character[] characterArray = new Character[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							characterArray[j] = Character.valueOf(values[j].toCharArray()[0]);
						}else{
							characterArray[j] = null;
						}
					}
					value = characterArray;
				}
				break;
			case JAVA_LANG_STRING_ARRAY:
				value = values;
				break;
			case JAVA_LANG_SHORT_ARRAY:
				if(values!=null){
					Short[] shortArray = new Short[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							shortArray[j] = Short.valueOf(values[j]);
						}else{
							shortArray[j] = null;
						}
					}
					value = shortArray;
				}
				break;
			case JAVA_LANG_INTEGER_ARRAY:
				if(values!=null){
					Integer[] integerArray = new Integer[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							integerArray[j] = Integer.valueOf(values[j]);
						}else{
							integerArray[j] = null;
						}
					}
					value = integerArray;
				}
				break;
			case JAVA_LANG_LONG_ARRAY:
				if(values!=null){
					Long[] longArray = new Long[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							longArray[j] = Long.valueOf(values[j]);
						}else{
							longArray[j] = null;
						}
					}
					value = longArray;
				}
				break;
			case JAVA_LANG_FLOAT_ARRAY:
				if(values!=null){
					Float[] floatArray = new Float[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							floatArray[j] = Float.valueOf(values[j]);
						}else{
							floatArray[j] = null;
						}
					}
					value = floatArray;
				}
				break;
			case JAVA_LANG_DOUBLE_ARRAY:
				if(values!=null){
					Double[] doubleArray = new Double[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							doubleArray[j] = Double.valueOf(values[j]);
						}else{
							doubleArray[j] = null;
						}
					}
					value = doubleArray;
				}
				break;
			case JAVA_LANG_BOOLEAN_ARRAY:
				if(values!=null){
					Boolean[] booleanArray = new Boolean[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							booleanArray[j] = Boolean.valueOf(values[j]);
						}else{
							booleanArray[j] = null;
						}
					}
					value = booleanArray;
				}
				break;
			case BYTE_ARRAY:
				if(values!=null){
					byte[] simpleByteArray = new byte[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleByteArray[j] = Byte.parseByte(values[j]);
						}else{
							simpleByteArray[j] = Byte.MIN_VALUE;
						}
					}
					value = simpleByteArray;
				}
				break;
			case CHAR_ARRAY:
				if(values!=null){
					char[] simpleCharArray = new char[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleCharArray[j] = values[j].toCharArray()[0];
						}else{
							simpleCharArray[j] = Character.MIN_VALUE;
						}
					}
					value = simpleCharArray;
				}
				break;
			case SHORT_ARRAY:
				if(values!=null){
					short[] simpleShortArray = new short[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleShortArray[j] = Short.parseShort(values[j]);
						}else{
							simpleShortArray[j] = Short.MIN_VALUE;
						}
					}
					value = simpleShortArray;
				}
				break;
			case INT_ARRAY:
				if(values!=null){
					int[] simpleIntArray = new int[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleIntArray[j] = Integer.parseInt(values[j]);
						}else{
							simpleIntArray[j] = Integer.MIN_VALUE;
						}
					}
					value = simpleIntArray;
				}
				break;
			case LONG_ARRAY:
				if(values!=null){
					long[] simpleLongArray = new long[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleLongArray[j] = Long.parseLong(values[j]);
						}else{
							simpleLongArray[j] = Long.MIN_VALUE;
						}
					}
					value = simpleLongArray;
				}
				break;
			case FLOAT_ARRAY:
				if(values!=null){
					float[] simpleFloatArray = new float[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleFloatArray[j] = Float.parseFloat(values[j]);
						}else{
							simpleFloatArray[j] = Float.MIN_VALUE;
						}
					}
					value = simpleFloatArray;
				}
				break;
			case DOUBLE_ARRAY:
				if(values!=null){
					double[] simpleDoubleArray = new double[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleDoubleArray[j] = Double.parseDouble(values[j]);
						}else{
							simpleDoubleArray[j] = Double.MIN_VALUE;
						}
					}
					value = simpleDoubleArray;
				}
				break;
			case BOOLEAN_ARRAY:
				if(values!=null){
					boolean[] simpleBooleanArray = new boolean[values.length];
					for (int j = 0; j < values.length; j++) {
						if(StringUtil.isNotBlank(values[j])){
							simpleBooleanArray[j] = Boolean.parseBoolean(values[j]);
						}else{
							simpleBooleanArray[j] = false;
						}
					}
					value = simpleBooleanArray;
				}
				break;
			}
		}else{
			value=values;
		}
		return value;
	}
}