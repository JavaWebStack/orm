package org.javawebstack.orm.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Indices.class)
public @interface Index {
    boolean unique() default false;
    String id() default "";
    String[] value() default {};
}
