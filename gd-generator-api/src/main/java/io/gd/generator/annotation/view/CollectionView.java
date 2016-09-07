package io.gd.generator.annotation.view;

import io.gd.generator.annotation.Field;

import java.lang.annotation.*;
import java.util.ArrayList;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CollectionViews.class)
public @interface CollectionView {

	String name();


	String[] groups() default {};

	Class<?> type() default ArrayList.class;

	Class<?> elementType() default Object.class;

	String elementGroup() default "";

	Field field() default @Field(label = "");

}
