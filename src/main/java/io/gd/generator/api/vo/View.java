package io.gd.generator.api.vo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Views.class)
public @interface View {
	
	String name();
	
	Class<?> type();
	
	String[] groups() default {}; // 如果留空 则默认全
	
	String elementGroup() default ""; // 若为collection则是collection元素 若为map则对应value
	
	Class<?> keyType() default Object.class; // 若为map则是key的class
	
}