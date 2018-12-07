package eu.bebendorf.ajorm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseField {

    String columnName() default "";
    boolean unyque() default false;
    boolean primary() default false;
    boolean id() default false;
    boolean ai() default false;

}
