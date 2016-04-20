package com.oneliang.util.validate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {
	int min() default Integer.MIN_VALUE;
	int max() default Integer.MAX_VALUE;
	boolean nullable() default true; 
}
