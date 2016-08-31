package io.gd.generator.annotation.view;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by freeman on 16/8/31.
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewObject {
	String[] groups();

	View[] views() default {};

	AssociationView[] associationViews() default {};

	CollectionView[] collectionViews() default {};

	MapView[] mapViews() default {};
}
