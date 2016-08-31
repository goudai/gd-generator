package io.gd.generator.annotation.view;

import java.lang.annotation.*;
import java.util.HashMap;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable()
public @interface MapView {

	String name() default "";

	String[] groups() default {};

	Class<?> type() default HashMap.class;

	Class<?> keyType() default String.class;

	Class<?> valueType() default Object.class;

	String keyGroup() default "";

	String valueGroup() default "";
}

