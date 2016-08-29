package io.gd.generator.api.vo;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Views {
	
	View[] value();
	
}