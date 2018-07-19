package com.jason.boot.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作用于controler中的方法，配合@CheckNull注解一起使用。
 * 它的属性定义了一个字符串数组，方法参数列表做非空校验的时候，忽略掉参数名在该字符串数组中的存在的值
 * 也就是说，参数名为该字符串数组中的值的参数不做非空校验
 * @author jason
 *
 */
@Target({ ElementType.METHOD })		//仅作用于方法
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckNullIgnore {
	//属性值即是要忽略非空校验的参数名
    String[] value();
}
