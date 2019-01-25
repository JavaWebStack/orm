package eu.bebendorf.ajorm.event;

public @interface OnCreate {
    boolean after() default false;
}
