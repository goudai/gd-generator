package io.gd.generator.api.query;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
	
	Predicate[] value();
	
}