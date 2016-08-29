package io.gd.generator.api;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field {
	
	String name();
	
	String description() default "";
	
}