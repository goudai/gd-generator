package io.gd.generator.annotation.view;

import java.lang.annotation.*;

/**
 * Created by freeman on 16/8/31.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Views {

	View[] value();
}
