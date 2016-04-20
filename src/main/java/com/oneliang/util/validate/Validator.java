package com.oneliang.util.validate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.oneliang.util.common.ClassUtil;
import com.oneliang.util.common.ObjectUtil;
import com.oneliang.util.common.StringUtil;
import com.oneliang.util.validate.annotation.Decimal;
import com.oneliang.util.validate.annotation.Length;
import com.oneliang.util.validate.annotation.Numeric;
import com.oneliang.util.validate.annotation.Regex;

public final class Validator {

	public static final ValidateProcessor DEFAULT_VALIDATE_PROCESSOR=new DefaultValidateProcessor();

	/**
	 * validate
	 * @param object
	 * @return List<ViolateConstrain>
	 * @throws Exception
	 */
	public static List<ViolateConstrain> validate(Object object) throws Exception {
		return validate(object, DEFAULT_VALIDATE_PROCESSOR);
	}

	/**
	 * validate
	 * @param object
	 * @param validateProcessor
	 * @return List<ViolateConstrain>
	 * @throws Exception
	 */
	public static List<ViolateConstrain> validate(Object object,ValidateProcessor validateProcessor) throws Exception {
		List<ViolateConstrain> violateConstrainList=new ArrayList<ViolateConstrain>();
		if (object!= null) {
			Field[] fields=object.getClass().getDeclaredFields();
			if (fields!=null) {
				for (Field field:fields) {
					if(validateProcessor!=null){
						ViolateConstrain violateConstrain=validateProcessor.validateProcess(object, field);
						if(violateConstrain!=null){
							violateConstrainList.add(violateConstrain);
						}
					}
				}
			}
		}
		return violateConstrainList;
	}

	public static abstract interface ValidateProcessor extends Serializable{

		/**
		 * validate processor
		 * @param object
		 * @param field
		 * @return ViolateConstrain
		 * @throws Exception
		 */
		public abstract ViolateConstrain validateProcess(Object object,Field field) throws Exception;
	}

	public static class DefaultValidateProcessor implements ValidateProcessor {

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 6186403665356911219L;

		/**
		 * validate processor
		 * @param object
		 * @param field
		 * @return ViolateConstrain
		 * @throws Exception
		 */
		public ViolateConstrain validateProcess(Object object,Field field) throws Exception {
			ViolateConstrain violateConstrain=null;
			String fieldName = field.getName();
			if(field.isAnnotationPresent(Numeric.class)){
				Numeric numeric=field.getAnnotation(Numeric.class);
				Object objectValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
				if(objectValue!=null){
					long value=ClassUtil.changeType(long.class, new String[]{objectValue.toString()});
					long min=numeric.min();
					long max=numeric.max();
					if(min>max){
						throw new Exception("min:"+min+" is lagger then max:"+max);
					}else if(value<min){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must >= "+min);
					}else if(value>max){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must <= "+max);
					}
				}else{
					boolean nullable=numeric.nullable();
					if(!nullable){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,can not be null");
					}
				}
			}else if(field.isAnnotationPresent(Decimal.class)){
				Decimal decimal=field.getAnnotation(Decimal.class);
				Object objectValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
				if(objectValue!=null){
					double value=ClassUtil.changeType(double.class, new String[]{objectValue.toString()});
					double min=decimal.min();
					double max=decimal.max();
					if(min>max){
						throw new Exception("min:"+min+" is lagger then max:"+max);
					}else if(value<min){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must >= "+min);
					}else if(value>max){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must <= "+max);
					}
				}else{
					boolean nullable=decimal.nullable();
					if(!nullable){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,can not be null");
					}
				}
			}else if(field.isAnnotationPresent(Length.class)){
				Length length=field.getAnnotation(Length.class);
				Object objectValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
				if(objectValue!=null){
					String value=ClassUtil.changeType(String.class, new String[]{objectValue.toString()});
					int min=length.min();
					int max=length.max();
					if(min>max){
						throw new Exception("min:"+min+" is lagger then max:"+max);
					}else if(value.length()<min){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must >= "+min);
					}else if(value.length()>max){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" must <= "+max);
					}
				}else{
					boolean nullable=length.nullable();
					if(!nullable){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,can not be null");
					}
				}
			}else if(field.isAnnotationPresent(Regex.class)){
				Regex regex=field.getAnnotation(Regex.class);
				Object objectValue=ObjectUtil.getterOrIsMethodInvoke(object, fieldName);
				if(objectValue!=null){
					String value=ClassUtil.changeType(String.class, new String[]{objectValue.toString()});
					String[] regexArray=regex.value();
					for(String regexValue:regexArray){
						if(!StringUtil.isMatchRegex(value, regexValue)){
							violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,"+value+" mush match regex value:"+regexValue);
						}
					}
				}else{
					boolean nullable=regex.nullable();
					if(!nullable){
						violateConstrain=new ViolateConstrain(fieldName,"The field:("+fieldName+") is violate constrain,can not be null");
					}
				}
			}
			return violateConstrain;
		}
	}

	public static final class ViolateConstrain{
		private String fieldName=null;
		private String result=null;
		public ViolateConstrain(String fieldName,String result){
			this.fieldName=fieldName;
			this.result=result;
		}
		/**
		 * @return the fieldName
		 */
		public String getFieldName() {
			return fieldName;
		}
		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}
	}
}
