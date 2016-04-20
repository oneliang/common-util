package com.oneliang.util.validate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Regex {
	String[] value();
	boolean nullable() default true;

	public static final String POSITIVE_INTEGER="^[1-9]\\d*$";
	public static final String NEGATIVE_INTEGER="^-[1-9]\\d*$";
	public static final String INTEGER_NOT_INCLUDE_ZERO="^-?[1-9]\\d*$";
	public static final String POSITIVE_INTEGER_INCLUDE_ZERO="^([1-9]\\d*|0)$";
	public static final String NEGATIVE_INTEGER_INCLUDE_ZERO="^(-[1-9]\\d*|0)$";
	public static final String POSITIVE_DECIMAL="^([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
	public static final String NEGATIVE_DECIMAL="^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
	public static final String DECIMAL_NOT_INCLUDE_ZERO="^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$";
	public static final String POSITIVE_DECIMAL_INCLUDE_ZERO="^([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$";
	public static final String NEGATIVE_DECIMAL_INCLUDE_ZERO="^((-([1-9]\\d*.\\d*)|(0.\\d*[1-9]\\d*))|0?.0+|0)$";
}
