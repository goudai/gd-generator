package io.gd.generator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 默认值
 * @author srests
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Default {
    enum DefaultType {
        VALUE,//值
        DBKEY //数据库关键字
    }

    String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
    String CURRENT_TIMESTAMP_ONUPDATE = "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP";

    /**
     * 默认值
     *
     * @return
     */
    String value();

    /**
     * 默认值类型
     *
     * @return
     */
    DefaultType type() default DefaultType.VALUE;
}
