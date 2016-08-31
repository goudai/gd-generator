package io.gd.generator.annotation.query;

import io.gd.generator.api.query.Predicate;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
	
	Predicate[] value();
	
}