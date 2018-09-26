package com.oneliang.util.jxl;

import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.write.Label;

import com.oneliang.Constants;
import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.jxl.JxlUtil.JxlProcessor;

public class DefaultJxlProcessor implements JxlProcessor {

	private static final String REGEX = "\\{[\\w\\.]*\\}";
	private static final String FIRST_REGEX="\\{";

	/**
	 * copying process
	 * @param <T>
	 * @param cell
	 * @param object
	 */
	public <T extends Object> void copyingProcess(Cell cell,T object){
		CellType cellType=cell.getType();
		if(cellType.equals(CellType.LABEL)){
			Label label=(Label)cell;
			String value=label.getString();
			if(object!=null){
				List<String> list=StringUtil.parseStringGroup(value,REGEX,FIRST_REGEX,StringUtil.BLANK,1);
				for (String string:list) {
					int pos = 0;
					if ((pos=string.lastIndexOf(Constants.Symbol.DOT)) > 0) {
						String className=string.substring(0, pos);
						String fieldName=string.substring(pos + 1, string.length());
						if(className.equals(object.getClass().getSimpleName())||className.equals(object.getClass().getName())){
							Object fieldValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
							value=value.replaceFirst(REGEX, fieldValue==null?StringUtil.BLANK:fieldValue.toString());
						}
					}
				}
			}
			label.setString(value);
		}
	}
	
	/**
	 * importing process
	 * @param <T>
	 * @param parameterClass
	 * @param cell
	 * @return Object
	 */
	public <T extends Object> Object importingProcess(Class<?> parameterClass, Cell cell){
		String cellValue=cell.getContents();
		Object value=ClassUtil.changeType(parameterClass,new String[]{cellValue});
		return value;
	}

	/**
	 * exporting process
	 * @param <T>
	 * @param clazz
	 * @param value
	 * @param fieldName
	 * @return String
	 */
	public <T extends Object> String exportingProcess(String fieldName, Object value){
		String result=null;
		if(value==null){
			result=StringUtil.BLANK;
		}else{
			Class<?> clazz=value.getClass();
			if(clazz.equals(boolean.class)||clazz.equals(short.class)
					||clazz.equals(int.class)||clazz.equals(long.class)
					||clazz.equals(float.class)||clazz.equals(double.class)
					||clazz.equals(byte.class)||clazz.equals(Boolean.class)
					||clazz.equals(Short.class)||clazz.equals(Integer.class)
					||clazz.equals(Long.class)||clazz.equals(Float.class)
					||clazz.equals(Double.class)||clazz.equals(Byte.class)){
				result=value==null?StringUtil.BLANK:value.toString();
			}else if(clazz.equals(String.class)||clazz.equals(Character.class)){
				result=value==null?StringUtil.BLANK:value.toString();
			}else if(clazz.equals(Date.class)){
				result=value==null?StringUtil.BLANK:TimeUtil.dateToString((Date)value);
			}else{
				if(value==null){
					result=StringUtil.BLANK;
				}else{
					result=value.toString();
				}
			}
		}
		result=result==null?StringUtil.BLANK:result;
		return result;
	}
}
