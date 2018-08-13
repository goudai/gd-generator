package io.gd.generator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author jianglin
 * @date 2018/8/13
 */
@Documented
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface TypeHandler {

    Class value();
}
