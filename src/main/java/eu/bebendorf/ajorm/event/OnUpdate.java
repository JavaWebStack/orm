package eu.bebendorf.ajorm.event;

public @interface OnUpdate {
    boolean after() default true;
}
