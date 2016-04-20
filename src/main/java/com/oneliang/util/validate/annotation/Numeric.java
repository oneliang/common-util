package com.oneliang.util.validate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Numeric {
	long min() default Long.MIN_VALUE;
	long max() default Long.MAX_VALUE;
	boolean nullable() default true;
}
