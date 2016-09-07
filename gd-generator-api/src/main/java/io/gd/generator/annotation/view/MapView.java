package io.gd.generator.annotation.view;

import io.gd.generator.annotation.Field;

import java.lang.annotation.*;
import java.util.HashMap;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MapViews.class)
public @interface MapView {

	String name();


	String[] groups() default {};

	Class<?> type() default HashMap.class;

	Class<?> keyType() default String.class;

	Class<?> valueType() default Object.class;

	String keyGroup() default "";

	String valueGroup() default "";

	Field field() default @Field(label = "");

}

