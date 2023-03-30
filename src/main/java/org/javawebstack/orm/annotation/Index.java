package org.javawebstack.orm.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Indices.class)
public @interface Index {
    Type type() default Type.AUTO;
    boolean unique() default false;
    String id() default "";
    String[] value() default {};

    enum Type {
        AUTO,
        BTREE,
        HASH
    }
}
