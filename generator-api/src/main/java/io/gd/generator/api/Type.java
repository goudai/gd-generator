package io.gd.generator.api;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Type {
	
	String name();
	
	String description() default "";
	
}