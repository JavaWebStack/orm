package org.javawebstack.orm.annotation;

import org.javawebstack.orm.util.KeyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    boolean id() default false;
    boolean ai() default true;
    KeyType key() default KeyType.NONE;
    String column() default "";
    int size() default -1;
}
