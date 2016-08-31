package io.gd.generator.annotation.view;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectionView {

	String name() default "";

	String[] groups() default {};

	Class<?> type() default ArrayList.class;

	Class<?> elementType() default Object.class;

	String elementGroup() default "";
}
