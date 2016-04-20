package com.oneliang.util.json;

import java.util.Date;

import com.oneliang.Constant;
import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.common.TimeUtil;
import com.oneliang.util.json.JsonUtil.JsonProcessor;

public class DefaultJsonProcessor implements JsonProcessor {

	/**
	 * default json processor
	 */
	public <T extends Object> String process(final String fieldName, final Object value, final boolean ignoreFirstLetterCase) {
		String result=null;
		if(value==null){
			result=StringUtil.NULL;
		}else{
			Class<?> clazz=value.getClass();
			if(clazz.isArray()){
				if(ClassUtil.isBaseArray(clazz)){
					result=JsonUtil.baseArrayToJson(value);
				}else if(ClassUtil.isSimpleArray(clazz)){
					result=JsonUtil.simpleArrayToJson((Object[])value,this);
				}else if(value!=null){
					result=JsonUtil.objectArrayToJson((Object[])value,new String[]{},this,ignoreFirstLetterCase);
				}
			}else if(clazz.equals(boolean.class)||clazz.equals(short.class)
					||clazz.equals(int.class)||clazz.equals(long.class)
					||clazz.equals(float.class)||clazz.equals(double.class)
					||clazz.equals(byte.class)||clazz.equals(Boolean.class)
					||clazz.equals(Short.class)||clazz.equals(Integer.class)
					||clazz.equals(Long.class)||clazz.equals(Float.class)
					||clazz.equals(Double.class)||clazz.equals(Byte.class)){
				result=value==null?StringUtil.NULL:value.toString();
			}else if(clazz.equals(String.class)||clazz.equals(Character.class)){
				result=value==null?StringUtil.NULL:Constant.Symbol.DOUBLE_QUOTES+value.toString()+Constant.Symbol.DOUBLE_QUOTES;
			}else if(clazz.equals(Date.class)){
				result=value==null?StringUtil.NULL:Constant.Symbol.DOUBLE_QUOTES+TimeUtil.dateToString((Date)value)+Constant.Symbol.DOUBLE_QUOTES;
			}else{
				if(value instanceof Iterable){
					result=JsonUtil.iterableToJson((Iterable<?>)value, new String[]{}, this, ignoreFirstLetterCase);
				}else{
					result=JsonUtil.objectToJson(value, new String[]{}, this, ignoreFirstLetterCase);
				}
			}
		}
		result=result==null?StringUtil.NULL:result;
		return result;
	}
}
