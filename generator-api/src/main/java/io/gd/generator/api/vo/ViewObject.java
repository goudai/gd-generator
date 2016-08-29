package io.gd.generator.api.vo;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewObject {
	
	String[] groups();
	
	View[] views() default {};
	
}