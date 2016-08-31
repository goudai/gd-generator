package io.gd.generator.annotation.view;

import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface AssociationView {

	String name() default "";

	String[] groups() default {};

	Class<?> type() default Object.class;

	String associationGroup() default "";

}
