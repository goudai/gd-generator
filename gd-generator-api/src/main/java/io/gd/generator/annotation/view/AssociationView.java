package io.gd.generator.annotation.view;

import java.lang.annotation.*;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(AssociationViews.class)
public @interface AssociationView {

	String name() default "";

	String label() default "";

	String[] groups() default {};

	Class<?> type() default Object.class;

	String associationGroup() default "";
}
