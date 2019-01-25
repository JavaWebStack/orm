package eu.bebendorf.ajorm.event;

public @interface OnDelete {
    boolean after() default false;
}
