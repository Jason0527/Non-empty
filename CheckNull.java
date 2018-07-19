package com.jason.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
/**
 * 作用于controller方法上，校验入参是否有空值的注解
 * @author jason
 *
 */
@Target({ ElementType.METHOD })		//仅作用于方法，校验方法所有入参，适用于较多参数的非空校验
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckNull {
	//这里只做非空校验。若想做其他类型的校验，可以添加属性值以做区分。但是通常在较多参数的情况下，一般只做非空的统一校验，其它个别字段的校验可以再代码中进行。
    String value() default "CHECK_NULL";
}
